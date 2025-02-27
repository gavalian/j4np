/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.io;

import j4np.data.base.DataEvent;
import j4np.data.base.DataFrame;
import j4np.data.base.DataSource;
import j4np.hipo5.base.Reader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import j4np.hipo5.base.FileHeader;
import j4np.hipo5.base.FileEventIndex;
import j4np.hipo5.base.HipoException;
import j4np.hipo5.base.RecordHeader;
import j4np.hipo5.base.RecordInputStream;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.DataType;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.utils.HipoLogos;
import j4np.utils.ProgressPrintout;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author gavalian
 */
public class HipoReader implements DataSource {
    
    public static int      NO_ERROR = 1;
    public static int ERROR_NOINDEX = 2;
    
    protected final List<Reader.RecordPosition> recordPositions = 
            new ArrayList<>();
    
    private int readerErrorCode = HipoReader.NO_ERROR;
    protected RandomAccessFile inStreamRandom;
    protected final RecordInputStream inputRecordStream = new RecordInputStream();
    /** File header. */
    protected FileHeader fileHeader;
    /** Object to handle event indexes in context of file and having to change records. */
    protected FileEventIndex eventIndex = new FileEventIndex();
    /** Byte order of file/buffer being read. */
    private ByteOrder byteOrder;
    /** Evio version of file/buffer being read. */
    private int evioVersion;
    
     /** Number or position of last record to be read. */
    protected int currentRecordLoaded;
    private   int debugMode = 0;
    protected int numberOfEvents = 0;
    
    protected List<Long>  readerTags = new ArrayList<Long>();
    
    private SchemaFactory  schemaFactory = new SchemaFactory();
    private List<String>   configFactory = new ArrayList<>();
    private Map<String,String>  userConfiguration = new HashMap<>();
    
    private ProgressPrintout      progress = new ProgressPrintout();
    private boolean          progressPrint = true;
    
    private Event            internalEvent = new Event();
    
    public HipoReader(){
        
    }
    
    public HipoReader(String file){
        this.open(file);
    }
    
    public HipoReader(String file, long... tags){
        this.setDebugMode(0);
        this.setTags(tags);
        this.open(file);
    }
    
    public void setDebugMode(int mode){
        this.debugMode = mode;
    }
    
    public void setProgressPrint(boolean mode){
        this.progressPrint = mode;
    }
    
    public final HipoReader setTags(long... tags){
        for(int i = 0; i < tags.length; i++){
            readerTags.add(tags[i]);
        }
        return this;
    }
    
    public int getErrorCode(){ return this.readerErrorCode;}

