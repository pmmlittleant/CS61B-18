public class HelloNumbers {
    public static void main(String[] args) {
        int x = 0, sums = 0;
        while (x < 10) {
            sums += x;
            x = x + 1;
            System.out.print(sums + " ");
        }
        System.out.println("");
        System.out.println(5 + 10);
        System.out.println(5 + "10");

    }
}

/**
 * 1. Before java variables can be used, they must be decalred.
 * 2. java variables must have a specific type.
 * 3. java variables can never change.
 * 4. types are verified before the code even runs!
 */
