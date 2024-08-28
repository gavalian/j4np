/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.io;

import j4np.hipo5.base.FileHeader;
import j4np.hipo5.base.HipoException;
import j4np.hipo5.base.RecordHeader;
import j4np.hipo5.base.RecordInputStream;
import j4np.hipo5.data.DataType;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.SchemaFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author gavalian
 */
public class HipoDoctor {

    protected RandomAccessFile inStreamRandom;
    protected FileHeader fileHeader;
     private SchemaFactory  schemaFactory = new SchemaFactory();
     HipoWriter writer = new HipoWriter();
    protected final RecordInputStream inputRecordStream = new RecordInputStream();
    public HipoDoctor(){
        
    }
    
    private void open(String filename){
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
            Node       schemaNode = new Node(120,1,DataType.STRING,20);
            for(int nevt = 0 ; nevt < recordEntries; nevt++){
                
                byte[] event = dictionaryRecord.getEvent(nevt);
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
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HipoDoctor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HipoDoctor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HipoException ex) {
            Logger.getLogger(HipoDoctor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void cure(String filename){
        HipoDoctor doctor = new HipoDoctor();
        //doctor.scan(filename);
        doctor.scanCure(filename);
    }
    
    public void scanCure(String filename){
        try {
            open(filename);
            writer.getSchemaFactory().copy(schemaFactory);
            writer.getSchemaFactory().show();
            String outputFile = filename + "_fixed";
            writer.open(outputFile);
            byte[] headerBytes = new byte[RecordHeader.HEADER_SIZE_BYTES];
            ByteBuffer headerBuffer = ByteBuffer.wrap(headerBytes);
            headerBuffer.order(ByteOrder.LITTLE_ENDIAN);
            FileChannel channel = inStreamRandom.getChannel().position(0L);
            long   nChannelSize = channel.size();
            
            int n = inStreamRandom.read(headerBytes);
            //byte[] inBuffer = new byte[56];
            
            long trailerPosition = headerBuffer.getLong(40);
            int userHeaderPosition = headerBuffer.getInt(2*4)*4;
            int userHeaderLength   = headerBuffer.getInt(6*4);
            long recordPosition    = userHeaderPosition + userHeaderLength;
            int eventsRecovered = 0;
            //System.out.printf("     trailer position = %d\n",trailerPosition);
            //System.out.printf("first record position = %d\n",recordPosition);
            //System.out.printf(" user header position = %d\n",userHeaderPosition);
            //System.out.printf(" user header   length = %d\n",userHeaderLength);
            this.inputRecordStream.readRecord(inStreamRandom, recordPosition);
            int nEvents = this.inputRecordStream.getEntries();
            Event event = new Event();
            //System.out.println("RECORD Entries # " + nEvents);
            for(int k = 0; k < nEvents; k++){
                eventsRecovered++;
                int   eventSize = inputRecordStream.getEventLength(k);
                event.require(eventSize);
                inputRecordStream.copyEvent(event.getEventBuffer(), 0, k);
                writer.addEvent(event);
            }
            boolean   doContinue = true;
            int     recordNumber = 0;
            while(doContinue==true){
                channel = inStreamRandom.getChannel().position(recordPosition);
                n = inStreamRandom.read(headerBytes);
                if(n==headerBytes.length){
                    int recordLengthWord = headerBuffer.getInt(9*4);
                    int recordLength     = 4*(0x00FFFFFF&recordLengthWord) + 56;
                    int magicWord        = headerBuffer.getInt(7*4);
                    //System.out.printf("RECORD # %8d WORD %10X : length = %12d, position %16d\n",
                    //        recordNumber,magicWord, recordLength, recordPosition);
                    recordPosition += recordLength;
                    
                    if(recordPosition>nChannelSize){
                        System.out.printf("Record # %d is corrupt\n",recordNumber);
                        doContinue = false;
                    }
                    recordNumber++;
                    try {
                        this.inputRecordStream.readRecord(inStreamRandom, recordPosition);
                        nEvents = this.inputRecordStream.getEntries();
                        
                        //System.out.println("RECORD Entries # " + nEvents);
                        for(int k = 0; k < nEvents; k++){
                            eventsRecovered++;
                            int   eventSize = inputRecordStream.getEventLength(k);
                            event.require(eventSize);
                            inputRecordStream.copyEvent(event.getEventBuffer(), 0, k);
                            writer.addEvent(event);
                        }
                    } catch (Exception e){
                        System.out.println("doctor:: unable to decompress the record.");
                        doContinue = false;
                    }
                } else {
                    doContinue = false;
                }
            }
            writer.close();
            System.out.printf("[HiPO-Doctor]  events recovered : %d\n",eventsRecovered);
            System.out.printf("[HiPO-Doctor] records recovered : %d\n",recordNumber);
            System.out.printf("[HiPO-Doctor]  cured file saved : %s\n\n",outputFile);

        } catch (IOException ex) {
            Logger.getLogger(HipoDoctor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HipoException ex) {
            Logger.getLogger(HipoDoctor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void scan(String filename){
        try {
            open(filename);
            byte[] headerBytes = new byte[RecordHeader.HEADER_SIZE_BYTES];
            ByteBuffer headerBuffer = ByteBuffer.wrap(headerBytes);
            headerBuffer.order(ByteOrder.LITTLE_ENDIAN);
            FileChannel channel = inStreamRandom.getChannel().position(0L);
            long   nChannelSize = channel.size();
            
            int n = inStreamRandom.read(headerBytes);
            //byte[] inBuffer = new byte[56];
            
            long trailerPosition = headerBuffer.getLong(40);
            int userHeaderPosition = headerBuffer.getInt(2*4)*4;
            int userHeaderLength   = headerBuffer.getInt(6*4);
            long recordPosition    = userHeaderPosition + userHeaderLength;
            
            System.out.printf("     trailer position = %d\n",trailerPosition);
            System.out.printf("first record position = %d\n",recordPosition);
            System.out.printf(" user header position = %d\n",userHeaderPosition);
            System.out.printf(" user header   length = %d\n",userHeaderLength);
            this.inputRecordStream.readRecord(inStreamRandom, recordPosition);
            boolean   doContinue = true;
            int     recordNumber = 0;
            while(doContinue==true){
                channel = inStreamRandom.getChannel().position(recordPosition);
                n = inStreamRandom.read(headerBytes);
                if(n==headerBytes.length){
                    int recordLengthWord = headerBuffer.getInt(9*4);
                    int recordLength     = 4*(0x00FFFFFF&recordLengthWord) + 56;
                    int magicWord        = headerBuffer.getInt(7*4);
                    System.out.printf("RECORD # %8d WORD %10X : length = %12d, position %16d\n",
                            recordNumber,magicWord, recordLength, recordPosition);
                    recordPosition += recordLength;
                    
                    if(recordPosition>nChannelSize){
                        System.out.printf("Record # %d is corrupt\n",recordNumber);
                        doContinue = false;
                    }
                    recordNumber++;
                } else {
                    doContinue = false;
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(HipoDoctor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HipoException ex) {
            Logger.getLogger(HipoDoctor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args){
        String filename = "/Users/gavalian/Work/dataspace/corrupt/halldfdc_005_fixed.h5";
        if(args.length>0){
            filename = args[0];
        }
        
        System.out.println("scanning file : " + filename);
        HipoDoctor doctor = new HipoDoctor();
        //doctor.scan(filename);
        doctor.scanCure(filename);
    }
}
