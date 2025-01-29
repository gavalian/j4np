/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.publish;

import j4np.geom.prim.Line3D;
import j4np.geom.prim.Path3D;
import j4np.geom.prim.Point3D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import twig.data.Axis;
import twig.data.Graph;
import twig.graphics.TGCanvas;
import twig.widgets.Line;
import twig.widgets.Polygon;

/**
 *
 * @author gavalian
 */
public class DriftChamberDiagram {
    
    public record SuperLayer(List<Line3D> layers, List<Line3D> boundaries){}
    public record WireHit(int sector, int layer, int wire){}
    
    public static class CalorimeterSector {
        
    }
    
    public static class DriftSector {

        SuperLayer[] slayers = new SuperLayer[6];
                
        public DriftSector(int sector){
            //slayers[0] = DriftSector.getLines(1.5, 0)
            double rotate = Math.toRadians(sector*60);
            init(rotate);
        }
        public DriftSector(){
            //slayers[0] = DriftSector.getLines(1.5, 0)
            init(0.0);
        }
        
        public Polygon getPolygon(int sl){
            Polygon p = new Polygon();
            p.addPoint(slayers[sl].boundaries.get(0).origin().x(), slayers[sl].boundaries.get(0).origin().y());
            p.addPoint(slayers[sl].boundaries.get(0).end().x(), slayers[sl].boundaries.get(0).end().y());
            p.addPoint(slayers[sl].boundaries.get(1).end().x(), slayers[sl].boundaries.get(1).end().y());
            p.addPoint(slayers[sl].boundaries.get(1).origin().x(), slayers[sl].boundaries.get(1).origin().y());
            p.addPoint(slayers[sl].boundaries.get(0).origin().x(), slayers[sl].boundaries.get(0).origin().y());
            p.attrLine().setLineColor(52);
            p.attrFill().setFillColor(182);
            return  p;
        }
        
        public List<Polygon> getFrame(){
            List<Polygon> p = new ArrayList<>();
            for(int i = 0; i < 6; i++) p.add(this.getPolygon(i));
            return p;
        }
        
        public final void init(double rotate){
            double spacing = 0.12;
            double[] radius = new double[]{1.5,2.5,3.5,4.5,5.5,6.5};    
            slayers[0] = new SuperLayer(DriftSector.getLines(rotate,1.5, spacing),
                    DriftSector.getLines(2,rotate,1.4, 6*spacing+0.1));
            slayers[1] = new SuperLayer(DriftSector.getLines(rotate,2.5, spacing),
                    DriftSector.getLines(2,rotate,2.4, 6*spacing+0.1));
            slayers[2] = new SuperLayer(DriftSector.getLines(rotate,3.5, spacing),
                    DriftSector.getLines(2,rotate,3.4, 6*spacing+0.1)
            );
            slayers[3] = new SuperLayer(DriftSector.getLines(rotate,4.5, spacing),
                    DriftSector.getLines(2,rotate,4.4, 6*spacing+0.1));
            
            slayers[4] = new SuperLayer(DriftSector.getLines(rotate,5.5, spacing),
                    DriftSector.getLines(2,rotate,5.4, 6*spacing+0.1));
            slayers[5] = new SuperLayer(DriftSector.getLines(rotate,6.5, spacing),
                    DriftSector.getLines(2,rotate,6.4, 6*spacing+0.1));            
        }
        
        public Graph getGraph(Path3D p){
            return getGraph(p,0,1,2,3,4,5);
        }
        
        public Graph getGraph(Path3D p, int... index){
             Graph g = new Graph();
             for(int j = 0; j < index.length; j++){
                 int which = index[j];
                 for(int i = 0; i < slayers[which].layers.size(); i++){
                     Line3D inter = p.getIntersection(slayers[index[j]].layers.get(i));
                     //System.out.printf("distance = %f\n",inter.length());
                     if(inter.length()<0.0000001)
                         g.addPoint(inter.origin().x(), inter.origin().y());
                 }
             }
             return g;
        }
        
