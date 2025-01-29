package j4np.geom.prim;

import java.util.ArrayList;
import java.util.List;

/** 
 * A polyline represented by a sequence of points. Polylines are useful for 
 * representing the path of a particle.
 * <p>
 * Points in the polyline can added or retrieved via 
 * {@link #addPoint(org.jlab.geom.prim.Point3D)} and {@link #point(int)}.
 * Alternatively, polylines can be interpreted as a sequence of lines accessible
 * via {@link #getLine(int)}.
 * <p>
 * For testing purposes, polylines can be randomly generated using the 
 * {@link #generate(org.jlab.geom.prim.Point3D, org.jlab.geom.prim.Vector3D, double, int)} and
 * {@link #generateRandom(org.jlab.geom.prim.Point3D, double, double, double, double, double, int)} methods.
 * 
 * @author gavalian
 */
public class Path3D implements Transformable, Showable {

    private final List<Point3D> points = new ArrayList(); // points of the path

    /**
     * Constructs a new empty {@code Path3D}.
     */
    public Path3D() {
        // nothing to do
    }

    /**
     * Constructs a new {@code Path3D} from the given points.
     *
     * @param points the points to copy into the new {@code Path3D}
     */
    public Path3D(Point3D... points) {
        for (Point3D point : points) {
            this.points.add(new Point3D(point));
        }
    }

    /**
     * Adds a point to the path with given coordinates (x,y,z).
     *
     * @param x x coordinate of the point
     * @param y y coordinate of the point
     * @param z z coordinate of the point
     */
    public void addPoint(double x, double y, double z) {
        points.add(new Point3D(x, y, z));
    }

    /**
     * Adds a point to the path with same coordinates as the given point.
     *
     * @param point point to be added to the path.
     */
    public void addPoint(Point3D point) {
        addPoint(point.x(), point.y(), point.z());
    }

    /**
     * Returns the number of points in this path.
     *
     * @return the number of points in this path
     */
    public int size() {
        return points.size();
    }

    /**
     * Returns the point at the given index or null if there is no such point.
     *
     * @param index the index of the point to return
     * @return the point at the given index or null if there is no such point
     */
    public Point3D point(int index) {
        if (index < 0 || points.size() <= index) {
            System.err.println("Warning: Point3D: point(int index): invalid index=" + index);
            return null;
        }
        return points.get(index);
    }

    /**
     * Clears the path so that it has no points and size() == 0.
     */
    public void clear() {
        points.clear();
    }

    /**
     * Returns the number of lines contained in this path.
     *
     * @return the number of lines
     */
    public int getNumLines() {
        return Math.max(points.size() - 1, 0);
    }

    /**
     * Constructs a new {@code Line3D} from the point at the given index to the
     * point with the next larger index. Returns null if such a line cannot be
     * constructed.
     *
     * @param index the index of the point at the origin of the line
     * @return the line from the point at the given index to the next point or
     * null if no such line can be constructed
     */
    public Line3D getLine(int index) {
        if (index < 0 || points.size() - 1 <= index) {
            System.err.println("Warning: Point3D: getLine(int index): invalid index=" + index);
            return null;
        }
        return new Line3D(points.get(index), points.get(index + 1));
    }

    public int[]  getClosest(List<Line3D> array){
        if(array.isEmpty())return new int[]{-1,-1};
        int   pathIndex = 0;
        int  arrayIndex = 0;
        
        Line3D pl = new Line3D();
        this.getLine(pl, 0);
        Line3D dist = pl.distanceSegments(array.get(0));
        double distance = dist.length();
        
        for(int i = 0; i<array.size();i++){
            for(int p = 0; p < this.getNumLines(); p++){
                this.getLine(pl, p);
                dist = pl.distanceSegments(array.get(i));
                //System.out.println("distance = " + dist.length() + "  orig = " + closest.length());
                if(dist.length()<distance){
                    distance = dist.length();
                    pathIndex = p; arrayIndex = i;
                }
            }
        }
        return new int[]{pathIndex,arrayIndex};
    }
    
    public Line3D getIntersection(Line3D line){
        Line3D   result = new Line3D();
        Line3D  segment = new Line3D();
        
        getLine(segment,0);
        Line3D    inter = line.distanceSegments(segment);
        double distance = inter.length();
        result.copy(inter);
        for(int i = 0; i < this.getNumLines(); i++){
            getLine(segment,i);
            inter = line.distanceSegments(segment);
            if(inter.length()<distance){
                distance = inter.length(); result.copy(inter);
            }
        }
        return result;
    }
    /*
    public Line3D getIntersection(List<Line3D> lines){
        
        Line3D   result = new Line3D();
        Line3D  segment = new Line3D();        
        getLine(segment,0);
        Line3D    inter = line.distanceSegments(segment);
        double distance = inter.length();
        result.copy(inter);
        for(int i = 0; i < this.getNumLines(); i++){
            getLine(segment,i);
            inter = line.distanceSegments(segment);
            if(inter.length()<distance){
                distance = inter.length(); result.copy(inter);
            }
        }
        return result;
    }*/
    
