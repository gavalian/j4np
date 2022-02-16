/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.data;

import j4np.utils.json.Json;
import j4np.utils.json.JsonArray;
import j4np.utils.json.JsonObject;
import j4np.utils.json.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author gavalian
 */
public class Schema {
    
    //private List<Integer>   types = new ArrayList<Integer>();
    //private List<Integer>   sizes = new ArrayList<Integer>();
    //private Map<String,Integer> names = new HashMap<String,Integer>();
        
    private List<SchemaEntry>        schemaEntriesList = new ArrayList<SchemaEntry>();
    private Map<String,Integer>       schemaEntriesMap = new HashMap<String,Integer>();
    
    private List<Integer> offsets = new ArrayList<Integer>();
    
    private int             group = 0;
    private int              item = 0;
    private String           name = "node";
    private int       entryLength = 0;
    private String    description = "none";
    
    public Schema(String __name, int __group, int __item){
        name = __name;
        group = __group;
        item = __item;
    }        
    
    public void addEntry(SchemaEntry entry){
       /* if(this.schemaEntriesMap.containsKey(entry.getName())==true){
            System.out.println("** ERROR ** schema " + this.name + " already contains entry with name " + entry.name);
        } else {
            this.schemaEntriesMap.put(entry.getName(), entry);
            this.schemaEntriesList.add(entry);
            entryLength += entry.getTypeSize();
        }*/
    }
    
    public void addEntry(String __name, String __type, String __desc){        
        /*DataType dataType = DataType.getTypeByLetter(__type);
        if(dataType==DataType.UNDEFINED){
            System.out.println("[schema::adn-entry] *** wrong data type " + __type +
                    " for entry " + name);
        } else {            
            SchemaEntry entry = new SchemaEntry(__name,__type,__desc);
            entry.setType(dataType.getType());
            entry.setTypeSize(dataType.getSize());
            addEntry(entry);
            //entryLength += dataType.getSize();
            //this.schemaEntriesList.add(entry);
            //this.schemaEntriesMap.put(__name, entry);
        }*/
    }
    
    public boolean contains(String[] columns){
        for(String item : columns) 
            if(this.schemaEntriesMap.containsKey(item)==false) return false;
        return true;
    }
    
    public Schema copyReduced(String name, int group, int item, String[] columns){
        SchemaBuilder schema = new SchemaBuilder(name,group,item);
        for(String entryName : columns){
            int order = schemaEntriesMap.get(entryName);
            SchemaEntry entry = schemaEntriesList.get(order);
            schema.addEntry(entry.getName(), entry.getType(), entry.getDescription());
        }
        return schema.build();
    }
    
    public String getName(){ return name; }
    public int    getGroup(){ return group;}
    public int    getItem(){ return item;}
    public String getElementName(int element){ return this.schemaEntriesList.get(element).getName();}
    
    public List<String>  getEntryList(){
        List<String> entryList = new ArrayList<String>();
        int nrows =schemaEntriesList.size();
        for(int i = 0; i < nrows; i++){
            entryList.add(schemaEntriesList.get(i).getName());
        }
        return entryList;
    }
    
    public String[] getEntryArray(){
        String[] nameArray = new String[schemaEntriesList.size()];
        int nrows =schemaEntriesList.size();
        for(int i = 0; i < nrows; i++){
            nameArray[i] = schemaEntriesList.get(i).getName();
        }
        return   nameArray;
    }
    
    public int[] getIndexArray(String[] elements){
        int[] result = new int[elements.length];
        for(int i = 0; i < elements.length; i++){
            if(this.schemaEntriesMap.containsKey(elements[i])==false){
                System.out.println("error: bank " + getName() + " does not have an entry " + elements[i]);
                result[i] = -1;
            } else {
                result[i] = this.getElementOrder(elements[i]);
            }
        }
        return result;
    }
    
    private boolean isDigit(char c){
       return  (c >= '0' && c <= '9');
    }
    
