package net.openplexus.vis;

import java.util.logging.Logger;
import processing.core.PApplet;

//Copyright 2005 Sean McCullough
//banksean at yahoo
public class SpringEdge extends Edge {

    float k = 0.05f; //stiffness
    float a = 100; //natural length.  ehmm uh, huh huh stiffness. natural length ;-)

    //This edge sublcass apples a spring force between the two nodes it connects
    //The spring force formula is F = k(currentLength-a)
    //This equation is one-dimensional, and applies to the straight line
    //between the two nodes.
    public SpringEdge(Node a, Node b, PApplet parent) {
        super(a, b, parent);
    }

    public void setNaturalLength(float l) {
        a = l;
    }

    public float getNaturalLength() {
        return a;
    }

    public Vector3D getForceTo() {
        float dx = dX();
        float dy = dY();
        float l = PApplet.sqrt(dx * dx + dy * dy);
        float f = k * (l - a);

        return new Vector3D(-f * dx / l, -f * dy / l, 0);
    }

    public Vector3D getForceFrom() {
        float dx = dX();
        float dy = dY();
        float l = PApplet.sqrt(dx * dx + dy * dy);
        float f = k * (l - a);

        return new Vector3D(f * dx / l, f * dy / l, 0);
    }

    @Override
    public void draw() {
//        float dx = dX();
//        float dy = dY();
//        Vector3D f = getForceFrom();
//
//        parent.stroke(255, 255, 255, 25);
//        parent.strokeWeight(100 / a);
//        parent.line(from.getX(), from.getY(), 0, to.getX(), to.getY(), 0);
        //text(s, from.getX() + dx/2 - textWidth(s)/2, from.getY() + dy/2);
        //smooth();
    }
    private static final Logger LOG = Logger.getLogger(SpringEdge.class.getName());
}