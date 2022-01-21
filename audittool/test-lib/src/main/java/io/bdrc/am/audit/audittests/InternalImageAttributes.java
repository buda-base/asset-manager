package io.bdrc.am.audit.audittests;

import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataNode;


import java.io.IOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static javax.imageio.metadata.IIOMetadataFormatImpl.standardMetadataFormatName;

/**
 * Holds various values extracted from IIOMetadata and ImageTypeSpecifier libraries
 */
    class InternalImageAtts {

        // Normalized value of group 4 compression tag. Different images have
        // different readings of this. See IsGroup4Compression
        private final String _group4Compression = "CCITT T6";
        // These are the public data fields
        String Compression = "";
        Integer BitDepth;
        Integer ImageTypeNum;

        public InternalImageAtts ( ImageReader reader, int imageIndex) throws IOException, UnsupportedFormatException {

            // asset-manager-85: jimk: rework to avoid @Beta google guava streams
            //Extra step to get Spliterator
            Spliterator<ImageTypeSpecifier> splitItr = Spliterators
                    .spliteratorUnknownSize(reader.getImageTypes(imageIndex), Spliterator.ORDERED);
            // Iterator -> Stream
            ImageTypeSpecifier imageTypeSpecifier =
                    (StreamSupport.stream(splitItr, false)).findFirst().orElseThrow(UnsupportedFormatException::new);

            BitDepth = imageTypeSpecifier.getColorModel().getPixelSize();

            // See java.awt.image.BufferedImage
            ImageTypeNum = imageTypeSpecifier.getBufferedImageType();

            // Get Compression
            LoadCompressionValues(reader, imageIndex);
        }
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

                // diag only IIOMetadataDumper.dumpMetadata(reader.getImageMetadata(imageIndex));

                // dont care if fails
                Compression = ((IIOMetadataNode) (iioMetadata.getElementsByTagName
                        ("CompressionTypeName")).item(0)).getAttribute("value");
            } catch (Exception eek) {
                Compression = "EXC_READ";
            }
        }
    }
