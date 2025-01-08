/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.examples;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.ByteUtils;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author gavalian
 */
public class Compression {
    
    public byte[] short2byte(short[] array){
        byte[] result = new byte[array.length*2];
        ByteBuffer bb = ByteBuffer.wrap(result);
        for(int k = 0; k < array.length; k++){
            bb.putShort(k*2, array[k]);
        }
        //System.arraycopy(array, 0, result, 0, result.length);
        return result;
    }
    
    public byte[] int2byte(int[] array){
        byte[] result = new byte[array.length*4];
        ByteBuffer bb = ByteBuffer.wrap(result);
        for(int k = 0; k < array.length; k++){
            bb.putInt(k*4, array[k]);
        }
        //System.arraycopy(array, 0, result, 0, result.length);
        return result;
    }
    
    public short[] byte2short(byte[] array){
        short[] result = new short[array.length/2];
        ByteBuffer bb = ByteBuffer.wrap(array);
        //System.arraycopy(array, 0, result, 0, result.length*4);
        for(int k = 0; k < result.length; k++){
            result[k] = bb.getShort(k*2);
        }
        return result;
    }
    
    public int[] byte2int(byte[] array){
        int[] result = new int[array.length/4];
        ByteBuffer bb = ByteBuffer.wrap(array);
        //System.arraycopy(array, 0, result, 0, result.length*4);
        for(int k = 0; k < result.length; k++){
            result[k] = bb.getInt(k*4);
        }
        return result;
    }
    
    public byte[] swap2(byte[] array){
        byte[] result = new byte[array.length];
        int size = array.length/2;
        for(int i = 0; i < size; i++){
            result[        i] = array[i*2];
            result[   size+i] = array[i*2+1];
        }
        return result;
    }
    
    public byte[] swap4(byte[] array){
        byte[] result = new byte[array.length];
        int size = array.length/4;
        for(int i = 0; i < size; i++){
            result[        i] = array[i*4];
            result[   size+i] = array[i*4+1];
            result[ 2*size+i] = array[i*4+2];
            result[ 3*size+i] = array[i*4+3];
        }
        return result;
    }
    
    public int[] convert(int[] array){
        byte[] arrayByte = int2byte(array);
        byte[] arraySwapped = swap4(arrayByte);
        int[]  result = byte2int(arraySwapped);
        return result;
    }
    
    public short[] convert2(short[] array){
        byte[] arrayByte = short2byte(array);
        byte[] arraySwapped = swap2(arrayByte);
        short[]  result = byte2short(arraySwapped);
        return result;
    }
    public static List<Node> fromTDC(Bank b){
        List<Node> nodes = new ArrayList<>();
        int group = b.getSchema().getGroup();
        nodes.add(new Node(group,1,b.getByte("sector")));
        nodes.add(new Node(group,2,b.getByte("layer")));
        nodes.add(new Node(group,3,b.getShort("component")));
        nodes.add(new Node(group,4,b.getInt("TDC")));
        return nodes;
    }
    
