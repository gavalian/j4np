/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class Structure {
    
    protected ByteBuffer buffer = null;
    
    public Structure(){ }
    
    public Structure(int group, int item, int type, int size){
        allocate(size+8);
        set(group,item,type,0);
    }
    
    protected final void allocate(int size){
        byte[] bytes = new byte[size];
        buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    
    public final void set(int group, int item, int type, int size){
        buffer.putShort( 0,  (short) group); // byte 0 and 1 (16 bits) are group ID
        buffer.put(      2,   (byte)  item); // byte 2 ( 8 bits) is item id
        buffer.put(      3,   (byte)  type); // byte 3 describes the type 
        buffer.putInt(   4,  size&0x00FFFFFF);
    }
    
    public int  group(){ return buffer.getShort(0);}
    public int   item(){ return buffer.get(2);}
    public int   type(){ return buffer.get(3);}
    public int   length(){ return buffer.getInt(4)&0x00FFFFFF;}
    public int capacity(){ return buffer.capacity();}
    
    public void copy(ByteBuffer b, int offset, int size){
        System.arraycopy(b.array(), offset, buffer.array(), 0, size);
    }
    
    public void show(){
        System.out.printf("      structure | ids [%6d/%4d], type = %3d, length = %5d, capacity = %9d\n",
                group(),item(),type(),length(),capacity());
        List<String> list = this.scanContent();
        for(String s : list) System.out.println("\t\t\u21b3 " + s);
    }
    
    public List<String> scanContent(){
        List<String> list = new ArrayList<>();
        int maxPosition = (buffer.getInt(4)&0x00FFFFFF)+8;
        int    position = 8;
        //System.out.println(" max postiion = " + maxPosition + " " + position);
        while(position<maxPosition){
            int  group = buffer.getShort(position);
            int   item = buffer.get(position+2);
            int   type = buffer.get(position+3);
            int format = (buffer.getInt(position+4)>>24)&0x000000FF;
            int   size = buffer.getInt(position+4)&0x00FFFFFF;
            list.add(String.format("ids [%6d/%4d], type = %3d, format =  %4d,   length = %9d",
                    group,item,type,format,size));
            position += size + 8;
            //System.out.println(" position = " + position + "  max position = " + maxPosition);
        }
        return list;
    }
    
    public void write(ByteBuffer b, int size){
        int length = length();
        int offset = 8 + length;
        System.arraycopy(b.array(), 0, buffer.array(), offset, size);
        buffer.putInt(4, length+size);
    }
    
    public void write(ByteBuffer b, int position, int size){
        int length = length();
        int offset = 8 + length;
        System.arraycopy(b.array(), position, buffer.array(), offset, size);
        buffer.putInt(4, length+size);
    }
    
    public static int find(int group, int item, ByteBuffer b, int offset, int maxscan){
        int position = offset;
        int maxposition = offset + maxscan;
        while(position<maxposition){
            int g = b.getShort(position);
            int i = b.get(position+2);
            if(g==group&&i==item) return position;
            position += 8 + b.getInt(position+4)&0x00FFFFFF;
        }        
        return -1;
    }
    
    public static void main(String[] args){
        
        Structure s = new Structure(1,12,12,512);        
        s.show();
        
        Node n = new Node(12,14,new int[]{5,6,7,8,9});
        Node f = new Node(12,24,new float[]{1.1f,1.2f,1.3f,1.4f,1.5f,1.6f,1.7f});
        
        System.out.println( " n = " + n.getBufferSize());
        System.out.println( " f = " + f.getBufferSize());
        Leaf leaf = new Leaf(12,34,"iiff",128);
        leaf.setRows(5);
        //s.copy(n.nodeBuffer, 0, n.getBufferSize());
        s.write(n.nodeBuffer, n.getBufferSize());        
        s.write(f.nodeBuffer, f.getBufferSize());
        s.write(leaf.structBuffer, leaf.getLength()+8);
        System.out.println(" leaf size = " + leaf.getLength());
        
        s.show();
        
        System.out.println(" size = " + s.length());
/*        
        System.out.println(" position = " + Structure.find(12, 24, s.buffer, 8, 136));
        System.out.println(" position = " + Structure.find(12, 22, s.buffer, 8, 136));
        Event e = new Event();
        e.write(n);e.write(f);
        e.scanShow();
        s.show();
        
        e.move(s, 12,14);
        //System.out.println(" ----- : " + e.scan(12, 14));
        e.move(s, 12,24);
        e.write(s);
        s.show();
        //e.write(s);
        
        e.scanShow();
        
        int position = e.scan(1, 12);
        System.out.println(" reading the structure fomr an event position = \n" + position);
        Structure struct = e.readAt(position);        
        struct.show();*/
    }
}
