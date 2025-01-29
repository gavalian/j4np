/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.publish;

import java.util.ArrayList;
import java.util.List;
import twig.data.Graph;
import twig.graphics.TGCanvas;
import twig.widgets.Polygon;

/**
 *
 * @author gavalian
 */
public class DetectorDiagrams {
    static double startTheta = 5;
    static double endTheta = 55;
    public static List<Polygon> create(double r, double theta_start, double theta_end, int ntheta, int nz, double rz, int fc, int lc){
        List<Polygon> calo = new ArrayList<>();
        double step = Math.toRadians(theta_end - theta_start)/ntheta;
        for(int z = 0; z < nz; z++){
            for(int t = 0; t < ntheta; t++){
                double rt = r+z*rz;
                Polygon p = new Polygon();
                p.attrFill().setFillColor(fc);
                p.attrLine().setLineColor(lc);
                double theta1 = t*step;
                double theta2 = (t+1)*step;
                p.addPoint(rt*Math.cos(theta1), rt*Math.sin(theta1));
                p.addPoint((rt+rz)*Math.cos(theta1), (rt+rz)*Math.sin(theta1));
                p.addPoint((rt+rz)*Math.cos(theta2), (rt+rz)*Math.sin(theta2));
                p.addPoint(rt*Math.cos(theta2), rt*Math.sin(theta2));
                p.addPoint(rt*Math.cos(theta1), rt*Math.sin(theta1));
                calo.add(p);
            }
        }        
        return calo;
    }
    
    public static List<Polygon> getCALO(){
        return DetectorDiagrams.create(0.78, startTheta, endTheta, 36, 9, 0.025,35,71);
    }
    public static List<Polygon> getHTCC(){
        return DetectorDiagrams.create(0.20, startTheta, endTheta, 4, 2, 0.025,52,71);
    }
    
    public static List<Polygon> getDC(){
        List<Polygon> dc = new ArrayList<>();
        List<Polygon> drift3_1 = DetectorDiagrams.create(0.68, startTheta, endTheta, 36, 6, 0.012, 31,61);
        List<Polygon> drift3_2 = DetectorDiagrams.create(0.60, startTheta, endTheta, 36, 6, 0.012, 41,61);
        
        List<Polygon> drift2_1 = DetectorDiagrams.create(0.52, startTheta, endTheta, 24, 6, 0.012, 31,61);
        List<Polygon> drift2_2 = DetectorDiagrams.create(0.44, startTheta, endTheta, 24, 6, 0.012, 41,61);
        
        List<Polygon> drift1_1 = DetectorDiagrams.create(0.36, startTheta, endTheta, 18, 6, 0.012, 31,61);
        List<Polygon> drift1_2 = DetectorDiagrams.create(0.28, startTheta, endTheta, 18, 6, 0.012, 41,61);
        
        dc.addAll(drift3_1);
        dc.addAll(drift3_2);
        dc.addAll(drift2_1);
        dc.addAll(drift2_2);
        dc.addAll(drift1_1);
        dc.addAll(drift1_2);
        return dc;
    }
    public static Graph getTrack(){
        Graph g = new Graph();
        
        return g;
    }
    
    public static void draw(){
        TGCanvas c = new TGCanvas(800,800);
        c.region(0).setBackgroundColor(0, 0, 0, 0);
        List<Polygon> calo = DetectorDiagrams.getCALO();  
        calo.get(2).attrFill().setFillColor(85);
        calo.get(3).attrFill().setFillColor(85);
        
        for(Polygon p1 : calo) c.draw(p1); 
        
        List<Polygon> dc = DetectorDiagrams.getDC();
        for(Polygon p2 : dc) c.draw(p2); 
        
        List<Polygon> htcc = DetectorDiagrams.getHTCC();
        htcc.get(2).attrFill().setFillColor(82);
        htcc.get(6).attrFill().setFillColor(82);
        for(Polygon p2 : htcc) c.draw(p2); 
        
    }
    public static void main(String[] args){
        DetectorDiagrams.draw();
    }
}
