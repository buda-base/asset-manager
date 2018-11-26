"""
Section of a work
"""

from Entities import EntityBase


class Volume(EntityBase):
    _pages: list

    # Suprpisingly hard to get a class back-reference:
    # a Work is defined to contain Volume objects, and a Volume
    # points back to a work, yet...
    _work: EntityBase
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
    def work(self) -> EntityBase:
        return self._work

    @work.setter
    def work(self, value: EntityBase):
        assert isinstance(value, EntityBase)
        self._work = value

    @property
    def name(self):
        return self._name
