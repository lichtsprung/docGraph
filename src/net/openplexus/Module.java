package net.openplexus;

import java.util.HashMap;

/**
 *
 * @author Robert Giacinto
 */
public class Module {
    protected String name;
    protected String description;
    protected HashMap<String, Integer> collocations;
    

    public Module(String name, String description) {
        this.name = name;
        this.description = description;
        collocations = new HashMap<String, Integer>();
    }
    
    
}
