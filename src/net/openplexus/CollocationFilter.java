package net.openplexus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    private HashBag collocations2, collocations3;
    private HashMap<Tuple, Double> likelyhoods;
    private HashSet<String> terms;
    private HashSet<String> stopWords;

    public CollocationFilter(String[] tokens, double threshold) {
        this.tokens = tokens;
        this.threshold = threshold;
        collocations2 = new HashBag();
        collocations3 = new HashBag();
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
                collocations2.add(t);
            }
        }

        for (int i = 0; i < tokens.length - 2; i++) {
            if (tokens[i].length() > 1
                    && tokens[i + 1].length() > 1
                    && tokens[i + 2].length() > 1
                    && !stopWords.contains(tokens[i])
                    && !stopWords.contains(tokens[i + 1])
                    && !stopWords.contains(tokens[i + 2])) {
                Tuple3 t = new Tuple3(tokens[i], tokens[i + 1], tokens[i + 2]);
                collocations3.add(t);
            }
        }
    }

    /**
     * Berechnet Signifikanz einer Kollokation mit log-likelyhood = sig(A,B) = -log(1-exp(-x)*sum(1/i!*x^1))/log n.
     */
    private double calcSig(String a, String b) {
        double countA = 0;
        double countB = 0;
        double countAB = collocations2.getCount(new Tuple2(a, b));
        double countN = collocations2.size();


        List<Tuple2> tuplesA = new ArrayList<Tuple2>(collocations2.size());

        for (Object o : collocations2.uniqueSet()) {
            Tuple2 t = (Tuple2) o;
            if (t.termA.equalsIgnoreCase(a)) {
                tuplesA.add(t);
            }
        }
        countA = tuplesA.size();

        List<Tuple2> tuplesB = new ArrayList<Tuple2>(collocations2.size());

        for (Object o : collocations2.uniqueSet()) {
            Tuple2 t = (Tuple2) o;
            if (t.termB.equalsIgnoreCase(b)) {
                tuplesB.add(t);
            }
        }
        countB = tuplesB.size();

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
        double countABC = collocations3.getCount(new Tuple3(a, b, c));
        double countN = collocations3.size();

        List<Tuple3> tuplesA = new ArrayList<Tuple3>(collocations3.size());

        for (Object o : collocations3.uniqueSet()) {
            Tuple3 t = (Tuple3) o;
            if (t.termA.equalsIgnoreCase(a)) {
                tuplesA.add(t);
            }
        }
        countA = tuplesA.size();

        List<Tuple3> tuplesB = new ArrayList<Tuple3>(collocations3.size());

        for (Object o : collocations3.uniqueSet()) {
            Tuple3 t = (Tuple3) o;
            if (t.termB.equalsIgnoreCase(b)) {
                tuplesB.add(t);
            }
        }
        countB = tuplesB.size();

        List<Tuple3> tuplesC = new ArrayList<Tuple3>(collocations3.size());

        for (Object o : collocations3.uniqueSet()) {
            Tuple3 t = (Tuple3) o;
            if (t.termC.equalsIgnoreCase(c)) {
                tuplesC.add(t);
            }
        }
        countC = tuplesC.size();

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
        for (Object o : collocations2.uniqueSet()) {
            Tuple2 t = (Tuple2) o;
            double sig = calcSig(t.termA, t.termB);
            if (sig > threshold) {
                likelyhoods.put(t, sig);
            }
        }

        System.out.println("Finding trigrams");
        for (Object o : collocations3) {
            Tuple3 t = (Tuple3) o;
            double sig = calcSig(t.termA, t.termB, t.termC);
            if (sig > threshold) {
                likelyhoods.put(t, sig);
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

        boolean isC2();

        boolean isC3();
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

        @Override
        public boolean isC2() {
            return true;
        }

        @Override
        public boolean isC3() {
            return false;
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

        @Override
        public boolean isC2() {
            return false;
        }

        @Override
        public boolean isC3() {
            return true;
        }
    }
}
