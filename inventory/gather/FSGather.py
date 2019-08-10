#! /usr/bin/env python3
"""
Gathers statistics about a file system
"""
import os
import sys
import platform
from datetime import date
from pathlib import Path
from os.path import join, getsize, isdir
from DBLib.DbAppParser import DbAppParser

from DBLib.DbApp import DbApp
from DBConfig import DBConfig
import pymysql


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

    def writeProject(self,project_root_folder: Path, process_state : str):
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
        hostname = platform.node()
        srcdir = (Path(project_path)).name


        self.start_connect()
        with os.scandir(project_path) as pdit:
            for de in pdit:
                if (de.is_dir()):
                    work_name = de.name
                    dStat = de.stat(follow_symlinks=False)
                    ctime = date.fromtimestamp(dStat.st_ctime).strftime(date_format)
                    imagecount = count_path(de.path)
                    self.CallAnySproc("AddWorkProcessState",
                                      obs_date,
                                      hostname,
                                      srcdir,
                                      work_name,
                                      ctime,
                                      process_state,
                                      imagecount)



def count_path(path_str: str) -> () :
    """
    :param path_str: string representing top of search
    :return: total files, total size in bytes of the files under path
    """
    total_size = 0


    try:
        for root,dirs,files in os.walk(path_str,topdown=True):
            num_files = len(files)
            # total_size = sum(getsize(join(root,name)) for name in files)

            for work_dir in dirs:
                d_file_count = count_path(join(root, work_dir))
                num_files += d_file_count


            # return here or you iterate through files
            # print(path_str,num_files,sep=",")
            return num_files
    except FileNotFoundError:
        pass




if __name__ == "__main__":

    par = DbFileParser("Scans the folder, counting the files in each subfolder, and sending the state to the database",
                       "Usage: FSGather -d section:configFile -s [\"cataloged\"\"scanned\",\"ReadyToProcess\",\"published\"]")


    dbwr = StateWriter(par.parsedArgs.drsDbConfig)

    dbwr.writeProject(par.parsedArgs.pathToScan,par.parsedArgs.pubState)


