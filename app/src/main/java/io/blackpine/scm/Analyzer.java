package io.blackpine.scm;

import java.io.File;

public abstract class Analyzer {
    public abstract void parse(File file) throws Exception;
}
