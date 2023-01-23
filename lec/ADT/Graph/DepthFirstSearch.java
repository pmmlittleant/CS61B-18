package ADT.Graph;

/** Common design pattern in graph algorithms:
 *  graph-processing client class:

 *  pass a graph object to a graph-processing method(or constructor)
 *  Can query the client class for information*/


/** properties for depth-first search:
 *  Guaranteed to reach every node.
 *  Runs in Θ(V+E) time :
 *  V is number of vertex, E is number of edges.
 *  Each vertex is visited exactly once, each edge is used once
 *  Space : Θ（V） ： call stack depth is at most V.
 *  * */

public class DepthFirstSearch {
    private boolean[] marked; //marked[v] is true iff v connected to s
    private int[] edgeTo;   // edgeTo[v] is previous vertex on path from s to v
    private int s;


    // Find all paths from G
    public DepthFirstSearch(Graph G, int s) {
        marked = new boolean[G.V()];
        edgeTo = new int[G.V()];
        dfs(G, s);
    }
    /** Depth-First Paths :
     *  find a path from s to every other reachable vertex,visiting each vertex at most once
     *  1.Mark v, instance variable : set marked[v] = true;
     *  2.For each unmarked adjacent vertex w:
     *    Set edgeTo[w] = v;
     *    dfs(w)
     *  */
    private void dfs(Graph G, int v) {
        marked[v] = true;
        for (int w : G.adj(v)) {
            if(!marked[w]) {
                edgeTo[w] = v;
                dfs(G, w);
            }
        }
    }

    // is there  a path from s to v?
    public boolean hasPathTo(int v) {
        return marked[v];
    }

    // Path from s to v (if any)
    public Iterable<Integer> pathTo(int v) {
        return null;
    }

}
