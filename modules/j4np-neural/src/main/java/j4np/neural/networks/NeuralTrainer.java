/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.networks;

import j4np.neural.classifier.NeuralClassifier;
import j4np.neural.data.Tracks;
import j4np.neural.data.TrackReader;

/**
 *
 * @author gavalian
 */
public class NeuralTrainer {
    
    
    public static void main(String[] args){
        Tracks list = TrackReader.read("run_5442_out_tr.h5", 5, 250);
        list.show();
        
        NeuralClassifier nc = new NeuralClassifier();
        nc.loadFromFile("clas12rga.network", 5442);
        
        nc.evaluate(list);
        
        list.show();
    }
}
