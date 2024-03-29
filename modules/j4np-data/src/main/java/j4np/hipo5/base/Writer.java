/*
 *   Copyright (c) 2016.  Jefferson Lab (JLab). All rights reserved. Permission
 *   to use, copy, modify, and distribute  this software and its documentation for
 *   educational, research, and not-for-profit purposes, without fee and without a
 *   signed licensing agreement.
 */

package j4np.hipo5.base;

//import org.jlab.coda.jevio.*;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import j4np.hipo5.base.Reader.RecordPosition;

/**
 * Class to write Evio/HIPO files.
 *
 * @version 6.0
 * @since 6.0 8/10/17
 * @author gavalian
 * @author timmer
 */

public class Writer implements AutoCloseable {

    /** Do we write to a file or a buffer? */
    private boolean toFile = true;

    // If writing to file ...

    /** Object for writing file. */
    private RandomAccessFile  outStream;
    /** The file channel, used for writing a file, derived from outStream. */
    private FileChannel  fileChannel;
    /** Header to write to file. */
    private FileHeader  fileHeader;

    // If writing to buffer ...
    
    /** The buffer being written to. */
    private ByteBuffer buffer;

    // For both files & buffers

    /** String containing evio-format XML dictionary to store in file header's user header. */
    private String dictionary;
    /** Evio format first event to store in file header's user header. */
    private byte[] firstEvent;
    /** Byte order of data to write to file/buffer. */
    private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
    /** Internal Record. */
    private RecordOutputStream  outputRecord;
    /** Byte array large enough to hold a header/trailer. */
    private byte[]  headerArray = new byte[RecordHeader.HEADER_SIZE_BYTES];
    /** Type of compression to use on file. Default is none. */
    private int    compressionType;
    /** Number of bytes written to file/buffer at current moment. */
    private long   writerBytesWritten;
    /** Number which is incremented and stored with each successive written record starting at 1. */
    private int   recordNumber = 1;
    /** Do we add a last header or trailer to file/buffer? */
    private boolean addTrailer;
    /** Do we add a record index to the trailer? */
    private boolean addTrailerIndex;

    /** List of record lengths interspersed with record event counts
     * to be optionally written in trailer. */
    private ArrayList<Integer> recordLengths = new ArrayList<Integer>(1500);
    
    
    private final List<RecordPosition> writerRecordPositions = new ArrayList<RecordPosition>(); 
    /**
     * Default constructor.
     * <b>No</b> file is opened. Any file will have little endian byte order.
     */
    
    private boolean reWriteFile = false;
    
    public Writer(){
        outputRecord = new RecordOutputStream();
        fileHeader = new FileHeader(true);
    }
    
    /**
     * Constructor with byte order. <b>No</b> file is opened.
     * File header type is evio file ({@link HeaderType#EVIO_FILE}).
     * @param order byte order of written file. Little endian if null.
     * @param maxEventCount max number of events a record can hold.
     *                      Value of O means use default (1M).
     * @param maxBufferSize max number of uncompressed data bytes a record can hold.
     *                      Value of < 8MB results in default of 8MB.
     */
    public Writer(ByteOrder order, int maxEventCount, int maxBufferSize){
        this(HeaderType.EVIO_FILE, order, maxEventCount, maxBufferSize, null, null);
    }

    /**
     * Constructor with byte order and header type. <b>No</b> file is opened.
     * @param hType the type of the file. If set to {@link HeaderType#HIPO_FILE},
     *              the header will be written with the first 4 bytes set to HIPO.
     * @param order byte order of written file. Little endian if null.
     * @param maxEventCount max number of events a record can hold.
     *                      Value of O means use default (1M).
     * @param maxBufferSize max number of uncompressed data bytes a record can hold.
     *                      Value of < 8MB results in default of 8MB.
     */
    public Writer(HeaderType hType, ByteOrder order, int maxEventCount, int maxBufferSize) {
        this(hType, order, maxEventCount, maxBufferSize, null, null);
    }

