/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.stream;

import j4np.data.base.DataEvent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author gavalian
 */
public class DataStreamProcess {
    
    private       int      nThreads = 4;
    private      long   elapsedTime = 0L;
    private  Supplier  dataSupplier = null;
    
    public DataStreamProcess(){
        
    }
    
    public DataStreamProcess(int threads){
        nThreads = threads;
    }
    
    
    public void run(Consumer consumer){
        
        ForkJoinPool runPool = new ForkJoinPool(nThreads);
        Stream<DataEvent> stream = Stream.generate(dataSupplier).limit(nThreads);
        //consumer = new DataEventConsumer();
        
        try {
            long then = System.currentTimeMillis();
            runPool.submit(()-> stream.parallel().forEach(consumer)).get();
            long  now = System.currentTimeMillis();
            elapsedTime = (now - then);            
        } catch (InterruptedException ex) {
            Logger.getLogger(DataStreamProcess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(DataStreamProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args){
        
    }
}
