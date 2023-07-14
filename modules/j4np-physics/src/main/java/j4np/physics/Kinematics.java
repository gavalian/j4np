/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics;

/**
 *
 * @author gavalian
 */
public class Kinematics {
    
    public static final double Mp = 0.93827208816;
    
    public static double getNu(LorentzVector beam, LorentzVector elec){
        return beam.e()-elec.e();
    }
    
    public static LorentzVector getQ(LorentzVector beam, LorentzVector elec){
        LorentzVector qvec = LorentzVector.from(beam);
        qvec.sub(elec); return qvec;
    }
    
    public static double getQ2(LorentzVector beam, LorentzVector elec){
        LorentzVector qvec = Kinematics.getQ(beam, elec);
        return -qvec.mass2();
    }
    
    public static double getXb(LorentzVector beam, LorentzVector elec){
        double Q2 = Kinematics.getQ2(beam, elec);
        double nu = Kinematics.getNu(beam, elec);
        return Q2/(2*Kinematics.Mp*nu);
    }
    
    public static double getZ(LorentzVector beam, LorentzVector elec, LorentzVector hadron){
         return hadron.e()/(beam.e()-elec.e());
    }
    
    public static double getY(LorentzVector beam, LorentzVector elec){
        double nu = Kinematics.getNu(beam, elec);
        return nu/beam.e();
    }
     
    public static double getPT(LorentzVector beam, LorentzVector elec, LorentzVector hadron){
        LorentzVector q = Kinematics.getQ(beam, elec);
        return q.vect().dot(hadron.vect())/q.vect().mag();
//        return Q2/(2*Kinematics.Mp);
    }
    
    
    
}

