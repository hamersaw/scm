package io.blackpine.scm;

import java.io.Reader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ScalaAnalyzer extends Analyzer {
    public static final String[] parsableExtensions =
        new String[]{"scala"};

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
    public void processReader(String filename,
            Reader reader) throws Exception {
        System.err.println("ScalaAnalyzer.parse(...) unimplemented");
    }

    public String toJson() {
        return "{\"classCount\":" + this.classCount.get()
            + ",\"fileCount\":" + this.fileCount.get()
            + ",\"methodCount\":" + this.methodCount.get()
            + ",\"packageCount\":" + this.packages.size() + "}";
    }
}
