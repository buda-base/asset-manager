from Entities import EntityBase


class Page(EntityBase):
    """
    The contents of one image in a volume.
    Deliberately avoiding the word Image, as that's a type
    in one implementation
    """

    _bytes: []
    _container_name: str

    # def __init__(self):
    #     self._bytes = []
    #     self._container_name = None

    @property
    def container_name(self):
        return

    @container_name.setter
    def container_name(self, value):
        pass

    @property
    def bytes(self):
        return self._bytes

    @bytes.setter
    def bytes(self, value):
        self._bytes = value
