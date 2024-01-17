/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.publish;

import j4np.geom.detector.dc.DriftChambers;
import j4np.geom.prim.Line3D;
import j4np.geom.prim.Path3D;
import j4np.geom.prim.Point3D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import twig.data.GraphErrors;
import twig.graphics.GraphNode3D;
import twig.graphics.TGCanvas;
import twig.widgets.Line;
import twig.widgets.Polygon;

/**
 *
 * @author gavalian
 */
public class DriftChamberTools {    
    
    public static int TRACK_COLOR = 5;
    public static int TRACK_TRAJ_WIDTH = 2;
    public static int TRACK_CLUSTER_SIZE = 8;
    public static int DC_HIT_SIZE = 8;
    public static int DC_HIT_COLOR = 41;
    
    public  DriftChambers driftChambers = new DriftChambers();
    
    
    public DriftChamberTools(){
        driftChambers.setOffset(20).setScale(15).setLayerOffset(2.5).setAngle(Math.toRadians(27.0));
    }
    
    public  GraphErrors getGraph(int[] sector, int[] layer, int[] wire){        
        GraphErrors gr = new GraphErrors();
        Point3D point = new Point3D();
        for(int r = 0; r < sector.length; r++){
            driftChambers.getLayerPoint(point, sector[r], layer[r], wire[r]);
            gr.addPoint(point.x(), point.y(), 0.0, 0.0);
        }
        return gr;
    }
    
    public  GraphErrors getGraph(int sector, List<Integer> layer, List<Integer> wire){        
        GraphErrors gr = new GraphErrors();
        Point3D point = new Point3D();
        for(int r = 0; r < layer.size(); r++){
            driftChambers.getLayerPoint(point, sector, layer.get(r),wire.get(r));
            gr.addPoint(point.x(), point.y(), 0.0, 0.0);
        }
        return gr;
    }
    
    public GraphErrors getTrackGraph(int sector, double[] array, boolean withOrigin){        
        
        GraphErrors gr = new GraphErrors();
        gr.attr().setLineColor(DriftChamberTools.TRACK_COLOR);
        gr.attr().setMarkerColor(DriftChamberTools.TRACK_COLOR);
        gr.attr().setLineWidth(DriftChamberTools.TRACK_TRAJ_WIDTH);
        gr.attr().setMarkerSize(DriftChamberTools.TRACK_CLUSTER_SIZE);
        
        if(withOrigin==true) gr.addPoint(0.0, 0.0);
        Point3D point = new Point3D();
        int layer = 3;
        for(int i = 0; i < array.length; i++){
            if(array[i]>0.1){
                driftChambers.getLayerPoint(point, sector, layer,array[i]);
                gr.addPoint(point.x(), point.y());
            }
            layer += 6;
        }        
        return gr;
    }
    
    public static Polygon polygonFromPath3D(Path3D path){
        Polygon p = new Polygon();
        Line3D line = new Line3D();
        for(int j = 0; j < path.getNumLines(); j++){
            path.getLine(line, j);
            p.addPoint(line.origin().x(), line.origin().y());
            p.addPoint(line.end().x(), line.end().y());            
        }
        path.getLine(line, 0);
        p.addPoint(line.origin().x(), line.origin().y());        
        p.attrLine().setLineColor(4);
        p.attrFill().setFillColor(183);
        
        return p;
    }
    
    public List<double[]> getTracks(int[] array){
        List<double[]> list = new ArrayList<>();
        int nsize = array.length/6;
        for(int i = 0; i < nsize; i++){
            double[] track = new double[6];
            for(int k = 0; k < 6; k++) track[k] = array[k+i*6]/100.0;
            list.add(track);
        }
        return list;
    }
    
    public List<Polygon> getBoundaries(){
        /*DriftChambers dc = new DriftChambers();
        dc.setOffset(20).setScale(17).setLayerOffset(3);
        dc.setAngle(Math.toRadians(25.0));*/
        Path3D p = new Path3D();
        List<Polygon> list = new ArrayList<>();
        for(int s = 1; s <=6 ; s++){
            for(int sl = 1; sl <=6; sl++){
                driftChambers.getSuperlayerPath(p, sl);
                Polygon poly = DriftChamberTools.polygonFromPath3D(p);
                poly.rotate(Math.toRadians(60.0*(s-1)));
                list.add(poly);
            }
        }
        return list;
    }
    
    public List<GraphErrors> generate(){
        List<GraphErrors> list = new ArrayList<>();
        Random r = new Random();
        for(int s = 1; s <=6 ; s++){
            List<Integer> layer = new ArrayList<>();
            List<Integer> wire = new ArrayList<>();
            for(int i = 0; i < 80; i++){
                layer.add(r.nextInt(36)+1);
                wire.add(r.nextInt(112)+1);
            }
            list.add(this.getGraph(s, layer, wire));
        }
        return list;
    }
    
