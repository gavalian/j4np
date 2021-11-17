/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.data;

import j4np.hipo5.base.Compressor;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;



/**
 *
 * @author gavalian
 */
public class DataFrameBuilder {
    
    private static final int ONE_MEG = 1024*1024;

    /** Maximum number of events per record. */
    private int MAX_EVENT_COUNT = ONE_MEG;

    /** Size of some internal buffers in bytes. */
    private int MAX_BUFFER_SIZE = 8*ONE_MEG;
    
    /** This buffer stores event lengths ONLY. */
    private ByteBuffer recordIndex;
    
    /** This buffer stores event data ONLY. */
    private ByteBuffer recordEvents;
    
    /** This buffer stores data that will be compressed. */
    //private ByteBuffer recordData;
    
    
    /** Number of valid bytes in recordIndex buffer */
    private int indexSize;
    
    /** Number of valid bytes in recordEvents buffer. */
    private int eventSize;
    
    private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
    
    
    private Compressor dataCompressor;
    
    /**
     * Constructor with specifying the maximum number of events and
     * maximum size of the buffer to use for events.
     * @param __maxEvents maximum number events
     * @param __maxSize maximum size of the event buffer
     */
    public DataFrameBuilder(int __maxEvents, int __maxSize){
        dataCompressor = Compressor.getInstance();
        MAX_EVENT_COUNT = __maxEvents;
        MAX_BUFFER_SIZE = __maxSize;
        allocate();
        reset();
    }
    /**
     * Constructor with specifying number of events, maximum size and the
     * byte order to be used for the buffer.
     * @param __maxEvents
     * @param __maxSize
     * @param __order 
     */    
    public DataFrameBuilder(int __maxEvents, int __maxSize, ByteOrder __order){
        dataCompressor = Compressor.getInstance();
        MAX_EVENT_COUNT = __maxEvents;
        MAX_BUFFER_SIZE = __maxSize;
        byteOrder       = __order;
        allocate();
        reset();
    }
    /**
     * Default constructor that will use default number of max events
     * (1000000) and maximum buffer size of 8 MB. Default byte order is
     * LITTLE_ENDIAN.
     */
    public DataFrameBuilder(){
        dataCompressor = Compressor.getInstance();
        allocate();
        reset();
    }        
    /**
     * Allocates buffers for keeping index array and also the events
     * added.
     */
    private void allocate(){

        recordIndex = ByteBuffer.wrap(new byte[MAX_EVENT_COUNT*4]);
        recordIndex.order(byteOrder);

        recordEvents = ByteBuffer.wrap(new byte[MAX_BUFFER_SIZE]);
        recordEvents.order(byteOrder);
    }
    /**
     * Adding an event into the builder array.
     * @param event bytes representing the event
     * @param offset offset inside the array
     * @param length length of the bytes to copy
     * @return true if there is space in the buffer to add the event, 
     *         false if event is not added
     */
    public boolean addEvent(byte[] event, int offset, int length){
        
        if(indexSize>=MAX_EVENT_COUNT) return false;        
        if((eventSize+length)>MAX_BUFFER_SIZE) return false;
        
        if(indexSize == 0){
            recordIndex.putInt(indexSize*4, length);
            indexSize++;
        } else {
            int size = recordIndex.getInt((indexSize-1)*4);
            recordIndex.putInt(indexSize*4, size+length);
            indexSize++;
        }
        
        System.arraycopy(event, offset, recordEvents.array(), eventSize, length);
        eventSize += length;
        return true;
    }
    /**
     * Prints out information about the builder.
     */
    public void show(){
        System.out.println(
                String.format(" EVENT SIZE = %8d, INDEX SIZE = %8d",
                        eventSize,indexSize));
        for(int i = 0; i < indexSize; i++){
            System.out.print(
                    String.format("%6d ", recordIndex.getInt(i*4)));
        }
        System.out.println();
    }
    
    public final void reset(){
        indexSize = 0;
        eventSize = 0;
    }    
 
    /**
     * creates a new DataFrame object and fills with information 
     * from builder. The size and count of objects are already filled
     * in DataFrame object.The resulting DataFrame is uncompressed.
     * @return 
     */
    public DataFrame build(){
        int      offset = DataFrame.HEADER_SIZE;
        int  bufferSize = indexSize*4 + eventSize;
        DataFrame frame = new DataFrame(bufferSize+offset);
        System.arraycopy(recordIndex.array(), 0, 
                frame.getFrameBuffer().array(), offset, indexSize*4);
        System.arraycopy(recordEvents.array(), 0, frame.getFrameBuffer().array(), 
                offset+indexSize*4, eventSize);
        frame.getFrameBuffer().putInt(frame.dataFrameSizePointer, bufferSize);
        frame.getFrameBuffer().putInt(frame.dataFrameSizeCompressedPointer, bufferSize);
        frame.getFrameBuffer().putInt(frame.dataFrameCountPointer, indexSize);
        return frame;
    }
    
