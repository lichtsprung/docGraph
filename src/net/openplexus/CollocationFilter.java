package net.openplexus;

import java.util.Set;
import org.apache.commons.collections.bag.HashBag;

/**
 * Dieses Filter extrahiert aus einem Text alle Kollokationen.
 * 
 * @author Robert Giacinto
 */
public class CollocationFilter {

    private String[] tokens;
    private int maxLevel;
    private HashBag collocations;
    private Set<String> collocationSet;

    public CollocationFilter(String[] tokens, int maxLevel) {
        this.tokens = tokens;
        this.maxLevel = maxLevel;
        collocations = new HashBag();
        fillBag();

        System.out.println(collocationSet.size());

    }

    private void fillBag() {
        for (int i = 1; i < maxLevel + 1; i++) {
            for (int k = 0; k < tokens.length - i; k++) {
                StringBuilder sb = new StringBuilder();
                for (int m = k; m <= k + i; m++) {
                    sb.append(tokens[m]).append(" ");
                }
                collocations.add(sb.toString());
            }
        }

        HashBag bag = new HashBag();
        for (Object o : collocations) {
            if (collocations.getCount(o) > 1) {
                bag.add(o, collocations.getCount(o));
            }
        }

        collocationSet = bag.uniqueSet();
        collocations = bag;
    }

    public Set<String> getUniqueCollocations() {
        return collocationSet;
    }

    public HashBag getCollocations() {
        return collocations;
    }

    public int getCollocationCount(String collocation) {
        return collocations.getCount(collocation);
    }
}