    public static List<Node> fromADC(Bank b){
        List<Node> nodes = new ArrayList<>();
        int group = b.getSchema().getGroup();
        nodes.add(new Node(group,1,b.getByte("sector")));
        nodes.add(new Node(group,2,b.getByte("layer")));
        nodes.add(new Node(group,3,b.getShort("component")));
        nodes.add(new Node(group,4,b.getInt("ADC")));
        nodes.add(new Node(group,5,b.getInt("integral")));            
        return nodes;
    }
    public static void uncompress(String file){
        HipoReader r = new HipoReader(file);
        HipoWriter w = new HipoWriter();
        w.setCompressionType(0);
        w.open("uncompressed.h5");
        
        Event e = new Event();
        while(r.hasNext()){
            r.next(e);
            w.addEvent(e);
        }
        
        w.close();
    }
    public static void writeFiles(String file){
        HipoReader r = new HipoReader(file);
        Bank[]     b = r.getBanks("DC::tdc","BMT::adc");
        
        HipoWriter[] w = new HipoWriter[2];
        w[0] = new HipoWriter();
        w[0].setCompressionType(0);
        w[0].open("dataoriginal.h5");

        Compression comp = new Compression();
        
        //w[1] = HipoWriter.create("datapacked.h5", r);
        
        w[1] = new HipoWriter();
        w[1].setCompressionType(0);
        w[1].open("datapacked.h5");
        
        Event event = new Event();
        
        while(r.nextEvent(b)){
            List<Node> nadc = Compression.fromADC(b[1]);
            List<Node> ntdc = Compression.fromTDC(b[0]);
            
            event.reset();
            for(int i = 0; i < nadc.size(); i++) event.write(nadc.get(i));
            for(int i = 0; i < ntdc.size(); i++) event.write(ntdc.get(i));
            w[0].addEvent(event);
            
            event.reset();
            
            for(int i = 0; i < 2; i++) event.write(nadc.get(i));

            short[] ss1 = nadc.get(2).getShort();
            short[] ss1_c = comp.convert2(ss1);
            
            int[] ii1 = nadc.get(3).getInt();
            int[] ii1_c = comp.convert(ii1);
            
            int[] ii2 = nadc.get(4).getInt();
            int[] ii2_c = comp.convert(ii2);
            
            event.write(new Node(b[1].getSchema().getGroup(),3,ss1_c));
            event.write(new Node(b[1].getSchema().getGroup(),4,ii1_c));
            event.write(new Node(b[1].getSchema().getGroup(),5,ii2_c));
            
            
            for(int i = 0; i < 2; i++) event.write(ntdc.get(i));
            
            short[] ss1_2  = ntdc.get(2).getShort();
            short[] ss1_2_c = comp.convert2(ss1_2);
                                    
            int[] ii1_2 = ntdc.get(3).getInt();
            int[] ii1_2_c = comp.convert(ii1_2);
            event.write(new Node(b[0].getSchema().getGroup(),3,ss1_2_c));
            event.write(new Node(b[0].getSchema().getGroup(),4,ii1_2_c));
            
            w[1].addEvent(event);
            //System.out.println("--- event ");
            //System.out.println(Arrays.toString(cc1));
            //System.out.println(Arrays.toString(cc1_c));
        }
        w[0].close();
        w[1].close();
    }
    public static void compressFloats(){
        Random r = new Random();
        float[] buffer = new float[1024*80];
        
        HipoWriter  w = new HipoWriter();
        HipoWriter ws = new HipoWriter();
        w.setCompressionType(0); ws.setCompressionType(0);
        w.open("data.h5"); ws.open("data_swaped.h5");
        
        Event e = new Event();
        for(int j = 0; j < 200; j++){
            for(int i = 0; i < buffer.length; i++) buffer[i] = r.nextFloat();

            Node n = new Node(12,1, buffer);
            
            e.reset(); e.write(n);
            w.addEvent(e);
            
            //System.out.println(" ORIGINAL ");
            //ByteUtils.printByteArray(n.getBufferData(), 0, 64);
            
            ByteBuffer b = ByteBuffer.wrap(n.getBufferData());            
            b.order(ByteOrder.LITTLE_ENDIAN);
            for(int k = 8; k < b.array().length-4; k+=4){
                int cu = b.getInt(k);
                int nx = b.getInt(k+4);
                b.putInt(k, cu^nx);
            }
            //System.out.println(" PACKED ");
            //ByteUtils.printByteArray(n.getBufferData(), 0, 64);
            ByteBuffer d = ByteUtils.getBytePack(b, 8, 4);
            System.arraycopy(d.array(), 0, n.getBufferData(), 0, n.getBufferData().length);
            e.reset();e.write(n);
            ws.addEvent(e);
            //System.out.println(" ORIGINAL ");
            //ByteUtils.printByteBuffer(b, 0, 32);
            //System.out.println(" PACKED ");
            //ByteUtils.printByteBuffer(d, 0, 32);
            //n.getBufferData();
            //System.out.println(" PACKED ");
            //ByteUtils.printByteArray(n.getBufferData(), 0, 64);
        }
        w.close();
        ws.close();
    }
    
    public static void main(String[] args){
        
        //String file = "/Users/gavalian/Work/DataSpace/decoded/clas_006595.evio.00625-00629.hipo";
        
        //Compression.writeFiles(file);
        //Compression.uncompress(file);
        
        Compression.compressFloats();
        
        /*
        Compression c = new Compression();
        
        int[]    a = new int[]{5,6};
        short[] a2 = new short[]{4,5,6}; 
        
        int[]    b = c.convert(a);
        short[] b2 = c.convert2(a2);
        
        System.out.println("A : = " + Arrays.toString(a));        
        System.out.println("B : = " + Arrays.toString(b));
        System.out.println("B (int) : " + Arrays.toString(c.int2byte(a)));
        System.out.println("B (int) : " + Arrays.toString(c.int2byte(b)));
        
        System.out.println("-----");
        
        System.out.println("A2: = " + Arrays.toString(a2));
        System.out.println("B2: = " + Arrays.toString(b2));
        System.out.println("A2 (short) : " + Arrays.toString(c.short2byte(a2)));
        System.out.println("B2 (short) : " + Arrays.toString(c.short2byte(b2)));
        */
    }
}
