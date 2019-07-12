"""
Module to write csv to database
"""

import csv
import pathlib
import sys

from DBLib.DbApp import DbApp
from DBLib.DbAppParser import DbAppParser, mustExistFile
from DBConfig import DBConfig
import pymysql

import time


class GetArgs:
    """
    Holds command line arguments
    """
    pass

#
class DbWriterParser(DbAppParser):
    """
    Parser for the Get Ready Related class
    Returns a structure containing fields:
    .drsDbConfig: str (from base class DBAppArgs
    .outline: bool
    .printmaster: bool
    .numResults: int
    .results: str (which will have to resolve to a pathlib.Path
    """

    def __init__(self, description: str, usage: str):
        """
        Constructor. Sets up the arguments
        """
        super().__init__(description, usage)
        self._parser.add_argument("sourceFile", help='csv file to send to inventory', type=mustExistFile)

class DbWriter(DbApp):
    """
    Writes to a db, connection string in the dbConfig file
    """

    dbName = ''
    dbConfigFile = ''
    monitor_interval = 50
    sproc = 'AddWorkProcessState'

    @property
    def oConfig(self):
        return self._config

    @oConfig.setter
    def oConfig(self, value):
        self._config = value

    '''
    Constructor
    :param dbConfig: opaque configuration
    '''

    def __init__(self, configInfo):
        self._config = configInfo

        '''Set up dbConfig'''
        try:
            args: list = configInfo.drsDbConfig.split(':')
            self.dbName = args[0]
            self.dbConfigFile = os.path.expanduser(args[1])


        except IndexError:
            raise IndexError('Invalid argument: Must be formatted as section:file ')

    def write_csv(self, srcFile: object, sproc: str):
        """
        @summary: emits a list into the configured database
        @param srcFile: file containing Comma separated list of values
        """

        hadBarf = False
        # Load the db configuration from the file given in
        #

        cfg = DBConfig.DBConfig(self.dbName, self.dbConfigFile)
        # cfg = dbConfig.DBConfig('dev', self.oConfig.drsDbConfig)
        dbConnection = self.start_connect(cfg)

        with dbConnection:
            curs = dbConnection.cursor()

            total = len(srcFile)
            etnow = time.perf_counter()
            calls = 0
            try:
                with open(srcFile,'r') as incsv:
                    inventory_csv =  csv.reader(incsv)
                    outhdr = next(inventory_csv)
                    for aState in inventory_csv:
                        try:
                            curs.callproc(sproc, tuple(aState))
                            calls += 1
                            if calls % self.monitor_interval == 0:
                                y = time.perf_counter()
                                print(" %d calls.  Rate: %5.2f /sec" % (
                                calls, self.monitor_interval / (y - etnow)))
                                etnow = y

                        except UnicodeEncodeError:
                            print(':{0}::{1}:'.format(aState[0].strip(), aState[1].strip()))
                            pass
                        except Exception:
                            hadBarf = True
                            exc_type, exc_obj, exc_tb = sys.exc_info()
                            print(exc_type)
                            if dbConnection is not None:
                                dbConnection.rollback()
                            raise
            finally:
                if not hadBarf:
                    dbConnection.commit()
                if curs is not None:
                    curs.close()

    def test(self):
        cfg = DBConfig.DbConfig(self.dbName, self.dbConfigFile)
        self.start_connect(cfg)

    @staticmethod
    def start_connect(cfg: object) -> object:
        """
        @summary: Creates the db connection from the configuration
        """
        return pymysql.connect(read_default_file=cfg.db_cnf, read_default_group=cfg.db_host, charset='utf8')

if __name__ == '__main__':
    par = DbWriterParser("Reads a csv file generated for projects in state"," sourceFilename: required file name")

    # par.sourceFile is the csv file
    import os
    fPath = pathlib.Path(os.path.expanduser(str(par.parsedArgs.sourceFile))).resolve()

    dbwr = DbWriter(par.parsedArgs)
    # obsdate, hostname, srcdir, workdir, date, state, imagecount
    # create procedure AddWorkProcessState ( IN hostname varchar (255), IN srcdir varchar (255), IN workName varchar (255), IN workCreateDate date, IN process_state varchar (255), IN imagecount int, IN obsdate date )
    dbwr.write_csv(par.parsedArgs.sourceFile,"AddWorkProcessState")

