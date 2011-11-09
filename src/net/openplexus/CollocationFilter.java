package net.openplexus;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.bag.HashBag;

/**
 * Dieses Filter extrahiert aus einem Text alle Kollokationen.
 *
 * @author Robert Giacinto
 */
public class CollocationFilter {

    /**
     * Der komplette Text in einzelne Tokens aufgeteilt.
     */
    private String[] tokens;
    /**
     * Prozent von Kollokationen, die berücksichtigt werden sollen.
     */
    private double thresholdP;
    /**
     * Der numerische Grenzwert für die Kollokationsauswahl.
     */
    private double threshold;
    /**
     * Alle Bigramme des Texts.
     */
    private HashBag collocations2;
    /**
     * Alle Trigramme des Texts.
     */
    private HashBag collocations3;
    /**
     * Die Sammlung von Kollokationen, die im weiteren Verlauf berücksichtigt werden.
     */
    private HashBag collocations;
    /**
     * Sammlung der Kollokationen und der berechneten Signifikanzen.
     */
    private HashMap<Tuple, Double> likelihoods;
    /**
     * Alle im Dokument identifizierten Terme.
     */
    private HashSet<String> terms;
    /**
     * Die Liste der Stopwords.
     */
    private HashSet<String> stopWords;
    /**
     * Ein Stemmer für die deutsche Sprache.
     */
    private GermanStemmer stemmer;

    /**
     * Analysiert den Text eines Moduls und extrahiert signifikante Kollokationen.
     * @param m das Modul
     * @param thresholdP der Grenzwert, ab dem eine Kollokationen signifikant ist
     */
    public CollocationFilter(Module m, double thresholdP) {
        this.tokens = m.tokens;
        this.thresholdP = thresholdP;
        collocations = new HashBag();
        collocations2 = new HashBag();
        collocations3 = new HashBag();
        likelihoods = new HashMap<Tuple, Double>();
        terms = new HashSet<String>();
        stopWords = new HashSet<String>();
        stemmer = new GermanStemmer();

        // Laden der Stopword-Datei
        loadStopwords();

        // Sammeln der n-gramme
        collectTuples();

        // Berechnen der log-likelihood
        calcSig();

    }

    /**
     * Sammelt alle Bi- und Trigramme aus dem Modultext.
     */
    private void collectTuples() {
        for (String term : tokens) {
            if (!stopWords.contains(term)) {
                terms.add(term);
            }
        }

        for (int i = 0; i < tokens.length - 1; i++) {
            if (tokens[i].length() > 1
                    && tokens[i + 1].length() > 1
                    && !stopWords.contains(stemmer.stem(tokens[i]))
                    && !stopWords.contains(stemmer.stem(tokens[i + 1]))) {
                Tuple2 t = new Tuple2(tokens[i], tokens[i + 1]);
                collocations2.add(t);
            }
        }

        for (int i = 0; i < tokens.length - 2; i++) {
            if (tokens[i].length() > 1
                    && tokens[i + 1].length() > 1
                    && tokens[i + 2].length() > 1
                    && !stopWords.contains(stemmer.stem(tokens[i]))
                    && !stopWords.contains(stemmer.stem(tokens[i + 1]))
                    && !stopWords.contains(stemmer.stem(tokens[i + 2]))) {
                Tuple3 t = new Tuple3(tokens[i], tokens[i + 1], tokens[i + 2]);
                collocations3.add(t);
            }
        }
    }

