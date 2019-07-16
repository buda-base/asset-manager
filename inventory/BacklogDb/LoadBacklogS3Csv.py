"""
Module to write csv to database from S3
"""

from BacklogDb.DbS3ReaderWriter import DbS3ReaderWriter
from BacklogDb.DbS3ReaderParser import DbS3ReaderParser
from BacklogDb.S3WorkFileManager import S3WorkFileManager
import boto3

todo_prefix = "processing/todo/"
processing_prefix = "processing/inprocess/"
done_prefix = "processing/done/"

s3_work_manager: S3WorkFileManager = None
import time
def BacklogFromS3():
    """
    processes the inventory files in a bucket
    :return:  None
    """

    failed = False

    par = DbS3ReaderParser("Processes a folder of csv files which contain backlog inventory",
                           "s3Object: identifier of folder where files to process live (without s3://)")

    session = boto3.session.Session(region_name='us-east-1')
    client = session.client('s3')
    # buildWorkListFromS3(session, client)


    bucket, key = DbS3ReaderWriter.toBucketObject(par.parsedArgs.s3Object)

    # Create processing keys if none
    todo_prefix = f"{key}/"

    # it only makes sense to have processing and done first level children of the bucket,and have
    # the children follow
    # That way, Folder/objectName --> processing/underway/Folder/objectName
    s3_work_manager = S3WorkFileManager(bucket, f"{key}/", f"{processing_prefix}{todo_prefix}", f"{done_prefix}{todo_prefix}")



    page_iterator = client.get_paginator('list_objects_v2').paginate(Bucket=bucket,
        Prefix=key)

    file_list = []
    # Get the object list from the first value
    for page in page_iterator:
        object_list = [x for x in page["Contents"]]

        # we need to replace key/ everywhere. Note this filters out the top level key (key/)
        # itself
        file_list.extend([x['Key'].replace(todo_prefix, '') for x in object_list if x['Key'] != todo_prefix])

    # We've ingested the contents of the to do list, move the files into processing
    work_list = [s3_work_manager.local_name_work_file(x) for x in file_list]

    s3_work_manager.mark_underway(file_list, work_list)

    dbwr = DbS3ReaderWriter(par.parsedArgs)
    for s3Path in work_list:
        dbwr.write_csv(f"{bucket}/{processing_prefix}{todo_prefix}{s3Path}","AddWorkProcessState")

    # dont need to rename work_list. Only when moving from src to done
    s3_work_manager.mark_done(work_list, work_list)


if __name__ == '__main__':
    BacklogFromS3()


