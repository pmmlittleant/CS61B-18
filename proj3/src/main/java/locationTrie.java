import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A trie which stores vertex's name (location's name)*/
public class locationTrie {
    private int size;
    private Node root;
    private class Node {
        private boolean isKey;
        private Map<Character, Node> links;
        public Node() {
            isKey = false;
            links = new HashMap<>();
        }
    }

    public locationTrie() {
        root = new Node();
        size = 0;
    }

    /** put key string into trie.*/
    public void put(String key) {
        putHelper(key, 0, root);
        size += 1;
    }
    public int size() {
        return size;
    }
    private Node putHelper(String key, int d, Node n) {
        if (n == null) {
            n = new Node();
        }
        if (d == key.length()) {
            n.isKey = true;
            return n;
        }
        char c = key.charAt(d);
        Node child = n.links.get(c);
        Node newChild = putHelper(key, d + 1, child);
        n.links.put(c, newChild);
        return n;
    }

    public List<String> getKeyByPrefix(String prefix) {
        Node start = findPrexNode(prefix, 0, root);
        List<String> keylist = new ArrayList<>();
        if (start == null) {
            return null;
        }
        addKeyStrings(start, prefix, keylist);
        return keylist;
    }

    /** return the start node by prefix.*/
    private Node findPrexNode(String prefix, int d, Node n) {
        if (n == null || d == prefix.length()) {
            return n;
        }
        char c = prefix.charAt(d);
        Node nd = n.links.get(c);
        return findPrexNode(prefix, d + 1, nd);
    }

    private void addKeyStrings(Node n, String sofar, List<String> keylist) {
        if (n == null) {
            return;
        }
        for (char c : n.links.keySet()) {
            Node child = n.links.get(c);
            if (child.isKey) {
                keylist.add(sofar + c);
            }
            addKeyStrings(child, sofar + c, keylist);
        }
    }
}
