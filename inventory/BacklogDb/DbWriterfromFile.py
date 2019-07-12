from . import DbWriter

class DbWwriterFromFile(DbWriter):
    """
    Subclass of db writer which reads from csv file
    """

    def __init__