/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics;

import j4np.hipo5.io.HipoReader;
import j4np.physics.PhysicsReaction.ReactionEntry;
import j4np.physics.VectorOperator.OperatorType;
import j4np.physics.data.PhysDataEvent;
import java.util.ArrayList;
import java.util.List;
import twig.data.H1F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class ReactionBuilder {
    private String filter = "";
    private double beamEnergy = 10.6;
    private String file   = "";
    private PhysDataEvent physDataEvent = null;
    private List<String>         vectors = new ArrayList<>();
    private List<ReactionEntry>  entries = new ArrayList<>();
    private boolean              rawBanks = false;
    
    private String     bank = "mc::event";
    
    public ReactionBuilder(){}
    public ReactionBuilder raw(boolean f){ rawBanks = f; return this;}
    public ReactionBuilder filter(String f){ filter = f; return this;}
    public ReactionBuilder beam(double energy){ beamEnergy = energy; return this;}
    public ReactionBuilder file(String f){ file = f; return this;}
    public ReactionBuilder vector(String desc){vectors.add(desc);return this;};
    public ReactionBuilder bank(String b){bank = b;return this;};
    public ReactionBuilder branch(String name, int order, String type){
        OperatorType oper = OperatorType.valueOf(type);
        entries.add(new ReactionEntry(name,order,oper));
        return this;
    }
    public Reaction build(){
        Reaction r = new Reaction();
        return r;
    }
    
    public PhysicsReaction buildPhysics(){
      PhysicsReaction react = new PhysicsReaction(filter,beamEnergy);
      for(int i = 0; i < vectors.size(); i++){
          String oper = vectors.get(i);
          if(oper.contains("[cm]")==true){
              String operString = oper.replace("[cm]", "");
              react.addVector(react.getVector(), operString);
          } else {
              react.addVector(oper);
          }
      }
      
      for(ReactionEntry entry : this.entries) react.addEntry(entry);
      HipoReader r = new HipoReader(file);
      
      react.setDataSource(r, bank);
      return react;
    }
    
    public static void main(String[] args){
        
        PhysicsReaction react = new ReactionBuilder()
                .filter("11:211:-211:Xn:X+:X-")
                //.bank("rec::event")
                .bank("REC::Particle")
                .beam(10.6)
                .vector("[cm]-[11]-[211]-[-211]")
                .vector("[cm]-[11]-[211]")
                .branch("mxepipi", 0, "MASS")
                .branch(  "mxepi", 1, "MASS")
                .file("/Users/gavalian/Work/temp/rec_output_filtered.hipo").buildPhysics();
        //.file("/Users/gavalian/Work/temp/dis_fmc_n_1.hipo").build();
        
        
        EventModifier frwd = new EventModifier(){
            @Override
            public void modify(PhysicsEvent event) {
                int count = event.count();
                for(int i = 0; i < count; i++)
                    if(Math.abs(event.status(i))>2000&&Math.abs(event.status(i))<3000){
                        event.status(i, 1);
                    } else {
                        event.status(i, -1);
                    }
            }
        };
        
        react.addModifier(frwd);
        
        /*while(react.next()==true){
            System.out.println(react.getPhysicsEvent().toLundString());
        }*/
        
        H1F h = react.geth("mxepipi", "", 120, 0.6, 1.5);
        H1F hn = react.geth("mxepi", "", 120, 0.6, 1.5);
        
        TGCanvas c = new TGCanvas(900,500);
        c.view().divide(2,1);
        c.view().region(0).draw(h);
        c.view().region(1).draw(hn);
        
    }
}
