package net.openplexus;

import org.apache.commons.collections.bag.HashBag;

/**
 *
 * @author Robert Giacinto
 */
public class Module {
    protected String name;
    protected String description;
    protected HashBag collocations;
    

    public Module(String name, String description) {
        this.name = name;
        this.description = description;
        collocations = new HashBag();
    }
    
    
}
