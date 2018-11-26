from os import scandir
from pathlib import Path

from bdrclib.Entities.Work import Work
from bdrclib.Entities.Metadata import Metadata
from bdrclib.Entities import Volume
from bdrclib.Repository.repository_base import RepositoryBase


class FileRepository(RepositoryBase):
    """
    File based repository, implements
    Root contains Works
    Work contains Volumes
    Volumes contain Content
    Content contains image bytes
    """

    def get_content(self, volume: Volume):
        pass

    def __init__(self, root: Path, image_group: str):
        """
        File Repository
        :param root: path to top of system
        :type root: Path
        :param image_group: directory name which contains validation test objects
        :type image_group: str
        """
        self._path = root
        self.image_group_root = image_group

    def get_work(self, work_handle: object) -> Work:
        """
        Fetch a work object residing at in the tree at 'work_handle'
        :type work_handle: object
        :rtype: Work
        """
        return Work(work_handle)

    def get_metadata(self, work: Work) -> Metadata:
        pass

    def get_volumes(self, work: Work) -> list:
        """
        The file repository implements volumes as a folder
        :param work:
        :return:
        """
        volumes_path = self.root_path / work.work_name / self.image_group_root
        return [Volume(f.name, work) for f in scandir(volumes_path) if f.is_dir()]

    def get_pages(self, volume: Volume):
        """
        Gets the page element names
        :return:
        :param volume:
        :return:
        """
        content_path = self.root_path / volume.work.work_name / self._image_group_root
        return [f.name for f in scandir(content_path) if f.is_file()]

    #    ----- region properties

    _image_group_root: str
    _path: Path

    @property
    def root_path(self) -> Path:
        """
        The file repository has a root_path, which is the
        top of a tree
        :return:
        """
        return self._path

    @root_path.setter
    def root_path(self, value):
        assert isinstance(value, Path)
        self._path = value

    @property
    def image_group_root(self):
        """
        Folder containing Volume directories
        :return:
        """
        return self._image_group_root

    @image_group_root.setter
    def image_group_root(self, value):
        self._image_group_root = value