        public static List<Line3D>  getLines( double start, double spacing){
            return getLines(0.0,start,spacing);
        }
        public static List<Line3D>  getLines( double rotate, double start, double spacing){
            return getLines(0,rotate,start,spacing);
        }
        
        public static List<Line3D>  getLines(int count, double rotate, double start, double spacing){
            List<Line3D> lines = new ArrayList<>();
            double phis = Math.toRadians(90+27);
            double phie = Math.toRadians(90-27);
            for(int i = 0; i < 6; i++){
                double h = start+spacing*i;
                double r = Math.abs(h/Math.sin(phis));
                Line3D line =
                        new Line3D(r*Math.cos(phis),r*Math.sin(phis),0.0,
                                r*Math.cos(phie),r*Math.sin(phie),0.0
                        );
                line.rotateZ(rotate);
                lines.add(line);
            }
            return lines;
        }
    }
    public static class DriftChamber {
        public DriftSector[] sectors = new DriftSector[6];
        public DriftChamber(){
            for(int i = 0; i < 6; i++) sectors[i] = new DriftSector(i);
        }
        public List<Polygon> getFrame(){
            List<Polygon> p = new ArrayList<>();
            for(int i = 0; i < sectors.length; i++) p.addAll(sectors[i].getFrame());
            return p;
        }
        public Graph getGraph(Path3D path, int... layers){
            for(int i = 0; i < sectors.length; i++){
               Graph g = sectors[i].getGraph(path, layers);
               if(g.getVectorX().getSize()>0) return g;
            }
            return null;
        }
        
        public Graph getGraph(List<WireHit> hits){
            Graph g = new Graph();
            for(int i = 0; i < hits.size(); i++){
                int s = hits.get(i).sector;
                int l = hits.get(i).layer;
                double w = (double) hits.get(i).wire;
                int sl = (l-1)/6;
                int ll = (l-1)%6;
                Line3D line = this.sectors[s-1].slayers[sl].layers.get(ll);
                Point3D p = line.lerpPoint( (w-1)/112.0);
                g.addPoint(p.x(),p.y());
            }
            g.attr().set("mc=2,lc=2");
            return g;
        }
    }
    
    
    public static Path3D getPath(double phi, double p){
        Path3D pt = new Path3D();
        double step = 0.1;
        double angle = Math.toRadians(phi);
        for(int i = 0; i < 100; i++){
            double r = i*step;
            double a1 = angle + i*(p*0.001);
            pt.addPoint(r*Math.cos(a1), r*Math.sin(a1),0.0);
        }
        return pt;
    } 
    
    public static List<Line3D>  getLines(double start, double spacing){
        List<Line3D> lines = new ArrayList<>();
        double phis = Math.toRadians(90+28);
        double phie = Math.toRadians(90-28);
        for(int i = 0; i < 6; i++){
            double h = start+spacing*i;
            double r = Math.abs(h/Math.sin(phis));
            lines.add(new Line3D(r*Math.cos(phis),r*Math.sin(phis),0.0,
                    r*Math.cos(phie),r*Math.sin(phie),0.0
            ));
        }
        return lines;
    }

    public static Graph getGraph(double phi, double p){
        Graph g = new Graph();
        double step = 0.1;
        double angle = Math.toRadians(phi);
        for(int i = 0; i < 100; i++){
            double r = i*step;
            double a1 = angle + i*(p*0.001);
            g.addPoint(r*Math.cos(a1), r*Math.sin(a1));
        }
        return g;
    }
    
    public static Graph getGraph(Path3D p, List<Line3D> lines){
        Graph g = new Graph();
        for(int i = 0; i < lines.size(); i++){
            Line3D inter = p.getIntersection(lines.get(i));
            g.addPoint(inter.origin().x(), inter.origin().y());
        }
        return g;
    }
    
