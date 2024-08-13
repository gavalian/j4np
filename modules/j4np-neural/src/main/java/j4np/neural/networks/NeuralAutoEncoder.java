/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.networks;

import deepnetts.net.layers.activation.ActivationType;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.deepnetts.DeepNettsNetwork;
import java.util.Random;

/**
 *
 * @author gavalian
 */
public class NeuralAutoEncoder {
    public static void main(String args[]){
    
        DataList list = DataList.fromCSV("data2.csv", DataList.range(0, 12), DataList.range(0, 12));
        //DataList list = DataList.fromCSV("data2.csv", new int[]{0,2,4,6,8,10}, new int[]{0,2,4,6,8,10});
        list.show();
        Random r = new Random();
        
        
        list.show();
        
        DataList[] data = DataList.split(list, 0.8,0.2);
        
        
        for(DataEntry entry : data[0].getList()){
            float[] input = entry.features();
            int order = r.nextInt(6);
            input[2*order] = 0.0f;
            input[2*order+1] = 0.0f;
        }
        
        DeepNettsNetwork encoder = new DeepNettsNetwork();
        encoder.activation(ActivationType.TANH)
                .outputActivation(ActivationType.LINEAR);
        //.lossType(LossType.MEAN_SQUARED_ERROR);
        
        encoder.init(new int[]{12,24,12,24,12});
        
        encoder.train(data[0], 1);
        
        for(DataEntry entry : data[1].getList()){
            float[] input = entry.features();
            int order = 0;
            input[2*order] = 0.0f;
            input[2*order+1] = 0.0f;
        }
        long then = System.currentTimeMillis();
        encoder.evaluate(data[1]);
        long now = System.currentTimeMillis();
        System.out.printf(" time = %d, size = %d\n",now-then, data[1].getList().size() );
        data[1].show();
        data[1].export("data_csv_evaluated12nbbb.csv");
        
    }
}
