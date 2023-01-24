/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.rich;

import j4np.geom.prim.Line3D;
import j4np.geom.prim.Point3D;
import j4np.geom.prim.Vector3D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import twig.data.Range;

/**
 *
 * @author gavalian
 */
public class RichParticle {
    
    private Vector3D vector = new Vector3D();
    private Point3D vertex   = new Point3D();
    private Random r = new Random();
    private List<RichHit>  hits = new ArrayList<>();
    private Line3D intersection = new Line3D();

    private Range  pRange = new Range(2.5,7.5);
    private Range  cosRange = new Range(0.0,1.0);
    private Range  phiRange = new Range(0.0,1.0);
    
    
    private Vector3D xAxis = new Vector3D(1.0,0.0,0.0);
    private Vector3D yAxis = new Vector3D(0.0,1.0,0.0);
    private Vector3D zAxis = new Vector3D(0.0,0.0,1.0);
    
    public RichParticle(){}
    
    public Line3D getLine(){ return new Line3D(vertex,vector);}
    public Line3D intersection(){ return intersection;}
    public List<RichHit> getHits(){ return hits;}

    public Range xRange = new Range(-0.242 ,  0.242);
    public Range yRange = new Range(-0.242 ,  0.242);
    public Range zRange = new Range(0.9 , 1.0);
    public Range hitRange = new Range(-0.5,0.5);
    
    public RichParticle thetaRange(double min, double max){
        double cosMin = Math.cos(min);
        double cosMax = Math.cos(max);
        if(cosMin<cosMax) cosRange.set(cosMin, cosMax);
        else cosRange.set(cosMax,cosMin); return this;
    }
    
    public RichParticle phiRange(double min, double max){
        phiRange.set(min, max); return this;
    }
    
    public RichParticle momRange(double min, double max){ 
        pRange.set(min, max); return this;
    }
    
    public void random(){
        double   p = pRange.min()+r.nextDouble()*pRange.length();
        double cos = cosRange.min()+r.nextDouble()*cosRange.length();
        double phi = phiRange.min()+r.nextDouble()*phiRange.length();
        vector.setMagThetaPhi(p, Math.acos(cos), phi);
    }
    
    public Vector3D vector(){ return this.vector;}
    
    
    public Vector3D getDirection(){
        Vector3D vec = this.intersection.direction();
        vec.unit();
        return new Vector3D(vec.dot(xAxis),vec.dot(yAxis),vec.dot(zAxis));
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("rich-particle: (%9.5f) theta = %9.4f, phi = %9.4f\n", 
                vector.mag(),Math.toDegrees(vector.theta()), Math.toDegrees(vector.phi())));
        str.append(String.format("     intersec:  ( x = %9.4f , y = %9.4f, z = %9.4f)\n", 
                intersection.origin().x(),intersection.origin().y(),
                intersection.origin().z()));
        str.append(String.format("     endpoint:  ( x = %9.4f , y = %9.4f, z = %9.4f)\n", 
                intersection.end().x(),intersection.end().y(),
                intersection.end().z()));
        Vector3D dir = this.getDirection();
        str.append(String.format("     endpoint:  ( x = %9.4f , y = %9.4f, z = %9.4f)\n", 
                dir.x(),dir.y(),
                dir.z()));
        
        str.append(String.format("\tn hits = %d\n", hits.size()));
        for(RichHit hit : hits) str.append(String.format("\t\t-> %9.4f %9.4f %9.4f\n", 
                hit.position().x(),hit.position().y(),hit.position().z())
                );
        return str.toString();
    }
    
    public List<String> csvSting(){
        List<String> lines = new ArrayList<>();
        Vector3D d = this.getDirection();
        for(int i = 0; i < hits.size(); i++){
          lines.add( String.format("%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f", 
                pRange.translate(this.vector.mag()),
                hitRange.translate(this.intersection.origin().x()),
                hitRange.translate(this.intersection.origin().y()),
                xRange.translate(d.x()),yRange.translate(d.y()),
                zRange.translate(d.z()),
                hitRange.translate(hits.get(i).position().x()),
                hitRange.translate(hits.get(i).position().y())
                
                ));
        }
        return lines;
    }
}