    public static void setStyle(TGCanvas c){
        c.region().getAxisFrame().getAxisX().getAttributes().setAxisLineDraw(Boolean.FALSE);
        c.region().getAxisFrame().getAxisX().getAttributes().setAxisTicksDraw(Boolean.FALSE);
        c.region().getAxisFrame().getAxisX().getAttributes().setAxisBoxDraw(false);
        c.region().getAxisFrame().getAxisX().getAttributes().setAxisLabelsDraw(false);
        c.region().getAxisFrame().getAxisY().getAttributes().setAxisLineDraw(Boolean.FALSE);
        c.region().getAxisFrame().getAxisY().getAttributes().setAxisTicksDraw(Boolean.FALSE);
        c.region().getAxisFrame().getAxisY().getAttributes().setAxisBoxDraw(false);
        c.region().getAxisFrame().getAxisY().getAttributes().setAxisLabelsDraw(false);
    }
    
    /*
    public static int SUPERLAYER_COLOR   = 174;
    public static int SUPERLAYER_OUTLINE =  4;
    public static double SUPERLAYER_SHIFT =  12;
    public static double TRAPEZOID_RATIO = 0.8;
    
    public static List<Polygon>  getSectorPolygon(int sector, boolean tilted){
        List<Polygon> plist = new ArrayList<>();
        for(int k = 1; k <= 6; k++){
            plist.add(DriftChambersTools.getSuperlayerPolygon(sector, k, tilted));
        }
        return plist;
    }
    public static Polygon getSuperlayerPolygon(int sector, int sl, boolean tilted){
        
        double[] r = new double[]{1,2,4,5,7,8};
        double xmax = 112;
        double xmin = 112;
        double height = r[sl-1] + DriftChambersTools.SUPERLAYER_SHIFT;
        if(tilted==true){
            double hmax  = r[5] + DriftChambersTools.SUPERLAYER_SHIFT;
            double hmin  = r[0] + DriftChambersTools.SUPERLAYER_SHIFT;
            //double hyp = Math.sqrt(hmax*hmax+112.0*0.5*112.0*0.5);
            xmax = (112.0)*(height+0.45)/hmax;
            xmin = (112.0)*(height-0.45)/hmax;
            //double costh = 
        }
        Polygon p = Polygon.trap(xmin,xmax, 0.9);
        
        p.move(112/2, r[sl-1] + DriftChambersTools.SUPERLAYER_SHIFT);
        p.attrFill().setFillColor(DriftChambersTools.SUPERLAYER_COLOR);
        p.attrLine().setLineColor(DriftChambersTools.SUPERLAYER_OUTLINE);
        if(tilted==true){
            p.rotate(Math.toRadians(-sector*60.0));
        }
        return p;
    }
    
    public static List<Line> getSuperLayer(double scale, double shift){
        List<Line> list = new ArrayList<>();
        for(int t = 0; t < 6; t++){
            Line l = new Line(0.,shift+scale*t,112.0,shift+scale*t);
            l.setLineColor(1); l.setWidth(1);
            list.add(l);
        }
        return list;
    }
    
    public static List<Line> getLines(double scale){
        List<Line> list = new ArrayList<>();
        double[] r = new double[]{0.75,1.75,3.75,4.75,6.75,7.75};
        for(int i = 0; i < 6 ; i++) {
            list.addAll(DriftChambersTools.getSuperLayer(scale, r[i]));
            //l.setLineColor(1); l.setWidth(2);
            //list.add(l);
        }
        return list;
    }
    
    public static List<Line> getLines(){
        List<Line> list = new ArrayList<>();
        double[] r = new double[]{1,2,4,5,7,8};
        for(int i = 0; i < 6 ; i++) {
            Line l = new Line(0.,r[i],112.0,r[i]);
            l.setLineColor(1); l.setWidth(2);
            list.add(l);
        }
        return list;
    }
    
    public static List<Polygon> getChambers(){
        List<Polygon> plist = new ArrayList<>();
        Polygon p = Polygon.box(112, 2);
        p.move(0, 3.5);
        p.attrFill().setFillColor(162);
        p.attrLine().setLineColor(2);
        
        plist.add(p);
        return plist;
    }
    
    public static void setStyle(TGCanvas c){
        c.region().getAxisFrame().getAxisX().getAttributes().setAxisLineDraw(Boolean.FALSE);
        c.region().getAxisFrame().getAxisX().getAttributes().setAxisTicksDraw(Boolean.FALSE);
        c.region().getAxisFrame().getAxisX().getAttributes().setAxisBoxDraw(false);
        c.region().getAxisFrame().getAxisX().getAttributes().setAxisLabelsDraw(false);
        c.region().getAxisFrame().getAxisY().getAttributes().setAxisLineDraw(Boolean.FALSE);
        c.region().getAxisFrame().getAxisY().getAttributes().setAxisTicksDraw(Boolean.FALSE);
        c.region().getAxisFrame().getAxisY().getAttributes().setAxisBoxDraw(false);
        c.region().getAxisFrame().getAxisY().getAttributes().setAxisLabelsDraw(false);
    }
    
    public static  GraphErrors getOposite(GraphErrors g, int offset){
        GraphErrors gr = new GraphErrors();
        for(int i = 0; i < 6; i++) gr.addPoint(offset-g.getVectorX().getValue(i), g.getVectorY().getValue(i));
        return gr;
    }
    
    public static  GraphErrors moveGraph(GraphErrors g, int move){
        GraphErrors gr = new GraphErrors();
        for(int i = 0; i < 6; i++) gr.addPoint(move+g.getVectorX().getValue(i), g.getVectorY().getValue(i));
        return gr;
    }
    public static GraphErrors makeGraph(){
        GraphErrors t1 = new GraphErrors("track1");        
        //t1.addPoint(8, -1);
        double off = DriftChambersTools.SUPERLAYER_SHIFT;
        t1.addPoint(12, 1+off);
        t1.addPoint(15, 2+off);
        t1.addPoint(25, 4+off);
        t1.addPoint(32, 5+off);
        t1.addPoint(50, 7+off);
        t1.addPoint(62, 8+off);
        return t1;
    }
    
    public static GraphErrors getRandom(int size){
        GraphErrors t1 = new GraphErrors("track1");
        double[] r = new double[]{1,2,4,5,7,8};
        Random rnd = new Random();
        for(int i = 0; i < size; i++){
            int layer = rnd.nextInt(r.length);
            int wire = rnd.nextInt(112);
            t1.addPoint(wire, r[layer]+DriftChambersTools.SUPERLAYER_SHIFT);
        }
        return t1;
    }
    */
    public static void drawTracks(){
        
        TGCanvas c = new TGCanvas(600,600);
        DriftChamberTools dctools = new DriftChamberTools();
        
        List<GraphErrors> graphs = dctools.generate();
        GraphErrors gr1 = dctools.getTrackGraph(6, new double[]{45.,48.0, 56.0, 58.0, 69,71},true);
        GraphErrors gr2 = dctools.getTrackGraph(5, new double[]{43.,41.0, 32.0, 28.0, 8,1},true);
        
        List<Polygon> poly = dctools.getBoundaries();
        DriftChamberTools.setStyle(c);
        c.region().draw(graphs, "");
        c.region().draw(gr1,"samePL").draw(gr2, "samePL");
        c.region().draw(poly);
        c.repaint();
        
        /*GraphErrors t0 = DriftChambersTools.makeGraph();
        
        GraphErrors t1 = DriftChambersTools.moveGraph(t0, 20);
        GraphErrors t2 = DriftChambersTools.getOposite(t1,112);

        GraphErrors h1 = DriftChambersTools.getRandom(8);
        
        t1.attr().set("ms=12,lw=2");
        t2.attr().set("ms=12,lw=2");
        h1.attr().set("ms=12,lw=2,mc=5");
        
        
        //List<Polygon> pols = DriftChambers.getChambers();
        //for(Polygon p : pols) c.draw(p);
        
        //List<Line> lines = DriftChambers.getLines();
        //for(Line l : lines) c.draw(l);
        
        //List<Line> lines = DriftChambers.getLines(0.12);
        //for(Line l : lines) c.draw(l);
        c.draw(t1,"samePL").draw(t2,"samePL").draw(h1,"sameP");
        c.region().axisLimitsX(-400, 400);
        c.region().axisLimitsY(-40, 40);
        //c.region().axisLimitsY(-13.5, 13.5);
        //c.region().axisLimitsY(0.0,9.0);
        //List<Line> lines = DriftChambers.getLines();
        //for(Line l : lines) c.draw(l);
        //List<Polygon> pols = DriftChambers.getChambers();
        //for(Polygon p : pols) c.draw(p);
        for(int i = 1; i <= 6 ; i++) {
            List<Polygon> p = DriftChambersTools.getSectorPolygon(i, true);
            
            //Polygon p = DriftChambers.getSuperlayerPolygon(0, i, true);
            c.region().draw(p);
        }
        c.repaint();                
        //DriftChambers.setStyle(c);*/
    }
    
    public static void drawTracks3D(){
        TGCanvas c = new TGCanvas(600,600);
        DriftChamberTools dctools = new DriftChamberTools();
        
        List<GraphErrors> graphs = dctools.generate();
        //GraphNode3D g3node = new GraphNode3D(graph3d,"F");
    }
    
    public static void main(String[] args){
        DriftChamberTools.drawTracks();
    }
}
