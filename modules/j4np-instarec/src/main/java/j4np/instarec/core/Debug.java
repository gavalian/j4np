/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.instarec.network.DataExtractor.DataPair;
import j4np.instarec.utils.EJMLModel;
import j4np.utils.io.TextFileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author gavalian
 */
public class Debug {
    
    public static List<DataPair> generate(int count, int inputSize, int outputSize){
        List<DataPair> pairs = new ArrayList<>();
        Random r = new Random();
        for(int i = 0; i < count; i++){
            float[] input = new float[inputSize];
            float[] output = new float[outputSize];
            for(int j = 0; j < input.length; j++) input[j] = r.nextFloat();
            for(int j = 0; j < output.length; j++) output[j] = r.nextFloat();
            pairs.add(new DataPair(input,output));
        }
        return pairs;
    }
    
    public static void debugThreading(int threads){
        List<String> lines = TextFileReader.readFile("etc/networks/trackclassifier12.network");
        EJMLModel model = EJMLModel.create(lines);
        System.out.println(model.summary());
        
        List<DataPair> data = Debug.generate(32, model.getInputSize(), model.getOutputSize());
        int iter = 500000; int counter = 0;
        
        ForkJoinPool streamPool = new ForkJoinPool(threads);
        
        Consumer<DataPair> consumer = (DataPair t) -> {
            model.feedForwardSoftmax(t.input(), t.output());
        };
        
        long then = System.currentTimeMillis();
        for(int i = 0; i < iter; i++){
            Stream<DataPair> stream = data.parallelStream();
            try {
                streamPool.submit(()-> stream.parallel().forEach(consumer)).get();
                counter+= data.size();
                //for(int d = 0; d < data.size(); d++){
                //    model.feedForwardSoftmax(data.get(d).input(), data.get(d).output()); counter++;
                //}
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(Debug.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        long  now = System.currentTimeMillis();
        System.out.printf("parrallel processed = %14d, in %14d msec, %4d\n",counter,now-then,threads);

        counter = 0;
        then = System.currentTimeMillis();
        for(int i = 0; i < iter; i++){
            for(int d = 0; d < data.size(); d++){
                model.feedForwardSoftmax(data.get(d).input(), data.get(d).output()); counter++;
            }
        }
        now = System.currentTimeMillis();
        System.out.printf("serial    processed = %14d, in %14d msec, %4d\n",counter,now-then,threads);        
    }
    
    public static void main(String[] args){
        Debug.debugThreading(2);
        Debug.debugThreading(4);
        Debug.debugThreading(8);
        Debug.debugThreading(10);
       /* HipoReader r = new HipoReader("wout.h5");
        Bank[] b = r.getBanks("HitBasedTrkg::Clusters");
        Event e = new Event();
        
        while(r.next(e)){
            //Bank[] bb = e.read(b[0].getSchema());
            e.read(b);
            b[0].show();
        }*/
    }
        
}
