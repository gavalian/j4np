/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.data;

import j4np.hipo5.io.HipoReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class CompositeNode extends BaseHipoStructure {
    
    DataStructureDescriptor dataDescriptor = null; 
    
    public CompositeNode(){
        super();
    }
    
    public CompositeNode(int size){
        super(size);
    }
    
    public CompositeNode(String format){
        dataDescriptor = new DataStructureDescriptor();
        dataDescriptor.parse(format);
        require(dataDescriptor.getStructureLength()*200);
        
    }
    
    public CompositeNode(String format, int rows){
        
        dataDescriptor = new DataStructureDescriptor();
        dataDescriptor.parse(format);
        int rowLength = dataDescriptor.getStructureLength();
        
        int size = rowLength*rows + 16;
        require(size);        
    }
    
    public CompositeNode(int group, int item, String format, int rows){        
        dataDescriptor = new DataStructureDescriptor();
        dataDescriptor.parse(format);
        int rowLength = dataDescriptor.getStructureLength();
        int formatLength = format.length();
        int size = rowLength*rows + formatLength + 8;
        //System.out.printf(" ---- %d %d %d %d\n", rowLength, formatLength, size, 8);
        require(size, true);
        this.setGroup(group).setItem(item).setType(10).setFormatAndLength(format, rowLength*rows);
        this.setRows(0);
    }
    
    public void refactor(int group, int item, String format){
        dataDescriptor = new DataStructureDescriptor();
        dataDescriptor.parse(format);
        int formatLength = format.length();
        int rowLength = dataDescriptor.getStructureLength();
        int totalLength = this.getCapacity();
        int rows = (totalLength - 8 - formatLength)/rowLength;
        this.setGroup(group).setItem(item).setType(10).setFormatAndLength(format, rowLength*rows);
    }
    
    public final void setRows(int rows){
        int rowLength = dataDescriptor.getStructureLength();
        int      size = rowLength*rows ;
        int  nodeSize = size + this.getHeaderLength();
        if(nodeSize>this.structBuffer.array().length){
            byte[] buffer = new byte[nodeSize+128];
            int    length = this.getLength();
            System.arraycopy(this.structBuffer.array(), 0, buffer, 0, length);
            this.structBuffer = ByteBuffer.wrap(buffer);
            this.structBuffer.order(bufferOrder);
            this.setSizeWord(nodeSize);
        } else {
            this.setSizeWord(nodeSize);
        }
        //int rowLength = dataDescriptor.getStructureLength();
        //int size = rowLength*rows + this.getHeaderLength();
        //setSize(size);
    }
    
    public final int getRows(){
        int size = getSize();
        int rowLength = dataDescriptor.getStructureLength();
        return size/rowLength;
    }
    
    public int getEntries(){ return this.dataDescriptor.getEntries();}    
    public int getEntryType(int entry){ return this.dataDescriptor.getEntryType(entry);}

    public final void reset(){
        setRows(0);
    }
    
    protected void initFromBuffer(byte[] array, int position, int length){
        this.require(length);
        System.arraycopy(array, position, this.getByteBuffer().array(), 0, length);
        int   headerLength = this.getHeaderLength();
        byte[] formatBytes = new byte[headerLength];
        System.arraycopy(this.getByteBuffer().array(), 8, formatBytes, 0, headerLength);
        this.dataDescriptor = new DataStructureDescriptor();
        this.dataDescriptor.parse(new String(formatBytes));
    }
    
    public int getRowsSize(){
        return dataDescriptor.structureLength;
    }
    
    public int getRowOffset(int row){
        int offset = getDataOffset() + 
                row * dataDescriptor.structureLength; 
            return offset;
    }

    private boolean testTypeForEntry(int entry, int type){
        if(dataDescriptor.getEntryType(entry)==type) return true;
        //.warn("error : the type for entry " + entry + " is not " + type);
        System.out.println("error : the type for entry " + entry + " is not " + type);
        return false;
    }
    
    public CompositeNode putInt( int entry,int row, int number){
        if(testTypeForEntry(entry,3)==true){
            int offset = getDataOffset() + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
            structBuffer.putInt(offset, number);
        }
        return this;
    }
    
    public CompositeNode putLong(int entry, int row, long number){
        if(testTypeForEntry(entry,8)==true){
            int offset = getDataOffset() + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
            structBuffer.putLong(offset, number);
        }
        return this;
    }
    
    public CompositeNode putByte(int entry, int row, byte number){
        if(testTypeForEntry(entry,1)==true){
            int offset = getDataOffset() + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
            structBuffer.put(offset, number);
        }
        return this;
    }
    public CompositeNode putShort(int entry, int row,  short number){
        if(testTypeForEntry(entry,2)==true){
            int offset = getDataOffset() + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
            structBuffer.putShort(offset, number);
        }
        return this;
    }
    
    public CompositeNode putFloat(int entry, int row, float number){
        if(testTypeForEntry(entry,4)==true){
            int offset = getDataOffset() + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
            structBuffer.putFloat(offset, number);
        }
        return this;
    }
    
    public CompositeNode putDouble(int entry, int row, double number){
        if(testTypeForEntry(entry,5)==true){
            int offset = getDataOffset() + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
            structBuffer.putDouble(offset, number);
        }
        return this;
    }
    
    public int getInt( int entry, int row){
        int offset = getDataOffset() + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
        int type = dataDescriptor.getEntryType(entry);
        switch(type){
            case 1 : return (int) structBuffer.get(offset);
            case 2 : return (int) structBuffer.getShort(offset);
            case 3 : return (int) structBuffer.getInt(offset);            
            default: System.out.printf("structure::get : error expected type = %d,%d%d (type was %d)\n",
                    1,2,3,type); return 0;
        }
    }
    
    public short getShort( int entry, int row){
        int offset = getDataOffset() + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
        int type = dataDescriptor.getEntryType(entry);
        return  structBuffer.getShort(offset);            
    }
    
    protected byte getByte( int entry, int row){
        int offset = getDataOffset() + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
        int type = dataDescriptor.getEntryType(entry);
        return  structBuffer.get(offset);
    }
    
    public long getLong(int entry, int row){
        int offset = getDataOffset() + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
        int type = dataDescriptor.getEntryType(entry);
        if(type==8) return structBuffer.getLong(offset);
        
        System.out.printf("structure::get : error expected type = %d (type was %d)\n",8,type);
        return 0L;       
    }
    
    protected float getFloat( int entry, int row){
        int offset = getDataOffset() +
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
        int type = dataDescriptor.getEntryType(entry);
        return structBuffer.getFloat(offset);        
    }
    
    public double getDouble( int entry, int row){
        int offset = getDataOffset() +
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
        int type = dataDescriptor.getEntryType(entry);
        switch(type){
            case 4 : return (double) structBuffer.getFloat(offset);
            case 5 : return structBuffer.getDouble(offset);
            default: System.out.printf("structure::get : error\n"); return 0.0;
        }
    }
    
    @Override
    protected ByteBuffer getByteBuffer(){ return structBuffer; }

    public void show(){
        System.out.printf(" structure : size = %d, max rows = %d\n",structBuffer.capacity(), this.getMaxRows());
        this.dataDescriptor.show();
    }
    
    public String rowToString(int row){
        
        StringBuilder str = new StringBuilder();
        int nrows = this.getRows();

        //str.append(String.format("%3d : ", type));
        int nentries = this.getEntries();
        for(int entry = 0; entry < nentries; entry++){
            int type = this.getEntryType(entry);
            switch(type){
                case 1: str.append(String.format(" %8d", this.getInt(entry,row))); break;
                case 2: str.append(String.format(" %8d", this.getInt(entry,row))); break;
                case 3: str.append(String.format(" %8d", this.getInt( entry,row))); break;
                case 4: str.append(String.format(" %8.4f", this.getDouble(entry,row))); break;
                case 5: str.append(String.format(" %8.4f", this.getDouble(entry,row))); break;
                case 8: str.append(String.format(" %8d", this.getLong(entry,row))); break;
                default: break;
            }
        }
        return str.toString();
    }
    
    protected String columnToString(int entry){
        
        StringBuilder str = new StringBuilder();
        int nrows = this.getRows();
        int type = this.getEntryType(entry);
        str.append(String.format("%3d : ", type));
        for(int row = 0; row < nrows; row++){
            switch(type){
                case 1: str.append(String.format(" %8d", this.getInt(entry,row))); break;
                case 2: str.append(String.format(" %8d", this.getInt(entry,row))); break;
                case 3: str.append(String.format(" %8d", this.getInt( entry,row))); break;
                case 4: str.append(String.format(" %8.4f", this.getDouble(entry,row))); break;
                case 5: str.append(String.format(" %8.4f", this.getDouble(entry,row))); break;
                case 8: str.append(String.format(" %8d", this.getLong(entry,row))); break;
                default: break;
            }
        }
        return str.toString();
    }
    
    public int getMaxRows(){
        int length = this.getCapacity() - 8 - this.dataDescriptor.getEntries();
        return length/this.dataDescriptor.getStructureLength();         
    }
    
    public void print(){
        int    nrows = this.getRows();
        int nentries = this.getEntries();
        for(int row = 0; row < nrows; row++){
            System.out.println(this.rowToString(row));
        }
    }
    
    public void print(int row){
        System.out.println(this.rowToString(row));
    }
    
    /**
     * Descriptor class is used in data Structure to define
     * the format of the internal buffer.
     */
    
    public static class DataStructureDescriptor {
        
        private int[] dataLength = null;
        private int[]   dataType = null;
        private int[] dataOffset = null;
        private int   structureLength = 0;
        
        public DataStructureDescriptor(){
            
        }
                
        public void init(int[] entryType, int[] entryLength) {
            
            dataLength = new int[entryType.length];
            dataOffset = new int[entryType.length];
            dataType   = new int[entryType.length];
            
            int offset = 0;
            for(int i = 0; i < dataLength.length; i++){
                dataLength[i] = entryLength[i];
                dataOffset[i] = offset;
                dataType[i] = entryType[i];
                offset += entryLength[i];
            }
            structureLength = offset;
        }
        
        public String getTypeString(int type){
            switch(type){
                case 1: return "b";
                case 2: return "s";
                case 3: return "i";
                case 4: return "f";
                case 5: return "d";
                case 8: return "l";
                default: return "u";
            }
        }
        
        public int getType(char type){
            switch(type){
                case 'b' : return 1;
                case 'B' : return 1;
                case 's' : return 2;
                case 'S' : return 2;
                case 'i' : return 3;
                case 'I' : return 3;
                case 'f' : return 4;
                case 'F' : return 4;
                case 'd' : return 5;
                case 'D' : return 5;
                case 'l' : return 8;
                case 'L' : return 8;
                default: return 0;
            }
        }
        
        public int getTypeSize(char type){
            switch(type){
                case 'b' : return 1;
                case 'B' : return 1;
                case 's' : return 2;
                case 'S' : return 2;
                case 'i' : return 4;
                case 'I' : return 4;
                case 'f' : return 4;
                case 'F' : return 4;
                case 'd' : return 8;
                case 'D' : return 8;
                case 'l' : return 8;
                case 'L' : return 8;
                default: return 0;
            }
        }
        
        public int getTypeSize(int type){
            switch(type){
                case 1 : return 1;
                case 2 : return 2;
                case 3 : return 4;
                case 4 : return 4;
                case 5 : return 8;
                case 8 : return 8;
                default: return 0;
            }
        }
        
        public void parse(String format){
            
            List<Integer> dataTypes = new ArrayList<>();
            
            int size = format.length();
            int  pos = 0;
            //System.out.println("parsing => " + format);
            while(pos<size){
                char cc = format.charAt(pos);
                
                if(Character.isDigit(cc)==true){
                    //System.out.printf(" position %4d : numk = %2c\n",
                    //        pos,cc);                    
                    pos++;
                    char ff = format.charAt(pos);
                    int type = this.getType(ff);
                    
                    //System.out.printf(" letter = %s, type = %3d\n",ff,type);
                    if(type==0){
                        System.out.println("[parser] error at position "
                        + pos + ", unknown type ["+cc+"]");
                    } else {
                        int multiplicity = Integer.parseInt(Character.toString(cc));
                        //System.out.println("adding " + multiplicity + " types");
                        for(int j = 0; j < multiplicity; j++)
                            dataTypes.add(type);
                    }
                } else {                                       
                    int type = this.getType(cc);
                    //System.out.printf(" letter = %s, type = %3d\n",cc,type);
                    if(type==0){
                        System.out.println("[parser] error at position "
                        + pos + ", unknown type ["+cc+"]");
                    } else {
                        dataTypes.add(type);
                    }
                }
                pos++;
                //char 
            }
            
            int[] types = new int[dataTypes.size()];
            int[] lengths = new int[dataTypes.size()];
            
            for(int i = 0; i < types.length; i++){
                types[i] = dataTypes.get(i);
                lengths[i] = this.getTypeSize(types[i]);
                //System.out.println(" data " + i + " = " + dataTypes.get(i) + " size = " + lengths[i]);
            }
            
            this.init(types,lengths);
        }
        
        public int getEntries(){
            return dataLength.length;
        }
        
        public int getEntrySize(int entry){
            return dataLength[entry];
        }
        
        public int getEntryType(int entry){
            return dataType[entry];
        }
        public int getEntryOffset(int entry){
            return dataOffset[entry];
        }
                
        
        public int getStructureLength(){ return structureLength; }
                
        public void show(){
            
            System.out.printf(" structure : entries = %6d, length = %6d\n",
                    dataLength.length, structureLength);
            
            System.out.printf(" type      : ");
            for(int i = 0; i < dataType.length; i++){
                System.out.printf("%4s ",getTypeString(dataType[i]));
            }
            System.out.println();
            
            System.out.printf(" type      : ");
            for(int i = 0; i < dataType.length; i++){
                System.out.printf("%4d ",dataType[i]);
            }
            System.out.println();
                        
            System.out.printf(" length    : ");
            for(int i = 0; i < dataLength.length; i++){
                System.out.printf("%4d ",dataLength[i]);
            }
            System.out.println();
            System.out.printf(" offset    : ");
            for(int i = 0; i < dataLength.length; i++){
                System.out.printf("%4d ",dataOffset[i]);
            }
            System.out.println();
            
        }
    }
    
    
    public static CompositeNode random(int nrows){

        CompositeNode node = new CompositeNode(144,15,"bsssffl",nrows);
        int nentries = node.getEntries();
        for(int i = 0; i < nrows; i++){
            for(int e = 0; e < nentries; e++){
                int type = node.getEntryType(e);
                switch(type){
                    case 1: node.putByte(e,i,(byte) ( i+1)); break;
                    case 2: node.putShort(e,i, (short) (i+1)); break;
                    case 3: node.putInt(e,i, i+1); break;
                    case 4: node.putFloat(e,i, (float) (i+1)); break;
                    case 8: node.putLong(e,i, (i+1)*10); break;
                    default: break;
                }
            }
        }
        node.setRows(nrows);
        return node;
    }
    
    public void copyRow(CompositeNode node, int srcRow, int dstRow){
        
        int dstrows = this.getRows();
        int srcrows = node.getRows();
        if(dstRow>=this.getRows()) { 
            System.out.printf("comp-node::copy:: error, the destination has rows %d, requested %d\n",
                    dstrows, dstRow);
            return;
        }
        
        if(srcRow>=node.getRows()) { 
            System.out.printf("comp-node::copy:: error, the source has rows %d, requested %d, must be [0-%d]\n",
                    srcrows, srcRow, srcrows-1);
            return;
        }
        
        
        int rowLengthDst = this.getRowsSize();
        int rowLengthSrc = node.getRowsSize();
        if(rowLengthDst!=rowLengthSrc){
            System.out.printf("comp-node::copy:: error, the format inconsistency src row length = %d, dst row length = %d\n",
                    rowLengthSrc, rowLengthDst);
            return;
        }
        
        int rowOffsetDst = this.getRowOffset(dstRow);
        int rowOffsetSrc = node.getRowOffset(srcRow);
        System.arraycopy(node.structBuffer.array(), rowOffsetSrc,
                this.structBuffer.array(), rowOffsetDst, rowLengthSrc);
    }
    
    
    public boolean copyRows(CompositeNode b, int row, int length){
        int rowSize      = b.getRowsSize();
        int rowSizeLocal = this.getRowsSize();
        if(rowSize!=rowSizeLocal){
            System.out.printf("error:: composite node copyRows. incompatible size %6d %6d\n",
                    rowSize, rowSizeLocal);
            return false;
        }
        int currentRow = this.getRows();
        int offsetlocal = this.getRowOffset(currentRow);
        int offset = b.getRowOffset(row);
        int blen   = length*rowSize;
        //System.out.printf(" current row = %d, row offset local = %d, row offset = %d, length %d\n",
        //        currentRow, offsetlocal, offset, blen );        
        //System.out.printf("capacity = %d\n",this.getByteBuffer().limit());
        if(offsetlocal+blen>=this.getByteBuffer().capacity()){
            System.out.printf("error:: composite node copyRows. insufficient space copy length %d\n",
                    blen); return false;
        }
        
        System.arraycopy(b.getByteBuffer().array(), offset, getByteBuffer().array(),
                offsetlocal , blen);
        
        this.setRows(currentRow+length);
        return true;
    }
    
    
    public void copy(Bank b){
        int nentries = this.getEntries();        
        int    nrows = this.getRows();
        b.setRows(nrows);
        for(int row = 0; row < nrows; row++){
            for(int entry = 0; entry < nentries; entry++){
                int type = this.getEntryType(entry);
                switch(type){
                    case 1: b.putByte(entry, row, this.getByte(entry, row) ); break;
                    case 2: b.putShort(entry, row, this.getShort(entry, row) ); break;
                    case 3: b.putInt(entry, row, this.getInt(entry, row) ); break;
                    case 4: b.putFloat(entry, row, this.getFloat(entry, row) ); break;
                    default: break;
                }
            }
        }
    }
    
    public static void main(String[] args){
        
        CompositeNode node1 = CompositeNode.random(12);
        node1.show();
        node1.print();
        
        node1.refactor(11, 22, "bss");
        node1.setRows(3);
        node1.show();
        node1.print();
        
        
        CompositeNode node2 = new CompositeNode(1,1,"bbii",1024);
        
        System.out.println(node2.getMaxRows());
        System.out.println(node2.getCapacity());
        /*
        CompositeNode node1 = CompositeNode.random(12);
        
        CompositeNode node2 = CompositeNode.random(6);
        
        System.out.println("----------------------");
        node1.print();
        System.out.println("----------------------");
        node2.print();
        
        node1.setRows(10);
        System.out.println("----------------------");
        node2.show();
        node1.print();
        node1.copyRows(node2,0,6);
        System.out.println("----------------------");
        node1.print();
        
        */
        /*
        node1.copyRow(node2, 5, 0);
        node1.copyRow(node2, 4, 2);        
        node1.copyRow(node2, 6, 2);
        
        System.out.println("----------------------");
        node1.print();*/
        /*
        Event e = new Event();
        Node n1 = new Node(5,1,new float[15]);
        HipoReader r = new HipoReader("/Users/gavalian/Work/DataSpace/trigger/clas_005630.h5_000000_daq.h5");
        
        CompositeNode n = new CompositeNode(5,5,"f",12);
        
        n.show();
        n.info();
        n.print();
        
        n.setRows(7);
        for(int i = 0; i < 7; i++) n.putFloat(0, i, (float) (i*0.1));
        n.show();
        n.info();
        n.print();
        
        System.out.println(n.getLength());
        
        e.write(n1);
        e.scanShow();
        e.write(n);
        
        e.scanShow();
                
        r.getEvent(e, 25);
        
        Event evt = new Event();
        evt.copyFrom(e);
        
        evt.scanShow();
        
        evt.write(n);
        
        evt.scanShow();*/
        
        /*
        CompositeNode struct = new CompositeNode(12,1,"ssb",5);
        for(int i = 0; i < 5; i++) struct.putShort(i, 1, (short) ((i+1)*4));
        struct.info();
        
        int   size = struct.getSize();
        int length = struct.getDataLength();
        
        System.out.println("size = " + size + "  data length = " + length);
        struct.show();
        
        
        int entries = struct.getEntries();
        int    rows = struct.getRows();
        System.out.println(" entries = " + entries + " rows = " + rows);
        for(int r = 0; r < rows; r++){
            System.out.printf("%3d %8d\n",r,struct.getInt(r, 1));
        }*/
        /*DataStructureDescriptor desc = new DataStructureDescriptor();
        desc.init(new int[]{4,4,2,2,1,1},new int[]{4,4,2,2,1,1});
        desc.show();        
        desc.parse("3b3f2i");        
        desc.show();*/        
        /*
        DataStructure struct = new DataStructure("2bs8fi",40);
        struct.show();
        
        for(int i = 0; i < 8 ; i++) {
            struct.putByte(  i, 0, (byte) (i+4));
            struct.putFloat( i, 4, (float) ((i+4)/15.0));
        }
        
        struct.setRows(8);
        struct.info();
        DataStructureUtils.print(struct, new int[]{0,2,4,5});
        */
    }
}
