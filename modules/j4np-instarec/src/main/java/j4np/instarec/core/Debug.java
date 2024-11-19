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
import java.nio.ByteBuffer;
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

    public static byte[] getRandom(int length){
        Random r = new Random();
        byte[] b = new byte[length];
        for(int i = 0; i < length; i++) b[i] = (byte) r.nextInt(128);
        return b;
    }
    
    public static List<ByteBuffer>  getList(int size, int length){
        List<ByteBuffer> l = new ArrayList<>();
        for(int i = 0; i < size; i++) l.add(ByteBuffer.wrap(new byte[length]));
        return l;
    }
    
    public static void debugTransport(){
        List<ByteBuffer>  frame = Debug.getList(64, 65*1024);
        List<ByteBuffer> single = Debug.getList(1, 65*1024*64);
        byte[] br1 = Debug.getRandom(62*1024);
        byte[] br2 = Debug.getRandom(62*1024*64);
        int iter = 500000;
        
        long then = System.currentTimeMillis();
        for(int i = 0; i < iter; i++){
            for(int j = 0; j < frame.size(); j++){
                System.arraycopy(br1, 0, frame.get(j).array(), 0, br1.length);
            }
        }
        long  now = System.currentTimeMillis();
        System.out.printf("iteration = %d, time = %d\n",iter,now-then);
        then = System.currentTimeMillis();
        for(int i = 0; i < iter; i++){            
            System.arraycopy(br2, 0, single.get(0).array(), 0, br2.length/2);            
        }
        now = System.currentTimeMillis();
        System.out.printf("iteration = %d, time = %d\n",iter,now-then);
        
        
        
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
        /*Debug.debugThreading(2);
        Debug.debugThreading(4);
        Debug.debugThreading(8);
        Debug.debugThreading(10);
        */
        Debug.debugTransport();
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
