/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.base;

import j4np.hipo5.data.Event;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * This class handles HIPO records without an index array. Designed primarily 
 * to be used with ET ring, where fast composition of events.
 * Random access to the events is not optimal here, suggested usage is 
 * reading entire record into List of events.
 * 
 * @author gavalian
 */
public class RecordFrame {
    
    private ByteBuffer  recordBuffer;
    private final int   ONE_MB = 1024*1024;
    private final int   DEFAULT_RECORD_SIZE = 2*1024*1024;
    private int         recordIntegrity = -1;
    
    
    public RecordFrame(){
        byte[] data = new byte[DEFAULT_RECORD_SIZE];
        recordBuffer = ByteBuffer.wrap(data);
        recordBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    
    public RecordFrame(int size){
        byte[] data = new byte[size];
        recordBuffer = ByteBuffer.wrap(data);
        recordBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    
    protected boolean require(int size){
        if(size<recordBuffer.capacity()) return false;
        byte[] data = new byte[size];
        recordBuffer = ByteBuffer.wrap(data);
        recordBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return true;
    }
    
    public void read(byte[] buffer, int position, int length){
        this.require(length);
        System.arraycopy(buffer, position, recordBuffer.array(), 0, length);
        boolean status = checkIntegrity();
        if(status==true) recordIntegrity = 0; else recordIntegrity = -1;
    }
    
    protected boolean checkIntegrity(){
        if(recordBuffer.getInt(8)!=14) return false;
        if(recordBuffer.getInt(28)!=0xc0da0100) return false;
        if(recordBuffer.getInt(16)!=0) return false;        
        // magic word check
        return true;
    }
    
    public int getEventCount(){
        return recordBuffer.getInt(12);
    }
    
    public int getDataLength(){
        return recordBuffer.getInt(32);
    }
    
    public void getEvents(List<Event> evList){
        int size = evList.size();
        int position = recordBuffer.getInt(8)*4;
        int recordSize = recordBuffer.getInt(0);
        boolean goOn = true;
        int     counter = 0;
        while(goOn==true){
            int length = recordBuffer.getInt(position+4);
            System.out.printf(" event length [%5d] = %d, list size = %d\n", counter, length, evList.size());
            if((counter+1)>evList.size()){
                Event e = new Event(length+64);
                e.initFrom(recordBuffer.array(), position, length);
                evList.add(e);
            } else {
                evList.get(counter).initFrom(recordBuffer.array(), position, length);
            }
            
            counter++;
            if(position+length>=recordSize){
                goOn = false;
            } else position += length;
        }
    }
    
    public void show(){
        System.out.printf("[RECORD FRAME BUILDER] CAPACITY = %d, LENGTH = %d, INTEGRITY = %d\n",
                recordBuffer.capacity(),this.getDataLength(), this.recordIntegrity);
        System.out.printf("          record Length : %d\n",recordBuffer.getInt(0));
        System.out.printf("          record number : %d\n",recordBuffer.getInt(4));
        System.out.printf("          header Length : %d\n",recordBuffer.getInt(8));
        System.out.printf("            event count : %d\n",recordBuffer.getInt(12));
        System.out.printf("     index array Length : %d\n",recordBuffer.getInt(16));
        System.out.printf("                version : %d\n",recordBuffer.getInt(20));
        System.out.printf("     user header length : %d\n",recordBuffer.getInt(24));
        System.out.printf("             magic word : %X\n",recordBuffer.getInt(28));
        System.out.printf("            data Length : %d\n",recordBuffer.getInt(32));
        System.out.printf(" compressed data Length : %d\n",recordBuffer.getInt(36));
    }
}
