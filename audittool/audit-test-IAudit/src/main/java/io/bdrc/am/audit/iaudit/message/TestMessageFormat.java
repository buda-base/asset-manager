package io.bdrc.am.audit.iaudit.message;

public class TestMessageFormat {
    final int argCount;
    String formatString ;

    public TestMessageFormat(int argc, String argFormat) {
        argCount = argc;
        formatString = argFormat;
    }
}
