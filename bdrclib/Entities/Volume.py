"""
Section of a work
"""

from bdrclib.Entities.EntityBase import EntityBase


class Volume(EntityBase):
    _pages: list
    _work: object
    _name: str

    def __init__(self, name: str, work: object):
        self._pages = []
        self._name = name
        self._work = work

    @property
    def pages(self):
        return self._pages

    @pages.setter
    def pages(self, value: list):
        assert isinstance(value, list)
        self._pages = value

    @property
    def work(self) -> object:
        return self._work

    @work.setter
    def work(self, value: object):
        assert isinstance(value, object)
        self._work = value

    @property
    def name(self):
        return self._name
