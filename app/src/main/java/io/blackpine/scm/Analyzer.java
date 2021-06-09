package io.blackpine.scm;

import net.sourceforge.pmd.lang.ast.Node;

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

    protected void printNode(Node node, int index) {
        String line = "";
        for (int i=0; i<index; i++) {
            line += "  ";
        }

        System.out.println(line + node);

        for (int i=0; i<node.jjtGetNumChildren(); i++) {
            this.printNode(node.jjtGetChild(i), index+1);
        }
    }
}
