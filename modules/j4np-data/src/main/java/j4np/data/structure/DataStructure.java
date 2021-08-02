/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.structure;

import com.sun.tools.sjavac.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DataStructure {
    
    private ByteBuffer                 structBuffer = null;
    private DataStructureDescriptor  dataDescriptor = null;

    /**
     * Index of size element and the first element in the 
     * buffer.
     */
    protected int DEFAULT_BUFFER_SIZE = 1024*4;
    protected int FIRST_ELEMENT_INDEX = 8;
    protected int          SIZE_INDEX = 0;
    
    public DataStructure(){
        structBuffer = ByteBuffer.wrap(new byte[DEFAULT_BUFFER_SIZE]);
        structBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }
        
    public DataStructure(String format){
        dataDescriptor = new DataStructureDescriptor();
        dataDescriptor.parse(format);
        //int size = dataDescriptor.getStructureLength();
        //size += (FIRST_ELEMENT_INDEX+16);
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        structBuffer  = ByteBuffer.wrap(buffer);
        structBuffer.order(ByteOrder.LITTLE_ENDIAN);        
    }
    
    public DataStructure(String format, int rows){
        dataDescriptor = new DataStructureDescriptor();
        dataDescriptor.parse(format);
        int rowLength = dataDescriptor.getStructureLength();
        int size = rowLength*rows + (FIRST_ELEMENT_INDEX+16);
        byte[] buffer = new byte[size];
        structBuffer  = ByteBuffer.wrap(buffer);
        structBuffer.order(ByteOrder.LITTLE_ENDIAN);        
    }
    
    public final void setRows(int rows){
        structBuffer.putInt(SIZE_INDEX, rows);
    }
    
    public final int getRows(){
        return structBuffer.getInt(SIZE_INDEX);
    }
    
    public int getEntries(){ return this.dataDescriptor.getEntries();}    
    public int getEntryType(int entry){ return this.dataDescriptor.getEntryType(entry);}

    public final void reset(){
        setRows(0);
    }
    
    public int getRowsSize(){
        return dataDescriptor.structureLength;
    }
    
    protected void require(int size){
        if(structBuffer.capacity()<size){
            byte[] buffer = new byte[size+64];
            structBuffer  = ByteBuffer.wrap(buffer);
            structBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }
    } 
    
    protected void requireRows(int nrows){
        int sizeByRow = FIRST_ELEMENT_INDEX + 16 +
                nrows*this.dataDescriptor.structureLength;
        this.require(sizeByRow);
        //int
    }    
    
    private boolean testTypeForEntry(int entry, int type){
        if(dataDescriptor.getEntryType(entry)==type) return true;
        Log.warn("error : the type for entry " + entry + " is not " + type);
        return false;
    }
    
    public DataStructure putInt(int row, int entry, int number){
        if(testTypeForEntry(entry,3)==true){
            int offset = FIRST_ELEMENT_INDEX + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
            structBuffer.putInt(offset, number);
        }
        return this;
    }
    
    public DataStructure putLong(int row, int entry, long number){
        if(testTypeForEntry(entry,3)==true){
            int offset = FIRST_ELEMENT_INDEX + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
            structBuffer.putLong(offset, number);
        }
        return this;
    }
    
    public DataStructure putByte(int row, int entry, byte number){
        if(testTypeForEntry(entry,1)==true){
            int offset = FIRST_ELEMENT_INDEX + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
            structBuffer.put(offset, number);
        }
        return this;
    }
    public DataStructure putShort(int row, int entry, short number){
        if(testTypeForEntry(entry,2)==true){
            int offset = FIRST_ELEMENT_INDEX + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
            structBuffer.putShort(offset, number);
        }
        return this;
    }
    
    public DataStructure putFloat(int row, int entry, float number){
        if(testTypeForEntry(entry,4)==true){
            int offset = FIRST_ELEMENT_INDEX + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
            structBuffer.putFloat(offset, number);
        }
        return this;
    }
    
    public DataStructure putDouble(int row, int entry, double number){
        if(testTypeForEntry(entry,5)==true){
            int offset = FIRST_ELEMENT_INDEX + 
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
            structBuffer.putDouble(offset, number);
        }
        return this;
    }
    
    public int getInt(int row, int entry){
        int offset = FIRST_ELEMENT_INDEX + 
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
    
    public double getDouble(int row, int entry){
        int offset = FIRST_ELEMENT_INDEX +
                row * dataDescriptor.structureLength + 
                dataDescriptor.getEntryOffset(entry);
        int type = dataDescriptor.getEntryType(entry);
        switch(type){
            case 4 : return (double) structBuffer.getFloat(offset);
            case 5 : return structBuffer.getDouble(offset);
            default: System.out.printf("structure::get : error\n"); return 0.0;
        }
    }
    
    protected ByteBuffer getByteBuffer(){ return structBuffer; }

    public void show(){
        System.out.printf(" structure : size = %d\n",structBuffer.capacity());
        this.dataDescriptor.show();
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
                
        public void init(int[] dataEntries, int[] entryTypes) {
            
            dataLength = new int[dataEntries.length];
            dataOffset = new int[dataEntries.length];
            dataType   = new int[dataEntries.length];
            
            int offset = 0;
            for(int i = 0; i < dataLength.length; i++){
                dataLength[i] = dataEntries[i];
                dataOffset[i] = offset;
                dataType[i] = entryTypes[i];
                offset += dataEntries[i];
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
    
    public static void main(String[] args){
        /*DataStructureDescriptor desc = new DataStructureDescriptor();
        desc.init(new int[]{4,4,2,2,1,1},new int[]{4,4,2,2,1,1});
        desc.show();        
        desc.parse("3b3f2i");        
        desc.show();*/        
        DataStructure struct = new DataStructure("2bs8f",40);
        struct.show();
        
        for(int i = 0; i < 8 ; i++) {
            struct.putByte(  i, 0, (byte) (i+4));
            struct.putFloat( i, 4, (float) ((i+4)/15.0));
        }
        
        struct.setRows(8);
        DataStructureUtils.print(struct, new int[]{0,2,4,5});
        
    }
}
