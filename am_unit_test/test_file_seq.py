from unittest import TestCase

# objects under test
from am_test_tests import name_ends_with_numbers


class FileTests(TestCase):

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
        files = ['storm42.drain', 'storm.drain42.',
                                  'freel.hoop42.no_number_here.number_here_273818ThisFails.nothing']
        self.assertFalse(name_ends_with_numbers(files), "Expected list to fail")

    def test_empty_names(self):
        files = ['', '' ]
        self.assertFalse(name_ends_with_numbers(files), "Expected list to fail")

    def test_numeric_suffix(self):
        files = ['hi.42', 'trombones.76' ]
        self.assertFalse(name_ends_with_numbers(files), "Expected list to fail")
