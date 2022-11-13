/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.data.decoder;

import java.util.ArrayList;
import java.util.List;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.Schema;
import org.jlab.jnp.hipo4.io.HipoReader;

/**
 *
 * @author gavalian
 */
public class RawDataBank {
    
    private final List<Integer>  orderList = new ArrayList<>();
    private final List<Integer>  indexList = new ArrayList<>();
    
    private Bank  dataBank = null;
    
    public RawDataBank(Schema sch, int allocate){
        dataBank = new Bank(sch,allocate);
    }   
    
    public final void setFilter(int... numOrders){
        orderList.clear();
        for(int i = 0; i < numOrders.length;i++) orderList.add(numOrders[i]);
    }
    
    public void read(Event evt){
        evt.read(dataBank);        
        this.notifyRead();
    }
    
    protected void notifyRead(){
        indexList.clear();
        int rows = dataBank.getRows();
        for(int i = 0; i < rows; i++){
            int order = dataBank.getInt("order", i);
            if(orderList.contains(order)) indexList.add(i);
        }
    }
    
    public int getSize(){ 
        return this.indexList.size();
    }
    public int sector(int index ) {
        return dataBank.getInt("sector", indexList.get(index));
    }
    public int layer(int index )  {
        return dataBank.getInt("layer", indexList.get(index));
    }
    public int component(int index ){
        return dataBank.getInt("component", indexList.get(index));
    }
    public int adc(int index ){
        return dataBank.getInt("ADC", indexList.get(index));
    }
    public int order(int index ){
        return dataBank.getInt("order", indexList.get(index));
    }
    public int getTrueIndex(int index){
        return this.indexList.get(index);
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/DataSpace/rga/rec_005988.00005.00009.hipo";
        HipoReader r = new HipoReader();
        r.open(file);
        Event e = new Event();
        
        RawDataBank ftof = new RawDataBank(r.getSchemaFactory().getSchema("FTOF::adc"),40);
        Bank        fadc = new Bank(r.getSchemaFactory().getSchema("FTOF::adc"));
        
        ftof.setFilter(1);
        
        for(int i = 0; i < 120; i++){
            r.nextEvent(e);
            e.read(fadc);
            // bank has to read initialize
            ftof.read(e);
            
            System.out.printf("FTOF ADC size %8d, filtered size = %8d\n",fadc.getRows(),ftof.getSize());
        }
    }
}
