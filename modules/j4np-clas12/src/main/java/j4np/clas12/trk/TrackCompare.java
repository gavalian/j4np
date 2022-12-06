/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.trk;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.List;
import twig.data.H1F;
import twig.data.TDirectory;

/**
 *
 * @author gavalian
 */
public class TrackCompare {
    
    public Bank  trk1 = null;
    public Bank trkc1 = null;
    
    
    public Bank  trk2 = null;
    public Bank trkc2 = null;
    
    TrackSelector selector6 = null;
    TrackSelector selector5 = null;
    
    
    H1F[] f1_h6 = new H1F[6];//("h6",8,0.5,8.5);
    H1F[] f1_h5 = new H1F[6];//("h5",8,0.5,8.5);
    
    H1F[] f2_h6 = new H1F[6];//("h6",8,0.5,8.5);
    H1F[] f2_h5 = new H1F[6];//("h5",8,0.5,8.5);
    
    public TrackCompare() {
        
        for(int i = 0; i < 6; i++){
            int s = i+1;
            f1_h6[i] = new H1F("f1h6_"+s,8,0.5,8.5);
            f1_h5[i] = new H1F("f1h5_"+s,8,0.5,8.5);
            f2_h6[i] = new H1F("f2h6_"+s,8,0.5,8.5);
            f2_h5[i] = new H1F("f2h5_"+s,8,0.5,8.5);
        }
        
        selector6 = new TrackSelector() {
            @Override
            public boolean select(Track trk){
                if(trk.chi2>400) return false;
                if(trk.vector.mag()<0.5||trk.vector.mag()>10.0) return false;
                if(trk.vertex.z()<-15||trk.vertex.z()>5) return false;
                if(trk.clusterCount()!=6) return false;
                return true;
            }
        };
        
        selector5 = new TrackSelector() {
            @Override
            public boolean select(Track trk){
                if(trk.chi2>400) return false;
                if(trk.vector.mag()<0.5||trk.vector.mag()>10.0) return false;
                if(trk.vertex.z()<-15||trk.vertex.z()>5) return false;
                if(trk.clusterCount()!=5) return false;
                return true;
            }
        };
    }
    
    public int  find(Track t, List<Track> list){
        int max = 1;
        for(int i = 0; i < list.size(); i++){
            if(t.sector==list.get(i).sector){
                int match = t.matchSegments(list.get(i));
                if(match > max) max = match;
            }
        }
        return max;
    }
    
    public void analyze(String file1, String file2){
        HipoReader r1 = new HipoReader(file1);
        HipoReader r2 = new HipoReader(file2);
        
        trk1  = r1.getBank("TimeBasedTrkg::TBTracks");
        trkc1 = r1.getBank("TimeBasedTrkg::TBClusters");
        
        trk2  = r2.getBank("TimeBasedTrkg::TBTracks");
        trkc2 = r2.getBank("TimeBasedTrkg::TBClusters");
        
        Event event = new Event();
        
        while(r1.hasNext()==true&&r2.hasNext()==true){
            r1.nextEvent(event);
            
            event.read(trk1);
            event.read(trkc1);
            
            
            r2.nextEvent(event);
            
            event.read(trk2);
            event.read(trkc2);
            
            List<Track> tracks1 = Track.read(trk1, trkc1);
            
            List<Track> tracks1c6 = Track.filter(tracks1, selector6);            
            List<Track> tracks1c5 = Track.filter(tracks1, selector5);
            
            List<Track> tracks2 = Track.read(trk2, trkc2);
            
            List<Track> tracks2c6 = Track.filter(tracks2, selector6);            
            List<Track> tracks2c5 = Track.filter(tracks2, selector5);
            
            //System.out.println(" count = " + tracks1.size() + " : " + tracks1c.size());
                        
            
            for(Track t : tracks1c6){
                int match = this.find(t, tracks2);
                f1_h6[t.sector-1].fill(match);
                f1_h6[t.sector-1].fill(8.0);
            }
            
            for(Track t : tracks1c5){
                int match = this.find(t, tracks2);
                f1_h5[t.sector-1].fill(match);
                f1_h5[t.sector-1].fill(8.0);
            } 
            
            for(Track t : tracks2c6){
                int match = this.find(t, tracks1);
                f2_h6[t.sector-1].fill(match);
                f2_h6[t.sector-1].fill(8.0);
            }
            
            for(Track t : tracks2c5){
                int match = this.find(t, tracks1);
                f2_h5[t.sector-1].fill(match);
                f2_h5[t.sector-1].fill(8.0);
            }
        }
        
        System.out.println("1) for 6 sector 1 : \n" + f1_h6[0].show());
        System.out.println("1) for 5 sector 1 : \n" + f1_h5[0].show());
        System.out.println("2) for 6 sector 1 : \n" + f2_h6[0].show());
        System.out.println("2) for 5 sector 1 : \n" + f2_h5[0].show());
        
        TDirectory dir = new TDirectory();
        for(int i = 0; i < 6; i++){
            int s = i+1;
            dir.add("/compare/analysis", f1_h6[i]);
            dir.add("/compare/analysis", f2_h6[i]);
            dir.add("/compare/analysis", f1_h5[i]);
            dir.add("/compare/analysis", f2_h5[i]);
        }
        dir.write("analysis.twig");
    }
    
    public static void main(String[] args){
        

        String file1 = "/Users/gavalian/Work/DataSpace/rga/rec_005988.00005.00009.hipo";
        String file2 = "/Users/gavalian/Work/DataSpace/rga/rec_005988.00005.00009.hipo";
        
        if(args.length>1){
            file1 = args[0]; file2 = args[1];
        }
        
        TrackCompare tc = new TrackCompare();
        tc.analyze(file1, file2);
    }
}
