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
public class InclusiveE2pions extends PhysicsReaction {
    
    public InclusiveE2pions(){
        super("11:211:-211:X+:X-:Xn",10.6);
        this.initialize();
    }
    
    public InclusiveE2pions(double energy){
        super("11:211:-211:X+:X-:Xn",energy);
        this.initialize();
    }
    
    
     public InclusiveE2pions(double energy, String file, String bank){
        super("11:211:-211:X+:X-:Xn",energy);
        this.initialize();
        HipoReader r = new HipoReader(file);
        this.setDataSource(r,bank);
        this.addModifier(new EventModifierStore.EventModifierForward());
    }
     
     public InclusiveE2pions(double energy, String file, String bank, String filter){
        super(filter,energy);
        this.initialize();
        HipoReader r = new HipoReader(file);
        this.setDataSource(r,bank);
        this.addModifier(new EventModifierStore.EventModifierForward());
    }
     
     public InclusiveE2pions(double energy, String file, String bank, String filter, EventModifier modifier){
        super(filter,energy);
        this.initialize();
        HipoReader r = new HipoReader(file);
        this.setDataSource(r,bank);
        this.addModifier(modifier);
    }
     
    @Override
    public void configChange(){
        System.out.println("reconfiguring");
        this.initialize();
    }
    
    private void initialize(){
        
        this.vecOprators.clear();
        this.operEntries.clear();
        this.addVector(this.getVector(), "-[11]-[211]-[-211]");
        
        this.addVector("[211]+[-211]");
        this.addVector( "[11]");
        this.addVector( "[211]");
        this.addVector( "[-211]");
        
        this.addVector(this.getVector(), "-[11]");
        this.addVector(this.getBeamVector(), "-[11]");
        
        this.addEntry("mx",     0, VectorOperator.OperatorType.MASS);

        
        this.addEntry("mpipi",  1, VectorOperator.OperatorType.MASS);
        this.addEntry("e_p",    2, VectorOperator.OperatorType.P);
        this.addEntry("e_th",   2, VectorOperator.OperatorType.THETA);
        this.addEntry("e_phi",  2, VectorOperator.OperatorType.PHI);
        
        this.addEntry("pip_p",   3, VectorOperator.OperatorType.P);
        this.addEntry("pip_th",  3, VectorOperator.OperatorType.THETA);
        this.addEntry("pip_phi", 3, VectorOperator.OperatorType.PHI);
        
        this.addEntry("pim_p",   4, VectorOperator.OperatorType.P);
        this.addEntry("pim_th",  4, VectorOperator.OperatorType.THETA);
        this.addEntry("pim_phi", 4, VectorOperator.OperatorType.PHI);
        this.addEntry("w2",      5, VectorOperator.OperatorType.MASS2);
        this.addEntry("q2",      6, VectorOperator.OperatorType.MASS2);
        
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
    
    public static InclusiveE2pions createData(double energy){        
        InclusiveE2pions p = new InclusiveE2pions(energy);
        p.addModifierData(); return p;
    }
    
    public static InclusiveE2pions createMC(double energy){
        InclusiveE2pions p = new InclusiveE2pions(energy);
        return p;
    }
}
