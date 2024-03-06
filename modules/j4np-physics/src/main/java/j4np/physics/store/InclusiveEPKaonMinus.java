/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.store;

import j4np.physics.EventModifier;
import j4np.physics.PhysicsEvent;
import j4np.physics.PhysicsReaction;
import j4np.physics.VectorOperator;

/**
 *
 * @author gavalian
 */
public class InclusiveEPKaonMinus extends PhysicsReaction {
    public InclusiveEPKaonMinus(double energy){
        super("11:2212:-321:X+:X-:Xn",energy);
        this.initialize();
    }
    
    private void initialize(){
        
        this.vecOprators.clear();
        this.operEntries.clear();
        this.addVector(this.getVector(), "-[11]-[2212]-[-321]");
        this.addVector("[2212]+[-321]");
        this.addVector( "[11]");
        this.addVector( "[2212]");
        this.addVector( "[-321]");
        
        this.addEntry("mxepk",     0, VectorOperator.OperatorType.MASS);
        this.addEntry("mpk",    1, VectorOperator.OperatorType.MASS);
        
        this.addEntry("e_p",    2, VectorOperator.OperatorType.P);
        this.addEntry("e_th",   2, VectorOperator.OperatorType.THETA);
        this.addEntry("e_phi",  2, VectorOperator.OperatorType.PHI);
        
        this.addEntry("p_p",   3, VectorOperator.OperatorType.P);
        this.addEntry("p_th",  3, VectorOperator.OperatorType.THETA);
        this.addEntry("p_phi", 3, VectorOperator.OperatorType.PHI);
        
        this.addEntry("k_p",   4, VectorOperator.OperatorType.P);
        this.addEntry("k_th",  4, VectorOperator.OperatorType.THETA);
        this.addEntry("k_phi", 4, VectorOperator.OperatorType.PHI);
        
        EventModifier modifier = new EventModifier(){
            @Override
            public void modify(PhysicsEvent event) {
                int counter = event.count();
                for(int i = 0; i < counter ; i++){
                    int status = event.status(i);
                    if(Math.abs(status)>=2000&&Math.abs(status)<3000){
                        event.status(i, 1);
                    } else { event.status(i, -1);}
                }
            }
        };
        this.addModifier(modifier);
        this.showBranches();
    }
}
