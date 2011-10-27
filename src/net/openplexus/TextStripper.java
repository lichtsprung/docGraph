package net.openplexus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 * Diese Klasse stellt eine statische Methode zur Verfügung, die den Text aus einer PDF-Datei extrahiert.
 * 
 * @author Robert Giacinto
 */
public class TextStripper {

    public static String extract(File file) {
        if (file.exists() && !file.isDirectory() && file.canRead() && file.getName().endsWith("pdf")) {
            try {
                PDFParser parser = new PDFParser(new FileInputStream(file));
                parser.parse();

                COSDocument document = parser.getDocument();
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(new PDDocument(document));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TextStripper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TextStripper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
    }
}
