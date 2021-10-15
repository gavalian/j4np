/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.io;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class CSVFileReader {
    
    private int       nInputFeatures = 1;
    private int      nOutputFeatures = 1;
    private int  nInputFeaturesStart = 0;
    private int nOutputFeaturesStart = 1;
    private int[]       inputColumns = null;
    private int[]      outputColumns = null;
    
    private TextFileReader  reader = null;
    
    public CSVFileReader(){
        
    }
    
    public CSVFileReader(String filename){
        open(filename);
    }
    
    public CSVFileReader(String filename, int input, int output){
        setInputOutput(input,output);
        open(filename);
    }
    
    public final CSVFileReader setInputOutput(int input, int output){
        nInputFeatures = input;
        nOutputFeaturesStart = input;
        nOutputFeatures = output;
        
        return this;
    }
    
    
    public final CSVFileReader setInputOutput(int[] inputs, int[] outputs){
        inputColumns = inputs;
        outputColumns = outputs;
        return this;
    }
    
    public final void open(String filename){
        reader = new TextFileReader(filename,",");        
    }
    
    public DataPairList  getData(){
        DataPairList list = new DataPairList();
        boolean doRun = true;
        while(doRun==true){
            DataPair pair = nextData();
            if(pair.getFirst()==null&&pair.getSecond()==null){
                doRun = false;
            } else {
                list.add(pair);
            }
        }
        return list;
    }
    
    public DataPair nextData(){
        boolean status = reader.readNext();
        if(status==false) return new DataPair(null,null);
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
        
        return new DataPair(input, output);
    }
    
    public static List<Double> redColumn(String file, int column, int max){
        
        List<Double> data = new ArrayList<>();
        TextFileReader reader = new TextFileReader();
        
        return data;
    }
    /*public <double[],double[]> nextDouble(){
        
    }*/
    
    public static void main(String[] args){
        CSVFileReader reader = new CSVFileReader("trees.csv",3,1);
        DataPairList list = new DataPairList();
        for(int i = 0; i < 30; i++){
            DataPair pair = reader.nextData();
            pair.show();
            list.add(pair);
        }        
        list.show();
        list.scan();
    }    
}
