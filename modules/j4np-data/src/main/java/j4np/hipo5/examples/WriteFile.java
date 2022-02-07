/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.examples;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.Schema.SchemaBuilder;
import j4np.hipo5.io.HipoWriter;
import java.util.Random;

/**
 *
 * @author gavalian
 */
public class WriteFile {
    
    public static void random(Bank b, Random r){
        
        int size = r.nextInt(35)+4;
        b.setRows(size);
        for(int i = 0; i < size; i++){
            b.putInt(   "crate", i, r.nextInt(28));
            b.putShort(  "slot", i, (short) r.nextInt(12));
            b.putShort(  "channel", i, (short) r.nextInt(1260));
            b.putFloat("tdc", i, r.nextFloat());
        }        
    }
    
    public static void writeFile(String filename){
        SchemaBuilder schemaBuilder = new SchemaBuilder("detector::tdc",1234,1);        
        //schema.parse("5I4F3BLL");
        schemaBuilder.addEntry(    "crate", "I", "crate of TDC");
        schemaBuilder.addEntry(     "slot", "S", "slot");
        schemaBuilder.addEntry(  "channel", "S", "channel");
        schemaBuilder.addEntry(      "tdc", "F", "tdc value float");
        
        Schema schema = schemaBuilder.build();
        
        Random     r = new Random();
        HipoWriter w = new HipoWriter();
        // add schema has to be called before opening the file
        // since the chemas get written to the file when opening
        w.getSchemaFactory().addSchema(schema);
        
        w.open(filename);
        
        // initialize event with size 20k, the defult size is 200K
        //---------------------------------------------------------
        Event event = new Event(20*1024); 
        Bank   bank = new Bank(schema);
        
        for(int i = 0; i < 200; i++){
            WriteFile.random(bank, r);
            event.reset(); // empty event buffer, remove all banks
            event.write(bank); // write fresh instance of bank
            w.addEvent(event); // write event to the file
        }
        w.close();        
    }
    
    public static void main(String[] args){
        WriteFile.writeFile("example_file.h5");
    }
}
