import collections
import os
from pathlib import Path
from typing import List, Any, Union


class DepthFileSys:
    """
    Creates a file tree of an arbitrary structure.
    Constructor takes optional args, each of which is a list of folder names
    Each argument specifies an object to create in each of the prior arguments
    entries.
    Arguments can be mixed lists and tuples.
    Ex
    temp = DepthFileSys(('topDir',),('folder1','folder2','folder3',),....,('something1','something2',))
    temp = DepthFileSys(['topDir'],['folder1','folder2','folder3'],('something1','something2',))
    The leaf node objects are empty files, with 600 permissions

    returns a file tree containing
        + topDir    \
                    |
                    + folder 1  \
                    .... other folders, under folder 1
                    |           + something1
                    |           + something2
                    + folder 2  \
                    |           + something1
                    |           + something2
                    + folder 3  \
                    |           + something1
                                + something2
    """

    _root: str
    _rootArgKey: str = 'root_path'
    _tree = []

    def __init__(self, *args):
        """
        Creates the folder structure
        :type args: iterated[tuple]
        :param args: multiple tuples:
        tuple 1 is an iterable of top level folders
        tuple 2...n-1 is a iterable of sub folders
        tuple n is a iterable of files, to be created in each of tuple 2-n

        """

        if args is None:
            return  # No baby sitting

        root: tuple = args[0]
        if not self.is_non_string_iterable(root):
            raise TypeError(f"{root} must be an iterable object.")

        # sweet that these operations return empty objects,
        # not None
        folders = args[1:-1]

        items: tuple = args[-1]

        # Go for them. If there is only one argument, create them as files
        self.tree = []
        if len(folders) == 0:
            # an iterable of strings. Create them all as files
            for arg in root:
                self.tree.append(self.create_file(str(arg)))
            return

        # Seed the top of the tree
        self.tree = list(root)

        for level in folders:
            # Add a level to the list of paths in the tree
            add_depth(level, self.tree)

        # Now make the directories
        for _ in self.tree:
            os.makedirs(_, exist_ok=True, mode=0o755)

        # and the files
        for aFolder in self.tree:
            for item in items:
                self.create_file(aFolder / item)

    @property
    def tree(self):
        return self._tree

    @tree.setter
    def tree(self, value):
        self._tree = value

    @staticmethod
    def create_file(file_path: str) -> Path:
        """
        touch a file
        :param file_path:
        :return:
        """
        rp: Path = Path(file_path).expanduser()
        rp.touch(mode=0o600, exist_ok=True)
        return rp

    @staticmethod
    def is_non_string_iterable(o: object) -> bool:
        """
        tests if an object is iterable and is not a string
        :type o: object
        :param o:
        :return:
        """
        if o is str:
            return False
        try:
            # noinspection PyTypeChecker
            _ = (e for e in o)
        except TypeError:
            return False
        return True


def add_depth(arg: tuple, parents: list):
    """
    Map function
    :param arg: tuple of children to add to list. Add all children
    to each node in list
    :param parents: list of parent directories. In/Out. out with one entry for each
    element in arg
    :return:
    """
    # each element in arg is an extra depth to add
    _accum = []
    for _parent in parents:
        for elem in arg:
            _accum.append(Path(_parent) / Path(elem))
    parents.clear()
    [parents.append(f) for f in _accum]


def add_child(parent: list, x: str):
    """
    Replace each element of parent with a path wise concatenation
    of itself and x
        :type parent: collections.Iterable
    :param parent: in/out
    :param x: object to add to each element of parent
    :return:
    """
    t_p: List[Union[Path, Any]] = []
    for p in parent:
        t_p.append(Path(p) / Path(x))
    parent.clear()
    [parent.append(f) for f in t_p]
