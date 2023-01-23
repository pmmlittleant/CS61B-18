package inheritance.map61b;
import java.util.List;


public interface Map61b<K, V> {
    boolean containsKey(K key);
    void put(K key, V value);
    V get(K key);
    int size();

    /** Returns a list of the keys in this map. */
    List<K> keys();
}
