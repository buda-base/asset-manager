from abc import ABC, abstractmethod
from bdrclib.Entities import volume, work, metadata

class RepositoryBase(ABC):

    @abstractmethod
    def get_work(self, workHandle: object) -> work:
        """
        Retrieves a work by name
        :param work_name:
        :return:
        """
        pass

    @abstractmethod
    def get_metadata(self, work: work) -> metadata:
        pass

    @abstractmethod
    def get_volumes(self, work: work) -> list:
        """
        retrieves Volume objects for a work
        :param Work: 
        :return: 
        """
        pass

    @abstractmethod
    def get_content(self, volume: volume):
        """
        Retrieves image bytes for a volume
        :param volume:
        :return:
        """


