package net.openplexus;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse nimmt einen String entgegen und extrahiert die beschriebenen
 * Module.
 *
 * @author Robert Giacinto
 */
public class ModuleExtractor {

    /**
     * Konstante für alle Informatik-Studiengänge.
     */
    public static final int TYPE_CS = 0;
    /**
     * Konstante für alle Ingenieur-Studiengänge.
     */
    public static final int TYPE_ENG = 1;
    /**
     * Liste aller identifzierten Module.
     */
    private List<Module> modules;
    /**
     * Der gesamte Text aus der PDF.
     */
    private String text;
    /**
     * Der Typ des Modulhandbuchs.
     */
    private int type;
    /**
     * Das gemeinsame Vokabular der Module.
     */
    private Vocabulary vocabulary;

    public ModuleExtractor(String text, int type) {
        this.text = text;
        this.type = type;
        modules = new ArrayList<Module>();
        vocabulary = new Vocabulary();
        strip();
    }

    /**
     * Entfernt unnötige Teile des Handbuchs.
     */
    private void strip() {
        if (type == TYPE_CS) {
            String tmp[] = text.split("Modulbezeichnung: ");
            String separator = null;

            if (tmp[0].contains("Medieninformatik")) {
                separator = " ggf.";
            } else {
                separator = "\n";
            }

            for (int i = 1; i < tmp.length; i++) {
                String name = tmp[i].substring(0, tmp[i].indexOf(separator));
                String description = tmp[i].substring(tmp[i].indexOf(separator), tmp[i].length());

                description = description.replaceAll("-\n", "");
                description = description.replaceAll("\n", " ");
                description = description.replaceAll("•", "");
                description = description.replaceAll("_", "");
                description = description.replaceAll("([0-9]*)", "");
                description = description.replaceAll("[A-Z]. ", "");
                description = description.replaceAll("\"", "");
                description = description.replaceAll(",", "");
                description = description.replaceAll("\\.", "");
                description = description.replaceAll(";", "");
                description = description.replaceAll(":", "");

                name = name.replaceAll(":", "");
                String tokens[] = description.toLowerCase().split(" ");
                for (int k = 0; k < tokens.length; k++) {
                    tokens[k].trim();
                }

                modules.add(new Module(name, description, tokens));
            }
        }
    }

    public List<Module> getModules() {
        return modules;
    }
}
