package io.bdrc.am.audit.audittests;

import javax.imageio.ImageReader;

class ImageAttributes {
    public static final String FILE_TIFF = "TIFF";
    public static final String FILE_JPG = "JPG";

    /* TODO: Other types
    public static final String FILE_PNG = "PNG";
    public static final String FILE_BMP = "BMP";
    public static final String FILE_PDF = "PDF";
    */

    // Container for IIOMetadata
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
}
