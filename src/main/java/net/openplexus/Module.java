package net.openplexus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    /**
     * Der Termvektor des Moduls.
     */
    protected List<TVComponent> featureVector;
    /**
     * Ähnlichkeitsvektor, der die Ähnlichkeiten zu den anderen Modulen
     * speichert.
     */
    protected HashMap<Module, Double> similarities;

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
        featureVector = new ArrayList<TVComponent>(tokens.length);
        similarities = new HashMap<Module, Double>(50);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Module other = (Module) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 37 * hash + (this.description != null ? this.description.hashCode() : 0);
        return hash;
    }

    public String getName() {
        return name;
    }

    public HashMap<Module, Double> getSimilarities() {
        return similarities;
    }

    public HashMap<Module, Double> getSimilarities(double min, double max) {
        HashMap<Module, Double> sim = new HashMap<Module, Double>(similarities.size());
        for (Module m : similarities.keySet()) {
            if (similarities.get(m) >= min && similarities.get(m) <= max) {
                System.out.println(similarities.get(m));
                sim.put(m, similarities.get(m));
            }
        }
        return sim;
    }

    public int getTermCount(String term) {
        return terms.getCount(term);
    }

    public int getTermCount() {
        return terms.uniqueSet().size();
    }
}
