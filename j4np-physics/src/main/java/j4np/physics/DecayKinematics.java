/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics;

import java.util.List;

/**
 *
 * @author gavalian
 */
public class DecayKinematics {
    
    public enum Frame {LOCAL, REST, LAB};
    
    public static double twoBodyDecayMomentum(double M, double m1, double m2){
        double mult1 = M*M - (m1+m2)*(m1+m2);
        double mult2 = M*M - (m1-m2)*(m1-m2);
        double mult  = mult1*mult2;
        if(mult<0){ /*Throw an exception here coming soon*/ return -1.0; }
        return Math.sqrt(mult)/(2.0*M);
    }
    
    public static Vector3 vectorToFrame(Vector3 frame, Vector3 vec)
    {
        TransMatrix matrix = new TransMatrix();
        matrix.compose(frame);
        return matrix.mult(vec);
    }
    
    public static LorentzVector[] decay(LorentzVector parent, double m1, double m2, 
            double theta_restframe, double phi_restframe){
        
        LorentzVector[] results = new LorentzVector[]{ new LorentzVector(), new LorentzVector()};
        double momentum = DecayKinematics.twoBodyDecayMomentum(parent.mass(),m1,m2);
        Vector3 vz = new Vector3(0.,0.,0.);
        Vector3 v1 = new Vector3();
        Vector3 v2 = new Vector3();

        v1.setMagThetaPhi(momentum, theta_restframe, phi_restframe);
        vz.sub(v1);
        v2.setMagThetaPhi(momentum, Math.PI-theta_restframe, 2.0*Math.PI-phi_restframe);
        results[0].setVectM(v1, m1);
        results[1].setVectM(vz, m2);
        return results;
    }
    
    private static  List<LorentzVector> decay(LorentzVector parent, double m1, double m2, 
           double m3, double theta_restframe, double phi_restframe){
        LorentzVector[] results = new LorentzVector[]{ new LorentzVector(), new LorentzVector(), new LorentzVector()};
        
        return List.of(results[0],results[1]);
    }
    

    public static List<LorentzVector> decay(LorentzVector parent, double[] childrenMass, double theata, double phi, Frame frame){
        LorentzVector[] results = DecayKinematics.decay(parent, 
                childrenMass[0], childrenMass[1], theata, phi);
        if(frame == Frame.LAB){
                    Vector3 vboost = parent.boostVector();                    
                    results[0].boost(vboost);
                    results[1].boost(vboost);
        }
        return List.of(results[0],results[1]);
    }
    
}
