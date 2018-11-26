"""
This module contains file naming tests
"""
from collections import defaultdict
from pathlib import Path
from typing import List
import re

# regexp for a stem ending in one or more unicode digits
_file_number_re = '^.*(?P<sequence>\d+)'

def name_ends_with_numbers(file_name_list: List[str]) -> bool:
    """
    This test tests the list of strings (potentially file names) for files
    which are named xxxxxx[0-9]*.xxx
    where xxx is any set of allowed file system characters.
    A directory passes only when every file name ends with a sequence of numbers.
    A file name is the basename of the file before the last . in the name.
    Ex: hoopsty.frelm.test45.job file name is hoopsty.frelm.test45

    An empty list fails

    """
    global _file_number_re

    last_passed: re.match = None
    for v in file_name_list:
        last_passed = re.fullmatch(_file_number_re, Path(v).stem)
        if not last_passed:
            break
    return last_passed is not None

def file_numbers_are_sequence(file_name_list: List[str]) -> bool:
    """
    Returns true only when the list of file specs passes name_ends_with_numbers (which is not tested here)
    and when the numeric component of each file (the last number before the last .) is a sequence from
    1 .. number of files
    :param file_name_list:
    :return:
    """
    file_sequence_count = defaultdict(int)

    # build a dictionary of sequences
    for f in file_name_list:
        cur_file_match:re.match = re.fullmatch(_file_number_re, Path(f).stem)
        file_name_seq: int = int(cur_file_match.group('sequence'))
        file_sequence_count[file_name_seq] += 1

    # First, test that the dictionary length = the file name length
    sequence_length =  len(file_sequence_count)
    if len(file_name_list) !=  len(file_sequence_count):
        return False

    # next test that the keys are a list without gaps
    sequence_keys = sorted(file_sequence_count.keys())
    lastFile:int = 0
    for idx in range(len(sequence_keys)):
        this_seq = sequence_keys[idx]
        if this_seq != lastFile + 1:
            return False
        lastFile = this_seq

    # last, make sure each sequence appears only once. This
    # prevents errors like
    # W1_Ihowdy42.jpg
    # _W1_Ihowdy42.jpg
    if not all(x == 1 for x in file_sequence_count.values()):
        return False

    # insert other tests here
    return True

