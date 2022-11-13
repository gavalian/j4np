/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.geom.detector;

import j4np.geom.prim.Line3D;
import j4np.geom.prim.Path3D;
import j4np.geom.prim.Point3D;
import j4np.geom.prim.Shape3D;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author gavalian
 */
public class Layer {
    
    protected int layer_id = 0;
    
    List<Shape3D> boundary = new ArrayList<>();
    List<Line3D>  components = new ArrayList<>();
    
    public List<Line3D>   getComponents(){return this.components;}
    public List<Shape3D>  getBoundary(){return this.boundary;}
    public Layer setId(int id){layer_id = id;return this;}
    public int   getId(){return layer_id;}
    
    public boolean hasIntersection(Path3D p){
        List<Point3D> intersects = new ArrayList<>();
        for(Shape3D shape : boundary){
            shape.intersection(p, intersects);
            if(intersects.size()>0){
                //System.out.println("intersection " + intersects.get(0));
                return true;
            }
        }
        return false;
    }
    
    
    public List<DetectorHit>  getHits(Path3D path){
        
        List<DetectorHit> hits = new ArrayList<>();
        
       if(hasIntersection(path)==true){
           
           int[] index = path.getClosest(components);
           Line3D  dir = path.getLine(index[0]);
           Line3D  dst = dir.distanceSegments(this.components.get(index[1]));
           DetectorHit hit = new DetectorHit(layer_id,index[1]+1);
           //System.out.println("path line = " + line);
           hit.getPosition().copy(dst.midpoint());
           hit.getLine().copy(this.components.get(index[1]));
           hit.setDistance(dst.length());
           hits.add(hit);
       }
       return hits;
    }
}
