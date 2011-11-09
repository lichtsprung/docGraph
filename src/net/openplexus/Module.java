package net.openplexus;

import org.apache.commons.collections.bag.HashBag;

/**
 * Ein Modul innerhalb des Modulhandbuchs.
 * @author Robert Giacinto
 */
public class Module {

    /**
     * Der Name des Moduls.
     */
    protected String name;
    /**
     * Der Modultext.
     */
    protected String description;
    /**
     * Die Terme, die im Modul gefunden wurden.
     */
    protected HashBag terms;
    /**
     * Der gesamte Text in einzelnen Tokens.
     */
    protected String[] tokens;


    /**
     * Erzeugt ein neues Modul.
     * @param name der Name des Moduls
     * @param description die gesamte Beschreibung des Moduls
     * @param tokens der Beschreibungstext in einzelnen Tokens
     */
    public Module(String name, String description, String[] tokens) {
        this.name = name;
        this.description = description;
        this.tokens = tokens;
        terms = new HashBag();
    }
}
