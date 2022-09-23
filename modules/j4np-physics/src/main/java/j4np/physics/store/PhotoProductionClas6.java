/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.store;

import j4np.hipo5.io.HipoReader;
import j4np.physics.PhysicsReaction;
import j4np.physics.VectorOperator;

/**
 *
 * @author gavalian
 */
public class PhotoProductionClas6 extends PhysicsReaction {
    
    public PhotoProductionClas6(String file){
        super("2212:211:-211:X+:X-:Xn",0.0);
        initialize();
        HipoReader r = new HipoReader(file);
        this.setDataSourcePhoto(r, "EVENT::particle", "TAGGER::tagr");
    }
    
    public PhotoProductionClas6(){
        super("2212:211:-211:X+:X-:Xn",0.0);
        initialize();
    }
    
    private void initialize(){
        
        this.addVector("[5001]"); // 0
        this.addVector("[211]+[-211]"); // 1
        this.addVector("[5000]+[5001]-[211]-[-211]"); // 2
        this.addVector("[5000]+[5001]-[2212]-[211]-[-211]"); // 3
        this.addVector("[5001]-[2212]"); // 4
        this.addVector("[2212]+[211]+[-211]"); // 5
        this.addVector("[5001]-[211]-[-211]"); // 6
        this.addVector("[2212]"); // 7
        this.addVector("[211]"); // 8
        this.addVector("[-211]"); // 9
        
        this.addEntry("gamma",   0, VectorOperator.OperatorType.P);
        
        this.addEntry("mk0",     1, VectorOperator.OperatorType.MASS);
        this.addEntry("k0p",     1, VectorOperator.OperatorType.P);
        this.addEntry("k0th",    1, VectorOperator.OperatorType.THETA);
        this.addEntry("k0phi",   1, VectorOperator.OperatorType.PHI);
        
        this.addEntry("mxk0",     2, VectorOperator.OperatorType.MASS);
        this.addEntry("mxk0p",    2, VectorOperator.OperatorType.P);
        this.addEntry("mxk0th",   2, VectorOperator.OperatorType.THETA);
        this.addEntry("mxk0phi",  2, VectorOperator.OperatorType.PHI);
        
        this.addEntry("mxpk0",    3, VectorOperator.OperatorType.MASS);
        this.addEntry("mxpk0p",   3, VectorOperator.OperatorType.P);
        this.addEntry("mxpk0th",  3, VectorOperator.OperatorType.THETA);
        this.addEntry("mxpk0phi", 3, VectorOperator.OperatorType.PHI);
        this.addEntry("mtp",      4, VectorOperator.OperatorType.MASS2);
        this.addEntry("mppipi",   5, VectorOperator.OperatorType.MASS);
        this.addEntry("mt",       6, VectorOperator.OperatorType.MASS2);
        
        this.addEntry("p_2212",   7, VectorOperator.OperatorType.P);
        this.addEntry("th_2212",  7, VectorOperator.OperatorType.THETA);
        this.addEntry("phi_2212", 7, VectorOperator.OperatorType.PHI);
        
        this.addEntry("p_211",    8, VectorOperator.OperatorType.P);
        this.addEntry("th_211",   8, VectorOperator.OperatorType.THETA);
        this.addEntry("phi_211",  8, VectorOperator.OperatorType.PHI);

        this.addEntry("p_m211",   9, VectorOperator.OperatorType.P);
        this.addEntry("th_m211",  9, VectorOperator.OperatorType.THETA);
        this.addEntry("phi_m211", 9, VectorOperator.OperatorType.PHI);
        
    }
    
    @Override
    public void configure() {
        
        ReactionConfiguration config = new ReactionConfiguration(null,this,true);
        config.show();
        
        this.setDataSourcePhoto(config.reader, config.getBankName(),config.getSecondBankName());
        this.modifiers.clear();
        this.addModifier(config.getEventModifier());
        double energy = config.getBeamEnergy();
        this.beamVector.setPxPyPzM(0.0, 0.0, 10.6, 0.0005);
    }
    
    public static void main(String[] args){
        String file = "";
        PhotoProductionClas6 p = new PhotoProductionClas6("sigma1660_d_pkl.h5");
    }
}
