package ADT.DisjointSets;

/** Track tree size (number of elements)
 *  Always link root of smaller tree to larger tree.
 *  add path compression operation.
 * */
public class WeightedQuickUnionDSWithcompression implements DisjointSets {
    private int[] parent;
    private int[] size;


    public WeightedQuickUnionDSWithcompression(int N) {
        parent = new int[N];
        size = new int[N];
        for (int i = 0; i < N; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    /** path compression */
    public int find (int p) {
        if (p == parent[p]) {
            return p;
        }
        parent[p] = find(parent[p]); //path compression operation. find the boss node
                                    // and reset every node's parent direct to the boss node.
        return parent[p];
    }

    @Override
    /** Link root of small tree to larger tree
     *  Update size[] array.
     *  O(logN)*/
    public void connect(int p, int q) {
        int i = parent[p];
        int j = parent[q];
        if (i == j) {
            return;
        }
        if (size[i] < size[j]) {
            parent[i] = j;
            size[j] += size[i];
        } else {
            parent[j] = i;
            size[i] += size[j];
        }

    }

    @Override
    /** O(logN) */
    public boolean isConnected(int p, int q) {
        return find(p) == find(q);
    }
}
