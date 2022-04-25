package io.bdrc.audit.audittests;

import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Builds sets of files to validate ImageNameTest
 */
public class FileNameBuilder {

    private final String _imageGroupParentFolder;

    private final String passingTemplate = "%s%0,4d";

    /**
     * Constructor
     *
     * @param root                   Temporary Folder containing the files
     * @param imageGroupParentFolder - name of parent of image groups
     */
    public FileNameBuilder(TemporaryFolder root, String imageGroupParentFolder) throws IOException {
        _imageGroupParentFolder = root.newFolder(imageGroupParentFolder).getAbsolutePath();
    }

    /**
     * Build a set of folders which validate
     */
    public void BuildPassesOneSuffix() throws IOException {
        BuildPassesFromBases("M1CKEY-%s", "I1CKEY",passingTemplate);
    }
   public void BuildPassesTwoSuffix() throws IOException {
        BuildPassesFromBases("M1CKEY-ICKEY-%s", "I1CKEY",passingTemplate);
    }

    public void BuildFails() throws IOException {
        String failingTemplate = "X%s-fail%0,4d";
        BuildPassesFromBases("DONTCARE-%s", "Really-dont-care", failingTemplate);
    }

    /**
     * Builds folders with a few files each, matching the template
     * @param dirBaseFormatTemplate String.format template for  image group directories (e.g. "ABCDE-%s")
     * @param fileBase template for file names in that
     */
    public void BuildPassesFromBases(String dirBaseFormatTemplate,
                                     String fileBase,
                                     String fileNameFormatTemplate) throws IOException {
        for (int i = 0; i < 3; i++) {
            String targetFileName = String.format(fileNameFormatTemplate,fileBase,i);
            Path igPath = Files.createDirectory(Paths.get(_imageGroupParentFolder,
                    String.format(dirBaseFormatTemplate,targetFileName)));
            for (int fi = 1 ; fi < 5 ; fi++ ) {
                Files.createFile(Paths.get(String.valueOf(igPath),String.format("%s%0,4d.jpg",targetFileName,fi )));
            }
        }
    }
}
