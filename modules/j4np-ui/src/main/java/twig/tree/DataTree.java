/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.tree;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Leaf;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class DataTree extends Tree {
    
    HipoReader           reader = new HipoReader();

    private   Event   treeEvent = new Event();

    Leaf leaf = null;
    
    int leafGroup = 0;
    int  leafItem = 0;
    
    private   int  bankRowCount = 0;
    private   int       bankRow = 0;
    Map<String,Integer>   map = new HashMap<>();
    List<String>          branches = new ArrayList<>();
    
    public DataTree(String file, int group, int item, int count){
        leaf = new Leaf(1024);
        leafGroup = group; leafItem = item;
        for(int i = 0; i < count; i++) { 
            map.put(String.format("c%d", i+1), i); 
            branches.add(String.format("c%d", i+1));
        }
        reader.open(file);
    }
    
    @Override
    public double getValue(int order) {
        int type = leaf.getEntryType(order);
        switch (type) {
            case 1: return leaf.getInt(order, bankRow);            
            case 2: return leaf.getInt(order, bankRow);
            case 3: return leaf.getInt(order, bankRow);
            case 4: return leaf.getDouble(order, bankRow);
            case 5: return leaf.getDouble(order, bankRow);
            default: return 0.0;
        }
    }

    @Override
    public double getValue(String branch) {
        int order = map.get(branch);
        return this.getValue(order);
    }

    @Override
    public List<String> getBranches() {
        return branches;
    }

    @Override
    public int getBranchOrder(String name) {
        return map.get(name);
    }

    @Override
    public void reset() {
        reader.rewind();
        treeEvent = reader.nextEvent(treeEvent, leafGroup, leafItem);
        //reader.getEvent(treeEvent,0);
        treeEvent.read(leaf, leafGroup, leafItem);
        bankRowCount = leaf.getRows();
        bankRow = 0;
    }

    @Override
    public boolean next() {
        bankRow++;
        if(bankRow<bankRowCount) return true;    
        if(reader.hasNext()==false) return false;
        
        //reader.next(treeEvent);
        
        treeEvent = reader.nextEvent(treeEvent, leafGroup, leafItem);
        //treeEvent.scanShow();
        treeEvent.read(leaf, leafGroup, leafItem);
        //treeEvent = reader.nextEvent(treeEvent, leafGroup, leafItem);
                
        bankRowCount = leaf.getRows();
        bankRow      = 0;
        
        return true;

    }

    @Override
    public void configure() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/Software/project-11.0/distribution/j4np/modules/j4np-instarec/chain_output.h5";
        DataTree t = new DataTree(file,32000,21,12);
        t.showBranches();
        /*t.next();
        
        for(int i = 0; i < 20; i++){
            t.next();
            System.out.println( t.getValue(0));
        }*/
        
        t.draw("c5","c1>0");
    }
}
