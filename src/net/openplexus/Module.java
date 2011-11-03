package net.openplexus;

import org.apache.commons.collections.bag.HashBag;

/**
 *
 * @author Robert Giacinto
 */
public class Module {

    protected String name;
    protected String description;
    protected HashBag terms;
    protected Vocabulary vocabulary;
    protected String[] tokens;
    protected String[] stemmedTokens;
    private GermanStemmer stemmer;

    public Module(String name, String description, String[] tokens, Vocabulary vocabulary) {
        this.name = name;
        this.description = description;
        this.vocabulary = vocabulary;
        this.tokens = tokens;
        terms = new HashBag();
        stemmedTokens = new String[tokens.length];
        stemmer = new GermanStemmer();

        for (int i = 0; i < tokens.length; i++) {
            stemmedTokens[i] = stemmer.stem(tokens[i]);
        }
    }
}
