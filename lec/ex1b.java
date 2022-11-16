public class ClassNameHere {
    /** Draw a triangle with height N */
    public static void drawTriangle(int N) {
        int i = 1;
        while (i <= N) {
            for (int j = 0; j < i; j++) {
                System.out.print("*");
            }
            System.out.println("");
            i += 1;
        }
    }

    /** Return the maximum value in an int array M */
    public static int formax(int[] m) {
        int len = m.length, maxnum = m[0];
        for (int i = 0; i < len; i++) {
            if (m[i] > maxnum) {
                maxnum = m[i];
            }
        }
        return maxnum;
    }

    /** Exercise 4 */
    public static void windowPosSum(int[] a, int n) {
        int len = a.length;
        for (int i = 0; i < len; i++) {
            int sum = 0;
            if (a[i] < 0) {
                continue;
            }
            for (int j = i; j <= i + n; j++) {
                if (j == len) {
                    break;
                }
                sum += a[j];
            }
            a[i] = sum;
        }
    }

    public static void main(String[] args) {
        drawTriangle(10);

        int[] numbers = new int[] { 9, 2, 15, 2, 22, 10, 6 };
        System.out.println(formax(numbers));

        int[] a = { 1, 2, -3, 4, 5, 4 };
        int n = 3;
        windowPosSum(a, n);
        System.out.println(java.util.Arrays.toString(a));

    }

}