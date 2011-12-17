package net.openplexus;

/**
 * Ein Trigram.
 * @author Robert Giacinto
 */
public class Tuple3 implements Tuple {

    public String termA, termB, termC;

    public Tuple3(String termA, String termB, String termC) {
        this.termA = termA;
        this.termB = termB;
        this.termC = termC;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tuple3 other = (Tuple3) obj;
        if ((this.termA == null) ? (other.termA != null) : !this.termA.equals(other.termA)) {
            return false;
        }
        if ((this.termB == null) ? (other.termB != null) : !this.termB.equals(other.termB)) {
            return false;
        }
        if ((this.termC == null) ? (other.termC != null) : !this.termC.equals(other.termC)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.termA != null ? this.termA.hashCode() : 0);
        hash = 71 * hash + (this.termB != null ? this.termB.hashCode() : 0);
        hash = 71 * hash + (this.termC != null ? this.termC.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return termA + " " + termB + " " + termC;
    }
}
