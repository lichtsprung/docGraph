package net.openplexus;

/**
 * Eine Komponente innerhalb des Term Vectos.
 *
 * @author Robert Giacinto
 */
public class TVComponent {

    private String term;
    private int count;
    private double weight;

    public TVComponent(String term, int count, double weight) {
        this.term = term;
        this.count = count;
        this.weight = weight;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getWeightedComponent() {
        return weight * count;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TVComponent other = (TVComponent) obj;
        if ((this.term == null) ? (other.term != null) : !this.term.equals(other.term)) {
            return false;
        }
        if (this.count != other.count) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.term != null ? this.term.hashCode() : 0);
        hash = 59 * hash + this.count;
        return hash;
    }

    @Override
    public String toString() {
        return "TVComponent{" + "term=" + term + ", count=" + count + '}';
    }
}
