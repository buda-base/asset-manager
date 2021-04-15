#!/usr/bin/env python3
"""
find_work.py
Originally toimplemnt analysis of bucketing stratgegies, repuprposed
just to reliably count files and get total size of each work in the archive
Runs in 3:30 on sattva, with Archive CIFS mounted.

"""
import os
from pathlib import Path
import csv
from collections import Counter
import datetime

# last 2 characters of string
l2 =  lambda x:  x[-2:]

def getFileSize(aPath: str) -> int :
    """
    Returns the total file and byte count in a path
    """
    size: int = 0
    _count: int = 0
    from os.path import join, getsize
    for root, dirs, files in os.walk(aPath):
        size += sum(getsize(join(root, name)) for name in files)
        _count += len(files)
    return size, _count

def hist_folder( root: Path  ) -> dict:
    """
    Count the distribution of top level folders in a tree, binned by various algorithms
    """
    buckets = Counter()
    bucket_sizes = Counter()
    bucket_file_count = Counter()
    
    for obj in os.scandir(root):
        if obj.is_dir():
            path_size, path_count = getFileSize(obj.path)
#            bucket_id = l2(obj.name)
            buckets[obj.name] += 1
            bucket_sizes[obj.name] += path_size
            bucket_file_count[obj.name] += path_count
            print(datetime.datetime.now().time(), obj.name,buckets[obj.name],path_count, path_size)
#        else:
#            print('file:' + obj.name)
    return buckets, bucket_sizes, bucket_file_count


def get_folder(root: str, csvfile: object, out_csv: object):
    ict = 0
    for obj in os.scandir(root):
        if obj.is_dir():
            ict = ict + 1
            path_size, path_count = getFileSize(obj.path)
            writer.writerow([obj.name, path_count, path_size])
            if (ict % 10) == 0:
                ict = 0
                csvfile.flush()
                print(datetime.datetime.now().time() , "10")
if __name__ == '__main__':
    print(datetime.datetime.now().time(), "scan start")
    
    print('scan done')
    with open('names.csv', 'w', newline='') as csvfile:
        writer = csv.writer(csvfile, delimiter=',' )
        writer.writerow(['bucket','file_count','size'])
        get_folder( '/mnt/rs3Archive', csvfile,  writer)



