package io.blackpine.scm;

import java.io.FileReader;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Analyzer {
    protected AtomicInteger fileCount;

    public Analyzer() {
        this.fileCount = new AtomicInteger();
    }

    public abstract String[] getParsableExtensions();
    public abstract String getLanguage();

    public void process(String filename) throws Exception {
        // open FileReader and process
        FileReader reader = new FileReader(filename);
        this.processReader(filename, reader);
        reader.close();

        // increment fileCount
        this.fileCount.incrementAndGet();
    }

    public abstract void processReader(String filename,
        Reader reader) throws Exception;

    public boolean updated() {
        return this.fileCount.get() != 0;
    }

    public abstract Map<String, String> getAttributes();
}
