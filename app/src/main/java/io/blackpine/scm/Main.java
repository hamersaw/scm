package io.blackpine.scm;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name="scm", mixinStandardHelpOptions=true,
    description="displays code metrics for a specified directory")
public class Main implements Callable<Integer> {
    @Parameters(index="0..*", description="root file(s) to evaluate")
    private File[] roots;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        // iterate over roots
        for (File root : this.roots) {
            // check if file exists
            if (!root.exists()) {
                System.err.println("root file '" + root
                    + "' does not exist");
                continue;
            }

            // recurse through directory checking for parsable files
        }

        return 0;
    }

    public void findParsableFiles(File root) {
    }
}
