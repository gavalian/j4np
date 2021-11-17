/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.io;

import j4np.hipo5.base.HeaderType;
import j4np.hipo5.base.Reader;
import j4np.hipo5.base.RecordOutputStream;
import j4np.hipo5.base.Writer;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.SchemaFactory;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author gavalian
 */
public class HipoWriter implements AutoCloseable {
    
    private Writer               writer = null;
    private int       maximumRecordSize = 8*1024*1024;
    private int     maximumRecordEvents = 1000000;
    private int         compressionType = 1;
    
    private final SchemaFactory schemaFactory = new SchemaFactory();
    
    private final Map<Long,RecordOutputStream> outputStreams = new HashMap<>();
    private       RecordOutputStream     defaultOutputStream = new RecordOutputStream();
            
//    private List<HipoDataSorter>           sorterList = new ArrayList<HipoDataSorter>();
    private String          rewriteMode = "RECREATE";

    private long               statsBytesWritten = 0L;
    private long            statsBytesCompressed = 0L;
    private long            statsTimeCompression = 0L;
    private long              statsTimeEventCopy = 0L;
    private long             statsNumberOfEvents = 0L;

    public HipoWriter(){
        
    }
    
    public HipoWriter(String file, SchemaFactory f){
        this.schemaFactory.copy(schemaFactory);
        this.open(file);
    }
    
    public HipoWriter(String mode){
        rewriteMode = mode;
    }
    
    public final HipoWriter setCompressionType(int compression){
        this.compressionType = compression; 
        writer.setCompressionType(compression);       
        return this;
    }
    
    public final HipoWriter setMaxSize(int size){
        this.maximumRecordSize = size; return this;
    }
    
    public final HipoWriter setMaxEvents(int size){
        this.maximumRecordEvents = size; return this;
    }
    
    public SchemaFactory getSchemaFactory(){ return schemaFactory;}
    
    public static boolean checkFile(String filename, boolean remove){
        boolean flag = false;
        File f = new File(filename);
        if(f.exists()==true){
           flag = true;
           if(remove==true) f.delete();
        }
        return flag;
    }
    
    public final void open(String filename){
        
        writer = new Writer( HeaderType.HIPO_FILE, // this write HIPO in the 
                // first bytes of the file
                ByteOrder.LITTLE_ENDIAN,  // Use LITTLE_ENDIAN by default
                this.maximumRecordEvents,  // Maximum number of events in each record
                this.maximumRecordSize);  // Maximum Size of the record buffer);
        //System.out.println("RECORD SIZE = " + this.maximumRecordSize);
        writer.setCompressionType(compressionType);
        
        RecordOutputStream record = new RecordOutputStream();

        List<Schema> schemas = schemaFactory.getSchemaList();
        int   dictionarySize = schemas.size();
        
        for(int i = 0; i < dictionarySize; i++){
            Event schemaEvent = HipoUtilsIO.getSchemaEvent(schemas.get(i));
            record.addEvent(schemaEvent.getEventBuffer().array(), 
                    0, schemaEvent.getEventBufferSize());
        }
        record.getHeader().setCompressionType(0);
        record.build();
        ByteBuffer buffer = record.getBinaryBuffer();
        int size = buffer.limit();
        int sizeWords = buffer.getInt(0);
        //System.out.println(" The encoded bytes = " + buffer.limit() + " size = " + size 
        //        + "  words = " + sizeWords);
        byte[] userHeader = new byte[sizeWords*4];
        System.arraycopy(buffer.array(), 0, userHeader, 0, userHeader.length);
        //writer.open(filename,userHeader);
        //writer.open(filename);
        if(this.rewriteMode.compareToIgnoreCase("recreate")==0){
            writer.setRewriteMode(true);
        }
        writer.open(filename,userHeader);
    }
    
