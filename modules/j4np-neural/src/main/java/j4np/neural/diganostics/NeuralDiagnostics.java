/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.diganostics;

import j4np.hipo5.data.Bank;
import j4np.neural.data.TrackReader;
import j4np.neural.networks.NeuralTrackFinder;

/**
 *
 * @author gavalian
 */
public class NeuralDiagnostics {
    
    public static void runValidation(String file, String network, int run){
        NeuralTrackFinder finder = new NeuralTrackFinder(network,run);
        finder.initializeNetworks();        
        TrackReader dr = new TrackReader();
        for(int i = 0; i < 1500; i++){
            Bank b = dr.readFromFile(file,3,27);
            //b.show();
            finder.processBank(b);
        }
        
        finder.showStats();
    }
    
    public static void main(String[] arg){
        String file = "run_005442_for_ai_va.h5";
        String network = "clas12rga-nuevo.network";
        int      run = 5442;
        NeuralDiagnostics.runValidation(file, network, run);
        
        file = "run_005442_for_ai_va.h5";
        network = "clas12rga-nuevo.network";
        run = 5443;
        NeuralDiagnostics.runValidation(file, network, run);
        
    }
}
