/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.store;

import j4np.hipo5.io.HipoReader;
import j4np.physics.PhysicsReaction;
import j4np.physics.VectorOperator;
import j4np.physics.data.PhysEventNode;

/**
 *
 * @author gavalian
 */
public class ReactionLambdaX extends PhysicsReaction {
    
    public ReactionLambdaX(String filter, double energy){
        super("11:211:11:Xn:X+:X-",10.6);
        this.initialize();
    }
    
    public ReactionLambdaX(String filter, double energy, String file){
        super("11:211:11:Xn:X+:X-",10.6);
        this.initialize();
        this.setFile(file);
    }
    
    
     private void initialize(){
        
        this.vecOprators.clear();
        this.operEntries.clear();
                             
        this.addVector(this.getVector(), "-[11,0]-[211,0,0.938]-[11,1,0.497]");
        this.addVector(this.getVector(), "-[11,1]-[211,0,0.938]-[11,0,0.497]");

        this.addVector( "[11,1,0.497]+[211,0,0.938]");
        this.addVector( "[11,0,0.497]+[211,0,0.938]");

        this.addVector("[11,0]");
        this.addVector("[11,1]");
        this.addVector("[211]");
        this.addVector(this.getVector(), "-[11]");
        this.addVector(this.getVector(), "-[11,1]");
        
        this.addVector(this.getBeamVector(), "-[11,0]");
        this.addVector(this.getBeamVector(), "-[11,1]");
        
        this.addVector( this.getBeamVector(), "-[11,0]-[11,1,0.497]-[211,0,0.938]");
        this.addVector( this.getBeamVector(), "-[11,1]-[11,0,0.497]-[211,0,0.938]");

        this.addEntry("mx2"    ,  0, VectorOperator.OperatorType.MASS2);
        this.addEntry("mx2r"   ,  1, VectorOperator.OperatorType.MASS2);        
        this.addEntry("lam"    ,  2, VectorOperator.OperatorType.MASS);
        this.addEntry("lamr"   ,  3, VectorOperator.OperatorType.MASS);
        
        this.addEntry("n1p"     , 4, VectorOperator.OperatorType.P);
        this.addEntry("n1t"     , 4, VectorOperator.OperatorType.THETA_DEG);
        this.addEntry("n1f"     , 4, VectorOperator.OperatorType.PHI_DEG);
        
        this.addEntry("n2p"     , 5, VectorOperator.OperatorType.P);
        this.addEntry("n2t"     , 5, VectorOperator.OperatorType.THETA_DEG);
        this.addEntry("n2f"     , 5, VectorOperator.OperatorType.PHI_DEG);
        
        this.addEntry("p1p"     , 6, VectorOperator.OperatorType.P);
        this.addEntry("p1t"     , 6, VectorOperator.OperatorType.THETA_DEG);
        this.addEntry("p1f"     , 6, VectorOperator.OperatorType.PHI_DEG);
        this.addEntry("w2"      , 7, VectorOperator.OperatorType.MASS2);
        this.addEntry("w2r"     , 8, VectorOperator.OperatorType.MASS2);
        
        this.addEntry("q2"      ,  9, VectorOperator.OperatorType.MASS2);
        this.addEntry("q2r"     , 10, VectorOperator.OperatorType.MASS2);
        this.addEntry("mt"     , 11, VectorOperator.OperatorType.MASS2);
        this.addEntry("mtr"     , 12, VectorOperator.OperatorType.MASS2);
        this.showBranches();
        
        this.addModifier(PhysicsReaction.FORWARD_ONLY);
        this.setEventClass(new PhysEventNode());
    }
     
     public final void setFile(String file){
         HipoReader r = new HipoReader(file);         
         this.setDataSource(r, "REC::Particle");
         this.setEventClass(new PhysEventNode());
     }
}
