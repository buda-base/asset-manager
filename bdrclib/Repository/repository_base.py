from abc import ABC, abstractmethod
from bdrclib.Entities import Volume, Work, Metadata

class RepositoryBase(ABC):

    @abstractmethod
    def get_work(self, workHandle: object) -> Work:
        """
        Retrieves a work by name
        :param work_name:
        :return:
        """
        pass

    @abstractmethod
    def get_metadata(self, work: Work) -> Metadata:
        pass

    @abstractmethod
    def get_volumes(self, work: Work) -> list:
        """
        retrieves Volume objects for a work
        :param Work: 
        :return: 
        """
        pass

    @abstractmethod
    def get_content(self, volume: Volume):
        """
        Retrieves image bytes for a volume
        :param volume:
        :return:
        """


