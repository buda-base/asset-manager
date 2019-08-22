#! /usr/local/bin/python3
"""
Gathers statistics about a file system
"""
import os
import sys
import platform
from datetime import date
from pathlib import Path
from os.path import join
from DBLib.DbAppParser import DbAppParser

from DBLib.DbApp import DbApp


class DbFileParser(DbAppParser):
    """
    Parser for the S3 reading parser
    Returns a structure containing fields:
    .drsDbConfig: str (from base class DBAppArgs
    .s3Object: bucket/prefix/key path to existing s3 object
    """

    def __init__(self, description: str, usage: str):
        """
        Constructor. Sets up the arguments
        """
        super().__init__(description, usage)
        self._parser.add_argument('-s', '--processing_state_arg', help='specify publish state', required=True)
        self._parser.add_argument("pathToScan", help='Path to scan. Basename becomes the project name')


def a_list(test_val: object, test_col: object, valid_values: object) -> object:
    """
    validates a test value is in a list of dictionary items
    :param test_val: value to seek
    :param test_col: index in dictionary
    :param valid_values: range of values
    :return: True if the test_val is in the dictionary
    """
    tv_upper = test_val.upper()
    rc: bool = True
    # noinspection PyTypeChecker
    value_list = [x[test_col] for x in valid_values]
    value_list_upper = [x.upper() for x in value_list]
    if tv_upper not in value_list_upper:
        print(f'{test_val} is invalid. Valid values are {str(value_list)}')
        rc = False
    return rc


class StateValidator(DbApp):
    """
    Validate arguments against a database
    """
    _process_state: str = None

    # noinspection PyTypeChecker
    def __init__(self, db_config: object, process_state: str) -> object:
        """

        :type db_config: object
        """
        # noinspection PyTypeChecker
        super().__init__(db_config)
        self._process_state = process_state

    def validate(self):
        test_col = 'state_name'
        return a_list(self._process_state, test_col,
                               self.CallAnyExec(f'select {test_col} from process_states'))

class StateWriter(DbApp):
    """
    Sends up a processing or publishing state to the processing.RDS database
    """

    # Constants. See processing.RDS db.publish_states
    _no_return: str = ""
    _full_pub_text: str = 'fullBDRC'
    _web_pub_text: str = 'webBDRC'
    _db_date_format = '%m-%d-%Y'

    def write_project(self, project_root_folder: Path, process_state: str):
        """
        Calculates sizes of project, and writes into db
        :param project_root_folder: Starting path. Basename becomes the project
        :param process_state: State of images in this project
        :return: None
        """
        # Prepare arguments for sproc:
        # CALL
        # `processing`.
        # `AddWorkProcessState`(
        # p_obs_date
        # hostname
        # srcdir
        # workName
        # p_workCreateDate
        # process_state
        # image_count

        obs_date = date.today().strftime(self._db_date_format)
        project_path: Path = Path(project_root_folder).resolve()
        host_name = platform.node()

        self.start_connect()

        if process_state.upper() == "PUBLISHED":
            self.do_published(process_state, project_path, obs_date, host_name)
        else:
            self.do_processed(process_state, project_path, obs_date, host_name)

    def do_processed(self, process_state: str, project_path: Path, obs_date: str, host_name: str):
        """
        Log in process activity.
        :param process_state: state of tree entry
        :type process_state: str
        :param project_path: parent folder of of projects containing works
        :type project_path: str
        :param obs_date:
        :type obs_date: str
        :param host_name: platform
        :type host_name: str
        :return:
        """
        with os.scandir(project_path) as project_iter:
            for project in project_iter:
                if project.is_dir():
                    project_name = project.name
                    project_stat = project.stat(follow_symlinks=False)
                    project_create_time = date.fromtimestamp(project_stat.st_ctime).strftime(self._db_date_format)
                    for work_dir in os.scandir(project):
                        if not work_dir.is_dir():
                            continue
                        work_name = os.path.basename(work_dir.path)
                        image_count = self.count_path(work_dir.path)
                        self.CallAnySproc("AddWorkProcessState", obs_date, host_name, project_name, work_name,
                                          project_create_time, process_state, "", image_count)

    def do_published(self, process_state: str, work_parent: Path, obs_date: str, host_name: str):
        """
        Log published activity.
        :param process_state: state of tree entry
        :type process_state: str

        :param work_parent: parent folder of of projects containing works
        :type work_parent: Path
        :param obs_date: 
        :type obs_date: str
        :param host_name: platform
        :type host_name: str
        :return:
        """
        project_name = str(work_parent)  # invariant. Could skip

        with os.scandir(work_parent) as work_parents:
            for pub_work in work_parents:
                if not pub_work.is_dir():
                    continue
                work_stat = pub_work.stat(follow_symlinks=False)
                work_create_time = date.fromtimestamp(work_stat.st_ctime).strftime(self._db_date_format)
                work_name = os.path.basename(pub_work.path)
                image_count = self.count_path(pub_work.path)
                publish_state = self.calc_published_state(process_state, Path(pub_work.path), image_count)
                self.CallAnySproc("AddWorkProcessState", obs_date, host_name, project_name, work_name, work_create_time,
                                  process_state, publish_state, image_count)

    def count_path(self, path_str: str) -> ():
        """
        :param path_str: string representing top of search
        :return: total files, total size in bytes of the files under path
        """
        num_files = 0

        try:
            for root, dirs, files in os.walk(path_str, topdown=True):
                num_files = len(files)
                # total_size = sum(getsize(join(root,name)) for name in files)

                for work_dir in dirs:
                    d_file_count = self.count_path(join(root, work_dir))
                    num_files += d_file_count

                # return here or you iterate through files
                # print(path_str,num_files,sep=",")
                return num_files
        finally:
            # Any number of things could happen, just give what we've got so far
            return num_files

    def calc_published_state(self, state: str, path: Path, reference_image_count: int) -> str:
        """
        Determines the publish state for a work.
        :param state: process state.
        :param path: Path to query
        :param reference_image_count: count of ALL images in a work, including sources, archives, images
        :return str 'full,' 'web', or '': if 'state !=  'published', return empty string. Otherwise,
        compare 'archive' file count to overall. If it's less than 1/3, assume it's a _web_pub_text, otherwise
        it's the full pub.
        """

        if not (path.exists() and state == 'published'):
            return ""


        # noinspection PyTypeChecker
        ig_file_count = 0
        for root, dirs, files in os.walk(path):

            # Trivial case
            if not Path(root,'archive').exists():
                return self._web_pub_text

            for image_groups in os.scandir(join(root, 'archive')):
                if not image_groups.is_dir():
                    continue
                for ig_root, ig_dirs, ig_files in os.walk(image_groups):
                        ig_file_count += len(ig_files)

            # approximate
            return self._web_pub_text if float(ig_file_count) < (
                    0.3 * float(reference_image_count)) else self._full_pub_text


if __name__ == "__main__":
    par = DbFileParser("Scans the folder, counting the files in each subfolder, and sending the state to the database",
                       "Usage: FSGather -d section:configFile -s [\"cataloged\"\"scanned\",\"ReadyToProcess\","
                       "\"published\"]")

    # Since you need the db config to work before you can get the valid states from the db
    if not StateValidator(par.parsedArgs.drsDbConfig, par.parsedArgs.processing_state_arg).validate():
        sys.exit(-1)

    dbwr = StateWriter(par.parsedArgs.drsDbConfig)

    dbwr.write_project(par.parsedArgs.pathToScan, par.parsedArgs.processing_state_arg)
