/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.data;

import j4np.utils.asciitable.Table;
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
    
    public List<Bank> getBanks(){ return dataBanks;}
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
    
    public static String getTable(List<Bank> banks, boolean skipEmpty){
        int nrows = 0;
        for(int i = 0; i < banks.size(); i++){
            if(banks.get(i).getRows()>0) nrows++;
        }
        if(nrows==0) return "";
        String[]  header = new String[]{"order", "bank name","group","item","rows", "size"};
        String[][]  data = new String[nrows][6];
        int counter = 0;
        for(int i = 0; i < banks.size(); i++){
            if(banks.get(i).getRows()>0){
                data[counter][0] = "" + (i+1);
                data[counter][1] = banks.get(i).getSchema().getName();
                data[counter][2] = "" + banks.get(i).getSchema().getGroup();
                data[counter][3] = "" + banks.get(i).getSchema().getItem();
                data[counter][4] = "" + banks.get(i).getRows();
                data[counter][5] = "" + banks.get(i).getNodeLength();
                counter++;
            }
        }
        return Table.getTable(header,data, new Table.ColumnConstrain(1,32));
        
    }
}
