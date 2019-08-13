import io
from tempfile import NamedTemporaryFile
import botocore
import boto3

from DBLib.DbAppParser import DbAppParser


class DbS3ReaderParser(DbAppParser):
    """
    Parser for the S3 reading parser
    Returns a structure containing fields:
    .drsDbConfig: str (from base class DBAppArgs
    .s3Object: bucket/prefix/key path to existing s3 object
    """

    def __init__(self, description: str, usage: str):
        """
        Constructor. Sets up the arguments
        """
        super().__init__(description, usage)
        self._parser.add_argument("s3Object", help='S3 object to read')


