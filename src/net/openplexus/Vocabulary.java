
package net.openplexus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Diese Klasse verwaltet das gemeinsame Vokabular aller geladener Dokumente.
 * Es gibt die Dimensionen und Features des semantischen Raums an, das durch die
 * einzelnen Begriffe aufgespannt wird.
 * 
 * @author Robert Giacinto
 */
public class Vocabulary {
    private Set<String> vocabulary;

    public Vocabulary() {
        vocabulary = new HashSet<String>();
    }
    
    public void addTerm(String term){
        vocabulary.add(term);
    }
    
    public void addTermCollection(Collection<String> collection){
        vocabulary.addAll(collection);
    }
    
    public int getSize(){
        return vocabulary.size();
    }
    
    
}
