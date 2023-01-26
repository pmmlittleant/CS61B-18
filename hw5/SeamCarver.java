import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeamCarver {
    private Picture p;
    private int w; // picture width
    private int h; // picture height
    public SeamCarver(Picture picture) {
        p  = picture;
        w = p.width();
        h = p.height();
    }


    // current picture
    public Picture picture() {
        return p;
    }

    // width of current picture
    public int width() {
        return w;
    }

    //height of current picture
    public int height() {
        return h;
    }

    //energey of pixel at column x and row y.
    public double energy(int x, int y) {
        if (x < 0 || x >= w || y < 0 || y >= h) {
            throw new IndexOutOfBoundsException();
        }
        return xGradient(x, y) + yGradient(x, y);
    }
    // calculate the  x-gradient energy of pixel at column x and row y.
    private double xGradient(int x, int y) {
        int leftX = x - 1 < 0 ? w - 1 : x - 1;
        int rightX = x + 1 == w ? 0 : x + 1;
        Color leftC = p.get(leftX, y);
        Color rightC = p.get(rightX, y);
        double redDiff = Math.abs(leftC.getRed() - rightC.getRed());
        double greenDiff = Math.abs(leftC.getGreen() - rightC.getGreen());
        double blueDiff = Math.abs(leftC.getBlue() - rightC.getBlue());
        return redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff;
    }
    // calculate the  y-gradient energy of pixel at column x and row y.
    private double yGradient(int x, int y) {
        int upY = y - 1 < 0 ? h - 1 : y - 1;
        int downY = y + 1 == h ? 0 : y + 1;
        Color upC = p.get(x, upY);
        Color downC = p.get(x, downY);
        double redDiff = Math.abs(upC.getRed() - downC.getRed());
        double greenDiff = Math.abs(upC.getGreen() - downC.getGreen());
        double blueDiff = Math.abs(upC.getBlue() - downC.getBlue());
        return redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff;
    }

    //Sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        Picture transP = new Picture(h, w);
        for (int i = 0; i < transP.height(); i++) {
            for (int j = 0; j < transP.width(); j++) {
                Color c = p.get(i ,h - 1- j);
                transP.set(j, i, c);
            }
        }
        SeamCarver sc = new SeamCarver(transP);
        int[] transPath = sc.findVerticalSeam();
        int[] path = new int[w];
        for (int x = 0; x < w; x++) {
            path[x] = h - 1 - transPath[x];
        }
        return path;
    }

    // sequence of indices for vertical seam (find a vertical seam of minimum total energy, similar to shortest path problem
    public int[] findVerticalSeam() {
        int[][] path = new int[h][w];
        double[][] cost = new double[h][w];
        minimumCostPath(path, cost);
        double minimumCost = Double.MAX_VALUE;
        int x = 0;
        for (int j = 0; j < w; j++) {
            if (cost[h-1][j] < minimumCost) {
                x = j;
                minimumCost = cost[h-1][j];
            }
        }
        return backTraceMinimumPath(path, x);
    }

    private int[] backTraceMinimumPath(int[][] path, int x) {
        int[] mini = new int[h];
        for (int i = h - 1; i >= 0; i--) {
            mini[i] = x;
            x = path[i][x];
        }
        return mini;
    }

    /** calculate cost of minimum path ending at (i, j) and record its parent's(i-1, col) column col */
    private void minimumCostPath(int[][] path, double[][] cost) {
        for (int x = 0; x < w; x++) {
            path[0][x] = -1;
            cost[0][x] = energy(x, 0);
        }
        for (int i = 1; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int parentCol = findPath(j, i - 1, cost);
                path[i][j] = parentCol;
                cost[i][j] = cost[i - 1][parentCol] + energy(j, i);
            }
        }
    }
    /** return column index which is the column of top right or top mid or top left of pixel (i, j).
     *  (i - 1,  column) is the minimum path to pixel(i, j)
     * */
    private int findPath(int j, int i, double[][] cost) {
        if (j == 0) {
            if (cost[i][j] < cost[i][j + 1]) {
                return j;
            }
            return j + 1;
        }
        if (j == w - 1) {
            if (cost[i][j] < cost[i][j - 1]) {
                return j;
            }
            return j - 1;
        }
        if (cost[i][j] <= cost[i][j - 1] && cost[i][j] <= cost[i][j + 1]) {
            return j;
        }
        if (cost[i][j -1] < cost[i][j] && cost[i][j - 1] < cost[i][j + 1]) {
            return j - 1;
        }
        return j + 1;
    }








    // remove horizontal seam from picture.
    public void removeHorizontalSeam(int[] seam) {
        if (seam.length != w) {
            throw new IllegalArgumentException();
        }
        SeamRemover.removeHorizontalSeam(p, seam);

    }
    // remove vertical seam from picture
    public void removeVerticalSeam(int[] seam) {
        if (seam.length != h) {
            throw new IllegalArgumentException();
        }
        SeamRemover.removeVerticalSeam(p, seam);
    }

    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        StdOut.printf("%d-by-%d image\n", picture.width(), picture.height());
        SeamCarver sc = new SeamCarver(picture);
        for (int row = 0; row < sc.height(); row++) {
            for (int col = 0; col < sc.width(); col++)
                StdOut.println(sc.energy(col, row));
            StdOut.println();
        }
    }
}
