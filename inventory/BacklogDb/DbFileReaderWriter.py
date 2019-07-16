from BacklogDb import DbWriter


class DbFileReaderWriter(DbWriter.DbWriter):
    """
    Subclass of db writer which opens from file on disk
    """

    def accessor(self, file: str, *arg: object):
        """
        OPpens a file stream to the named object
        :param file:
        :param arg:
        :return:
        """
        return open(file, *arg)
