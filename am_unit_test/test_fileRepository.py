import tempfile
import unittest as u
from pathlib import Path
from typing import List
#  from unittest import TestCase
import shutil

from am_unit_test.DepthFileSys import DepthFileSys

# Test subject
from bdrclib.File_Manager.FileRepository import FileRepository


class TestFileRepository(u.TestCase):
    _fileSysRoots: List[Path] = []
    _fileSystem: DepthFileSys
    _fileSysRoot: str
    _expected_works = ('Work1', 'Work2',)
    _expected_volumes = ('Work1_Volume2', 'Work1_volume1', 'heemsterizer',)
    _expected_pages = ('IMG_001.h00pstyfreen', 'IMG_002.h00opstyfreen',)

    _repos: List[FileRepository]

    def test_GetWork(self):
        # Create a path for work
        for expected_name in self._expected_works:
            rep = FileRepository(self._fileSysRoots[0], 'images')
            w = rep.get_work(expected_name)
            self.assertEqual(w.work_name, expected_name)

    def test_work_no_volumes(self):
        for expected_name in self._expected_works:
            rep = FileRepository(self._fileSysRoots[0], 'images')
            w = rep.get_work(expected_name)
            # Repository doesn't automatically initialize volumes
            self.assertEqual(0, len(w.volumes), "Volumes not empty")

    def test_work_no_metadata(self):
        expected_name: str = 'Work1'
        rep = FileRepository(self._fileSysRoots[0], 'images')
        w = rep.get_work(expected_name)
        # Repository doesnt initialize metadata
        self.assertIsNone(w.metadata, )

    def test_GetVolumes(self):
        for expected_name in self._expected_works:
            rep = FileRepository(self._fileSysRoots[0], 'images')
            w = rep.get_work(expected_name)
            volumes = rep.get_volumes(w)
            vol_names = sorted([v.name for v in volumes])
            self.assertListEqual(sorted(self._expected_volumes), vol_names, "Returned volumes not same")

    def test_volumes_pages_empty(self):
        for expected_name in self._expected_works:
            rep = FileRepository(self._fileSysRoots[0], 'images')
            w = rep.get_work(expected_name)
            for vol in rep.get_volumes(w):
                self.assertListEqual(vol.pages, [], f"{vol.name} should have empty page list")

    def test_pages(self):
        for expected_name in self._expected_works:
            rep = FileRepository(self._fileSysRoots[0], 'images')
            w = rep.get_work(expected_name)
            for vol in rep.get_volumes(w):
                vol.pages = rep.get_pages(vol)
                self.assertListEqual(vol.pages, sorted(self._expected_pages))

    @u.expectedFailure
    def test_GetMetadata(self):
        self.fail()

    @classmethod
    def setUpClass(cls):
        # DepthFileSys supports multiple roots
        cls._fileSysRoots = (tempfile.mkdtemp(prefix='root'),)
        cls._fileSystem = DepthFileSys(cls._fileSysRoots, cls._expected_works, ('images', 'archive',),
                                       cls._expected_volumes, cls._expected_pages)

        # noinspection PyCallByClass
        cls.assertIsInstance(cls, cls._fileSystem, DepthFileSys, "its not a dfs")

    @classmethod
    def tearDownClass(cls):
        for root in cls._fileSysRoots:
            # noinspection PyTypeChecker
            shutil.rmtree(root, ignore_errors=True)
