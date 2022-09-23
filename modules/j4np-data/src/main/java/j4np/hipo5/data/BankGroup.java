/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class BankGroup {
    
    List<Bank>       dataBanks = new ArrayList();
    Map<String,Bank>   dataMap = new HashMap<>();
    
    public void read(Event event){
        for(Bank b : dataBanks) event.read(b);
    }
    
    public final void init(SchemaFactory f){
        List<String>  names = f.getSchemaKeys();
        Collections.sort(names);
        for(String s : names){
            Bank b = new Bank(f.getSchema(s));
            dataBanks.add(b);
            dataMap.put(s, b);
        }
    }
    
    public Bank get(String b){
        return this.dataMap.get(b);
    }
    
    public void show(){
        for(Bank b : dataBanks){
            if(b.getRows()>0)
                System.out.println(b.getSummary());
            //b.show();
        }
    }
}
