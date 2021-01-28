package io.bdrc.assetmanager;

public class InvalidObjectData extends Exception {
    public InvalidObjectData(final String s, final Throwable e) {
        super(s,e);
    }

    public InvalidObjectData(final String s) {
        super(s);
    }

    public InvalidObjectData(final Throwable e ) {
        super(e);
    }
}
