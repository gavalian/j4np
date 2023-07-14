/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.clas12.view;

import j4np.geom.prim.Vector3D;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import twig.data.GraphErrors;
import twig.graphics.TGCanvas;
import twig.widgets.Polygon;

/**
 *
 * @author gavalian
 */
public class DriftChamberView {
    
    public int       nWires = 112/6;
    public int sectorOffset = 53;
    public int  wireGroup = 4;
    public double beamOffset = 20.0;
    
    
    public void getCoordinate(Vector3D vec, int sector, int layer, int wire){
        this.getCoordinate(vec, sector, layer, wire, 0.0, 0.0);
    }
    
    public void getCoordinate(Vector3D vec, int sector, int layer, int wire, double layerpadding, double wirepadding){
        
        int  shiftedWire = wire/wireGroup;
        double   opening = 62.0;
        
        double  firstWireX = (beamOffset+layer)*Math.cos(Math.toRadians(opening));
        double  firstWireY = (beamOffset+layer)*Math.sin(Math.toRadians(opening));
        
        double      length = 2*firstWireX;
        double        step = length/(112./wireGroup);
        if(layer%2!=0)
            vec.setXYZ(firstWireX - step*shiftedWire + wirepadding*step, firstWireY + step*layerpadding, 0.0);
        else vec.setXYZ(firstWireX - step*shiftedWire - step*0.5 + wirepadding*step, firstWireY + step*layerpadding, 0.0);
        vec.rotateZ(Math.toRadians(60*sector));
        /*double reverse = layer-35;
        double factor = (1-(reverse/35)*2.0);
        //System.out.printf(" layer = %d, reverse = %.2f, factor = %f\n",layer, reverse,factor);
        double x = wire*factor;
        double offset = nWires*factor/2.0;
        vec.setXYZ(x-offset, layer-sectorOffset,0.0);*/
    }
    
    public GraphErrors getSectorView(int sector, int region){
        GraphErrors g = new GraphErrors();
        Vector3D vec = new Vector3D();
        for(int layer = 0; layer < 6; layer++){
            for(int w = 0; w < 112; w += wireGroup){
                this.getCoordinate(vec, sector, region*6+layer, w);
                //System.out.printf(" %d -> %f, %f\n",region,vec.x(),vec.y());
                g.addPoint(vec.x(), vec.y());
            }
        }
        g.attr().setMarkerSize(3);
        return g;
    }
    
    public GraphErrors getRegionView(int sector, int region){
        GraphErrors g = new GraphErrors();
        Vector3D vec = new Vector3D();
        
        getCoordinate(vec, sector, region*6, 0,-0.2, 0.5);
        g.addPoint(vec.x(), vec.y());
        getCoordinate(vec, sector, region*6, 112,-0.2,-0.5);
        g.addPoint(vec.x(), vec.y());
        getCoordinate(vec, sector, (region+1)*6, 112,-0.2,-0.5);
        g.addPoint(vec.x(), vec.y());
        getCoordinate(vec, sector, (region+1)*6, 0,-0.2,0.5);
        g.addPoint(vec.x(), vec.y());
        getCoordinate(vec, sector, region*6, 0,-0.2,0.5);
        g.addPoint(vec.x(), vec.y());
        g.attr().setLineWidth(3);
        return g;
    }
    
    public void getCoordinate(Vector3D vec, double layer, double wire){
        double reverse = layer-35;
        double factor = (1-(reverse/35)*2.0);
        //System.out.printf(" layer = %d, reverse = %.2f, factor = %f\n",layer, reverse,factor);
        double x = wire*factor;
        double offset = nWires*factor/2.0;
        vec.setXYZ(x-offset, layer-sectorOffset,0.0);
    }
    
    public void fill(GraphErrors g, int sector, int layer, int wire){
        Vector3D v = new Vector3D();
        this.getCoordinate(v, sector, layer, wire);
        g.addPoint(v.x(),v.y());
    }
    
