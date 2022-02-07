/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.regression;

import j4np.physics.LorentzVector;
import j4np.utils.io.TextFileReader;

/**
 *
 * @author gavalian
 */
public class PhysicsAnalysis {
    
    public static LorentzVector getVector(double mass,String[] data){
        double px = Double.parseDouble(data[1]);
        double py = Double.parseDouble(data[2]);
        double pz = Double.parseDouble(data[3]);
        return LorentzVector.withPxPyPzM(px, py, pz, mass);
    }
    
    public static LorentzVector getVectorInf(double mass,String[] data){
        int sector = (int) Double.parseDouble(data[4]);
        double p   = Double.parseDouble(data[11]);
        double th  = Double.parseDouble(data[12]);
        double fi  = Double.parseDouble(data[13]);
        System.out.printf("(%9.5f %9.5f %9.5f)\n",p,th,fi);
        return null;//LorentzVector.withPxPyPzM(px, py, pz, mass);
    }
    
    public static void main(String[] args){
        TextFileReader r = new TextFileReader();
        r.open("/Users/gavalian/Downloads/ert_predictions_norm.csv");
        while(r.readNext()==true){
            String      line = r.getString();
            String[]  tokens = line.split("\\s+");
            PhysicsAnalysis.getVectorInf(0.0005, tokens);
        }
    }
}
