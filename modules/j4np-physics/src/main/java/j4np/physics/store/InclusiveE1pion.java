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

/**
 *
 * @author gavalian
 */
public class InclusiveE1pion extends PhysicsReaction {
    
    public InclusiveE1pion(){
        super("11:211:X+:X-:Xn",10.6);
        initialize();
    }
    
    public InclusiveE1pion(double energy){
        super("11:211:X+:X-:Xn",energy);
        initialize();
    }
    
    public InclusiveE1pion(double energy, String file, String bank){
        super("11:211:X+:X-:Xn",energy);
        initialize();
        HipoReader r = new HipoReader(file);
        this.setDataSource(r,bank);
    }

    private void initialize(){
        this.vecOprators.clear();
        this.operEntries.clear();
        this.addVector(this.getVector(), "-[11]-[211]");
        this.addVector( "[11]");
        this.addVector( "[211]");
        
        this.addEntry("mx",      0, VectorOperator.OperatorType.MASS);        
        this.addEntry("e_p",     1, VectorOperator.OperatorType.P);
        this.addEntry("e_th",    1, VectorOperator.OperatorType.THETA);
        this.addEntry("e_phi",   1, VectorOperator.OperatorType.PHI);        
        this.addEntry("pip_p",   2, VectorOperator.OperatorType.P);
        this.addEntry("pip_th",  2, VectorOperator.OperatorType.THETA);
        this.addEntry("pip_phi", 2, VectorOperator.OperatorType.PHI);
                
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
        
        
        this.showBranches();
    }
    
    @Override
    public void configChange(){
        System.out.println("reconfiguring");
        this.initialize();
    }
    
    private void addModifierData(){
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
    }
    public static InclusiveE1pion createData(double energy){        
        InclusiveE1pion p = new InclusiveE1pion(energy);
        p.addModifierData(); return p;
    }
    
    public static InclusiveE1pion createMC(double energy){
        InclusiveE1pion p = new InclusiveE1pion(energy);
        return p;
    }
}
