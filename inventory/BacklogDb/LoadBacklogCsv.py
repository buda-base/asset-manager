"""
Module to write csv to database
"""

import csv
import pathlib
import sys
from BacklogDb.DbWriter import DbWriter
import BacklogDb.DbFileReaderParser


import time
def BacklogFromFile():
    par = BacklogDb.DbFileReaderParser("Reads a csv file generated for projects in state", " sourceFilename: required file name")

    dbwr = DbWriter(par.parsedArgs)
    # Parameters in the file must be in this order obsdate, hostname, srcdir, workdir, date, state, imagecount
    # create procedure AddWorkProcessState ( IN hostname varchar (255), IN srcdir varchar (255), IN workName varchar (255), IN workCreateDate date, IN process_state varchar (255), IN imagecount int, IN obsdate date )
    dbwr.write_csv(par.parsedArgs.sourceFile,"AddWorkProcessState")


if __name__ == '__main__':
    BacklogFromFile()


