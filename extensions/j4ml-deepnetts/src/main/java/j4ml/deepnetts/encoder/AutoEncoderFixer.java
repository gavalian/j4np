/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.deepnetts.encoder;

import deepnetts.data.TabularDataSet;
import deepnetts.net.FeedForwardNetwork;
import j4ml.deepnetts.network.DeepNettsRegression;
import j4np.utils.io.DataPair;
import j4np.utils.io.DataPairList;
import j4np.utils.io.TextFileReader;
import java.util.Iterator;
import javax.visrec.ml.data.DataSet;
import org.jlab.jnp.readers.TextFileWriter;

/**
 *
 * @author gavalian
 */
public class AutoEncoderFixer {
    private   int  startPosition = 1;
    private   int         length = 6;
    
    public  DataPairList loadData(String filename){
        TextFileReader reader = new TextFileReader();
        reader.setSeparator(",");
        reader.open(filename);
        DataPairList list = new DataPairList();

        while(reader.readNext()==true){
            double[]  first = reader.getAsDoubleArray(startPosition, length);
            double[] second = reader.getAsDoubleArray(startPosition, length);
            list.add(new DataPair(first,second));
        }
        return list;
    }
    
    public DataSet generate(DataPairList list){
        TabularDataSet  dataset = new TabularDataSet(6,6);
        int size = list.getList().size();
        for(int i = 0; i < size; i++){
            for(int j = 0; j < 6; j++){
                float[] input = list.getList().get(i).floatFirst();
                float[] output = list.getList().get(i).floatSecond();
                input[j] = (float) 0.0;
                dataset.add(new TabularDataSet.Item(input, output));
            }
        }
        
        dataset.setColumnNames(DeepNettsRegression.generateNames(6, 6));
        return dataset;
    }
    
    public int  missingNode(float[] data){
        int index = -1;
        for(int i = 0; i < data.length; i++){
            if(data[i]<0.00000000001) index = i;
        }
        return index;
    }
    
    public void evaluate(DataSet ds, FeedForwardNetwork network){
        Iterator iter = ds.iterator();
        int counter = 0;
        long then = System.nanoTime();
        TextFileWriter writer = new TextFileWriter();
        writer.open("evaluate.txt");
        
        while(iter.hasNext()){
            TabularDataSet.Item  item = (TabularDataSet.Item) iter.next();
            float[]  input  = item.getInput().getValues();
            float[]  output = item.getTargetOutput().getValues();
            
            float[]  prediction = network.predict(item.getInput().getValues());           
            
            int  which = missingNode(input);
            String result = String.format("%3d %8.5f %8.5f", which,
                    output[which],prediction[which]);
            //System.out.println(result);
            writer.writeString(result);
            counter++;
        }
        writer.close();
    }
    
    public static void main(String[] args){
        AutoEncoderFixer  fixer = new AutoEncoderFixer();        
        DataPairList list = fixer.loadData("7.txt");
        list.scan();
        
        DataSet ds = fixer.generate(list);        
        DataSet[]  data = ds.split(0.8,0.2);
        DeepNettsRegression regression = new DeepNettsRegression();
        regression.init(new int[]{6,12,12,6,12,12,6});
        regression.train(data[0], 50);
        
        
        fixer.evaluate(data[1], regression.getNetwork());
    }
}
