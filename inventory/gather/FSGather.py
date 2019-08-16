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
        self._parser.add_argument('-p', '--publish_state_arg', help='specify publish state', required=False)
        self._parser.add_argument("pathToScan", help='Path to scan. Basename becomes the project name')


def a_list(test_val: object, test_col: object, valid_values: object) -> object:
    """
    validates a test value is in a list of dictionary items
    :param test_val: value to seek
    :param test_col: index in dictionary
    :param valid_values: range of values
    :return:
    """
    tv_upper = test_val.upper()
    rc: bool = True
    # noinspection PyTypeChecker
    value_list = [x[test_col] for x in valid_values]
    value_list_upper = [x.upper() for x in value_list]
    if tv_upper not in value_list_upper:
        print(f'{test_val} is invalid. Valid values are {str(valid_values)}')
        rc = False
    return rc


class StateValidator(DbApp):
    """
    Validate arguments against a database
    """
    _publish_state: str = None
    _process_state: str = None

    # noinspection PyTypeChecker
    def __init__(self, db_config: object, process_state: str, publish_state: str) -> object:
        """

        :type db_config: object
        """
        # noinspection PyTypeChecker
        super().__init__(db_config)
        self._process_state = process_state
        self._publish_state = publish_state

    def validate(self):
        test_col = 'state_name'
        proc_ok: bool = a_list(self._process_state, test_col,
                               self.CallAnyExec(f'select {test_col} from process_states'))
        pub_ok: bool = True
        if self._publish_state:
            test_col = 'state_name'
            pub_ok = a_list(self._publish_state, test_col, self.CallAnyExec(f'select {test_col}  from publish_states'))

        # test prints output, just tell caller its ok to to continue
        return proc_ok and pub_ok


class StateWriter(DbApp):

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

        date_format = '%m-%d-%Y'

        obs_date = date.today().strftime(date_format)
        project_path = Path(project_root_folder).resolve()
        host_name = platform.node()

        self.start_connect()
        with os.scandir(project_path) as project_iter:
            for project in project_iter:
                if project.is_dir():
                    project_name = project.name
                    project_stat = project.stat(follow_symlinks=False)
                    project_create_time = date.fromtimestamp(project_stat.st_ctime).strftime(date_format)
                    for work_dir in os.scandir(project):
                        if work_dir.is_dir():
                            work_name = os.path.basename(work_dir.path)
                            image_count = self.count_path(work_dir.path)
                            self.CallAnySproc("AddWorkProcessState", obs_date, host_name, project_name, work_name,
                                              project_create_time, process_state, image_count)

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


if __name__ == "__main__":
    par = DbFileParser("Scans the folder, counting the files in each subfolder, and sending the state to the database",
                       "Usage: FSGather -d section:configFile -s [\"cataloged\"\"scanned\",\"ReadyToProcess\","
                       "\"published\"]")

    # Since you need the db config to work before you can get the valid states from the db
    if not StateValidator(par.parsedArgs.drsDbConfig, par.parsedArgs.processing_state_arg,
                          par.parsedArgs.publish_state_arg).validate():
        sys.exit(-1)

    dbwr = StateWriter(par.parsedArgs.drsDbConfig)

    dbwr.write_project(par.parsedArgs.pathToScan, par.parsedArgs.processing_state_arg)
