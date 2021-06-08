package io.blackpine.scm;

import java.util.Map;

public class TabbedPrinter implements Printer {
    @Override
    public void process(String language, Map<String, String> attributes) {
        System.out.println(language);
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            System.out.println("  " + entry.getKey()
                + " : " + entry.getValue());
        }
    }
}
