/** Demonstrates creation of a method in java. */
public class Larger {
    /** Return the larger of x and y. */
    public static int lager(int x, int y) {
        if (x > y) {
            return x;
        }
        return y;
    }

    public static void main(String[] args) {
        System.out.println(lager(-5, 10));
    }
}

/**
 * 1.Functions must be declared as part of a class in java.
 * A function that is part of a class is called a method.
 * in java, all functions are methods.
 * 
 * 2.To define a function in java, we use 'public static'.
 * 3.All parameters of a function must have a declared type,
 * and the return value of a function must have a declared type.
 * Functions in java return only one value!
 */