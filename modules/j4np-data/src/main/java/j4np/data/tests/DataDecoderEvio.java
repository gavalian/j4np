/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.tests;

import j4np.data.base.DataEvent;
import j4np.data.base.DataFrame;
import j4np.data.base.DataNodeCallback;
import j4np.data.base.DataUtils;
import j4np.data.evio.EvioEvent;
import j4np.data.evio.EvioFile;
import j4np.data.evio.EvioNode;
import j4np.data.structure.DataStructure;
import j4np.data.structure.DataStructureUtils;
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
public class DataDecoderEvio implements DataNodeCallback,Consumer<EvioEvent> {
    
    //DataStructure   tdcData = new DataStructure("bbbsbi",20000);
    DataTranslator tdcTable = new DataTranslator();
    DataTranslator ecalTable = new DataTranslator();
    
    public DataDecoderEvio(){
        tdcTable.read("etc/db/dc_tt.txt");
        ecalTable.read("etc/db/ec_tt.txt");
    }
    
    @Override
    public void apply(DataEvent event, int position, int[] identification) {
        //if(node.getTag()==57607)
        
        /*if(identification[2]==57622){
            EvioNode node = new EvioNode(2*1024);
            event.getAt(node, position);
            int crate = identification[0];
            DataDecoder.decode57622(node,crate,tdcData);
            
        }
        */
        
        if(identification[2]==57607){
            int crate = identification[0];
            EvioNode node = new EvioNode(2*1024);
            event.getAt(node, position);
            int length = node.getBuffer().getInt(0);
            /*System.out.printf("callback activated at : %8d , id length = %4d [%5d,%5d,%5d,%5d], size = %5d, rows = %d\n",
                    position,identification.length,
                    identification[0],identification[1],
                    identification[2],identification[3], length, tdcData.getRows()
                );
            */
            for(int i = 1 ; i < length; i++){
                int   word = node.getBuffer().getInt(i*4);
                int  slot  = DataUtils.getInteger(word, 27, 31 );
                int  chan  = DataUtils.getInteger(word, 19, 25);
                int  value = DataUtils.getInteger(word,  0, 18);
                if(value!=0){
                    //System.out.println("writing value " + value);
                    /*int rows = tdcData.getRows();
                    tdcData.setRows( rows+1);
                    tdcData.putByte( rows, 0, (byte) 0);
                    tdcData.putByte( rows, 1, (byte) crate);
                    tdcData.putByte( rows, 2, (byte) slot);
                    tdcData.putShort(rows, 3, (short) chan);
                    tdcData.putByte( rows, 4, (byte) 0);
                    tdcData.putInt(  rows, 5, value);*/
                }
                //System.out.printf("\t crate = %4d, slot = %4d, chan = %4d, value = %8d\n",
                //        crate,slot,chan,value);
            }
        }
    }
    @Override
    public void accept(EvioEvent event) {
        processDataEvent(event);
    }
    
    public void processDataEvent(EvioEvent event){
        
        //System.out.println("---- processing event ");
        DataStructure   tdcData = new DataStructure("bbbsbi",20000);
        DataStructure   adcData = new DataStructure("bbbsbi",20000);
        tdcData.setRows(0);
        adcData.setRows(0);
        //DataStructure tdcData = new DataStructure("bbbsbi",1200); 
        
        DataNodeCallback callback = new DataNodeCallback(){
            @Override
            public void apply(DataEvent event, int position, int[] identification) {
                if(identification[2]==57622){
                    EvioNode node = new EvioNode(2*1024);
                    event.getAt(node, position);
                    int crate = identification[0];
                    DataDecoder.decode_57622(node,crate,tdcData);                    
                }
                
                if(identification[2]==57638){
                    //System.out.println("--------- decoding event ADC -----");
                    EvioNode node = new EvioNode(2*1024);
                    event.getAt(node, position);
                    int crate = identification[0];
                    DataDecoder.decode_57638(node,crate,adcData, ecalTable);
                }
            }   
        };
        
        event.setCallback(callback);
        event.scan();

        int    rows = tdcData.getRows();
        //System.out.println("rows found = " + rows);
        //System.out.printf("rows = %d\n",rows);
        int[]  haddr = new int[]{0,0,0,0};
        int[]  saddr = new int[4];
        
        //long value = DataTranslator.getHash(15,4,5,6);
        
        for(int i = 0; i < rows; i++){
            haddr[0] = tdcData.getInt(i, 1);
            haddr[1] = tdcData.getInt(i, 2);
            haddr[2] = tdcData.getInt(i, 3);
            long hash = DataTranslator.getHash(haddr);
            if(tdcTable.getMap().containsKey(hash)==true){
                long value = tdcTable.getMap().get(hash);
                DataTranslator.decodeHash(value, saddr);
                tdcData.putByte(  i, 1, (byte) saddr[0]);
                tdcData.putByte(  i, 2, (byte) saddr[1]);
                tdcData.putShort( i, 3, (short) saddr[2]);
                tdcData.putByte(  i, 4, (byte) saddr[3]);
            } else {
                System.out.printf(" error : failed to find the key %016X\n",hash);
            }
        }
        //DataStructureUtils.print(tdcData);//, new int[]{0,2,4,5});
        //System.out.println(tdcData);
        //tdcData.show();        
    }
    
