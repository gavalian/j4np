package j4np.geom.prim;

import java.util.List;

/**
 * A 3D triangle represented by three points.
 * <p>
 * Since any three points in 3D space that are not collinear define a plane,
 * a face can be converted into a plane via {@link #plane()}.
 * <p>
 * The normal of the surface of a face is oriented such that when looking
 * antiparallel to the normal towards the face the face's points wound
 * counterclockwise. Conversely, when looking in a direction parallel to the
 * normal, the points are wound in a clockwise fashion.
 * <p>
 * The intersection of a line with a face can be calculated using the
 * intersection methods:
 * {@link #intersection(org.jlab.geom.prim.Line3D, org.jlab.geom.prim.Point3D) intersection(...)}, 
 * {@link #intersectionRay(org.jlab.geom.prim.Line3D, org.jlab.geom.prim.Point3D) intersectionRay(...)}, 
 * {@link #intersectionSegment(org.jlab.geom.prim.Line3D, org.jlab.geom.prim.Point3D) intersectionSegment(...)}.
 *
 * @author gavalian
 */
public interface Face3D extends Transformable, Showable {
    /**
     * Returns the point from this {@code Face3D} with corresponding index. If
     * an invalid index is given, then null is returned.
     *
     * @param index index of the point
     * @return the point at the corresponding index, otherwise null
     */
    Point3D point(int index);
    
    /**
     * Finds the intersections of the given infinite line with this
     * {@code Face3D}. If intersections are found they will be appended to the
     * given list. The return value will indicate the number of intersections
     * that were found.
     * @param line the infinite line
     * @param intersections the list to store the intersections in
     * @return the number of intersections found
     */
    int intersection(Line3D line, List<Point3D> intersections);

    /**
     * Finds the intersections of the given ray with this
     * {@code Face3D}. If intersections are found they will be appended to the
     * given list. The return value will indicate the number of intersections
     * that were found.
     * @param line the ray
     * @param intersections the list to store the intersections in
     * @return the number of intersections found
     */
    int intersectionRay(Line3D line, List<Point3D> intersections);
    
    /**
     * Finds the intersections of the given line segment with this
     * {@code Face3D}. If intersections are found they will be appended to the
     * given list. The return value will indicate the number of intersections
     * that were found.
     *
     * @param line the line segment
     * @param intersections the list to store the intersections in
     * @return the number of intersections found
     */
    int intersectionSegment(Line3D line, List<Point3D> intersections);
    
    /**
     * returns a line that will be the reflection of the line when hitting the 
     * boundary of the face. This method will be extended in Triangle3D class.
     * @param line the incoming ray, that intersects with the triangle
     * @param reflection the outgoing reflected ray from the surface
     * @return 0 if the incoming ray is not intersecting with the face, 
     *   and 1 if otherwise.
     */
    int reflection(Line3D line, Line3D reflection);
}
