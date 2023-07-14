/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.publish;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import twig.data.GraphErrors;
import twig.graphics.TGCanvas;
import twig.widgets.Line;

/**
 *
 * @author gavalian
 */
public class DriftChambers {
    
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
        t1.addPoint(12, 1);
        t1.addPoint(15, 2);
        t1.addPoint(25, 4);
        t1.addPoint(32, 5);
        t1.addPoint(50, 7);
        t1.addPoint(62, 8);
        return t1;
    }
    
    public static GraphErrors getRandom(int size){
        GraphErrors t1 = new GraphErrors("track1");
        double[] r = new double[]{1,2,4,5,7,8};
        Random rnd = new Random();
        for(int i = 0; i < size; i++){
            int layer = rnd.nextInt(r.length);
            int wire = rnd.nextInt(112);
            t1.addPoint(wire, r[layer]);
        }
        return t1;
    }
    
    public static void drawTracks(){
        TGCanvas c = new TGCanvas(600,600);
        
        GraphErrors t0 = DriftChambers.makeGraph();
        
        GraphErrors t1 = DriftChambers.moveGraph(t0, 20);
        GraphErrors t2 = DriftChambers.getOposite(t1,112);

        GraphErrors h1 = DriftChambers.getRandom(8);
        
        t1.attr().set("ms=12,lw=2");
        t2.attr().set("ms=12,lw=2");
        h1.attr().set("ms=12,lw=2,mc=5");
        
        c.draw(t1,"PL").draw(t2,"samePL").draw(h1,"sameP");
        c.region().axisLimitsX(0.0, 112);
        DriftChambers.setStyle(c);
        List<Line> lines = DriftChambers.getLines();
        for(Line l : lines) c.draw(l);
    }
    
    public static void main(String[] args){
        DriftChambers.drawTracks();
    }
}
