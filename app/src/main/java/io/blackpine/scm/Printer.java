package io.blackpine.scm;

import java.util.Map;

public interface Printer {
    public abstract void process(String language,
        Map<String, String> attributes);
}
