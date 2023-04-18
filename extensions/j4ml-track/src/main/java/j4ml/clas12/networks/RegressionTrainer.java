/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.clas12.networks;

import deepnetts.net.layers.activation.ActivationType;
import j4ml.data.DataList;
import j4ml.data.DataNormalizer;
import j4ml.deepnetts.DeepNettsNetwork;

/**
 *
 * @author gavalian
 */
public class RegressionTrainer {
    
    
    DataNormalizer dc6normalizer = new DataNormalizer(
            new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
            new double[]{112,112,112,112,112,112});    
    
    DataNormalizer regression3normalizer = new DataNormalizer(
            new double[]{ 0.5, 0.0, 40.0},
            new double[]{10.5, 0.2, 80.0}
    );
    
    public  RegressionTrainer(){
        
    }
    
    public void train(DataList list){
        
        DataList.normalizeInput( list, dc6normalizer);
        DataList.normalizeOutput(list, regression3normalizer);
        
        DeepNettsNetwork encoder = new DeepNettsNetwork();
        encoder.activation(ActivationType.TANH)
                .outputActivation(ActivationType.LINEAR);
        encoder.init(new int[]{6,12,12,12,3});
        
        
    }
}
