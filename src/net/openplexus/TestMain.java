package net.openplexus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Robert Giacinto
 */
public class TestMain {
    
    public static void main(String[] args) {
        File f = new File("docs/inf/ti_bachelor_modulhandbuch.pdf");
//        System.out.println("File: " + f.getName() + " loaded");
//        System.out.println("Extracting text...");
//        String text = TextStripper.extract(f);
//        System.out.println("Analysing text...");
//        ModuleExtractor tiExtractor = new ModuleExtractor(text, ModuleExtractor.TYPE_CS);
//        
//        f = new File("docs/inf/wi_bachelor_modulhandbuch.pdf");
//        System.out.println("File: " + f.getName() + " loaded");
//        System.out.println("Extracting text...");
//        text = TextStripper.extract(f);
//        System.out.println("Analysing text...");
//        ModuleExtractor wiExtractor = new ModuleExtractor(text, ModuleExtractor.TYPE_CS);
//        
        f = new File("docs/inf/ai_bachelor_modulhandbuch.pdf");
        System.out.println("File: " + f.getName() + " loaded");
        System.out.println("Extracting text...");
        String text = TextStripper.extract(f);
        System.out.println("Analysing text...");
        ModuleExtractor aiExtractor = new ModuleExtractor(text, ModuleExtractor.TYPE_CS);
        
        System.out.println("Building feature vector...");
        Vocabulary vocab = new Vocabulary();
        List<Module> modules = new ArrayList<Module>();
//        modules.addAll(tiExtractor.getModules());
//        modules.addAll(wiExtractor.getModules());
        modules.addAll(aiExtractor.getModules());
        
        for (Module m : modules) {
            vocab.addTerms(m);
        }
        
    }
}
