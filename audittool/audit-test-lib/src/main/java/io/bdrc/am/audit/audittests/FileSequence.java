package io.bdrc.am.audit.audittests;

import io.bdrc.am.audit.iaudit.Outcome;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.*;
import java.util.Map;
import java.util.TreeMap;

public class FileSequence extends PathTestBase {

    public FileSequence(String pathName) {
        super("FileSequence");
        SetParams(pathName);
        _pm = new PropertyManager("/auditTool.properties");
        _sequenceLength = getSequenceLength();

    }

    /**
     * Internal class to pass to TestWrapper.
     * note base class must pass its test (such as directory exists)
     */
    public class FileSequenceOperation implements ITestOperation {

        public String getName() {
            return getTestName();
        }

        public void run() throws java.io.IOException {

            Path dir = Paths.get(getPath());
            int sequenceLength = getSequenceLength();


// Creating the filter
            DirectoryStream.Filter<Path> filter = entry -> !(entry.toFile().isHidden());

            try (DirectoryStream<Path> pathDirectoryStream = Files.newDirectoryStream(dir, filter)) {

                // iterate over directories in path
                for (Path entry : pathDirectoryStream)
                {
                    sysLogger.debug(String.format("entry %s", entry.toString()));

                    TreeMap<Integer, String> filenames = new TreeMap<>();

                    // reiterate NoDirInImages test
                    if (!entry.toFile().isDirectory()) {
                        sysLogger.error("a File {}", entry.getFileName().toString());
                        FailTest(Outcome.FILES_IN_MAIN_FOLDER, dir.toString(), entry.toString());
                        // keep going
                        // return;
                    }

                    // add the files in the directory to a buffer. Along the way
                    // make sure everything in this folder is a file
                    for (Path aFile : Files.newDirectoryStream(entry,filter)) {
                        sysLogger.debug("Not a File {}", aFile.toString());
                        if (!aFile.toFile().isFile()) {
                            sysLogger.error(String.format("Not a File %s", aFile.toString()));
                            FailTest(Outcome.DIR_IN_IMAGES_FOLDER, aFile.toString(), entry.toString());
                        }

                        String thisFileName = FilenameUtils.getBaseName(aFile.getFileName().toString());
                        String fileSequence = thisFileName.substring(thisFileName.length()- sequenceLength);

                        int thisFileIndex = 0;
                        try {
                            thisFileIndex = Integer.parseInt(fileSequence);
                        }
                        catch(NumberFormatException nfe) {
                            String errorText = String.format("File %s does not end in an integer",thisFileName);
                            sysLogger.error(errorText);
                            FailTest(Outcome.FILE_SEQUENCE,errorText );
                        }

                        // fail if duplicate number
                        if (filenames.containsKey(thisFileIndex)) {
                            FailTest(Outcome.DUP_SEQUENCE, filenames.get(thisFileIndex),thisFileName);
                            // return;
                        }
                        filenames.put(thisFileIndex, thisFileName);

                    }

                    // If filenames contains entries for every number of files, the test passes.
                    // The last element must be the same as the size, or there are files missing
                    int lastKey, size ;
                    lastKey = filenames.lastKey();
                    size =  filenames.size();
                    if ( lastKey > size) {
                        FailTest(Outcome.FILE_SEQUENCE, String.format("Last file index is %d, but there are %d files " +
                                "in the folder.",lastKey,size));
                        GenerateFileMissingMessages(filenames);
                    }

                    // Because we have a "non-set" state
                    if (!IsTestFailed()) {
                        PassTest();
                    }


                    // Since we're using a sparse TreeMap, this test will never fail
                    // find any files which don't have a number, skipping the plug
//                    StringBuilder absentSequences = new StringBuilder();
//                    for (int i = 1 ; i < filenames.size() ; i++)
//                    {
//                        if (isEmpty(filenames[i])) {
//                            absentSequences.append(String.format("%4d, ",i));
//                        }
//                    }
//                    if (!isEmpty(absentSequences.toString())) {
//                        FailTest(Outcome.FILE_SEQUENCE,String.format("Missing sequences %s",
//                                absentSequences.toString())) ;
//                        return;
//                    }

                }

            } catch (DirectoryIteratorException die) {
                sysLogger.error("Directory iteration error", die);
                FailTest(Outcome.SYS_EXC, die.getCause().getLocalizedMessage());
            }
            catch(NumberFormatException nfe) {
                sysLogger.error("Number Format error", nfe);
                FailTest(Outcome.SYS_EXC, nfe.getCause().getLocalizedMessage());

            }
        }

        private void GenerateFileMissingMessages(final TreeMap<Integer, String> filenames) {
            int curEntry = 1;
            for (Map.Entry<Integer, String> entry : filenames.entrySet()) {
                Integer k = entry.getKey();
                while (curEntry++ < k) {
                    FailTest(Outcome.FILE_SEQUENCE, String.format("File Sequence %4d missing", curEntry));
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
     * @return the number of digits at the end of the file name which
     *          represent the sequence
     */
    private int getSequenceLength() {
        if (_sequenceLength == 0) {
            _sequenceLength = _pm.getPropertyInt(this.getClass().getCanonicalName() + ".SequenceLength");
        }
        return _sequenceLength;
    }

    // region fields
    private int _sequenceLength;
    private PropertyManager _pm ;
    // endregion

}
