package io.bdrc.am.audit.audittests;

import io.bdrc.am.audit.iaudit.ClassPropertyManager;
import io.bdrc.am.audit.iaudit.Outcome;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class FileSequence extends ImageGroupParents {

    /**
     * Create test with external logger
     *
     * @param logger diagnostic logger, not for results
     */
    public FileSequence(Logger logger) {
        super("FileSequence");
        sysLogger = logger;
        _pm = new ClassPropertyManager("/auditTool.properties", getClass());
        _sequenceLength = getSequenceSubstringLength();

    }


    /**
     * Constructor with builtin logger
     * Useful if you want your slf4j profile to drive logging.
     * note base class must pass its test (such as directory exists)
     * IDC about https://stackoverflow.com/questions/285177/how-do-i-call-one-constructor-from-another-in-java
     * The factory method is not the simplest way to write an external library.
     */
    public FileSequence() {
        this(LoggerFactory.getLogger(FileSequence.class));
    }

    /**
     * Internal class to pass to TestWrapper.
     */
    public class FileSequenceOperation implements ITestOperation {

        public String getName() {
            return getTestName();
        }

        public void run() throws java.io.IOException {
            Path dir = Paths.get(getPath());
            int sequenceLength = getSequenceSubstringLength();


// Creating the filter

            // asset-manager #29 don't count json files in image groups
            DirectoryStream.Filter<Path> filter =
                    entry -> !(entry.toFile().isHidden() || entry.toString().endsWith("json")) ;

            try (DirectoryStream<Path> pathDirectoryStream = Files.newDirectoryStream(dir, filter)) {

                // iterate over directories in path
                for (Path entry : pathDirectoryStream) {
                    sysLogger.debug(String.format("entry %s", entry.toString()));

                    // reiterate no files in image group parent test
                    if (!failFile(dir, entry)) {

                        // We only want to inspect specific directories
                        if (_imageGroupParents.contains(entry.getFileName().toString())) {
                            sequenceImageGroupParent(sequenceLength, filter, entry);
                        }
                    }
                }

            } catch (DirectoryIteratorException die) {
                sysLogger.error("Directory iteration error", die);
                FailTest(Outcome.SYS_EXC, die.getCause().getLocalizedMessage());
            } catch (NumberFormatException nfe) {
                sysLogger.error("Number Format error", nfe);
                FailTest(Outcome.SYS_EXC, nfe.getCause().getLocalizedMessage());

            }
        }


        /**
         * Fails the test if the tested Path is not a directory
         *
         * @param parent container
         * @param entry  tested Path
         * @return false if entry is a directory
         */
        private Boolean failFile(Path parent, Path entry) {
            Boolean isFile = !entry.toFile().isDirectory();
            if (isFile) {
                sysLogger.error("a File {}", entry.getFileName().toString());
                FailTest(LibOutcome.FILES_IN_MAIN_FOLDER, parent.toString(), entry.toString());
            }
            return isFile;
        }

        /**
         * Special handling for a parent of a series of image groups
         *
         * @param sequenceLength   How many characters before the file extension make up the number
         * @param filter           disregard hidden files
         * @param imageGroupParent the directory we're testing
         * @throws IOException on system failures
         */
        private void sequenceImageGroupParent(final int sequenceLength, final DirectoryStream.Filter<Path> filter, final Path imageGroupParent) throws IOException
        {

            // asset-manager #23 don't count directories in image groups
            // asset-manager #29 don't count json files in image groups
            DirectoryStream.Filter<Path> filesInImageGroupFilter =
                    entry -> !(entry.toFile().isHidden()
                            || entry.toFile().isDirectory()
                            || entry.toString().endsWith("json"));

            for (Path anImageGroup : Files.newDirectoryStream(imageGroupParent, filter)) {
                boolean firstFolderFailure = false;
                if (failFile(imageGroupParent, anImageGroup)) {
                    continue;
                }
                sysLogger.debug("ImageGroup {}", anImageGroup.toString());

                TreeMap<Integer, String> filenames = new TreeMap<>();

                for (Path imageGroupFile : Files.newDirectoryStream(anImageGroup, filesInImageGroupFilter)) {

                    String thisFileName = FilenameUtils.getBaseName(imageGroupFile.getFileName().toString());

                    // BUG: Dont parse by sequence length. Requirements call for parsing backeward from last .
                    // to first non int, up to field length.
                    String fileSequence = trailingDigits(thisFileName,sequenceLength);

                    int thisFileIndex = 0;
                    try {
                        thisFileIndex = Integer.parseInt(fileSequence);
                    } catch (NumberFormatException nfe) {
                        String errorText = String.format("File %s does not end in an integer: ends with %s",
                                thisFileName, fileSequence);
                        sysLogger.error(errorText);
                        if (!firstFolderFailure) {
                            firstFolderFailure = true;
                            FailTest(LibOutcome.DIR_FAILS_SEQUENCE, anImageGroup.toString());
                        }
                        FailTest(LibOutcome.FILE_SEQUENCE, errorText);
                    }

                    // fail if duplicate number
                    if (filenames.containsKey(thisFileIndex)) {
                        if (!firstFolderFailure) {
                            firstFolderFailure = true;
                            FailTest(LibOutcome.DIR_FAILS_SEQUENCE, anImageGroup.toString());
                        }
                        FailTest(LibOutcome.DUP_SEQUENCE, filenames.get(thisFileIndex), thisFileName);
                    }
                    filenames.put(thisFileIndex, thisFileName);

                }
                // If filenames contains entries for every number of files, the test passes.
                // The last element must be the same as the size, or there are files missing
                Integer lastKey, size;
                lastKey = filenames.lastKey();
                size = filenames.size();
                if (lastKey > size) {
                    FailTest(LibOutcome.FILE_COUNT, anImageGroup.toString(), lastKey.toString(), size.toString() );
                    GenerateFileMissingMessages(filenames);
                }

            }

            // Because we have a "non-set" state
            if (!IsTestFailed()) {
                PassTest();
            }
        }

        /**
         * Scan a string from the end to the beginning until either 'maxTrailing' digits are found, or a non-digit
         * is found. Returns the String of those digits.
         * @param source source string
         * @param maxTrailing maximum number to look back
         * @return the integer represented by up to the last 'maxTrailing' digits in the string. Stops when a non-digit
         */
        private String trailingDigits(String source, int maxTrailing) {
            int beginScan = source.length() -1;

            while((maxTrailing-- > 0) && Character.isDigit(source.charAt(beginScan))) {
                beginScan--;
            }

            return source.substring(++beginScan);
        }
        private void GenerateFileMissingMessages(final TreeMap<Integer, String> filenames) {
            Integer curEntry = 0;
            for (Map.Entry<Integer, String> entry : filenames.entrySet()) {
                Integer k = entry.getKey();
                while (++curEntry < k) {
                    FailTest(LibOutcome.FILE_SEQUENCE, String.format("File Sequence %4d missing", curEntry));
                }
            }
        }
    }


    @Override
    public void LaunchTest() {

        // have base class tests here?
        // Yes, under the doctrine of One responsibility
        RunBaseTests();
        if (IsTestFailed()) {
            return;
        }
        TestWrapper(new FileSequenceOperation());
    }

    /**
     * Get Sequence length from base class properties
     *
     * @return the number of digits at the end of the file name which
     * represent the sequence
     */
    private int getSequenceSubstringLength() {
        if (_sequenceLength == 0) {
            _sequenceLength = _pm.getPropertyInt(this.getClass().getCanonicalName() + ".SequenceLength");
        }
        return _sequenceLength;
    }



    //endregion
    // region fields
    private int _sequenceLength;
    private final ClassPropertyManager _pm;


    // endregion

}
