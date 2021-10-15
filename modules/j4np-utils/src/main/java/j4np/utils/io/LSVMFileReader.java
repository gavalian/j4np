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
public class LSVMFileReader {

    private TextFileReader        reader = null;    
    private int             nArrayLength = 1;
    private String          dataFileName = "input.lsvm";
    private String             outFormat = "%.5f";
    private double             threshold = 0.0000000001;
    private int                  classes = 2;
    
    public LSVMFileReader(){ }
    public LSVMFileReader(String filename){
        dataFileName = filename;
        open(dataFileName);
    }
    
    public LSVMFileReader(String filename, int arrayLength){
        dataFileName = filename;
        setArrayLength(arrayLength);
        open(dataFileName);
    }
    
    public final LSVMFileReader open(String filename){
        dataFileName = filename;
        reader = new TextFileReader(filename);
        return this;
    }
    
    public final LSVMFileReader setArrayLength(int len){ 
        nArrayLength = len; return this;
    }
    
    public final LSVMFileReader setClasses(int nc){
        this.classes = nc; return this;
    }
    
    public double[] toDouble(String line, int arrayLength){
        String[] tokens = line.trim().split("\\s+");
        double[]  array = new double[arrayLength];
        for(int i = 0; i < tokens.length; i++){
            if(tokens[i].contains(":")==true){
                String[] pair = tokens[i].split(":");
                int     index = Integer.parseInt(pair[0]);
                double  value = Double.parseDouble(pair[1]);
                if(index>0&&index<=arrayLength) array[index-1] = value;
            }
        }
        return array;
    }
    
    public float[] toFloat(String line, int arrayLength){
        String[] tokens = line.trim().split("\\s+");
        float[]  array = new float[arrayLength];
        for(int i = 0; i < tokens.length; i++){
            if(tokens[i].contains(":")==true){
                String[] pair = tokens[i].split(":");
                int     index = Integer.parseInt(pair[0]);
                float  value = Float.parseFloat(pair[1]);
                if(index>0&&index<=arrayLength) array[index-1] = value;
            }
        }
        return array;
    }
    
    public String toDataString(double[] array){
       StringBuilder str = new StringBuilder();
       String dataFormat = "%d:"+ outFormat;
       for(int i = 0; i < array.length; i++){
           if(array[i]>threshold){
               str.append(String.format(dataFormat, i+1,array[i])).append(" ");
           }
       }
       return str.toString();
    }
    
    public DataPair toData(String line){
        double[] dataDouble = this.toDouble(line, nArrayLength);
        String[] tokens     = line.split("\\s+");
        
        int lineClass = Integer.parseInt(tokens[0]);
        double[] output = new double[classes];
        for(int i = 0; i < output.length; i++) output[i] = 0.0;
        if(lineClass<classes){
            output[lineClass] = 1.0;
        } else {
            System.out.println("lsvm::error >> class " + lineClass + " is invalid");
        }
        return new DataPair(dataDouble,output);    
    }
    
    public DataPair nextData(){
        boolean status = reader.readNext();
        if(status==false) return new DataPair(null,null);
        String line = reader.getString();
        //System.out.println("line : " + line);
        double[] dataDouble = this.toDouble(line, nArrayLength);
        String[] tokens     = line.split("\\s+");
        
        int lineClass = Integer.parseInt(tokens[0]);
        double[] output = new double[classes];
        for(int i = 0; i < output.length; i++) output[i] = 0.0;
        if(lineClass<classes){
            output[lineClass] = 1.0;
        } else {
            System.out.println("lsvm::error >> class " + lineClass + " is invalid");
        }
        return new DataPair(dataDouble,output);
    }
    
    public double[] nextDouble(){
        boolean status = reader.readNext();
        if(status==false) return null;
        String line = reader.getString();
        //System.out.println("line : " + line);
        double[] dataDouble = this.toDouble(line, nArrayLength);
        return dataDouble;
    }
    
    public static void main(String[] args){
        
        LSVMFileReader reader = new LSVMFileReader("dc_lstm_data_500K.lsvm",36);
        
        for(int i = 0; i < 10; i++){
            DataPair pair = reader.nextData();
            pair.show();
        }
        /*int whichSwitch = 0;
        
        if(args.length>0) whichSwitch = Integer.parseInt(args[0]);
        
        
        List<int[]> dataSwitch = new ArrayList<>();
        
        dataSwitch.add(new int[]{0,1,2,3,4,5,6,7,8,9,10,11});
        dataSwitch.add(new int[]{12,13,14,15,16,17,18,19,20,21,22,23});
        dataSwitch.add(new int[]{24,25,26,27,28,29,30,31,32,33,34,35});
        dataSwitch.add(new int[]{0,1,2,3,4,5,12,13,14,15,16,17});
        dataSwitch.add(new int[]{0,1,2,3,4,5,18,19,20,21,22,23});
        
        
        LSVMFileReader reader = new LSVMFileReader("dc_lstm_data_500K.lsvm",36);
        for(int i = 0; i < 3000; i ++){
            List<double[]> buffer = new ArrayList<>();
            for(int k = 0; k < 10; k++){
                double[] v = reader.nextDouble();
                buffer.add(v);
            }
            
            for(int j = 0; j < 9; j++){
                double[] a = buffer.get(j);
                double[] b = buffer.get(j+1);
                double[] c = DataArrayUtils.swap(a, b, dataSwitch.get(whichSwitch));
                //System.out.println("\n\nevent = " + i);
                //System.out.println(reader.toDataString(a));
                //System.out.println(reader.toDataString(b));
                double distance =112*DataArrayUtils.difference(a, c)/6;
                //System.out.println("difference = " + 112*DataArrayUtils.difference(a, c)/6);                
                if(distance>32) System.out.println(reader.toDataString(c));
            }
        }*/
    }
}
