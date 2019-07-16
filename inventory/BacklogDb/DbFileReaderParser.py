from DBLib.DbAppParser import DbAppParser, mustExistFile


class DbFileReaderParser(DbAppParser):
    """
    Parser for the file reading parser
    Returns a structure containing fields:
    .drsDbConfig: str (from base class DBAppArgs
    .sourceFile: absolute path to existing file
    """

    def __init__(self, description: str, usage: str):
        """
        Constructor. Sets up the arguments
        """
        super().__init__(description, usage)
        self._parser.add_argument("sourceFile", help='csv file to send to inventory', type=mustExistFile)

