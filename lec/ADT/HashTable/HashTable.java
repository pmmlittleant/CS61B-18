package ADT.Hashing;

// Two challenges:1. Collision handling 2. Computing a hashcode
/** Hashcode properties:
 * 1.It must be an Integer.
 * 2.If we run .hashCode() on an object twice, it should return the same number.
 * 3.Two objects that are considered .equal() must have the same hash code.
 * a good Hashcode should : Distribute items evenly
 * */

public class HashSet {





    /** convert English strings to integer by add characters scaled by powers of 27.*/
    private int englishToInt(String eg) {
        char[] chars = eg.toCharArray();
        int power = eg.length() - 1;
        int inter = 0;
        for (char c : chars) {
            inter += (c - 96) * Math.pow(27, power);
            power --;
        }
        return inter;
    }

    public static void main(String[] args) {
        String a = "dog";
        HashSet s = new HashSet();

        System.out.println(s.englishToInt("potato"));
    }
}
