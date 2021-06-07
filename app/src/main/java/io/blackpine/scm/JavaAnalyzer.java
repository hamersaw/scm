package io.blackpine.scm;

import java.io.File;

public class JavaAnalyzer extends Analyzer {
    public static final String[] parsableExtensions =
        new String[]{"java"};

    @Override
    public String[] getParsableExtensions() {
        return parsableExtensions;
    }

    @Override
    public void parse(File file) throws Exception {
        System.err.println("JavaAnalyzer.parse(...) unimplemented");
    }
}
