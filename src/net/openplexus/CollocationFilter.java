package net.openplexus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.bag.HashBag;

/**
 * Dieses Filter extrahiert aus einem Text alle Kollokationen.
 * 
 * @author Robert Giacinto
 */
public class CollocationFilter {

    private String[] tokens;
    private double threshold;
    private HashBag collocations;
    private HashMap<Tuple, Double> likelyhoods;
    private HashSet<String> terms;
    private HashSet<String> stopWords;

    public CollocationFilter(String[] tokens, double threshold) {
        this.tokens = tokens;
        this.threshold = threshold;
        collocations = new HashBag();
        likelyhoods = new HashMap<Tuple, Double>();
        terms = new HashSet<String>();
        stopWords = new HashSet<String>();
        loadStopwords();
        collectTuples();
        calcSig();

    }

    private void collectTuples() {
        for (String term : tokens) {
            if (!stopWords.contains(term)) {
                terms.add(term);
            }
        }

        for (int i = 0; i < tokens.length - 1; i++) {
            if (tokens[i].length() > 1
                    && tokens[i + 1].length() > 1
                    && !stopWords.contains(tokens[i])
                    && !stopWords.contains(tokens[i + 1])) {
                Tuple2 t = new Tuple2(tokens[i], tokens[i + 1]);
                collocations.add(t);
            }
        }

        for (int i = 0; i < tokens.length - 2; i++) {
            if (tokens[i].length() > 1
                    && tokens[i + 1].length() > 1
                    && tokens[i + 2].length() > 1
                    && !stopWords.contains(tokens[i])
                    && !stopWords.contains(tokens[i + 1])
                    && !stopWords.contains(tokens[i + 2])) {
                Tuple3 t = new Tuple3(tokens[i], tokens[i + 1], tokens[i + 1]);
                collocations.add(t);
            }
        }
    }

    /**
     * Berechnet Signifikanz einer Kollokation mit log-likelyhood = sig(A,B) = -log(1-exp(-x)*sum(1/i!*x^1))/log n.
     */
    private double calcSig(String a, String b) {
        double countA = 0;
        double countB = 0;
        double countAB = collocations.getCount(new Tuple2(a, b));
        double countN = collocations.size();

        for (String tmp : terms) {
            countA += collocations.getCount(new Tuple2(a, tmp));
        }

        for (String tmp : terms) {
            countB += collocations.getCount(new Tuple2(tmp, b));
        }

        double x = (countA * countB) / countN;
        double sum = 0;

        for (int i = 0; i < countAB; i++) {
            sum += (1 / factorial(i)) * Math.pow(x, i);
        }

        double num = 1 - Math.pow(Math.E, -x) * sum;

        return -Math.log(num) / Math.log(countN);
    }

    private double calcSig(String a, String b, String c) {
        double countA = 0;
        double countB = 0;
        double countC = 0;
        double countABC = collocations.getCount(new Tuple3(a, b, c));
        double countN = collocations.size();

        for (String tmpB : terms) {
            for (String tmpC : terms) {
                countA += collocations.getCount(new Tuple3(a, tmpB, tmpC));
            }
        }

        for (String tmpA : terms) {
            for (String tmpC : terms) {
                countB += collocations.getCount(new Tuple3(tmpA, b, tmpC));
            }
        }

        for (String tmpA : terms) {
            for (String tmpB : terms) {
                countC += collocations.getCount(new Tuple3(tmpA, tmpB, c));
            }
        }

        double x = (countA * countB * countC) / countN;
        double sum = 0;

        for (int i = 0; i < countABC; i++) {
            sum += (1 / factorial(i)) * Math.pow(x, i);
        }

        double num = 1 - Math.pow(Math.E, -x) * sum;

        return -Math.log(num) / Math.log(countN);
    }

    private double factorial(int n) {
        if (n < 2) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }

    private void calcSig() {
        System.out.println("Term Count: " + terms.size());
        System.out.println("Finding bigrams");
        for (String a : terms) {
            for (String b : terms) {
                Tuple2 t = new Tuple2(a, b);
                double sig = calcSig(a, b);
                if (sig > threshold) {
                    likelyhoods.put(t, sig);
                }
            }
        }

        System.out.println("Finding trigrams");
        for (String a : terms) {
            for (String b : terms) {
                for (String c : terms) {

                    double sig = calcSig(a, b, c);
                    if (sig > threshold) {
                        Tuple3 t = new Tuple3(a, b, c);
                        likelyhoods.put(t, sig);
                    }
                }
            }
        }
    }

    private void loadStopwords() {
        File file = new File("docs/stopwords.txt");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String words = reader.readLine();
            String[] sw = words.split(",");
            stopWords.addAll(Arrays.asList(sw));
        } catch (IOException ex) {
            Logger.getLogger(CollocationFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String collocationsToString() {
        StringBuilder sb = new StringBuilder();
        for (Entry<Tuple, Double> e : likelyhoods.entrySet()) {
            sb.append(e.getKey()).append(" : ").append(e.getValue()).append("; ");
        }
        return sb.toString();
    }

    private interface Tuple {
    }

    private class Tuple2 implements Tuple {

        private String termA;
        private String termB;

        public Tuple2(String termA, String termB) {
            this.termA = termA;
            this.termB = termB;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Tuple2 other = (Tuple2) obj;
            if ((this.termA == null) ? (other.termA != null) : !this.termA.equals(other.termA)) {
                return false;
            }
            if ((this.termB == null) ? (other.termB != null) : !this.termB.equals(other.termB)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 31 * hash + (this.termA != null ? this.termA.hashCode() : 0);
            hash = 31 * hash + (this.termB != null ? this.termB.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            return "Tuple2{" + "termA=" + termA + ", termB=" + termB + '}';
        }
    }

    private class Tuple3 implements Tuple {

        private String termA, termB, termC;

        public Tuple3(String termA, String termB, String termC) {
            this.termA = termA;
            this.termB = termB;
            this.termC = termC;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Tuple3 other = (Tuple3) obj;
            if ((this.termA == null) ? (other.termA != null) : !this.termA.equals(other.termA)) {
                return false;
            }
            if ((this.termB == null) ? (other.termB != null) : !this.termB.equals(other.termB)) {
                return false;
            }
            if ((this.termC == null) ? (other.termC != null) : !this.termC.equals(other.termC)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 71 * hash + (this.termA != null ? this.termA.hashCode() : 0);
            hash = 71 * hash + (this.termB != null ? this.termB.hashCode() : 0);
            hash = 71 * hash + (this.termC != null ? this.termC.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            return "Tuple3{" + "termA=" + termA + ", termB=" + termB + ", termC=" + termC + '}';
        }
    }
}
