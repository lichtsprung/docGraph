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

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.HeadlessException;
import java.util.HashMap;
import javax.swing.JFrame;
import net.openplexus.Module;
import net.openplexus.TestMain;

/**
 * Die Visualisierung der Analyse der Modulhandbücher.
 *
 * @author Robert Giacinto
 */
public class GraphMain extends JFrame {
    
    private mxGraph graph;
    private TestMain test;
    private HashMap<String, Object> vertices;
    
    public GraphMain() throws HeadlessException {
        super("Modulhandbuch Visualisierung");
        test = new TestMain();
        vertices = new HashMap<String, Object>(test.getModules().size());
        
        graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        
        graph.getModel().beginUpdate();
        // TODO Erzeugen aller Vertizes (die Terme aus dem Modulhandbuch)
        // TODO Verbinden der Module mit den Termen
        try {
            
            for (Module m : test.getModules()) {
                vertices.put(m.getName(), graph.insertVertex(parent, null, m.getName(), 0, 0, 250, 20));
            }
            
            for (Module m : test.getModules()) {
                System.out.println("Connecting " + m.getName());
                HashMap<Module, Double> similarities = m.getSimilarities(0.05, 0.96);
                for (Module m2 : similarities.keySet()) {
                    graph.insertEdge(parent, null, similarities.get(m2), vertices.get(m.getName()), vertices.get(m2.getName()));
                }
            }
            
        } finally {
            graph.getModel().endUpdate();
        }
        
        
        graph.getModel().beginUpdate();
        try {
            mxOrganicLayout cl = new mxOrganicLayout(graph);
            cl.setOptimizeEdgeDistance(true);
            cl.setAverageNodeArea(300000);
            cl.setOptimizeNodeDistribution(true);
            cl.setFineTuning(true);
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
