package io.blackpine.scm;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;

import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.JavaLanguageHandler;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class JavaAnalyzer extends Analyzer {
    public static final String[] parsableExtensions =
        new String[]{"java"};
    public static final String language = "java";

    protected AtomicInteger classCount, methodCount;
    protected Set<String> packages;

    public JavaAnalyzer() {
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
        LanguageVersion languageVersion = new LanguageVersion(
            new JavaLanguageModule(), "", new JavaLanguageHandler(11));

        PMDConfiguration configuration = new PMDConfiguration();
        Parser parser = PMD.parserFor(languageVersion, configuration);

        // parse nodes
        //printChildren(node, 0);
        Node node = parser.parse(filename, reader);
        parseNode(node);
    }

    /*private void printChildren(Node root, int index) {
        String line = "";
        for (int i=0; i<index; i++) {
            line += "  ";
        }

        System.out.println(line + root);

        for (int i=0; i<root.jjtGetNumChildren(); i++) {
            printChildren(root.jjtGetChild(i), index+1);
        }
    }*/

    private void parseNode(Node node) {
        if (node instanceof ASTPackageDeclaration) {
            // if package is not already registered -> register
            ASTPackageDeclaration declarationNode = 
                (ASTPackageDeclaration) node;

            if (!this.packages.contains(declarationNode.getName())) {
                this.packages.add(declarationNode.getName());
            }
        } else if (node instanceof ASTClassOrInterfaceDeclaration) {
            // increment class count
            this.classCount.incrementAndGet();
        } else if (node instanceof ASTMethodDeclaration) {
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
