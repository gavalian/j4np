/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.evio;

import j4np.data.base.DataEvent;
import j4np.data.base.DataNodeCallback;
import j4np.data.base.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class EvioFile implements DataSource {
    
    protected RandomAccessFile    inStreamRandom = null;
    protected long             nextEventPosition = 0;
    protected int                nextEventLength = 0;
    protected int                   currentEvent = 0;
    protected ByteOrder          streamByteOrder = ByteOrder.LITTLE_ENDIAN;
    protected EvioBlock                dataBlock = new EvioBlock(2*1024*1024);    
    
    public EvioFile(){
        
    }
    
    public EvioFile(String filename){
        open(filename);
    }
    
    public final void open(String filename){
        try {
            System.out.println("[READER] ----> opening current file : " + filename);
            inStreamRandom = new RandomAccessFile(filename,"r");
            System.out.println("[READER] ---> open successful, size : " + inStreamRandom.length());
            firstBlock();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EvioFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EvioFile.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        initProperties();
    }
    
    public EvioBlock block(){ return dataBlock;}
    
    public  int decodeTag(int desc){ return (desc>>16)&0x0000FFFF;}
    public  int decodeNum(int desc){ return (desc)&0x000000FF;}
    public  int decodeType(int desc){ return (desc>>8)&0x000000AF;}
        
    public  boolean next(){
        
        byte[] buffer = new byte[nextEventLength+24];
        try {
            inStreamRandom.seek(nextEventPosition);
            int nread = inStreamRandom.read(buffer);
            if(nread!=buffer.length) return false;
            
            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
            byteBuffer.order(streamByteOrder);
            
            int length      = byteBuffer.getInt(0);
            int description = byteBuffer.getInt(4);
            int offset      = length*4 + 4;
            nextEventPosition = nextEventPosition + offset;
            System.out.printf("debug : length = %d, readin %d\n",buffer.length, offset);
            nextEventLength   = byteBuffer.getInt(offset)*4+4;

            System.out.println("EVENT BUFFER");
            String data = "";//DataArrayUtils.;//..getStringArray(byteBuffer, 10, 100);
            System.out.println(data);
            System.out.println("END EVENT BUFFER");
            System.out.printf("Length = %8d, description = (%4d,%4d) type = %4d\n",
                    length, 
                    decodeTag(description),
                    decodeNum(description),
                    decodeType(description)
                    );
            
             System.out.printf("next position = %8d, next length = %8d\n",
                    nextEventPosition,nextEventLength);
        } catch (IOException ex) {
            Logger.getLogger(EvioFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    private boolean firstBlock(){
        byte[] array = new byte[60];        
        try {            
            this.inStreamRandom.seek(0);
            this.inStreamRandom.read(array);
            
            ByteBuffer header = ByteBuffer.wrap(array);
            header.order(streamByteOrder);
            
            int length = header.getInt(0)*4+32;            
            dataBlock.allocate(length);
            this.inStreamRandom.seek(0);
            inStreamRandom.read(dataBlock.blockBuffer, 0, length+8*4);
            //dataBlock.init(array,0,2*1024*1014);
            dataBlock.setPosition(0);
            dataBlock.scan();
            dataBlock.show();
        } catch (IOException ex) {
            Logger.getLogger(EvioFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;   
    }
    

    
    public boolean nextBlock(){
        byte[] array = new byte[2*1024*1024];
        try {
            long nextPosition = dataBlock.nextPosition();
            int  nextLength   = dataBlock.nextLength();
            dataBlock.allocate(nextLength+32);
            inStreamRandom.seek(nextPosition);
            int actualRead = inStreamRandom.read(dataBlock.blockBuffer, 0, nextLength+32);
            dataBlock.setPosition(nextPosition);
            /*System.out.printf("requested read = %8d, actual = %8d\n", 
                    nextLength+32, actualRead);*/
            dataBlock.scan();
            if(nextLength+32!=actualRead) return false;
            dataBlock.show();
        } catch (IOException ex) {
            Logger.getLogger(EvioFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    protected void initProperties(){
        byte[] headerBytes = new byte[72];
        try {
            inStreamRandom.read(headerBytes);
            ByteBuffer header = ByteBuffer.wrap(headerBytes);
            header.order(ByteOrder.LITTLE_ENDIAN);
            int firstEventLength = header.getInt(32);
            System.out.println("FIRST EVENT LENGTH = " + firstEventLength);
            nextEventPosition = 32;
            nextEventLength   = firstEventLength*4 + 4;            
        } catch (IOException ex) {
            Logger.getLogger(EvioFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    

    @Override
    public boolean hasNext() {
        if(dataBlock.hasNext()==true) return true;
        return dataBlock.hasNext();
    }

    @Override
    public boolean next(DataEvent event) {
        dataBlock.moveNext();
        EvioEvent evioEvent = (EvioEvent) event;
        if(dataBlock.hasNext()==true){
            dataBlock.getEvent(evioEvent);
            return true;
        } else {
            boolean flag = this.nextBlock();
            if(flag==false){
                evioEvent.setSize(0);
                return false;
            } else {
                if(dataBlock.hasNext()==true){
                    dataBlock.getEvent(evioEvent);
                    return true;
                }
            }            
        }
        return false;
    }

    @Override
    public int position() {
        return currentEvent;
    }

    @Override
    public boolean position(int pos) {
        System.out.printf("[warning] setting position for evio files is not supported\n");
        return false;
    }

    @Override
    public int entries() {
        return -1;
    }
    
    public static void main(String[] args){
        EvioFile struct = new EvioFile();
        //struct.open("/Users/gavalian/Work/DataSpace/evio/segm1.evio");
        //struct.open("/Users/gavalian/Work/DataSpace/evio/segm5662.evio.00001");
        struct.open("/Users/gavalian/Work/DataSpace/evio/clas_003852.evio.981");        
        //struct.firstBlock();
        
        int counter = 0;
        EvioEvent event = new EvioEvent();
        
        
        event.setCallback(new DataNodeCallback(){
            int counter = 0;
            int[] tdc = new int[1000];
            @Override
            public void apply(int position, int[] identification) {
                if(identification[2]==57634){
                    EvioNode node = new EvioNode();
                    event.getAt(node, position);
                    int dataSize = node.getSize();
                    //System.out.println(" size = " + dataSize);
                    for(int i = 0; i < dataSize; i++) 
                        tdc[i] = node.getBuffer().getInt(8+i*4);
                    counter++;
                    //System.out.printf("found it : >>>>>  at %5d, crate = %6d\n",position,identification[0]);
                    //node.show();
                }
            }
            
        });
        
        long nanoTime = 0L;
        long nanoTimeTotal = 0L;
        long start = System.nanoTime();
        //while(struct.hasNext()==true&&counter<50){
        while(struct.hasNext()==true){
            struct.next(event);
            //event.info();
            //System.out.printf("--------- event # %d\n",counter+1);
            long then = System.nanoTime();
            event.scan();
            long now  = System.nanoTime();
            nanoTime += (now-then);
            counter++;            
        }
        long end = System.nanoTime();
        nanoTimeTotal = end - start;
        TimeUnit seconds = TimeUnit.SECONDS;
        
        System.out.printf("---> events in the block = %s, time = %8.5f, total time = %8.5f", 
                counter,((double)nanoTime)*10e-9,((double)nanoTimeTotal)*10e-9
        );
    }
}
