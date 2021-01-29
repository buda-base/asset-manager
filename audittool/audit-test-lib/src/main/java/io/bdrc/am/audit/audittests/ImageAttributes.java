package io.bdrc.am.audit.audittests;

import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataNode;

import java.io.IOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static javax.imageio.metadata.IIOMetadataFormatImpl.standardMetadataFormatName;

class ImageAttributes {
    public static final String FILE_TIFF = "TIFF";
    public static final String FILE_JPG = "JPG";

    /* TODO: Other types
    public static final String FILE_PNG = "PNG";
    public static final String FILE_BMP = "BMP";
    public static final String FILE_PDF = "PDF";
    */
    InternalImageAtts InternalImageAtts;
    String ImageFileFormat;

    public ImageAttributes(ImageReader inReader) throws UnsupportedFormatException {

        // For debugging, but we don't have a logger
//        System.out.println(inReader.getClass().getName());
//        Class it = inReader.getClass();
//        Class itSuper = it.getSuperclass();
//        while (itSuper != null) {
//            System.out.println(itSuper.getName());
//            itSuper = itSuper.getSuperclass();
//        }

        // Cleanup reduction asset-manager-85


        if (inReader instanceof com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader) {
            ImageFileFormat = ImageAttributes.FILE_JPG;
        } else if (inReader instanceof com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReader) {
            ImageFileFormat = ImageAttributes.FILE_TIFF;
        }
        // asset-manager-85: jimk: When we upgraded compiler to Java 14, these became unnecessary, as they were
        // implemented
        // in javax.imageio in Java 9
//         else if (inReader instanceof com.github.jaiimageio.impl.plugins.tiff.TIFFImageReader) {
//            ImageFileFormat = ImageAttributes.FILE_TIFF;
//        }
//        else if (inReader instanceof com.sun.imageio.plugins.jpeg.JPEGImageReader) {
//            ImageFileFormat = ImageAttributes.FILE_JPG;
//        }

        else {
            throw new UnsupportedFormatException("Unsupported file type");
        }

    }


    /**
     * Get internal attributes, and load them into reader attributes
     *
     * @param reader     ImageReader for image file object
     * @param imageIndex image inside the file
     * @throws UnsupportedFormatException if image reader cant load the attributes
     * @throws IOException                if reader cant retrieve the image types
     */
    public void LoadInternalImageAttribs(
            ImageReader reader, int imageIndex) throws UnsupportedFormatException, IOException
    {
        InternalImageAtts iias = new InternalImageAtts();
        this.InternalImageAtts = iias;

        // asset-manager-85: jimk: rework to avoid @Beta google guava streams
        //Extra step to get Spliterator
        Spliterator<ImageTypeSpecifier> splitItr = Spliterators
                .spliteratorUnknownSize(reader.getImageTypes(imageIndex), Spliterator.ORDERED);
        // Iterator -> Stream
        ImageTypeSpecifier imageTypeSpecifier =
                (StreamSupport.stream(splitItr, false)).findFirst().orElseThrow(UnsupportedFormatException::new);

        iias.BitDepth = imageTypeSpecifier.getColorModel().getPixelSize();

        // See java.awt.image.BufferedImage
        iias.ImageTypeNum = imageTypeSpecifier.getBufferedImageType();

        // Get Compression
        iias.LoadCompressionValues(reader, imageIndex);
    }
}
