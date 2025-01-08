/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.data;

import j4np.hipo5.data.Schema.SchemaBuilder;
import j4np.utils.FileUtils;
import j4np.utils.asciitable.Table;
import j4np.utils.json.Json;
import j4np.utils.json.JsonArray;
import j4np.utils.json.JsonObject;
import j4np.utils.json.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author gavalian
 */
public class SchemaFactory {
    
    private Map<String,Schema>        schemaEntries = new HashMap<String,Schema>();
    private Map<Integer,Schema> schemaEntriesGroups = new HashMap<Integer,Schema>();
        
    public SchemaFactory(){
        
    }
    
    public void add(SchemaFactory sf){
        for(Schema sc : sf.getSchemaList()){
            this.addSchema(sc);
        }
    }
    
    public void addSchema(Schema schema){
        
        if(schemaEntries.containsKey(schema.getName())==true){
            System.out.println("schema factory:: --->>> error. factory already contains schema with name [" + 
                    schema.getName() + "]");
            return;
        }
        
        if(hasSchema(schema.getGroup(),schema.getItem())==true){
            System.out.println("schema factory:: --->>> error. factory already contains schema with ids [" 
                     + schema.getGroup() + ", " + schema.getItem() + "]");
            return;
        }
        int hash = hashCode(schema.getGroup(),schema.getItem());                
        schemaEntries.put(schema.getName(), schema);        
        schemaEntriesGroups.put(hash, schema);
    }
    
    public void copy(SchemaFactory __factory){
        this.schemaEntries.clear();
        this.schemaEntriesGroups.clear();
        List<Schema> schemas = __factory.getSchemaList();
        for(int i = 0; i < schemas.size(); i++){
            this.addSchema(schemas.get(i));
        }
    }
    
    public void copy(SchemaFactory __factory, boolean deep){
        this.schemaEntries.clear();
        this.schemaEntriesGroups.clear();
        List<Schema> schemas = __factory.getSchemaList();
        for(int i = 0; i < schemas.size(); i++){
            if(deep==true){
                this.addSchema(schemas.get(i).copy());
            } else {
                this.addSchema(schemas.get(i));
            }
        }
    }
    
    public List<int[]> getIdentifiers(List<String> banks){
        List<int[]> ids = new ArrayList<>();
        for(String bank : banks){
            if(this.hasSchema(bank)==true){
                Schema sc = this.getSchema(bank);
                ids.add(new int[]{sc.getGroup(),sc.getItem()});
            }
        }
        return ids;
    }
    
    public Bank getBank(String name){
        if(this.hasSchema(name)==false){
            SchemaBuilder builder = new SchemaBuilder("empty",0,0);            
            builder.addEntry("dummy", "F", "dummy variable");
            System.out.println("[schema factory] getBank erorr: there is no schema with name " + name);
            return new Bank(builder.build());
        }
        return new Bank(this.getSchema(name));
    }
    
    public Bank getBank(String name, int rows){
        if(this.hasSchema(name)==false){
            SchemaBuilder builder = new SchemaBuilder("empty",0,0);            
            builder.addEntry("dummy", "F", "dummy variable");
            System.out.println("[schema factory] getBank erorr: there is no schema with name " + name);
            return new Bank(builder.build());
        }
        return new Bank(this.getSchema(name),rows);
    }
    
    public List<Bank>    getBanks(){
        List<Bank>      bankList = new ArrayList<Bank>();
        List<Schema>  schemaList = getSchemaList();
        for(Schema sc : schemaList){
            Bank b = new Bank(sc);
            bankList.add(b);
        }
        return bankList;
    }
    
