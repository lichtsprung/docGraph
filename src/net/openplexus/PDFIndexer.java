package net.openplexus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 *
 * @author Robert Giacinto
 */
public class PDFIndexer {

    private static final String DATA = "/home/robert/test/data";
    private static final String INDEX = "/home/robert/test/index";
    private IndexWriter writer;
    private Set<String> stopWords;

    public PDFIndexer() throws IOException {
        stopWords = new LinkedHashSet<String>();
        fillSet(stopWords);
        IndexWriterConfig indexConfig = new IndexWriterConfig(Version.LUCENE_33, new StandardAnalyzer(Version.LUCENE_33, new File("stopwords.txt")));
        Directory directory = FSDirectory.open(new File(INDEX));
        writer = new IndexWriter(directory, indexConfig);
    }

    public void close() throws CorruptIndexException, IOException {
        writer.close();
    }

    public void index() throws FileNotFoundException, IOException {
        File[] files = new File(DATA).listFiles();
        for (File f : files) {
            if (!f.isDirectory()
                    && f.canRead()
                    && !f.isHidden()
                    && f.exists()
                    && f.getName().endsWith(".pdf")) {
                indexFile(f);
            }
        }
    }

    public void indexFile(File f) throws FileNotFoundException, IOException {
        PDFParser parser = new PDFParser(new FileInputStream(f));
        parser.parse();
        COSDocument document = parser.getDocument();
        PDFTextStripper textStripper = new PDFTextStripper();
        String text = textStripper.getText(new PDDocument(document));
        text = text.replaceAll("(_)*", "");
        text = text.replaceAll("Modulhandbuch [A-Za-z ,0-9.]*", "");
        text = text.replaceAll(":", " ");
        String[] module = text.split("Modulbezeichnung");
        int i = 0;
        for (String s : module) {
            if (i > 0) {
                String modulbezeichnung = s.substring(0, s.indexOf("\n"));
                Document doc = new Document();
                doc.add(new Field("course", modulbezeichnung, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
                doc.add(new Field("content", s, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
                doc.add(new Field("filename", f.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                doc.add(new Field("fullpath", f.getCanonicalPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                writer.addDocument(doc);
            }
            i++;
        }
        document.close();
    }

    public void printTermVectors() throws CorruptIndexException, IOException {
        IndexReader reader = IndexReader.open(writer, true);
        for (int i = 0; i < reader.numDocs(); i++) {
            Document doc = reader.document(i);
            System.out.println("================== " + doc.get("course") + " =====================");
            TermFreqVector termFreqVector = reader.getTermFreqVector(i, "content");
            List<Tuple> termVec = new ArrayList<Tuple>(termFreqVector.size());

            for (int j = 0; j < termFreqVector.size(); j++) {
                termVec.add(new Tuple(termFreqVector.getTerms()[j], termFreqVector.getTermFrequencies()[j]));
            }

            Collections.sort(termVec);

            for (Tuple t : termVec) {
                System.out.println(t);
            }
        }
        reader.close();

    }

    public Collection<Tuple> getTerms(String course) throws CorruptIndexException, IOException {
        IndexReader reader = IndexReader.open(writer, true);
        List<Tuple> termVec = null;

        for (int i = 0; i < reader.numDocs(); i++) {
            Document doc = reader.document(i);
            if (doc.get("course").equalsIgnoreCase(course)) {
                TermFreqVector termFreqVector = reader.getTermFreqVector(i, "content");
                termVec = new ArrayList<Tuple>(termFreqVector.size());

                for (int j = 0; j < termFreqVector.size(); j++) {
                    termVec.add(new Tuple(termFreqVector.getTerms()[j], termFreqVector.getTermFrequencies()[j]));
                }

                Collections.sort(termVec);
            }

        }
        reader.close();

        return termVec;
    }

    public void printDistinctCourses() throws CorruptIndexException, IOException {
        IndexReader reader = IndexReader.open(writer, true);
        HashSet<String> course = new HashSet<String>(reader.numDocs());

        for (int i = 0; i < reader.numDocs(); i++) {
            Document doc = reader.document(i);
            course.add(doc.get("course"));
        }

        System.out.println("Anzahl unterschiedlicher Kurse: " + course.size());
        String[] courses = course.toArray(new String[0]);
        Arrays.sort(courses);
        for (String s : courses) {
            System.out.println(s);
        }
        reader.close();
    }

    public Collection<String> getCourses() throws CorruptIndexException, IOException {
        IndexReader reader = IndexReader.open(writer, true);
        HashSet<String> courses = new HashSet<String>(reader.numDocs());

        for (int i = 0; i < reader.numDocs(); i++) {
            Document doc = reader.document(i);
            courses.add(doc.get("course"));
        }
        reader.close();
        return courses;
    }

    public Collection<ModuleDocument> getModuleDocuments() throws CorruptIndexException, IOException {
        List<ModuleDocument> modules = new ArrayList<ModuleDocument>();
        Collection<String> courses = getCourses();
        for (String course : courses) {
            Collection<Tuple> terms = getTerms(course);
            modules.add(new ModuleDocument(course, terms));
        }
        return modules;
    }

    public static void main(String[] args) throws IOException {
        PDFIndexer pDFIndexer = new PDFIndexer();
        pDFIndexer.index();
        pDFIndexer.printTermVectors();
        pDFIndexer.printDistinctCourses();
        Collection<ModuleDocument> modules = pDFIndexer.getModuleDocuments();
        for (ModuleDocument m : modules) {
            System.out.println("========== " + m.name + " ==========");
            for (Tuple t : m.terms) {
                System.out.println(t);
            }
            System.out.println("====================================");
        }
        pDFIndexer.close();
    }

    private void fillSet(Set<String> stopWords) {
        stopWords.add("der");
        stopWords.add("die");
        stopWords.add("das");
        stopWords.add("am");
        stopWords.add("im");
        stopWords.add("des");
        stopWords.add("was");
        stopWords.add("wer");
        stopWords.add("wo");
        stopWords.add("sie");
        stopWords.add("er");
        stopWords.add("es");
        stopWords.add("wir");
        stopWords.add("ihr");
        stopWords.add("sie");
        stopWords.add("dozent");
        stopWords.add("fh");
        stopWords.add("fachhochschule");
        stopWords.add("köln");
        stopWords.add("zum");
        stopWords.add("zur");
        stopWords.add("über");
        stopWords.add("ti");
        stopWords.add("und");
        stopWords.add("von");
        stopWords.add("0");
        stopWords.add("1");
        stopWords.add("2");
        stopWords.add("3");
        stopWords.add("4");
        stopWords.add("5");
        stopWords.add("6");
        stopWords.add("7");
        stopWords.add("8");
        stopWords.add("9");
        stopWords.add("ihrer");
        stopWords.add("ihren");
        stopWords.add("literatur");
        stopWords.add("dann");
        stopWords.add("dies");
        stopWords.add("deutsch");
        stopWords.add("dem");
        stopWords.add("aus");
        stopWords.add("auch");
        stopWords.add("als");
        stopWords.add("ggf");
        stopWords.add("zu");
        stopWords.add("dass");
        stopWords.add("den");
        stopWords.add("durch");
        stopWords.add("davon");
        stopWords.add("kein");
        stopWords.add("keine");
        stopWords.add("keiner");
        stopWords.add("in");
        stopWords.add("für");
        stopWords.add("sich");
        stopWords.add("ein");
        stopWords.add("einer");
        stopWords.add("eine");
        stopWords.add("auf");
        stopWords.add("ects");
        stopWords.add("kürzel");
        stopWords.add("modern");
        stopWords.add("soll");
        stopWords.add("sollen");
        stopWords.add("zuvor");
        stopWords.add("prof");
        stopWords.add("professor");
        stopWords.add("sowie");
        stopWords.add("an");
        stopWords.add("erfolreich");
        stopWords.add("erstellen");
        stopWords.add("hinausgehen");
        stopWords.add("klausur");
        stopWords.add("kürzel");
        stopWords.add("lernziele");
        stopWords.add("lernen");
        stopWords.add("statt");
        stopWords.add("unter");
        stopWords.add("verstehen");
        stopWords.add("verstanden");
        stopWords.add("wird");
        stopWords.add("werden");
        stopWords.add("anhand");
        stopWords.add("befasst");
        stopWords.add("befassen");
        stopWords.add("information");
        stopWords.add("informations");
        stopWords.add("nach");
        stopWords.add("anderen");
        stopWords.add("anders");
        stopWords.add("auswahl");
        stopWords.add("bei");
        stopWords.add("bestimmen");
        stopWords.add("erproben");
        stopWords.add("form");
        stopWords.add("folie");
        stopWords.add("je");
        stopWords.add("kürzel");
        stopWords.add("ggf");
        stopWords.add("kürzel:ggf");
        stopWords.add("mündlich");
        stopWords.add("mündliche");
        stopWords.add("insbesondere");
        stopWords.add("erfolgreich");
        stopWords.add("erläutert");
        stopWords.add("erläutern");
        stopWords.add("arbeit");
        stopWords.add("alle");
        stopWords.add("campus");
        stopWords.add("abgeschlossen");
        stopWords.add("abgeschlossene");
        stopWords.add("teil");
        stopWords.add("sowohl");
        stopWords.add("semester");
        stopWords.add("innerhalb");
        stopWords.add("inhalt");
        stopWords.add("kann");
        stopWords.add("fällen");
        stopWords.add("geeignet");
        stopWords.add("geeigneten");
        stopWords.add("geeignete");
        stopWords.add("geeigneter");
        stopWords.add("erfolgreicher");
        stopWords.add("einzelheiten");
        stopWords.add("aufgabe");
        stopWords.add("schriftliche");
        stopWords.add("schriftlich");
        stopWords.add("oder");
        stopWords.add("hauptstudium");
        stopWords.add("grundstudium");
        stopWords.add("gehend");
        stopWords.add("einschließlich");
        stopWords.add("entweder");
        stopWords.add("erlernt");
        stopWords.add("ersten");
        stopWords.add("erwartet");
        stopWords.add("bis");
        stopWords.add("alle");
        stopWords.add("einem");
        stopWords.add("wartet");
        stopWords.add("wie");
        stopWords.add("warum");
    }

    public static class Tuple implements Comparable<Tuple> {

        public String name;
        public int count;

        public Tuple(String name, int count) {
            this.name = name;
            this.count = count;
        }

        @Override
        public int compareTo(Tuple that) {
            if (count > that.count) {
                return -1;
            } else if (count == that.count) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public String toString() {
            return name + " - " + count;
        }
    }

    public static class ModuleDocument {

        public String name;
        public Collection<Tuple> terms;

        public ModuleDocument(String name, Collection<Tuple> terms) {
            this.name = name;
            this.terms = terms;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
