/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.regression;

import j4ml.common.Track;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.physics.LorentzVector;
import j4np.physics.Vector3;
import j4np.physics.VectorOperator;
import j4np.physics.data.PhysDataEvent;
import j4np.physics.store.EventModifierStore;
import j4np.physics.store.EventModifierStore.EventModifierForward;
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
    
    
    public void extractMCsingle(String file,int max){
        
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
        Bank    mc = r.getBank("MC::Particle");
        LorentzVector vCM = LorentzVector.withPxPyPzM(0.0,0.0, 6.6, 0.0005);
        vCM.add(0.0,0.0,0.0, 0.938);
        VectorOperator vop = new VectorOperator(vCM,"-[11]-[211]");
        
        PhysDataEvent phys = new PhysDataEvent(mc);
        int counter = 0;    
        
        while(r.hasNext()){
        
            r.next(ev);
            ev.read(bc);
            ev.read(bt);
            ev.read(part);
            
            phys.read(ev);
            vop.apply(phys);
            
            //double mass = vop.getValue(VectorOperator.OperatorType.MASS);
            //System.out.println(" mass = " + mass);
            
            
            //int pid = part.getInt("pid", 0);
            //int status = part.getInt("status", 0);
            //int npart = countCharged(part);
            
            List<Track>  trk = Track.read(bt, bc);
            List<Track> trkc = Track.getComplete(trk);
            int reaction = 0;
            //System.out.printf("track = %d, tracks valid = %d, charged = %d\n",trk.size(), trkc.size(),npart);
            for(int kk = 0; kk < trkc.size(); kk++ )
                w.writeString(String.format("%s",trkc.get(kk).toString()));
            
            counter++;
            if(max>0&&counter>max) break;
        }
        w.close();
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
        Bank    mc = r.getBank("MC::Particle");
        LorentzVector vCM = LorentzVector.withPxPyPzM(0.0,0.0, 6.6, 0.0005);
        vCM.add(0.0,0.0,0.0, 0.938);
        VectorOperator vop = new VectorOperator(vCM,"-[11]-[211]");
        
        PhysDataEvent phys = new PhysDataEvent(mc);
        int counter = 0;    
        
        while(r.hasNext()){
        
            r.next(ev);
            ev.read(bc);
            ev.read(bt);
            ev.read(part);
            
            phys.read(ev);
            vop.apply(phys);
            
            double mass = vop.getValue(VectorOperator.OperatorType.MASS);
            //System.out.println(" mass = " + mass);
            if(part.getRows()>0&&mass>0.9){
            
                int pid = part.getInt("pid", 0);
                int status = part.getInt("status", 0);
                int npart = countCharged(part);
                
                if(pid==11&&Math.abs(status)>=2000&&Math.abs(status)<3000&&npart==2){
                    
                    List<Track>  trk = Track.read(bt, bc);
                    List<Track> trkc = Track.getComplete(trk);
                    int reaction = 0;
                    if(mass<0.95) reaction = 1;
                    //System.out.printf("track = %d, tracks valid = %d, charged = %d\n",trk.size(), trkc.size(),npart);
                    if(trkc.size()==2){
                        int in = indexByCharge(trkc,-1);
                        int ip = indexByCharge(trkc, 1);
                        if(in>=0&&ip>=0){
                           // w.writeString(String.format("%d %s",reaction,trkc.get(in).toString()));
                           // w.writeString(String.format("%d %s",reaction,trkc.get(ip).toString()));
                           w.writeString(String.format("%s",trkc.get(in).toString()));
                           w.writeString(String.format("%s",trkc.get(ip).toString()));
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
        
        
        Bank    mc = r.getBank("MC::Particle");
        LorentzVector vCM = LorentzVector.withPxPyPzM(0.0,0.0, 6.6, 0.0005);
        vCM.add(0.0,0.0,0.0, 0.938);
        VectorOperator vop = new VectorOperator(vCM,"-[11]-[211]");
        
        PhysDataEvent phys = new PhysDataEvent(mc);
        
        int counter = 0;    
        while(r.hasNext()){
            r.next(ev);
            ev.read(bc);
            ev.read(bt);
            
            phys.read(ev);
            vop.apply(phys);
            double mass = vop.getValue(VectorOperator.OperatorType.MASS);
            List<Track>  trk = Track.read(bt, bc);
            List<Track> trkc = Track.getComplete(trk);
            if(trkc.size()==3&&mass>0.9){
                int in  = indexByCharge(trkc,-1);
                int in2 = indexByCharge(trkc,-1,1);
                int ip  = indexByCharge(trkc, 1);
                int reaction = 0;
                if(mass<0.95) reaction = 1;
                if(in>=0&&in2>=0&&ip>=0){
                    //w.writeString(String.format("%d %s",reaction,trkc.get(in).toString()));
                    //w.writeString(String.format("%d %s",reaction,trkc.get(in2).toString()));
                    //w.writeString(String.format("%d %s",reaction,trkc.get(ip).toString()));
                    
                    w.writeString(String.format("%s",trkc.get(in).toString()));
                    w.writeString(String.format("%s",trkc.get(in2).toString()));
                    w.writeString(String.format("%s",trkc.get(ip).toString()));
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
    
    public String vecStr(Vector3 v){
        return String.format("%9.4f %9.4f %9.4f", 
                v.mag(), 
                Math.toDegrees(v.theta()),
                Math.toDegrees(v.phi())
        );
    }
    
    public void extractTwo(String file,int max){        
    
        HipoReader r = new HipoReader(file);
        TextFileWriter w = new TextFileWriter();
        
        EventModifierForward fwd = new EventModifierStore.EventModifierForward();
        w.open(outputFile);
        // structures
        //Bank    bt = r.getBank("TimeBasedTrkg::TBTracks");
        //Bank    bc = r.getBank("TimeBasedTrkg::TBClusters");
        
        Bank    bth = r.getBank(inputBanks.get(0)[0]);
        Bank    bch = r.getBank(inputBanks.get(0)[1]);
        
        Bank    btt = r.getBank(inputBanks.get(1)[0]);
        Bank    bct = r.getBank(inputBanks.get(1)[1]);
        
        Event   ev = new Event();
        
        
        Bank    part = r.getBank("REC::Particle");
        Bank    mc   = r.getBank("MC::Particle");
        
        LorentzVector vCM = LorentzVector.withPxPyPzM(0.0,0.0, 6.6, 0.0005);
        vCM.add(0.0,0.0,0.0, 0.938);
        VectorOperator vop = new VectorOperator(vCM,"-[11]-[211]");
        
        PhysDataEvent physmc = new PhysDataEvent(mc);        
        PhysDataEvent physdt = new PhysDataEvent(part);
        
        int counter = 0;    
        
        while(r.hasNext()){
        
            r.next(ev);
            ev.read(bch);
            ev.read(bth);
            ev.read(bct);
            ev.read(btt);            
            ev.read(part);            
            physmc.read(ev);
            physdt.read(ev);                        
            vop.apply(physmc);
            
            fwd.modify(physdt);
            
            int pid = part.getInt("pid", 0);
            int status = part.getInt("status", 0);
            int npart = countCharged(part);
                
            if(physdt.countByCharge(-1)==1&&physdt.countByCharge(+1)==1
                    &&pid==11&&Math.abs(status)>=2000&&Math.abs(status)<3000){            
                double mass = vop.getValue(VectorOperator.OperatorType.MASS);
                List<Track> trkh = Track.read(bth, bch);
                List<Track> trkt = Track.read(btt, bct);
                
                List<Track> trkhc = Track.getComplete(trkh);
                List<Track> trktc = Track.getComplete(trkt);
                /*
                System.out.println("event-");
                System.out.println(physdt.toLundString());
                System.out.println(physmc.toLundString());
                System.out.println("--- hb");
                trkh.stream().forEach(System.out::println);
                System.out.println("--- tb");
                trkt.stream().forEach(System.out::println);
                
                System.out.println("--- hb complete");
                trkhc.stream().forEach(System.out::println);
                System.out.println("--- tb complete");
                trktc.stream().forEach(System.out::println);
                */
                
                int reaction = 0;
                if(mass>0.92&&mass<0.940) reaction = 1;
                
                int in_t = indexByCharge(trktc,-1);
                int ip_t = indexByCharge(trktc, 1);
                        
                int in_h = indexByCharge(trkhc,-1);
                int ip_h = indexByCharge(trkhc, 1);
                
                if(trkhc.size()==2&&trktc.size()==2&&
                        in_t>=0&&ip_t>=0&&in_h>=0&&ip_h>=0){
                    
                    Vector3 vmce = new Vector3();
                    Vector3 vmcp = new Vector3();
                    physmc.vector(vmce,  11, 0);
                    physmc.vector(vmcp, 211, 0);
                    StringBuilder stre = new StringBuilder();
                    stre.append(String.format("%1d ", reaction))
                            .append(trktc.get(in_t))                            
                            .append(" hb: ")
                            .append(vecStr(trkhc.get(in_h).vector))
                            .append(" mc: ")
                            .append(vecStr(vmce));
                            
                    StringBuilder strp = new StringBuilder();
                    strp.append(String.format("%1d ", reaction))
                            .append(trktc.get(ip_t))
                            .append(" hb: ")
                            .append(vecStr(trkhc.get(ip_h).vector))
                            .append(" mc: ")
                            .append(vecStr(vmcp));
                    //System.out.println("--------   STREAM ");
                    //System.out.println(stre.toString());
                    //System.out.println(strp.toString());
                    w.writeString(stre.toString());
                    w.writeString(strp.toString());                    
                }
            }
            
            
//System.out.println(" mass = " + mass);
            /*if(part.getRows()>0&&mass>0.9){
            
                int pid = part.getInt("pid", 0);
                int status = part.getInt("status", 0);
                int npart = countCharged(part);
                
                if(pid==11&&Math.abs(status)>=2000&&Math.abs(status)<3000&&npart==2){
                    
                    List<Track>  trk = Track.read(bt, bc);
                    List<Track> trkc = Track.getComplete(trk);
                    int reaction = 0;
                    if(mass<0.95) reaction = 1;
                    //System.out.printf("track = %d, tracks valid = %d, charged = %d\n",trk.size(), trkc.size(),npart);
                    if(trkc.size()==2){
                        int in = indexByCharge(trkc,-1);
                        int ip = indexByCharge(trkc, 1);
                        if(in>=0&&ip>=0){
                            w.writeString(String.format("%d %s",reaction,trkc.get(in).toString()));
                            w.writeString(String.format("%d %s",reaction,trkc.get(ip).toString()));
                            //System.out.println(trkc.get(in));
                            //System.out.println(trkc.get(ip));
                            //for(Track t : trkc)
                            //  System.out.println(t);
                        }
                    }
                }
            }            */
            counter++;
            if(max>0&&counter>max) break;
        }
        w.close();
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/DataSpace/regression/rec_epi_0002_000_nA_tor_m_1.hipo_filtered.h5";
        
        DataExtractRegression ext = new DataExtractRegression();
        ext.setDCLevel(1);
        ext.extractTwo(file,2000);
    }
}
