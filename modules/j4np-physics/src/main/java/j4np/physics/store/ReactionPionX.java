/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.store;

import j4np.hipo5.io.HipoReader;
import j4np.physics.LorentzVector;
import j4np.physics.PhysicsEvent;
import j4np.physics.PhysicsReaction;
import j4np.physics.VectorOperator;
import j4np.physics.data.PhysEventNode;

/**
 *
 * @author gavalian
 */
public class ReactionPionX extends PhysicsReaction {
     public ReactionPionX(String filter, double energy){
        super("11:211:11:X+:X-:Xn",10.6);
        this.initialize();
    }
    
    public ReactionPionX(String filter, double energy, String file){
        super("11:211:11:X+:X-:Xn",10.6);
        this.initialize();
        this.setFile(file);
    }
    
    
     private void initialize(){
        
        this.vecOprators.clear();
        this.operEntries.clear();
       
        this.addVector(this.getVector(), "-[11,0]-[211,0,0.938]-[11,1,0.13957]");
        this.addVector(this.getVector(), "-[11,1]-[211,0,0.938]-[11,0,0.13957]");        
        this.addVector(this.getVector(), "-[11,0]-[211,0,0.938]");
        this.addVector(this.getVector(), "-[11,1]-[211,0,0.938]");

        
        this.addVector("[11,0]");
        this.addVector("[11,1]");
        this.addVector("[211]");
        
        VectorOperatorCustom vop = new VectorOperatorCustom(this.getBeamVector());
        
        this.addVector(vop);
        
        this.addEntry("mx2"    ,  0, VectorOperator.OperatorType.MASS2);
        this.addEntry("mx2r"   ,  1, VectorOperator.OperatorType.MASS2);        
        this.addEntry("rho"    , 2, VectorOperator.OperatorType.MASS);
        this.addEntry("rhor"   , 3, VectorOperator.OperatorType.MASS);
        
        this.addEntry("n1p"     , 4, VectorOperator.OperatorType.P);
        this.addEntry("n1t"     , 4, VectorOperator.OperatorType.THETA_DEG);
        this.addEntry("n1f"     , 4, VectorOperator.OperatorType.PHI_DEG);
        
        this.addEntry("n2p"     , 5, VectorOperator.OperatorType.P);
        this.addEntry("n2t"     , 5, VectorOperator.OperatorType.THETA_DEG);
        this.addEntry("n2f"     , 5, VectorOperator.OperatorType.PHI_DEG);
        
        this.addEntry("p1p"     , 6, VectorOperator.OperatorType.P);
        this.addEntry("p1t"     , 6, VectorOperator.OperatorType.THETA_DEG);
        this.addEntry("p1f"     , 6, VectorOperator.OperatorType.PHI_DEG);
        this.addEntry("mtt", 7, VectorOperator.OperatorType.MASS2);
        
        this.showBranches();
        
        this.addModifier(PhysicsReaction.FORWARD_ONLY);
        this.setEventClass(new PhysEventNode());
    }
     
     public final void setFile(String file){
         HipoReader r = new HipoReader(file);         
         this.setDataSource(r, "REC::Particle");
         this.setEventClass(new PhysEventNode());
     }
     
     public static class VectorOperatorCustom extends VectorOperator {
         LorentzVector  ve = new LorentzVector();
         LorentzVector vpp = new LorentzVector();
         LorentzVector vpm = new LorentzVector();
         LorentzVector vq2 = new LorentzVector();
         public VectorOperatorCustom(LorentzVector lv){
             super(lv);
         }
         
         @Override
         public void apply(PhysicsEvent event){
             
             event.vector(ve,  0.0005, 11, 0);
             event.vector(vpm, 0.139,  11, 1);
             event.vector(vpp, 0.139, 211, 0);
             
             opVector.copy(vec);
             opVector.sub(ve).sub(vpm).sub(vpp);             
         }
     }
}
