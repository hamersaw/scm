package io.blackpine.scm;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import org.reflections.Reflections;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Callable;

@Command(name="scm", mixinStandardHelpOptions=true,
    description="displays code metrics for specified directory(ies) or file(s)")
public class Main implements Callable<Integer> {
    @Option(names={"-t", "--thread-count"},
        description="thread count [default: 4]")
    private short theadCount = 4;

    @Parameters(index="0..*", description="root file(s) to evaluate")
    private File[] roots;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        // find all classes (in this package) extending Analyzer
        Reflections reflections = 
            new Reflections(Main.class.getPackage().getName());
        Set<Class<? extends Analyzer>> analyzerClasses =
            reflections.getSubTypesOf(Analyzer.class);

        // initialize analyzer instances
        HashMap<String, Analyzer> analyzers = new HashMap();
        for (Class<? extends Analyzer> analyzerClass : analyzerClasses) {
            Analyzer analyzer = analyzerClass.newInstance();
            for (String extension : analyzer.getParsableExtensions()) {
                // TODO - check if extension already exists
                analyzers.put(extension, analyzer);
            }
        }

        // TODO - intialize worker threads

        // iterate over roots
        for (File root : this.roots) {
            // check if file exists
            if (!root.exists()) {
                System.err.println("root file '" + root
                    + "' does not exist");
                continue;
            }

            // recurse through directory checking for parsable files
            this.findParsableFiles(root, analyzers.keySet());
        }

        // TODO - wait for worker threads to complete

        return 0;
    }

    public void findParsableFiles(File root,
            Set<String> parsableExtensions) {
        if (root.isDirectory()) {
            // if directory -> iterate over children
            for (File child : root.listFiles()) {
                this.findParsableFiles(child, parsableExtensions);
            }
        } else if (root.isHidden()) {
            // TODO - skip hidden files?
        } else {
            // identify file extension
            String filename = root.getAbsolutePath();
            int lastDotIndex = filename.lastIndexOf('.');
            int lastSeparatorIndex =
                filename.lastIndexOf(File.separatorChar);

            if (lastDotIndex <= lastSeparatorIndex) {
                return;
            }

            // check if extension is parasable
            String extension = filename.substring(lastDotIndex + 1);
            if (parsableExtensions.contains(extension)) {
                // TODO - add file to parse queue
                System.out.println("TODO - parse '" + filename + "'");
            }
        }
    }
}