    public static Graph getGraph(Path3D p){
        Graph g = new Graph();
        int n = p.getNumLines(); Line3D line = new Line3D();
        for(int i = 0; i < n; i++){
            p.getLine(line, i);
            g.addPoint(line.origin().x(), line.origin().y());
        }
        return g;
    }
    
    
    public static void drawData(){
        Random r = new Random();
        List<WireHit> hits = new ArrayList<>();
        for(int i = 0; i < 200; i++){
            hits.add(new WireHit(r.nextInt(6)+1,r.nextInt(36)+1,r.nextInt(112)+1));
        }
        DriftChamber chamber = new DriftChamber();
        TGCanvas c = new TGCanvas(600,600);
        
        Graph g = chamber.getGraph(hits);
        c.draw(g);
         List<Polygon> p = chamber.getFrame();
         c.region().draw(p);
        c.view().region(0).axisLimitsX(-10, 10);
        c.view().region(0).axisLimitsY(-10, 10);
    }
    
    
    public static void drawTracks(){
        TGCanvas c = new TGCanvas(600,600);

        
        
        DriftSector sector = new DriftSector(0);

        DriftChamber chamber = new DriftChamber();

        Path3D path = DriftChamberDiagram.getPath(85, 3);        
        Graph g  = chamber.getGraph(path, 0,1,2,3,4,5);
        Graph gl =  DriftChamberDiagram.getGraph(path);
        
        Path3D path2 = DriftChamberDiagram.getPath(195, -2);        
        Graph g2 = chamber.getGraph(path2, 0,1,2,3,4,5);
        Graph gl2 =  DriftChamberDiagram.getGraph(path2);
        
        Path3D path3 = DriftChamberDiagram.getPath(320, -1.5);        
        Graph g3 = chamber.getGraph(path3, 0,1,2,3,4,5);
        Graph gl3 =  DriftChamberDiagram.getGraph(path3);  
          Random r = new Random();
        Axis   axis = new Axis(new double[]{0,0.35,0.55,0.7,0.8,0.9,1.0});
        for(int i = 0; i < 65; i++){
            int which = r.nextInt(6);
            
            double whichValue = r.nextDouble();
            int bin = axis.getBin(whichValue);
            System.out.printf(" bin = %d max bins = %d\n",bin,axis.getNBins());
            double angle = r.nextDouble()*360-180;
            double mom = r.nextDouble()*6-3;
            Path3D ptmp = DriftChamberDiagram.getPath(angle, mom);
            Graph gtmp = chamber.getGraph(ptmp, bin);
            if(gtmp!=null){
                gtmp.attr().set("mc=2");
                c.draw(gtmp,"sameP");
            }
        }
        g.attr().set("lc=4,mc=#005500");
        c.draw(g,"samePL");
        c.draw(g2,"samePL");
        c.draw(g3,"samePL");
        c.draw(gl,"sameL");
        c.draw(gl2,"sameL");
        c.draw(gl3,"sameL");
        List<Polygon> p = chamber.getFrame();
        
        c.region().draw(p);
        //c.region().set("fc=#5020AA");
        List<WireHit> hits = new ArrayList<>();
        for(int i = 0; i < 200; i++){
            hits.add(new WireHit(r.nextInt(6)+1,r.nextInt(36)+1,r.nextInt(112)+1));
        }
        Graph gg = chamber.getGraph(hits);
        c.draw(gg,"sameP");
        c.view().region(0).axisLimitsX(-10, 10);
        c.view().region(0).axisLimitsY(-10, 10);
        c.repaint();
        Line3D line = new Line3D(-4,6,0,4,6,0);
        Line3D inter = path.getIntersection(line);
        System.out.println(inter);
    }
    
    public static void main(String[] args){
       DriftChamberDiagram.drawData();
       DriftChamberDiagram.drawTracks();
        
    }
}
