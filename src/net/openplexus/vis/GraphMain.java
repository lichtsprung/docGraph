/*
 * Copyright 2011 Cologne University of Applied Sciences Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.openplexus.vis;

import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.HeadlessException;
import javax.swing.JFrame;

/**
 * Die Visualisierung der Analyse der Modulhandbücher.
 * 
 * @author Robert Giacinto
 */
public class GraphMain extends JFrame {

    private mxGraph graph;

    public GraphMain() throws HeadlessException {
        super("Modulhandbuch Visualisierung");

        graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        // TODO Erzeugen aller Vertizes (die Terme aus dem Modulhandbuch)
        // TODO Verbinden der Module mit den Termen
        try {
            Object v1 = graph.insertVertex(parent, null, "Medieninformatik", 0, 0, 20, 20);
            Object v2 = graph.insertVertex(parent, null, "Algortithmen", 0, 0, 20, 20);
            Object v3 = graph.insertVertex(parent, null, "Wissenschaft", 0, 0, 20, 20);
            Object v4 = graph.insertVertex(parent, null, "Computergrafik", 0, 0, 20, 20);
            Object v5 = graph.insertVertex(parent, null, "Raytracing", 0, 0, 20, 20);
            Object v6 = graph.insertVertex(parent, null, "Filme", 0, 0, 20, 20);
            Object v7 = graph.insertVertex(parent, null, "Flash", 0, 0, 20, 20);
            Object v8 = graph.insertVertex(parent, null, "Usability", 0, 0, 20, 20);
            graph.insertEdge(parent, null, null, v2, v1);
            graph.insertEdge(parent, null, null, v3, v1);
            graph.insertEdge(parent, null, null, v4, v1);
            graph.insertEdge(parent, null, null, v5, v3);
            graph.insertEdge(parent, null, null, v6, v2);
            graph.insertEdge(parent, null, null, v7, v2);
            graph.insertEdge(parent, null, null, v8, v2);

        } finally {
            graph.getModel().endUpdate();
        }


        graph.getModel().beginUpdate();
        try {
            mxOrganicLayout cl = new mxOrganicLayout(graph);
            cl.setFineTuning(true);
            cl.setFineTuningRadius(15);
            cl.setEdgeLengthCostFactor(0.3);
            cl.execute(parent);
        } finally {
            graph.getModel().endUpdate();
        }
        mxGraphComponent component = new mxGraphComponent(graph);

        getContentPane().add(component);
    }

    public static void main(String[] args) {
        GraphMain gm = new GraphMain();
        gm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gm.setSize(800, 600);
        gm.setVisible(true);
    }
}
