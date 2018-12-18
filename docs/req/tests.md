# Test Requirements
this document defines each test in [Requirements](requirements.md#Shared Requirements)
See [Digitization standards Folder Structure](https://buda-base.github.io/digitization-guidelines/#standards/standards-en/)
and [bdrc-audit prototype to do list](https://github.com/ngawangtrinley/bdrc-audit/blob/master/README.md)

The test subject is a set of files in a path. The audit user inputs test parameters. Each test may have its own set of parameters.

For most tests, the required input is the container of a **project**. This structure is named the <a name="Standard_Structure">Standard Project Structure</a> The container is a folder which contains:
- a Bibliographic Info Sheet in Excel format
- a directory named "archive" which contains subfolders (no naming convention)
- a directory named "images"  which contains subfolders (no naming convention)

## Audit number of files per folder
### Test Parameters
Path to a folder containing a [Standard Project Structure](#Standard_Structure)

### Success criteria:
Passes only when:
* every subfolder contains only files whose file name ends with a number.
* The numbers in the file name endings form a series 1..n where n is the number of files in the folder.
* Numbers may contain leading zeroes. It is not required that each file contain the same format. For example,
```
W42_I4242_0001.jpg
W42_I4242_2.jpg
```
is a valid sequence for a two file folder.

##  File Type
### Test parameters

- Path to a folder containing a [Standard Project Structure](#Standard_Structure)
- a designation of the folder containing the archival master (default name: `archive` )
- a designation of the folder containing the web images (default name: `images` )
### Success criteria:
Passes only when:
* the archive files contain only images which match these requirements:

Feature|Standard
--|--
File format	|JPEG 2000 (.j2k), wavelets compression, highest data size
Resolution |	600 ppi/dpi
Color profile	| RGB, 24-bits
Pixel dimensions |	consistent width
~~Quality~~ |	~~1 page per image, no borders, no scan lines, no artifacts~~

The Quality aspect cannot be determined by programmatic inspection, so it is not a requirement in the audit phase.

* the web images folder files have the following properties:

Feature|Standard
--|--
File Format |	JPEG (.jpg) for color pages, TIFF G4 compression (.tif) for text only pages
Resolution|	consistent ppi/dpi
Color Profile	| RGB 24-bits for JPEG, BW 1-bit for TIFF
Pixel dimensions|	consistent width
File size |	smaller than 400 KB

## Canonical paths
### Test Parameters
- Path to a folder containing a [Standard Project Structure](#Standard_Structure)
- Work Name: an unformatted text string
- Volume names: a list of unformatted text strings, representing the volumes in the work.

Note this document does not specify formats for work and volume names.
### Success Criteria
This test passes when:

1. the last element of the project folder is the work name.
2. The project folder contains one folder for each given volume.

## Audit Biblio-sheets
**Needs more detail**
