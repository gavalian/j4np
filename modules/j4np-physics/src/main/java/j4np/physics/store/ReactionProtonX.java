/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.store;

import j4np.hipo5.io.HipoReader;
import j4np.physics.EventModifier;
import j4np.physics.PhysicsEvent;
import j4np.physics.PhysicsReaction;
import j4np.physics.VectorOperator;
import j4np.physics.VectorOperator.OperatorType;
import j4np.physics.data.PhysEventNode;

/**
 *
 * @author gavalian
 */
public class ReactionProtonX extends PhysicsReaction {
    
    
    public ReactionProtonX(String filter, double energy){
        super("11:211:11:X+:X-:Xn",10.6);
        this.initialize();
        System.out.println(" SIZE = " + this.modifiers.size());
    }
    
    public ReactionProtonX(String filter, double energy, String file){
        super("11:211:11:X+:X-:Xn",10.6);
        this.initialize();
        this.setFile(file);
    }
    
    
     private void initialize(){
        
        this.vecOprators.clear();
        this.operEntries.clear();
       
        this.addVector(this.getVector(), "-[11,0]-[211]-[11,1,0.13957]");
        this.addVector(this.getVector(), "-[11,1]-[211]-[11,0,0.13957]");        
        this.addVector( "[11,1,0.13957]+[211]");
        this.addVector( "[11,0,0.13957]+[211]");
        
        this.addVector("[11,0]");
        this.addVector("[11,1]");
        this.addVector("[211]");
        
        this.addEntry("mx"    ,  0, OperatorType.MASS);
        this.addEntry("mxr"   ,  1, OperatorType.MASS);        
        this.addEntry("rho"    , 2, OperatorType.MASS);
        this.addEntry("rhor"   , 3, OperatorType.MASS);
        
        this.addEntry("n1p"     , 4, OperatorType.P);
        this.addEntry("n1t"     , 4, OperatorType.THETA_DEG);
        this.addEntry("n1f"     , 4, OperatorType.PHI_DEG);
        
        this.addEntry("n2p"     , 5, OperatorType.P);
        this.addEntry("n2t"     , 5, OperatorType.THETA_DEG);
        this.addEntry("n2f"     , 5, OperatorType.PHI_DEG);
        
        this.addEntry("p1p"     , 6, OperatorType.P);
        this.addEntry("p1t"     , 6, OperatorType.THETA_DEG);
        this.addEntry("p1f"     , 6, OperatorType.PHI_DEG);
        
        this.showBranches();
        
        this.addModifier(PhysicsReaction.FORWARD_ONLY);
        this.setEventClass(new PhysEventNode());
    }
     
     public final void setFile(String file){
         HipoReader r = new HipoReader(file);
         this.setDataSource(r, "REC::Particle");
     }
}
