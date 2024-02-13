/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.display;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.neural.classifier.NeuralClassifier;
import j4np.neural.data.DataNodes;
import j4np.neural.data.TrackConstructor;
import j4np.neural.data.Tracks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import twig.data.GraphErrors;
import twig.graphics.TGCanvas;
import twig.publish.DriftChamberTools;
import twig.publish.InstaRec;
import twig.widgets.Polygon;

/**
 *
 * @author gavalian
 */
public class DriftEventDisplay {
    
    public static CompositeNode getNode(Bank c){
        CompositeNode clus = DataNodes.getNodeClusters();
        int nrows = c.getRows();        
        
        int[] value = c.getInt("byte");
        int counter = 0;
        for(int i = 0; i < nrows; i+=3){
            int ii = c.getInt(0, i);
            int sector = ii/10;
            int layer  = ii - 10*(ii/10);
            double wire   = c.getInt(0, i+1);
            double  dec   = (float) c.getInt(0, i+2);
            clus.putByte(0,counter, (byte) (counter));
            clus.putByte(1,counter, (byte) (sector));
            clus.putByte(2,counter, (byte) (layer));
            clus.putFloat(3, counter, (float) (wire + dec/100.0));
            //System.out.printf(" %d %d %d\n",sector[counter],layer[counter],wire[counter]);
            counter++;
            clus.setRows(counter);
        }
        
        //clus.print();
        return clus;
    }
    
    public static void showClusters(Bank b, int sector, int order){
        
        NeuralClassifier classifier = new NeuralClassifier();        
        classifier.loadFromFile("etc/networks/clas12rgd.network", 4450);
        
        DriftChamberTools dt = new DriftChamberTools();
        CompositeNode node = DriftEventDisplay.getNode(b);
        //node.print();
        TrackConstructor tc = new TrackConstructor();
        
        for(int i = 0; i < node.getRows(); i++){
            tc.add(node.getInt(1, i),node.getInt(2, i), node.getInt(0, i), node.getDouble(3, i));
        }
        
        //tc.show();
        
        Tracks list = new Tracks();
        
        tc.sectors[sector].create(list, 2);
        
        //list.show();
        float[] input = new float[6];
        int nrows = list.getRows();
        
        List<GraphErrors> graphs = new ArrayList<>();
        List<GraphErrors> graphsTrue = new ArrayList<>();
        
        for(int i = 0; i < nrows; i++){
            list.getInput(input, i);
            float[] output = classifier.evaluate(input);
            
            for(int k = 0; k < input.length; k++) input[k] *= 112.0;            
            if(output[0]<0.3){
                System.out.println(" " + Arrays.toString(input) + " ===> " + Arrays.toString(output));
                GraphErrors g = dt.getTrackGraph(1, input, true);
                g.attr().set("lc=5,lw=3,mc=5,ms=8,ls=5,mo=5");
                graphsTrue.add(g);
            }
            GraphErrors g = dt.getTrackGraph(1, input, true);
            g.attr().set("lc=42,lw=1,mc=42,ms=8,mo=2");
            graphs.add(g);
        }
        
        List<Polygon> poly = dt.getSectorBoundaries();
        TGCanvas c = new TGCanvas(400,550);
        
        DriftChamberTools.setStyle(c);
        System.out.println();
        c.region().draw(graphs,"APL");
        c.region().draw(graphsTrue,"sameAPL");
        c.region().draw(poly);
        c.region().axisLimitsX(-90,90);
        //c.region().axisLimitsX(-100,100);
        c.view().left(10).right(10).bottom(10).top(10);
        c.repaint();
        c.view().export("track_classifier_"+order+".pdf", "pdf");
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/Software/project-10.8/study/instarec/cooked_data.h5";
        HipoReader r = new HipoReader(file);
        int order = 2;
        Bank[] b = r.getBanks("MLTR::Clusters","MLTR::Tracks","DC::tdc","REC::Track");
        Event event = new Event();
        
        r.getEvent(event, 2);        
        event.read(b);       
        DriftEventDisplay.showClusters(b[0], 1, 2);
        
        r.getEvent(event, 18);        
        event.read(b);       
        DriftEventDisplay.showClusters(b[0], 2, 18);
        
        r.getEvent(event, 32);        
        event.read(b);       
        DriftEventDisplay.showClusters(b[0], 4, 32);
        
        r.getEvent(event, 35);
        event.read(b);       
        DriftEventDisplay.showClusters(b[0], 2, 35);
        //InstaRec ir = new InstaRec();
        //ir.makePlot(b[0], b[1], b[2],b[3]);
        //ir.makePlot2D(b[2],b[0]);
    } 
}
