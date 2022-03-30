/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.data;

import j4np.utils.io.TextFileReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class CSVReader {
    
    private int       nInputFeatures = 1;
    private int      nOutputFeatures = 1;
    private int  nInputFeaturesStart = 0;
    private int nOutputFeaturesStart = 1;
    private int[]       inputColumns = null;
    private int[]      outputColumns = null;
    
    private TextFileReader  reader = null;
    
    public CSVReader(){
        
    }
    
    public CSVReader(String filename){
        open(filename);
    }
    
    public CSVReader(String filename, int input, int output){
        setInputOutput(input,output);
        open(filename);
    }
    
    public CSVReader(String filename, int[] input, int[] output){
        setInputOutput(input,output);
        open(filename);
    }
    
    public final CSVReader setInputOutput(int input, int output){
        nInputFeatures = input;
        nOutputFeaturesStart = input;
        nOutputFeatures = output;
        
        return this;
    }
    
    
    public final CSVReader setInputOutput(int[] inputs, int[] outputs){
        inputColumns = inputs;
        outputColumns = outputs;
        return this;
    }
    
    public final void open(String filename){
        reader = new TextFileReader(filename,",");        
    }
    
    public DataList  getData(){
        DataList list = new DataList();
        boolean doRun = true;
        while(doRun==true){
            DataEntry pair = nextData();
            if(pair.getFirst()==null&&pair.getSecond()==null){
                doRun = false;
            } else {
                list.add(pair);
            }
        }
        return list;
    }
    
    public DataEntry nextData(){
        boolean status = reader.readNext();
        if(status==false) return new DataEntry(null,null);
        String line = reader.getString();
        String[] tokens = line.trim().split("\\s+");
        double[] input  = new double[inputColumns.length];
        
        for(int i = 0; i < inputColumns.length; i++){
           int index = inputColumns[i];
           input[i]  = Double.parseDouble(tokens[index]);
        }
        
        double[] output = new double[outputColumns.length];
        
        for(int i = 0; i < output.length; i++){
           int index = outputColumns[i];
           output[i]  = Double.parseDouble(tokens[index]);
        }
        
        return new DataEntry(input, output);
    }
    
    public static List<Double> redColumn(String file, int column, int max){
        
        List<Double> data = new ArrayList<>();
        TextFileReader reader = new TextFileReader();
        
        return data;
    }
    /*public <double[],double[]> nextDouble(){
        
    }*/
    
    public static void main(String[] args){
        CSVReader reader = new CSVReader("/Users/gavalian/Work/Software/project-10.4/j4np-1.0.4/regression_data_2.csv",6,3);                
        reader.setInputOutput(new int[]{0,1,2,3,4,5}, new int[]{6,7,8});
        DataList list = reader.getData();
        list.show();

        DataNormalizer in_norm = new DataNormalizer(new double[]{1,1,1,1,1,1},
                new double[]{112,112,112,112,112,112});
        
        DataNormalizer out_norm = new DataNormalizer(new double[]{0.5,5.0,40.0},
                new double[]{6.5,35.0,120.0});
        
        list.scan();
        
        DataList.normalizeInput(list, in_norm);
        DataList.normalizeOutput(list, out_norm);
        
        list.show();
        list.scan();
        
        
    }    
}
