/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.level3.data;

import j4np.data.base.DataFrame;
import j4np.data.base.DataSource;
import j4np.data.decoder.DataDecoderEvio;
import j4np.data.evio.EvioEvent;
import j4np.data.evio.EvioFile;
import j4np.network.InputDataStream;
import j4np.utils.Benchmark;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 *
 * @author gavalian
 */
public class Level3Decoder implements InputDataStream {        

    private String  connectionString = "";
    private int           debugLevel = 1;
    private boolean      continueRun = true;
    private int        dataFrameSize = 50;
    private int             nthreads = 2;
    private int                delay = 2000;
    
    DataFrame<EvioFile,DecoderEvent> frame = new DataFrame();
    private DataSource   dataSource = null;
    
    //private EtDataSource   dataSource = null;
    
    private DataDecoderEvio   decoder = new DataDecoderEvio();
    private Benchmark       benchmark = new Benchmark();   
    
    
    ForkJoinPool decoderPool = null;
    
    public Level3Decoder(String connection, int frameSize){
        this.connectionString = connection;
        this.dataFrameSize = frameSize;
        
        benchmark.addTimer("reader");
        benchmark.addTimer("decode");        
        benchmark.addTimer("convert");
        
    }
    
    public Level3Decoder setThreads(int n){ nthreads = n; return this;}
    public Level3Decoder setFrameSize(int n){ dataFrameSize = n; return this;}
    public Level3Decoder setDelay(int n){ delay = n; return this;}
    
    public  Level3Decoder setDataSource(DataSource ds){
        this.dataSource = ds; return this;
    }
    
    private void connect(String connection){
        //dataSource = new EtDataSource(dataFrameSize);
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
        return String.format("[level-3] >> read %5.2f msec, decode %5.2f msec, convert %5.2f msec", 
                benchmark.getTimer("reader").getMiliseconds(),
                benchmark.getTimer("decode").getMiliseconds(),
                benchmark.getTimer("convert").getMiliseconds()
                );
        
    }
    
    public String getStatusStringRate(){
        return String.format("[level-3] >> read %5.2f msec, decode %5.2f msec, convert %5.2f msec", 
                ((double) benchmark.getTimer("reader").getMiliseconds())
                        /benchmark.getTimer("reader").getCounter(),
                ((double) benchmark.getTimer("decode").getMiliseconds())
                        /benchmark.getTimer("decode").getCounter(),
                ((double) benchmark.getTimer("convert").getMiliseconds())
                / benchmark.getTimer("convert").getCounter()
                );        
    }
    
    public void init(){
        
        for(int i = 0; i < this.dataFrameSize; i++){
            this.frame.addEvent(new DecoderEvent());
        }
        System.out.println("\n\n\n");
        System.out.println("************************************");
        System.out.printf("created  data frame with size : %d\n",this.frame.getCount());
        
        this.decoderPool = new ForkJoinPool(nthreads);
        System.out.printf("created thread pool with size : %d\n",nthreads);
        connect(connectionString);
        System.out.printf("connected to et ring address  : %s\n",connectionString);
        //System.out.println("et ring connection status    : %s\n",);
        System.out.println("************************************");
        
    }
    
    
    @Override
    public void open(String url) {
        this.connect(connectionString);
    }

    @Override
    public void setBatch(int size) {
        this.dataFrameSize = size/6;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public INDArray[] next() {
        
        int nfilled = 0;
        int ntdc    = 0;
        
        try {
            
            this.benchmark.getTimer("decode").resume();
            dataSource.nextFrame(frame);
            Stream stream = frame.getParallelStream();
            decoderPool.submit(() -> stream.forEach(decoder)).get();
            this.benchmark.getTimer("decode").pause();
            
            int nframes = frame.getCount();
            
            this.benchmark.getTimer("convert").resume();
            INDArray DCArray=Nd4j.zeros(6*nframes,6,112,1);
            INDArray ECArray=Nd4j.zeros(6*nframes,6, 72,1);        
            for(int f = 0; f < nframes; f++){
                DecoderEvent event = (DecoderEvent) frame.getEvent(f);
                int rows = event.tdcData.getRows();
                ntdc += rows;
                for(int r = 0 ; r < rows; r++){
                    int detector = event.tdcData.getInt(r, 0);
                    int   sector = event.tdcData.getInt(r, 1);
                    //System.out.printf("detector = %5d sector %5d\n",detector, sector);
                    if(detector==0){
                        int index1 = (sector-1)+f*6;
                        int  layer = event.tdcData.getInt(r, 2);
                        int   wire = event.tdcData.getInt(r, 3)-1;
                        int superlayer = (layer-1)/6;
                        //System.out.printf("--- %5d %5d %5d %5d, sector = %d frame = %d (nframes=%d)\n"
                        //        ,index1,superlayer,wire,0,sector,f,nframes);
                        double value = DCArray.getDouble(index1,superlayer,wire,0) + 1.0/6.0;
                        DCArray.putScalar(new int[]{index1,superlayer,wire,0}, value);
                        nfilled++;
                    }                    
                }
            }
            this.benchmark.getTimer("convert").pause();
            System.out.printf("tdc processed %8d, filled %8d\n",ntdc,nfilled);
            return (new INDArray[]{DCArray,ECArray});
            
        } catch (InterruptedException ex) {
            Logger.getLogger(Level3Decoder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(Level3Decoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void apply(INDArray result) {
        long size = result.size(0);
        int  positive = 0;
        int  negative = 0;
        
        
        for(int i = 0; i < size; i++){
            double prob = result.getDouble(i,1);
            if(prob>0.5){
               positive++;
            } else negative++;
        }
        System.out.printf("\t\t**** size = %8d, positive = %8d, negative = %8d\n",
                size,positive,negative);
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
