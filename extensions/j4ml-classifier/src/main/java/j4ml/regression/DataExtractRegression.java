/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.regression;

import j4ml.common.Track;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DataExtractRegression {
    
    public String          outputFile = "extracted_regression.txt";
    public List<String[]>  inputBanks = new ArrayList<>();
    public int                dcLevel = 0;
    
    
    public DataExtractRegression(){
                
        inputBanks.add(new String[]{
            "HitBasedTrkg::HBTracks",
            "HitBasedTrkg::HBClusters"}
        );
        
        inputBanks.add(new String[]{
            "TimeBasedTrkg::TBTracks",
            "TimeBasedTrkg::TBClusters"});
    }
    
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
    
    public int indexByCharge(List<Track> trkc, int charge, int skip){
        int order = 0;
        for(int i = 0; i < trkc.size(); i++){
            if(trkc.get(i).charge==charge){
                if(order==skip){
                    return i;
                } else { order++;}
            }
        }
        return -1;
    }
    
    public void setDCLevel(int level){
        dcLevel = level;
    }
    
    public int countCharged(Bank b){
        int c = 0;
        for(int i =0; i< b.getRows(); i++){
            if(b.getInt("charge", i)!=0) c++;
        } 
        return c;
    }
    
    public void extract(String file,int max){
        
        HipoReader r = new HipoReader(file);
        TextFileWriter w = new TextFileWriter();
        
        w.open(outputFile);
        // structures
        //Bank    bt = r.getBank("TimeBasedTrkg::TBTracks");
        //Bank    bc = r.getBank("TimeBasedTrkg::TBClusters");
        
        Bank    bt = r.getBank(inputBanks.get(dcLevel)[0]);
        Bank    bc = r.getBank(inputBanks.get(dcLevel)[1]);        
        Event   ev = new Event();
        
        
        Bank    part = r.getBank("REC::Particle");
        
        int counter = 0;    
        while(r.hasNext()){
            r.next(ev);
            ev.read(bc);
            ev.read(bt);
            ev.read(part);
            
            
            
            
            if(part.getRows()>0){
            
                int pid = part.getInt("pid", 0);
                int status = part.getInt("status", 0);
                int npart = countCharged(part);
                if(pid==11&&Math.abs(status)>=2000&&Math.abs(status)<3000&&npart==2){
                    
                    List<Track>  trk = Track.read(bt, bc);
                    List<Track> trkc = Track.getComplete(trk);
                    
                    //System.out.printf("track = %d, tracks valid = %d, charged = %d\n",trk.size(), trkc.size(),npart);
                    if(trkc.size()==2){
                        int in = indexByCharge(trkc,-1);
                        int ip = indexByCharge(trkc, 1);
                        if(in>=0&&ip>=0){
                            w.writeString(trkc.get(in).toString());
                            w.writeString(trkc.get(ip).toString());
                            //System.out.println(trkc.get(in));
                            //System.out.println(trkc.get(ip));
                            //for(Track t : trkc)
                            //  System.out.println(t);
                        }
                    }
                }
            }            
            counter++;
            if(max>0&&counter>max) break;
        }
        w.close();
    }
    
    public void extractThree(String file,int max){
        
        HipoReader r = new HipoReader(file);
        TextFileWriter w = new TextFileWriter();
            
        w.open(outputFile);
        // structures
        //Bank    bt = r.getBank("TimeBasedTrkg::TBTracks");
        //Bank    bc = r.getBank("TimeBasedTrkg::TBClusters");
        
        Bank    bt = r.getBank(inputBanks.get(dcLevel)[0]);
        Bank    bc = r.getBank(inputBanks.get(dcLevel)[1]);        
        Event   ev = new Event();
        
        int counter = 0;    
        while(r.hasNext()){
            r.next(ev);
            ev.read(bc);
            ev.read(bt);
            List<Track>  trk = Track.read(bt, bc);
            List<Track> trkc = Track.getComplete(trk);
            if(trkc.size()==3){
                int in  = indexByCharge(trkc,-1);
                int in2 = indexByCharge(trkc,-1,1);
                int ip  = indexByCharge(trkc, 1);
                if(in>=0&&in2>=0&&ip>=0){
                    w.writeString(trkc.get(in).toString());
                    w.writeString(trkc.get(in2).toString());
                    w.writeString(trkc.get(ip).toString());
                    //System.out.println(trkc.get(in));
                    //System.out.println(trkc.get(ip));
                    //for(Track t : trkc)
                      //  System.out.println(t);
                }
            }
            counter++;
            if(max>0&&counter>max) break;
        }
        w.close();
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/dataspace/regression/rec_epi_0002_000_nA.filtered.hb.hipo";
        
        DataExtractRegression ext = new DataExtractRegression();
        ext.setDCLevel(1);
        ext.extract(file,2000);
    }
}
