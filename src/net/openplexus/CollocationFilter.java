package net.openplexus;

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
    
    public CollocationFilter(String[] tokens, int maxLevel) {
        this.tokens = tokens;
        this.maxLevel = maxLevel;
        collocations = new HashBag();
        fillBag();
        
        for (Object c : collocations) {
            System.out.println(c);
        }
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
            if (collocations.getCount(o) > 2) {
                bag.add(o, collocations.getCount(o));
            }
        }
        
        collocations = bag;
        
    }
}
