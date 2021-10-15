/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.data;

import j4ml.clas12.track.Track;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class ExtractMonteCarloTracks extends ExtractModule {
    
    public ExtractMonteCarloTracks(String file){
        super(file);
    }
    
    @Override
    public void process(BankStore store) {
        
        
        int nrows = store.getMap().get("TimeBasedTrkg::TBClusters").getRows();
        int ntracks = store.getMap().get("TimeBasedTrkg::TBTracks").getRows();
        
        int nsegments = 0;
        if(ntracks==1){
            int c1 = store.getMap().get("TimeBasedTrkg::TBTracks").getInt("Cluster1_ID", 0);
            int c2 = store.getMap().get("TimeBasedTrkg::TBTracks").getInt("Cluster2_ID", 0);
            int c3 = store.getMap().get("TimeBasedTrkg::TBTracks").getInt("Cluster3_ID", 0);
            int c4 = store.getMap().get("TimeBasedTrkg::TBTracks").getInt("Cluster4_ID", 0);
            int c5 = store.getMap().get("TimeBasedTrkg::TBTracks").getInt("Cluster5_ID", 0);
            int c6 = store.getMap().get("TimeBasedTrkg::TBTracks").getInt("Cluster6_ID", 0);
            if(c1>0&&c2>0&&c3>0&&c4>0&&c5>0&&c6>0&&nrows==6){
                double[] values = new double[6];
                for(int loop = 0; loop < nrows; loop++){
                    int id = store.getMap().get("TimeBasedTrkg::TBClusters").getInt("id", loop);
                    double wire = store.getMap().get("TimeBasedTrkg::TBClusters").getFloat("avgWire",loop);
                    if(id==c1) values[0] = wire;
                    if(id==c2) values[1] = wire;
                    if(id==c3) values[2] = wire;
                    if(id==c4) values[3] = wire;
                    if(id==c5) values[4] = wire;
                    if(id==c6) values[5] = wire;
                }
                
                String data = String.format("1 1:%.4f 2:%.4f 3:%.4f 4:%.4f 5:%.4f 6:%.4f", 
                        values[0]/112,values[1]/112,values[2]/112.0,
                        values[3]/112,values[4]/112,values[5]/112.0
                        );
                String dataCSV = String.format("1,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f",
                        values[0]/112,values[1]/112,values[2]/112.0,
                        values[3]/112,values[4]/112,values[5]/112.0
                        );
                //System.out.println(data);
                writers.get(0).writeString(data);
            }
        }
        
    }
    
    public static void main(String[] args){
        List<String> inputs = new ArrayList<>();
        
        inputs.addAll(Arrays.asList(               
                "/Users/gavalian/Work/DataSpace/pid/rec_training.hipo"
        ));
        /*
        inputs.addAll(Arrays.asList(               
                "rec_training.hipo"
        ));*/
        
        ExtractTracking extract = new ExtractTracking();
        extract.addModule(new ExtractMonteCarloTracks(
                "tracks_montecarlo_neg.lsvm"));

        extract.processFile(inputs);
    }
}
