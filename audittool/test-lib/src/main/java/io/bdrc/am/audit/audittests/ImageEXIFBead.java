package io.bdrc.am.audit.audittests;

import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;

public class ImageEXIFBead {

    public int getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(final int tagNumber) {
        this.tagNumber = tagNumber;
    }

    public int getTagValue() {
        return tagValue;
    }

    public void setTagValue(final int tagValue) {
        this.tagValue = tagValue;
    }

    public String getTagDescription() {
        return tagDescription;
    }

    public void setTagDescription(final String tagDescription) {
        this.tagDescription = tagDescription;
    }

    private int tagNumber;
    private int tagValue;
    private String tagDescription;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageEXIFBead)) return false;
        final ImageEXIFBead that = (ImageEXIFBead) o;
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
            return String.format("tag: %d value: %d", tagNumber, tagValue);
        else
            return tagDescription;
    }
}

