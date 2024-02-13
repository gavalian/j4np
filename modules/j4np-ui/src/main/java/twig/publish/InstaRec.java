/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.publish;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import twig.data.GraphErrors;
import twig.data.H1F;
import twig.data.H2F;
import twig.graphics.TGCanvas;
import twig.widgets.Polygon;

/**
 *
 * @author gavalian
 */
public class InstaRec {
    
    public GraphErrors getGraphClusters(Bank c){
        DriftChamberTools dt = new DriftChamberTools();
        int nrows = c.getRows();
        int[] sector = new int[nrows/3];
        int[]  layer = new int[nrows/3];
        int[]   wire = new int[nrows/3];
        
        int[] value = c.getInt("byte");
        int counter = 0;
        for(int i = 0; i < nrows; i+=3){
            int ii = c.getInt(0, i);
            sector[counter] = ii/10;
            layer[counter] = ii - 10*(ii/10);
            wire[counter] = c.getInt(0, i+1);
            //System.out.printf(" %d %d %d\n",sector[counter],layer[counter],wire[counter]);
            counter++;
        }
        
        GraphErrors g = dt.getGraph(sector, layer, wire);
        return g;
    }
    
    public H1F[] getClusterPositions(int l, Bank c){
        H1F[] h = H1F.duplicate(6, "clusters", 112,0.5,112.5);
        int nrows = c.getRows();
        int[] value = c.getInt("byte");
        int counter = 0;
        for(int i = 0; i < nrows; i+=3){
            int ii = c.getInt(0, i);
            int sector = ii/10;
            int layer = ii - 10*(ii/10);
            int wire = c.getInt(0, i+1);
            if(l==layer) h[sector-1].fill(wire);
            //System.out.printf(" %d %d %d\n",sector[counter],layer[counter],wire[counter]);
            counter++;
        }
        return h;
    }
    
    public List<GraphErrors> getGraphTracks(Bank t, Bank rec){
        DriftChamberTools dt = new DriftChamberTools();
        int nrows = t.getRows();
        Random r = new Random();
        List<GraphErrors> list = new ArrayList<>();
        int ntrk = nrows/6;
        double[] traj = new double[6];
        int counter = 0;
        for(int k = 0; k < ntrk; k++){
            for(int i = 0; i < 6; i++){
                traj[i] = t.getInt(0,counter); traj[i] /= 100.0;
                counter++;
            }
            int s = findSector(k,rec);
            System.out.printf("%d : (%d)  %s\n", k, s,Arrays.toString(traj));

            GraphErrors g = dt.getTrackGraph(s, traj, true);
            list.add(g);
        }
        
        return list;
    }
    
    public void makePlot(Bank c, Bank t, Bank dc, Bank rec){
        
        DriftChamberTools dt = new DriftChamberTools();
        GraphErrors g = this.getGraphClusters(c);
        List<GraphErrors> trk = this.getGraphTracks(t,rec);        
        List<Polygon> poly = dt.getBoundaries();        
        GraphErrors tdc = dt.getGraph(dc.getInt("sector"), dc.getInt("layer"), dc.getInt("component"));
        TGCanvas cv = new TGCanvas(800,800);
        //cv.draw(g); 
        cv.region().draw(poly);
        cv.region().draw(tdc,"sameP");
        cv.region().draw(trk, "samePL");

        DriftChamberTools.setStyle(cv);
    }
    
    public int findSector(int index, Bank b){
        for(int i = 0; i < b.getRows(); i++)
            if(b.getInt("index",i)==index) return b.getInt("sector",i);
        return -1;
    }
    
    public void makePlot2D(Bank dc, Bank cl){
        TGCanvas c = new TGCanvas(800,800);
        H2F[] h = DriftChamberTools.getHistosSector(dc.getInt("sector"), dc.getInt("layer"), dc.getInt("component"));
        H1F[] h1 = this.getClusterPositions(1, cl);
        c.view().divide(2,6);
        for(int i = 0; i < h.length; i++){
            H2F hc = h[i].crop(0, 0, 112, 6);
            c.cd(i).draw(hc);
        }        
        for(int i = 0; i < h1.length; i++){
            c.cd(i+6).draw(h1[i]);
        }
        
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/Software/project-10.8/study/instarec/cooked_data.h5";
        HipoReader r = new HipoReader(file);
        
        Bank[] b = r.getBanks("MLTR::Clusters","MLTR::Tracks","DC::tdc","REC::Track");
        Event event = new Event();
        r.getEvent(event, 2); // 15 is good
        
        event.read(b);
        
        InstaRec ir = new InstaRec();
        ir.makePlot(b[0], b[1], b[2],b[3]);
        //ir.makePlot2D(b[2],b[0]);
    }
}
