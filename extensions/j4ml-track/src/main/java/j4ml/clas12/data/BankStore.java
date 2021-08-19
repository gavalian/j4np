/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoChain;

/**
 *
 * @author gavalian
 */
public class BankStore {
    List<String> bankList = new ArrayList<>();
    Map<String, Bank> bankMap = new HashMap<>();
    
            
    public BankStore(String[] banks){
        bankList.addAll(Arrays.asList(banks));
    }
    
    public void init(HipoChain chain){
        for(String bank : bankList){
            if(chain.getSchemaFactory().hasSchema(bank)==false){
                System.out.printf("(error) -> bank with name %s does not exist.",bank);
            } else {
                Bank b = chain.getBank(bank);
                bankMap.put(bank, b);
            }
        }
    }
    
    public void read(Event event){
        for(Map.Entry<String,Bank> entry : bankMap.entrySet()){
            event.read(entry.getValue());
        }
    }
    
    public Map<String,Bank> getMap(){
        return this.bankMap;
    }
}
