package io.bdrc.am.audit.audittests;

import com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader;
// import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReader;

import javax.imageio.ImageReader;
// import com.github.jaiimageio.impl.plugins.tiff.TIFFImageReader;
import javax.imageio.metadata.IIOMetadataNode;


class ReaderAtts {
    public static final String FILE_TIFF = "TIFF";
    public static final String FILE_JPG = "JPG";

    /* TODO: Other types
    public static final String FILE_PNG = "PNG";
    public static final String FILE_BMP = "BMP";
    public static final String FILE_PDF = "PDF";
    */
    InternalImageAtts InternalImageAtts;
    String ImageFileFormat ;

    public ReaderAtts(ImageReader inReader) throws UnsupportedFormatException {

        // For debugging, but we don't have a logger
//        System.out.println(inReader.getClass().getName());
//        Class it = inReader.getClass();
//        Class itSuper = it.getSuperclass();
//        while (itSuper != null) {
//            System.out.println(itSuper.getName());
//            itSuper = itSuper.getSuperclass();
//        }
        if (inReader instanceof com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReader) {
            ImageFileFormat = ReaderAtts.FILE_TIFF;
        }
        else if (inReader instanceof com.github.jaiimageio.impl.plugins.tiff.TIFFImageReader) {
            ImageFileFormat = ReaderAtts.FILE_TIFF;
        }
        else if (inReader instanceof com.sun.imageio.plugins.jpeg.JPEGImageReader) {
            ImageFileFormat = ReaderAtts.FILE_JPG;
        }
        else if (inReader instanceof com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader) {
            ImageFileFormat = ReaderAtts.FILE_JPG;
        }
        else
        {
            throw new UnsupportedFormatException("Unsupported file type");
        }

    }
}

class InternalImageAtts {
    private final String _group4Compression = "CCITT T6";
    ImageTypeAtts ImageTypeAtts = null;
    IIOMetadataNode iioMetadata = null;
    String Compression = "";

    /**
     * Test input compression value for "CCITT T6" or "CCITT T.6"
     * @return true if the <code>Compression</code> field is one of the variants of CCITT T6
     * Libraries are inconsistent
     */
    public boolean IsGroup4Compression() {
        String upComp = Compression.toUpperCase().replace(".","");
        return upComp.equals(_group4Compression) ;
    }
}

class ImageTypeAtts {
    Integer BitDepth ;
    Integer ImageTypeNum ;
}
