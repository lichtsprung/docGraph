package net.openplexus;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse nimmt einen String entgegen und extrahiert die beschriebenen Module.
 * @author Robert Giacinto
 */
public class ModuleExtractor {

    public static final int TYPE_CS = 0;
    public static final int TYPE_ENG = 1;
    private List<Module> modules;
    private String text;
    private int type;
    private Vocabulary vocabulary;

    public ModuleExtractor(String text, int type) {
        this.text = text;
        this.type = type;
        modules = new ArrayList<Module>();
        vocabulary = new Vocabulary();
        strip();
        findCollocations();
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
                description = description.replaceAll("\n", " ");
                description = description.replaceAll("•", "");
                description = description.replaceAll("_", "");
                name = name.replaceAll(":", "");

                modules.add(new Module(name, description));
            }
        }
    }

    private void findCollocations() {
        for (Module m : modules) {
            String tokens[] = m.description.split(" ");
            System.out.println("Finding collocations for class: " + m.name);

            CollocationFilter filter = new CollocationFilter(tokens, 5);
            m.collocations.addAll(filter.getCollocations());
            vocabulary.addTermCollection(filter.getUniqueCollocations());
        }
    }
}
