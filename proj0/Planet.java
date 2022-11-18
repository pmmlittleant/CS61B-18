public class Planet {
    public double xxPos, yyPos, xxVel, yyVel, mass;
    public String imgFileName;
    public static final double G = 6.67e-11;

    public Planet(double xP, double yP, double xV, double yV, double m, String img) {
        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = m;
        imgFileName = img;
    }

    public Planet(Planet p) {
        xxPos = p.xxPos;
        yyPos = p.yyPos;
        xxVel = p.xxVel;
        yyVel = p.yyVel;
        mass = p.mass;
        imgFileName = p.imgFileName;
    }

    /** Return distance between two planet. */
    public double calcDistance(Planet p) {
        double dx = p.xxPos - xxPos;
        double dy = p.yyPos - yyPos;
        double r2 = dx * dx + dy * dy;
        return Math.sqrt(r2);
    }

    /** Return the force extered on this planet by the given planet */
    public double calcForceExertedBy(Planet p) {
        double r = calcDistance(p);
        double force = G * mass * p.mass / (r * r);
        return force;
    }

    public double calcForceExertedByX(Planet p) {
        double fx = calcForceExertedBy(p) * (p.xxPos - xxPos) / calcDistance(p);
        return fx;
    }

    public double calcForceExertedByY(Planet p) {
        double fy = calcForceExertedBy(p) * (p.yyPos - yyPos) / calcDistance(p);
        return fy;
    }

    public double calcNetForceExertedByX(Planet[] ps) {
        double netFx = 0;
        for (Planet p : ps) {
            if (!this.equals(p)) {
                netFx += calcForceExertedByX(p);
            }
        }
        return netFx;
    }

    public double calcNetForceExertedByY(Planet[] ps) {
        double netFy = 0;
        for (Planet p : ps) {
            if (!this.equals(p)) {
                netFy += calcForceExertedByY(p);
            }
        }
        return netFy;
    }

    public void update(double t, double fx, double fy) {
        double netAx = fx / mass;
        double netAy = fy / mass;
        xxVel = xxVel + t * netAx;
        yyVel = yyVel + t * netAy;
        xxPos = xxPos + t * xxVel;
        yyPos = yyPos + t * yyVel;
    }
}
