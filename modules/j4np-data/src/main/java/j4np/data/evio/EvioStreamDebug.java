/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.evio;

import j4np.data.base.DataFrame;
import j4np.data.base.DataSourceFrame;
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
public class EvioStreamDebug {
    
    public static class PrimeNumber{
        
        Random r = new Random();
        long   start = 50000000;
        
        public boolean isPrime(long n){
            if (n <= 1) {  
                return false;  
            } 
            long iter = (long) Math.sqrt(n);
            for(int i = 1; i < iter; i++){
                if(n%i == 0 ) return false;
            }
            return true;
        }
        
        public int find(long number){
            int counter = 0;
            for(long i = 0; i < number; i++){
               if(isPrime(i)==true) counter++; 
            }
            return counter;
        }
        
        public void doPrimes(){
            int count = find(start);
        }
        
    }
    

    public static class EvioEventConsumer implements Consumer<EvioEvent> {
        PrimeNumber prime = new PrimeNumber();
        @Override
        public void accept(EvioEvent event) {
            prime.doPrimes();
        }
        
    }
    
    public static void run(DataSourceFrame frame, Consumer consumer){
        Stream<EvioEvent>  stream = frame.getStream();
        stream.forEach(consumer);
    }
    
    public static void runParallel(DataSourceFrame frame, Consumer consumer){
        Stream<EvioEvent>  stream = frame.getParallelStream();
        stream.forEach(consumer);
    }
    
    public static void benchmarkEvioStream(String filename, Consumer consumer, int nframe, int threads){
        DataFrame<EvioEvent> frame = new DataFrame();
        for(int i = 0; i < nframe; i++) frame.addEvent(new EvioEvent(128));
        
        EvioFile  reader = new EvioFile();
        EvioEvent  event = new EvioEvent();
        
        reader.open(filename);
        
        long then = System.currentTimeMillis();
        boolean isEmpty = false;
        
        ForkJoinPool myPool = new ForkJoinPool(threads);
        int counter = 0;
        while(isEmpty==false&&counter<2000){            
            int count = reader.nextFrame(frame);
            counter += count;
            if(count!=frame.getCount()) isEmpty=true;
            //System.out.println("consuming at : " + counter);
            Stream<EvioEvent>  stream = frame.getParallelStream();
            try {
                myPool.submit(() -> stream.forEach(consumer)).get();                
            } catch (InterruptedException ex) {
                Logger.getLogger(EvioStreamDebug.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(EvioStreamDebug.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        long now = System.currentTimeMillis();
        double timePerEvent = (1.0*(now-then))/counter;
        System.out.printf("%4d %4d  n= %9d  time= %12d  %12.4f \n",nframe,threads,
                counter,now-then, 1.0/timePerEvent);
        
    }
    
    public static void benchmarkEvioStream(String filename, Consumer consumer){
        int[] batch = new int[]{50,100,200,400};
        
        for(int b = 0; b < batch.length; b++){
            for(int c = 2 ; c < 16; c++){
                EvioStreamDebug.benchmarkEvioStream(filename, consumer, batch[b], c*4);
            }
        }
    }
    public static void main(String[] args){
        
        /*PrimeNumber prime = new PrimeNumber();
        long then = System.currentTimeMillis();
        for(int i = 0; i < 3; i++){
            prime.doPrimes();
        }
        long now = System.currentTimeMillis();
        System.out.printf("time elapsed = %d\n",now-then);*/
        

        String filename = "/Users/gavalian/Work/DataSpace/evio/clas_003852.evio.981";
        if(args.length>0){
            filename = args[0];
        }
        
        EvioEventConsumer consumer = new EvioEventConsumer();
        //EvioStreamDebug.benchmarkEvioStream(filename, consumer, 20, 4);
        EvioStreamDebug.benchmarkEvioStream(filename, consumer);
        /*
        DataFrame<EvioFile,EvioEvent> frame = new DataFrame();        
        for(int i = 0; i < 50; i++) frame.addEvent(new EvioEvent(128));
        
        frame.show();
        
        EvioFile  reader = new EvioFile();
        EvioEvent  event = new EvioEvent();
        
        reader.open(filename);
        
        EvioEventConsumer consumer = new EvioEventConsumer();
        
        int counter = 0;
        long then = System.currentTimeMillis();
        boolean isEmpty = false;
        
        ForkJoinPool myPool = new ForkJoinPool(16);
        
        while(isEmpty==false&&counter<200){
            //for(int i = 0; i < 20; i++){
            int count = reader.nextFrame(frame);
            counter += count;
            if(count!=frame.getCount()) isEmpty=true;
                        
            Stream<EvioEvent>  stream = frame.getParallelStream();
            System.out.println("consuming at : " + counter);
            
            try {
                myPool.submit(() -> stream.forEach(consumer)).get();
                
                //frame.show();
                //boolean status = reader.next(event);
                //if(status==true) counter++;
            } catch (InterruptedException ex) {
                Logger.getLogger(EvioStreamDebug.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(EvioStreamDebug.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        long now = System.currentTimeMillis();
        System.out.printf("number of events read = %d, time = %d\n",counter,now-then);
        frame.show();*/
        
    }
}
