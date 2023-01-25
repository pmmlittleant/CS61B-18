import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {

    public static final double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
    /** Each tile is 256x256 pixels. */
    public static final int TILE_SIZE = 256;
    double ROOT_DPP = (ROOT_LRLON - ROOT_ULLON) * 288200 / TILE_SIZE;
    public Rasterer() {
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        queryBox qb = new queryBox(params);
        //check corner case
        if (qb.outOfBound()) {
            results.put("query_success", false);
            return results;
        }

        tilesForQueryBox tiles = new tilesForQueryBox(qb);

        results.put("raster_ul_lon", tiles.rasterUllon());
        results.put("raster_ul_lat", tiles.rasterUllat());
        results.put("raster_lr_lon", tiles.rasterLrlon());
        results.put("raster_lr_lat", tiles.rasterLrlat());
        results.put("depth", tiles.depth);
        results.put("query_success", true);
        results.put("render_grid", tiles.renderGrid());
         return results;
    }

    private class queryBox {
        double ullon;
        double ullat;
        double lrlat;
        double lrlon;
        double w;
        double h;
        double lonDPP; // longitude distance per pixel of the query Box

        public queryBox(Map<String, Double> params) {
            lrlon = params.get("lrlon");
            ullon = params.get("ullon");
            lrlat = params.get("lrlat");
            ullat = params.get("ullat");
            w = params.get("w");
            h = params.get("h");
            lonDPP = (lrlon - ullon) * 288200 / w;
        }
        /** return true if queryBox doesn't make sense.*/
        public boolean outOfBound() {
            if (lrlon <= ROOT_ULLON || ullon >= ROOT_LRLON || ullat <= ROOT_LRLAT || lrlat >= ROOT_ULLAT) {
                return true;
            }
            if (lrlon <= ullon || lrlat >= ullat) {
                return true;
            }
            return false;
        }

    }
    private class tilesForQueryBox {
        private queryBox qb;
        private int depth;
        public tilesForQueryBox(queryBox qeuryBox) {
            qb = qeuryBox;
            depth = findDepth(qb.lonDPP);
        }

        /** Return the render-grids of files.*/
        private String[][] renderGrid() {
            int startY = findStartRowY(), endY = findEndRowY(), startX = findStartColX(), endX = findEndColX();
            int row = endY - startY + 1;
            int col = endX - startX + 1;
            String[][] grids = new String[row][col];
            for (int i = 0, y = startY; i < row; i ++, y++) {
                for (int j = 0, x = startX; j < col; j++, x++) {
                    String file = "d" + depth + "_x" + x + "_y" + y + ".png";
                    grids[i][j] = file;
                }
            }
            return grids;
        }

        /** Return the raster_ul_lon.*/
        private double rasterUllon() {
            int x = findStartColX();
            return ullonOf(depth, x);
        }
        /** Return the raster_ul_lat.*/
        private double rasterUllat() {
            int y = findStartRowY();
            return ullatOf(depth, y);
        }
        /** Return the raster_lr_lon.*/
        private double rasterLrlon() {
            int x = findEndColX();
            return lrlonOf(depth, x);
        }
        /** Return the raster_lr_lat.*/
        private double rasterLrlat() {
            int y = findEndRowY();
            return lrlatOf(depth, y);
        }

        /** Return the most proper depth of image according to request lonDPP.*/
        private int findDepth(double lonDPP) {
            int d = 0;
            while (ROOT_DPP * Math.pow(0.5, d) > lonDPP) {
                d++;
            }
            return d <= 7 ? d : 7;
        }
        /**Return the start row y of tiles.*/
        private int findStartRowY() {
            if (qb.ullat >= ROOT_ULLAT) {
                return 0;
            }
            double distance = ROOT_ULLAT - qb.ullat;
            double width = latWidthPerImage(depth);
            int rowY = (int) (distance / width);
            return rowY;
        }
        /**Return the end row y of tiles. */
        private int findEndRowY() {
            if (qb.lrlat <= ROOT_LRLAT) {
                return (int) Math.pow(2, depth) - 1;
            }
            double distance = ROOT_ULLAT - qb.lrlat;
            double width = latWidthPerImage(depth);
            int endRowY = (int) (distance / width);
            return endRowY;
        }
        /***Return the start col x of tiles.*/
        private int findStartColX() {
            if (qb.ullon <= ROOT_ULLON) {
                return 0;
            }
            double distance = qb.ullon - ROOT_ULLON;
            double width = lonWidthPerImage(depth);
            int startColX = (int) (distance / width);
            return startColX;
        }
        /**Return the end col x of tiles.*/
        private int findEndColX() {
            if (qb.lrlon >= ROOT_LRLON) {
                return (int) Math.pow(2, depth) - 1;
            }
            double distance = qb.lrlon - ROOT_ULLON;
            double width = lonWidthPerImage(depth);
            return (int) (distance / width);
        }
    }



    /** Return the ullon of file at depth d with col x */
    private double ullonOf(int d, int x) {
        double lonWidth = lonWidthPerImage(d);
        return ROOT_ULLON + lonWidth * x;
    }
    /** Return the ullat of file at depth d with row y */
    private double ullatOf(int d, int y) {
        double latWidth = latWidthPerImage(d);
        return ROOT_ULLAT - latWidth * y;
    }
    /** Return the lrlon of file at depth d with col x */
    private double lrlonOf(int d, int x) {
        double lonWidth = lonWidthPerImage(d);
        return ROOT_ULLON + (x + 1) * lonWidth;
    }
    /** Return the lrlat of file at depth d with row y */
    private double lrlatOf(int d, int y) {
        double latWidth = latWidthPerImage(d);
        return ROOT_ULLAT - latWidth * (y + 1);
    }
    /** return the longitudinal width of a tile at depth d.*/
    private double lonWidthPerImage(int d) {
        return (ROOT_LRLON - ROOT_ULLON)/ Math.pow(2, d);
    }
    /** return the latitudinal width of a tile at depth d.*/
    private double latWidthPerImage(int d) {
        return (ROOT_ULLAT - ROOT_LRLAT) / Math.pow(2, d);
    }

}