    public Bank[] getBanks(String[] banks, int size ){
        Bank[] b = new Bank[banks.length];
        for(int i = 0; i < b.length; i++) b[i] = this.getBank(banks[i], size);
        return b;
    }
    /**
     * returns a reduced SchemaFactory where the list
     * of regular expressions are taken from the argument.
     * @param regExList
     * @return reduced SchemaFactory
     */
    public SchemaFactory reduce(String regExList){
        String  regExPosics = regExList.replace("*", ".*");
        //String[] tokens = regExList.split(",");
        String[] tokens = regExPosics.split(",");
        List<String> regEx = Arrays.asList(tokens);
        return reduce(regEx);
    }
    /**
     * Removes schema that match given regular expressions given
     * as an argument. The regular expressions are passed as comma
     * separated list, wildcards are accepted.
     * @param regEx list of regular expression to match
     * @return new reduced schema factory
     */
    public SchemaFactory remove(String regExList){
        String  regExPosics = regExList.replace("*", ".*");
        String[] tokens = regExPosics.split(",");
        List<String> regEx = Arrays.asList(tokens);
        return remove(regEx);
    }
    /**
     * Removes schema that match given regular expressions given
     * as an argument.
     * @param regEx list of regular expression to match
     * @return new reduced schema factory
     */
    public SchemaFactory remove(List<String> regEx){
        List<Pattern> exp = new ArrayList<Pattern>();
        for(String item : regEx){
            String itemPosix = item.replace("*", ".*");
            exp.add(Pattern.compile(itemPosix));
        }
        
        Set<String> banksKeep = new HashSet<String>();
        for(Map.Entry<String,Schema> schema : schemaEntries.entrySet()){
            boolean keepBank = true;
            for(Pattern p : exp){
                Matcher m = p.matcher(schema.getKey());
                if(m.matches()==true) keepBank = false;
            }
            if(keepBank==true) banksKeep.add(schema.getKey());
        }
        
        SchemaFactory factory = new SchemaFactory();        
        for(String item : banksKeep){
            factory.addSchema(getSchema(item));
        }
        return factory;
    }
    /**
     * Reduce schema factory to contain only banks that match
     * the regular expressions provided as an argument.
     * @param regEx list of regular expressions to match
     * @return new reduced schema factory
     */
    public SchemaFactory reduce(List<String> regEx){
        List<Pattern> exp = new ArrayList<Pattern>();
        for(String item : regEx){
            String itemPosix = item.replace("*", ".*");
            exp.add(Pattern.compile(itemPosix));
        }
        Set<String> banksKeep = new HashSet<String>();        
        for(Map.Entry<String,Schema> schema : schemaEntries.entrySet()){
            boolean keep = false;
            for(Pattern p : exp){
                Matcher m = p.matcher(schema.getKey());
                if(m.matches()==true) banksKeep.add(schema.getKey());
            }
        }
        SchemaFactory factory = new SchemaFactory();
        for(String item : banksKeep){
            //Schema sch = new Schema();
            //sch.copy(this.getSchema(item));
            //factory.addSchema(sch);
            //System.out.println(" bank ---> " + item);
            factory.addSchema(getSchema(item));
        }
        return factory;
    }
    
    public boolean hasSchema(String name){
        return this.schemaEntries.containsKey(name);
    }
    
    public boolean hasSchema(int group, int item){
        int hash = hashCode(group,item);
        return this.schemaEntriesGroups.containsKey(hash);
    }
    public Schema getSchema(int group, int item){
        int hash = hashCode(group,item);        
        return this.schemaEntriesGroups.get(hash);
    }
    public Schema getSchema(String name){
        return this.schemaEntries.get(name);
    }
    
    public List<String> getSchemaKeys(){
        List<String> keys = new ArrayList<String>();
        Set<String> keySet = schemaEntries.keySet();
        for(String item : keySet) keys.add(item);
        return keys;
    }
    
    public List<Schema> getSchemaList(){
        List<Schema> keys = new ArrayList<Schema>();
        for(Map.Entry<String,Schema> entry : schemaEntries.entrySet()){
            keys.add(entry.getValue());
        }
        return keys;
    }
    
    
    private int hashCode(int group, int item){
        return ((group<<16)&0xffff0000)|item;
    }
    
