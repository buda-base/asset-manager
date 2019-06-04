package io.bdrc.am.audit.audittests;

import com.google.common.collect.Streams;
import com.sun.javafx.iio.ImageStorage;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.Buffer;
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
         * Collect image properties for validation:
         * from
         * PIL Image class.
         * See github.com/buda-base/volume-manifest-tool, It tests PIL Image fields:
         * See https://pillow.readthedocs.io/en/3.1.x/handbook/concepts.html?highlight=modes
         * .format (values "TIFF" "JPEG")
         * .mode (value "1") means (1-bit pixels, black and white, stored with one pixel per byte)
         * .info["compression"]
         * <p>
         * The Java analogues of this are:
         * Pil Image.format = ImageIo.Reader type reader instanceof TIFFImageReader orJPEGImageReader
         * (see ReaderAtts ctor)
         * <p>
         * .info["compression"] = IIOMetadata node "CompressionTypeName"
         * .mode = ImageTypeSpecifier.getBuffereImageType (see BufferImage.java for constants)
         *
         * @param imageGroup folder containing images
         * @throws IOException If io error
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

                    try {

                        // Thanks marc Agate
                        reader = Streams.stream(ImageIO.getImageReadersBySuffix(fileExt))
                                .findFirst()
                                .orElseThrow
                                        (UnsupportedFormatException::new);

                        ImageInputStream in = ImageIO.createImageInputStream(fileObject);

                        ReaderAtts ra = new ReaderAtts(reader);

                        // we dont care about jpgs
                        if (ra.ImageFileFormat.equals(ReaderAtts.FILE_JPG)) {
                            continue;
                        }
                        try {
                            reader.setInput(in);

                            for (int i = 0; i < reader.getNumImages(true); i++) {
                                InternalImageAtts iias = new InternalImageAtts();
                                ra.InternalImageAtts = iias;
                                ImageTypeSpecifier its = Streams.stream(reader.getImageTypes(0))
                                        .findFirst().orElseThrow(UnsupportedFormatException::new);

                                ImageTypeAtts itas = new ImageTypeAtts();
                                iias.ImageTypeAtts = itas;

                                itas.BitDepth = its.getColorModel().getPixelSize();

                                // See java.awt.image.BufferedImage
                                itas.ImageTypeNum = its.getBufferedImageType();


                                try {
                                    iias.iioMetadata = (IIOMetadataNode) reader.getImageMetadata(i).getAsTree
                                            (standardMetadataFormatName);

                                    // dont care if fails
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

                        // Phew. We got image data!!!!
                        String validationErrors = validate(ra, fileObjectPathString);
                        if (validationErrors.length() > 0) {
                            FailTest(LibOutcome.INVALID_TIFF, fileObjectPathString, validationErrors);
                        }
                    } catch (UnsupportedFormatException usfx) {
                        FailTest(LibOutcome.NO_IMAGE_READER, fileObjectPathString);
                    } catch (Exception eek) {
                        FailTest(Outcome.SYS_EXC, "ImageAttributeTest", " in " + fileObjectPathString + ":" + eek
                                .getMessage
                                        ());
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
         * Validate normalized image statistics
         * if format is "TIFF"
         * - Compression must be "CCITT 6" ( group 4)
         * - ImageTypeNum must be BufferedImage.TYPE_BYTE_BINARY
         * <p>
         * If TIFF, image must be Group4 compression and "binary"
         *
         * @param readerAtts collected image statistics from reader
         */
        private String validate(final ReaderAtts readerAtts, String filePath) {
            boolean failed = false;
            StringBuilder failedReasons = new StringBuilder();

            if (readerAtts.ImageFileFormat.equals(ReaderAtts.FILE_TIFF)) {

                // Test mode: 1 bit/pixel, image type num one of the
                // BufferedImage.ImageType enums
                ImageTypeAtts itas = readerAtts.InternalImageAtts.ImageTypeAtts;
                if (!(itas.BitDepth == 1
                        && (itas.ImageTypeNum == BufferedImage.TYPE_BYTE_GRAY ||
                        itas.ImageTypeNum == BufferedImage.TYPE_BYTE_BINARY ||
                        itas.ImageTypeNum == BufferedImage.TYPE_USHORT_GRAY))
                        ) {
                    failed = true;
                    failedReasons.append("binarytif");
                }

                if (!(readerAtts.InternalImageAtts.Compression.equals(InternalImageAtts.Group4Compression))){
                    String pluralString = "";
                    if (failed) {
                        pluralString = "-";
                    }
                    failedReasons.append(String.format("%stiffnotgroup4", pluralString));
                }
            }

            return failedReasons.toString();
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
