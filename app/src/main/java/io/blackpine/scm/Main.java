package io.blackpine.scm;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import org.reflections.Reflections;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

@Command(name="scm", mixinStandardHelpOptions=true,
    description="displays code metrics for specified directory(ies) or file(s)")
public class Main implements Callable<Integer> {
    @Option(names={"-t", "--thread-count"},
        description="thread count [default: 4]")
    private short threadCount = 4;

    @Parameters(index="0..*", description="root file(s) to evaluate")
    private File[] roots;

    private List<Class<? extends Analyzer>> analyzerClasses =
        new ArrayList(Arrays.asList(JavaAnalyzer.class,
            JavascriptAnalyzer.class, ScalaAnalyzer.class));

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        // initialize analyzer instances
        List<Analyzer> analyzers = new ArrayList();
        HashMap<String, Analyzer> extensions = new HashMap();
        for (Class<? extends Analyzer> analyzerClass : analyzerClasses) {
            Analyzer analyzer = analyzerClass.newInstance();
            analyzers.add(analyzer);

            for (String extension : analyzer.getParsableExtensions()) {
                // check if extension already exists
                if (extensions.containsKey(extension)) {
                    System.err.println("can not register extension '" +
                        extension + "' more than once");
                    return 1;
                }

                extensions.put(extension, analyzer);
            }
        }

        // intialize worker threads
        BlockingQueue<String> queue = new LinkedBlockingQueue();
        List<FutureTask> futureTasks = new ArrayList();
        for (int i=0; i<this.threadCount; i++) {
            FutureTask<Integer> futureTask =
                new FutureTask(new Worker(extensions, queue));
            futureTasks.add(futureTask);

            Thread thread = new Thread(futureTask);
            thread.start();
        }

        // iterate over roots
        for (File root : this.roots) {
            // check if file exists
            if (!root.exists()) {
                System.err.println("root file '" + root
                    + "' does not exist");
                continue;
            }

            // recurse through directory checking for parsable files
            this.findFiles(root, queue);
        }

        // send poison pills down queue
        for (int i=0; i<this.threadCount; i++) {
            queue.put("");
        }

        // wait for worker threads to complete
        for (FutureTask<Integer> futureTask : futureTasks) {
            int exitCode = futureTask.get();
            if (exitCode != 0) {
                return exitCode;
            }
        }

        // TODO - print analyzer results
        Printer printer = new TabbedPrinter();
        for (Analyzer analyzer : analyzers) {
            if (analyzer.updated()) {
                printer.process(analyzer.getLanguage(),
                    analyzer.getAttributes());
            }
        }

        return 0;
    }

    public void findFiles(File root, BlockingQueue<String> queue)
            throws Exception {
        if (root.isDirectory()) {
            // if directory -> iterate over children
            for (File child : root.listFiles()) {
                this.findFiles(child, queue);
            }
        } else if (root.isHidden()) {
            // TODO - skip hidden files?
        } else {
            queue.put(root.getAbsolutePath());
        }
    }

    class Worker implements Callable<Integer> {
        HashMap<String, Analyzer> extensions;
        BlockingQueue<String> queue;

        public Worker(HashMap<String, Analyzer> extensions,
                BlockingQueue<String> queue) {
            this.extensions = extensions;
            this.queue = queue;
        }

        @Override
        public Integer call() throws Exception {
            String filename = null;
            while (true) {
                // take next file - break loop if poison pill (ie. "")
                filename = queue.take();
                if (filename.isEmpty()) {
                    break;
                }

                // identify file extension
                int lastDotIndex = filename.lastIndexOf('.');
                int lastSeparatorIndex =
                    filename.lastIndexOf(File.separatorChar);

                if (lastDotIndex <= lastSeparatorIndex) {
                    continue;
                }

                // check if extension is parasable
                String extension = filename.substring(lastDotIndex + 1);
                if (!extensions.containsKey(extension)) {
                    continue;
                }

                // parse file
                Analyzer analyzer = extensions.get(extension);
                analyzer.process(filename);
            }

            return 0;
        }
    }
}
