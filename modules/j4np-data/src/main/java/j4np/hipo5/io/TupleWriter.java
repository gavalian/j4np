/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.io;

import j4np.hipo5.base.HeaderType;
import j4np.hipo5.base.Reader;
import j4np.hipo5.base.RecordOutputStream;
import j4np.hipo5.base.Writer;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.utils.io.TextFileReader;
import j4np.utils.io.TextFileWriter;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author gavalian
 */
public class TupleWriter {
    protected Event[]          events = null;
    protected int          bucketSize = 1024*128;
    protected  Writer          writer = null;
    protected  String      tupleLeafs = null;
    protected  String       tupleName = "tuple";
    protected  int         countLeafs = 0;
    protected  int    compressionType = 1;
    
    protected  RecordOutputStream     defaultOutputStream = null;
    
    
    public TupleWriter(){
        
    }
    
    public TupleWriter(String[] columns){
        this.parse(columns);
    }
    
    public TupleWriter(String columns){
        this.parse(columns);
    }
    
    public int nColumns(){ return countLeafs;}
    
    public void setCompressiont(int c){
        this.compressionType = c;
    }
    
    public final void parse(String[] tokens){
        int tag = 1;
        StringBuilder str = new StringBuilder();
        str.append(tupleName);
        for(int i = 0; i < tokens.length; i++){
            //if(i!=0) str.append(":");
            str.append(String.format(":%s/F/%d", tokens[i].trim(),tag)); tag++;
        }
        countLeafs = tokens.length;
        tupleLeafs = str.toString();
    }
    
    public final void parse(String format){
        String[] tokens = format.split(":");
        int tag = 1;
        StringBuilder str = new StringBuilder();
        str.append(tupleName);
        for(int i = 0; i < tokens.length; i++){
            //if(i!=0) str.append(":");
            str.append(String.format(":%s/F/%d", tokens[i].trim(),tag)); tag++;
        }
        countLeafs = tokens.length;
        tupleLeafs = str.toString();
    }
    
    public boolean open(String filename){
        writer = new Writer( HeaderType.HIPO_FILE, // this write HIPO in the 
                // first bytes of the file
                ByteOrder.LITTLE_ENDIAN,  // Use LITTLE_ENDIAN by default
                2,  // Maximum number of events in each record
                bucketSize*2);
        writer.setCompressionType(1);        
        defaultOutputStream = new RecordOutputStream(ByteOrder.LITTLE_ENDIAN,2,bucketSize*2,1);
        
        Event  event = new Event();
        Node  format = new Node(12,27,tupleLeafs);
        event.write(format);
        
        defaultOutputStream.addEvent(event.getBuffer().array(), 0, event.getBuffer().getInt(4));
        defaultOutputStream.getHeader().setCompressionType(this.compressionType);
        defaultOutputStream.build();
        ByteBuffer buffer = defaultOutputStream.getBinaryBuffer();
        int sizeWords = buffer.getInt(0);
        //System.out.println(" The encoded bytes = " + buffer.limit() + " size = " + size 
        //        + "  words = " + sizeWords);
        byte[] userHeader = new byte[sizeWords*4];
        System.arraycopy(buffer.array(), 0, userHeader, 0, userHeader.length);
        writer.setRewriteMode(true);
        writer.open(filename,userHeader); 
        this.create(countLeafs, bucketSize);

        return true;        
    }
    
    protected void create(int count, int size){
        events = new Event[count];
        for(int i = 0; i < count; i++){
            events[i] = new Event(size+64);
            events[i].getBuffer().putInt(4, 24);
            events[i].getBuffer().putShort(16, (short) 27);
            events[i].getBuffer().put(18, (byte) 1);
            events[i].getBuffer().put(19, (byte) 4);
        }
    }
    
    public void fill(float[] values){
        
        if(events[0].getBuffer().getInt(4)+4>this.bucketSize){
            for(int j = 0; j < events.length; j++){
                defaultOutputStream.reset();
                int size = events[j].getBuffer().getInt(4) - 24;
                events[j].getBuffer().putInt(20, size&(0x00FFFFFF));
                defaultOutputStream.addEvent(events[j].getBuffer());
                defaultOutputStream.getHeader().setUserRegisterFirst(j+1);
                
                //writer.setCompressionType(0);
                defaultOutputStream.getHeader().setCompressionType(this.compressionType);
                int written = writer.writeRecord(defaultOutputStream);
                //System.out.printf("writer export branch : %5d - size = %8d\n",j,written);
                events[j].getBuffer().putInt(4, 24);
            }
        }
        
        for(int i = 0; i < values.length; i++){
            int size = events[i].getBuffer().getInt(4);            
            events[i].getBuffer().putFloat(size,values[i]);
            events[i].getBuffer().putInt(4, size+4);
        }
    }
    
