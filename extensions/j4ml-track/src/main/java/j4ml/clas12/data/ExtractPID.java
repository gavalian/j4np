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
public class ExtractPID extends ExtractModule {
    
    public ExtractPID(String... files){
        super(files);
    }
    
    @Override
    public void process(BankStore store) {
        List<Track>  tracks = Track.read(
                store.getMap().get("TimeBasedTrkg::TBTracks"),
                store.getMap().get("TimeBasedTrkg::TBClusters")
        );
        
        for(Track t : tracks ){
            System.out.println(t);
        }
    }
    
    public static void main(String[] args){
        ExtractTracking extract = new ExtractTracking();
        extract.addModule(new ExtractPID(
                "particle_identification.csv"));
        
        List<String> inputs = new ArrayList<>();
        
        inputs.addAll(Arrays.asList(               
                "/Users/gavalian/Work/DataSpace/pid/rec_out_electron.hipo"
        ));
        extract.processFile(inputs);
    }
}
