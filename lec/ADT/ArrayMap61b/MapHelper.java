package inheritance.map61b;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;


/**
 *  generic methods in java
 * */
public class MapHelper {
    /** Return item in map if it exists. */

    // define type parameters before the return type of the method to create a generic method.
    public static <K, V> V get(Map61b<K, V> map, K key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return null;
    }

    /** Return max of all keys. Works only if x and y have comparable data.*/

    // extends : type upper bound. State a fact that K must be a subclass of comparable
    public static <K extends Comparable<K>, V> K maxKey(Map61b<K, V> map) {
        List<K> keylist = map.keys();
        K largest = keylist.get(0);
        for (K k: keylist) {
            if (k.compareTo(largest) > 0) {
                largest = k;
            }
        }
        return largest;
    }

    @Test
    public void testMaxKey() {
        Map61b<String, Integer> map = new ArrayMap<String, Integer>();
        map.put("horse", 3);
        map.put("fish", 9);
        map.put("house", 10);

        String exp = "house";
        assertEquals(exp, MapHelper.maxKey(map));
    }
}
