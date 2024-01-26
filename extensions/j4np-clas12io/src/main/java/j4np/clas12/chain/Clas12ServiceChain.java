/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.chain;

import j4np.clas12.decoder.Clas12ConvertService;
import j4np.clas12.decoder.Clas12DecoderService;
import j4np.clas12.decoder.Clas12FitterService;
import j4np.clas12.decoder.Clas12TranslateService;
import j4np.data.base.DataEvent;
import j4np.data.base.DataFrame;
import j4np.data.base.DataSource;
import j4np.data.base.DataSync;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.utils.ProgressPrintout;
import j4np.utils.io.OptionParser;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author gavalian
 */
public class Clas12ServiceChain {
    
    private List<DataWorker>  workers = new ArrayList<>();
    private List<DataWorker>  monitor = new ArrayList<>();
    
    ForkJoinPool         streamPool = null;
    public int      numberOfThreads =   4;
    public int            frameSize =  16;
    public int            maxEvents =  -1;
    
    DataSource source = null;
    DataSync     sync = null;
    DataFrame<Event>  frame = new DataFrame<>();
    
    public Clas12ServiceChain(){
        
    }
     
    public Clas12ServiceChain addWorker(DataWorker worker){
         workers.add(worker); return this;
    }
     
    public Clas12ServiceChain addMonitor(DataWorker worker){
         monitor.add(worker); return this;
    }
    
    public Clas12ServiceChain setSource(DataSource src){ source = src; return this;}
    public Clas12ServiceChain setSync(DataSync src){ sync = src; return this;}
    
    protected void initThreadPool(){
        System.out.println("****************************************************************");
        System.out.printf("* initializing stream poll with %d threads *\n",numberOfThreads);
        System.out.println("****************************************************************");
        streamPool = new ForkJoinPool(numberOfThreads);
    }
    
    protected void initDataFrame(){
        frame.reset();
        for(int j = 0; j < frameSize; j++) frame.addEvent(new Event());
    }
    
    public void process_original(){
        this.initThreadPool();
        this.initDataFrame();
        int nProcessed = 0;
        ProgressPrintout progress = new ProgressPrintout();
        while(source.hasNext()==true){
            try {
                source.nextFrame(frame);
                
                for(DataWorker consumer : workers){                
                    Stream<Event> stream = frame.getParallelStream();
                    long then = System.nanoTime();                                        
                    streamPool.submit(()-> stream.parallel().forEach(consumer)).get();
                }
                progress.updateStatus(frame.getCount());
                nProcessed += frame.getCount();
                if(sync!=null) 
                    for(DataEvent data : frame.getList()) sync.add(data);
                
                long now = System.nanoTime();
            } catch (InterruptedException ex) {
                Logger.getLogger(Clas12ServiceChain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(Clas12ServiceChain.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(this.maxEvents>0&&nProcessed>this.maxEvents) break;            
        }
        
        if(sync!=null) sync.close();
        System.out.println(" done...... ");
    }
    
    public void process(){
        this.initThreadPool();
        this.initDataFrame();
        int nProcessed = 0;
        
        DataWorker<HipoReader,Event> consumer = new DataWorker<>(){
            @Override
            public boolean init(HipoReader src) {
                return true;//throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void execute(Event e) {
                for(DataWorker consumer : workers){
                    consumer.accept(e);
                }
            }
            
        };
        ProgressPrintout progress = new ProgressPrintout();
        progress.setMode(1);
        while(source.hasNext()==true){
            try {
                source.nextFrame(frame);
                
                //for(DataWorker consumer : workers){                
                Stream<Event> stream = frame.getParallelStream();
                long then = System.nanoTime();                                        
                streamPool.submit(()-> stream.parallel().forEach(consumer)).get();
                //}
                for(Event ev : frame.getList()){
                    for(DataWorker mon : monitor){
                        mon.accept(ev);
                    }
                }
                progress.updateStatus(frame.getCount());
                nProcessed += frame.getCount();
                if(sync!=null) 
                    for(DataEvent data : frame.getList()) sync.add(data);
                
                long now = System.nanoTime();
            } catch (InterruptedException ex) {
                Logger.getLogger(Clas12ServiceChain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(Clas12ServiceChain.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(this.maxEvents>0&&nProcessed>this.maxEvents) break;            
        }
        
        if(sync!=null) sync.close();
        System.out.println(progress.getUpdateString());
        System.out.println(" done...... ");
    }
    
    public void singleDecode(){
        
        this.initDataFrame();
        int nProcessed = 0;
        ProgressPrintout progress = new ProgressPrintout();
        progress.setMode(1);
        while(source.hasNext()==true){
            try {
                source.nextFrame(frame);
                for(int n = 0; n < frame.getCount(); n++){
                    for(DataWorker consumer : workers){      
                        consumer.execute(frame.getEvent(n));
                        //Stream<Event> stream = frame.getParallelStream();
                        //long then = System.nanoTime();                                        
                        //streamPool.submit(()-> stream.parallel().forEach(consumer)).get();
                    }
                }
                progress.updateStatus(frame.getCount());
                if(sync!=null) 
                    for(DataEvent data : frame.getList()) sync.add(data);
                
                long now = System.nanoTime();
                nProcessed += frame.getCount();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("---- oops....");
            }
            if(this.maxEvents>-1&&nProcessed>maxEvents) break;
        }
        
        if(sync!=null) sync.close();
        System.out.println(progress.getUpdateString());
        System.out.println(" done...... ");
    }
    
    public static void main(String[] args){
        
        OptionParser parser = new OptionParser();
        parser.addRequired("-o", "output file name")
                .addOption("-n", "-1","number of events to process")
                .addRequired("-d", "dictionary directory")
                .addOption("-t", "4","number of threads")
                .addOption("-f", "32", "data frame size")
                .addOption("-m", "3", "decoding mode");
        
        parser.parse(args);
        
        String file = parser.getInputList().get(0);
        
        Clas12ServiceChain chain = new Clas12ServiceChain();
        chain.maxEvents = parser.getOption("-n").intValue();
        chain.numberOfThreads= parser.getOption("-t").intValue();
        chain.frameSize = parser.getOption("-f").intValue();
        
        Evio2HipoSource source = new Evio2HipoSource();
        source.open(file);
        
        System.out.printf(">>>> loading dictionary from [%s]\n",parser.getOption("-d").stringValue());
        HipoWriter w = new HipoWriter();
        w.getSchemaFactory().initFromDirectory(parser.getOption("-d").stringValue());
        w.open(parser.getOption("-o").stringValue());
        
        chain.setSource(source).setSync(w);
        
        Clas12DecoderService decoder = new Clas12DecoderService();
        Clas12FitterService fitter = new Clas12FitterService();
        Clas12TranslateService translate = new Clas12TranslateService();
        Clas12ConvertService convert = new Clas12ConvertService();
        convert.init(null);
        
        int mode = parser.getOption("-m").intValue();
        
        if(mode>0) chain.addWorker(decoder);
        if(mode>1) chain.addWorker(fitter);
        if(mode>2) chain.addWorker(translate);
        if(mode>3) chain.addWorker(convert);
        
        long now= 0L; long then = 0L;
        then = System.currentTimeMillis();
        chain.process();
        //chain.singleDecode();
        now = System.currentTimeMillis();
        System.out.printf("first time = %d msec\n",now-then);
        
        //chain.maxEvents = 4000;
        //chain.singleDecode();
    }
}
