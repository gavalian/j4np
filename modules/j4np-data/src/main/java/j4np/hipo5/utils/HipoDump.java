/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.utils;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.BankGroup;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class HipoDump {
    
    SchemaFactory factory = new SchemaFactory();
    SchemaFactory require = new SchemaFactory();
    HipoReader     reader = null;
    Event           event = new Event();
    
    int              currentEvent = 0;
    List<Event>       markedEvent = new ArrayList<>();
    BankGroup               group = new BankGroup();
    
    public HipoDump(){
        
    }
    
    public HipoDump(String file){
        this.init(file, "*", "*");
    }
    
    public HipoDump(String file, String selection){
        this.init(file, selection, "*");
    }
    
    public HipoDump(String file, String selection, String exists){
        this.init(file, selection, exists);
    }
    
    public boolean exist(Event event){
        if(require.getSchemaList().isEmpty()) return true;
        for(Schema sch : require.getSchemaList()){
            if(event.hasBank(sch)==false) return false;
        }
        return true;
    }
    
    public final void init(String file, String selection, String exists){
        reader = new HipoReader(file);
        
        if(exists.compareTo("*")!=0){
            require.copy(reader.getSchemaFactory());
            require = reader.getSchemaFactory().reduce(exists);
        }
        
        factory = reader.getSchemaFactory().reduce(selection);
        group.init(factory);
        System.out.printf("DUMP : factory disctionaries %d, reduced size = %d\n",
                reader.getSchemaFactory().getSchemaList().size(), factory.getSchemaList().size());
    }
    
    public void rewind(){
        this.reader.rewind();this.currentEvent = 0;
    }
    
    public void advance(){
        if(reader.hasNext()==false) {
            System.out.println("\n\n:::: no more events in the file.....\n");
            return;
        }
        
        boolean advanceFurther = true;
        int            skipped = 0;
        do {
            reader.nextEvent(event);
            if(this.exist(event)==true)  advanceFurther = false;
            if(reader.hasNext()==false)  advanceFurther = false;
             this.currentEvent++;
             skipped++;
        } while(advanceFurther==true);
        
            System.out.println();
            System.out.printf("CURRENT EVENT [skipped = %4d] : %8d\n",skipped-1, this.currentEvent);           
           
        /*} else {
            System.out.println("\n\n:::: no more events in the file.....\n");
        }*/
    }
    
    public void showNext(){
        this.advance();this.show();
    }
    
    public void show(){
        System.out.println();
        System.out.println("CURRENT EVENT: " + this.currentEvent + "\n"); 
        group.read(event);
        //group.show();
        System.out.println(BankGroup.getTable(group.getBanks(), true));
    }
    
    public void gotoEvent(int index){
        System.out.printf("CURRENT EVENT %d, GOTO %d\n",this.currentEvent,index);
        reader.getEvent(event, index);
    }
    
    public void show(String bank){
        try {
            Bank b = group.get(bank.trim());
            b.show();
        } catch (Exception e){
            System.out.println("::: bank not found : " + bank.trim());
        }
    }
    
    public void show(int order){
        try {
            Bank b = group.getBanks().get(order);
            b.show();
        } catch (Exception e){
            System.out.println("::: bank not found : " + order);
        }
    }
    public void describe(String bank){
        Bank b = reader.getBank(bank);
        b.getSchema().show();
    }
    
    public void show(int group, int item){
        try {
            Map<Integer,Node> nodes = this.event.readNodes(group);
            if(nodes.containsKey(item)==true){
                nodes.get(item).show();
            } else {
                System.out.printf("::: can not find node %d/%d\n",group,item);
            }
        } catch (Exception e){
            System.out.printf("::: can not find node %d/%d\n",group,item);
        }
    }
    
    public void showRaw(){
        System.out.println();
        System.out.println("CURRENT EVENT: " + this.currentEvent + "\n"); 
        event.scanShow();
    }
    
    public void markEvent(){
        markedEvent.add(event.copy());
        System.out.println();
        System.out.println("::: marked event # " + this.currentEvent);
        System.out.println("::: number of marked events :  " + markedEvent.size());
        System.out.println();
    }
    
    public void export(String file){
        HipoWriter w = HipoWriter.create(file, reader);
        for(Event e : this.markedEvent) w.add(event);
        w.close();
    }
    
    public static void runOnFile(String input, String selection, String exist){
        
    }
}
