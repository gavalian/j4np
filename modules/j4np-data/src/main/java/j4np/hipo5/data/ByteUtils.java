/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.lz4.LZ4SafeDecompressor;

/**
 *
 * @author gavalian
 */
public class ByteUtils {
    
    public static final int MTU = 1024*1024;
    public static TreeMap<Integer,Integer> bitMap = ByteUtils.createBitMap();
    
    public static TreeMap<Integer,Integer>  createBitMap(){
        TreeMap<Integer,Integer> map = new TreeMap<Integer,Integer>();
        for(int loop = 0; loop < 32; loop++){
            int  integer_value = 0;
            for(int hb = 0 ; hb < loop; hb++){
                integer_value = integer_value | (1<<hb);
            }
            map.put(loop, integer_value);
        }
        return map;
    }
    
    public static void uncompressLZ4(byte[] compressed, byte[] uncompressed){
        /*
        System.out.println("LZ4 decompress = " + compressed.length + "  " 
                + uncompressed.length +  " BYTE  " + 
                String.format("%X %X %X %X", compressed[0], compressed[1], compressed[2],
                        compressed[3])
                
        );*/
        LZ4Factory factory = LZ4Factory.fastestInstance();
//        LZ4FastDecompressor decompressor = factory.fastDecompressor();
        LZ4SafeDecompressor decompressor = factory.safeDecompressor();
        int cl = decompressor.decompress(compressed, 0, compressed.length,uncompressed, 0);
    }
    
    public static byte[] compressLZ4(byte[] uncompressed){
        
        LZ4Factory factory = LZ4Factory.fastestInstance();
        LZ4Compressor compressor = factory.fastCompressor();
        
        int maxCompressedLength = compressor.maxCompressedLength(uncompressed.length);
        byte[] compressed = new byte[maxCompressedLength];
        int compressedLength = compressor.compress(uncompressed, 0, 
                uncompressed.length, compressed, 0, maxCompressedLength);
        //System.out.println(String.format("LZ4 COMPRESSOR = %X %X %X %X", compressed[0], compressed[1], compressed[2],
        //                compressed[3]));
        //System.out.println("DATA LEN  = " +  uncompressed.length + "  MAX = " + maxCompressedLength + "   CL = " + compressedLength);
        byte[]  compressedBytes = new byte[compressedLength];
        System.arraycopy(compressed, 0, compressedBytes, 0, compressedBytes.length);
        return compressedBytes;
    }

