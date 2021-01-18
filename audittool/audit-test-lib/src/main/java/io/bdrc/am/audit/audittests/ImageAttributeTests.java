package io.bdrc.am.audit.audittests;

import com.google.common.collect.Streams;
import io.bdrc.am.audit.iaudit.LibOutcome;
import io.bdrc.am.audit.iaudit.Outcome;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;

import static javax.imageio.metadata.IIOMetadataFormatImpl.standardMetadataFormatName;

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

            ReportUnvisited( sysLogger, false);

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

            if (sysLogger.isDebugEnabled())
            {
                Iterator<ImageReader> ir = ImageIO.getImageReadersBySuffix("tif");

                while (ir.hasNext())
                {
                    ImageReader r = ir.next();
                    sysLogger.debug("reader obj: {}, class: {}", r, r.getClass().getCanonicalName());
                }

                sysLogger.debug("----------------------------------------");
                sysLogger.debug("Scan for plugins, then format name ");
                ImageIO.scanForPlugins();
                IIORegistry.getDefaultInstance().registerApplicationClasspathSpis();
                ir = ImageIO.getImageReadersByFormatName("TIFF");
                while (ir.hasNext())
                {
                    ImageReader r = ir.next();
                    sysLogger.debug("reader obj: {}, class: {}", r, r.getClass().getCanonicalName());
                }

                sysLogger.debug("----------------------------------------");
                sysLogger.debug("Scan for reader file suffixes");
                String[] a = ImageIO.getReaderFileSuffixes();
                for (int i = 0; i < a.length; i++)
                {
                    sysLogger.debug("reader file suffix {}: {}", i, a[i]);
                }
            }
            DirectoryStream.Filter<Path> filter =
                    entry -> (entry.toFile().isFile()
                                      && !(entry.toFile().isHidden()
                                                   || entry.toString().endsWith("json")));

            try (DirectoryStream<Path> imageFiles = Files.newDirectoryStream(imageGroupParent, filter))
            {
                for (Path imageFile : imageFiles)
                {
                    File fileObject = imageFile.toAbsolutePath().toFile();
                    String fileObjectPathString = imageFile.toAbsolutePath().toString();

                    String fileExt = FilenameUtils.getExtension(fileObjectPathString);

                    ImageReader reader;

                    try
                    {

                        // Thanks marc Agate
                        reader = Streams.stream(ImageIO.getImageReadersBySuffix(fileExt))
                                         .findFirst()
                                         .orElseThrow
                                                  (UnsupportedFormatException::new);

                        // sysLogger.debug("Got reader from ImageReadersBySuffix");
                        ImageInputStream in = ImageIO.createImageInputStream(fileObject);

                        ReaderAtts ra = new ReaderAtts(reader);

                        // we don't care about jpgs
                        if (ra.ImageFileFormat.equals(ReaderAtts.FILE_JPG))
                        {
                            in.close();
                            reader.dispose();
                            continue;
                        }
                        try
                        {
                            reader.setInput(in);

                            for (int i = 0; i < reader.getNumImages(true); i++)
                            {
                                InternalImageAtts iias = new InternalImageAtts();
                                ra.InternalImageAtts = iias;
                                ImageTypeSpecifier its = Streams.stream(reader.getImageTypes(0))
                                                                 .findFirst().orElseThrow(UnsupportedFormatException::new);

                                // sysLogger.debug("Got imageTypeSpecifier from getImageTypes");
                                ImageTypeAtts itas = new ImageTypeAtts();
                                iias.ImageTypeAtts = itas;

                                itas.BitDepth = its.getColorModel().getPixelSize();

                                // See java.awt.image.BufferedImage
                                itas.ImageTypeNum = its.getBufferedImageType();


                                try
                                {
                                    iias.iioMetadata = (IIOMetadataNode) reader.getImageMetadata(i).getAsTree
                                                                                                            (standardMetadataFormatName);

                                    // dont care if fails
                                    iias.Compression = ((IIOMetadataNode) (iias.iioMetadata.getElementsByTagName
                                                                                                    ("CompressionTypeName")).item(0)).getAttribute("value");
                                } catch (Exception eek)
                                {
                                    iias.iioMetadata = null;
                                    iias.Compression = "EXC_READ";
                                    sysLogger.error(eek.getMessage());
                                }
                            }
                        } finally
                        {
                            // jimk asset-manager-73
                            in.close();
                            reader.dispose();
                        }

                        // Phew. We got image data!!!!
                        String validationErrors = validate(ra);
                        if (validationErrors.length() > 0)
                        {
                            FailTest(LibOutcome.INVALID_TIFF, fileObjectPathString, validationErrors);
                        }
                    } catch (UnsupportedFormatException usfx)
                    {
//                        usfx.printStackTrace(System.out);
                        FailTest(LibOutcome.NO_IMAGE_READER, fileObjectPathString);
                    } catch (NoSuchFileException nsfe)
                    {
                        String badPath = nsfe.getFile();
                        sysLogger.error("No such file {}", badPath);
                        FailTest(LibOutcome.ROOT_NOT_FOUND, badPath);
                    } catch (Exception eek)
                    {
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
            if (!IsTestFailed())
            {
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
         * @param readerAtts collected image statistics from reader
         */
        private String validate(final ReaderAtts readerAtts) {
            boolean failed = false;
            StringBuilder failedReasons = new StringBuilder();

            if (readerAtts.ImageFileFormat.equals(ReaderAtts.FILE_TIFF))
            {

                // Test mode: 1 bit/pixel, image type num one of the
                // BufferedImage.ImageType enums
                ImageTypeAtts itas = readerAtts.InternalImageAtts.ImageTypeAtts;

                // We only care about non-monochrome files
                if (itas.BitDepth == 1)
                {
                    if (!(itas.ImageTypeNum == BufferedImage.TYPE_BYTE_GRAY ||
                                  itas.ImageTypeNum == BufferedImage.TYPE_BYTE_BINARY ||
                                  itas.ImageTypeNum == BufferedImage.TYPE_USHORT_GRAY))
                    {
                        failed = true;
                        failedReasons.append("binarytif");
                    }

                    if (!(readerAtts.InternalImageAtts.IsGroup4Compression()))
                    {
                        String pluralString = "";
                        if (failed)
                        {
                            pluralString = "-";
                        }
                        failedReasons.append(String.format("%stiffnotgroup4 :%s", pluralString,
                                String.format("bd:%d:\titn :%d:\tcomp :%s:",
                                        itas.BitDepth,
                                        itas.ImageTypeNum,
                                        readerAtts.InternalImageAtts
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
        if (IsTestFailed())
        {
            return;
        }
        TestWrapper(new ImageAttributeTestOperation());
    }

    // region private methods

    /**
     *
     * @return if there is any non-empty image group parents
     */
    private boolean hasValidTargets(ArrayList<String> possibles) {
       return possibles.stream().anyMatch(x -> x.length() > 0);
    }
    // endregion

}
