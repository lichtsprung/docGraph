package net.openplexus;

import java.util.*;
import org.apache.commons.collections.bag.HashBag;

/**
 * Diese Klasse verwaltet das gemeinsame Vokabular aller geladener Dokumente. Es
 * gibt die Dimensionen und Features des semantischen Raums an, das durch die
 * einzelnen Begriffe aufgespannt wird.
 *
 * @author Robert Giacinto
 */
public class Vocabulary {
    
    private Set<String> vocabulary;
    private List<String> sortedVocabulary;

    /**
     * Das gemeinsame Vokabular aller Dokumente.
     */
    public Vocabulary() {
        vocabulary = new HashSet<String>();
        sortedVocabulary = new ArrayList<String>();
    }

    /**
     * Fügt dem Vokabular einen Term hinzu.
     *
     * @param term der hinzuzufügende Term
     */
    public void addTerm(String term) {
        vocabulary.add(term);
        updateSortedVocabulary();
    }

    /**
     * Fügt dem Vokabular eine Sammlung von Termen hinzu.
     *
     * @param collection die Sammlung von Termen
     */
    public void addTermCollection(Collection<String> collection) {
        vocabulary.addAll(collection);
        updateSortedVocabulary();
    }

    /**
     * Gibt die Anzahl von Termen im Vokabular zurück.
     *
     * @return die Anzahl von Termen
     */
    public int getSize() {
        return vocabulary.size();
    }

    /**
     * Sucht in einem Dokument nach Kollokationen und Termen und fügt diese dem
     * Vokabular hinzu.
     *
     * @param m das zu untersuchende Dokument
     */
    public void addTerms(Module m) {
        System.out.println("Finding collocations for class: " + m.name);

        // TODO Finden der Kollokationen und Einfügen in das globale Vokabular aka Feature-Vektor.
        TermFilter filter = new TermFilter(m, 0.50);
        vocabulary.addAll(filter.getTerms());
        updateSortedVocabulary();
        
    }
    
    private void updateSortedVocabulary() {
        sortedVocabulary.clear();
        sortedVocabulary.addAll(vocabulary);
        Collections.sort(sortedVocabulary);
    }
    
    public void print() {
        for (String s : sortedVocabulary) {
            System.out.println(s);
        }
    }
    
    public List<TVComponent> expandTermVector(HashBag terms) {
        List<TVComponent> tv = new ArrayList<TVComponent>(sortedVocabulary.size());
        
        for (String s : sortedVocabulary) {
            if (terms.contains(s)) {
                tv.add(new TVComponent(s, terms.getCount(s)));
            } else {
                tv.add(new TVComponent(s, 0));
            }
        }
        
        return tv;
    }
}