    public Line3D findClosest(List<Line3D> array){
        if(array.isEmpty()) return new Line3D(0,0,0,1000,1000,1000);
        Line3D pl = new Line3D();
        //Line3D closest = new Line3D(0,0,0,1000,1000,1000);
        this.getLine(pl, 0);
        Line3D closest = pl.distanceSegments(array.get(0));
        
        for(int i =1; i<array.size();i++){
            for(int p = 0; p < this.getNumLines(); p++){
                this.getLine(pl, p);
                Line3D dist = pl.distanceSegments(array.get(i));
                //System.out.println("distance = " + dist.length() + "  orig = " + closest.length());
                if(dist.length()<closest.length()){
                    closest.copy(dist);
                }
            }
        }
        return closest;
    }
    /**
     * Constructs a new {@code Line3D} from the point at the given index to the
     * point with the next larger index.Returns null if such a line cannot be
 constructed.
     *
     * @param line line to copy points to
     * @param index the index of the point at the origin of the line
     * @return the line from the point at the given index to the next point or
     * null if no such line can be constructed
     */
    public Line3D getLine(Line3D line, int index) {
        if (index < 0 || points.size() - 1 <= index) {
            System.err.println("Warning: Point3D: getLine(int index): invalid index=" + index);
            return null;
        }
        line.origin().copy(points.get(index));
        line.end().copy(points.get(index+1));
        
        return line;//new Line3D(points.get(index), points.get(index + 1));
    }
    /**
     * Constructs a new {@code Line3D} from a line contained in this path to the
     * given point such that the length of the constructed line is minimal.
     *
     * @param point the point
     * @return the line with minimum distance from the path to the point, or
     * null if this path contains fewer than 2 points
     */
    public Line3D distance(Point3D point) {
        Line3D shortestLine = null;
        for (int i = 0; i < points.size() - 1; i++) {
            Line3D pathLine = getLine(i);
            Line3D line = pathLine.distanceSegment(point);
            if (shortestLine == null || line.length() < shortestLine.length()) {
                shortestLine = line;
            }
        }
        return shortestLine;
    }

    public Line3D distance(double x, double y, double z) {
        Line3D shortestLine = null;
        Point3D point = new Point3D(x,y,z);
        for (int i = 0; i < points.size() - 1; i++) {
            Line3D pathLine = getLine(i);
            Line3D line = pathLine.distanceSegment(point);
            if (shortestLine == null || line.length() < shortestLine.length()) {
                shortestLine = line;
            }
        }
        return shortestLine;
    }
    /**
     * Constructs a new {@code Line3D} from a line contained in this path to the
     * given point such that the length of the constructed line is minimal.
     *
     * @param line the line
     * @return the line with minimum distance from the path to the point, or
     * null if this path contains fewer than 2 points
     */
    public Line3D distance(Line3D pl) {
        Line3D shortestLine = null;
        for (int i = 0; i < points.size() - 1; i++) {
            Line3D pathLine = getLine(i);
            Line3D line = pathLine.distanceSegments(pl);//.distanceSegments(line);
            if (shortestLine == null || line.length() < shortestLine.length()) {
                shortestLine = line;
            }
        }
        return shortestLine;
    }
    
    /**
     * Finds the index of the closest point in this path to the given point.
     *
     * @param point the point
     * @return the index of the closest points in this path or -1 if this path
     * contains no points
     */
    public int closestNodeIndex(Point3D point) {
        if (size() == 0) {
            System.err.println("Warning: Path3D: closestNodeIndex(Point3D point): the path is empty");
        }
        int closest = -1;
        double minDist = Double.POSITIVE_INFINITY;
        for (int index = 0; index < points.size(); index++) {
            double dist = points.get(index).distance(point);
            if (dist < minDist) {
                closest = index;
                minDist = dist;
            }
        }
        return closest;
    }

    /**
     * Returns the length of the path starting at the origin and using the
     * specified number points. If the given number of points exceeds the number
     * of points in this path then the total path length is returned.
     *
     * @param numPoints number of points to use in the calculation
     * @return the length
     */
    public double length(int numPoints) {
        int stop = Math.min(numPoints, points.size()) - 1;
        double len = 0.0;
        for (int pointIndex = 0; pointIndex < stop; pointIndex++) {
            len += points.get(pointIndex).distance(points.get(pointIndex + 1));
        }
        return len;
    }

