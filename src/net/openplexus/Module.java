package net.openplexus;

import org.apache.commons.collections.bag.HashBag;

/**
 *
 * @author Robert Giacinto
 */
public class Module {

    protected String name;
    protected String description;
    protected HashBag terms;
    protected Vocabulary vocabulary;

    public Module(String name, String description, Vocabulary vocabulary) {
        this.name = name;
        this.description = description;
        this.vocabulary = vocabulary;
        terms = new HashBag();
    }
}
