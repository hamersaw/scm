package io.blackpine.scm;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;
import net.sourceforge.pmd.lang.scala.ast.ASTDefnDef;
import net.sourceforge.pmd.lang.scala.ast.ASTDefnObject;
import net.sourceforge.pmd.lang.scala.ast.ASTPkg;

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
        // initailize language parser
        LanguageVersion languageVersion =
            new ScalaLanguageModule().getVersion("2.13");

        PMDConfiguration configuration = new PMDConfiguration();
        Parser parser = PMD.parserFor(languageVersion, configuration);

        // parse nodes
        Node node = parser.parse(filename, reader);
        parseNode(node);
    }

    protected void parseNode(Node node) {
        if (node instanceof ASTPkg) {
            // if package is not already registered -> register
            ASTPkg defnNode = (ASTPkg) node;

            String packageName = defnNode.getNode().ref().toString();
            if (!this.packages.contains(packageName)) {
                this.packages.add(packageName);
            }
        } else if (node instanceof ASTDefnObject) {
            // increment class count
            this.classCount.incrementAndGet();
        } else if (node instanceof ASTDefnDef) {
            // increment method count
            this.methodCount.incrementAndGet();
        }

        for (int i=0; i<node.jjtGetNumChildren(); i++) {
            parseNode(node.jjtGetChild(i));
        }
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
