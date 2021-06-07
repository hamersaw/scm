package io.blackpine.scm;

import java.io.File;

public class ScalaAnalyzer extends Analyzer {
    public static final String[] parsableExtensions =
        new String[]{"scala"};

    @Override
    public String[] getParsableExtensions() {
        return parsableExtensions;
    }

    @Override
    public void parse(File file) throws Exception {
        System.err.println("ScalaAnalyzer.parse(...) unimplemented");
    }
}
