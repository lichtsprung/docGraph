package net.openplexus.vis;

import processing.core.PApplet;

public class Edge {

    Node to;
    Node from;
    Graph g;
    PApplet parent;

    public Edge(Node t, Node f, PApplet parent) {
        to = t;
        from = f;
        this.parent = parent;
    }

    public void setGraph(Graph h) {
        g = h;
    }

    public void draw() {
        parent.stroke(255);
        parent.line(from.getX(), from.getY(), to.getX(), to.getY());
    }

    public Node getTo() {
        return to;
    }

    public Node getFrom() {
        return from;
    }

    public void setTo(Node n) {
        to = n;
    }

    public void setFrom(Node n) {
        from = n;
    }

    public float dX() {
        return to.getX() - from.getX();
    }

    public float dY() {
        return to.getY() - from.getY();
    }
}
