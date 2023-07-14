/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.clas12.view;

import j4np.geom.prim.Vector3D;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.List;
import twig.data.GraphErrors;
import twig.widgets.Polygon;

/**
 *
 * @author gavalian
 */
public class CalorimeterView {
    public double generalOffset = 60.0;
    public double paddleHeight   = 3;
    public double openingAngle  = 62;
    
    public Polygon getStrip( int sector, int layer, int paddle){
        Polygon p = new Polygon();
        
        double offset = generalOffset+ paddleHeight*layer;
        
        double startX = offset*Math.cos(Math.toRadians(openingAngle));
        double startY = offset*Math.sin(Math.toRadians(openingAngle));
        double startXu = (offset+paddleHeight-2)*Math.cos(Math.toRadians(openingAngle));
        double startYu = (offset+paddleHeight-2)*Math.sin(Math.toRadians(openingAngle));
        
        double length = 2*startX;
        double lengthup = 2*startXu;
        
        double   step = length/36.0;
        double   stepup = lengthup/36.0;
        
        
        p.addPoint(startX-paddle*step, startY);
        p.addPoint(startX-(paddle+1)*step, startY);

        p.addPoint(startXu-(paddle+1)*stepup, startYu);        
        p.addPoint(startXu-paddle*stepup, startYu);
        p.addPoint(startX-paddle*step, startY);
        p.fill().setFillColor(3); p.fill().setFillStyle(1);
        p.rotateDeg(sector*60);
        return p;
    }
    
    public List<Polygon> getBoundary(){
        List<Polygon> list = new ArrayList<>();
        for(int k = 0; k < 6; k++) list.addAll(this.getSectorBoundary(k));
        return list;
    }
    
    public List<Polygon> getSectorBoundary(int sector){
        List<Polygon> list = new ArrayList<>();
        double offset = generalOffset;
        
        double startX = offset*Math.cos(Math.toRadians(openingAngle));
        double startY = offset*Math.sin(Math.toRadians(openingAngle));
        double startXu = (offset+paddleHeight*3)*Math.cos(Math.toRadians(openingAngle));
        double startYu = (offset+paddleHeight*3)*Math.sin(Math.toRadians(openingAngle));
        
        Polygon pcal = new Polygon(new double[]{startX, -startX, -startXu,startXu,startX}, 
                new double[]{startY, startY,startYu,startYu,startY});
        list.add(pcal);
        
        offset = generalOffset + 3*paddleHeight;
        
        startX = offset*Math.cos(Math.toRadians(openingAngle));
        startY = offset*Math.sin(Math.toRadians(openingAngle));
        startXu = (offset+paddleHeight*3)*Math.cos(Math.toRadians(openingAngle));
        startYu = (offset+paddleHeight*3)*Math.sin(Math.toRadians(openingAngle));
        
        Polygon ecin = new Polygon(new double[]{startX, -startX, -startXu,startXu,startX}, 
                new double[]{startY, startY,startYu,startYu,startY});
        list.add(ecin);
        
        offset = generalOffset + 6*paddleHeight;
        
        startX = offset*Math.cos(Math.toRadians(openingAngle));
        startY = offset*Math.sin(Math.toRadians(openingAngle));
        startXu = (offset+paddleHeight*3)*Math.cos(Math.toRadians(openingAngle));
        startYu = (offset+paddleHeight*3)*Math.sin(Math.toRadians(openingAngle));
        
        Polygon ecout = new Polygon(new double[]{startX, -startX, -startXu,startXu,startX}, 
                new double[]{startY, startY,startYu,startYu,startY});
        
        list.add(ecout);
        //pcal.addPoint(startX, startY);
        for(Polygon p : list){ 
            p.rotate(Math.toRadians(sector*60)); 
            p.line().setLineColor(4);
            p.fill().setFillStyle(1);
            p.fill().setFillColor(184);
        }
        return list;
    } 
    
    
    public List<Polygon> getFromFile(String filename, int event){
        
        List<Polygon> list = new ArrayList<>();
        HipoReader r = new HipoReader(filename);
        Bank ec = r.getBank("ECAL::adc");
        Event e = new Event();
        r.getEvent(e, event);
        e.read(ec);        
        for(int i = 0; i < ec.getRows(); i++){
            int  comp = ec.getInt("component", i)-1;
            int layer = ec.getInt("layer", i);
            if(layer>=1&&layer<=3) comp = comp/2;
            list.add(getStrip(
            ec.getInt("sector", i)-1,
            ec.getInt("layer", i)-1,
            comp
            ));
        }
        
        return list;
    }
    public GraphErrors getView(int sector, int layer){
        return null;
    }
}
