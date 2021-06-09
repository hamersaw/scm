package io.blackpine.scm;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class JavascriptAnalyzer extends Analyzer {
    public static final String[] parsableExtensions =
        new String[]{"js"};
    public static final String language = "javascript";

    protected AtomicInteger methodCount;

    public JavascriptAnalyzer() {
        this.methodCount = new AtomicInteger(0);
    }

    @Override
    public String[] getParsableExtensions() {
        return parsableExtensions;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public void processReader(String filename,
            Reader reader) throws Exception {
        System.err.println("JavascriptAnalyzer.parse(...) unimplemented");
    }

    @Override
    public Map<String, String> getAttributes() {
        HashMap<String, String> attributes = new HashMap();
        attributes.put("fileCount",
            Integer.toString(this.fileCount.get()));
        attributes.put("methodCount",
            Integer.toString(this.methodCount.get()));

        return attributes;
    }
}
