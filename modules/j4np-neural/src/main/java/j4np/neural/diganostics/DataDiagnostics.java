/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.diganostics;

import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.neural.dc.DCTools;
import j4np.neural.finder.NeuralTrackFinder;
import java.util.logging.Level;
import java.util.logging.Logger;
import twig.data.GraphErrors;
import twig.graphics.TGCanvas;


/**
 *
 * @author gavalian
 */
public class DataDiagnostics {
    
    public static void main(String[] args){
        NeuralTrackFinder finder = new NeuralTrackFinder();
        finder.loadNetwork();
        
        String file = "/Users/gavalian/Work/DataSpace/rgd/ttt.h5";
        HipoReader r = new HipoReader(file);
        Event e = new Event();
        CompositeNode node = new CompositeNode(50*1024);
        CompositeNode node1 = new CompositeNode(50*1024);
        CompositeNode node2 = new CompositeNode(50*1024);
        TGCanvas c = new TGCanvas();
        c.region().setBackgroundColor(240, 240, 240);
        c.region().getAxisFrame().setBackgroundColor(220, 220, 220);
        while(r.next(e)==true){
            finder.processEvent(e);
            //e.scanShow();
            e.read(node1,32100,1);
            e.read(node2,32100,2);
            //node1.print();
            //node2.print();
            GraphErrors g = DCTools.getGraph(node1);
            c.draw(g);
            if(node2.getRows()>0){
                for(int i = 0; i < node2.getRows();i++){
                    GraphErrors gt = DCTools.getTrackGraph(node2, i);
                    c.draw(gt, "PLsame");
                }
                c.region().showLegend(0.05, 0.98);
            }
            c.region().axisLimitsX(-100, 100);
            c.region().axisLimitsY(-100, 100);
            try {
                //e.read(node,33,1);
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(DataDiagnostics.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
