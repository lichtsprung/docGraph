package net.openplexus.vis;

import java.util.ArrayList;
import java.util.logging.Logger;
import net.openplexus.Module;
import net.openplexus.TestMain;
import processing.core.PApplet;

/**
 *
 * @author Robert Giacinto
 */
public class GraphForce extends PApplet {

    private static final Logger LOG = Logger.getLogger(GraphForce.class.getName());
    int W = 500;
    int H = 500;
    Graph g = buildSimilarityGraph();
    TestMain test;
    float scaleFactor = 1;
    float offsetV = 0;
    float offsetH = 0;

    @Override
    public void setup() {
        size(W, H, P3D);
        smooth();

    }

    @Override
    public void draw() {
        camera(0f, 0f, 1220.0f, // eyeX, eyeY, eyeZ
                0f, 0f, 0f, // centerX, centerY, centerZ
                0.0f, 1.0f, 0.0f); // upX, upY, upZ
//        float fov = PI / 2.0f;
//        float cameraZ = (height / 2.0f) / tan(PI * fov / 360.0f);
//        perspective(fov, width / height,
//                cameraZ / 2.0f, cameraZ * 2.0f);


        background(0);
        if (g != null) {
            doLayout();
            g.draw();
        }
    }

    @Override
    public void doLayout() {

        //calculate forces on each node
        //calculate spring forces on each node
        for (int i = 0; i < g.getNodes().size(); i++) {
            ForcedNode n = (ForcedNode) g.getNodes().get(i);
            ArrayList edges = (ArrayList) g.getEdgesFrom(n);
            n.setForce(new Vector3D(0, 0, 0));
            for (int j = 0; edges != null && j < edges.size(); j++) {
                SpringEdge e = (SpringEdge) edges.get(j);
                Vector3D f = e.getForceFrom();
                n.applyForce(f);
            }

            edges = (ArrayList) g.getEdgesTo(n);
            for (int j = 0; edges != null && j < edges.size(); j++) {
                SpringEdge e = (SpringEdge) edges.get(j);
                Vector3D f = e.getForceTo();
                n.applyForce(f);
            }

        }

        //calculate the anti-gravitational forces on each node
        //this is the N^2 shittiness that needs to be optimized
        //TODO: at least make it N^2/2 since forces are symmetrical
        for (int i = 0; i < g.getNodes().size(); i++) {
            ForcedNode a = (ForcedNode) g.getNodes().get(i);
            for (int j = 0; j < g.getNodes().size(); j++) {
                ForcedNode b = (ForcedNode) g.getNodes().get(j);
                if (b != a) {
                    float dx = b.getX() - a.getX();
                    float dy = b.getY() - a.getY();
                    float r = sqrt(dx * dx + dy * dy);
                    //F = G*m1*m2/r^2  

                    if (r != 0) { //don't divide by zero.
                        float f = 100 * (a.getMass() * b.getMass() / (r * r));
                        Vector3D vf = new Vector3D(-dx * f, -dy * f, 0);
                        a.applyForce(vf);
                    }
                }
            }
        }

        //move nodes according to forces
        for (int i = 0; i < g.getNodes().size(); i++) {
            ForcedNode n = (ForcedNode) g.getNodes().get(i);
            if (n != g.getDragNode()) {
                n.setPosition(n.getPosition().add(n.getForce()));
            }
        }
    }

    @Override
    public void keyPressed() {
        if (key == ' ') {
            g = buildRandomGraph();
        } else if (key == 'w') {
            offsetV += 1;
        } else if (key == 'a') {
            offsetH -= 1;
        } else if (key == 's') {
            offsetV -= 1;
        } else if (key == 'd') {
            offsetH += 1;
        }
    }

    @Override
    public void mousePressed() {
        g.setSelectedNode(null);
        g.setDragNode(null);
        for (int i = 0; i < g.getNodes().size(); i++) {
            Node n = (Node) g.getNodes().get(i);
            if (n.containsPoint(mouseX / scaleFactor, mouseY / scaleFactor)) {
                g.setSelectedNode(n);
                g.setDragNode(n);
            }
        }
    }

    @Override
    public void mouseMoved() {
        if (g.getDragNode() == null) {
            g.setHoverNode(null);
            for (int i = 0; i < g.getNodes().size(); i++) {
                Node n = (Node) g.getNodes().get(i);
                if (n.containsPoint(mouseX / scaleFactor, mouseY / scaleFactor)) {
                    g.setHoverNode(n);
                }
            }
        }
    }

    @Override
    public void mouseReleased() {
        g.setDragNode(null);
    }

    @Override
    public void mouseDragged() {
        if (g.getDragNode() != null) {
            g.getDragNode().setPosition(new Vector3D(mouseX / scaleFactor, mouseY / scaleFactor, 0));
        }
    }

    Graph buildRandomGraph() {
        Graph graph;
        int nNodes = 15;
        int nEdges = 40;
        graph = new Graph();

        for (int i = 0; i < nNodes; i++) {
            ForcedNode n = new ForcedNode(new Vector3D(W / 4 + random(W / 2), H / 4 + random(H / 2), 0), this);
            n.setLabel(i + "");
            n.setMass(1.0f + random(3));
            graph.addNode(n.label, n);
        }

        for (int i = 0; i < nEdges; i++) {
            Node a = (Node) graph.getNodes().get((int) random(graph.getNodes().size()));
            Node b = (Node) graph.getNodes().get((int) random(graph.getNodes().size()));
            if (a != b && !(graph.isConnected(a, b))) {
                SpringEdge e = new SpringEdge(a, b, this);
                e.setNaturalLength(10 + random(90));
                graph.addEdge(e);
            }
        }

        return graph;
    }

    Graph buildSimilarityGraph() {
        test = new TestMain();
        Graph g = new Graph();

        // Knoten
        for (int i = 0; i < test.getModules().size(); i++) {
            ForcedNode n = new ForcedNode(new Vector3D(W / 8 + random(W / 4), H / 8 + random(H / 4), 0), this);
            n.setLabel(test.getModules().get(i).getName());
            n.setMass(5f);
            g.addNode(n.label, n);
        }

        for (Module m1 : test.getModules()) {
            for (Module m2 : m1.getSimilarities(0.05, 0.95).keySet()) {
                if (!m1.equals(m2)) {
                    double sim = m1.getSimilarities().get(m2);
                    Node a = g.getNode(m1.getName());
                    Node b = g.getNode(m2.getName());
                    SpringEdge e = new SpringEdge(a, b, this);
                    e.setNaturalLength(100 - 100 * (float) sim);
                    g.addEdge(e);
                }
            }
        }



        return g;
    }

    public static void main(String args[]) {
        PApplet.main(new String[]{"net.openplexus.vis.GraphForce"});
    }
}
