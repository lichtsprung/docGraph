package net.openplexus;

import java.util.HashMap;
import org.apache.commons.collections.bag.HashBag;

/**
 * Ein Modul innerhalb des Modulhandbuchs.
 *
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
    protected HashMap<String, Integer> termVector;

    /**
     * Erzeugt ein neues Modul.
     *
     * @param name der Name des Moduls
     * @param description die gesamte Beschreibung des Moduls
     * @param tokens der Beschreibungstext in einzelnen Tokens
     */
    public Module(String name, String description, String[] tokens) {
        this.name = name;
        this.description = description;
        this.tokens = tokens;
        terms = new HashBag();
        termVector = new HashMap<String, Integer>();
    }
    
    public HashMap<String, Integer> getTermVector(Vocabulary vocabulary){
        HashMap<String, Integer> t = new HashMap<String, Integer>();
        return t;
    }
}
