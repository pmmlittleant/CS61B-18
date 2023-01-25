import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */
    private Map<Long, Node> vertices = new HashMap<>();
    private Map<Long, Node> cleaned = new HashMap<>(); // nodes that are all connected.
    private Map<String, List<Long>> locations = new HashMap<>(); // mapping from location names(cleaned) to list of nodes ids.
    protected static class Node {

        private long id;
        private double lat;
        private double lon;
        private String name;
        private LinkedList<Edge> edges = new LinkedList<>();

        public Node(String id, String lon, String lat) {
            this.id = Long.parseLong(id);
            this.lat = Double.parseDouble(lat);
            this.lon = Double.parseDouble(lon);
        }
        private void addName(String n) {
            name = n;
        }

        /**Return true if node is connected to other nodes.*/
        private boolean hasNeighbors() {
            return edges.size() != 0;
        }
        private void addEdge(Edge e) {
            edges.add(e);
        }

    }

    private class Edge {
        private Map<Node, Node> connectedNodes = new HashMap<>();
        private String roadName;
        public Edge(Node n, Node m, String name) {
            connectedNodes.put(n, m);
            connectedNodes.put(m, n);
            roadName = name;
        }

        /**Return the neighbor node w of node v.*/
        private Node getNeighbor(Node v) {
            return connectedNodes.get(v);
        }
        private String getRoadName() {
            return roadName;
        }

    }

    /**Return the edge's roadName between two nodes*/
    public String getRoadName(long n, long v) {
        if (vertices.containsKey(n) && vertices.containsKey(v)) {
            Node nd = vertices.get(n);
            Node nv = vertices.get(v);
            for (Edge e : nd.edges) {
                if (e.getNeighbor(nd) == nv) {
                    return e.getRoadName();
                }
            }
        }
        return "unknown road";
    }
    /** Insert a Node to the graph.*/
    public void addVertex(Node nd) {
        long id = nd.id;
        vertices.put(id, nd);
    }

    /** Connect two vertexes v1, v2, with edge name in the graph. and add them to cleaned*/
    public void connectVertice(long v1, long v2, String  name) {
        if (v1 != v2 && vertices.containsKey(v1) && vertices.containsKey(v2)) {
            Node n1 = vertices.get(v1);
            Node n2 = vertices.get(v2);
            Edge e = new Edge(n1, n2, name);
            n1.addEdge(e);
            n2.addEdge(e);
            if (n1.hasNeighbors()) {
                cleaned.put(v1, n1);
            }
            if (n2.hasNeighbors()) {
                cleaned.put(v2, n2);
            }
        }
    }
    /** Add a location to the node( add a name to the node)and update locations map.*/
    public void addLocation(long v, String name) {
        if (vertices.containsKey(v)) {
            Node n = vertices.get(v);
            n.addName(name);
            List<Long> locs = locations.getOrDefault(cleanString(name), new ArrayList<>());
            locs.add(v);
            locations.put(cleanString(name), locs);
        }
    }
    /** Return name string of a node (uncleaned)*/
    public String getLocation(long v) {
        String name = vertices.get(v).name;
        return name;
    }
    /** Return a list of nodes id which match paramater name */
    public List<Long> getVerticesByName(String name) {
        if (locations.containsKey(cleanString(name))) {
            return locations.get(cleanString(name));
        }
        return null;
    }
    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        vertices = cleaned;
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        return vertices.keySet();
    }

    /**
     * Returns ids of all vertices adjacent to v.
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        ArrayList<Long> ids = new ArrayList<>();
        if (vertices.containsKey(v)) {
            Node n = vertices.get(v);
            for (Edge e : n.edges) {
                Node w = e.getNeighbor(n);
                ids.add(w.id);
            }
        }
        return ids;
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        double minDis = Double.MAX_VALUE;
        long vertex = 0;
        for (Long v : vertices.keySet()) {
            double distance = distance(lon(v), lat(v), lon, lat);
            if (minDis >= distance) {
                vertex = v;
                minDis = distance;
            }
        }
        return vertex;
    }

    /**
     * Gets the longitude of a vertex.
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        return vertices.get(v).lon;
    }

    /**
     * Gets the latitude of a vertex.
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        return vertices.get(v).lat;
    }
}
