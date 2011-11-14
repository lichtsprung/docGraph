package net.openplexus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Diese Klasse verwaltet das gemeinsame Vokabular aller geladener Dokumente. Es
 * gibt die Dimensionen und Features des semantischen Raums an, das durch die
 * einzelnen Begriffe aufgespannt wird.
 *
 * @author Robert Giacinto
 */
public class Vocabulary {

    private Set<String> vocabulary;

    /**
     * Das gemeinsame Vokabular aller Dokumente.
     */
    public Vocabulary() {
        vocabulary = new HashSet<String>();
    }

    /**
     * Fügt dem Vokabular einen Term hinzu.
     *
     * @param term der hinzuzufügende Term
     */
    public void addTerm(String term) {
        vocabulary.add(term);
    }

    /**
     * Fügt dem Vokabular eine Sammlung von Termen hinzu.
     *
     * @param collection die Sammlung von Termen
     */
    public void addTermCollection(Collection<String> collection) {
        vocabulary.addAll(collection);
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
        TermFilter filter = new TermFilter(m, 0.90);
        vocabulary.addAll(filter.getTerms());

        // TODO Hinzufügen der restlichen Terme zum Vokabular bis auf die Terme, die durch die Kollokationen schon abgedeckt wurden.
    }

    public void print() {
        for (String s : vocabulary) {
            System.out.println(s);
        }
    }
}
