package io.bdrc.am.audit.audittests;

import io.bdrc.am.audit.iaudit.LibOutcome;
import io.bdrc.am.audit.iaudit.Outcome;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public class ImageAttributeTests extends ImageGroupParents {
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

            // Is there anything to do?
            if (!hasValidTargets(_imageGroupParents)) {
                PassTest();
                return;
            }

            // We only want directories in the _imageGroupParents entries
            DirectoryStream.Filter<Path> filter =
                    entry -> (entry.toFile().isDirectory()
                            && !entry.toFile().isHidden()
                            && _imageGroupParents.contains(entry.getFileName().toString()));

            try (DirectoryStream<Path> imageGroupParents = Files.newDirectoryStream(Paths.get(getPath()), filter)) {
                for (Path anImageGroupParent : imageGroupParents) {

                    MarkVisited(anImageGroupParent.getFileName().toString());

                    DirectoryStream.Filter<Path> imageGroupFilter =
                            entry -> (entry.toFile().isDirectory()
                                    && !entry.toFile().isHidden());

                    for (Path anImageGroup : Files.newDirectoryStream(anImageGroupParent, imageGroupFilter)) {
                        TestImages(anImageGroup);
                    }
                }
                // Because we have a "non-set" state
                if (!IsTestFailed()) {
                    PassTest();
                }
            } catch (DirectoryIteratorException die) {
                sysLogger.error("Directory iteration error", die);
                FailTest(Outcome.SYS_EXC, die.getCause().getLocalizedMessage());

            }

            ReportUnvisited(sysLogger, false);

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
         * (see ImageAttributes ctor)
         * <p>
         * .info["compression"] = IIOMetadata node "CompressionTypeName"
         * .mode = ImageTypeSpecifier.getBufferedImageType (see BufferImage.java for constants)
         *
         * @param imageGroupParent folder containing imageGroups
         * @throws IOException If io error
         */
        private void TestImages(final Path imageGroupParent) throws IOException {

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
            // before you scan for plugins
            String classpathStr = System.getProperty("java.class.path");
            sysLogger.debug("Classpath {}", classpathStr);
            sysLogger.debug("Pre scan for plugins - by suffix ");

            if (sysLogger.isDebugEnabled()) {
                Iterator<ImageReader> ir = ImageIO.getImageReadersBySuffix("tif");

                while (ir.hasNext()) {
                    ImageReader r = ir.next();
                    sysLogger.debug("reader obj: {}, class: {}", r, r.getClass().getCanonicalName());
                }

                sysLogger.debug("----------------------------------------");
                sysLogger.debug("Scan for plugins, then format name ");
                ImageIO.scanForPlugins();
                IIORegistry.getDefaultInstance().registerApplicationClasspathSpis();
                ir = ImageIO.getImageReadersByFormatName("TIFF");
                while (ir.hasNext()) {
                    ImageReader r = ir.next();
                    sysLogger.debug("reader obj: {}, class: {}", r, r.getClass().getCanonicalName());
                }

                sysLogger.debug("----------------------------------------");
                sysLogger.debug("Scan for reader file suffixes");
                String[] a = ImageIO.getReaderFileSuffixes();
                for (int i = 0; i < a.length; i++) {
                    sysLogger.debug("reader file suffix {}: {}", i, a[i]);
                }
            }
            DirectoryStream.Filter<Path> filter =
                    entry -> (entry.toFile().isFile()
                            && !(entry.toFile().isHidden()
                            || entry.toString().endsWith("json")));

            try (DirectoryStream<Path> imageFiles = Files.newDirectoryStream(imageGroupParent, filter)) {
                for (Path imageFile : imageFiles) {
                    File fileObject = imageFile.toAbsolutePath().toFile();
                    String fileObjectPathString = imageFile.toAbsolutePath().toString();

                    // String fileExt = FilenameUtils.getExtension(fileObjectPathString);

                    boolean validatedFile = false;


                    try {

                        // Thanks marc Agate
//                        reader = Streams.stream(ImageIO.getImageReadersBySuffix(fileExt))
//                                         .findFirst()
//                                         .orElseThrow
//                                                  (UnsupportedFormatException::new);

                        // sysLogger.debug("Got reader from ImageReadersBySuffix");
                        ImageInputStream imageInputStream = ImageIO.createImageInputStream(fileObject);
                        // this ide from https://github.com/haraldk/TwelveMonkeys/issues/428
                        Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);

                        while (!validatedFile && readers.hasNext()) {
                            ImageReader reader = readers.next();

                            // We need a certain reader. Try the next one in the list if
                            // this reader doesnt work
                            try {
                                ImageAttributes readerAttribs = new ImageAttributes(reader);

                                try {

                                    reader.setInput(imageInputStream);

                                    // Slow - take out of loop
                                    if (reader.getNumImages(true) < 1) {
                                        continue;
                                    }
                                    // Later - do multiple images
                                        readerAttribs.LoadInternalImageAttribs(reader,0);
                                        // Extract EXIF data, to validate rotation

                                } finally {
                                    // jimk asset-manager-73
                                    imageInputStream.close();
                                    reader.dispose();
                                }


                            // Phew. We got image data!!!!
                            String validationErrors = validate(readerAttribs);
                            if (validationErrors.length() > 0) {
                                FailTest(LibOutcome.INVALID_TIFF, fileObjectPathString, validationErrors);
                            }
                            validatedFile = true;

                            } catch (UnsupportedFormatException usfx) {

                                // All this means is that the reader is not one of the ones we care
                                // about. Try another reader
                            }
                        }    // for each reader
                    } catch (NoSuchFileException nsfe) {
                        String badPath = nsfe.getFile();
                        sysLogger.error("No such file {}", badPath);
                        FailTest(LibOutcome.ROOT_NOT_FOUND, badPath);
                    } catch (Exception eek) {
                        FailTest(Outcome.SYS_EXC, "ImageAttributeTest", " in " + fileObjectPathString + ":" + eek
                                .getMessage
                                        ());
                    }

                    // None of the available readers could read this file
                    if (!validatedFile) {
                        FailTest(LibOutcome.NO_IMAGE_READER, fileObjectPathString);
                    }


                    // TODO:  1 image / file !!!!
                    /*
                    *  fields: im: Image.open
im.width
im.height
im.info["compression"] "group4"  " G4 is defined in the ITU-T T.6 fax standard for transmitting black and white images."
"CCITT T6" (T.6?) (per Java Imaging IO  is the name of Group 4 encoding
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

            sysLogger.debug("Test outcome {} error count {}", getTestResult().getOutcome(),
                    getTestResult()
                            .getErrors().size());
            if (!IsTestFailed()) {
                PassTest();
            }

        }


        /**
         * Validate normalized image statistics
         * if format is "TIFF"
         * - Compression must be "CCITT T6" ( group 4) or "CCITT T.6" (libraries are inconsistent)
         * - ImageTypeNum must be BufferedImage.TYPE_BYTE_BINARY
         * <p>
         * If TIFF, image must be Group4 compression and "binary"
         *
         * @param imageAttributes collected image statistics from reader
         */
        private String validate(final ImageAttributes imageAttributes) {
            boolean failed = false;
            StringBuilder failedReasons = new StringBuilder();

            if (imageAttributes.ImageFileFormat.equals(ImageAttributes.FILE_TIFF)) {

                // Test mode: 1 bit/pixel, image type num one of the
                // BufferedImage.ImageType enums
                InternalImageAtts itas = imageAttributes.InternalImageAtts;

                // We only care about non-monochrome files
                if (itas.BitDepth == 1) {
                    if (!(itas.ImageTypeNum == BufferedImage.TYPE_BYTE_GRAY ||
                            itas.ImageTypeNum == BufferedImage.TYPE_BYTE_BINARY ||
                            itas.ImageTypeNum == BufferedImage.TYPE_USHORT_GRAY)) {
                        failed = true;
                        failedReasons.append("binarytif");
                    }

                    if (!(imageAttributes.InternalImageAtts.IsGroup4Compression())) {
                        String pluralString = "";
                        if (failed) {
                            pluralString = "-";
                        }
                        failedReasons.append(String.format("%stiffnotgroup4 :%s", pluralString,
                                String.format("bd:%d:\titn :%d:\tcomp :%s:",
                                        itas.BitDepth,
                                        itas.ImageTypeNum,
                                        imageAttributes.InternalImageAtts
                                                .Compression)));
                    }
                }
            }

            return failedReasons.toString();
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
        TestWrapper(new ImageAttributeTestOperation());
    }

    // region private methods

    /**
     * @return if there is any non-empty image group parents
     */
    private boolean hasValidTargets(ArrayList<String> possibles) {
        return possibles.stream().anyMatch(x -> x.length() > 0);
    }
    // endregion

}
