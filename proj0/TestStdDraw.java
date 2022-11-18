public class TestStdDraw {
    public static void main(String[] args) {
        StdDraw.setScale(-100, 100);
        StdDraw.enableDoubleBuffering();
        double size = 10;
        while (size < 100) {
            StdDraw.clear();
            StdDraw.picture(0, 0, "images/electron.png", size, size);
            StdDraw.show();
            StdDraw.pause(5);
            size += 10;
        }
    }
}
