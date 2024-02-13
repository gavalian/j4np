/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.decay;

import j4np.physics.LorentzVector;
import twig.math.F1D;
import twig.math.RandomFunc;

/**
 *
 * @author gavalian
 */
public class PhotonBeam {
    F1D b = null;
    RandomFunc rf = null;
    public PhotonBeam(double min, double max){
        b = new F1D("b","1/x",min,max);
        rf = new RandomFunc(b);
    }
    
    public void getBeam(LorentzVector v){
        double energy = rf.random();
        v.setPxPyPzM(0.0, 0.0, energy, 0.0);
    }
}
