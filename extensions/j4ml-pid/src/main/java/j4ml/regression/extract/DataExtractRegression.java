/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.regression.extract;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DataExtractRegression {
    
    public int  countByCharge(List<Track> trkc, int charge){
        int c = 0;
        for(Track t : trkc){
            if(t.charge==charge) c++;
        }
        return c;
    }
    
    public int indexByCharge(List<Track> trkc, int charge){
        for(int i = 0; i < trkc.size(); i++){
            if(trkc.get(i).charge==charge) return i;
        }
        return -1;
    }
    
    public void extract(String file,int max){
        HipoReader r = new HipoReader(file);
        // structures
        //Bank    bt = r.getBank("TimeBasedTrkg::TBTracks");
        //Bank    bc = r.getBank("TimeBasedTrkg::TBClusters");
        
        Bank    bt = r.getBank("HitBasedTrkg::HBTracks");
        Bank    bc = r.getBank("HitBasedTrkg::HBClusters");
        Event   ev = new Event();
        int counter = 0;    
        while(r.hasNext()){
            r.next(ev);
            ev.read(bc);
            ev.read(bt);
            List<Track> trk = Track.read(bt, bc);
            List<Track> trkc = Track.getComplete(trk);
            if(trkc.size()==2){
                int in = indexByCharge(trkc,-1);
                int ip = indexByCharge(trkc, 1);
                if(in>=0&&ip>=0){
                    System.out.println(trkc.get(in) + "  0.0 0.0 0.0");
                    System.out.println(trkc.get(ip) + "  0.0 0.0 0.0");
                    //for(Track t : trkc)
                      //  System.out.println(t);
                }
            }
            counter++;
            if(max>0&&counter>max) break;
        }
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/dataspace/regression/rec_epi_0002_000_nA.filtered.hb.hipo";
        
        DataExtractRegression ext = new DataExtractRegression();
        ext.extract(file,-1);
    }
}