    public DataFrame build(DataFrameBuffer dfb, int compressionType){
        
        int  bufferSize = indexSize*4 + eventSize;        
        dfb.require(bufferSize*2);
        
        System.arraycopy(recordIndex.array(), 0, dfb.getBuffer().array(), 
                0, indexSize*4);
        System.arraycopy(recordEvents.array(), 0, dfb.getBuffer().array(), 
                indexSize*4, eventSize);
        
        int compressedSize = 0;
        
        try {
          switch(compressionType){
              case 1:
                  compressedSize = dataCompressor.compressLZ4(
                                dfb.getBuffer().array(), 0, bufferSize,
                                dfb.getBuffer().array(), bufferSize,
                                bufferSize+200);
                  break;
              case 2:
                  compressedSize = dataCompressor.compressLZ4Best(
                                dfb.getBuffer().array(), 0, bufferSize,
                                dfb.getBuffer().array(), bufferSize,
                                bufferSize+200);
                  break;
              default:
                  break;
          }  
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" --- something went wrong ---");
        }
        
        System.out.println(
                String.format(" DATA SIZE = %8d, COMPRESSED SIZE = %8d",
                        bufferSize,compressedSize));
        int offset = DataFrame.HEADER_SIZE;
        
        DataFrame frame = new DataFrame(offset+compressedSize);
        
        return frame;
    }
    
    public static class DataFrameBuffer {
        private ByteBuffer buffer;
        
        public DataFrameBuffer(){
            
        }
        
        public DataFrameBuffer(int size){
            resize(size);
        }
        
        public final void resize(int size){
            byte[] bytes = new byte[size];
            buffer = ByteBuffer.wrap(bytes);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        
        public void require(int size){
            if(buffer.capacity()<size){
                resize(size+128);
            }
        }
        
        public ByteBuffer getBuffer(){ return buffer;}
    }    
    
    public static void main(String[] args){
        
        
        DataFrameBuilder builder = new DataFrameBuilder(30,300*1024);
        int pos = 0;
        for(int i = 0; i < 30; i++){
            int size = (int) (200+Math.random()*500);
            System.out.println(i + " : size = " + size + 
                    " position = " + pos );
            byte[] array = new byte[size];
            for(int k = 0; k < size; k++){
                array[k] = (byte) (i+1);
            }            
            builder.addEvent(array,0, size);
            pos += size;
            //builder.show();

        }
        
        //builder.reset();
        //builder.show();
        
        
        DataFrame  frame = builder.build();
        
        DataFrameBuffer data = new DataFrameBuffer(600*1024);
        
        DataFrame  cframe = builder.build(data, 1);
        cframe.show();
        
        frame.show();

       for(int i = 0; i < 30; i++){
            int position = frame.getEventPosition(i);
            int length   = frame.getEventLength(i);
            System.out.println(String.format("%4d : %8d %8d %8d", 
                    i,position,length, position - 136));
        }
        
        
        //int iterations = 15000000;
        int iterations = 1500000;
        
        //MemoryMonitor monitor = new MemoryMonitor(200);
        //monitor.init();
        
        for(int w = 0; w < 10; w++){
            System.out.println(" no copy : warm up " + w);
            for(int k = 0; k < iterations; k++){
                for(int i = 0; i < 20;i++){
                    ByteBuffer  buffer = frame.getEvent(i);
                }
            }
        }
        
        
        long start_time = System.currentTimeMillis();
        
        for(int k = 0; k < iterations; k++){
            for(int i = 0; i < 20;i++){
                ByteBuffer  buffer = frame.getEvent(i);
            }
        }
        long end_time = System.currentTimeMillis();
        long duration = end_time - start_time;
        
        System.out.println(" exacution time no copy = " + duration + " msec");
        
        ByteBuffer persistent = ByteBuffer.wrap(new byte[800]);
        
        for(int w = 0; w < 10; w++){
            System.out.println(" no copy : warm up " + w);
            for(int k = 0; k < iterations; k++){
                for(int i = 0; i < 20;i++){
                    frame.getEvent(persistent,i);
                    
                }
            }
        }
        
        start_time = System.currentTimeMillis();
        
        for(int k = 0; k < iterations; k++){
            for(int i = 0; i < 20;i++){
                frame.getEvent(persistent,i);
            }
        }
        end_time = System.currentTimeMillis();
        duration = end_time - start_time;
        System.out.println(" exacution time    copy = " + duration + " msec");
        
        
        for(int w = 0; w < 10; w++){
            System.out.println(" no copy : warm up " + w);
            for(int k = 0; k < iterations; k++){
                for(int i = 0; i < 20;i++){
                    byte[] array = frame.getEventCopy(i);
                    
                }
            }
        }
        
        start_time = System.currentTimeMillis();
        
        for(int k = 0; k < iterations; k++){
            for(int i = 0; i < 20;i++){
                byte[] array = frame.getEventCopy(i);
                /*for(int a = 0; a < array.length; a++){
                    System.out.print(
                            String.format("%3d ", array[a]));
                }*/
                //System.out.println();
            }
        }
        end_time = System.currentTimeMillis();
        duration = end_time - start_time;
        System.out.println(" exacution time    new  = " + duration + " msec");
        
        //monitor.timerStop();
        //DataUtils.showString("HPDF");
        //System.out.println(monitor);
    }
}
