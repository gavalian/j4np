/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.store;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.physics.PDGDatabase;
import j4np.physics.PDGParticle;
import j4np.physics.Vector3;
import java.util.ArrayList;
import java.util.List;
import twig.config.TStyle;
import twig.data.DataGroup;
import twig.data.H1F;
import twig.data.H2F;
import twig.graphics.TGCanvas;
import twig.graphics.TTabCanvas;

/**
 *
 * @author gavalian
 */
public class ParticleEfficiency {
    int   particleid = 11;
    int   geantid = 0;
    int   charge = -1;
    
    Bank mcBank = null;
    Bank rcBank = null;
    
    List<DataGroup>  dataGroups = new ArrayList<>();
    public double matchCompare = 0.5;
    
    public ParticleEfficiency(int pid){
        this.particleid = pid;
        this.geantid    = PDGDatabase.getParticleById(pid).gid();
        this.charge     = PDGDatabase.getParticleById(pid).charge();
    }
    
    public List<DataGroup> getDataGroups(){ return this.dataGroups;}
    
    public void init(HipoReader r){
        mcBank = r.getBank("MC::Particle");
        rcBank = r.getBank("REC::Particle");
        dataGroups.clear();
        dataGroups.add(this.createGroup());
        dataGroups.add(this.createGroup2());
        dataGroups.add(this.createGroup3());
    }
    
    protected int findIndex(Bank part, Vector3 vec){
        int npart = part.getRows();
        int index = -1;
        double compare = 1000.0;
        
        Vector3 p = new Vector3();
        for(int i = 0; i < npart; i++){
            p.setXYZ(part.getFloat("px", i), part.getFloat("py", i),part.getFloat("pz", i));
            double dist = p.compare(vec);
            if(dist<compare) {compare = dist; index = i;}
        }
        return index;
    }
    
    public void process(Event e){
        
        e.read(mcBank,rcBank);
        Vector3 mcpart = new Vector3();
        Vector3 rcpart = new Vector3();
        for(int i = 0; i < mcBank.getRows(); i++){
            int mcpid = mcBank.getInt("pid", i);
            
            if(mcpid==this.particleid){
            
                mcpart.setXYZ(mcBank.getFloat("px", i), 
                        mcBank.getFloat("py", i), mcBank.getFloat("pz", i));
                
                ((H2F)dataGroups.get(0).getData().get(0)).fill(mcpart.phi(), mcpart.theta());
                ((H2F)dataGroups.get(0).getData().get(1)).fill(mcpart.mag(), mcpart.theta());
                
                ((H2F)dataGroups.get(1).getData().get(0)).fill(mcpart.phi(), this.geantid);
                ((H2F)dataGroups.get(1).getData().get(1)).fill(mcpart.theta(), this.geantid);
                ((H2F)dataGroups.get(1).getData().get(2)).fill(mcpart.mag(), this.geantid);
                
                //System.out.println(mcpart.phi()+" "+mcpart.theta());
                int index = this.findIndex(rcBank, mcpart);
                int pid0 = rcBank.getInt("pid", 0);
                
                if(index>=0&&pid0==11){
                    rcpart.setXYZ(
                            rcBank.getFloat("px", index), 
                            rcBank.getFloat("py", index),
                            rcBank.getFloat("pz", index)
                    );
                    double compare = rcpart.compare(mcpart);
                    
                    int rcpid = rcBank.getInt("pid", index);
                   
                    ((H2F)dataGroups.get(2).getData().get(3)).fill(mcpart.phi(), mcpart.phi()-rcpart.phi());
                    ((H2F)dataGroups.get(2).getData().get(4)).fill(mcpart.theta(), mcpart.theta()-rcpart.theta());
                    ((H2F)dataGroups.get(2).getData().get(5)).fill(mcpart.mag(), mcpart.mag()-rcpart.mag());
                    if(rcpid==this.particleid){
                        ((H2F)dataGroups.get(2).getData().get(0)).fill(mcpart.phi(), mcpart.phi()-rcpart.phi());
                        ((H2F)dataGroups.get(2).getData().get(1)).fill(mcpart.theta(), mcpart.theta()-rcpart.theta());
                        ((H2F)dataGroups.get(2).getData().get(2)).fill(mcpart.mag(), mcpart.mag()-rcpart.mag());
                    }
                    //System.out.println(" compare = " + compare);
                    //((H1F)dataGroups.get(0).getData().get(2)).fill(compare);
                    
                    if(compare<this.matchCompare){

                        //int rcpid = rcBank.getInt("pid", index);

                        PDGParticle pp = PDGDatabase.getParticleById(rcpid);
                        int rcgid = -1;
                        if(pp!=null) rcgid = pp.gid();
                        
                        ((H2F)dataGroups.get(1).getData().get(3)).fill(mcpart.phi(), rcgid);
                        ((H2F)dataGroups.get(1).getData().get(4)).fill(mcpart.theta(), rcgid);
                        ((H2F)dataGroups.get(1).getData().get(5)).fill(mcpart.mag(), rcgid);
                
                         ((H2F)dataGroups.get(0).getData().get(2)).fill(mcpart.phi(), mcpart.theta());
                         ((H2F)dataGroups.get(0).getData().get(3)).fill(mcpart.mag(), mcpart.theta());
                         

                         if(rcpid==mcpid){
                             ((H2F)dataGroups.get(0).getData().get(4)).fill(mcpart.phi(), mcpart.theta());
                             ((H2F)dataGroups.get(0).getData().get(5)).fill( mcpart.mag(),mcpart.theta());
                         }
                    }
                }
            }  
        }
    }
    