    public static void benchmark(String filename, int nframe, int nthreads){
        
        //String filename = "/Users/gavalian/Work/DataSpace/evio/clas_003852.evio.981";
        DataFrame<EvioFile,EvioEvent> frame = new DataFrame();
        
        for(int i = 0; i < nframe; i++) frame.addEvent(new EvioEvent(128));
        
        EvioFile  reader = new EvioFile();
        EvioEvent  event = new EvioEvent();
        
        reader.open(filename);
        
        DataDecoderEvio consumer = new DataDecoderEvio();
        
        long then = System.currentTimeMillis();
        boolean isEmpty = false;
        
        ForkJoinPool myPool = new ForkJoinPool(nthreads);        
        int counter = 0;
        long processingTime = 0L;
        
        while(isEmpty==false){            
            int count = reader.nextFrame(frame);
            counter += count;
            if(count!=frame.getCount()) isEmpty=true;
            //System.out.println("consuming at : " + counter);
            long pthen = System.nanoTime();
            Stream<EvioEvent>  stream = frame.getParallelStream();
            
            try {                
                myPool.submit(() -> stream.forEach(consumer)).get();
            } catch (InterruptedException ex) {
                Logger.getLogger(DataDecoderEvio.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(DataDecoderEvio.class.getName()).log(Level.SEVERE, null, ex);
            }
            long pnow = System.nanoTime();
            processingTime += pnow-pthen;
        }
        
        long   now = System.currentTimeMillis();
        
        double time = (now - then)/1000.0;
        double eventRate = counter/time;
        
        double timeConsumer = ((double) processingTime)/1000.0/1000000.0;
        double eventRateConsumer = counter/timeConsumer;
        double timePerEvent = (1.0*(now-then))/counter;
        System.out.printf("%4d %4d events %9d, time  %8.2f sec  %12.2f evt/sec" 
                + " , consumter time = %8.2f sec %12.2f evt/sec\n",nframe,nthreads,
                counter,time, eventRate, timeConsumer, eventRateConsumer);
    }
    
    
    public static void debug(String filename, int count){
        EvioFile  reader = new EvioFile();
        EvioEvent  event = new EvioEvent();
        
        reader.open(filename);
        DataDecoderEvio decoder = new DataDecoderEvio();
        
        int counter = 0;
        while(reader.hasNext()&&counter<count){
            counter++;
            reader.next(event);

            decoder.processDataEvent(event);
        }
    }
    
    public static void main(String[] args){                        
        //String filename = "/Users/gavalian/Work/DataSpace/evio/clas_003852.evio.981";
        String filename = "/Users/gavalian/Work/DataSpace/evio/clas_011878.evio.00001";
       if(args.length>0) filename = args[0];
       
       //DataDecoderEvio.benchmark(filename, 200, 1);
       
       //DataDecoderEvio.debug(filename, 140);
       
        for(int i = 1; i <= 16 ; i++){
            DataDecoderEvio.benchmark(filename, 200, i*2);
        }
        
        /*EvioFile  reader = new EvioFile();
        EvioEvent  event = new EvioEvent();
        DataDecoderEvio decoder = new DataDecoderEvio();
        
        reader.open(filename);
        //for(int i =0; i < 100; i++){
        int counter = 0;
        long readTime = 0L;
        long processTime = 0L;
        long then = System.currentTimeMillis();
        while(reader.hasNext()==true){
        //for(int i = 0; i < 100; i++){
            long rt = System.nanoTime();
            reader.next(event);
            readTime += System.nanoTime() - rt;
            long pt = System.nanoTime();
            decoder.processDataEvent(event);
            processTime += System.nanoTime() - pt;
            counter++;
        }
        long now = System.currentTimeMillis();
        System.out.printf("processed %d events, time = %d ms, read = %d ns, process = %d ns",
                counter, now- then, readTime/1000/1000,processTime/1000/1000);
        */
    }

    
    
}
