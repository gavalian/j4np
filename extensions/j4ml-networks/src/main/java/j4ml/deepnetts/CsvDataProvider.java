/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.deepnetts;

import deepnetts.data.TabularDataSet;
import j4np.utils.io.TextFileReader;
import java.util.Arrays;
import javax.visrec.ml.data.DataSet;

/**
 *
 * @author gavalian
 */
public class CsvDataProvider implements DataProvider {
    
    public  boolean       isReversed = false;
    private TextFileReader         r = new TextFileReader();
    private int              nInputs = 0;
    private int             nOutputs = 0;
    private boolean     isClassifier = false;
    
    
    public CsvDataProvider(String file, int inputs, int outputs){
        r.setSeparator(",");
        r.open(file);
        nInputs = inputs; nOutputs = outputs;
    }

    @Override
    public DataSet getData() {
        
        TabularDataSet  dataset = new TabularDataSet(nInputs,nOutputs);
        
        while(r.readNext()==true){
            if(this.isReversed==false){
                float[]  inBuffer = r.getAsFloatArray(0, nInputs);
                float[] outBuffer = r.getAsFloatArray(nInputs, nOutputs);
                dataset.add(new TabularDataSet.Item(inBuffer, outBuffer) );
                //System.out.printf("%s ==> %s\n",Arrays.toString(inBuffer),Arrays.toString(outBuffer));
            } else {
                float[]  inBuffer = r.getAsFloatArray(nOutputs, nInputs);
                float[] outBuffer = r.getAsFloatArray(0, nOutputs);
                dataset.add(new TabularDataSet.Item(inBuffer, outBuffer) );
            }

        }
        
        String[] names = DataSetUtils.generateNames(nInputs, nOutputs);
        dataset.setColumnNames(names);;
        return dataset;
    }
    
    
}
