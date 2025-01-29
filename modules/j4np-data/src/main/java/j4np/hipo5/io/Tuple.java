/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.io;

import j4np.hipo5.base.FileHeader;
import j4np.hipo5.base.HipoException;
import j4np.hipo5.base.Reader;
import j4np.hipo5.base.RecordHeader;
import j4np.hipo5.base.RecordInputStream;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.DataType;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.utils.HipoLogos;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class Tuple {
    
    record Positions(long offset, long length){}
    
    protected RandomAccessFile inStreamRandom;
    protected FileHeader fileHeader;
    private ByteOrder byteOrder;
    /** Evio version of file/buffer being read. */
    private int evioVersion;
    private int readerErrorCode = HipoReader.NO_ERROR;
    protected final RecordInputStream inputRecordStream = new RecordInputStream();
    
    protected long[] branches = null;
    protected List<Positions>[] address = null;
    protected Event[] events = null;
    protected List<String>  activeBranches = new ArrayList<>();
    
    protected int  currentBucket = 0;
    protected int   currentEvent = 0;
    protected int   currentOffset = 20;
    
    protected int  currentNodeSize = 0;
    
    public Tuple(){
        
    }
    
    public Tuple(String[] branches){
        
    }
    
    public Tuple setBranches(String branches){
        String[] tokens = branches.split(":");
        this.activeBranches.clear();
        for(String b : tokens) this.activeBranches.add(b);
        return this;
    }
    
    public Tuple setBranches(String[] branches){
        this.activeBranches.clear();
        for(String b : branches) this.activeBranches.add(b);
        return this;
    }
    
    public void setBranches(long... branchList){
        branches = new long[branchList.length];
        address  = new List[branchList.length];
        events   = new Event[branchList.length];
        
        for(int i = 0; i < branches.length;i++){
            branches[i] = branchList[i];
            address[i]  = new ArrayList<>();
            events[i]   = new Event(256*1024);
        }
    }
    
    public void fill(int[] bins, int branch){
        int length = events[branch].getBuffer().getInt(20);
        int elements = length/4;
        for(int i = 0; i < elements; i++){
            int bin = (int) (events[branch].getBuffer().getFloat(i*4+24)*bins.length);
            if(bin>=0&&bin<bins.length) bins[bin]++;
        }
    }
    
    public int getBuckets(){
       return address[0].size();
    }
    protected void readBucket(int bucket){
        
        for(int j = 0; j < address.length; j++){
            long position = address[j].get(bucket).offset;
            try {
                
                inputRecordStream.readRecord(inStreamRandom, position);
                int size = inputRecordStream.getHeader().getDataLength();
                int comp = inputRecordStream.getHeader().getCompressedDataLength();
                long tag = inputRecordStream.getHeader().getUserRegisterFirst();
                //if(bucket==0)
                inputRecordStream.copyEvent(events[j].getEventBuffer(), 0, 0);
                //System.out.printf(" reading : %3d - size = %8d, compressed = %8d, tag = %3d\n",
                //        j,size,comp,tag);
            } catch (HipoException ex) {
                Logger.getLogger(Tuple.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        int size = events[0].getBuffer().getInt(4);
        int nelements = (size-24)/4;
        //System.out.printf(" size = %d , nelements = %d\n",size,nelements);
        this.currentNodeSize = nelements;
    }
    
    /*public boolean next(){
       if(currentEvent+1>this.currentNodeSize){
           this.currentBucket++;
           if(this.currentBucket>=this.address[0].size()) return false;
           this.readBucket(currentBucket);
           currentEvent = 0;
           currentOffset = 20+currentEvent*4;
       } else {
           currentEvent++;
           currentOffset = 20+currentEvent*4;
       }
       return true;
    }*/
    
    public boolean next(float[] array){
       if(currentEvent+1>this.currentNodeSize){
           this.currentBucket++;
           if(this.currentBucket>=this.address[0].size()) {
               //System.out.printf(" can't read bucket %d at current event %d, node size = %d\n",
               //    currentBucket,currentEvent, currentNodeSize);
               return false;
           }
           //System.out.printf(" reading bucket %d at current event %d, node size = %d\n",
           //        currentBucket,currentEvent, currentNodeSize);
           this.readBucket(currentBucket);
           currentEvent  = 0;
           currentOffset = 24+currentEvent*4;
       } else {
           currentEvent++;
           currentOffset = 24+currentEvent*4;
       }       
       for(int i = 0; i < events.length; i++) array[i] = events[i].getBuffer().getFloat(currentOffset);
       return true;
    }
    
    public double getBrnach(int branch){
        //return events[branch].getBuffer().getFloat(20+currentEvent*4);
        return (double) events[branch].getBuffer().array()[20];
        //return value;
    }
    
    public void open(String file){
         try { 
             inStreamRandom = new RandomAccessFile(file,"r");
             fileHeader = new FileHeader();
            byte[] headerBytes = new byte[RecordHeader.HEADER_SIZE_BYTES];
            ByteBuffer headerBuffer = ByteBuffer.wrap(headerBytes);        
            FileChannel channel = inStreamRandom.getChannel().position(0L);
            int n = inStreamRandom.read(headerBytes);
            
            int userHeaderPosition = fileHeader.getHeaderLength();
            int userHeaderLength   = fileHeader.getUserHeaderLength();
            
            RecordInputStream  dictionaryRecord = new RecordInputStream();
            dictionaryRecord.readRecord(inStreamRandom, userHeaderPosition);
            
            int     recordEntries = dictionaryRecord.getEntries();
            Event dictionaryEvent = new Event();            
            Node       schemaNode = new Node(12,27,DataType.STRING,1024);
            
            System.out.println("record entries = " + recordEntries);
            byte[] event = dictionaryRecord.getEvent(0);
            dictionaryEvent.initFrom(event);
            int nodePosition = dictionaryEvent.scan(12, 27);
            
            if(nodePosition>4){
                schemaNode = dictionaryEvent.read(schemaNode, nodePosition);
                schemaNode.show();
                
                String[] tokens = schemaNode.getString().split(":");
                List<Long> tags = new ArrayList<>();
                for(int i = 1; i < tokens.length; i++){
                    String[] pairs = tokens[i].split("/");
                    if(this.activeBranches.isEmpty()==true){
                        tags.add(Long.parseLong(pairs[2]));                       
                    } else {
                        if(this.activeBranches.contains(pairs[0])==true){
                            tags.add(Long.parseLong(pairs[2]));
                        }
                    }
                }
                //for(Long l : tags) System.out.print(" " + l);
                //System.out.println();
                long[] ab = new long[tags.size()];
                for(int i = 0; i < ab.length; i++) ab[i] = tags.get(i);
                this.setBranches(ab);
            }
            this.scanFileTrailer();
            this.readBucket(0);
            
         } catch (FileNotFoundException ex) {
             HipoLogos.showYoda();
             HipoLogos.printFileNotFound();
             Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
         } catch (IOException ex) {
            Logger.getLogger(Tuple.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HipoException ex) {
            Logger.getLogger(Tuple.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private int contains(long tag, long[] tags){
        for(int i = 0; i < tags.length;i++) if(tags[i]==tag) return i;
        return -1;
    }
    
    
    public void reset(){
        currentBucket = 0;
        currentEvent  = 0;
        this.readBucket(currentBucket);        
    }
    
    public final void scanFileTrailer(){
        
        fileHeader = new FileHeader();
        byte[] headerBytes = new byte[RecordHeader.HEADER_SIZE_BYTES];
        ByteBuffer headerBuffer = ByteBuffer.wrap(headerBytes);        
        //System.out.println(" SIZE = " + headerBytes.length);
        try {

            FileChannel channel = inStreamRandom.getChannel().position(0L);
            int n = inStreamRandom.read(headerBytes);
            
            fileHeader.readHeader(headerBuffer);
            byteOrder = fileHeader.getByteOrder();
            
            evioVersion = fileHeader.getVersion();
            
            long trailerPosition = fileHeader.getTrailerPosition();
            long channelSize     = channel.size();
                        
            if((trailerPosition+54)>channelSize){
                System.out.println("\n\n");
                System.out.println("reader:: ****** warning : this file seems to be corrupt or not closed properly.");
                System.out.println("reader:: ****** warning : please use HipoDoctor class to salvage useful parts.");
                System.out.println("reader:: ****** warning : or use hipoutils -doctor to re-index and write file.");
                System.out.println("\n\n");
                this.readerErrorCode = HipoReader.ERROR_NOINDEX;
                //recordPositions.clear();
                return;
            }
            //System.out.println("VERSION = " + evioVersion + "  TRAILER = " + fileHeader.getTrailerPosition());
            
            this.inputRecordStream.readRecord(inStreamRandom, trailerPosition);
            
            byte[] trailerData = inputRecordStream.getEvent(0);
            
            //System.out.println(" Number of events = " + inputRecordStream.getEntries() + " size = " + trailerData.length);
            Event trailerEvent = new Event();
            
            trailerEvent.initFrom(trailerData);
            Bank indexNode = HipoUtilsIO.createIndexNode();
            
            int scanIndex = trailerEvent.scan(indexNode.getSchema().getGroup(),
                    indexNode.getSchema().getItem());
            
            trailerEvent.read(indexNode, scanIndex);
            
            //indexNode.show();
            int nrows = indexNode.getRows();
            
            for(int i = 0; i < nrows; i++){
                long tag = indexNode.getLong("userWordOne", i);
                int index = this.contains(tag, branches);
                if(index>=0){
                    address[index].add(new Positions(indexNode.getLong("position", i), indexNode.getInt("length", i)));
                }
            }
            //trailerEvent.scan();
            //trailerEvent.show();
            //System.out.println(indexNode.nodeString());
            //indexNode.getSchema().show();
            /*recordPositions.clear();
            numberOfEvents = 0;
            int size = indexNode.getRows();
            for(int loop = 0; loop < size; loop++){
                Reader.RecordPosition position = new Reader.RecordPosition(
                        indexNode.getLong(0, loop),
                        indexNode.getInt(1, loop),
                        indexNode.getInt(2, loop)
                );
                position.setUserWordOne(indexNode.getLong(3, loop));
                position.setUserWordTwo(indexNode.getLong(4, loop));
                if(readerTags.size()>0){
                   if(readerTags.contains(position.getUserWordOne())){
                       recordPositions.add(position);
                       numberOfEvents += position.getCount();
                   }
                } else {
                    recordPositions.add(position);
                    numberOfEvents += position.getCount();
                }
                
            }*/
            
        } catch (IOException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HipoException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    public void show(){
        for(int i = 0; i < address.length; i++){
            System.out.println(" TAG = " + branches[i] + "  size = " + address[i].size());
            for(int k = 0; k < address[i].size(); k++){
                System.out.printf(" %12d %8d ", address[i].get(k).offset,address[i].get(k).length);
            }
            System.out.println();
        }
    }
    public long[] getRange(int size){
        long[] range = new long[size];
        for(int r = 1; r<=size; r++) range[r-1] = r;
        return range;
    }
    
    public int getBin(double value){
        int bin = (int) (value*100);
        return bin;
    }
    
    public static void benchmark1(){
        Tuple t = new Tuple();
        t.setBranches("c1:c2:c3:c4:c5:c6:c7:c8:c9:10:c11:c12");
        t.open("tuple.h5");
        
        float[] data = new float[4];
        int counter = 0;
        long then = System.currentTimeMillis();
        
        int[] h1d = new int[100];
        int[] h2d = new int[100];
        int[] h3d = new int[100];
        int[] h4d = new int[100];
        int nbuckets = t.getBuckets();
        
        for(int i = 0; i < nbuckets; i++){
            t.readBucket(i);
            t.fill(h1d, 0);
            t.fill(h2d, 1);
            t.fill(h3d, 2);
            t.fill(h4d, 3);
        }
        /*while(t.next(data)){
            counter++;
            //System.out.println(Arrays.toString(data));
        }*/
        long now = System.currentTimeMillis();
        
        System.out.printf(" time = %d\n",now-then);
        System.out.println(" processed rows = " + counter);
    }
    
    public static void benchmark(){
        
        Tuple t = new Tuple();
        t.setBranches("c1:c2:c3:c4");
        
        t.open("tuple.h5");
        
        float[] data = new float[4];
        int counter = 0;
        long then = System.currentTimeMillis();
        
        double[] h1d = new double[100];
        double[] h2d = new double[100];
        double[] h3d = new double[100];
        double[] h4d = new double[100];
        int nbuckets = t.getBuckets();
        
        int bin = 0;
        while(t.next(data)){
            counter++;
            bin = t.getBin(data[0]);
            h1d[bin]++;
            bin = t.getBin(data[1]);
            h2d[bin]++;
            bin = t.getBin(data[2]);
            h3d[bin]++;
            bin = t.getBin(data[3]);
            h4d[bin]++;
            //System.out.println(Arrays.toString(data));
        }
        long now = System.currentTimeMillis();
        
        System.out.printf(" time = %d\n",now-then);
        System.out.println(" processed rows = " + counter);
        System.out.println(Arrays.toString(h1d));
    }
    public static void main(String[] args){
        
        for(int i = 0 ; i < 5; i++) 
            Tuple.benchmark();
        /*Tuple t = new Tuple();
        t.setBranches("c1:c2:c3:c21");
        t.open("tuple.h5");
        
        float[] data = new float[4];
        int counter = 0;
        long then = System.currentTimeMillis();
        
        int[] h1d = new int[100];
        int[] h2d = new int[100];
        int[] h3d = new int[100];
        int[] h4d = new int[100];
        int nbuckets = t.getBuckets();
        
        for(int i = 0; i < nbuckets; i++){
            t.readBucket(i);
            t.fill(h1d, 0);
            t.fill(h2d, 1);
            t.fill(h3d, 2);
            t.fill(h4d, 3);
        }*/
        /*while(t.next(data)){
            counter++;
            //System.out.println(Arrays.toString(data));
        }*/
        //long now = System.currentTimeMillis();
        
        //System.out.printf(" time = %d\n",now-then);
        //System.out.println(" processed rows = " + counter);
        
        //System.out.println(Arrays.toString(h1d));
        /*
        Tuple t = new Tuple();
        t.open("tuple_unc.h5");
        //t.setBranches(new long[]{9,12,13,14,15,16,18});
        //t.setBranches(new long[]{9,10,21,23});
        t.setBranches(t.getRange(12));
        t.scanFileTrailer();
        t.show();
        
        //t.readBucket(0);
        long count = 0L;
        int[] array = new int[100];
        double[] values = new double[4];
        float[]    barr = new float[24];
        
        for(int k = 0; k < 25; k++){
            t.reset();
           // for(int j = 0; j < 100000; j++){
           long then = System.currentTimeMillis();
           while(t.next(barr)){
               //System.out.println(barr[0]);
               //values[0] = t.getBrnach(0);
               //values[1] = t.getBrnach(1);
               //values[2] = t.getBrnach(2);
               //values[3] = t.getBrnach(3);
               for(int j = 0; j < 12; j++){
                   //int bin = t.getBin(values[j]);
                   int bin = (int) (barr[j]*100);
                   if(bin>=0&&bin<100) array[bin]++;
               }
               count++;
               //System.out.printf(" value = %f\n",value);
           }
           long now = System.currentTimeMillis();
        
           System.out.printf("count = %d, time = %d msec\n",count,now-then);
        }
        */
        //t.readBucket(2);
    }
}
