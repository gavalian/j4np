/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.base;

import j4np.hipo5.data.Event;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class RecordFrameBuilder {
    
    private ByteBuffer  recordBuffer;
    private final int   ONE_MB = 1024*1024;
    
    
    private int   MAX_DATA_SIZE = 2*1024*1024;
    private int      MAX_EVENTS = 10;
    private int recordIntegrity = -1;
    
    public RecordFrameBuilder(){
        byte[] data = new byte[MAX_DATA_SIZE];
        recordBuffer = ByteBuffer.wrap(data);
        recordBuffer.order(ByteOrder.LITTLE_ENDIAN);
        reset();
    }
    
    
    public ByteBuffer recordBuffer(){ return recordBuffer;}
    public int        recordLength(){ return recordBuffer.getInt(0);}
    
    public RecordFrameBuilder(int maxSize, int maxEvents){
        MAX_DATA_SIZE = maxSize;
        MAX_EVENTS    = maxEvents;
        byte[] data = new byte[MAX_DATA_SIZE];
        recordBuffer = ByteBuffer.wrap(data);
        recordBuffer.order(ByteOrder.LITTLE_ENDIAN);
        reset();
    }
    
    public boolean addEvent(byte[] buffer, int position, int length){
        int space = MAX_DATA_SIZE-recordBuffer.getInt( 0);
        int count = recordBuffer.getInt( 12);
        if(space<length) return false;
        if(count>=MAX_EVENTS) return false;

        int  size = recordBuffer.getInt( 0);
        int dataSize = recordBuffer.getInt( 32);
        System.arraycopy(buffer, position, this.recordBuffer.array(), size, length);
        recordBuffer.putInt( 12,  count+1);
        recordBuffer.putInt( 0, size + length);
        recordBuffer.putInt( 32, dataSize + length);
        recordBuffer.putInt( 36, dataSize + length);
        return true;
    }
    
    public final boolean reset(){
         recordBuffer.putInt( 0,  56);//*(reinterpret_cast<int *>(&dataBuffer[0])) recordBuffer.putInt(0,56); // record length 1                                                                                 
         recordBuffer.putInt( 4,   1);//*(reinterpret_cast<int *>(&dataBuffer[4]))  = 1; // record number 2                                                                                  
         recordBuffer.putInt( 8,  14);//*(reinterpret_cast<int *>(&dataBuffer[8]))  = 14; // header length words 3                                                                           
         recordBuffer.putInt( 12,  0);//*(reinterpret_cast<int *>(&dataBuffer[12])) = 0; // event count 4                                                                                    
         recordBuffer.putInt( 16,  0);//*(reinterpret_cast<int *>(&dataBuffer[16])) = 0; // index array length 5                                                                             
         recordBuffer.putInt( 20,  5);//*(reinterpret_cast<int *>(&dataBuffer[20])) = 5; // verion + bitinfo 6                                                                               
         recordBuffer.putInt( 24,  0);//*(reinterpret_cast<int *>(&dataBuffer[24])) = 0; // user header length                                                                               
         recordBuffer.putInt( 28,  0xc0da0100);//*(reinterpret_cast<int *>(&dataBuffer[28])) = 0xc0da0100;
         recordBuffer.putInt( 32,  0);//*(reinterpret_cast<int *>(&dataBuffer[32])) = 0; // uncompressed data length                                                                         
         recordBuffer.putInt( 36,  0);//*(reinterpret_cast<int *>(&dataBuffer[36])) = 0; // compressed data length 
        return true;
    }
    
    public void show(){
        System.out.printf("[RECORD FRAME BUILDER] MAX SIZE = %d, MAX EVENTS = %d\n",
                this.MAX_DATA_SIZE, this.MAX_EVENTS);
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
    
    
    public static void main(String[] args){
        RecordFrameBuilder frame = new RecordFrameBuilder(1024,100);
        frame.show();
        
        for(int i = 0; i < 25000; i++){
            Event e = new Event();
            frame.addEvent(e.getEventBuffer().array(), 0, e.getEventBufferSize());
        }
        
        frame.show();
       
        RecordFrame r = new RecordFrame();
        r.show();
        
        r.read(frame.recordBuffer().array(), 0, frame.recordLength());
        
        r.show();
        List<Event> evList = new ArrayList<>();
        
        for(int k = 0; k < 25; k++) evList.add(new Event());
        r.getEvents(evList);
        System.out.println(" event list size = " + evList.size());
    }
}
