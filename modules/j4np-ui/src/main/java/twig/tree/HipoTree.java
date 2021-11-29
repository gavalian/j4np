/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.tree;

import j4np.hipo5.base.HipoException;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class HipoTree extends Tree {

    HipoReader           reader = new HipoReader();
    Bank               treeBank = null;
    private   Event   treeEvent = new Event();
    
    List<String>  schemaEntries = new ArrayList<>();
    
    private   int  bankRowCount = 0;
    private   int       bankRow = 0;
    private   int     bankGroup = 0;
    private   int      bankItem = 0;
    

    public HipoTree(String file, String bank){
            reader.open(file);
            treeBank  = reader.getBank(bank);
            treeBank.getSchema().show();
            bankGroup = treeBank.getSchema().getGroup();
            bankItem  = treeBank.getSchema().getItem();
            init();
    }
    
    
    private void init(){
        schemaEntries.clear();
        schemaEntries.addAll(treeBank.getSchema().getEntryList());
    }
    
    @Override
    public double getValue(int order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getValue(String branch) {
        return treeBank.getValue(branch, bankRow);
    }

    @Override
    public List<String> getBranches() {
        return this.schemaEntries;
    }

    @Override
    public int getBranchOrder(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.        
    }

    @Override
    public void reset() {
        reader.getEvent(treeEvent,0);
        treeEvent.read(treeBank);
        bankRowCount = treeBank.getRows();
        bankRow = 0;
    }

    @Override
    public boolean next() {
        bankRow++;
        if(bankRow<bankRowCount) return true;    
        if(reader.hasNext()==false) return false;
        
        treeEvent = reader.nextEvent(treeEvent, bankGroup, bankItem);
        
        if(treeEvent.scan(bankGroup, bankItem)<0) return false;
        
        treeEvent.read(treeBank);
        
        bankRowCount = treeBank.getRows();
        bankRow      = 0;
        
        return true;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
