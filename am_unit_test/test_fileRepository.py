import os
import tempfile
from pathlib import Path
from typing import List
from unittest import TestCase
import shutil

from am_unit_test.DepthFileSys import DepthFileSys

# Test subject
from bdrclib.File_Manager.FileRepository import FileRepository


class TestFileRepository(TestCase):
    _fileSysRoots: List[Path] = []
    _fileSystem: DepthFileSys
    _fileSysRoot: str

    _repos: List[FileRepository]

    def test_GetWork(self):
        # Create a path for work
        expected_name: str = 'Work1'
        rep = FileRepository(self._fileSysRoots[0],'images')
        w = rep.get_work(expected_name)
        self.assertEqual(w.work_name,expected_name)

        # Repository doesn't automatically initialize volumes
        self.assertIsNone(w.volumes,"Volumes not null")

        # Repository doesnt initialize metadata
        self.assertIsNone(w.metadata,)



    def test_GetMetadata(self):
        self.fail()

    def test_GetVolumes(self):
        expected_name: str = 'Work1'
        rep = FileRepository(self._fileSysRoots[0], 'images')
        w = rep.get_work(expected_name)
        volumes = rep.get_volumes(w)
        contents = []
        for volume in volumes:
            contents.append(volume.get_contents)

    def test_GetPages(self):
        self.fail()

    def test_root(self):
        self.fail()

    def setUp(self):
        """
        Make the repository
        :return:
        """
   #      self._repos = [FileRepository(Path(p), 'archive') for p in self._fileSysRoots]

    @classmethod
    def setUpClass(cls):
        # add as many roots as you want
        cls._fileSysRoots = (tempfile.mkdtemp(prefix='root1'),)
        cls._fileSystem = DepthFileSys(cls._fileSysRoots, ('Work1', 'Work2'), ('images', 'archive',),
                                       ('IMG_001.h00pstyfreen', 'IMG_002.h00opstyfreen',))

        cls.assertIsInstance(cls,cls._fileSystem,DepthFileSys,"its not a dfs")

    @classmethod
    def tearDownClass(cls):
        for root in cls._fileSysRoots:
            shutil.rmtree(root,ignore_errors=True)
