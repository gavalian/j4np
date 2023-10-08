/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.regression;

import j4ml.ejml.EJMLModel;
import j4np.utils.base.ArchiveProvider;
import j4np.utils.base.ArchiveUtils;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class NeuralVertexModel {
    private   EJMLModel models = null;
    private   String    modelName = "vertex.network";
    
    public NeuralVertexModel(){
        
    }
    
    public void load(String networkFile, int run){
        ArchiveProvider ap = new ArchiveProvider(networkFile);
        int runNumber = ap.findEntry(run);
        System.out.printf(":::: archive provider found run # %d for requested (run=%d)\n",runNumber, run);

        String archiveFile = String.format("network/%d/%s/%d/%s/vertex.network",
                        runNumber,"default",1,"neg"); 
        List<String> networkContent = ArchiveUtils.getFileAsList(networkFile,archiveFile);
        System.out.printf(":::: vertex init  [%3d] sector = %2d (%s) (lines = %4d) from file : %s\n", 
                1,1,"neg", networkContent.size(),archiveFile);
        models=  EJMLModel.create(networkContent);
    }
    
    public EJMLModel getModel(){ return models;}
    
}