    /**
     * Constructor with byte order. <b>No</b> file is opened.
     * This method places the dictionary and first event into the file header's user header.
     *
     * @param hType the type of the file. If set to {@link HeaderType#HIPO_FILE},
     *              the header will be written with the first 4 bytes set to HIPO.
     * @param order byte order of written file. Little endian if null.
     * @param maxEventCount max number of events a record can hold.
     *                      Value of O means use default (1M).
     * @param maxBufferSize max number of uncompressed data bytes a record can hold.
     *                      Value of < 8MB results in default of 8MB.
     * @param dictionary    string holding an evio format dictionary
     * @param firstEvent    byte array containing an evio first event.
     *                      It must be in the same byte order as the order argument.
     */
    public Writer(HeaderType hType, ByteOrder order, int maxEventCount, int maxBufferSize,
                  String dictionary, byte[] firstEvent) {
        if (order != null) {
            byteOrder = order;
        }
        this.dictionary = dictionary;
        this.firstEvent = firstEvent;
        outputRecord = new RecordOutputStream(order, maxEventCount, maxBufferSize, 1);
        if(hType==HeaderType.HIPO_FILE){
            fileHeader = new FileHeader(false);
        } else {
            fileHeader = new FileHeader(true);
        }
    }

    /**
     * Constructor with filename.
     * The output file will be created with no user header.
     * File byte order is little endian.
     * @param filename output file name
     */
    public Writer(String filename){
        this();
        open(filename);
    }

    /**
     * Constructor with filename & byte order.
     * The output file will be created with no user header. LZ4 compression.
     * @param filename      output file name
     * @param order         byte order of written file or null for default (little endian)
     * @param maxEventCount max number of events a record can hold.
     *                      Value of O means use default (1M).
     * @param maxBufferSize max number of uncompressed data bytes a record can hold.
     *                      Value of < 8MB results in default of 8MB.
     */
    public Writer(String filename, ByteOrder order, int maxEventCount, int maxBufferSize){
        this(order, maxEventCount, maxBufferSize);
        open(filename);
    }

    /**
     * Constructor for writing to a ByteBuffer. Byte order is taken from the buffer.
     * LZ4 compression.
     * @param buf buffer in to which to write events and/or records.
     * @param maxEventCount max number of events a record can hold.
     *                      Value of O means use default (1M).
     * @param maxBufferSize max number of uncompressed data bytes a record can hold.
     *                      Value of < 8MB results in default of 8MB.
     */
    public Writer(ByteBuffer buf, int maxEventCount, int maxBufferSize) {
        buffer = buf;
        byteOrder = buf.order();
        outputRecord = new RecordOutputStream(byteOrder, maxEventCount, maxBufferSize, 1);
    }

    /**
     * Get the file's byte order.
     * @return file's byte order.
     */
    public ByteOrder getByteOrder() {return byteOrder;}

    /**
     * Get the file header.
     * @return file header.
     */
    public FileHeader getFileHeader() {return fileHeader;}

    /**
     * Get the internal record's header.
     * @return internal record's header.
     */
    public RecordHeader getRecordHeader() {return outputRecord.getHeader();}

    /**
     * Get the internal record used to add events to file.
     * @return internal record used to add events to file.
     */
    public RecordOutputStream getRecord() {return outputRecord;}

    /**
     * Does this writer add a trailer to the end of the file?
     * @return true if this writer adds a trailer to the end of the file, else false.
     */
    public boolean addTrailer() {return addTrailer;}

    /**
     * Set whether this writer adds a trailer to the end of the file.
     * @param addTrailer if true, at the end of file, add an ending header (trailer)
     *                   with no index of records and no following data.
     *                   Update the file header to contain a file offset to the trailer.
     */
    public void addTrailer(boolean addTrailer) {this.addTrailer = addTrailer;}

    /**
     * Does this writer add a trailer with a record index to the end of the file?
     * @return true if this writer adds a trailer with a record index
     *         to the end of the file, else false.
     */
    public boolean addTrailerWithIndex() {return addTrailerIndex;}

