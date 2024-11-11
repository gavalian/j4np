/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.central;

import j4np.geom.prim.Line3D;
import j4np.geom.prim.Path3D;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import twig.data.GraphErrors;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class Detector {
    double  offset = 20.0;
    double     gap =  5.0;
    List<Layer> detector = new ArrayList<>();
    
    public Detector(){
        
    }
    
    
    public Layer generateLayer(int n){
        double r = n*gap + offset;
        double step = 1.0;
        Layer layer = new Layer(n);
        for(int i = 0; i < 360; i+=2.0){
            double angle = Math.toRadians((double)i);
            layer.getWires().add(
                    new Line3D(
                            r*Math.cos(angle),r*Math.sin(angle),-100,
                            r*Math.cos(angle),r*Math.sin(angle), 100
                    ));
        }
        
        for(int i = 0; i < layer.getWires().size(); i++){
            double angle = n%2==0?3:-3;
            layer.getWires().get(i).origin().rotateZ(Math.toRadians(angle));
            layer.getWires().get(i).end().rotateZ(Math.toRadians(-angle));
        }
        return layer;
    }
    
    public void generate(){
        detector.clear();
        for(int i = 1; i <=8; i++){
            detector.add(this.generateLayer(i));
        }
    }
    
    public void tracks(){
        Random r = new Random();
        double len = 500;
        TextFileWriter w = new TextFileWriter("llm.txt");
        
        for(int k = 0; k < 60000; k++){
            double  phi = r.nextDouble()*Math.PI*2.0-Math.PI;
            double   th = r.nextDouble()*Math.PI/2.0 + Math.PI/4.0;
            Line3D line = new Line3D(0.0,0.0,0.0,len*Math.cos(phi)*Math.sin(th), len*Math.sin(phi)*Math.sin(th),
                    len*Math.cos(th)); 
            //System.out.printf(" phi = %f, theta = %f\n",Math.toDegrees(phi),Math.toDegrees(th));
            List<Integer> nh = new ArrayList<>();
            for(int i = 0; i < detector.size(); i++){
                int which = detector.get(i).getIntersection(line);
                //System.out.printf("-- %5d sensor = %6d\n",i+1,which);
                nh.add((i+1)*1000+which);
            }
            StringBuilder str = new StringBuilder();
            for(int i = 0; i < nh.size(); i++) { System.out.printf("%5d ",nh.get(i)); str.append(String.format("%5d ",nh.get(i)));}
            System.out.printf(" 1\n"); str.append(" 1");
            
            w.writeString(str.toString());
            
            int replace = r.nextInt(8);
            int    with = r.nextInt(180);
            StringBuilder str2 = new StringBuilder();
            nh.set(replace, (replace+1)*1000+with);
            for(int i = 0; i < nh.size(); i++){ System.out.printf("%5d ",nh.get(i)); str2.append(String.format("%5d ",nh.get(i)));}
            System.out.printf(" 0\n"); str2.append(" 0");
            w.writeString(str2.toString());
        }
        
        w.close();
    }
    
    public static class Swimmer {
        public static GraphErrors swim(){
        // Constants
        double q = 1.0;  // Charge of the particle (Coulombs)
        double m = 1.0;  // Mass of the particle (kilograms)
        double B = 1.0;  // Magnetic field strength (Tesla)
        double v = 1.0;  // Initial velocity magnitude (m/s)
        double omega = q * B / m;  // Cyclotron frequency (rad/s)
        double timeStep = 0.01;  // Time step for simulation (seconds)
        double totalTime = 10.0;  // Total simulation time (seconds)
        
        // Initial position and velocity
        double x = 0.0, y = 0.0, z = 0.0;
        double vx = v, vy = 0.0, vz = 0.1;  // Small vz to demonstrate helical motion
        GraphErrors g = new GraphErrors();
        System.out.printf("Time\t\tX\t\tY\t\tZ\n");
        for (double t = 0; t <= totalTime; t += timeStep) {
            // Print the current position
            System.out.printf("%.2f\t\t%.4f\t%.4f\t%.4f\n", t, x, y, z);
            g.addPoint(x, y);
            // Update position using current velocity
            x += vx * timeStep;
            y += vy * timeStep;
            z += vz * timeStep;
            
            // Update velocity (Lorentz force)
            double vxNew = vx + (q * (vy * B) / m) * timeStep;
            double vyNew = vy - (q * (vx * B) / m) * timeStep;
            // vz remains constant as B is along z and there's no electric field
            vx = vxNew;
            vy = vyNew;
        }

        return g;
        }
    }
    public static class Layer {
        
        public int layerID = 0;
        List<Line3D> wires = new ArrayList<>();
        public Layer(int id) { layerID = id;}
        public List<Line3D> getWires(){ return wires;}
        
        public int getIntersection(Line3D line){
            double distance = 1000.0;
            int       which = 0;
            for(int i = 0; i < wires.size(); i++){
                double doca = wires.get(i).distanceSegments(line).length();
                if(doca<distance){ distance = doca; which = i;}
            }
            return which;
        }
    }
    
    public static void main(String[] args){
        Detector det = new Detector();
        det.generate();
        
        det.tracks();
        
        
        //GraphErrors g = Swimmer.swim();
        
        //TGCanvas c = new TGCanvas();
        //c.draw(g);
    }
}
