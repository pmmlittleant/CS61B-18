package ADT;
public interface DisjointSets {
    /** Connect two items P and Q. */

    void connect(int p, int q);

    /** Checks to see if  two items are connected. */

    boolean isConnected(int p, int q);
}
