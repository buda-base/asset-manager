from pathlib import Path
from unittest import TestCase

# objects under test
from am_test_tests import name_ends_with_numbers, file_numbers_are_sequence


class FileNumberTests(TestCase):

    def test_numbers_only_test(self):
        """
        Test
        """
        files = ['storm42.drain', 'storm.drain42.'
                                  'freel.hoop42.no_number_here.number_here_273818.nothing']
        self.assertTrue(name_ends_with_numbers(files), "Expected list to succeed")

    def test_empty_list(self):
        self.assertFalse(name_ends_with_numbers([]), "Empty list should fail")

    def test_non_conform_names(self):
        files = ['storm42.drain', 'storm.drain42.', 'freel.hoop42.no_number_here.number_here_273818ThisFails.nothing']
        self.assertFalse(name_ends_with_numbers(files), "Expected list to fail")

    def test_empty_names(self):
        files = ['', '']
        self.assertFalse(name_ends_with_numbers(files), "Expected empty list to fail")

    def test_numeric_suffix(self):
        files = ['hi.42', 'trombones.76']
        self.assertFalse(name_ends_with_numbers(files), "Expected name.number list to fail")


class FileSequenceTests(TestCase):
    """
    Test file_numbers_are_sequence
    """
    _pathPrefix = str(Path("root", "interDir", "dir42", "filenameBase"))
    _no_folder = "aFile"

    # ----------------       BEGIN REAL TEST SECTION   ---------------------
    # test drivers follow this section
    def test_empty_list(self):
        """
        Failure expected when list is empty
        :return:
        """
        files = []
        self.assertFalse(file_numbers_are_sequence(files), "Empty list should fail")

    def do_test_duplicate_numbers(self, seed: str):
        """
        Sequence should only contain one of each number
        :return:
        """
        files = [f"{seed}{x}.ext" for x in range(5)]
        files[2] = files[4]
        self.assertFalse(file_numbers_are_sequence(files), f"Duplicate numeric should have failed.")

    def do_test_one_digit(self, seed: str):
        """
        Tests one to three place digits
        :param seed:
        :return:
        """
        files = [f"{seed}{x}.ext" for x in range(5, 9)]
        self.assertTrue(file_numbers_are_sequence(files), f"Sequential like {files[0]} should have worked")

        files = [f"{seed}{x}.ext" for x in range(9, 15)]
        self.assertTrue(file_numbers_are_sequence(files), f"Sequential like {files[0]} should have worked")

        files = [f"{seed}{x}.ext" for x in range(89, 117)]
        self.assertTrue(file_numbers_are_sequence(files), f"Sequential like {files[0]} should have worked")

        files = [f"{seed}{x}.ext" for x in range(9, 134)]
        self.assertTrue(file_numbers_are_sequence(files), f"Sequential like {files[0]} should have worked")

    def do_test_numbers_in_ext(self, seed: str):
        files = [f"{seed}.{x}" for x in range(5)]
        self.assertFalse(file_numbers_are_sequence(files), f"numeric extensions like {files[0]} should have failed")

    def do_test_unordered_list(self, seed: str):
        """
        re-arrange a list which should pass. it should still pass
        :return:
        """
        files = [f"{seed}{x}.ext" for x in range(5)]
        import random
        random.shuffle(files)
        self.assertTrue(file_numbers_are_sequence(files), f"Random file list {files[0]} should have worked")

    def do_test_not_all_numeric(self, seed: str):
        """
        Add a non-numeric to the end of the collection.
        :param seed:
        :return:
        """
        files = [f"{seed}{x}.ext" for x in range(5)]
        files.append(f"{seed}IshouldFail")
        self.assertFalse(file_numbers_are_sequence(files), f"One file at end without a numeric should have failed.")

        files = [f"{seed}{x}.ext" for x in range(5)]
        files[2] = f"{seed}I should Fail"
        self.assertFalse(file_numbers_are_sequence(files), f"One file in middle without a numeric should have failed.")

        files = ["I should fail"]
        self.assertFalse(file_numbers_are_sequence(files), f"One file  without a numeric should have failed.")

        files = [f"Im 42 {seed}"]
        self.assertFalse(file_numbers_are_sequence(files), f"fake numeric {files[0]} should have failed.")

    # --------------    END REAL TEST SECTION     -------------------------

    # --------------    BEGIN TEST DRIVER SECTION -------------------------
    def test_one_digit(self):
        self.do_test_one_digit(self._no_folder)
        self.do_test_one_digit(self._pathPrefix)

    def test_numbers_in_ext(self):
        self.do_test_numbers_in_ext(self._no_folder)
        self.do_test_numbers_in_ext(self._pathPrefix)

    def test_unordered_list(self):
        self.do_test_unordered_list(self._no_folder)
        self.do_test_unordered_list(self._pathPrefix)

    def test_not_all_numeric(self):
        self.do_test_not_all_numeric(self._no_folder)
        self.do_test_not_all_numeric(self._pathPrefix)

    def test_duplicate_numbers(self):
        self.do_test_duplicate_numbers(self._pathPrefix)
        self.do_test_duplicate_numbers(self._no_folder)

    def test_random_numbers(self):
        self.assertWarns("Not implemented")

    def test_zero_fill(self):
        self.assertWarns("Not implemented")  # --------------    END TEST DRIVER SECTION -------------------------
