/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class EventFilter {
    
    protected List<String>     bankNameList = new ArrayList<String>();
    protected List<String>    bankExistList = new ArrayList<String>();
    protected Event           filteredEvent = new Event();
    protected List<Bank>           bankList = new ArrayList<Bank>();
    protected List<Schema>       bankExists = new ArrayList<Schema>();
    
    public EventFilter(){
        
    }
    
    public EventFilter(String regExp){
       setBankList(regExp); 
    }
    
    public EventFilter(List<String> regExp){
       bankNameList.addAll(regExp);
    }
    
    public  final  void addBankList(String regExp){
        String[] tokens = regExp.split(",");
        /*System.out.println("************* TOKENS SIZE = " + tokens.length);
        for(int i = 0; i < tokens.length; i++){
            System.out.println("\t TOKENS " + i + "  NAME : " + tokens[i]);
        }*/
        bankNameList.addAll(Arrays.asList(tokens));
    }
    
    
    public  final  void addBankExistList(String regExp){
        String[] tokens = regExp.split(",");
        bankExistList.addAll(Arrays.asList(tokens));
    }
    
    public  final  void setBankList(String regExp){
        bankNameList.clear();
        String[] tokens = regExp.split(",");        
        bankNameList.addAll(Arrays.asList(tokens));
    }
    
    public String getExpression(){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < bankNameList.size(); i++){
            if(i!=0) str.append(",");
            str.append(bankNameList.get(i));
        }
        return str.toString();
    }
    
    public void init(SchemaFactory factory){
        
        SchemaFactory   reduced = factory.reduce(bankNameList);
        SchemaFactory constrain = factory.reduce(bankExistList);
        bankExists.addAll(constrain.getSchemaList());
        //System.out.println("reduced size = " + reduced.getSchemaList().size());
        bankList.addAll(reduced.getBanks());
        //bankExists.addAll(constrain.getBanks());
        System.out.println("*************");
        System.out.println("[EventFilter] --> events filter   exp : " + getExpression());
        System.out.println("[EventFilter] --> events filter  size : " + bankList.size());
        System.out.println("[EventFilter] --> constrain list size : " + bankExists.size());
        System.out.println("\n");
    }
    
    public Event getReduced(Event inEvent){
        filteredEvent.reset();
        for(int i = 0; i < bankList.size(); i++){
            Bank bank = bankList.get(i);
            inEvent.read(bank);
            if(bank.getRows()>0) filteredEvent.write(bank);
        }
        return this.filteredEvent;
    }
    
    public boolean checkConstrains(Event inEvent){
        boolean status = true;
        int cSize = bankExists.size();
        for(int i = 0; i < cSize; i++){
            if(inEvent.hasBank(bankExists.get(i))==false) return false;
        }
        return status;
    }
    
    public void reduceEvent(Event inEvent){
        
        if(checkConstrains(inEvent)==false){
            inEvent.reset(); return;
        }
        
        filteredEvent.reset();
        for(int i = 0; i < bankList.size(); i++){
            Bank bank = bankList.get(i);
            inEvent.read(bank);
            if(bank.getRows()>0) filteredEvent.write(bank);
        }
        inEvent.initFrom(filteredEvent.getEventBuffer().array(), filteredEvent.getEventBufferSize());
    }
}
