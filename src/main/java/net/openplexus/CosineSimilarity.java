package net.openplexus;

import java.util.List;
import java.util.logging.Logger;

/**
 * Bestimmt die Kosinusähnlichkeit zwischen zwei Modulen.
 *
 * @author Robert Giacinto
 */
public class CosineSimilarity {

    public static double calculate(Module m1, Module m2) {
        List<TVComponent> tv1 = m1.featureVector;
        List<TVComponent> tv2 = m2.featureVector;
        double length1 = 0;
        for (TVComponent c : tv1) {
            length1 += c.getWeightedComponent() * c.getWeightedComponent();
        }

        double length2 = 0;
        for (TVComponent c : tv2) {
            length2 += c.getWeightedComponent() * c.getWeightedComponent();
        }

        length1 = Math.sqrt(length1);
        length2 = Math.sqrt(length2);

        assert tv1.size() == tv2.size();
        double dot = 0;
        for (int i = 0; i < tv1.size(); i++) {
            dot += tv1.get(i).getWeightedComponent() * tv2.get(i).getWeightedComponent();
        }


        return dot / (length1 * length2);
    }
    private static final Logger LOG = Logger.getLogger(CosineSimilarity.class.getName());

    private CosineSimilarity() {
    }
}
