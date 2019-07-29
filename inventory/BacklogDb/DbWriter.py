import csv
import os
import re
import sys
import time
from abc import ABC, abstractmethod

from DBLib.DbApp import DbApp
from DBConfig import DBConfig
import pymysql


class GetArgs:
    """
    Holds command line arguments
    """
    pass


class DbWriter(DbApp, ABC):
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

    def write_csv(self, source_spec: str, sproc: str):
        """
        @summary: emits a list into the configured database
        @param source_spec: object descriptor: local file name or s3 object
        """

        hadBarf = False
        # Load the db configuration from the file given in
        #

        cfg = DBConfig.DBConfig(self.dbName, self.dbConfigFile)
        # cfg = dbConfig.DBConfig('dev', self.oConfig.drsDbConfig)
        dbConnection = self.start_connect(cfg)

        with dbConnection:
            curs = dbConnection.cursor()

            etnow = time.perf_counter()
            calls = 0
            try:
              #  with open(srcFile,'r') as incsv:
                with self.accessor(source_spec)as data_stream:
                    inventory_csv =  csv.reader(data_stream)
                    next(inventory_csv)
                    for aState in inventory_csv:
                        try:
                            curs.callproc(sproc, tuple([self.fixDate(x) for x in aState]))
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
                            print(f"{exc_type} input line :{aState} file:{source_spec}: line {calls}")
                            if dbConnection is not None:
                                dbConnection.rollback()
                            raise
            finally:
                if not hadBarf:
                    dbConnection.commit()
                if curs is not None:
                    curs.close()

                # Clean up as needed
                self.close_hook()

    def fixDate(self, inputItem: str)-> str:
        """
        Convert any dates in the list from yyyy-mm-dd to mm-dd-yyyy
        :return: 
        """
        return re.sub(r'(\d{4})-(\d{1,2})-(\d{1,2})', '\\2-\\3-\\1', inputItem)

    def test(self):
        cfg = DBConfig.DbConfig(self.dbName, self.dbConfigFile)
        self.start_connect(cfg)

    @staticmethod
    def start_connect(cfg: object) -> object:
        """
        @summary: Creates the db connection from the configuration
        """
        return pymysql.connect(read_default_file=cfg.db_cnf, read_default_group=cfg.db_host, charset='utf8')

    @abstractmethod
    def accessor(self, resourceId : str, *arg: object) -> object :
        """
        Abstract method which opens the resource as an iterable
        :param resourceId: identifier. (e.g, file or S3 bucket/prefix/key, no URI
        :return:
        """
        pass

    def close_hook(self):
        """
        If a subclass needs to release resources, implement this.
        :return:
        """
        pass