    private boolean isLetter(char c){
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z');
    }
    /**
     * Parse a schema string into a 
     * @param format 
     */
    public void parse(String format){
        /*int position = 0;
        int length   = format.length();
        char strChar;
        int  howMany = 1;
        types.clear();
        while(position<length){
            strChar = format.charAt(position);
            
            if(isDigit(strChar)==true){
                howMany = Integer.parseInt(""+strChar);
                position++;
                strChar = format.charAt(position);
            } else {
                howMany = 1;
            }
            
            int type = DataType.getTypeByLetter(strChar+"").getType();
            for(int i = 0; i < howMany; i++){
                System.out.print(" " + type);
                types.add(type);
            }
            System.out.println();
            position++;
        }
        
        entryLength = 0;
        this.offsets.clear();
        for(int i =0; i < this.types.size(); i++){
            DataType   dataType = DataType.getTypeById(types.get(i));
            offsets.add(entryLength);
            sizes.add(dataType.getSize());
            entryLength += dataType.getSize();
            SchemaEntry entry = new SchemaEntry("a","I","");
            entry.setType(types.get(i));
            entry.setTypeSize(dataType.getSize());
            System.out.println(entry);
        }*/
    }
    
    public boolean hasEntry(String name){
        return schemaEntriesMap.containsKey(name);
    }
    
    public void show(){
        System.out.println(String.format("NAME : %s , group = %d, item = %d, row length = %d", 
                this.name, this.group,this.item, this.entryLength));
        for(int i = 0; i < this.schemaEntriesList.size(); i++){
            System.out.println(schemaEntriesList.get(i) + " offset = " + offsets.get(i));
        }
    }
    
    public void setNames(String[] elementNames){
        /*if(elementNames.length!=this.types.size()){
            System.out.println("** schema::error ** the size of names does not match.");
        } else {
            this.names.clear();
            for(int i = 0; i < elementNames.length; i++){
                names.put(elementNames[i], i);
            }
        }*/
    }
    /**
     * Initializes the entries for the Schema and offset buffer for
     * calculating the offset for given entry.
     * @param entries list of entries.
     */
    protected void setEntries(List<SchemaEntry> entries){
        schemaEntriesList.clear();
        offsets.clear();
        int offsetCount = 0;
        for(int i = 0; i < entries.size(); i++){
            offsets.add(offsetCount);
            schemaEntriesList.add(entries.get(i));
            schemaEntriesMap.put(entries.get(i).getName(), i);
            offsetCount += entries.get(i).getTypeSize();
        }
        this.entryLength = offsetCount;
    }
    
    protected int getElementOrder(String name){
        return schemaEntriesMap.get(name);
    }
    
    public void setNames(String elementNames){
        String[] tokens = elementNames.split(":");
        this.setNames(tokens);
    }
    
    public int getElements(){
        return this.schemaEntriesList.size();
    }
    
    public int getType(int element){
        return this.schemaEntriesList.get(element).getTypeId();
    }
    
    public int getType(String name){
        if(schemaEntriesMap.containsKey(name)==true){
            int id = schemaEntriesMap.get(name);
            return schemaEntriesList.get(id).getTypeId();
        }
        return -1;//this.schemaEntriesMap.getTypeId();
    }
    
    public int getEntryLength(){
        return entryLength;
    }
    
    public int getOffset(int element, int index, int size){
        int    offset = size*offsets.get(element) + index*schemaEntriesList.get(element).getTypeSize();        
        return offset;
    }
        
    public Schema getCopy(){
        SchemaBuilder builder = new SchemaBuilder(getName(),getGroup(),getItem());
        for(int i = 0; i < schemaEntriesList.size();i++){
            builder.addEntry(schemaEntriesList.get(i).getCopy());
        }
        return builder.build();
    }
    
    public int getOffset(String name, int index, int size){
        /*if(this.names.containsKey(name)==false) return -1;
        int   element = this.names.get(name);
        int    offset = size*offsets.get(element) + index*sizes.get(element);
        return offset;*/
        int elementid = schemaEntriesMap.get(name);
        return getOffset(elementid,index,size);
    }
    
    
    public int  getEntryOrder(String name){
        if(schemaEntriesMap.containsKey(name))
            return schemaEntriesMap.get(name);
        System.out.printf("%s >>> Error, no entry [%s] found in bank [%s]\n ",
                this.getClass().getName(),name,name);
        return -1;
    }
    
