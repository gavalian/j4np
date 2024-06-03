/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.central2;

import j4np.geom.prim.Line3D;
import j4np.geom.prim.Point3D;
import j4np.geom.prim.Vector3D;
import j4np.hipo5.data.Bank;

/**
 *
 * @author gavalian
 */
public class Segment {
    
    public int[]  reference = new int[2];
    public int       status = 0;
    public int         ring = 0;
    public int       sector = 0;
    public Point3D midpoint = new Point3D();

    public Segment(int down, int up){
        reference[0] = down; reference[1] = up;
    }
    
    public void update(Bank b){
        Line3D up = new Line3D(
                b.getFloat("xo", reference[0]), 
                b.getFloat("yo", reference[0]),
                b.getFloat("zo", reference[0]),
                b.getFloat("xe", reference[0]), 
                b.getFloat("ye", reference[0]),
                b.getFloat("ze", reference[0])
        );
        Line3D down = new Line3D(
                b.getFloat("xo", reference[1]), 
                b.getFloat("yo", reference[1]),
                b.getFloat("zo", reference[1]),
                b.getFloat("xe", reference[1]), 
                b.getFloat("ye", reference[1]),
                b.getFloat("ze", reference[1])
        );
        Line3D cross = up.distanceSegments(down);
        midpoint = cross.midpoint();
        int s1 = b.getInt("status", reference[0]);
        int s2 = b.getInt("status", reference[1]);
        ring   = b.getInt("layer", reference[0]);
        sector = b.getInt("sector", reference[0]);
        if(s1>0&&s2>0) status = 1; else status = 0;
    }
    public boolean isValid(){
        if(ring==1) return (midpoint.z()>-219&&midpoint.z()<113);
        if(ring==3) return (midpoint.z()>-180&&midpoint.z()<153);
        if(ring==5) return (midpoint.z()>-141&&midpoint.z()<192);
        return false;
    }
    
    public double costheta() {
        final double tol = 1e-6;
        double l = Math.sqrt(midpoint.x()*midpoint.x()+
                midpoint.y()*midpoint.y()+midpoint.z()*midpoint.z());
        double ret;
        if (l < (2.*tol)) { ret = 1.0;} else {
            ret = midpoint.z() / l;
        }
        return ret;
    }
    public double theta(){ return Math.acos(costheta());}
    public double phi(){ return Math.atan2(midpoint.y(), midpoint.x());}
    
    @Override
    public String toString(){
        Vector3D vec = midpoint.toVector3D();
        return String.format("[%3d, %3d] (%3d) {%2d, %2d}  [%12.5f %12.5f %12.5f] [%12.5f %12.5f]", 
                reference[0],reference[1], status, ring,sector, midpoint.x(),midpoint.y(),midpoint.z(),
                Math.toDegrees(vec.theta()), Math.toDegrees(vec.phi()));
    }
    
}