    public static byte[] compressLZ4max(byte[] uncompressed){
        
        LZ4Factory factory = LZ4Factory.fastestInstance();
        
        LZ4Compressor compressor = factory.highCompressor(9);
        
        int maxCompressedLength = compressor.maxCompressedLength(uncompressed.length);
        byte[] compressed = new byte[maxCompressedLength];
        int compressedLength = compressor.compress(uncompressed, 0, 
                uncompressed.length, compressed, 0, maxCompressedLength);
        //System.out.println(String.format("LZ4 COMPRESSOR = %X %X %X %X", compressed[0], compressed[1], compressed[2],
        //                compressed[3]));
        //System.out.println("DATA LEN  = " +  uncompressed.length + "  MAX = " + maxCompressedLength + "   CL = " + compressedLength);
        byte[]  compressedBytes = new byte[compressedLength];
        System.arraycopy(compressed, 0, compressedBytes, 0, compressedBytes.length);
        return compressedBytes;
    }
    /**
     * returns the byte array GZIP compressed.
     * @param count
     * @return 
     */
    public static byte[] gzip(byte[] ungzipped) {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();        
        try {
            final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bytes);
            /*{
                {
                    this.def.setLevel(Deflater.BEST_COMPRESSION);
                }
            };*/
            
            gzipOutputStream.write(ungzipped);
            gzipOutputStream.close();
        } catch (IOException e) {
           // LOG.error("Could not gzip " + Arrays.toString(ungzipped));
            System.out.println("[iG5DataCompressor] ERROR: Could not gzip the array....");
        }
        return bytes.toByteArray();
    }
    /**
     * The fastest Ungzip implementation. See PageInfoTest in ehcache-constructs.
     * A high performance implementation, although not as fast as gunzip3.
     * gunzips 100000 of ungzipped content in 9ms on the reference machine.
     * It does not use a fixed size buffer and is therefore suitable for arbitrary
     * length arrays.
     * 
     * @param gzipped
     * @return a plain, uncompressed byte[]
     */
    public static byte[] ungzip(final byte[] gzipped) {
        byte[] ungzipped = new byte[0];
        int    internalBufferSize  = 1*1024*1024;
        try {
            final GZIPInputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(gzipped),1024*1024);
            
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(gzipped.length);
            final byte[] buffer = new byte[ByteUtils.MTU];
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = inputStream.read(buffer, 0, ByteUtils.MTU);
                if (bytesRead != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
            }
            ungzipped = byteArrayOutputStream.toByteArray();
            inputStream.close();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            //LOG.error("Could not ungzip. Heartbeat will not be working. " + e.getMessage());
            System.out.println("[iG5DataCompressor] ERROR: could not uncompress the array. "
                    + e.getMessage());
        }
        return ungzipped;
    }

    public static byte[] generateByteArray(int count){
        byte[] array = new byte[count];
        byte data = 2;
        for(int i = 0; i < count; i++){
            array[i] = data;
            data++;
            if(data>125) data = 0;
        }
        return array;
    }
    
    public static void printBitMap(){
        for(Map.Entry<Integer,Integer> entry : bitMap.entrySet()){
            System.out.println(String.format("%4d : ", entry.getKey()) 
                    + String.format("%32s", Integer.toBinaryString(entry.getValue())).replace(' ', '0'));
        }
    }
    
    public static void printByteBuffer(ByteBuffer b, int offset, int wrap){
        ByteUtils.printByteArray(b.array(), offset, wrap, -1);
    }
    
    public static void printByteBuffer(ByteBuffer b, int offset, int wrap, int max){
        ByteUtils.printByteArray(b.array(), offset, wrap, max);
    }
    
    public static void printByteArray(byte[] b, int offset, int wrap){
        ByteUtils.printByteArray(b, offset, wrap, -1);
    }
    
    public static void printByteArray(byte[] b, int offset, int wrap, int max){
        int count = 0; 
        int position = offset;
        boolean keep = true;
        while(keep){
            count++;
            if(position>=b.length){
                keep = false;
            } else {
                System.out.printf("  %02X ", b[position]);
                if(count%wrap==0) System.out.println();
            }
            position++;
            if(max>0) if(count>max) keep = false;
        }
        System.out.println();
    }
    
    public static int getInteger(int data, int bitstart, int bitend){
        int length = bitend - bitstart + 1;
        if(ByteUtils.bitMap.containsKey(length)==true){
            int value = ((data>>bitstart)&ByteUtils.bitMap.get(length));
            return value;
        } else {
            System.out.println("[DataUtilities] : ERROR length = " + length);
        }
        return 0;
    }
    
    public static short getShortFromByte(byte data){
        short short_data = 0;
        return (short) ((short_data|data)&0x00FF);
    }
    
    public static int  getIntFromShort(short data){
        int int_data = 0;
        return (int) ( (int_data|data)&0x0000FFFF);
    }
    
    public static int  getIntFromByte(byte data){
        int int_data = 0;
        return (int) ( (int_data|data)&0x0000FFFF);
    }
    
    public static short  getShortFromInt(int data){
        short int_data = 0;
        return (short) ( (int_data|data)&0xFFFF);
    }
    
    public static byte   getByteFromInt(int data){
        byte byte_data = 0;
        return (byte) ((byte_data|data)&0xFF);
    }
    /**
     * returns a byte from short number, anything that exceeds the number that
     * fits in the byte they will be thrown away.
     * @param data short data (2 bytes)
     * @return a byte which is lower part of the given short (1 - byte)
     */
    public static byte   getByteFromShort(short data){
        byte byte_data = 0;
        return (byte) ((byte_data|data)&0xFF);
    }
    /**
     * returns byte string representation of the given integer, with 
     * 32 bits 0 and 1.
     * @param word reference integer word
     * @return 
     */
    public static String getByteString(int word){
        String strL = String.format("%32s", Integer.toBinaryString(word)).replace(' ', '0');
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < 32; i++){
            str.append(strL.charAt(i));
            if((i+1)%8==0) str.append(" ");
        }
        return str.toString();
    }
    /**
     * write a number to a specific bits of existing integer. the specified
     * bits are reset to 0 first then the provided number is written into 
     * specified bits
     * @param word reference integer word (4 bits) is not changed
     * @param number and integer number written (user should check if the number fits)
     * @param start starting bits (first bit is 0)
     * @param end ending bit (for one bit start=end)
     * @return returns a new integer with bits written
     */
    public static int write(final int word, int number, int start, int end){
        int index = end - start + 1;
        int result = (word & ~(ByteUtils.bitMap.get(index)<<(start)));
        result = result|( (number&ByteUtils.bitMap.get(index))<<(start)) ;
        return result;
    }
    /**
     * write a number to a specific bits of existing long (64-bits). the specified
     * bits are reset to 0 first then the provided number is written into 
     * specified bits
     * @param word reference integer word (4 bits) is not changed
     * @param number and integer number written (user should check if the number fits)
     * @param start starting bits (first bit is 0)
     * @param end ending bit (for one bit start=end)
     * @return returns a new integer with bits written
     */
    public static long writeLong(final long word, int number, int start, int end){
        int index   = end - start + 1;
        long result = (word & ~(ByteUtils.bitMap.get(index)<<(start)));
        result = result|( (number&ByteUtils.bitMap.get(index))<<(start)) ;
        return result;
    }
    /**
     * read specific bit from given integer.
     * @param word integer reference word
     * @param start starting bit (0 being the first one)
     * @param end   ending bit (for reading 1 bit start must be = to end)
     * @return 
     */
    public static int read(final int word, int start, int end){
        int index = end-start+1;
        return (word>>(start))&ByteUtils.bitMap.get(index);
    }
     
    /**
     * read specific bit from given long.
     * @param word long reference word
     * @param start starting bit (0 being the first one)
     * @param end   ending bit (for reading 1 bit start must be = to end)
     * @return 
     */
    public static int readLong(final long word, int start, int end){
        int index = end-start+1;
        return (int) ((word>>(start))&ByteUtils.bitMap.get(index));
    }
    
    public static void main(String[] args){
        //BioByteUtils.printBitMap();
        //System.out.println(BioByteUtils.getByteString(1245678));
        int header  = 0x00FFFFFF;
        int headerM = ByteUtils.write(header, 4, 10, 12);
        int headerR = ByteUtils.read(headerM, 10,12);
        
        System.out.println(ByteUtils.getByteString(header));
        System.out.println(ByteUtils.getByteString(headerM));
        System.out.println(ByteUtils.getByteString(headerR));
        
        byte[] buffer = ByteUtils.generateByteArray(1845000);
        System.out.println("Length = " + buffer.length);
        byte[] compressed = ByteUtils.gzip(buffer);
        System.out.println("compressed length = " + compressed.length);
        
        long stime_ = System.currentTimeMillis();
        for(int loop = 0; loop < 50000; loop++){
            byte[] dc = ByteUtils.ungzip(compressed);
        }
        long etime_ = System.currentTimeMillis();
        double time = (etime_-stime_)/1000.0;
        System.out.println("Deflate speed = " + time + " sec");
    }
}