    public Schema copy(){
        SchemaBuilder schbuilder = new SchemaBuilder(getName(),getGroup(),getItem());
        int nentries = this.schemaEntriesList.size();
        for(int i = 0; i < nentries; i++){
            SchemaEntry entry = this.schemaEntriesList.get(i);
            schbuilder.addEntry(entry.getName(),entry.getType(),entry.getDescription());
        }
        return schbuilder.build();
    }
    
    public String getSchemaString(){
        StringBuilder str = new StringBuilder();
        str.append("{").append(getName()).append("/").
                append(getGroup()).append("/").
                append(getItem()).append("}{");
        for(int i = 0; i < schemaEntriesList.size(); i++){
            if(i!=0) str.append(",");
            str.append(schemaEntriesList.get(i).getName()).append("/");
            str.append(schemaEntriesList.get(i).getType());
        }
        str.append("}");
        return str.toString();        
    }
    
    public String getJsonString(){
        
        StringBuilder str = new StringBuilder();
        
        str.append("{ ");
        str.append("\"name\": \"").append(name).append("\", ");
        str.append("\"group\": ").append(group).append(", ");
        str.append("\"item\": ").append(item).append(", ");
        str.append("\"entries\": [ ");
        int nEntries = this.schemaEntriesList.size();
        for(int i = 0; i < nEntries; i++){
            SchemaEntry entry = this.schemaEntriesList.get(i);
            str.append(entry.toJsonString());//.append("\n");
            if(i<nEntries-1) str.append(", ");
        }
        str.append("] }");
        return str.toString();
    }
    
    public String getJsonStringPretty(){
        
        StringBuilder str = new StringBuilder();
        
        str.append("{ \n");
        str.append("  \"name\": \"").append(name).append("\",\n");
        str.append("  \"group\": ").append(group).append(",\n");
        str.append("  \"item\": ").append(item).append(",\n");
        str.append("  \"entries\": [ \n");
        int nEntries = this.schemaEntriesList.size();
        for(int i = 0; i < nEntries; i++){
            SchemaEntry entry = this.schemaEntriesList.get(i);
            str.append("       ").append(entry.toJsonString());//.append("\n");
            if(i<nEntries-1) str.append(",").append("\n");
        }
        str.append("\n   ] \n}");
        return str.toString();
    }
    
    public static Schema fromJsonObject(JsonObject jsonObject){
        
        if(jsonObject.get("name")==null){
            System.out.println("schema:: -->> error : does not contain node [name]");
            return null;
        }
        
        if(jsonObject.get("group")==null){
            System.out.println("schema:: -->> error : does not contain node [group]");
            return null;
        }
        
        if(jsonObject.get("item")==null){
            System.out.println("schema:: -->> error : does not contain node [item]");
            return null;
        }
        
        String bankName = jsonObject.get("name").asString();
        Integer group = jsonObject.get("group").asInt();
        Integer item  = jsonObject.get("item").asInt();

        JsonArray  entries = jsonObject.get("entries").asArray();
        SchemaBuilder schema = new SchemaBuilder(bankName,group,item);        
        for(JsonValue items : entries.values()){
            JsonObject entry = items.asObject();
            String  entryName = entry.get("name").asString();
            String  entryType = entry.get("type").asString();
            String  entryDesc = entry.get("info").asString();
            schema.addEntry(entryName, entryType, entryDesc);
        }
        return schema.build();
    }
    
    public static Schema fromJsonString(String json){
        JsonObject bankDesc = (JsonObject) Json.parse(json);        
        return Schema.fromJsonObject(bankDesc);        
    }
    
    public String getFormatString(){
        StringBuilder str = new StringBuilder();
        
        return str.toString();
    }
    
    
    public static class SchemaEntry {
        
        private String  name = "";
        private String  type = "I";
        private String  description = "none";
        private int     typeId = 1;
        private int     typeSize = 1;
        
        public SchemaEntry(){
            
        }
        
        public SchemaEntry(String entryName, String entryType, String entryDescription){
            name = entryName;
            type = entryType;
            description = entryDescription;
        }
        
        public String getName(){ return name;}
        public String getType(){ return type;}
        public int    getTypeSize(){ return this.typeSize;}
        public int    getTypeId(){ return this.typeId;}
        public String getDescription(){ return description;}