    /**
     * Set whether this writer adds a trailer with a record index to the end of the file.
     * @param addTrailingIndex if true, at the end of file, add an ending header (trailer)
     *                         with an index of all records but with no following data.
     *                         Update the file header to contain a file offset to the trailer.
     */
    public void addTrailerWithIndex(boolean addTrailingIndex) {
        addTrailerIndex = addTrailingIndex;
        if (addTrailingIndex) {
            addTrailer = true;
        }
    }
    
    protected void checkFileExistsAndExit(String filename){
       File file = new File(filename);
       if(file.exists()==true){
           System.out.println("\n\n\n");
           System.out.println("*************************************************************************\n");
           System.out.println("Unfortunatelly the file that you're trying to open already exists.");
           System.out.println("File Name : " + filename + "\n");
           System.out.println("What you need to do is : delete the file, then run this same program again.");
           System.out.println("Warning: think twice, (in some cases three times) before deleting a file.");
           System.out.println("\n*************************************************************************");
           System.out.println("\n\n\n");
           System.exit(0);
       }
    }
    protected void checkFileExistsAndDelete(String filename){
       File file = new File(filename);
       if(file.exists()==true){
           boolean status = file.delete();
           if(status==true){
               System.out.printf("\n WARNING : deleting file : %s\n",
                       filename);
           } else {
               System.out.printf("\n\n ERROR : error deleting file : %s\n\n exiting \n\n",
                       filename);
               System.exit(0);
           }
       }
    }
    /**
     * sets the flag that makes writer to exists if the output file exists.
     * @param flag
     * @return 
     */
    public Writer setRewriteMode(boolean flag){
        this.reWriteFile = flag; return this;        
    }
    /**
     * Open a new file and write file header with no user header.
     * @param filename output file name
     */
    public final void open(String filename) {open(filename, new byte[]{});}

    /**
     * Open a file and write file header with given user's header.
     * User header is automatically padded when written.
     * @param filename disk file name.
     * @param userHeader byte array representing the optional user's header.
     *                   If this is null AND dictionary and/or first event are given,
     *                   the dictionary and/or first event will be placed in its
     *                   own record and written as the user header.
     */
    public final void open(String filename, byte[] userHeader){

        if(reWriteFile==true){
           checkFileExistsAndDelete(filename);
        } else {
            checkFileExistsAndExit(filename);
        }
        
        if (userHeader == null) {
            // If dictionary and firstEvent not defined ...
            if (dictionary == null && firstEvent == null) {
                userHeader = new byte[0];
            }
            // Else place dictionary and/or firstEvent into
            // record which becomes user header
            else {
                userHeader = createDictionaryRecord();
            }
        }

        try {
            outStream = new RandomAccessFile(filename, "rw");
            fileChannel = outStream.getChannel();
            // Create complete file header here (general file header + index array + user header)
            ByteBuffer headerBuffer = createHeader(userHeader);
            // Write this to file
            outStream.write(headerBuffer.array());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        }

        writerBytesWritten = (long) (fileHeader.getLength());
    }

    /**
     * Create a byte array representation of record
     * containing dictionary and/or first event.
     * No compression.
     * @return byte array representation of record
     *         containing dictionary and/or first event.
     *         Null if both are null.
     */
    private byte[] createDictionaryRecord() {
        if (dictionary == null && firstEvent == null) return null;

        // Create record.
        // Bit of chicken&egg problem, so start with default internal buf size.
        RecordOutputStream record = new RecordOutputStream(byteOrder, 2, 0, 0);

        // How much data we got?
        int bytes=0;
        if (dictionary != null) {
            bytes += dictionary.length();
        }
        if (firstEvent != null) {
            bytes += firstEvent.length;
        }

        // If we have huge dictionary/first event ...
        if (bytes > record.getInternalBufferCapacity()) {
            record = new RecordOutputStream(byteOrder, 2, bytes, 0);
        }

        // Add events to record
        if (dictionary != null) {
            try {record.addEvent(dictionary.getBytes("US-ASCII"));}
            catch (UnsupportedEncodingException e) {/* never happen */}
        }
        if (firstEvent != null) {
            record.addEvent(firstEvent);
        }

        // Make events into record. Pos = 0, limit = # valid bytes.
        record.build();
        // Buffer contains record data
        ByteBuffer buf = record.getBinaryBuffer();
        // Return array representation of record
        byte[] rec = new byte[buf.limit()];

        if (buf.hasArray()) {
            // Backing array may be bigger
            System.arraycopy(buf.array(), buf.arrayOffset(), rec, 0, rec.length);
        }

        // Buffer is ready to read from record.build()
        buf.get(rec);

        return rec;
    }
    
