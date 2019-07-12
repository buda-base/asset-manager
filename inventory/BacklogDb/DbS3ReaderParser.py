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


class DbFS3ReaderWriter(DbWriter):
    """
    subclass which provides file access to stream
    """

    def accessor(self, s3object: str, *arg) -> object:
        """
        required implementation. downloads s3 object into temporary storage.
        :type args: extra arguments as needed
        :param file_name:
        :return:
        """

        return gets3blob(s3object, *arg)

def gets3blob(s3_spec: str):
    """
    Downloads an S3 object into a temporary file, and returns a file iterator
    :param s3_spec: composite file spec: bucket+prefix+key
    :param arg: optional positional arguments to 'open' 'r' must be the first one.
    :return:
    """

    bucket_id, key_id = toBucketObject(s3_spec)
    session = boto3.session.Session(region_name='us-east-1')
    client = session.client('s3')
    bucket = session.resource('s3').Bucket(bucket_id)
    f = io.BytesIO()
    try:
        bucket.download_fileobj(key_id, f)
        f.seek(0)
        return f
    except botocore.exceptions.ClientError as e:
        if e.response['Error']['Code'] == '404':
            return None
        else:
            raise

def toBucketObject(s3_id: str):
    """
    Decomposes a string of form a/b/c/d into AWS S3 bucket (everything before first /)
    and everything else
    :return:
    """
    first_sep = s3_id.index('/')
    if first_sep < 0:
        raise IndexError(f"{s3_id} invalid. Must contain and not begin with / ")
    return s3_id[:first_sep],s3_id[first_sep+1:]
