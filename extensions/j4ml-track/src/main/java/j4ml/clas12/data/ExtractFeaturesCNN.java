/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.data;

import j4ml.clas12.track.DCSector;
import j4ml.clas12.track.Track;
import java.util.List;
import java.util.Random;

/**
 *
 * @author gavalian
 */
public class ExtractFeaturesCNN extends ExtractModule {
    Random r = new Random();
    
    public ExtractFeaturesCNN(String file){
        super(file);
    }
    
    public ExtractFeaturesCNN(String... files){
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
            if(tSector.size()==2){
                //System.out.println("sector - " + s);
                //for(Track t : tSector) System.out.println(t);
                List<Track> tc = Track.getComplete(tSector);
                List<Track> tv = Track.getValid(tc);
                //for(Track t : tv) System.out.println(t);
                if(tv.size()==2){
                    int label_1 = 1;
                    int label_2 = 1;
                    
                    if(tv.get(0).charge<0) label_1 = 2;
                    if(tv.get(1).charge<0) label_2 = 2;
                    
                    DCSector dc_1 = this.getSector(tv.get(0).clusters,store);                    
                    //dc_1.show();
                    DCSector dc_2 = this.getSector(tv.get(1).clusters,store);
                    //dc_2.show();
                    int howMany = r.nextInt(2) + 1;
                    int[] clusters_1_2 = this.swap(tv.get(0).clusters,
                            tv.get(1).clusters
                            , howMany);
                    int[] clusters_2_1 = this.swap(tv.get(1).clusters,
                            tv.get(0).clusters
                            , howMany);
                    
                    DCSector dc_1_2 = this.getSector(clusters_1_2,store);
                    DCSector dc_2_1 = this.getSector(clusters_2_1,store);
                    
                    //dc_1_2.show();
                    //dc_2_1.show();

                    String data1 = String.format("%d %s",label_1,
                            dc_1.getFeaturesStringExtended());
                    String data2 = String.format("%d %s",label_2,
                            dc_2.getFeaturesStringExtended());

                    String data12 = String.format("%d %s",0,
                            dc_1_2.getFeaturesStringExtended());
                    String data21 = String.format("%d %s",0,
                            dc_2_1.getFeaturesStringExtended());
                    //System.out.println("----- sector = " + s);
                    //System.out.println(data1);
                    //System.out.println(data2);
                    //System.out.println(data12);
                    //System.out.println(data21);
                    if(tv.get(0).charge>0){
                        writers.get(0).writeString(data1);
                        writers.get(0).writeString(data12);
                    } else {
                        writers.get(1).writeString(data1);
                        writers.get(1).writeString(data12);
                    }
                    if(tv.get(1).charge>0){
                        writers.get(0).writeString(data2);
                        writers.get(0).writeString(data21);
                    } else {
                        writers.get(1).writeString(data2);
                        writers.get(1).writeString(data21);
                    }
                                        
                }
            }
        }        
    }
    
}
