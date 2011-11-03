/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.openplexus;

/**
 * Ein  Bigram.
 * @author Robert Giacinto
 */
public class Tuple2 implements Tuple {

    public String termA;
    public String termB;

    public Tuple2(String termA, String termB) {
        this.termA = termA;
        this.termB = termB;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tuple2 other = (Tuple2) obj;
        if ((this.termA == null) ? (other.termA != null) : !this.termA.equals(other.termA)) {
            return false;
        }
        if ((this.termB == null) ? (other.termB != null) : !this.termB.equals(other.termB)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (this.termA != null ? this.termA.hashCode() : 0);
        hash = 31 * hash + (this.termB != null ? this.termB.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return termA + " " + termB;
    }
}
