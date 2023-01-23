public class Printfigure {
    public static void main(String[] args) {
        int i = 1;
        while (i <= 5) {
            for (int j = 0; j < i; j++) {
                System.out.print("*");
            }
            System.out.println("");
            i += 1;
        }
    }
}