    /**
     * Berechnet Signifikanz einer Kollokation mit log-likelihood = sig(A,B) =
     * -log(1-exp(-x)*sum(1/i!*x^i))/log n.
     *
     * @param a der Term A
     * @param b der Term B
     * @return der Signifikanzwert dieser Kollokation
     */
    private double calcSig(String a, String b) {
        double countA = 0;
        double countB = 0;
        double countAB = collocations2.getCount(new Tuple2(a, b));
        double countN = collocations2.size();


        // Zählen, wie häufig es ein Bigramm gibt, das mit dem Term A beginnt.

        List<Tuple2> tuplesA = new ArrayList<Tuple2>(collocations2.size());

        for (Object o : collocations2.uniqueSet()) {
            Tuple2 t = (Tuple2) o;
            if (t.termA.equalsIgnoreCase(a)) {
                tuplesA.add(t);
            }
        }
        countA = tuplesA.size();

        // Zählen, wie häufig es ein Bigramm gibt, das mit dem Term B endet.

        List<Tuple2> tuplesB = new ArrayList<Tuple2>(collocations2.size());

        for (Object o : collocations2.uniqueSet()) {
            Tuple2 t = (Tuple2) o;
            if (t.termB.equalsIgnoreCase(b)) {
                tuplesB.add(t);
            }
        }
        countB = tuplesB.size();

        // Ausrechnen der Hilfsvariablen aus bestimmen der log-likelihood des Bigramms (A,B).

        double x = (countA * countB) / countN;
        double sum = 0;

        for (int i = 0; i < countAB; i++) {
            sum += (1 / factorial(i)) * Math.pow(x, i);
        }

        double num = 1 - Math.pow(Math.E, -x) * sum;

        return -Math.log(num) / Math.log(countN);
    }

    /**
     * Berechnet die log-likelihood der Trigramms (A,B,C).
     *
     * @param a der Term A
     * @param b der Term B
     * @param c der Term C
     * @return die log-likelihood der Kollokation für das Modul
     */
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

    /**
     * Berechnet die Fakultät von n.
     *
     * @param n die Zahl, von der die Fakultät berechnet werden soll.
     * @return die Fakultät
     */
    private double factorial(int n) {
        if (n < 2) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }

    /**
     * Berechnet für alle Kollokationen (Bi- und Trigramme) innerhalb eines
     * Moduls die log-likelihood der einzelnen Termkombinationen.
     */
    private void calcSig() {
        System.out.println("Term Count: " + terms.size());
        System.out.println("Finding bigrams");
        for (Object o : collocations2.uniqueSet()) {
            Tuple2 t = (Tuple2) o;
            double sig = calcSig(t.termA, t.termB);
            likelihoods.put(t, sig);
        }

        System.out.println("Finding trigrams");
        for (Object o : collocations3) {
            Tuple3 t = (Tuple3) o;
            double sig = calcSig(t.termA, t.termB, t.termC);
            likelihoods.put(t, sig);
        }

        double minSig = getMinSig(likelihoods);
        double maxSig = getMaxSig(likelihoods);
        double diff = maxSig - minSig;
        threshold = minSig + thresholdP * diff;
        System.out.println("Threshold = " + threshold);

        collocations.addAll(collocations2);
        collocations.addAll(collocations3);

        for (Tuple t : likelihoods.keySet()) {
            if (likelihoods.get(t) < threshold) {
                collocations.remove(t);
            }
        }
        for (Object o : collocations) {
            System.out.println(o);
        }
    }

    /**
     * Lädt die Datei der Stopwords, die nicht berücktsichtigt werden sollen.
     */
    private void loadStopwords() {
        System.out.println("Loading stopwords...");
        File file = new File("docs/stopwords.txt");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String words = reader.readLine();
            String[] sw = words.toLowerCase().split(",");

            for (int i = 0; i < sw.length; i++) {
                sw[i] = stemmer.stem(sw[i]);
            }

            stopWords.addAll(Arrays.asList(sw));
        } catch (IOException ex) {
            Logger.getLogger(CollocationFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gibt die Minimalsignifikanz für eine Sammlung von Kollokationen zurück.
     *
     * @param likelihoods die Signifikanzwerte, die berücksichtigt werden sollen
     * @return das Minimum
     */
    private double getMinSig(HashMap<Tuple, Double> likelihoods) {
        double min = Double.MAX_VALUE;
        for (Tuple t : likelihoods.keySet()) {
            if (likelihoods.get(t) < min) {
                min = likelihoods.get(t);
            }
        }
        return min;
    }

    /**
     * Gibt die Maximalsignifikanz für eine Sammlung von Kollokationen zurück.
     *
     * @param likelihoods die Signifikanzwerte, die berücksichtigt werden sollen
     * @return das Maximum
     */
    private double getMaxSig(HashMap<Tuple, Double> likelihoods) {
        double max = Double.MIN_VALUE;
        for (Tuple t : likelihoods.keySet()) {
            if (likelihoods.get(t) > max) {
                max = likelihoods.get(t);
            }
        }
        return max;
    }
}
