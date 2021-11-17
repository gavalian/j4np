/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.data;

/**
 *
 * @author gavalian
 */
public class CompositeBank {
    
    private Bank  masterBank = null;
    private Bank   slaveBank = null;
    private int    indexNode = 0;

    public CompositeBank(Schema master, Schema dependent, String indexName){
        indexNode = master.getElementOrder(indexName);
        masterBank = new Bank(master);
        slaveBank = new Bank(dependent);
    }
    
    protected Bank getMasterBank(){ return masterBank;}
    protected Bank getSlaveBank(){ return  slaveBank;}
    
    protected void processIndex(){ 
    
    }
    
    public int getRows(){ 
        return masterBank.getRows();
    }
    
    protected int getRowIndexFromMaster(int masterRow){
        int reference = masterBank.getInt(indexNode, masterRow);
        return reference;
    }
    
    public int getInt(String name, int row){
        if(masterBank.getSchema().hasEntry(name)==true){
            return masterBank.getInt(name, row);
        } else {
            int rowIndex = getRowIndexFromMaster(row);
            if(rowIndex>=0&&rowIndex<slaveBank.getRows()){
                return slaveBank.getInt(name, rowIndex);
            }
        }
        return 0;
    }
    
    public double getDouble(String name, int row){
        if(masterBank.getSchema().hasEntry(name)==true){
            return masterBank.getFloat(name, row);
        } else {
            int rowIndex = getRowIndexFromMaster(row);
            if(rowIndex>=0&&rowIndex<slaveBank.getRows()){
                return slaveBank.getFloat(name, rowIndex);
            }
        }
        return 0.0;
    }
    
}
