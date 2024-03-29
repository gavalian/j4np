/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.data;

import j4ml.clas12.track.DCSector;
import j4ml.clas12.track.Track;
import j4np.utils.io.DataArrayUtils;
import java.util.List;
import java.util.Random;

/**
 *
 * @author gavalian
 */
public class ExtractPhysicsCSV extends ExtractModule {
    Random r = new Random();
    public ExtractPhysicsCSV(String file){
        super(file);
        System.out.print("REG_POS:p,theta,phi,");
        for(int i = 0; i < 36; i++){
            if(i!=0) {
                System.out.printf(",f%d",i+1);
            } else {
                System.out.printf("f%d",i+1);
            }
        }
        System.out.println();
    }
    
    public ExtractPhysicsCSV(String... files){
        super(files);
    }
    
    public int countCharge(List<Track> tracks, int charge){
        int count = 0;
        for(Track t : tracks) if(t.charge==charge) count++;
        return count;
    }
    
    public int indexCharge(List<Track> tracks, int charge){
        int count = 0;
        for(Track t : tracks) if(t.charge==charge) count++;
        return count;
    }
    
    @Override
    public void process(BankStore store) {        
        //System.out.println("-----");
        List<Track>  tracks = Track.read(
                store.getMap().get("TimeBasedTrkg::TBTracks"),
                store.getMap().get("TimeBasedTrkg::TBClusters")
        );
        int pid = 0;
        
        if(store.getMap().get("REC::Particle").getRows()>0){
           pid =  store.getMap().get("REC::Particle").getInt("pid", 0);
        }
        if(pid!=11) return;
        if(tracks.size()==2){
            
        
            List<Track> tc = Track.getComplete(tracks);
            List<Track> tv = Track.getValid(tc);
            
            if(tv.size()==2){
                int n_count = countCharge(tv,-1);
                int p_count = countCharge(tv, 1);
                if(n_count==1&&p_count==1){
                    int n_index = 0;
                    int p_index = 1;
                    if(tv.get(0).charge>0){
                        n_index = 1; p_index = 0;
                    }
                    
                    DCSector sec_pos = getSector(tv.get(p_index).clusters,store);
                    DCSector sec_neg = getSector(tv.get(n_index).clusters,store);
                    
                    
                    int sector_pos = tv.get(p_index).sector-1;
                    double[] features = sec_pos.getFeatures36();
                    double[] sector_features = new double[36];//sec_pos.getFeatures36();
                    double norm = 112*6;
                    for(int kk = 0; kk < 36; kk++){
                        sector_features[kk] = features[kk]*112 + sector_pos*112;
                        sector_features[kk] = sector_features[kk]/norm;
                    }
                    if(sector_pos==3) System.out.printf("REG_POS:%s,%s\n",DataArrayUtils.doubleToString(tv.get(p_index).features(),","),
                             DataArrayUtils.doubleToString(features, ","));
                    
                    String particles = String.format("%d,%d,%s,%d,%d,%s,", 
                            tv.get(n_index).charge,tv.get(n_index).sector,
                            DataArrayUtils.doubleToString(tv.get(n_index).features(),","),
                            tv.get(p_index).charge,tv.get(p_index).sector,
                            DataArrayUtils.doubleToString(tv.get(p_index).features(),",")                            
                            );
                    
                    String data = String.format("%s,%s,%s",particles,
                            DataArrayUtils.doubleToString(sec_neg.getFeatures36(), ","),
                            DataArrayUtils.doubleToString(sec_pos.getFeatures36(), ",")
                            );
                    //System.out.println(data);
                    writers.get(0).writeString(data);
                }
            }
        }
        
    }
    
}
