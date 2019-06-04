package io.bdrc.am.audit.audittests;

import com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReader;

import javax.imageio.ImageReader;
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

        if (inReader instanceof TIFFImageReader) {
            ImageFileFormat = ReaderAtts.FILE_TIFF;
        }
        else if (inReader instanceof JPEGImageReader) {
            ImageFileFormat = ReaderAtts.FILE_JPG;
        }
        else throw new UnsupportedFormatException("Unsupported file type");
    }
}

class InternalImageAtts {
    public static final String Group4Compression = "CCITT 6" ; // T.6
    ImageTypeAtts ImageTypeAtts = null;
    IIOMetadataNode iioMetadata = null;
    String Compression = "";
}

class ImageTypeAtts {
    Integer BitDepth ;
    Integer ImageTypeNum ;
}
