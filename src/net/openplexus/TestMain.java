package net.openplexus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Robert Giacinto
 */
public class TestMain {

    private List<Module> modules;

    public TestMain() {
        try {
            File f = new File("docs/inf/ti_bachelor_modulhandbuch.pdf");
            System.out.println("File: " + f.getName() + " loaded");
            System.out.println("Extracting text...");
            String text = TextStripper.extract(f);
            System.out.println("Analysing text...");
            ModuleExtractor tiExtractor = new ModuleExtractor(text, ModuleExtractor.TYPE_CS, "TI");

            f = new File("docs/inf/wi_bachelor_modulhandbuch.pdf");
            System.out.println("File: " + f.getName() + " loaded");
            System.out.println("Extracting text...");
            text = TextStripper.extract(f);
            System.out.println("Analysing text...");
            ModuleExtractor wiExtractor = new ModuleExtractor(text, ModuleExtractor.TYPE_CS, "WI");

            f = new File("docs/inf/ai_bachelor_modulhandbuch.pdf");
            System.out.println("File: " + f.getName() + " loaded");
            System.out.println("Extracting text...");
            text = TextStripper.extract(f);
            System.out.println("Analysing text...");
            ModuleExtractor aiExtractor = new ModuleExtractor(text, ModuleExtractor.TYPE_CS, "AI");
    //        
            f = new File("docs/inf/mi_bachelor_modulhandbuch.pdf");
            System.out.println("File: " + f.getName() + " loaded");
            System.out.println("Extracting text...");
            text = TextStripper.extract(f);
            System.out.println("Analysing text...");
            ModuleExtractor miExtractor = new ModuleExtractor(text, ModuleExtractor.TYPE_CS, "MI");

            System.out.println("Building feature vector...");
            Vocabulary vocab = new Vocabulary();
            modules = new ArrayList<Module>(50);
            modules.addAll(tiExtractor.getModules());
            modules.addAll(wiExtractor.getModules());
            modules.addAll(aiExtractor.getModules());
            modules.addAll(miExtractor.getModules());

            for (Module m : modules) {
                System.out.println("Adding terms of class: " + m.name);
                vocab.addTerms(m);
            }

            for (Module m : modules) {
    //            System.out.println("Setting feature vector for class: " + m.name);
                m.featureVector = vocab.getFeatureVector(m);
            }

            System.out.println("Calculating similarities...");
            File similarities = new File("similarities.csv");
            BufferedWriter wr = new BufferedWriter(new FileWriter(similarities));

            for (Module m1 : modules) {
                for (Module m2 : modules) {
                    double sim = CosineSimilarity.calculate(m1, m2);
                    m1.similarities.put(m2, sim);
                    wr.write(m1.getName() + ";" + sim + ";" + m2.getName());
                    wr.newLine();
                    if (m1.similarities.get(m2) > 0.15 && m1.similarities.get(m2) < 0.9) {
                        System.out.println("Similarity between " + m1.name + " and " + m2.name + ": " + m1.similarities.get(m2));
                    }
                }
            }
            wr.flush();
            wr.close();
        } catch (IOException ex) {
            Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }

    public static void main(String[] args) {
       new TestMain();
    }
    private static final Logger LOG = Logger.getLogger(TestMain.class.getName());
}