    private void addOutputStream(long id){
        RecordOutputStream outStream = new RecordOutputStream(ByteOrder.LITTLE_ENDIAN,maximumRecordSize,
                maximumRecordEvents,compressionType);
        outStream.getHeader().setUserRegisterFirst(id);
        outputStreams.put(id, outStream);
    }
    
    public void addEvent(Event event, long eventTag){
        if(eventTag==0){
            int size = event.getEventBufferSize();
            boolean status = 
                    defaultOutputStream.addEvent(event.getEventBuffer().array(), 0, size);
            if(status==false){
                this.statsBytesWritten += defaultOutputStream.getUncompressedSize();
                long then = System.nanoTime();
                int bytesWritten = writer.writeRecord(defaultOutputStream);
                defaultOutputStream.reset();
                defaultOutputStream.getHeader().setUserRegisterFirst(0);
                defaultOutputStream.addEvent(event.getEventBuffer().array(), 0, size);
                long now  = System.nanoTime();
                this.statsTimeCompression += now-then;
                this.statsBytesCompressed += bytesWritten;
            }
            return;
        }
        
        if(outputStreams.containsKey(eventTag)==false){
            System.out.println("[sorted-writer] ---->>>> adding output stream with tag = " + eventTag);
            addOutputStream(eventTag);
        }
        
        int tag = (int) eventTag;
        
        event.setEventTag(tag);
        
        RecordOutputStream stream = outputStreams.get(eventTag);
        long streamTag = stream.getHeader().getUserRegisterFirst();
        int size = event.getEventBufferSize();
        boolean status = stream.addEvent(event.getEventBuffer().array(), 0, size);
        if(status==false){
            long then = System.nanoTime();
            writer.writeRecord(stream);
            stream.reset();
            stream.getHeader().setUserRegisterFirst(streamTag);
            stream.addEvent(event.getEventBuffer().array(), 0, size);
            long now  = System.nanoTime();
            this.statsTimeCompression += now-then;
        }
    }
    
    public void addEvent(Event event){
        //long eventTag = 0L;
        int eventTag = event.getEventTag();
        addEvent(event,eventTag);
    }
    
    @Override
    public void close(){
        
        System.out.println("[sorted-writer] --->>>> closing file.");
        for(Map.Entry<Long,RecordOutputStream> entry : outputStreams.entrySet()){
            if(entry.getValue().getEventCount()>0){
                writer.writeRecord(entry.getValue());
            }
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
    
    public long getWriterTime(){
        return this.statsTimeCompression+this.statsTimeEventCopy;
    }
    
    public long getBytesWritten(){
        return this.statsBytesWritten;
    }
    
    public void stats(){
        double timeCopy = ((double) statsTimeEventCopy)/1_000_000_000;
        double timeComp = ((double) statsTimeCompression)/1_000_000_000;
        double ratio    = ((double) statsBytesCompressed)/statsBytesWritten;
        System.out.println("**");
        System.out.printf("Writer<5>: number of tags         : %14d\n",this.outputStreams.size()+1);
        System.out.printf("Writer<5>: number of events       : %14d\n",this.statsNumberOfEvents);
        System.out.printf("Writer<5>: bytes written    (byte): %14d\n",this.statsBytesWritten);
        System.out.printf("Writer<5>: bytes compressed (byte): %14d\n",this.statsBytesCompressed);
        System.out.printf("Writer<5>: compression ratio      : %14.4f\n",ratio);        
        System.out.printf("Writer<5>: time event copy   (sec): %14.3f\n",timeCopy);
        System.out.printf("Writer<5>: time compression  (sec): %14.3f\n",timeComp);        
        System.out.println("***");
    }
    
    public static class WriterBucketConfiguration {
        long writerTag     = 0;
        int numberOfEvents = 1000000;
        int numberOfBytes  = 8*1024*1024;
        public WriterBucketConfiguration(long __tag, int __nev, int __nb){
            writerTag = __tag;
            numberOfBytes  = __nb;
            numberOfEvents = __nev;
        }
    }
}
