/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.data;

/**
 *
 * @author gavalian
 */
public enum DataType {
    
    UNDEFINED (  0,  0, "U", "UNDEFINED"),
    BYTE      (  1,  1, "B", "BYTE"),
    SHORT     (  2,  2, "S", "SHORT"),
    INT       (  3,  4, "I", "INT"),    
    FLOAT     (  4,  4, "F", "FLOAT"),
    DOUBLE    (  5,  8, "D", "DOUBLE"),
    STRING    (  6,  1, "C", "STRING"),
    GROUP     (  7,  0, "G", "GROUP"),
    LONG      (  8,  8, "L", "LONG"),
    VECTOR3F  (  9, 12, "V", "VECTOR3F"),
    COMPOSITE ( 10,  1, "N", "COMPOSITE"),
    TABLE     ( 11,  1, "T", "TABLE"),
    BRANCH    ( 12,  1, "H", "BRANCH");
    
    private final int typeid;
    private final int sizeOf;
    private final String typename;
    private final String typeLetter;
    
    DataType(){
        typeid = 0;
        sizeOf = 0;
        typeLetter = "U";
        typename = "UNDEFINED";
    }
    
    DataType(int id, int s, String letter, String name){
        typeid = id;
        sizeOf = s;
        typeLetter = letter;
        typename = name;
    }
    
    public static DataType getType(String name) {
        name = name.trim();
        for(DataType id: DataType.values())
            if (id.getName().equalsIgnoreCase(name)) 
                return id;
        return UNDEFINED;
    }
    
    public static DataType getTypeByLetter(String name) {
        name = name.trim();
        for(DataType id: DataType.values())
            if (id.getLetter().equalsIgnoreCase(name)) 
                return id;
        return UNDEFINED;
    }
    
    public static DataType getTypeById(int tid) {
        for(DataType id: DataType.values())
            if (id.getType()==tid) 
                return id;
        return UNDEFINED;
    }
    public String getName() {
        return typename;
    }
    
     /**
     * Returns the id number of the detector.
     * @return the id number of the detector
     */
    public int getType() {
        return typeid;
    }
    
    public int getSize(){
        return sizeOf;
    }
    
    public String getLetter(){
        return this.typeLetter;
    }
}
