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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
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
    private List edges;
    
    public GraphMain() throws HeadlessException {
        super("Modulhandbuch Visualisierung");
        test = new TestMain();
        vertices = new HashMap<String, Object>(test.getModules().size());
        edges = new ArrayList(500);
        
        graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        Random random = new Random(System.nanoTime());
        graph.getModel().beginUpdate();
        try {
            
            for (Module m : test.getModules()) {
                vertices.put(m.getName(), graph.insertVertex(parent, null, m.getName(), 100, 100, 350, 30));
            }
            
            for (Module m : test.getModules()) {
                System.out.println("Connecting " + m.getName());
                HashMap<Module, Double> similarities = m.getSimilarities();
                for (Module m2 : similarities.keySet()) {
                    edges.add(graph.insertEdge(parent, null, similarities.get(m2), vertices.get(m.getName()), vertices.get(m2.getName())));
                }
            }
            
        } finally {
            graph.getModel().endUpdate();
        }
        
        
        graph.getModel().beginUpdate();
        try {
//            mxOrganicLayout cl = new mxOrganicLayout(graph);
////            cl.setOptimizeEdgeLength(true);
////            cl.setOptimizeEdgeCrossing(true);
////            cl.setOptimizeEdgeLength(true);
////            cl.setOptimizeNodeDistribution(true);
//            cl.setInitialMoveRadius(500);
////            cl.setMinMoveRadius(50);
//            cl.setFineTuning(true);
            mxFastOrganicLayout cl = new mxFastOrganicLayout(graph);
            cl.setMaxIterations(100000);
            cl.setForceConstant(50);
            cl.setInitialTemp(1000);
            cl.setUseInputOrigin(false);
            cl.execute(parent);
        } finally {
            graph.getModel().endUpdate();
        }
        
        graph.getModel().beginUpdate();
        try {
            for (Object o : edges) {
                graph.getModel().remove(o);
            }
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
