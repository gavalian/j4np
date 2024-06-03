/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.showcase;

import j4np.geom.detector.dc.DriftChambers;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.List;
import twig.data.GraphErrors;
import twig.publish.DriftChamberTools;
import twig.widgets.Polygon;


/**
 *
 * @author gavalian
 */
public class DriftChamberStore {
    public GraphErrors dcHits = null;
    public List<GraphErrors> dcTracks = new ArrayList<>();
    public Bank dcBank = null;
    public CompositeNode node = new CompositeNode();
    public DriftChamberTools tools = new DriftChamberTools();
    public List<Polygon>    boundaries = null;
    
    public DriftChamberStore(HipoReader r){
        dcBank = r.getBank("DC::tdc");
        node = new CompositeNode(32100,2,"ssfff",4096);
        boundaries = tools.getBoundaries();
        for(Polygon p : boundaries) { p.attrFill().setFillStyle(-1); p.attrFill().setFillColor(-1);}
    }
    
    public void apply(Event e){
        
        e.read(dcBank);
        if(dcBank.getRows()>0){
            int[] sector = dcBank.getInt("sector");
            int[] layer  = dcBank.getInt("layer");
            int[] wire   = dcBank.getInt("component");
            dcHits = tools.getGraph(sector, layer, wire);
        } else { dcHits = new GraphErrors();}
        
        e.read(node,32100,2);
        
        dcTracks.clear();
        double[] clusters = new double[6];
        for(int r = 0; r < node.getRows(); r++){
            int sec = node.getInt(1, r);
            for(int c = 0; c < 6; c++) clusters[c] = node.getDouble(c+10, r);
            dcTracks.add(tools.getTrackGraph(sec, clusters, true));
        }
        //System.out.println("ROWS : " + node.getRows());
        
    }
    
    
}
