/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.geom.detector;

import j4np.geom.prim.Line3D;
import j4np.geom.prim.Point3D;

/**
 *
 * @author gavalian
 */
public class DetectorHit {
    protected int identifier    = 0;
    protected int[] keys = null;
    protected Point3D position = new Point3D();
    protected Line3D  component = new Line3D();
    
    protected double  distance = 100000.0;
    
    public DetectorHit(int... identifiers){
        keys = new int[identifiers.length];
        for(int i = 0; i < keys.length; i++) keys[i] = identifiers[i];
    }
    
    public int[]   getKeys(){return keys;}
    public Point3D getPosition(){return position;}
    public Line3D  getLine(){return component;}
    public double  getDistance(){return this.distance;}
    public void    setDistance(double d){this.distance = d;}
    public String  keysString(){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < keys.length; i++) str.append(String.format("%5d ", keys[i]));
        return str.toString();
    }
    
    @Override
    public String toString(){
        return String.format("%s : %s : d = %8.5f", this.keysString(),position.toString(),distance);
    }
}
