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

    private final String igPassTemplate = "MICKEY-%s";
    private final String igBaseName = "I1CKEY";
    private final String stringFormatToken = "%s";

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

        BuildPassesFromBases(igPassTemplate, igBaseName,passingTemplate);
    }
   public void BuildPassesTwoSuffix() throws IOException {
        BuildPassesFromBases("M1CKEY-ICKEY-%s", igBaseName,passingTemplate);
    }

    /**
     * Builds a set of files with the given separator
     * @param sepString Separator to include
     * @throws IOException when BuildPasses cant build
     */
   public void BuildUsingSep(String sepString) throws IOException {
       String decimalFormatToken = "%0,4d";

       // These are inserted as literals to build a format string
       final String sb = stringFormatToken +
               sepString +
               decimalFormatToken;
        BuildPassesFromBases( igPassTemplate, igBaseName, sb);
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

        // Make three directories based on the fileBase
        for (int i = 0; i < 3; i++) {

            // igStem is used to generate both the ig folder and the files in it.
            String igStem = String.format("%s%01d", fileBase, i);

            String dirName = String.format(dirBaseFormatTemplate,igStem);

            Path igPath = Files.createDirectory(Paths.get(_imageGroupParentFolder,dirName));

            // Create five name conforming files (Passes)
            for (int fi = 1 ; fi < 5 ; fi++ ) {

                String targetFileStem = String.format(fileNameFormatTemplate,igStem,fi);
                Files.createFile(Paths.get(String.valueOf(igPath), String.format("%s.jpg", targetFileStem)));
            }
        }
    }
}
