package io.bdrc.am.audit.audittests;

import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataNode;
import java.io.IOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static javax.imageio.metadata.IIOMetadataFormatImpl.standardMetadataFormatName;


    class InternalImageAtts {

        // Normalized value of group 4 compression tag. Different images have
        // different readings of this. See IsGroup4Compression
        private final String _group4Compression = "CCITT T6";
        // These are the public data fields
        String Compression = "";
        Integer BitDepth;
        Integer ImageTypeNum;

        /**
         * Test input compression value for "CCITT T6" or "CCITT T.6"
         *
         * @return true if the <code>Compression</code> field is one of the variants of CCITT T6
         * Libraries are inconsistent
         */
        public boolean IsGroup4Compression() {
            String upComp = Compression.toUpperCase().replace(".", "");
            return upComp.equals(_group4Compression);
        }

        public void LoadCompressionValues(ImageReader reader, int imageIndex) {
            try {
                IIOMetadataNode iioMetadata = (IIOMetadataNode) reader.getImageMetadata(imageIndex).getAsTree
                        (standardMetadataFormatName);

                // dont care if fails
                Compression = ((IIOMetadataNode) (iioMetadata.getElementsByTagName
                        ("CompressionTypeName")).item(0)).getAttribute("value");
            } catch (Exception eek) {
                Compression = "EXC_READ";
            }
        }

    }
