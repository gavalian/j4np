 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.store;

import j4np.physics.LorentzVector;
import j4np.physics.PhysicsEvent;
import j4np.physics.PhysicsReaction;
import j4np.physics.Vector3;
import j4np.physics.VectorOperator;
import j4np.physics.VectorOperator.OperatorType;

/**
 *
 * @author gavalian
 */
public class ReactionKStar extends PhysicsReaction {
    
    public ReactionKStar(){
        super("11:-211:321:X+:X-:Xn",10.6);
        this.initialize();
    }
    
    public ReactionKStar(double energy){
        super("11:-211:321:X+:X-:Xn",energy);
        this.initialize();
    }
    
    public final void initialize(){
        this.vecOprators.clear();
        this.operEntries.clear();
        this.addVector(this.getVector(), "-[11]-[-211]-[321]");
        this.addVector(this.getVector(), "-[11]");
        this.addVector("[321]+[-211]");
        this.addVector("[11]");
        this.addVector("[321]");
        this.addVector("[-211]");
        
        VectorOperatorPhi phi = new VectorOperatorPhi(this.getBeamVector());
        
        this.addVector(phi);
        this.addVector(this.getVector(), "-[11]-[321]");
        
        this.addEntry("mm2"   , 0, OperatorType.MASS);
        this.addEntry("w2"    , 1, OperatorType.MASS);
        this.addEntry("imkp"  , 2, OperatorType.MASS);
        this.addEntry("ep"    , 3, OperatorType.P);
        this.addEntry("ef"    , 3, OperatorType.PHI);
        this.addEntry("kp"    , 4, OperatorType.P);
        this.addEntry("kf"    , 4, OperatorType.PHI);
        this.addEntry("pip"   , 5, OperatorType.P);
        this.addEntry("pif"   , 5, OperatorType.PHI);

        this.addEntry("ksp"    , 2, OperatorType.P);
        this.addEntry("ksf"    , 2, OperatorType.PHI);

        this.addEntry("ksphi" , 6, OperatorType.PHI);
        this.addEntry("mmkp" ,  7, OperatorType.MASS);
    }
    
    public static class VectorOperatorPhi extends VectorOperator {
         LorentzVector  ve = new LorentzVector();
         LorentzVector vq2 = new LorentzVector();
         LorentzVector vkp = new LorentzVector();
         LorentzVector vpp = new LorentzVector();
         LorentzVector vks = new LorentzVector();
         
         public VectorOperatorPhi(LorentzVector lv){
             super(lv);
         }
         
         @Override
         public void apply(PhysicsEvent event){
             
             event.vector(ve,  0.0005,     11, 0);
             event.vector(vpp, 0.139570, -211, 0);
             event.vector(vkp, 0.493677,  321, 0);
             
             this.opVector.copy(vkp);
             this.opVector.add(vpp);
             
             vq2.copy(vec);
             vq2.sub(this.ve);
             
             //vks.copy(vkp);
             //vks.add(vpp);             
             
             Vector3 ex = this.vec.vect().cross(ve.vect());
             Vector3 ez = vq2.vect();
             
             ex.unit();
             ez.unit();
             //Vector3 ey = ex.cross(ez);// original implementation
             Vector3 ey = ez.cross(ex);
             
             double xc = this.opVector.vect().dot(ex);
             double yc = this.opVector.vect().dot(ey);
             double zc = this.opVector.vect().dot(ez);
             
             /*System.out.println("******");
             System.out.println(ex);
             System.out.println(ey);
             System.out.println(ez);
             System.out.printf("COORD = %9.5f %9.5f %9.5f \n",
                    xc,yc,zc);*/
             //double   phinr = norm.phi();
             //double   phiks = this.opVector.phi();
             double frame_phi = Math.atan2(yc, xc);
             double frame_th  = Math.acos(zc/this.opVector.vect().mag());
             
             
             this.opVector.vect().setMagThetaPhi(opVector.vect().mag(), 
                     frame_th, frame_phi);
             
             //System.out.printf("KS = %9.5f %9.5f ( %9.5f )\n",
             //        phinr, phiks,this.opVector.vect().phi());
             //opVector.copy(vec);
             //opVector.sub(ve).sub(vpm).sub(vpp);             
         }
     }
}
