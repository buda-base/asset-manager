"""
Module to write csv to database
"""

from BacklogDb.DbFileReaderWriter import DbFileReaderWriter
from BacklogDb.DbFileReaderParser import DbFileReaderParser


import time
def BacklogFromFile():
    par = DbFileReaderParser("Reads a csv file generated for projects in state", " sourceFile: name of file to load")

    dbwr = DbFileReaderWriter(par.parsedArgs)
    # Parameters in the file must be in this order obsdate, hostname, srcdir, workdir, date, state, imagecount
    # create procedure AddWorkProcessState ( IN hostname varchar (255), IN srcdir varchar (255), IN workName varchar (255), IN workCreateDate date, IN process_state varchar (255), IN imagecount int, IN obsdate date )
    dbwr.write_csv(par.parsedArgs.sourceFile,"AddWorkProcessState")


if __name__ == '__main__':
    BacklogFromFile()


