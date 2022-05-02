/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.io;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.data.Schema;


/**
 *
 * @author gavalian
 */
public class HipoUtilsIO {
    
    public static int dictNodeGroup = 120;
    public static int dictNodeItem  = 1;
    
    public static int HEADER_NODE_GROUP = 32555;
    public static int HEADER_NODE_ITEM  = 2;
    public static int HEADER_NODE_ITEM_KEY  = 1;
    
    /**
     * Creates a Schema for storing file index.
     * @return Schema defining the bank structure
     */
    public static Schema createIndexSchema(){
        
        Schema.SchemaBuilder schemaBuilder = new Schema.SchemaBuilder("file::index",32111,1);
        schemaBuilder.addEntry(     "position", "L", "record position");        
        schemaBuilder.addEntry(       "length", "I", "record length");
        schemaBuilder.addEntry(      "entries", "I", "number of entries in the record");
        schemaBuilder.addEntry(  "userWordOne", "L", "first user word");
        schemaBuilder.addEntry(  "userWordTwo", "L", "second user word");
        Schema schema = schemaBuilder.build();
        return schema;
    }
    /**
     * Creates an empty node for reading the file index node
     * from the file.
     * @return file index node
     */
    public static Bank createIndexNode(){
        Schema schema = HipoUtilsIO.createIndexSchema();
        Bank node = new Bank(schema);
        return node;
    }
    
    public static Event getSchemaEvent(Schema schema){
        Node   jsonNode = HipoUtilsIO.getSchemaJsonNode(schema);
        Node stringNode = HipoUtilsIO.getSchemaNode(schema);
        int sizes = jsonNode.getBufferSize() + stringNode.getBufferSize();
        Event event = new Event(sizes+24);
        
        //System.out.println(" writing to event : " + schema.getName());
        //jsonNode.show();
        //stringNode.show();
        event.write(jsonNode);
        event.write(stringNode);
        return event;
    }
    
    public static Node getSchemaNode(Schema schema){
        String data = schema.getSchemaString();
        Node   node = new Node(120,2,data);
        return node;
    }
    
    public static Node getSchemaJsonNode(Schema schema){
        String data = schema.getJsonString();
        Node   node = new Node(120,1,data);
        return node;
    }
    /**
     * Creates a node for file index with predefined number
     * of rows.
     * @param rows
     * @return file index node with given number of rows
     */
    public static Bank createIndexNode(int rows){
        Schema schema = HipoUtilsIO.createIndexSchema();
        Bank node = new Bank(schema, rows);
        return node;
    }
    
}
