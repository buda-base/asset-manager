from DBLib.DbAppParser import DbAppParser, mustExistFile
from . import DbWriter


class DbFileReaderParser(DbAppParser):
    """
    Parser for the file reading parser
    Returns a structure containing fields:
    .drsDbConfig: str (from base class DBAppArgs
    .sourceFile: abosolute path to existing file
    """

    def __init__(self, description: str, usage: str):
        """
        Constructor. Sets up the arguments
        """
        super().__init__(description, usage)
        self._parser.add_argument("sourceFile", help='csv file to send to inventory', type=mustExistFile)


class DbFileReaderWriter(DbWriter):
    """
    subclass which provides file access to stream
    """

    def accessor(self, file_name: str, *arg) -> object:
        """
        required implementation. Opens file on local system
        :type args: extra arguments as needed
        :param file_name:
        :return:
        """
        return open(file_name, *arg)
