/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.classifier;

import j4ml.ejml.EJMLModel;
import j4np.utils.base.ArchiveProvider;
import j4np.utils.base.ArchiveUtils;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class ClassifierCLI {
    
     EJMLModel model = null;//new EJMLModel();
     
    public ClassifierCLI(String network, int run){
     this.init(network, run);
    }
    
    public ClassifierCLI(String network){
     this.init(network, 10);
    }
    
    private void init(String network, int run){
        ArchiveProvider ap = new ArchiveProvider(network);
        int runNumber = ap.findEntry(run);
        String archiveFile = String.format("network/%d/%s/trackClassifier.network",runNumber,"default");
        List<String> networkContent = ArchiveUtils.getFileAsList(network,archiveFile);
        model = EJMLModel.create(networkContent);
    }
    public void evaluate(String line, boolean normalize){
        this.evaluate(line, "\\s+", normalize);
    }
    
    public void evaluate(String line){
        this.evaluate(line, "\\s+", false);
    }
    public void evaluate(String line, String delim, boolean normalize){
        String[] tokens = line.split(delim);
        float factor = 112.0f;
        if(normalize==false) factor = 1.0f;
        if(tokens.length>=6){
            System.out.println("---");
            float[] input = new float[6];
            for(int i = 0; i < 6; i++) input[i] = Float.parseFloat(tokens[i])/factor;
            float[] output = new float[3];
            model.feedForwardSoftmax(input, output);
            for(int i = 0; i < 6; i++) System.out.printf("%9.6f  ", input[i]);
            System.out.printf("  :  ");
            for(int i = 0; i < 3; i++) System.out.printf("%9.6f  ", output[i]);
            System.out.println("\n");
            return;
        } 
        System.out.println("error: the size of the array is " + tokens.length);
    }
}
