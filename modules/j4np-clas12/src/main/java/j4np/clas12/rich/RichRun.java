/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.rich;

import j4np.geom.prim.Line3D;
import j4np.geom.prim.Point3D;
import j4np.geom.prim.Vector3D;
import j4np.utils.io.TextFileWriter;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import twig.data.DataRange;
import twig.data.H1F;
import twig.data.H2F;
import twig.data.Range;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class RichRun {
    
    public static Line3D getLine(Line3D line, double phi, double theta){
        
        Vector3D ex = new Vector3D(1,0.0,0.0);
        ex.rotateZ(phi);
        
        Vector3D ldir = line.toVector();
        ldir.unit();
        
        Vector3D n = ldir.cross(ex);
        n.unit();
        
        double move = 1.0/Math.tan(theta);
        Line3D   original = new Line3D(line.origin(),ldir);
        Point3D     ortho = original.lerpPoint(move);
        Line3D perp = new Line3D(ortho,n);
        
        return new Line3D(line.origin().x(),line.origin().y(),line.origin().z(),
                perp.end().x(),perp.end().y(),perp.end().z());
    }
    
    public static void main(String[] args){
        /*
        Line3D line = new Line3D(0.,0.,0.,1.0,0.0,1.0);
        
        line.show();
        
        Line3D line2 = RichRun.getLine(line, Math.toRadians(0.0), Math.toRadians(15.0));
        
        line2.show();
        
        double dot = line.toVector().dot(line2.toVector())/(line2.toVector().mag()*line.toVector().mag());
        System.out.println(" dot = " + Math.toDegrees(Math.acos(dot)));
        */
        
        int sensorBins = 120;
        Random r = new Random();
        
        RichGeometry geom = new RichGeometry();
        geom.move(0, 0, 2.0);
        
        
        RichParticle part = new RichParticle();
        part.momRange(2.5, 7.0)
                .thetaRange(Math.toRadians(0.0), Math.toRadians(25))
                .phiRange(0.0, 2.0*Math.PI);
                
        TGCanvas c = new TGCanvas(900,900);
        c.view().initTimer(1000);
        
        H2F h = new H2F("h",sensorBins,-0.62,0.62,sensorBins,-0.62,0.62);
        H1F[] d = new H1F[3];
        for(int i = 0; i < d.length; i++) d[i] = new H1F("d",120,-1,1);

        c.view().divide(2, 2);
        c.cd(0).draw(h);
        for(int k = 0; k < d.length; k++) c.cd(k+1).draw(d[k]);
        //Line3D line = new Line3D(0,0.2,2.0,1.5,0.,10.0);
        double angle = 20.0;
        
        int iter = 70000;
        long then = System.currentTimeMillis();

        DataRange rxy = new DataRange(-0.001,0.0001,-0.00001,0.0001);
        DataRange rxz = new DataRange(-0.001,0.0001,0.9,0.9002);
        TextFileWriter w = new TextFileWriter();
        w.open("trainingdata.csv");
        Range rt = new Range(0.23,0.32);
        
        for(int i = 0; i < iter; i++){
            
            part.random();
            geom.process(part);
            //System.out.println(part);
            double phi = r.nextDouble()*2.0*Math.PI;
            if(part.intersection().end().z()>0){
                
                double mom = part.vector().mag();
                double theta = RichGeometry.getRingTheta(0.139, mom);
                //double theta = RichGeometry.getRingTheta(0.497, mom);
                
                geom.getHits(r, part, theta, 50);
                
                h.reset();
                for(int kk = 0; kk < part.getHits().size(); kk++){
                    h.fill(
                            part.getHits().get(kk).position().x(),
                            part.getHits().get(kk).position().y()
                            );
                }
                
                                
                Vector3D dir = part.getDirection();
                rxy.grow(dir.x(), dir.y());
                rxz.grow(dir.x(), dir.z());
                
                d[0].fill(dir.x());
                d[1].fill(dir.y());
                d[2].fill(dir.z());
                
                if(part.getHits().size()>0){
                    List<String> lines = part.csvSting();
                    for(String line : lines) 
                        w.writeString(line+","+String.format("%.6f", rt.translate(theta)));//,rt.translate(thetapi)));
                }
                
                //c.repaint();
                //System.out.println(part);
            }
            //ray.show();
            //Line3D line = Line3D.generate(0.0, 0.0, 0.0, 0.0, Math.PI/5.0, 0.0,2.0*Math.PI);
            //Line3D ray = RichRun.getLine(line, phi, Math.toRadians(angle));
            //Point3D point = geom.getSensorCross(ray);
            //Point3D point = geom.getHit(ray);
            //if(point!=null){
                //point.show();
                //h.fill(point.x(), point.y());
            //}
            //line.show();
            /*try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(RichRun.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            //System.out.println("next event");
            
        }
        w.close();
        long now = System.currentTimeMillis();
        //geom.sensors.show();
        System.out.printf("---- time = %d msec, iter = %d, average %f msec/track\n",
                now-then, iter, ( (double) (now-then)) /iter);
    
        System.out.println(rxy);
        System.out.println(rxz);
    }
}
