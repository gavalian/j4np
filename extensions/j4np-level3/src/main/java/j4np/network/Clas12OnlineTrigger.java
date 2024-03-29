/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.network;

import j4np.level3.data.EvioDataSource;
import j4np.level3.data.Level3Decoder;
import j4np.utils.BenchmarkTimer;
import j4np.utils.io.OptionParser;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 *
 * @author gavalian
 */
public class Clas12OnlineTrigger {
    
    Level3Decoder            decoder = null;
    Clas12TriggerProcessor processor = null;
    
    String         connectionString = "/tmp/etlocal:localhost";
    String         networkFile      = "etc/network/trained_model.h5";
    int            numberOfThreads  = 12;
    
    public Clas12OnlineTrigger(){
        
    }
    
    public void configure(){
        
        decoder = new Level3Decoder(connectionString,400);
        decoder.setThreads(numberOfThreads);
        
        decoder.init();
        processor = new Clas12TriggerProcessor();
        processor.initNetwork(networkFile);
        
    }
    
    public void run(){
        int counter = 0;
        int stats   = 0;
        BenchmarkTimer timer = new BenchmarkTimer();
        
        while(decoder.hasNext()==true){
            timer.resume();
            processor.processNext(decoder);
            timer.pause();
            counter++;
            if(counter%10==0){
                System.out.println(decoder.getStatusStringRate());
                System.out.printf("\t>>> counter = %12d <-> %5.2f msec/event\n",
                        counter,
                        ( (double) timer.getMiliseconds())/timer.getCounter());                
            }
        }
    }
    
    public void process(String filename, String networkFileName){
        decoder = new Level3Decoder(filename,400);
        decoder.setThreads(numberOfThreads);
        if(filename.contains(":")==false){
            EvioDataSource evio = new EvioDataSource();
            decoder.setDataSource(evio);
        }
        decoder.init();
        
        processor = new Clas12TriggerProcessor();
        processor.initNetwork(networkFileName);
        
        this.run();
    }
    
    public static void testInIDE(){
        Clas12OnlineTrigger trigger = new Clas12OnlineTrigger();
        trigger.configure();
        trigger.run();
    }        
    
    public static void main(String[] args){
        
        if(args.length==0){
            //Clas12OnlineTrigger.testInIDE();
            Clas12OnlineTrigger trigger = new Clas12OnlineTrigger();
            //trigger.decoder.setThreads(6);
            
            trigger.process("/Users/gavalian/Work/DataSpace/evio/clas_003852.evio.981", "etc/network/trained_model.h5");
        }
        
        OptionParser parser = new OptionParser();
        
        parser.addRequired("-f", "file name to process (use /dir/file:host to attach to et ring)");
        
    }
}
