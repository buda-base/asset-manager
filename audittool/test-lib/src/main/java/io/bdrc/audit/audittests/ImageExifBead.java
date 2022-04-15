package io.bdrc.audit.audittests;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;

public class ImageExifBead {

    public int getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(final int tagNumber) {
        this.tagNumber = tagNumber;
    }

    public Object getTagValue() {
        return tagValue;
    }

    public void setTagValue(final Object tagValue) {
        this.tagValue = tagValue;
    }

    public String getTagDescription() {
        return tagDescription;
    }

    public void setTagDescription(final String tagDescription) {
        this.tagDescription = tagDescription;
    }

    private int tagNumber;
    private Object tagValue;
    private String tagDescription;

    public ImageExifBead()
    {

    }

    /**
     * Create an imageExifBead from a DrewNoakes metadata directory object
     * Note the tag is descriptive only. the value lives in the directory
     *
     * @param exifDirectory metadata class
     * @param tagType tag whose value populates this object
     */
    public ImageExifBead(Directory exifDirectory, int tagType) throws MetadataException {
        Tag tag = new Tag(tagType, exifDirectory);

        this.setTagNumber(tag.getTagType());
        this.setTagValue(exifDirectory.getObject(this.getTagNumber()));
        this.setTagDescription(String.format("[%s] - %s  value: %s desc: %s",
                exifDirectory.getName(), tag.getTagName(), this.getTagValue().toString(), tag.getDescription()));


    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final ImageExifBead that)) return false;
        return getTagNumber() == that.getTagNumber() &&
                getTagValue() == that.getTagValue() &&
                Objects.equal(getTagDescription(), that.getTagDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tagNumber, tagValue, tagDescription);
    }

    @Override
    public String toString() {
        if (StringUtils.isBlank(tagDescription))
            return String.format("tag: %d value: %s", tagNumber, tagValue.toString());
        else
            return tagDescription;
    }
}

