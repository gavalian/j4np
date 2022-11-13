/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.base;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.utils.ProgressPrintout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author gavalian
 * @param <R> the data source class
 * @param <T> the data event class
 */
public class DataStream<R extends DataSource,K extends DataSync, T extends DataEvent> {
    /*
    private DataFrame<R,T>       frame = new DataFrame<>();
    private List<Consumer>   consumers = new ArrayList<>();
    private R               dataSource = null;
    private T               dataEvent  = null;
    private int         nDataFrameSize = 2;
    */
    private int     numberOfThreads = 1;
    private int       numberOfCores = 1;
    
    private DataWorker<R,T>   consumer = null;
    private R                   source = null;
    private K              destination = null;
    private DataFrame<T>         frame = null;
    private int              maxEvents = -1;
    
    
    ForkJoinPool         streamPool = null;
        
    private long         timeReadFrame = 0L;
    private long          timeConsumer = 0L;
    private long          timeExportSync = 0L;
    
    public DataStream(){
       initThreading();
    }
    
    private final void initThreading(){
        numberOfCores = Runtime.getRuntime().availableProcessors();
        numberOfThreads = (int) (((double) 2.0*numberOfCores)/3.0);
        if(numberOfThreads==0) numberOfThreads = 1;
    }
    
    public final void show(){
        System.out.println();
        System.out.printf(">>>>     system cores : %d\n",numberOfCores);
        System.out.printf(">>>>     threads used : %d\n",numberOfThreads);
        System.out.printf(">>>>   data read time : %s\n", ProgressPrintout.timeStringFromNano(timeReadFrame) );
        System.out.printf(">>>>    consumer time : %s\n", ProgressPrintout.timeStringFromNano(timeConsumer) );
        System.out.printf(">>>> data export time : %s\n", ProgressPrintout.timeStringFromNano(timeExportSync) );
        System.out.println();
    }
    
    public DataStream withSource(R ds){ source = ds; return this;}
    public DataStream withOutput(K dst){ destination = dst; return this;}    
    public DataStream withFrame(DataFrame<T> df){ frame = df; return this;}
    public DataStream limit(int nEvents){ this.maxEvents = nEvents; return this;}
    /*
    protected void initFrame(String clazzName){
       
    } */   
    public DataStream consumer(DataWorker<R,T> c){
        consumer = c; return this;
    }
    
    public DataStream threads(int n){ numberOfThreads = n; return this;}
    public DataStream source(R src){ source = src; return this;}
    public DataStream frame(DataFrame fr){ frame = fr; return this;}
    
    public void run(String filename){
        SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());        
        System.out.printf("[stream] ( %s ) : system cores %4d, threads initialized %4d\n",
                formatter.format(date),numberOfCores,numberOfThreads);
        source.open(filename);
    }
    
    public void run(){
        
        int frameSize = frame.getCount();
        System.out.printf("\n\n****************************\n");
        System.out.printf("* Initialize Thread POOL # %d\n",numberOfThreads);
        System.out.printf("****************************\n");
        streamPool = new ForkJoinPool(numberOfThreads);
        
        boolean keepGoing = true;
        
        consumer.init(source);
        
        ProgressPrintout progress = new ProgressPrintout();
        
        while(keepGoing==true){
            
            long then = System.nanoTime();
            int nframe = source.nextFrame(frame);
            long now = System.nanoTime();            
            timeReadFrame += (now-then);
            progress.updateStatus(nframe);
            if(nframe!=frameSize) keepGoing = false;
            Stream<T> stream = frame.getParallelStream();
            
            try {
                then = System.nanoTime();
                streamPool.submit(()-> stream.parallel().forEach(consumer)).get();
                now = System.nanoTime();
                timeConsumer += (now-then);
                
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(DataStream.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(destination!=null){
                then = System.nanoTime();
                for(int r = 0; r < nframe; r++){ destination.add(frame.getEvent(r));} 
                now = System.nanoTime();
                timeExportSync += (now-then);
            }
            if(progress.getCounter()>this.maxEvents&&this.maxEvents>0) break;
        }
        consumer.finilize();
        if(destination!=null) destination.close();
    }
    
    
    public static void main(String[] args){
        
        
        String file = "/Users/gavalian/Work/dataspace/pid/rec_output_filtered_4.hipo";
        
        DataStream<HipoReader,HipoWriter,Event> str = new DataStream();
        str.show();
        
        DataFrame<Event>  frame = new DataFrame<>();
        HipoReader       source = new HipoReader();
        
        source.open(file);
        
        
        DataWorker<HipoReader,Event> worker = new DataWorker<HipoReader,Event>(){

            public final AtomicInteger counter = new AtomicInteger(0);
            private Schema  schema = null;
            @Override
            public void execute(Event e) {                
                Bank b = new Bank(schema);
                e.read(b);
                int pid = b.getInt("pid", 0);
                int status = b.getInt("status", 0);
                if(pid==11&&status>-3000&&status<-2000){
                    int howMany = counter.intValue();
                    counter.set(howMany+1);
                }
                
                try {
                    Thread.sleep(25);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DataStream.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }           

            @Override
            public boolean init(HipoReader src) {
                schema = src.getSchemaFactory().getSchema("REC::Particle");
                return true;
            }
            
            public void show(){
                System.out.printf(" counter value = %d\n",counter.intValue());
            }
            
        };
        
        for(int i = 0; i < 8; i++){ frame.addEvent(new Event());}
        str.threads(4);
        str.withSource(source).withFrame(frame).consumer(worker).run();
        
        str.show();
        //str.source(source).frame(frame).consumer();                
        
    }
}
