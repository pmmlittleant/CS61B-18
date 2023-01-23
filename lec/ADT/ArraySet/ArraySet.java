package ADT.ArraySet;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

/** Implement Set using Array
 *  Θ(N) in the worst case.
 * */
public class ArraySet<T> implements Iterable<T> {
    private T[] values;
    private int size;

    public ArraySet() {
        values = (T[]) new Object[100];
        size = 0;
    }

    public void add(T value){
        /* Throws an IllegalArgumentException if the value is null.
        if (value == null) {
            throw new IllegalArgumentException("You can't add null to an ArraySet.");
        }
        */
        if (contains(value)) {
            return;
        }
        values[size] =value;
        size += 1;
    }

    public boolean contains(T value) {
        for (int i = 0; i < size; i++) {

            if (values[i] == null && value == null) { // null-safe method.
                return true;
            }
            if (values[i].equals(value)) {
                return true;
            }
        }
        return false;
    }
    public int size() {
        return size;
    }

    /** Returns an iterator*/
    @Override
    public Iterator<T> iterator() {
        return new ArraySetIterator();
    }

    private class ArraySetIterator implements Iterator<T> {
        private int wizPos;
        public ArraySetIterator() {
            wizPos = 0;
        }
        @Override
        public boolean hasNext() {
            return wizPos < size;
        }
        @Override
        public T next() {
            T item = values[wizPos];
            wizPos += 1;
            return item;
        }
    }

    /** toString method provides a string representation of an object.
     * System.out.println(Object x) calls x.toString()
     * */
    /*
    @Override
    public String toString() {
        StringBuilder returnSB = new StringBuilder("{");
        for (T item: this) {
            returnSB.append(item); //implicitly java calls item.toSting() when combining string to another object.
            returnSB.append(", ");
        }
        returnSB.append("}");
        return returnSB.toString();
    }
    */
    @Override
    public String toString() {
        List<String> listOfItems = new ArrayList<>();
        for (T x: this) {
            listOfItems.add(x.toString());
        }
        return "{" + String.join(", ", listOfItems) + "}";
    }

    /**
     **/
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        /**
         * (o instanceof ArraySet oas)
         * instanceof checks to see if o's dynamic type is ArraySet,
         * casts o into a variable of static type ArraySet called oas.
         * also handles null.*/

        if (o instanceof ArraySet) {
            ArraySet oas = (ArraySet) o;
            // check sets are of the same size
            if (oas.size() != size()) {
                return false;
            }
            // check that all of My items are in the other array set
            for (T item: this) {
                if (!oas.contains(item)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    /** Create generic static method ArraySet.of
     *  ... : VAR Arg  a variable number of arguments
     * */
    public static <Glerp> ArraySet<Glerp> of(Glerp... stuff) {
        ArraySet<Glerp> returnSet = new ArraySet<Glerp>();
        for (Glerp x : stuff) {
            returnSet.add(x);
        }
        return returnSet;
    }

    public static void main(String[] args) {

        ArraySet<Integer> aset = new ArraySet<Integer>();
        aset.add(5);
        aset.add(23);
        aset.add(42);

        //implicitly call aset.toString().
        System.out.println(aset);


        /** ArraySet must have iterator method witch has hasNext and next methods.*/
        Iterator<Integer> aseer = aset.iterator();
        while (aseer.hasNext()) {
            int item = aseer.next();
            System.out.println(item);
        }

        /** Iteration
         * ArraySet must implement Iterable<T> can use enhanced for loop. */
        for (int i: aset) {
            System.out.println(i);
        }

        /** generic static method ArraySet.of(...) */
        ArraySet<String> ass = ArraySet.of("dog", "sky", "cat");
        System.out.println(ass);
    }

    @Test
    public void equalsTest() {
        ArraySet<Integer> as = new ArraySet<Integer>();
        as.add(10);
        as.add(12);
        as.add(13);
        ArraySet<Integer> oas = new ArraySet<Integer>();
        assertFalse(as.equals(oas));

        oas.add(12);
        oas.add(13);
        oas.add(13);
        assertFalse(as.equals(oas));

        oas.add(10);
        assertTrue(as.equals(oas));

        Set<Integer> set = new HashSet<>();
        set.add(10);
        set.add(12);
        set.add(13);
        assertFalse(as.equals(set));
    }
}
