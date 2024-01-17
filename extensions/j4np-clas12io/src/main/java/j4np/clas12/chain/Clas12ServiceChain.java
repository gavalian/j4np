/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.chain;

import j4np.clas12.decoder.Clas12ConvertService;
import j4np.clas12.decoder.Clas12DecoderService;
import j4np.clas12.decoder.Clas12TranslateService;
import j4np.data.base.DataEvent;
import j4np.data.base.DataFrame;
import j4np.data.base.DataSource;
import j4np.data.base.DataSync;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoWriter;
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
    ForkJoinPool         streamPool = null;
    public int      numberOfThreads =  8;
    public int            frameSize =  16;
    
    DataSource source = null;
    DataSync     sync = null;
    DataFrame<Event>  frame = new DataFrame<>();
    
    public Clas12ServiceChain(){
        
    }
     
     public Clas12ServiceChain addWorker(DataWorker worker){
         workers.add(worker); return this;
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
    
    public void process(){
        this.initThreadPool();
        this.initDataFrame();
        
        while(source.hasNext()==true){
            try {
                source.nextFrame(frame);
                
                for(DataWorker consumer : workers){                
                    Stream<Event> stream = frame.getParallelStream();
                    long then = System.nanoTime();                                        
                    streamPool.submit(()-> stream.parallel().forEach(consumer)).get();
                }
                if(sync!=null) 
                    for(DataEvent data : frame.getList()) sync.add(data);
                
                long now = System.nanoTime();
            } catch (InterruptedException ex) {
                Logger.getLogger(Clas12ServiceChain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(Clas12ServiceChain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(sync!=null) sync.close();
        System.out.println(" done...... ");
    }
    
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/DataSpace/evio/clas_018640.evio.00000";
        Clas12ServiceChain chain = new Clas12ServiceChain();
        
        Evio2HipoSource source = new Evio2HipoSource();
        source.open(file);
        
        HipoWriter w = new HipoWriter();
        w.getSchemaFactory().initFromDirectory("/Users/gavalian/Work/Software/project-10.8/distribution/coatjava/etc/bankdefs/hipo4");
        w.open("output_chain.h5");
        
        chain.setSource(source).setSync(w);
        
        Clas12DecoderService decoder = new Clas12DecoderService();
        Clas12TranslateService translate = new Clas12TranslateService();
        Clas12ConvertService convert = new Clas12ConvertService();
        convert.init(null);
        chain.addWorker(decoder).addWorker(translate).addWorker(convert);
        
        chain.process();
    }
}