    /**
     * Convenience method that sets compression type for the file.
     * The compression type is also set for internal record.
     * When writing to the file, record data will be compressed
     * according to the given type.
     * @param compression compression type
     * @return this object
     */
    public final Writer setCompressionType(int compression){
        outputRecord.getHeader().setCompressionType(compression);
        compressionType = outputRecord.getHeader().getCompressionType();
        return this;
    }

    public int getCompressionType(){
        return outputRecord.getHeader().getCompressionType();
    }
    /**
     * Create and return a byte array containing a general file header
     * followed by the user header given in the argument.
     * If user header is not padded to 4-byte boundary, it's done here.
     * @param userHeader byte array containing a user-defined header
     * @return byte array containing a file header followed by the user-defined header
     */
    public ByteBuffer createHeader(byte[] userHeader){
        // Amount of user data in bytes
        int userHeaderBytes = userHeader.length;

        fileHeader.reset();
        fileHeader.setUserHeaderLength(userHeaderBytes);

        byte[] array = new byte[fileHeader.getLength()];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.order(byteOrder);

        try {
            fileHeader.writeHeader(buffer, 0);
        }
        catch (HipoException e) {/* never happen */}
        
        System.arraycopy(userHeader, 0, array,
                         RecordHeader.HEADER_SIZE_BYTES, userHeaderBytes);

        return buffer;
    }

