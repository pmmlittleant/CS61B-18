import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    private static ArrayList<Long> fringe;
    private static GraphDB gr;
    private static long target; // destination node t.
    private static Map<Long, Double> disFromS; // distance from a node to Start node s.
    private static Map<Long, Long> edgeTo; // an edge value node to key node.  v->k.
    private static HashSet<Long> visited; // a set of already visited nodes.
    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     * @param g The graph to use.
     * @param stlon The longitude of the start location.
     * @param stlat The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {

        fringe = new ArrayList<>();
        edgeTo = new HashMap<>();
        visited = new HashSet<>();
        disFromS = new HashMap<>();
        target = g.closest(destlon, destlat);
        gr = g;
        long s = g.closest(stlon, stlat);
        disFromS.put(s, 0.0);
        edgeTo.put(s, null);
        fringe.add(s);

        while (!fringe.isEmpty()) {
            long n = getMinVertex();
            visited.add(n);
            if (n == target) {
                break;     // find the shortest paths from s to t.
            }
            for (long w : gr.adjacent(n)) {
                if (!visited.contains(w)) {
                    double sTon = disFromS.getOrDefault(n, Double.MAX_VALUE);
                    double sTow = disFromS.getOrDefault(w, Double.MAX_VALUE);
                    double edgeWeight = gr.distance(w, n);
                    if (sTon + edgeWeight < sTow) {
                        disFromS.put(w, sTon + edgeWeight);
                        edgeTo.put(w, n);
                        fringe.add(w);
                    }
                }
            }
        }
        return getShortestPath();
    }

    /** return the List of nodes (the shortest path list)*/
    private static List<Long> getShortestPath() {
        List<Long> path = new ArrayList<>();

        while (edgeTo.get(target) != null) {
            path.add(target);
            target = edgeTo.get(target);
        }
        path.add(target);
        Collections.reverse(path);
        return path;
    }

    /** return the node that's closet to t in unvisited nodes list*/
    private static long getMinVertex() {
        double d = Double.MAX_VALUE;
        long v = 0;
        for (int i = 0; i < fringe.size(); i++) {
            long n = fringe.get(i);
            double heuristic = distanceCost(n);
            if (heuristic < d) {
                d = heuristic;
                v = i;
            }
        }
        return fringe.remove((int) v);
    }
    /**return the heuristic h(n) for node n to tell how close it is to t.*/
    private static double distanceCost(long n) {
        //return disFroms.get(n)       Dijkstra algorithm
        return disFromS.get(n) + gr.distance(n, target); // A star algorithm
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     * @param g The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        List<NavigationDirection> naviList = new ArrayList<>();
        long n = route.get(0), v = route.get(1);
        String prevway = g.getRoadName(n, v), curway;
        double distance = g.distance(n, v);
        int direction = 0;
        NavigationDirection navi;
        if (route.size() == 2) {
            navi = creatNaviWith(direction, prevway, distance);
            naviList.add(navi);
            return naviList;
        }
        for (int i = 1; i < route.size() - 1; i++) {
            n = route.get(i);
            v = route.get(i + 1);
            curway = g.getRoadName(n, v);
            if (prevway.equals(curway)) {
                distance += g.distance(n, v);
            } else {
                navi = creatNaviWith(direction, prevway, distance);
                naviList.add(navi);
                distance = g.distance(n, v);
                prevway = curway;
                direction = getDirection(n, v, g);
            }
        }
        naviList.add(creatNaviWith(direction, prevway, distance));
        return naviList;
    }
    private static int getDirection(long n, long v, GraphDB g) {
        double angle = g.bearing(n, v);
        if (angle >= -15 && angle < 15) {
            return 1;
        }
        if (angle < -15 && angle >= -30) {
            return 2;
        }
        if (angle >= 15 && angle < 30) {
            return 3;
        }
        if (angle < -30 && angle >= -100) {
            return 5;
        }
        if (angle >= 30 && angle < 100) {
            return 4;
        }
        if (angle < -100) {
            return 6;
        }
        return 7;
    }
    private static NavigationDirection creatNaviWith(int direction, String way, double distance) {
        NavigationDirection nv = new NavigationDirection();
        nv.direction = direction;
        nv.way = way;
        nv.distance = distance;
        return nv;
    }
    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";
        
        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents.*/
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                    && way.equals(((NavigationDirection) o).way)
                    && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
