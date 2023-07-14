/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.clas12.networks;

import deepnetts.net.layers.activation.ActivationType;
import j4ml.data.DataList;
import j4ml.data.DataNormalizer;
import j4ml.deepnetts.DeepNettsNetwork;
import j4ml.ejml.EJMLModel;
import j4np.physics.Vector3;
import j4np.utils.base.ArchiveUtils;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class RegressionNetwork {
    
    
    DataNormalizer dc6normalizer = new DataNormalizer(
            new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
            new double[]{112,112,112,112,112,112});    
    
    DataNormalizer regression3normalizer = new DataNormalizer(
            new double[]{ 0.5, 0.0, 40.0},
            new double[]{10.5, 0.2, 80.0}
    );
    
    
    EJMLModel regressionModelPos = null;
    EJMLModel regressionModelNeg = null;
    
    public  RegressionNetwork(){
        
    }
    
    public RegressionNetwork load(String archive, int run, String flavor){
        
         String archiveFilePos = String.format("network/%d/%s/regression_pos.network",run,flavor); 
         String archiveFileNeg = String.format("network/%d/%s/regression_neg.network",run,flavor); 
         //String     dataDir = String.format("network/%d/%s",run,flavor);
        
        List<String> networkContentPos = ArchiveUtils.getFileAsList(archive,archiveFilePos);
        List<String> networkContentNeg = ArchiveUtils.getFileAsList(archive,archiveFileNeg);
        
        regressionModelPos = EJMLModel.create(networkContentPos);
        regressionModelPos.setType(EJMLModel.ModelType.TANH_LINEAR);
        System.out.println(regressionModelPos.summary());
        
        regressionModelNeg = EJMLModel.create(networkContentNeg);
        regressionModelNeg.setType(EJMLModel.ModelType.TANH_LINEAR);
        System.out.println(regressionModelNeg.summary());
        return this;
    }
    
    public Vector3 getVector(float[] dcdata, int charge){
        float[] result = new float[3];
        if(charge<0)
            regressionModelNeg.feedForwardTanhLinear(dcdata, result);
        else regressionModelPos.feedForwardTanhLinear(dcdata, result);
        
        Vector3 vector = new Vector3();
        System.out.println(">>>> " + result[0] + "  denormalized = " + regression3normalizer.denormalize(result[0], 0));
        vector.setMagThetaPhi(
                regression3normalizer.denormalize(result[0], 0),
                DCUtils.consine2degree(regression3normalizer.denormalize(result[1], 1)),                
                regression3normalizer.denormalize(result[2], 2)
                );
        return vector;
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
