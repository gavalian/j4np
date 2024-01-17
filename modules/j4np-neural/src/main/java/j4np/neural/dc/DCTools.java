/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.dc;

import j4np.geom.prim.Point3D;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.data.Event;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import twig.data.GraphErrors;
import twig.data.H2F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class DCTools {
    
    public static List<H2F> createView(){
        List<H2F> list = new ArrayList<>();
        for(int i = 1; i <= 6; i++)
            list.add(new H2F("dcs"+1,112,0.5,112.5,36,0.5,36.5));
        return list;
    }
    
    public static List<H2F> dcView(Bank dc){
        List<H2F> list = DCTools.createView();
        int nrows = dc.getRows();
        for(int r = 0; r < nrows; r++){
            int sector = dc.getInt("sector", r) - 1;
            if(sector>=0&&sector<6){
                list.get(sector).fill( 
                        dc.getInt("component", r),
                        dc.getInt("layer", r) );
            }
        }
        return list;
    }
    
    public static List<H2F> dcView(CompositeNode dc){
        List<H2F> list = DCTools.createView();
        int nrows = dc.getRows();
        for(int r = 0; r < nrows; r++){
            int sector = dc.getInt(1, r) - 1;
            int    det = dc.getInt(0, r);
            if(sector>=0&&sector<6&&det==6){
                list.get(sector).fill( 
                        dc.getInt(3, r),
                        dc.getInt(2, r) );
            }
        }
        return list;
    }
    public static GraphErrors getTrackGraph(CompositeNode node, int row){
        GraphErrors g = new GraphErrors();
        g.attr().set("ms=8,mc=5,lc=5,lw=3");
        g.attr().setLegend(String.format("charge = %d, probability = %.5f",
                node.getInt(0,    row),node.getDouble(3,row) ));
        
        int nrows = node.getRows();
        double offset = 20;
        double scale = 10.0;
        Point3D p = new Point3D();
        int  sector = node.getInt(1,    row);
        
        for(int i = 0; i < 6; i++){

        int   layer = i+1;
        double wire = node.getDouble(10+i, row);
        
        double x = layer*scale + offset;
        double yrange = 2*x*Math.tan(Math.toRadians(30.0));
        double y = wire*(yrange/112.)-yrange/2.0;
        p.set(x, y, 0.0);
        p.rotateZ(Math.toDegrees(60.0*sector));
        //if(sector==1)
        g.addPoint(p.x(),p.y());
        }
        return g;

    }
    public static GraphErrors getGraph(CompositeNode node){
        GraphErrors g = new GraphErrors();
        g.attr().set("ms=8,mc=4,lc=4");
        g.attr().setLegend("drift chamber hits");
        
        int nrows = node.getRows();
        double offset = 20;
        double scale = 10.0;
        Point3D p = new Point3D();
        for(int i = 0; i < nrows; i++){
            int  sector = node.getInt(1, i);
            int   layer = node.getInt(2, i);
            double wire = node.getDouble(3, i);
            double x = layer*scale + offset;
            double yrange = 2*x*Math.tan(Math.toRadians(30.0));
            double y = wire*(yrange/112.)-yrange/2.0;
            p.set(x, y, 0.0);
            p.rotateZ(Math.toDegrees(60.0*sector));
            //if(sector==1)
            g.addPoint(p.x(),p.y());
        }
        return g;
    }
    
    public static void main(String[] args){
        //String file = "/Users/gavalian/Work/DataSpace/trigger/recon/006152/rec_clas_006152.evio.00050-00054.hipo";
        String file = "/Users/gavalian/Work/DataSpace/rgd/ttt.h5";
        HipoReader r = new HipoReader(file);
        Event e = new Event();
        
        Bank[] banks = r.getBanks("DC::tdc");
        TGCanvas c = new TGCanvas();
        CompositeNode node = new CompositeNode(50*1024);
        
        c.view().divide(3, 2);
        while(r.next(e)==true){
            e.read(node,33,1);
            //node.print();
            List<H2F> hl = DCTools.dcView(node);
            for(int i = 0; i < 6; i++) c.cd(i).draw(hl.get(i));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(DCTools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
