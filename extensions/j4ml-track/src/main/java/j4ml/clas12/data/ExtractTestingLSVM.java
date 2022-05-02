/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.data;

import j4ml.clas12.track.ClusterCombinations;
import j4ml.clas12.track.ClusterStore;
import j4ml.clas12.track.DCSector;
import j4ml.clas12.track.Track;
import j4np.hipo5.data.Bank;
import java.util.List;
import java.util.Random;


/**
 *
 * @author gavalian
 */
public class ExtractTestingLSVM extends ExtractModule {
    
    ClusterStore        cstore = new ClusterStore();
    ClusterCombinations combi = new ClusterCombinations();
    int linesWritten = 1;
    
    Random r = new Random();
    public ExtractTestingLSVM(String file){
        super(file);
    }
    
    public ExtractTestingLSVM(String... files){
        super(files);
    }
    
    @Override
    public void process(BankStore store) {        
        //System.out.println("-----");
        List<Track>  tracks = Track.read(
                store.getMap().get("TimeBasedTrkg::TBTracks"),
                store.getMap().get("TimeBasedTrkg::TBClusters")
        );
        
        for(int s = 1; s <= 6; s++ ){
            
            List<Track>  tSector = this.forSector(tracks, s);
            
            if(tSector.size()==1){
                //System.out.println("sector - " + s);
                //for(Track t : tSector) System.out.println(t);                
                
                List<Track> tc = Track.getComplete(tSector);
                List<Track> tv = Track.getValid(tc);
                /*System.out.println(" sector size = " + tSector.size() 
                        + "  complete " + tc.size() + " valid " + tv.size() );*/
                Bank bank = store.getMap().get("TimeBasedTrkg::TBClusters");
                //for(Track t : tv) System.out.println(t);
                if(tv.size()==1){
                    
                    bank.show();
                    cstore.reset();
                    int nrows = bank.getRows();
                    for(int r = 0; r < nrows; r++){
                        int cid     = bank.getInt("id",r);
                        int sector  = bank.getInt("sector",r);
                        int slayer  = bank.getInt("superlayer",r);
                        double wire = bank.getFloat("avgWire",r);
                        //System.out.printf("%3d %3d %3d %8.3f\n",cid,sector,slayer,wire);
                        if(sector==s){
                            System.out.printf("-- adding %d %d %d %8.4f\n",sector,cid,slayer,wire);
                            cstore.add(slayer-1, cid, wire);
                        }
                    }
                    
                    System.out.println("**************************");
                    System.out.println(cstore);
                    
                    int status = 1;
                    if(tv.get(0).charge<0) status = 2;
                    combi.reset();
                    //cstore.getCombinationsFullNoCut(combi);
                    cstore.getCombinationsFull(combi);
                    int index = combi.find(tv.get(0).clusters);
                    if(index>=0) combi.setRow(index).setStatus(status);
                    //System.out.println( cstore.toString());
                    if(combi.getSize()>15){
                        //System.out.println(tv.get(0));
                        //System.out.println(combi.getString(false));
                        for(int k = status-1; k < writers.size(); k+=2){ 
                            writers.get(k).writeString(String.format("------ %d , %d ", linesWritten, combi.getSize()));
                        }
                        linesWritten += 1 + combi.getSize();
                        for(int j = 0; j < combi.getSize(); j++){
                            DCSector sector = this.getSector(combi.getLabels(j), store);
                            if(status==1){
                                writers.get(0).writeString(
                                        String.format("%d %s", combi.setRow(j).getStatus(),
                                                reader.toDataString(sector.getFeatures6())));
                                writers.get(2).writeString(
                                        String.format("%d %s", combi.setRow(j).getStatus(),
                                                reader.toDataString(sector.getFeatures12())));
                                writers.get(4).writeString(
                                        String.format("%d %s", combi.setRow(j).getStatus(),
                                                reader.toDataString(sector.getFeatures36())));
                                writers.get(6).writeString(
                                        String.format("%d %s", combi.setRow(j).getStatus(),
                                                sector.getFeaturesStringExtended()));
                            } else {
                                writers.get(1).writeString(
                                        String.format("%d %s", combi.setRow(j).getStatus(),
                                                reader.toDataString(sector.getFeatures6())));
                                writers.get(3).writeString(
                                        String.format("%d %s", combi.setRow(j).getStatus(),
                                                reader.toDataString(sector.getFeatures12())));
                                writers.get(5).writeString(
                                        String.format("%d %s", combi.setRow(j).getStatus(),
                                                reader.toDataString(sector.getFeatures36())));
                                writers.get(7).writeString(
                                        String.format("%d %s", combi.setRow(j).getStatus(),
                                                sector.getFeaturesStringExtended()));
                            }
                        }
                    }
                    
                    
                }
            }
        }        
    }
    
}
