/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.particle;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class ClasReader {
    
    private Bank      bPART = null;
    private Bank      bCALO = null;
    private Bank      bCALI = null;
    private Bank      bCHER = null;
    
    public ClasReader(){
        
    }
    
    public void init(HipoReader r){
        bPART = r.getBank("REC::Particle");
        bCALO = r.getBank("REC::Calorimeter");
        bCALI = r.getBank("ECAL::calib");
        bCHER = r.getBank("REC::Cherenkov");
    }
    
    public List<ClasParticle> read(Event event){        
        List<ClasParticle>  partList = new ArrayList<>();
        event.read(bCHER); event.read(bCALO);
        event.read(bCALI); event.read(bPART);
        int nrows = bPART.getRows();
        for(int i = 0; i < nrows; i++){
            ClasParticle p = ClasParticle.initFromBanks(bPART, bCALO, bCALI, bCHER, i);
            partList.add(p);
        }
        return partList;
    }
    
    public static List<ClasParticle> importData(String f, int max){
        List<ClasParticle> list = new ArrayList<>();
        HipoReader r = new HipoReader(f);
        Event ev = new Event();
        ClasReader cr = new ClasReader();
        
        cr.init(r);
        int counter = 0;
        while(r.hasNext()){
            counter++;
            if(counter>max&&max>0) break;
        //for(int i = 0; i < 2000; i++){
            r.nextEvent(ev);
            List<ClasParticle> cpl = cr.read(ev);
            list.addAll(cpl);
        }
        return list;
    }
    public static void main(String[] args){
        String f = "/Users/gavalian/Work/software/project-10a.0.4/data/pid/pid_elec.hipo";
        HipoReader r = new HipoReader(f);
        Event ev = new Event();
        ClasReader cr = new ClasReader();
        
        cr.init(r);
        
        for(int i = 0; i < 2000; i++){
            r.nextEvent(ev);
            List<ClasParticle> cpl = cr.read(ev);
            for(ClasParticle cp : cpl)
                if(cp.getCharge()!=0)
                    System.out.println(cp);
        }
    }
}
