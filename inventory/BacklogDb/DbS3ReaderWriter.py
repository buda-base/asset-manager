import os
import tempfile

from BacklogDb import DbWriter
import boto3
import botocore
import io


class DbS3ReaderWriter(DbWriter.DbWriter):
    """
    subclass which provides file access to stream
    """

    _tempFile = None

    def accessor(self, s3_spec: str, *arg) -> object:
        """
          Downloads an S3 object into a temporary file, and returns a file iterator
        :param s3_spec: composite file spec: bucket+prefix+key
        :param arg: optional positional arguments to 'open' 'r' must be the first one.
        :return: Readable file stream
        """

        bucket_id, key_id = self.toBucketObject(s3_spec)
        session = boto3.session.Session(region_name='us-east-1')
        client = session.client('s3')
        bucket = session.resource('s3').Bucket(bucket_id)

        try:
            self._tempFile = tempfile.NamedTemporaryFile('w+b',delete=False)
            with open(self._tempFile.name, 'wb') as f:
            # with open(self._tempFile.name, 'wb') as f:
                bucket.download_fileobj(key_id, f)
            # return self._tempFile.file
            return open(self._tempFile.name,'r')
        except botocore.exceptions.ClientError as e:
            if e.response['Error']['Code'] == '404':
                return None
            else:
                raise
        except Exception as ei:
            print(type(ei))
            print(ei)
            raise


    def close_hook(self):
       """
       Clean up temp file
       :return: None
       """
       os.unlink(self._tempFile.name)


    @staticmethod
    def toBucketObject( s3_id: object) -> object:
        """
        Decomposes a string of form a/b/c/d into AWS S3 bucket (everything before first /)
        and everything else
        :return:
        """
        first_sep = s3_id.index('/')
        if first_sep < 0:
            raise IndexError(f"{s3_id} invalid. Must contain and not begin with / ")
        return s3_id[:first_sep],s3_id[first_sep+1:]