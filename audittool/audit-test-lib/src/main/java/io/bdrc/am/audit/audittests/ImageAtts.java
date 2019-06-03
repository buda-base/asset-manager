package io.bdrc.am.audit.audittests;

import org.w3c.dom.Node;

import javax.imageio.metadata.IIOMetadataNode;
import java.util.ArrayList;

/**
 * Collection of image attributes.
 * Since JavaIO iterates over many variables, such as readers, images, and metadata format names,
 * Collect all the possible values here
 */
class ImageAtts {
    ArrayList<ReaderAtts> ReaderAtts = new ArrayList<>();
}

class ReaderAtts {
    ArrayList<InternalImageAtts> InternalImageAtts = new ArrayList<>();
}

class InternalImageAtts {
    ArrayList<ImageTypeAtts> ImageTypeAtts = new ArrayList<>();
    IIOMetadataNode iioMetadata = null;
    String Compression = "";
}

class ImageTypeAtts {
    Integer BitDepth ;
    Integer ImageTypeNum ;
}
