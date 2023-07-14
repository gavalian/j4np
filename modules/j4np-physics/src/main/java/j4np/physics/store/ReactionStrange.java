/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.store;

import j4np.hipo5.io.HipoReader;
import j4np.physics.Kinematics;
import j4np.physics.LorentzVector;
import j4np.physics.PhysicsEvent;
import j4np.physics.PhysicsReaction;
import static j4np.physics.PhysicsReaction.FORWARD_ONLY;
import twig.data.DataGroup;
import twig.data.H1F;
import twig.data.H2F;
import twig.data.TDirectory;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class ReactionStrange extends PhysicsReaction {
    
    public ReactionStrange(String file, String bank){
        super("11:X-:X+:Xn",10.5);
        this.setDataSource(new HipoReader(file), bank);
        //this.addModifier(FORWARD_ONLY);
    }
    
    
    @Override
    public DataGroup process(){
        DataGroup  group = createGroup();
        
        LorentzVector   beam = LorentzVector.from(this.beamVector);
        LorentzVector   elec = new LorentzVector();
        LorentzVector hadron = new LorentzVector();
        
         while(this.next()==true){
            
             PhysicsEvent event = this.getPhysicsEvent();
             if(this.eventFilter.isValid(event)==true){
                 event.vector(elec, 0.0005, 11, 0);
                 
                 if(event.countByPid(211)>0){
                     for(int k = 0; k < event.countByPid(211);k++){
                         event.vector(hadron, 0.139, 211, k);
                         double  z = this.getZ(beam, elec, hadron);
                         double xb = Kinematics.getXb(beam, elec);
                         //System.out.println(" z = " + z);
                         ((H2F) group.getData().get(0)).fill(z,xb);
                     }
                 }
                 
                 if(event.countByPid(-211)>0){
                     for(int k = 0; k < event.countByPid(211);k++){
                         event.vector(hadron, 0.139, -211, k);
                         double  z = this.getZ(beam, elec, hadron);
                         double xb = Kinematics.getXb(beam, elec);
                         //System.out.println(" z = " + z);
                         ((H2F) group.getData().get(2)).fill(z,xb);
                     }                                        
                 }
                 
                 if(event.countByPid(321)>0){
                     //System.out.println(event.toLundString());
                     for(int k = 0; k < event.countByPid(321);k++){
                         event.vector(hadron, 0.497, 321, k);
                         double z = this.getZ(beam, elec, hadron);
                         double xb = Kinematics.getXb(beam, elec);
                         //System.out.println(" z = " + z);
                         ((H2F) group.getData().get(1)).fill(z,xb);
                     }
                 }
                 
                 if(event.countByPid(-321)>0){
                     for(int k = 0; k < event.countByPid(321);k++){
                         event.vector(hadron, 0.497, -321, k);
                         double z = this.getZ(beam, elec, hadron);
                         double xb = Kinematics.getXb(beam, elec);
                         //System.out.println(" z = " + z);
                         ((H2F) group.getData().get(3)).fill(z,xb);
                     }
                     
                     //event.vector(hadron, 0.497, -321, 0);
                     //double z = this.getZ(beam, elec, hadron);
                     //System.out.println(" z = " + z);
                     //((H1F) group.getData().get(3)).fill(z);
                 }
             }
         }
         
         
         //H1F hRatioP = H1F.divide(((H1F) group.getData().get(1)),((H1F) group.getData().get(0)));
         //H1F hRatioN = H1F.divide(((H1F) group.getData().get(3)),((H1F) group.getData().get(2)));
         //group.add(hRatioP, 4, "EP");
         //group.add(hRatioN, 5, "EP");
        return group;
    }
    
    protected double getZ(LorentzVector vBeam, LorentzVector vE, LorentzVector vH){
        return vH.e()/(vBeam.e()-vE.e());
    }
    
    protected DataGroup createGroup(){
        
        DataGroup  group = new DataGroup(2,2);
        
        H2F hPionsZpXb = new H2F("hPionsZpXb","r;Z;x_B",35,0.1,0.8,20,0.0,1.0);
        H2F hKaonsZpXb = new H2F("hKaonsZpXb","r;Z;x_B",35,0.1,0.8,20,0.0,1.0);
        H2F hPionsZnXb = new H2F("hPionsZnXb","r;Z;x_B",35,0.1,0.8,20,0.0,1.0);
        H2F hKaonsZnXb = new H2F("hKaonsZnXb","r;Z;x_B",35,0.1,0.8,20,0.0,1.0);
        
        hPionsZpXb.attr().setLegend("#pi^+");
        hKaonsZpXb.attr().setLegend("#K^+"); 
        
        
        hPionsZnXb.attr().setLegend("#pi^+");
        hKaonsZnXb.attr().setLegend("#K^+");        
       
        
        group.add(hPionsZpXb, 0, "");
        group.add(hKaonsZpXb, 1, "");
        group.add(hPionsZnXb, 2, "");
        group.add(hKaonsZnXb, 3, "");
        
        return group;
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/Software/project-10.6/study/tmd/sidis_tmd.h5";
        ReactionStrange rs = new ReactionStrange(file,"MC::Particle");
        rs.addModifier(MC_FORWARD_ONLY);
        DataGroup group = rs.process();
        
        TGCanvas c = new TGCanvas();
        group.draw(c.view(), true);
        
        //String file = "/Users/gavalian/Work/Software/project-10.6/study/tmd/sidis_tmd.h5";
        ReactionStrange rs2 = new ReactionStrange(file,"REC::Particle");
        rs2.addModifier(FORWARD_ONLY);
        DataGroup group2 = rs2.process();
        
        TGCanvas c2 = new TGCanvas();
        group2.draw(c2.view(), true);
        
        TDirectory dir = new TDirectory();
        dir.add("/sim", group.getData());
        dir.add("/rec", group2.getData());
        
        dir.write("kaon_mult.twig");
    }
}
