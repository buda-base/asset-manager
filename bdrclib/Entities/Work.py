from bdrclib.Entities.EntityBase import EntityBase
from bdrclib.Entities.Volume import Volume
from bdrclib.Entities.Metadata import Metadata


class Work(EntityBase):
    """
    A collection of Volumes, with metadata
    """

    def __init__(self,handle: str):
        self.volumes = []
        self.metadata = None
        self.work_name = handle

    _volumes: [Volume]
    _metadata: Metadata
    _work_name: str

    @property
    def volumes(self):
        return self._volumes

    @volumes.setter
    def volumes(self, value):
        self._volumes = value

    @property
    def metadata(self):
        return self._metadata

    @metadata.setter
    def metadata(self, value):
        self._metadata = value

    @property
    def work_name(self):
        return self._work_name

    @work_name.setter
    def work_name(self, value: str):
        assert isinstance(value, str)
        self._work_name = value

