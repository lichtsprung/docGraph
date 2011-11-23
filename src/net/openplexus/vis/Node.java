package net.openplexus.vis;

//Copyright 2005 Sean McCullough
import java.util.logging.Logger;
import processing.core.PApplet;

//banksean at yahoo
public class Node {

    Vector3D position;
    float h = 10;
    float w = 10;
    String label = "";
    Graph g;
    PApplet parent;

    public Node() {
        position = new Vector3D();
    }

    public void setGraph(Graph h) {
        g = h;
    }

    public void setLabel(String s) {
        label = s;
    }

    public boolean containsPoint(float x, float y) {
        float dx = position.getX() - x;
        float dy = position.getY() - y;

        return (parent.abs(dx) < w / 2 && parent.abs(dy) < h / 2);
    }

    public Node(Vector3D v, PApplet parent) {
        position = v;
        this.parent = parent;
    }

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D v) {
        position = v;
    }

    public float getX() {
        return position.getX();
    }

    public float getY() {
        return position.getY();
    }

    public void draw() {
        parent.stroke(0);
        parent.fill(255);
        parent.ellipse(getX(), getY(), h, w);
    }
    private static final Logger LOG = Logger.getLogger(Node.class.getName());
}
