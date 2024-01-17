/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.decoder;

import j4np.data.base.DataFrame;
import j4np.data.base.DataStream;
import j4np.data.base.DataWorker;
import j4np.data.evio.EvioEvent;
import j4np.data.evio.EvioFile;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.data.SchemaFactory;
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
public class Clas12Decoder {
    
    ForkJoinPool         streamPool = null;
    public SchemaFactory factory = new SchemaFactory();
    public int      numberOfThreads =  6;
    public int            frameSize = 16;
    //Clas12DecoderService service = new Clas12DecoderService();
    private DataWorker         consumer = null;
    private DataWorker        converter = null;
    
    private List<DataWorker>  workers = new ArrayList<>();
    private long           timeConsumer = 0L;

    
    public Clas12Decoder(){
        
    }
    
    public void initThreadPool(){
        System.out.println("****************************************************************");
        System.out.printf("* initializing stream poll with %d threads *\n",numberOfThreads);
        System.out.println("****************************************************************");
        streamPool = new ForkJoinPool(numberOfThreads);
    }
    
    
    
    public void consumer(DataWorker c){ consumer = c;}
    public void converter(DataWorker c){ converter = c;}
    
    public void decode(DataFrame<Event> frame){
        try {
            Stream<Event> stream = frame.getParallelStream();
            long then = System.nanoTime();
            streamPool.submit(()-> stream.parallel().forEach(consumer)).get();
            long now = System.nanoTime();
            timeConsumer += (now-then);            
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(DataStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void convert(DataFrame<Event> frame){
        try {
            Stream<Event> stream = frame.getParallelStream();
            long then = System.nanoTime();
            streamPool.submit(()-> stream.parallel().forEach(converter)).get();
            long now = System.nanoTime();
            timeConsumer += (now-then);            
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(DataStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void decodeFile(String file, String output){
        this.timeConsumer = 0L;
        EvioFile r = new EvioFile();
        r.open(file);
        DataFrame<Event> frame = new DataFrame<>();
        for(int k = 0; k < this.frameSize; k++) frame.addEvent(new Event(2*1024*1024));
        EvioEvent event = new EvioEvent();
        Event     hipo = new Event();
        
        HipoWriter w = new HipoWriter();
        w.getSchemaFactory().copy(factory);
        //Node node_evio = new Node(1,11,DataType.BYTE,1*1024*1024);
        w.open(output);
        int current = 0;
        
        while(r.hasNext()==true){
            r.next(event);            
            //node_evio.initDataFrom(event.getBuffer().array(), event.bufferLength()*4+8);
            Node node_evio = new Node(1,11,event.getBuffer().array(),0,event.bufferLength()*4+8);                       
            frame.getList().get(current).reset();
            frame.getList().get(current).write(node_evio);
            current++;
            if(current>=frameSize) { 
                this.decode(frame);
                if(converter!=null) this.convert(frame);
                current = 0;
                for(Event e : frame.getList()) w.addEvent(e);
            }
        }
        w.close();
        double time = this.timeConsumer;
        time /= 1_000_000_000;
        System.out.printf("\n\n*** consumer time = %.4f sec\n\n",time );
    }
    
    public static void main(String[] args){
        
        String file = "/Users/gavalian/Work/DataSpace/evio/clas_018640.evio.00000";
        Clas12Decoder d = new Clas12Decoder();
        Clas12DecoderService service = new Clas12DecoderService();
        Clas12ConvertService convert = new Clas12ConvertService();
        
        service.initialize();
        
        convert.init(null);
        d.factory.initFromDirectory("/Users/gavalian/Work/Software/project-10.8/distribution/coatjava/etc/bankdefs/hipo4");
        d.initThreadPool();
        d.consumer(service);
        d.converter(convert);
        //for(int k = 0; k < 500; k++)
        d.decodeFile(file, "output_decoder.h5");
    }
}