    public void complete(){
        
        H1F hn1 = ((H2F)dataGroups.get(0).getData().get(3)).projectionX();
        H1F hd1 = ((H2F)dataGroups.get(0).getData().get(1)).projectionX();
        H1F hr1 = H1F.divide(hn1, hd1);
        hr1.attr().setTitleX(String.format("acceptance (pid=%d)", this.particleid));
        hr1.attr().set("lc=2,lw=2");
        hr1.setName("Ratio 1");
        dataGroups.get(0).add(hr1, 6, "");
        
        H1F hn2 = ((H2F)dataGroups.get(0).getData().get(5)).projectionX();
        H1F hd2 = ((H2F)dataGroups.get(0).getData().get(1)).projectionX();
        H1F hr2 = H1F.divide(hn2, hd2);
        hr2.attr().set("lc=3,lw=2");
        hr2.setName("Ratio 2");
        dataGroups.get(0).add(hr2, 7, "same");
        hr2.attr().setTitleX(String.format("acc and pid (pid=%d)", this.particleid));
       
    }
    
    protected DataGroup createGroup(){
        DataGroup grp = new DataGroup(2,4);
        grp.setName(String.format("C [%d]", particleid));
        grp.add(new H2F("theta-phi-mc","r;#phi [rad]; #theta [rad]",
                80,-Math.PI,Math.PI,80,
                Math.toRadians(2.5),
                Math.toRadians(42.5)), 0, "");
        
        grp.add(new H2F("theta-p-mc","r;#phi [rad]; p [GeV]",80,0.2,10.2,
                80,Math.toRadians(2.5),
                Math.toRadians(42.5)), 1, "");
         
        //grp.add(new H1F("compare-all",120,0.0,1.0), 2, "");
        
        grp.add(new H2F("theta-phi-rec","r;#phi [rad]; #theta [rad]",80,-Math.PI,Math.PI,80,Math.toRadians(2.5),
                Math.toRadians(42.5)), 2, "");
        grp.add(new H2F("theta-p-rec","r;#phi [rad]; p [GeV]",80,0.2,10.2,80,Math.toRadians(2.5),
                Math.toRadians(42.5)), 3, "");
       
        //grp.add(new H1F("compare-pid",80,0.0,1.0), 5, "");
        
        grp.add(new H2F("theta-phi-rec-match","r;#phi [rad]; #theta [rad]",80,-Math.PI,Math.PI,80,Math.toRadians(2.5),
                Math.toRadians(42.5)), 4, "");
        grp.add(new H2F("theta-p-rec-match","r;#phi [rad]; p [GeV]",80,0.2,10.2,80,Math.toRadians(2.5),
                Math.toRadians(42.5)), 5, "");
        
        return grp;
    }
    