    /**
     * Write a general header as the last "header" or trailer in the file
     * optionally followed by an index of all record lengths.
     * @param writeIndex if true, write an index of all record lengths in trailer.
     */
    public void writeTrailer(boolean writeIndex){

        // If we're NOT adding a record index, just write trailer
        if (!writeIndex) {
            try {
                FileHeader.writeTrailer(headerArray, recordNumber, byteOrder, null);
                // TODO: not really necessary to keep track here?
                writerBytesWritten += RecordHeader.HEADER_SIZE_BYTES;
                outStream.write(headerArray, 0, RecordHeader.HEADER_SIZE_BYTES);
            }
            catch (HipoException ex) {
                // never happen
            }
            catch (IOException ex) {
                Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }

        // Create the index of record lengths & entries in proper byte order
        byte[] recordIndex = new byte[4*recordLengths.size()];
        try {
            for (int i = 0; i < recordLengths.size(); i++) {
                ByteDataTransformer.toBytes(recordLengths.get(i), byteOrder,
                                            recordIndex, 4*i);
//System.out.println("Writing record length = " + recordOffsets.get(i) +
//", = 0x" + Integer.toHexString(recordOffsets.get(i)));
            }
        }
        catch (HipoException e) {/* never happen */}

        // Write trailer with index

        // How many bytes are we writing here?
        int dataBytes = RecordHeader.HEADER_SIZE_BYTES + recordIndex.length;

        // Make sure our array can hold everything
        if (headerArray.length < dataBytes) {
//System.out.println("Allocating byte array of " + dataBytes + " bytes in size");
            headerArray = new byte[dataBytes];
        }

        try {
            // Place data into headerArray - both header and index
        FileHeader.writeTrailer(headerArray, recordNumber,
                                  byteOrder, recordIndex);
            // TODO: not really necessary to keep track here?
            writerBytesWritten += dataBytes;
            outStream.write(headerArray, 0, dataBytes);
        }
        catch (HipoException ex) {
            // never happen
        }
        catch (IOException ex) {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return;
    }

    /**
     * Appends the record to the file.Using this method in conjunction with 
     * addEvent() is not thread-safe.
     * @param record record object
     * @return the number of bytes written to the stream
     */
    public int writeRecord(RecordOutputStream record) {
        
        RecordHeader header = record.getHeader();

        // Make sure given record is consistent with this writer
        header.setCompressionType(compressionType);
        header.setRecordNumber(recordNumber++);
        //System.out.println( " set compresstion type = " + compressionType);
        record.getHeader().setCompressionType(compressionType);
        record.setByteOrder(byteOrder);

        record.build();
        int bytesToWrite = header.getLength();
        // Record length of this record
        recordLengths.add(bytesToWrite);
        // Followed by events in record
        recordLengths.add(header.getEntries());
        writerBytesWritten += bytesToWrite;
        
        try {
            long  outputPosition = outStream.getChannel().position();
            int    recordEntries = header.getEntries();
            long     userLongOne = header.getUserRegisterFirst();
            long     userLongTwo = header.getUserRegisterSecond();
            
            RecordPosition recordInfo = new RecordPosition(outputPosition, bytesToWrite, recordEntries);
            recordInfo.setUserWordOne(userLongOne).setUserWordTwo(userLongTwo);
            this.writerRecordPositions.add(recordInfo);
            outStream.write(record.getBinaryBuffer().array(), 0, bytesToWrite);
        } catch (IOException ex) {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bytesToWrite;
    }

    // Use internal outputRecordStream to write individual events

    /**
     * Add a byte array to the internal record. If the length of
     * the buffer exceeds the maximum size of the record, the record
     * will be written to the file (compressed if the flag is set).
     * Internal record will be reset to receive new buffers.
     * Using this method in conjunction with writeRecord() is not thread-safe.
     *
     * @param buffer array to add to the file.
     * @param offset offset into array from which to start writing data.
     * @param length number of bytes to write from array.
     */
    public void addEvent(byte[] buffer, int offset, int length){
        boolean status = outputRecord.addEvent(buffer, offset, length);
        if(!status){
            writeOutput();
            outputRecord.addEvent(buffer, offset, length);
        }
    }

    /**
     * Add a byte array to the internal record. If the length of
     * the buffer exceeds the maximum size of the record, the record
     * will be written to the file (compressed if the flag is set).
     * Internal record will be reset to receive new buffers.
     * Using this method in conjunction with writeRecord() is not thread-safe.
     *
     * @param buffer array to add to the file.
     */
    public void addEvent(byte[] buffer){
        addEvent(buffer,0,buffer.length);
    }
    
    /** Write internal record with incremented record # to file. */
    private void writeOutput(){
        
        RecordHeader header = outputRecord.getHeader();
        header.setRecordNumber(recordNumber++);
        // --- Added on SEP 21 - gagik
        header.setCompressionType(compressionType);
        outputRecord.build();
        int bytesToWrite = header.getLength();
        /*System.out.println(" RECORD # " + header.getRecordNumber() + " RAW SIZE = " 
                + header.getDataLength()
                + "  SIZE = " + header.getLength()
                + "  # EVENTS " + header.getEntries() );*/
//int wordsToWrite = bytesToWrite/4;
        //int remainder    = bytesToWrite%4;
        // Record length of this record
        recordLengths.add(bytesToWrite);
        // Followed by events in record
        recordLengths.add(header.getEntries());
        writerBytesWritten += bytesToWrite;
        
        //----> to add
        
        //System.out.println(" bytes to write = " + bytesToWrite);
        try {
            long  outputPosition = outStream.getChannel().position();
            int    recordEntries = header.getEntries();
            long     userLongOne = header.getUserRegisterFirst();
            long     userLongTwo = header.getUserRegisterSecond();
            
            RecordPosition recordInfo = new RecordPosition(outputPosition, bytesToWrite, recordEntries);
            recordInfo.setUserWordOne(userLongOne).setUserWordTwo(userLongTwo);
            this.writerRecordPositions.add(recordInfo);
            if (outputRecord.getBinaryBuffer().hasArray()) {
                outStream.write(outputRecord.getBinaryBuffer().array(), 0, bytesToWrite);
            }
            else {
                // binary buffer is ready to read after build()
                fileChannel.write(outputRecord.getBinaryBuffer());
            }
            outputRecord.reset();
        } catch (IOException ex) {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //---------------------------------------------------------------------

    /** Get this object ready for re-use.
     * Follow calling this with call to {@link #open(String)}. */
    public void reset() {
        outputRecord.reset();
        fileHeader.reset();
        writerBytesWritten = 0L;
        recordNumber = 1;
        addTrailer = false;
    }
    
    public void purge(){
        if (outputRecord.getEventCount() > 0) {
            writeOutput();
        }
    }
    
    public List<Integer>  getRecordLengths(){
       return this.recordLengths; 
    }
    
    public List<RecordPosition> getRecordPositions(){
        return this.writerRecordPositions;
    }
    
    public void writeLastRecord(RecordOutputStream record){
        
        long trailerPosition = writerBytesWritten;
        int entries = record.getEventCount();
        RecordHeader header = record.getHeader();
        header.setRecordNumber(recordNumber++);
        // --- Added on SEP 21 - gagik
        header.setCompressionType(compressionType);
        record.build();
        int bytesToWrite = header.getLength();
        writerBytesWritten += bytesToWrite;
        
        System.out.println(String.format("Writer<5>: Last Record => pos = %5d, entries = %5d ", 
                trailerPosition, entries));
        
        try {
            if (record.getBinaryBuffer().hasArray()) {
                outStream.write(record.getBinaryBuffer().array(), 0, bytesToWrite);
            }
            else {
                // binary buffer is ready to read after build()
                fileChannel.write(record.getBinaryBuffer());
            }
            record.reset();
            
            
            outStream.seek(FileHeader.TRAILER_POSITION_OFFSET);
            if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
                outStream.writeLong(Long.reverseBytes(trailerPosition));
            }
            else {
                outStream.writeLong(trailerPosition);
            }
            
            // Find & update file header's bit-info word
            if (addTrailerIndex) {
                outStream.seek(RecordHeader.BIT_INFO_OFFSET);
                int bitInfo = fileHeader.setBitInfo(false, false, true);
                if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
                    outStream.writeInt(Integer.reverseBytes(bitInfo));
                }
                else {
                    outStream.writeInt(bitInfo);
                }
            }            
        } catch (IOException ex) {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Close opened file. If the output record contains events,
     * they will be flushed to file. Trailer and its optional index
     * written if requested.
     */
    @Override
    public void close(){
       
        try {
            outStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
            /*if (outputRecord.getEventCount() > 0) {
            writeOutput();
            }*/
            /*purge();
            try {
            if (addTrailer) {
            // Keep track of where we are right now which is just before trailer
            long trailerPosition = writerBytesWritten;
            
            // Write the trailer
            writeTrailer(addTrailerIndex);
            
            // Find & update file header's trailer position word
            outStream.seek(FileHeader.TRAILER_POSITION_OFFSET);
            if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            outStream.writeLong(Long.reverseBytes(trailerPosition));
            }
            else {
            outStream.writeLong(trailerPosition);
            }
            
            // Find & update file header's bit-info word
            if (addTrailerIndex) {
            outStream.seek(RecordHeader.BIT_INFO_OFFSET);
            int bitInfo = fileHeader.setBitInfo(false, false, true);
            if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            outStream.writeInt(Integer.reverseBytes(bitInfo));
            }
            else {
            outStream.writeInt(bitInfo);
            }
            }
            }
            
            outStream.close();
            //System.out.println("[writer] ---> bytes written " + writerBytesWritten);
            } catch (IOException ex) {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        
    }
}