    public GraphErrors getSectorView(double layer){
        GraphErrors g = new GraphErrors();
        Vector3D v = new Vector3D();
        for(int wire = 0; wire < nWires; wire++){
            int r = (int) layer;
            //if(r%2==0)
            getCoordinate(v,layer, wire);
            //else getCoordinate(v,layer, wire-0.25);
            g.addPoint(v.x(), v.y());
        }
        g.attr().setMarkerSize(1);
        return g;
    }
    
    
    public List<GraphErrors> getSectorViewBoundaries(){
        List<GraphErrors> list = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            int layer = i*6;
            list.add(this.getSectorView(layer-0.5));
        }
        return list;
    }
    
    public List<GraphErrors> getTotalViewBoundaries(){
        List<GraphErrors> list = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            int layer = i*6;
            list.add(this.getSectorView(layer-0.5));
        }
        Vector3D v = new Vector3D();
        List<GraphErrors> total = new ArrayList<>();
        for(int s = 0; s < 6; s++){
            for(int l = 0; l < list.size(); l++){
                GraphErrors line = new GraphErrors();
                for(int n = 0; n < list.get(l).getVectorX().getSize(); n++){
                    double x = list.get(l).getVectorX().getValue(n);
                    double y = list.get(l).getVectorY().getValue(n);
                    v.setXYZ(x, y, 0);
                    v.rotateZ(Math.toRadians(s*60));
                    line.addPoint(v.x(), v.y());
                }
                total.add(line);
            }
        }
        return total;
    }
    
    public GraphErrors getSectorView(){
        GraphErrors g = new GraphErrors();
        Vector3D v = new Vector3D();
        for(int layer = 0; layer < 36; layer++){
            for(int wire = 0; wire < nWires; wire++){
               getCoordinate(v,layer, wire);
               //else getCoordinate(v,layer, wire+0.25);
               g.addPoint(v.x(), v.y());
            }
        }
        g.attr().setMarkerSize(1);
        return g;
    }
    
    public GraphErrors getTotalView(){
        GraphErrors g = this.getSectorView();
        Vector3D v = new Vector3D();
        
        GraphErrors gt = new GraphErrors();
        
        for(int i = 0; i < 6; i++){
            for(int n = 0; n < g.getVectorX().getSize(); n++){
                v.setXYZ(g.getVectorX().getValue(n),g.getVectorY().getValue(n),0.0);
                v.rotateZ(Math.toRadians(i*60));
                gt.addPoint(v.x(), v.y());
            }
        }
        gt.attr().setMarkerSize(2);
        return gt;
    }
    public List<GraphErrors> getFromFile(String filename, int event){
        int mSize = 12;
        int[] mColor = new int[]{51,4,8,5};
        int[] orders  = new int[]{60,50,40,0};
        
        GraphErrors[] g = new GraphErrors[4];
        for(int n = 0; n < g.length; n++){
            g[n] = new GraphErrors();
            g[n].attr().setMarkerSize(mSize);
            g[n].attr().setMarkerColor(mColor[n]);
            g[n].attr().setMarkerOutlineColor(mColor[n]);
        }
                
        HipoReader r = new HipoReader(filename);
        Bank dc = r.getBank("DC::tdc");
        Event e = new Event();
        r.getEvent(e, event);
        e.read(dc);
        for(int i = 0; i < dc.getRows(); i++){
            int order = dc.getInt("order", i);
            for(int k = 0; k < orders.length; k++){
                if(order==orders[k]) this.fill(g[k], 
                        dc.getInt("sector", i)-1,
                        dc.getInt("layer", i)-1,
                        dc.getInt("component", i)-1
                        );
            }
        }
        return Arrays.asList(g[0],g[1],g[2],g[3]);
    }
    
    public static void main(String[] args){
        
        String filename = "/Users/gavalian/Work/Software/project-10.6/study/tracking/outfile_for_test_noise1.hipo";
        DriftChamberView view = new DriftChamberView();
        CalorimeterView  ecal = new CalorimeterView();
        
        TGCanvas    c = new TGCanvas(1000,1000);
        for(int s = 0; s < 6; s++) for(int r = 0; r < 6; r++){
            GraphErrors g = view.getSectorView(s,r);
            GraphErrors gr = view.getRegionView(s,r);
            //c.draw(g,"same"); 
            c.draw(gr,"sameL");
        }

        List<GraphErrors> data = view.getFromFile(filename, 1);
        for(GraphErrors g : data){ g.attr().setMarkerSize(6);c.draw(g,"sameP");}
        Random r = new Random();
        
        
        List<Polygon> ecalPoly = ecal.getBoundary();
        for(Polygon p : ecalPoly) c.draw(p);
        
        List<Polygon>  polygons = ecal.getFromFile(filename, 1);
        for(Polygon p : polygons) c.draw(p);
        /*
        for(int i = 0; i < 9; i++){
            for(int k = 0; k < 5; k++){
                Polygon p = ecal.getStrip(1, i, r.nextInt(36));
                c.draw(p);
            }
        }*/
        
        c.region().axisLimitsX(-90, 90);
        c.region().axisLimitsY(-90, 90);
        /*
        GraphErrors g = view.getSectorView();
        List<GraphErrors> boundary = view.getSectorViewBoundaries();
        
        
        for(GraphErrors b : boundary) c.draw(b,"Lsame");
        c.draw(g,"same");
        
        TGCanvas    ct = new TGCanvas();
        GraphErrors gt = view.getTotalView();
        List<GraphErrors> total = view.getTotalViewBoundaries();
        
        ct.draw(gt);
        System.out.printf(" total = %d",total.size());
        for(GraphErrors b : total) ct.draw(b,"Lsame");
        */
    }
}
