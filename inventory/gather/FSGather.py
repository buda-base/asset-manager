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
        self._parser.add_argument('-s', '--pubState', help='specify publish state', required=True)
        self._parser.add_argument("pathToScan", help='Path to scan. Basename becomes the project name')


class StateWriter(DbApp):

    def writeProject(self, project_root_folder: Path, process_state: str):
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
        # p_obsdate
        # hostname
        # srcdir
        # workName
        # p_workCreateDate
        # process_state
        # imagecount

        date_format = '%m-%d-%Y'

        obs_date = date.today().strftime(date_format)
        project_path = Path(project_root_folder).resolve()
        host_name = platform.node()
        project_name = (Path(project_path)).name

        self.start_connect()
        with os.scandir(project_path) as project_iter:
            for project in project_iter:
                if (project.is_dir()):
                    project_name = project.name
                    project_stat = project.stat(follow_symlinks=False)
                    project_create_time = date.fromtimestamp(project_stat.st_ctime).strftime(date_format)
                    for work_dir in os.scandir(project):
                        if work_dir.is_dir():
                            work_name = os.path.basename(work_dir.path)
                            image_count = count_path(work_dir.path)
                            self.CallAnySproc("AddWorkProcessState", obs_date, host_name, project_name, work_name,
                                          project_create_time, process_state, image_count)


def count_path(path_str: str) -> ():
    """
    :param path_str: string representing top of search
    :return: total files, total size in bytes of the files under path
    """
    total_size = 0
    num_files = 0

    try:
        for root, dirs, files in os.walk(path_str, topdown=True):
            num_files = len(files)
            # total_size = sum(getsize(join(root,name)) for name in files)

            for work_dir in dirs:
                d_file_count = count_path(join(root, work_dir))
                num_files += d_file_count

            # return here or you iterate through files
            # print(path_str,num_files,sep=",")
            return num_files
    finally:
        # Any number of things could happen, just give what we've got so far
        return num_files


if __name__ == "__main__":
    par = DbFileParser("Scans the folder, counting the files in each subfolder, and sending the state to the database",
                       "Usage: FSGather -d section:configFile -s [\"cataloged\"\"scanned\",\"ReadyToProcess\",\"published\"]")

    dbwr = StateWriter(par.parsedArgs.drsDbConfig)

    dbwr.writeProject(par.parsedArgs.pathToScan, par.parsedArgs.pubState)