    /*
    public HipoWriter createWriter(){
        HipoWriter writer = new HipoWriter();
        writer.getSchemaFactory().copy(schemaFactory);
        return writer;
    }*/

    
    @Override
    public final void open(String filename) {
        try { 
            inStreamRandom = new RandomAccessFile(filename,"r");
            
            fileHeader = new FileHeader();
            byte[] headerBytes = new byte[RecordHeader.HEADER_SIZE_BYTES];
            ByteBuffer headerBuffer = ByteBuffer.wrap(headerBytes);        
            FileChannel channel = inStreamRandom.getChannel().position(0L);
            int n = inStreamRandom.read(headerBytes);
            
            int userHeaderPosition = fileHeader.getHeaderLength();
            int userHeaderLength   = fileHeader.getUserHeaderLength();
            
            RecordInputStream  dictionaryRecord = new RecordInputStream();
            dictionaryRecord.readRecord(inStreamRandom, userHeaderPosition);
            
            int recordEntries = dictionaryRecord.getEntries();
            //System.out.println("reader:: ***** dictionary read with entries = "+recordEntries);
            
            /*if(debugMode>0){
                System.out.println(String.format("reader:: *****>>>>> openning file : %s", filename));
                System.out.println(String.format("reader:: ***** dictionary entries : %12d", recordEntries));
            }*/
            
            Event dictionaryEvent = new Event();            
            Node       schemaNode = new Node(120,1,DataType.STRING,128);
            for(int nevt = 0 ; nevt < recordEntries; nevt++){
                
                byte[] event = dictionaryRecord.getEvent(nevt);
                dictionaryEvent.initFrom(event);
                //System.out.println(" event # " + nevt);
                //dictionaryEvent.scanShow();
                //dictionaryEvent.show();
                int nodePosition = dictionaryEvent.scan(120, 1);
                if(nodePosition>4){
                    try {
                        schemaNode = dictionaryEvent.read(schemaNode, nodePosition);                    
                        //schemaNode.show();
                        //System.out.println("NODE TYPE = " + schemaNode.getType());                
                        Schema schema = Schema.fromJsonString(schemaNode.getString());
                        //schema.show();
                        schemaFactory.addSchema(schema);
                        //System.out.printf("position : %d\n",nodePosition);
                        //System.out.printf("content  : %s\n\n", schemaNode.getString());
                    } catch (Exception e) { 
                        System.out.println("something went horribly wrong");
                        System.out.printf("position : %d\n",nodePosition);
                        System.out.printf("content  : %s\n\n", schemaNode.getString());
                    }
                }
                
                int configKeyPos = dictionaryEvent.scan(32555,1);
                int configStringPos = dictionaryEvent.scan(32555,2);
                
                if(configKeyPos>0&&configStringPos>0){
                    Node   ckn = new Node(32555,1,DataType.STRING,20);
                    Node   csn = new Node(32555,2,DataType.STRING,20);
                    ckn = dictionaryEvent.read(ckn,configKeyPos);
                    csn = dictionaryEvent.read(csn,configStringPos);
                    userConfiguration.put(ckn.getString(), csn.getString());
                }
            }
            
            this.scanFileTrailer();
            this.redoFileEventIndex();
            
            /*if(debugMode>0){
                System.out.println(String.format("reader:: ***** number of  records : %12d", recordPositions.size()));
                System.out.println(String.format("reader:: ***** number of  events  : %12d", eventIndex.getMaxEvents()));
            }            
            if(debugMode>0){
                System.out.println(String.format("reader:: *****>>>>> openning file : %s", filename));
                System.out.println(String.format("reader:: ***** dictionary entries : %12d", recordEntries));
            }*/
            
            if(this.debugMode>0) System.out.printf("[hipo5] >> records %9d, event %9d, schemas %5d << f = %s\n",
                    recordPositions.size(),eventIndex.getMaxEvents(),schemaFactory.getSchemaKeys().size(), filename);
            
            /*for(int i = 0; i < recordPositions.size(); i++){
                System.out.printf("----> record %5d , position %5d\n",i,recordPositions.get(i).getPosition());
            }*/
        } catch (FileNotFoundException ex) {
            HipoLogos.showYoda();
            HipoLogos.printFileNotFound();
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | HipoException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean hasConfig(String name){
        return userConfiguration.containsKey(name);
    }
    
    public String getConfig(String name){
        return userConfiguration.get(name);
    }
    
    public Map<String,String> getUserConfigurations(){ return this.userConfiguration;}
    
    public void showRecords(){
        System.out.println("--------> size = " + recordPositions.size());
        int counter = 0;
        int entries = 0;
        for(Reader.RecordPosition pos : recordPositions){
            System.out.println(String.format("%4d : ",counter) + pos);
            counter++;
            entries += pos.getCount();
        }
        System.out.println(" Entries = " + entries);
    }
    
    public void showInfo(){
        Map<Long,Integer>    counter = new HashMap<>();
        Map<Long,Integer>    entries = new HashMap<>();
        Map<Long,Integer> dataLength = new HashMap<>();
        for(Reader.RecordPosition pos : recordPositions){
            long    tag = pos.getUserWordOne();
            int   count = pos.getCount();
            int  length = pos.getLength();
            
            if(counter.containsKey(tag)==false){
                counter.put(tag, 0);
                entries.put(tag, 0);
                dataLength.put(tag, 0);
            }            
            Integer value = counter.get(tag); counter.put(tag, value+1);
            value = entries.get(tag); entries.put(tag, value + count);
            value = dataLength.get(tag); dataLength.put(tag, value + length);            
        }
        System.out.printf("file info: (# records = %d):\n",recordPositions.size());
        Set<Long> keys = counter.keySet();
        for(Long key : keys){
            System.out.printf("****>>>> index tag : %8d, count = %9d, entries = %9d, data length = %9d\n",key,
                    counter.get(key),entries.get(key),dataLength.get(key));
        }
    }
    
    public final void redoFileEventIndex(){
        int nRecords = recordPositions.size();
        for(int n = 0; n < nRecords; n++){
            eventIndex.addEventSize(recordPositions.get(n).getCount());
        }
        eventIndex.rewind();
        //eventIndex.show();
    }
    
    public void rewind(){ eventIndex.rewind(); }
    public boolean hasNext(){
        return eventIndex.canAdvance();
    }
    
    public SchemaFactory getSchemaFactory(){ return schemaFactory;}
    
    public Event getEvent(Event event, int index){
        int previousRecord = eventIndex.getRecordNumber();
        eventIndex.setEvent(index);
        int currentRecord  = eventIndex.getRecordNumber();
        if(currentRecord!=previousRecord){
            long position = this.recordPositions.get(currentRecord).getPosition();
            try {
                inputRecordStream.readRecord(inStreamRandom, position);
                //System.out.println(String.format("*****>>>> read record # %5d, events = %8d", 
                //        record, inputRecordStream.getEntries()));
                
            } catch (HipoException ex) {
                Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        int recordEventNumber = eventIndex.getRecordEventNumber();
        //System.out.println(" RecordEvent Number = " + recordEventNumber + "   entries " + inputRecordStream.getEntries());
        int         eventSize = inputRecordStream.getEventLength(recordEventNumber);
        event.require(eventSize);
        try {
            inputRecordStream.copyEvent(event.getEventBuffer(), 0, recordEventNumber);
        } catch (HipoException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return event;
    }
    /**
     * returns the next event that contains schemas provided
     * by the list.
     * @param event event to fill
     * @param schemas
     * @return the filled event.
     */
    public Event nextEvent(Event event, List<Schema> schemas){
        //System.out.println("SCHEMAS SIZE = " + schemas.size());        
        if(schemas.isEmpty()){
            return nextEvent(event);
        }
        
        while(hasNext()==true){
            nextEvent(event);
            if(event.hasBanks(schemas)==true) return event;
        }
        event.reset();
        return event;
    }
    
    public boolean nextEvent(Bank[] banks){
        if(this.hasNext()==false){
            for(Bank b : banks) b.reset();
            return false;
        }
        this.nextEvent(internalEvent);
        this.internalEvent.read(banks);
        return true;
    }
    /**
     * returns the next event that contains schemas provided
     * by the list.
     * @param event event to fill
     * @param _group group id 
     * @param _item item id
     * @return the filled event.
     */
    public Event nextEvent(Event event, int _group, int _item){
        //System.out.println("SCHEMAS SIZE = " + schemas.size());        
        
        while(hasNext()==true){
            nextEvent(event);
            try {
                if(event.scan(_group, _item)>0) return event;
            } catch (Exception e){
                 int   eventNumber = eventIndex.getEventNumber();
                int  recordNumber = eventIndex.getRecordNumber();
                System.out.printf("(corruption error) : oh no. cant scan the event (%8d, %8d) with length = %d (%X,%d,%d) \n",
                        recordNumber, eventNumber, event.getEventBufferSize(), 
                                event.getEventBuffer().getInt(0),
                                event.getEventBuffer().getInt(0),
                                event.getEventBuffer().getInt(4)
                        
                        );                                
            }
        }
        event.reset();
        return event;
    }
    
    public Bank getBank(String name){
        return this.schemaFactory.getBank(name);
    }
    
    public Bank[] getBanks(String... banks){
        Bank[] b = new Bank[banks.length];
        for(int i = 0; i < banks.length; i++)
            b[i] = this.getBank(banks[i]);
        return b;
    }
    
    public Schema[] getSchemas(String... schemas){
        Schema[] s = new Schema[schemas.length];
        for(int i = 0; i < s.length; i++)
            s[i] = this.getSchemaFactory().getSchema(schemas[i]);
        return s;
    }
       
    public Schema[] getSchemasCopy(String... schemas){
        Schema[] s = new Schema[schemas.length];
        for(int i = 0; i < s.length; i++)
            s[i] = this.getSchemaFactory().getSchema(schemas[i]).copy();
        return s;
    }
    
    public boolean next(){
        
        int   eventNumber = eventIndex.getEventNumber();
        int  recordNumber = eventIndex.getRecordNumber();
        //System.out.println("next event:: event # " + eventNumber + " , record # " + recordNumber);
        //if(eventNumber<0) eventIndex.advance();        
        //if( eventNumber == 0 ){
        if( eventNumber < 0 ){
            long position = this.recordPositions.get(0).getPosition();
            //System.out.println("next event:: reading first record at position " + position);
            try {
                inputRecordStream.readRecord(inStreamRandom, position);
            } catch (HipoException ex) {
                Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println("next event:: entries " + inputRecordStream.getEntries());
            //System.out.println(String.format("*****>>>> read record # %5d, events = %8d", 
            //        0, inputRecordStream.getEntries()));
            eventIndex.advance();
        } else {
            //System.out.println(" event # = " + eventNumber + " record # = " + recordNumber);
            if(!eventIndex.canAdvance()){
                System.out.println("can no longer advance");
                return false;
            }
            if(eventIndex.advance()){
                int    record = eventIndex.getRecordNumber();
                long position = this.recordPositions.get(record).getPosition();
                //System.out.println("next event:: reading record # " + record + " at position " + position);
                try {
                    inputRecordStream.readRecord(inStreamRandom, position);
                    //System.out.println(String.format("*****>>>> read record # %5d, events = %8d", 
                    //        record, inputRecordStream.getEntries()));
                    
                } catch (HipoException ex) {
                    Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return true;
    }
    
    public Event nextEvent(Event event){
        
        int   eventNumber = eventIndex.getEventNumber();
        int  recordNumber = eventIndex.getRecordNumber();
        //System.out.println("next event:: event # " + eventNumber + " , record # " + recordNumber);
        //if(eventNumber<0) eventIndex.advance();        
        //if( eventNumber == 0 ){
        if(this.progressPrint==true) this.progress.updateStatus();
        if( eventNumber < 0 ){
            long position = this.recordPositions.get(0).getPosition();
            //System.out.println("next event:: reading first record at position " + position);
            try {
                inputRecordStream.readRecord(inStreamRandom, position);
            } catch (HipoException ex) {
                Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println("next event:: entries " + inputRecordStream.getEntries());
            //System.out.println(String.format("*****>>>> read record # %5d, events = %8d", 
            //        0, inputRecordStream.getEntries()));
            eventIndex.advance();
        } else {
            //System.out.println(" event # = " + eventNumber + " record # = " + recordNumber);
            if(!eventIndex.canAdvance()){
                System.out.println("can no longer advance");
                return null;
            }
            if(eventIndex.advance()){
                int    record = eventIndex.getRecordNumber();
                long position = this.recordPositions.get(record).getPosition();
                //System.out.println("next event:: reading record # " + record + " at position " + position);
                try {
                    inputRecordStream.readRecord(inStreamRandom, position);
                    //System.out.println(String.format("*****>>>> read record # %5d, events = %8d", 
                    //        record, inputRecordStream.getEntries()));
                    
                } catch (HipoException ex) {
                    Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        int recordEventNumber = eventIndex.getRecordEventNumber();
        //System.out.println(" RecordEvent Number = " + recordEventNumber + "   entries " + inputRecordStream.getEntries());
        int         eventSize = inputRecordStream.getEventLength(recordEventNumber);
        event.require(eventSize);
        try {
            inputRecordStream.copyEvent(event.getEventBuffer(), 0, recordEventNumber);
        } catch (HipoException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        //eventIndex.advance();
        return event;
    }
    
    public ByteBuffer nextEventByteBuffer(){
        
        int   eventNumber = eventIndex.getEventNumber();
        int  recordNumber = eventIndex.getRecordNumber();
        //System.out.println("next event:: event # " + eventNumber + " , record # " + recordNumber);
        //if(eventNumber<0) eventIndex.advance();        
        //if( eventNumber == 0 ){
        if(this.progressPrint==true) this.progress.updateStatus();
        if( eventNumber < 0 ){
            long position = this.recordPositions.get(0).getPosition();
            //System.out.println("next event:: reading first record at position " + position);
            try {
                inputRecordStream.readRecord(inStreamRandom, position);
            } catch (HipoException ex) {
                Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println("next event:: entries " + inputRecordStream.getEntries());
            //System.out.println(String.format("*****>>>> read record # %5d, events = %8d", 
            //        0, inputRecordStream.getEntries()));
            eventIndex.advance();
        } else {
            //System.out.println(" event # = " + eventNumber + " record # = " + recordNumber);
            if(!eventIndex.canAdvance()){
                System.out.println("can no longer advance");
                return null;
            }
            if(eventIndex.advance()){
                int    record = eventIndex.getRecordNumber();
                long position = this.recordPositions.get(record).getPosition();
                //System.out.println("next event:: reading record # " + record + " at position " + position);
                try {
                    inputRecordStream.readRecord(inStreamRandom, position);
                    //System.out.println(String.format("*****>>>> read record # %5d, events = %8d", 
                    //        record, inputRecordStream.getEntries()));
                    
                } catch (HipoException ex) {
                    Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        int recordEventNumber = eventIndex.getRecordEventNumber();
        //System.out.println(" RecordEvent Number = " + recordEventNumber + "   entries " + inputRecordStream.getEntries());
        int         eventSize = inputRecordStream.getEventLength(recordEventNumber);
        
        ByteBuffer event = null;
        try {
            //inputRecordStream.copyEvent(event.getEventBuffer(), 0, recordEventNumber);
             event = inputRecordStream.copyEventBuffer(recordEventNumber);
        } catch (HipoException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        //eventIndex.advance();
        return event;
    }
    
    
    public Event emptyRead(Event event){
                int eventNumber = eventIndex.getEventNumber();
        int recordNumber = eventIndex.getRecordNumber();
        
        if( eventNumber == 0 ){
            long position = this.recordPositions.get(0).getPosition();
            try {
                inputRecordStream.readRecord(inStreamRandom, position);
            } catch (HipoException ex) {
                Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println(String.format("*****>>>> read record # %5d, events = %8d", 
            //        0, inputRecordStream.getEntries()));
            eventIndex.advance();
        } else {
            //System.out.println(" event # = " + eventNumber + " record # = " + recordNumber);
            if(!eventIndex.canAdvance()){
                System.out.println("can no longer advance");
                return null;
            }
            if(eventIndex.advance()){
                int    record = eventIndex.getRecordNumber();
                long position = this.recordPositions.get(record).getPosition();
                try {
                    inputRecordStream.readRecord(inStreamRandom, position);
                    //System.out.println(String.format("*****>>>> read record # %5d, events = %8d", 
                    //        record, inputRecordStream.getEntries()));
                    
                } catch (HipoException ex) {
                    Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        int recordEventNumber = eventIndex.getRecordEventNumber();
        //System.out.println(" RecordEvent Number = " + recordEventNumber + "   entries " + inputRecordStream.getEntries());
        int         eventSize = inputRecordStream.getEventLength(recordEventNumber);
        event.require(eventSize);
        /*try {
            inputRecordStream.copyEvent(event.getEventBuffer(), 0, recordEventNumber);
        } catch (HipoException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        //eventIndex.advance();
        return event;
    }
    
    public final void buildDictionary(ByteBuffer buffer, int offset){
        try {
            inputRecordStream.readRecord(buffer, offset);
            byte[] dictionaryData = inputRecordStream.getEvent(0);
            Event dictionaryEvent = new Event();            
            dictionaryEvent.initFrom(dictionaryData);            
            int recordEntries = inputRecordStream.getEntries();
            //System.out.println("reader:: ***** dictionary read with entries = "+recordEntries);            
                    
            Node       schemaNode = new Node(120,1,DataType.STRING,20);
            for(int nevt = 0 ; nevt < recordEntries; nevt++){
                
                byte[] event = inputRecordStream.getEvent(nevt);
                dictionaryEvent.initFrom(event);
                //dictionaryEvent.show();
                int nodePosition = dictionaryEvent.scan(120, 1);
                schemaNode = dictionaryEvent.read(schemaNode, nodePosition);
                
                //schemaNode.show();
                //System.out.println("NODE TYPE = " + schemaNode.getType());                
                Schema schema = Schema.fromJsonString(schemaNode.getString());
                //schema.show();
                schemaFactory.addSchema(schema);
            }
            
        } catch (HipoException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public final void buildFileIndex(ByteBuffer buffer, int offset){
        try {
            inputRecordStream.readRecord(buffer, offset);
            byte[] trailerData = inputRecordStream.getEvent(0);
            Event trailerEvent = new Event();
            
            trailerEvent.initFrom(trailerData);
            Bank indexNode = HipoUtilsIO.createIndexNode();
            
            int scanIndex = trailerEvent.scan(indexNode.getSchema().getGroup(),
                    indexNode.getSchema().getItem());
            
            trailerEvent.read(indexNode, scanIndex);
            recordPositions.clear();
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
                //System.out.println(position);
            }
        } catch (HipoException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            if(debugMode>0){
                System.out.printf("reader:: ************ file size   : %12d\n",channelSize);
            }
            if(trailerPosition==0){
                System.out.println("\n\n");
                System.out.println("reader:: ****** warning : file does not appear to have an index.");
                System.out.println("reader:: ****** warning : please use HipoDoctor class to rebuild index.");
                System.out.println("reader:: ****** warning : or use hipoutils -doctor to re-index and write file.");
                System.out.println("\n\n");
                this.readerErrorCode = HipoReader.ERROR_NOINDEX;
                recordPositions.clear();
                return;
            }
            
            if((trailerPosition+54)>channelSize){
                System.out.println("\n\n");
                System.out.println("reader:: ****** warning : this file seems to be corrupt or not closed properly.");
                System.out.println("reader:: ****** warning : please use HipoDoctor class to salvage useful parts.");
                System.out.println("reader:: ****** warning : or use hipoutils -doctor to re-index and write file.");
                System.out.println("\n\n");
                this.readerErrorCode = HipoReader.ERROR_NOINDEX;
                recordPositions.clear();
                return;
            }
            //System.out.println("VERSION = " + evioVersion + "  TRAILER = " + fileHeader.getTrailerPosition());
            
            this.inputRecordStream.readRecord(inStreamRandom, trailerPosition);
            
            byte[] trailerData = inputRecordStream.getEvent(0);
            if(debugMode>0){
                System.out.printf("reader:: **** scan trailer #bytes : %12d\n",trailerData.length);
            }
            //System.out.println(" Number of events = " + inputRecordStream.getEntries() + " size = " + trailerData.length);
            Event trailerEvent = new Event();
            
            trailerEvent.initFrom(trailerData);
            Bank indexNode = HipoUtilsIO.createIndexNode();
            
            int scanIndex = trailerEvent.scan(indexNode.getSchema().getGroup(),
                    indexNode.getSchema().getItem());
            
            trailerEvent.read(indexNode, scanIndex);
            //trailerEvent.scan();
            //trailerEvent.show();
            //System.out.println(indexNode.nodeString());
            //indexNode.getSchema().show();
            recordPositions.clear();
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
                
            }
            
        } catch (IOException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HipoException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public int getEventCount(){
        //System.out.println("************************* REQUESTED NUMBER OF EVENTS " + numberOfEvents);
        return numberOfEvents;
    }
    
    public void close(){
        try {
            this.inStreamRandom.close();
        } catch (IOException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    

    @Override
    public boolean next(DataEvent event) {
        if(this.hasNext()==true){
            this.nextEvent((Event) event); return true;
        } 
        return false;
    }

    @Override
    public int position() {
        return this.eventIndex.getEventNumber();
    }

    @Override
    public boolean position(int pos) {
        this.eventIndex.setEvent(pos); return true;
    }

    @Override
    public int entries() {
        return this.getEventCount();
    }

    @Override
    public synchronized int nextFrame(DataFrame frame) {

        int frameSize = frame.getCount();
        int   counter = 0;
        
        for(int i = 0; i < frameSize; i++){
            Event event = (Event) frame.getEvent(i);
            if(this.hasNext()==true){
                this.nextEvent(event);
                counter++;
            } else {
                event.reset();
            }
        }
        return counter;        
    }
    
    public static void append(Event event, SchemaFactory factory, String output){
        File f = new File(output);
        if(f.exists()==true){
            HipoReader r = new HipoReader(output);           
            HipoWriter w = HipoWriter.create("tmp.h5", factory);
            Event e = new Event();
            while(r.next(e)) w.add(event);
            w.add(event);
            w.close();
            File f2 = new File("tmp.h5");
            f2.renameTo(f);            
        } else {
            HipoWriter w = HipoWriter.create(output, factory);
            w.add(event);
            w.close();
        }
    }
    
    @Override
    public boolean configure(){
        JFileChooser fileChooser = new JFileChooser();

        // Set a title for the dialog
        fileChooser.setDialogTitle("Select a File");

        // Add filters for .hipo and .h5 file extensions
        FileNameExtensionFilter hipoFilter = new FileNameExtensionFilter("HIPO Files (*.hipo)", "hipo");
        FileNameExtensionFilter h5Filter = new FileNameExtensionFilter("HDF5 Files (*.h5)", "h5");
        
        // Add filters to the file chooser
        fileChooser.addChoosableFileFilter(hipoFilter);
        fileChooser.addChoosableFileFilter(h5Filter);

        // Set one of the filters as the default
        fileChooser.setFileFilter(hipoFilter);

        // Show the open dialog
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            // Get the selected file
            File selectedFile = fileChooser.getSelectedFile();
            //System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            this.setDebugMode(1);
            this.open(selectedFile.getAbsolutePath());
        } else {
            System.out.println("File selection canceled.");
        }
        
        return true;
    }
    public static void main(String[] args){
        //String filename = "/Users/gavalian/Work/Software/project-7a.0.0/skim_calib_clas_005700.evio.00020.hipo_7.hipo";
       String file = "/Users/gavalian/Work/DataSpace/decoded/clas_006595.evio.00625-00629_unc.hipo";
       HipoReader r = new HipoReader(file);
       Event event = new Event();
       long size = 0L;
       long then = System.currentTimeMillis();
       while(r.hasNext()){
           r.nextEvent(event);
           size += event.getEventBuffer().getInt(4);
       }
       long now = System.currentTimeMillis();
       
       System.out.printf("size = %d, time = %d\n", size, now-then);
       HipoReader r2 = new HipoReader(file);

       size = 0L;
       then = System.currentTimeMillis();
       while(r2.hasNext()){
           ByteBuffer buffer = r2.nextEventByteBuffer();
           size += buffer.getInt(4);
       }
       now = System.currentTimeMillis();
       System.out.printf("size = %d, time = %d\n", size, now-then);
    }
}
