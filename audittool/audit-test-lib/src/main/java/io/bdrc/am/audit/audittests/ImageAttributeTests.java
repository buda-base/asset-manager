package io.bdrc.am.audit.audittests;

import com.google.common.collect.Streams;
import io.bdrc.am.audit.iaudit.Outcome;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.stream.Stream;

import static javax.imageio.metadata.IIOMetadataFormatImpl.standardMetadataFormatName;

public class ImageAttributeTests extends PathTestBase {
    /**
     * new AuditTestBase
     *
     * @param logger internal logger
     */
    public ImageAttributeTests(Logger logger)
    {
        super("ImageAttributeTests");
        sysLogger = logger;
    }

    public class ImageAttributeTestOperation implements AuditTestBase.ITestOperation {

        @Override
        public void run() throws IOException {
            // throw new IOException("Not implemented");
            Path rootFolder = Paths.get(getPath());

            // This test only examines derived image image groups
            Path examineDir = Paths.get(getPath(), testParameters.get("DerivedImageGroupParent"));

// Creating the filter
            DirectoryStream.Filter<Path> filter =
                    entry -> (entry.toFile().isDirectory() && !(entry.toFile().isHidden()));

            try (DirectoryStream<Path> imageGroupDirs = Files.newDirectoryStream(examineDir, filter)) {
                for (Path imagegroup : imageGroupDirs) {
                    TestImages(imagegroup);
                }

            } catch (DirectoryIteratorException die) {
                sysLogger.error("Directory iteration error", die);
                FailTest(Outcome.SYS_EXC, die.getCause().getLocalizedMessage());

            }
        }

        @Override
        public String getName() {
            return getTestName();
        }

        /**
         * Image Testing.
         * Tests images for various quality metrics.
         * References for field values are from
         * https://docs.oracle.com/javase/10/docs/api/javax/imageio/metadata/doc-files/tiff_metadata.html
         *
         * @param imageGroup
         * @throws IOException
         */
        private void TestImages(final Path imageGroup) throws IOException {

            /*
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("JPEG");
            ImageReader aReader;
            while (readers.hasNext()) {
                aReader = readers.next();
                System.out.println("reader: " + aReader);
            }

            readers = ImageIO.getImageReadersByFormatName("TIFF");
            while (readers.hasNext()) {
                aReader = readers.next();
                System.out.println("reader: " + aReader);
            }
            */

            DirectoryStream.Filter<Path> filter =
                    entry -> (entry.toFile().isFile() && !(entry.toFile().isHidden()));

            try (DirectoryStream<Path> imageFiles = Files.newDirectoryStream(imageGroup, filter)) {
                for (Path imageFile : imageFiles) {
                    File fileObject = imageFile.toAbsolutePath().toFile();
                    String fileObjectPathString = imageFile.toAbsolutePath().toString();
                    long imageLength = fileObject.length();

                    String fileExt = FilenameUtils.getExtension(fileObjectPathString);

                    ImageReader reader;

                    ImageAtts imageAtts = new ImageAtts();
                    try {

                        // Thanks marc Agate
                        reader = Streams.stream(ImageIO.getImageReadersBySuffix(fileExt))
                                .findFirst()
                                .orElseThrow
                                        (UnsupportedFormatException::new);

                        ImageInputStream in = ImageIO.createImageInputStream(fileObject);

                        ReaderAtts ra = new ReaderAtts();
                        imageAtts.ReaderAtts.add(ra);
                        try {
                            reader.setInput(in);

                            for (int i = 0; i < reader.getNumImages(true); i++) {
                                InternalImageAtts iias = new InternalImageAtts();
                                ra.InternalImageAtts.add(iias);
                                for (Iterator<ImageTypeSpecifier> imageTypes = reader.getImageTypes(i);
                                     imageTypes.hasNext(); ) {
                                    final ImageTypeSpecifier imageTypeSpecifier = imageTypes.next();

                                    ImageTypeAtts itas = new ImageTypeAtts();
                                    iias.ImageTypeAtts.add(itas);

                                    itas.BitDepth = imageTypeSpecifier.getColorModel().getPixelSize();
                                    itas.ImageTypeNum = imageTypeSpecifier.getBufferedImageType();
                                }

                                try {
                                    iias.iioMetadata = (IIOMetadataNode) reader.getImageMetadata(i).getAsTree
                                            (standardMetadataFormatName);

                                    // dont care if fails
                                    NodeList compressions = iias.iioMetadata.getElementsByTagName("CompressionTypeName");
                                    IIOMetadataNode compressionNode = (IIOMetadataNode) compressions.item(0);
                                    String compression = compressionNode.getAttribute("value");
                                    iias.Compression = ((IIOMetadataNode) (iias.iioMetadata.getElementsByTagName
                                            ("CompressionTypeName")).item(0)).getAttribute("value");
                                } catch (Exception eek) {
                                    iias.iioMetadata = null;
                                    iias.Compression = "EXC_READ";
                                    sysLogger.error(eek.getMessage());
                                }
                            }
                        } finally {
                            reader.dispose();
                        }
                    }
                    catch (UnsupportedFormatException usfx) {
                        FailTest(LibOutcome.NO_IMAGE_READER,fileObjectPathString);
                    }
                    finally {

                        // Phew. We got image data!!!!
                        validate(imageAtts);
                    }



                    // TODO:  1 image / file !!!!
                    /*
                    *  fields: im: Image.open
im.width
im.height
im.info["compression"] "group4"  " G4 is defined in the ITU-T T.6 fax standard for transmitting black and white images."
"CCITT T6" (per Java Imaging IO  is the name of Group 4 encoding
im.mode (values. Caredabout: 1)

                    *
                     */

                    /*
                    int iiwidth = reader.getWidth(0);
                    int iiHeight = reader.getHeight(0);
                    ImageTypeSpecifier imgType = reader.getImageTypes(0).next();
                    int iibitDepth = imgType.getColorModel().getPixelSize();
                    int imgTypeNum = imgType.getBufferedImageType();

                    // Can use image metadata,
                            IIOMetadata imageMeta = reader.getImageMetadata(0);
                    // or image stream metadata
                    //      https://www.silverbaytech.com/2014/05/29/iiometadata-tutorial-part-2-retrieving-image-metadata/
                    //      IIOMetadata imageMeta = reader.getStreamMetadata(0);
                            sysLogger.info(imageMeta.getNativeMetadataFormatName());
                            */
                }
            }

        }

        /**
         * validates image properties. Validation tests
         * If TIFF, must be Group4 compression and "binary"
         * @param imageAtts
         */
        private void validate(final ImageAtts imageAtts) {
        }
    }



    @Override
    public void LaunchTest() {
        TestWrapper(new ImageAttributeTestOperation());
    }

    /**
     * Set all test parameters (not logging or framework)
     * TestProcessedImage expects
     * 1. path - parent container
     * 2. kwargs: These named arguments are required in (String [])(params[1])
     * "DerivedImageGroupParent=folderName
     * "ImageFileSizeLimit=0....n"
     *
     * @param params implementation dependent optional parameters
     */
    @Override
    public void setParams(final Object... params) {
        if ((params == null) || (params.length < 2)) {
            throw new IllegalArgumentException(String.format("Audit test :%s: Required Arguments path, and " +
                            "propertyDictionary not given.",
                    getTestName()));
        }
        super.setParams(params[0]);
        LoadParameters((String[]) (params[1]));
    }
}