        public SchemaEntry getCopy(){
            SchemaEntry entry = new SchemaEntry(name,type,description);
            entry.setType(typeId).setTypeSize(typeSize);
            return entry;
        }
        
        public SchemaEntry setType(int _type){
            this.typeId = _type; return this;
        }

        public SchemaEntry setType(String _type){
            this.type = _type; return this;
        }
        
        public SchemaEntry setTypeSize(int _typesize){
            this.typeSize = _typesize; return this;
        }
        
        public String toJsonString(){
            StringBuilder str = new StringBuilder();
            str.append("{ \"name\": \"").append(name).append("\", ");
            str.append("\"type\": \"").append(type).append("\", ");
            str.append("\"info\": \"").append(description).append("\" }");
            return str.toString();
        }
        
        @Override
        public String toString(){
            String format = String.format("%12s : type = %3s (%3d) size = %4d", name,
                    type,typeId,typeSize);
            return format;
        }
    }
    
    
    public static class SchemaBuilder {
        
        private String name = "";
        private int    group = 0;
        private int    item  = 0;
        
        List<SchemaEntry> entries = new ArrayList<SchemaEntry>();
        
        public SchemaBuilder(){
            
        }
        
        public SchemaBuilder(String __name, int __group, int __item){
            name  = __name;
            group = __group;
            item  = __item;
        }
        
        public SchemaBuilder setName(String __name) { name  =  __name; return this;}
        public SchemaBuilder setGroup( int __group) { group = __group; return this;}
        public SchemaBuilder setItem(  int  __item) { item  =  __item; return this;}
        
        public SchemaBuilder addEntry( String __name, String __type, String __desc){
            DataType dataType = DataType.getTypeByLetter(__type);
            SchemaEntry entry = new SchemaEntry(__name,__type,__desc);
            entry.setType(dataType.getType()).setTypeSize(dataType.getSize());
            entries.add(entry);
            return this;
        }
        
        public SchemaBuilder addEntry(SchemaEntry entry) { entries.add(entry); return this;}
        
        public Schema build(){
            Schema schema = new Schema(name,group,item);
            for(int i = 0; i < entries.size(); i++){
                schema.addEntry(entries.get(i));
            }
            schema.setEntries(entries);
            return schema;
        }
    }
    
    public static void main(String[] args){
        
        SchemaBuilder schemaBuilder = new SchemaBuilder("data::event",1234,1);        
        //schema.parse("5I4F3BLL");
        schemaBuilder.addEntry(    "pid", "I", "particle id");
        schemaBuilder.addEntry(     "px", "F", "x-component of momentum");
        schemaBuilder.addEntry(     "py", "F", "y-component of momentum");
        schemaBuilder.addEntry(     "pz", "F", "z-component of momentum");
        schemaBuilder.addEntry( "charge", "S", "particle charge");
        schemaBuilder.addEntry( "status", "S", "status of the particle");
        
        Schema schema = schemaBuilder.build();
        
        schema.show();
        
        SchemaFactory factory = new SchemaFactory();
        factory.addSchema(schema);
        
        List<String> list = factory.getSchemaJsonString();
        for(String entry : list){
            System.out.println(entry);
        }
        SchemaFactory factory2 = new SchemaFactory();
        
        factory2.initFromJson(list);
        
        Schema schema2 = schema.getCopy();
        
        schema2.show();
        
        factory.show();
        
        
        //System.out.println(schema.getJsonString());
        //Schema derived = Schema.fromJsonString(schema.getJsonString());
        //derived.show();
        /*
        SchemaEntry entry = new SchemaEntry("px","F","x-component of momentum");
        System.out.println(entry.toJsonString());
        
        NodeSchema schema2 = new NodeSchema("mc::event",1234,1);
        
        schema2.addEntry("pid", "I", "particle LUND ID");
        schema2.addEntry("px" , "F", "x-component of momentum");
        schema2.addEntry("py" , "F", "y-component of momentum");
        schema2.addEntry("pz" , "F", "z-component of momentum");
        System.out.println(schema2.getJsonString());*/
    }
}
