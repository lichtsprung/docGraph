/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.openplexus;

import java.io.File;

/**
 *
 * @author Robert Giacinto
 */
public class TestMain {
    
    public static void main(String[] args) {
        File f = new File("docs/inf/wi_bachelor_modulhandbuch.pdf");
        String text = TextStripper.extract(f);
        ModuleExtractor miExtractor = new ModuleExtractor(text, ModuleExtractor.TYPE_CS);
        Vocabulary vocab = new Vocabulary();
        
        for (Module m : miExtractor.getModules()) {
            vocab.findCollocations(m);
        }
        
    }
}
