/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.level3.data;

import j4np.data.base.DataFrame;
import j4np.data.decoder.DataDecoderEvio;
import j4np.data.evio.EvioEvent;
import j4np.data.evio.EvioFile;
import j4np.utils.Benchmark;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author gavalian
 */
public class Level3Decoder {
    
    

    private String  connectionString = "";
    private int           debugLevel = 1;
    private boolean      continueRun = true;
    private int        dataFrameSize = 50;
    private int             nthreads = 2;
    private int                delay = 2000;
    
    DataFrame<EvioFile,DecoderEvent> frame = new DataFrame();
    private EtDataSource   dataSource = null;    
    private DataDecoderEvio   decoder = new DataDecoderEvio();
    private Benchmark       benchmark = new Benchmark();   
    
    
    public Level3Decoder(String connection, int frameSize){
        this.connectionString = connection;
        this.dataFrameSize = frameSize;
        
        benchmark.addTimer("reader");
        benchmark.addTimer("decode");
        
    }
    
    public Level3Decoder setThreads(int n){ nthreads = n; return this;}
    public Level3Decoder setFrameSize(int n){ dataFrameSize = n; return this;}
    public Level3Decoder setDelay(int n){ delay = n; return this;}
    
    private void connect(String connection){
        dataSource = new EtDataSource(dataFrameSize);
        dataSource.open(connection);
        
    }
    
    public void run(){
        init();
        continueRun = true;
        ForkJoinPool myPool = new ForkJoinPool(nthreads);
        
        while(continueRun){
            
            benchmark.getTimer("reader").resume();
            dataSource.nextFrame(frame);
            benchmark.getTimer("reader").pause();
            
            benchmark.getTimer("decode").resume();
            Stream stream = frame.getParallelStream();
            
            try {
                myPool.submit(() -> stream.forEach(decoder)).get();
            } catch (InterruptedException ex) {
                Logger.getLogger(Level3Decoder.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(Level3Decoder.class.getName()).log(Level.SEVERE, null, ex);
            }
            benchmark.getTimer("decode").pause();
            
            if(debugLevel>0){
                String status = getStatusString();
                System.out.println(status);
                int nframes = frame.getCount();
                for(int j = 0; j < nframes; j++)
                    System.out.println( ((DecoderEvent) frame.getEvent(j)).summaryString());
            }
                        
            if(delay>0){
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Level3Decoder.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public String getStatusString(){
        return String.format("[level-3] >> read %5.2f msec, decode %5.2f msec ", 
                benchmark.getTimer("reader").getMiliseconds(),
                benchmark.getTimer("decode").getMiliseconds());
    }
    
    public void init(){
        for(int i = 0; i < this.dataFrameSize; i++){
            this.frame.addEvent(new DecoderEvent());
        }        
        connect(connectionString);        
    }
    
    public static void main(String[] args){
        
        Level3Decoder level3 = new Level3Decoder("/tmp/etlocal:localhost",5);
        
        //level3.setThreads(1);
        level3.run();
        /*EtDataSource source = new EtDataSource(20);
        source.open("/tmp/etlocal:localhost");
        int time = 0;
        int interval = 2000;
        
        DataFrame<EvioFile,EvioEvent> frame = new DataFrame<>();
        
        for(int i = 0 ; i < 50; i++) frame.addEvent(new EvioEvent());
        
        while(true){
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ex) {
                Logger.getLogger(Level3Decoder.class.getName()).log(Level.SEVERE, null, ex);
            }
            time += interval;
            System.out.printf("------ elapsed time %d mili-seconds\n",time);
            source.nextFrame(frame);
        }*/
    }
}
