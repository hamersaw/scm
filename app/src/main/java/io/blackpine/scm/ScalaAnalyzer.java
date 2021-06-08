package io.blackpine.scm;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ScalaAnalyzer extends Analyzer {
    public static final String[] parsableExtensions =
        new String[]{"scala"};
    public static final String language = "scala";

    protected AtomicInteger classCount, methodCount;
    protected Set<String> packages;

    public ScalaAnalyzer() {
        this.classCount = new AtomicInteger(0);
        this.methodCount = new AtomicInteger(0);
        this.packages = ConcurrentHashMap.newKeySet();
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
        System.err.println("ScalaAnalyzer.parse(...) unimplemented");
    }

    @Override
    public Map<String, String> getAttributes() {
        HashMap<String, String> attributes = new HashMap();
        attributes.put("fileCount",
            Integer.toString(this.fileCount.get()));
        attributes.put("classCount",
            Integer.toString(this.classCount.get()));
        attributes.put("methodCount",
            Integer.toString(this.methodCount.get()));
        attributes.put("packageCount",
            Integer.toString(this.packages.size()));

        return attributes;
    }
}
