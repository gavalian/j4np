/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics.analysis;

import java.util.ArrayList;
import java.util.List;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoChain;

/**
 *
 * @author gavalian
 */
public class BankStore {
    List<String> bankNames = new ArrayList<>();
    List<Bank>   bankStructures = new ArrayList<>();
    
    public BankStore(){}
    
    
    public void add(String... names){
        for(int i = 0; i < names.length; i++) bankNames.add(names[i]);
    }
    
    public void init(HipoChain chain){
        bankStructures.clear();
        for(int i = 0; i < bankNames.size(); i++){
            System.out.printf("........ schema retrieve : %s\n",bankNames.get(i));
            if(chain.getSchemaFactory().hasSchema(bankNames.get(i))==true){
                Bank b = chain.getBank(bankNames.get(i));
                bankStructures.add(b);
            } else {
                System.out.printf("[bankstore] --> error : no bank %s exists in the file.\n",
                        bankNames.get(i));
            }
        }
    }
    
    
    public void read(Event event){
        for(Bank b : bankStructures){
            event.read(b);
        }
    }
    
    public List<Bank> getBanks(){
        return bankStructures;
    }
    
    public void show(){
        for(int i = 0; i < this.bankStructures.size(); i++){
            System.out.printf(".... %24s : rows = %8d\n",
                    bankStructures.get(i).getSchema().getName(),
                    bankStructures.get(i).getRows()
                    );
        }
    }
    
}