    public void close(){
        //this.writer.writeTrailer(true);
        for(int j = 0; j < events.length; j++){
            defaultOutputStream.reset();
            int size = events[j].getBuffer().getInt(4) - 24;
            events[j].getBuffer().putInt(20, size&(0x00FFFFFF));
            defaultOutputStream.addEvent(events[j].getBuffer());
            defaultOutputStream.getHeader().setUserRegisterFirst(j+1);
            
            //writer.setCompressionType(0);
            defaultOutputStream.getHeader().setCompressionType(this.compressionType);
            int written = writer.writeRecord(defaultOutputStream);
            //System.out.printf("writer export branch : %5d - size = %8d\n",j,written);
            events[j].getBuffer().putInt(4, 24);
        }
        
        List<Reader.RecordPosition> recordInfos = writer.getRecordPositions();                
        Bank indexNode = HipoUtilsIO.createIndexNode(recordInfos.size());        
        for(int loop = 0; loop < recordInfos.size(); loop++){
            Reader.RecordPosition pos = recordInfos.get(loop);
            indexNode.putLong( 0, loop, pos.getPosition());
            indexNode.putInt(  1, loop, pos.getLength());
            indexNode.putInt(  2, loop, pos.getCount());
            indexNode.putLong( 3, loop, pos.getUserWordOne());
            indexNode.putLong( 4, loop, pos.getUserWordTwo());
        }
        
        Event event = new Event();
        event.write(indexNode);
        
        RecordOutputStream indexRecord = new RecordOutputStream();
        indexRecord.addEvent(event.getEventBuffer().array(), 0,event.getEventBufferSize());
        /*        for(int i = 0; i < recordLengths.size(); i++){
            System.out.println(" length " + i + " = " + recordLengths.get(i));
        }*/
        
        indexRecord.getHeader().setCompressionType(0);
        writer.writeLastRecord(indexRecord);
        writer.close();
    }
    
    public void debugShow(){
        for(int i = 0; i < events.length; i++){
            System.out.printf(" event # %5d - length = %9d\n",i,events[i].getBuffer().getInt(4));
        }
    }
    
    public static String[] generate(int ncolumns){
        String[] c = new String[ncolumns];
        for(int i = 0; i < c.length; i++) c[i] = String.format("c%d", i+1);
        return c;
    }
    
    public static void cvs(String output, String csvfile){
        File fcsv = new File(csvfile);
        if(fcsv.exists()==false){
            System.out.println("\nTupleWriter:: (error) csv file not found : " + csvfile);
            return;
        }
        File fout = new File(output);
        if(fout.exists()==true){
            System.out.println("\nTupleWriter:: (error) the output file exists : " + output);
            return;
        }
        
        TextFileReader r = new TextFileReader();
        boolean status = r.readNext();
        
        if(status==true){
            String[] columns = r.getString().split(",");
            TupleWriter w = new TupleWriter(columns);
            w.open(output);
            int nLine = 0;
            while(r.readNext()==true){
                nLine++;
                float[] data = r.getAsFloatArray(0, w.nColumns());
                w.fill(data);
            }
            w.close();
        }        
    }
    
    public static void createRandom(String output, int ncolumns, int nrows){
        TupleWriter.createRandom(output, ncolumns, nrows, false);
    }
    
    public static void createRandom(String output, int ncolumns, int nrows, boolean gaussian){
        Random      r = new Random();
        String[] columns = TupleWriter.generate(ncolumns);
        TupleWriter w = new TupleWriter(columns);
        w.open(output);
        TextFileWriter wt = new TextFileWriter("data.csv");
        String[] header = new String[24];
        for(int i = 0; i < 24; i++){
            header[i] = String.format("c%d", i);
        }
         wt.writeString(Arrays.toString(header).replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s+", ""));
        r.setSeed(123456);
        float[] data = new float[ncolumns];
        for(int j = 0; j < nrows; j++){
            for(int i = 0; i < ncolumns; i++) data[i] = gaussian==true?(float)(r.nextGaussian()*0.25+0.5):r.nextFloat();
            w.fill(data);
            wt.writeString(Arrays.toString(data).replaceAll("\\[", "").replaceAll("\\]", ""));
        }
        w.close();
        wt.close();
    }
    
    public static void main(String[] args){
        
        
        TupleWriter.createRandom("tuple.h5", 24,50000000, false);
       /* TupleWriter w = new TupleWriter();
        w.parse("c1:c2:c3:c4:c5:c6");
        w.open("tuple.h5");
        float[] data = new float[6];
        Random r = new Random();
        for(int i = 0; i < 6*1024/4; i++){
            for(int j = 0; j < data.length; j++) data[j] = (float) r.nextFloat();//r.nextGaussian();
            long then = System.nanoTime();
            //wt.writeString(Arrays.toString(data).replaceAll("\\[", "").replaceAll("\\]", ""));
            w.fill(data);
            long now = System.nanoTime();            
        }
        w.close();*/
        //System.out.println(w.tupleLeafs);
        /*
        TupleWriter w = new TupleWriter();
        w.open("tuple_unc.h5");
        w.create(24, 1024*162);
        float[] data = new float[24];
        Random r = new Random();
        TextFileWriter wt = new TextFileWriter("data.csv");
        String[] header = new String[24];
        for(int i = 0; i < 24; i++){
            header[i] = String.format("c%d", i);
        }
        wt.writeString(Arrays.toString(header).replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s+", ""));
        r.setSeed(123456);
        long time = 0L;
        //long then = System.currentTimeMillis();
        for(int k = 0; k < 10; k++)
            for(int i = 0; i < 5000000; i++){
                for(int j = 0; j < data.length; j++) data[j] = (float) r.nextFloat();//r.nextGaussian();
                long then = System.nanoTime();
                //wt.writeString(Arrays.toString(data).replaceAll("\\[", "").replaceAll("\\]", ""));
                w.fill(data);
                long now = System.nanoTime();
                time += now - then;
            }
        
        
        w.close();
        w.debugShow();
        wt.close();
        System.out.printf("writing time = %d\n",time);*/
    }
}