    public void reset(){
        schemaEntries.clear();
    }
    
    
    public void initFromJson(List<String> jsonSchemaList){
        for(String schemaDesc : jsonSchemaList){
            Schema schema = Schema.fromJsonString(schemaDesc);
            //System.out.println("adding schema : " + schema.getName());
            schema.show();
            addSchema(schema);
        }
    }
    
    public void readFile(String filename){
        String jsonObject = FileUtils.readFileAsString(filename);
        JsonArray bankArray = (JsonArray) Json.parse(jsonObject).asArray();
        for(JsonValue items : bankArray.values()){
                JsonObject entry = items.asObject();
                Schema schema = Schema.fromJsonObject(entry);
                System.out.println("schemafactory:: ***** schema loaded : " + schema.getName());
                addSchema(schema);
        }
    }
    
    public void initFromDirectory(String dir){
        List<String> jsonFiles = FileUtils.getFileListInDir(dir, "json");
        for(int i = 0; i < jsonFiles.size(); i++){
            //System.out.println(">>> open json file : " + jsonFiles.get(i));
            String jsonObject = FileUtils.readFileAsString(jsonFiles.get(i));
            //System.out.println(jsonObject);
            JsonArray bankArray = (JsonArray) Json.parse(jsonObject).asArray();
            int counter = 0;
            for(JsonValue items : bankArray.values()){
                JsonObject entry = items.asObject();
                Schema schema = Schema.fromJsonObject(entry);
                counter++;
                if(schema!=null){
                    addSchema(schema);
                } else {
                    System.out.println("schema factory:: --->>> error. parsing schema " + counter 
                    + " failed in file " + jsonFiles.get(i));
                }
            }
        }
    }
    
    public List<String> getSchemaJsonString(){
        List<String> schemaJsonList = new ArrayList<String>();
        for(Map.Entry<String,Schema> entry : schemaEntries.entrySet()){
            schemaJsonList.add(entry.getValue().getJsonString());
        }
        return schemaJsonList;
    }
    
    public void show(){
        List<String> schemaList = getSchemaKeys();
        int nrows = schemaList.size();
        String[]  header = new String[]{"order", "schema name","group","item","elements", "size"};
        String[][]  data = new String[nrows][6];
        
        Collections.sort(schemaList);
        
        int counter = 0;
        for(int i = 0; i < nrows; i++){
            Schema sc = this.getSchema(schemaList.get(i));
                data[counter][0] = "" + (i+1);
                data[counter][1] = sc.getName();
                data[counter][2] = "" + sc.getGroup();
                data[counter][3] = "" + sc.getItem();
                data[counter][4] = "" + sc.getElements();
                data[counter][5] = "" + sc.getEntryLength();
                counter++;
        }
        String table = Table.getTable(header,data, new Table.ColumnConstrain(1,42));
        System.out.println("| SCHEMA FACTORY - revision 5.01-dub");
        System.out.println(table);
    /*
        for(int i = 0; i < schemaList.size(); i++){
            Schema schema = getSchema(schemaList.get(i));
            System.out.println(String.format("%24s : (%5d,%5d) size = %4d", 
                    schema.getName(),
                    schema.getGroup(),
                    schema.getItem(),
                    schema.getEntryLength()));
        }*/
    }
    
    public static void main(String[] args){
        SchemaFactory factory = new SchemaFactory();
        factory.initFromDirectory("/Users/gavalian/Work/Software/project-10.8/distribution/coatjava/etc/bankdefs/hipo4");
        factory.show();
        List<Schema> schemas = factory.getSchemaList();
        
        System.out.println("--------------------------------------");
        System.out.println("**************************************");
        
        SchemaFactory reduced = factory.reduce("REC::.*");
        System.out.println("**************************************");
        reduced.show();
        /*
        for(Schema schema: schemas){
            System.out.println(schema.getJsonStringPretty());
        }*/
        
        String wildcard = "data::pro*";
        String regex = wildcard.replace("*", ".*");
        System.out.println("WILD : " + wildcard);
        System.out.println("REGX : " + regex);
    }
}
