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
import twig.data.H2F;
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
    public static int DC_HIT_COLOR = 81;
    
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
        
        gr.attr().setLineColor(DriftChamberTools.DC_HIT_COLOR);
        gr.attr().setMarkerColor(DriftChamberTools.DC_HIT_COLOR);
        gr.attr().setMarkerOutlineColor(DriftChamberTools.DC_HIT_COLOR);
        gr.attr().setLineWidth(DriftChamberTools.TRACK_TRAJ_WIDTH);
        gr.attr().setMarkerSize(DriftChamberTools.TRACK_CLUSTER_SIZE);
        return gr;
    }
    
    public static H2F[] getHistosSector(int[] sector, int[] layer, int[] wire){
        H2F[] hc = H2F.duplicate(6, "DC::tdc", 112, 0.5, 112.5, 36, 0.5,36.5);
        for(int n = 0; n < sector.length; n++){
            int bin = sector[n]-1;
            if(bin>=0&&bin<=5) hc[bin].setBinContent(wire[n]-1,layer[n]-1, 1.0);
        }
        return hc;
    } 
    
    public static H2F[] getHistosSuperLayer(int[] sector, int[] layer, int[] wire){
        H2F[] hc = H2F.duplicate(36, "DC::tdc", 112, 0.5, 112.5, 6, 0.5,36.5);
        for(int n = 0; n < sector.length; n++){
            int bin = sector[n]-1;
            int ll = (layer[n]-1)%6;
            int  r = (layer[n]-1)/6;
            if(bin>=0&&bin<=5&&r<6&&ll<6) {
                //int cbin = bin*6
                hc[bin].setBinContent(wire[n]-1,layer[n]-1, 1.0);
            }
        }
        return hc;
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
        gr.attr().setMarkerOutlineColor(DriftChamberTools.TRACK_COLOR);
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
    
    public GraphErrors getTrackGraph(int sector, float[] array, boolean withOrigin){        
        GraphErrors gr = new GraphErrors();
        gr.attr().setLineColor(DriftChamberTools.TRACK_COLOR);
        gr.attr().setMarkerColor(DriftChamberTools.TRACK_COLOR);
        gr.attr().setMarkerOutlineColor(DriftChamberTools.TRACK_COLOR);
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
    
    public List<Polygon> getSectorBoundaries(){
        Path3D p = new Path3D();
        List<Polygon> list = new ArrayList<>();
        for(int sl = 1; sl <=6; sl++){
                driftChambers.getSuperlayerPath(p, sl);
                Polygon poly = DriftChamberTools.polygonFromPath3D(p);
                //poly.rotate(Math.toRadians(60.0*(s-1)));
                list.add(poly);
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
        c.region().axisLimitsX(-180, 180);
        c.region().axisLimitsY(-180, 180);
    }
    
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
