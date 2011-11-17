/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
        String text = TextStripper.extract(f);
        ModuleExtractor miExtractor = new ModuleExtractor(text, ModuleExtractor.TYPE_CS);
        
        f = new File("docs/inf/wi_bachelor_modulhandbuch.pdf");
        text = TextStripper.extract(f);
        ModuleExtractor wiExtractor = new ModuleExtractor(text, ModuleExtractor.TYPE_CS);
        
        
        Vocabulary vocab = new Vocabulary();
        List<Module> modules = new ArrayList<Module>();
        modules.addAll(miExtractor.getModules());
        modules.addAll(wiExtractor.getModules());
        
        for (Module m : modules) {
            vocab.addTerms(m);
        }
        
        
        vocab.print();
    }
}
