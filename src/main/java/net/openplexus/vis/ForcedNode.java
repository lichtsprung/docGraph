package net.openplexus.vis;

import java.util.logging.Logger;
import processing.core.PApplet;

public class ForcedNode extends Node {

    Vector3D f = new Vector3D(0, 0, 0);
    float mass = 1;

    public ForcedNode(Vector3D v, PApplet parent) {
        super(v, parent);
        h = 20;
        w = 20;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float m) {
        mass = m;
        h = m * 20;
        w = m * 20;
    }

    public void setForce(Vector3D v) {
        f = v;
    }

    public Vector3D getForce() {
        return f;
    }

    public void applyForce(Vector3D v) {
        f = f.add(v);
    }

    public void draw() {
        //super.draw();
        if (g.getSelectedNode() == this) {
            parent.stroke(32, 64, 255, 128);
            parent.strokeWeight(10);
            parent.fill(255, 255, 255, 128);
        } else if (g.getHoverNode() == this) {
            parent.noStroke();
            parent.fill(255, 255, 255, 128);
        } else {
            parent.noStroke();
            parent.fill(255, 255, 255, 64);
        }
        if (label.contains("(TI)")) {
            parent.fill(255, 0, 0, 128);
        } else if (label.contains("(AI)")) {
            parent.fill(0, 255, 0, 128);
        } else if (label.contains("(WI)")) {
            parent.fill(0, 0, 255, 128);
        }else if (label.contains("(MI)")) {
            parent.fill(255, 0, 255, 128);
        }
        parent.ellipse(getX(), getY(), h, w);

        parent.fill(255);
        parent.text(label, getX(), getY());
    }
    private static final Logger LOG = Logger.getLogger(ForcedNode.class.getName());
}