    protected DataGroup createGroup2(){
        
        DataGroup grp = new DataGroup(3,2);
                grp.setName(String.format("G [%d]", particleid));
        grp.add(new H2F("phi-pid-mc","r;#phi [rad];geant ID",80,-Math.PI,Math.PI,15,-1.5,13.5),0,"");
        grp.add(new H2F("theta-pid-mc","r; #theta [rad]; geant ID",80,
                Math.toRadians(2.5),Math.toRadians(42.5),15,-1.5,13.5),1,"");
        grp.add(new H2F("p-pid-mc","r; p [GeV]; geant ID",80,0.0,10.,15,-1.5,13.5),2,"");
        
        grp.add(new H2F("phi-pid-rec","r;#phi [rad];geant ID",80,-Math.PI,Math.PI,15,-1.5,13.5),3,"");
        
        grp.add(new H2F("theta-pid-rec","r;#theta [rad];geant ID",80,
                Math.toRadians(2.5),Math.toRadians(42.5),15,-1.5,13.5),4,"");
        grp.add(new H2F("p-pid-rec","r; p [GeV]; geant ID",80,0.0,10.,15,-1.5,13.5),5,"");
                
        return grp;
    }
    
    protected DataGroup createGroup3(){
        
        DataGroup grp = new DataGroup(3,2);
        
        double range = 0.05;
        grp.setName(String.format("S [%d]", particleid));
        
        grp.add(new H2F("phi-compare-pid","r;#phi [rad];geant ID",80,-Math.PI,Math.PI,80,-range,range),0,"");
        grp.add(new H2F("theta-compare-pid","r; #theta [rad]; geant ID",80,
                Math.toRadians(2.5),Math.toRadians(42.5),80,-range,range),1,"");
        
        grp.add(new H2F("p-compare-pid","r; p [GeV]; geant ID",80,0.0,10.,80,-range,range),2,"");
        
        grp.add(new H2F("phi-compare-all","r;#phi [rad];geant ID",80,-Math.PI,Math.PI,80,-range,range),3,"");
        
        grp.add(new H2F("theta-compare-all","r;#theta [rad];geant ID",80,
                Math.toRadians(2.5),Math.toRadians(42.5),80,-range,range),4,"");
        grp.add(new H2F("p-compare-all","r; p [GeV]; geant ID",80,0.0,10.,80,-range,range),5,"");
        
        return grp;
    }
    
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/Software/project-10.6/study/tmd/sidis_tmd.h5";
        HipoReader r = new HipoReader(file);
        ParticleEfficiency eff = new ParticleEfficiency(321);
        ParticleEfficiency effp = new ParticleEfficiency(211);
        eff.init(r);
        effp.init(r);
        TStyle.getInstance().getPalette().palette2d().setPalette("kRainBow");
        Event e = new Event();
        int counter = 0;
        while(r.hasNext()&&counter<40000000){
            r.next(e);
            eff.process(e);
            effp.process(e);
            counter++;            
        }
        
        eff.complete();
        effp.complete();
        
        
        TTabCanvas tc = new TTabCanvas();
        
        DataGroup.draw( eff.getDataGroups(),tc.getDataCanvas());
        DataGroup.draw(effp.getDataGroups(),tc.getDataCanvas());
        
        /*
        TGCanvas c = new TGCanvas(900,1200);
        eff.getDataGroups().get(0).draw(c.view(),true);
        
        TGCanvas cp = new TGCanvas(900,1200);
        effp.getDataGroups().get(0).draw(cp.view(),true);
        
        
        TGCanvas c3 = new TGCanvas(900,900);
        eff.getDataGroups().get(1).draw(c3.view(), true);
        
        TGCanvas c4 = new TGCanvas(900,900);
        effp.getDataGroups().get(1).draw(c4.view(), true);*/
    }
}
