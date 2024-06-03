/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.ejml;

import j4ml.deepnetts.CsvDataProvider;
import j4ml.deepnetts.DataSetUtils;
import java.util.List;
import javax.visrec.ml.data.DataSet;
import twig.data.H1F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class EJMLModelRegression {
    
    EJMLModel    model = null;
    
    public EJMLModelRegression(String file){
        model = new EJMLModel(file,EJMLModel.ModelType.LINEAR);
        model.printSummary();
    }
    
    public EJMLModel getModel(){return model;}
    
    public static void main(String[] args){
    
        CsvDataProvider provider = new CsvDataProvider("mc_particle_train.csv",6,3);
        DataSet ds = provider.getData();
        
        
        List<H1F> features = DataSetUtils.featureHist(ds, false);
        TGCanvas c = new TGCanvas();        
        c.view().divide(2,3);
        for(int i = 0; i < features.size(); i++) c.view().region(i).draw(features.get(i));
        
        List<H1F> labels = DataSetUtils.labelHist(ds, false);
        TGCanvas c2 = new TGCanvas();        
        c2.view().divide(1,3);
        for(int i = 0; i < labels.size(); i++) c2.view().region(i).draw(labels.get(i));
    }
}