    /**
     * Modifies the contents of this {@code Path3D} such that the path contains
     * the specified number of evenly spaced points, the origin of the path is
     * at (px,py,pz), the points are collinear along the vector (vx,vy,vz), and
     * the total length of the path is equal to the given length.
     *
     * @param px x coordinate of the origin
     * @param py y coordinate of the origin
     * @param pz z coordinate of the origin
     * @param vx direction vector x component
     * @param vy direction vector y component
     * @param vz direction vector z component
     * @param length length of the path to generate
     * @param npoints number of nodes in the path
     */
    public void generate(double px, double py, double pz,
            double vx, double vy, double vz, double length, int npoints) {
        generate(new Point3D(px, py, pz),
                new Vector3D(vx, vy, vz),
                length,
                npoints);
    }

    /**
     * Modifies the contents of this {@code Path3D} such that the path contains
     * the specified number of evenly spaced points, the origin of the path is
     * at the given origin point, the points are collinear along the given
     * direction vector, and the total length of the path is equal to the given
     * length.
     *
     * @param origin vector of the origin point
     * @param direction direction vector
     * @param length path length
     * @param npoints number of nodes in the path
     */
    public void generate(Point3D origin, Vector3D direction, double length, int npoints) {
        clear();
        addPoint(origin);
        Point3D point = new Point3D();
        for (int p = 1; p < npoints; p++) {
            direction.setMag(p * length / (npoints - 1));
            point.set(origin, direction);
            addPoint(point);
        }
    }

    /**
     * Modifies the contents of this {@code Path3D} such that the path contains
     * the specified number of evenly spaced points, the origin of the path is
     * at the given origin point, the points are collinear along a randomly
     * generated vector bounded by theta min and max and phi min and max, and
     * the total length of the path is equal to the given length.
     *
     * @param px origin x coordinate
     * @param py origin y coordinate
     * @param pz origin z coordinate
     * @param thetaMin minimum theta angle in radians
     * @param thetaMax maximum theta angle in radians
     * @param phiMin minimum phi angle in radians
     * @param phiMax maximum phi angle in radians
     * @param length length of the path in cm
     * @param npoints number of points along the path
     */
    public void generateRandom(double px, double py, double pz,
            double thetaMin, double thetaMax, double phiMin, double phiMax,
            double length, int npoints) {
        generateRandom(new Point3D(px, py, pz),
                thetaMin, thetaMax, phiMin, phiMax, length, npoints);
    }

    /**
     * Modifies the contents of this {@code Path3D} such that the path contains
     * the specified number of evenly spaced points, the origin of the path is
     * at the given origin point, the points are collinear along a randomly
     * generated vector bounded by theta min and max and phi min and max, and
     * the total length of the path is equal to the given length.
     *
     * @param origin the origin point
     * @param thetaMin minimum theta angle in radians
     * @param thetaMax maximum theta angle in radians
     * @param phiMin minimum phi angle in radians
     * @param phiMax maximum phi angle in radians
     * @param length length of the path in cm
     * @param npoints number of points along the path
     */
    public void generateRandom(Point3D origin,
            double thetaMin, double thetaMax, double phiMin, double phiMax,
            double length, int npoints) {
        
        //double theta = Math.random() * (thetaMax - thetaMin) + thetaMin;
        double cosThetaMax = Math.cos(thetaMin);
        double cosThetaMin = Math.cos(thetaMax);
        double cosTheta = Math.random()*Math.abs(cosThetaMax-cosThetaMin) + cosThetaMin;
        double theta    = Math.acos(cosTheta);
        double   phi = Math.random() * (phiMax - phiMin) + phiMin;
        
        Vector3D direction = new Vector3D();
        direction.setMagThetaPhi(1, theta, phi);
        generate(origin, direction, length, npoints);
    }

    @Override
    public void translateXYZ(double dx, double dy, double dz) {
        for (Point3D p : points) {
            p.translateXYZ(dx, dy, dz);
        }
    }

    @Override
    public void rotateX(double angle) {
        for (Point3D p : points) {
            p.rotateX(angle);
        }
    }

    @Override
    public void rotateY(double angle) {
        for (Point3D p : points) {
            p.rotateY(angle);
        }
    }

    @Override
    public void rotateZ(double angle) {
        for (Point3D p : points) {
            p.rotateZ(angle);
        }
    }

    /**
     * Invokes {@code System.out.println(this)}.
     */
    @Override
    public void show() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Path3D:\n");
        for (Point3D point : points) {
            str.append("\t");
            str.append(point);
            str.append("\n");
        }
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }
}
