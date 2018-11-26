import tempfile
import unittest
import shutil

from am_unit_test.DepthFileSys import DepthFileSys


class TestFileRepository(unittest.TestCase):
    def test_something(self):
        dfs = DepthFileSys(
            (tempfile.mkdtemp(prefix='root1'), tempfile.mkdtemp( prefix='root2'),),
            ('depth1',), ('depth21', 'depth22',), ('depth31', 'depth32', 'depth33',), ('depth41', 'depth42'))
        print(dfs.tree)
        for ad in dfs.tree:
            shutil.rmtree(ad,ignore_errors=True)


if __name__ == '__main__':
    unittest.main()
