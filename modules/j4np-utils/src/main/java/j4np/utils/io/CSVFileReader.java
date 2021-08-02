/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.io;

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
    
    
    public final void open(String filename){
        reader = new TextFileReader(filename,",");        
    }
    
    public DataPair nextData(){
        boolean status = reader.readNext();
        if(status==false) return new DataPair(null,null);
        String line = reader.getString();
        String[] tokens = line.trim().split("\\s+");
        double[] input  = new double[nInputFeatures];
        
        for(int i = 0; i < nInputFeatures; i++){
           int index = i + nInputFeaturesStart;
           input[i]  = Double.parseDouble(tokens[index]);
        }
        
        double[] output = new double[nOutputFeatures];
        for(int i = 0; i < nOutputFeatures; i++){
           int index = i + nOutputFeaturesStart;
           output[i]  = Double.parseDouble(tokens[index]);
        }
        return new DataPair(input, output);
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
