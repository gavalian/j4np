/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.tree;

import j4np.hipo5.base.HipoException;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.Schema.SchemaBuilder;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.utils.io.TextFileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import twig.studio.OpenHipoDialog;

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
    

    public HipoTree(){}
    
    public HipoTree(String file){
            reader.open(file);
            treeBank  = reader.getBank("t::tree");
            treeBank.getSchema().show();
            bankGroup = treeBank.getSchema().getGroup();
            bankItem  = treeBank.getSchema().getItem();
            init();
    }
    
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
        return treeBank.getValue(order, bankRow);
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
        return this.treeBank.getSchema().getEntryOrder(name);
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
    
    
    public static HipoTree withFile(String file){
        HipoTree tree = new HipoTree(file);
        return tree;
    }
    
    public static HipoTree withFile(String file,String bank){
        HipoTree tree = new HipoTree(file,bank);
        return tree;
    }
    
    public static void fromTree(Tree t, String file, String cut){
        
        t.reset();
        List<String> branches = t.getBranches();
        TreeCut         cutExp = new TreeCut("1",cut,branches);
        String[] names = t.getBranchString().split(":");
        
        Schema.SchemaBuilder builder = new Schema.SchemaBuilder("t::tree",1120,1);
        for(int i = 0; i < names.length; i++)
            builder.addEntry(names[i], "F", "");
        
        Schema schema = builder.build();
        
        HipoWriter w = new HipoWriter();
        
        w.getSchemaFactory().addSchema(schema);
        w.open(file);
        Event event = new Event();
        
        int nLinesPerEvent = 300;
        int  nLinesWritten = 0;
        boolean writeFlag = true;
         while(writeFlag==true){
             
             int countDown = nLinesPerEvent;
             
             boolean hasEntries = true;
             Bank b = new Bank(schema,nLinesPerEvent);
             
             while(countDown>0&&hasEntries==true){
                 hasEntries = t.next();
                 if(hasEntries==false){
                     writeFlag = false;
                 } else {
                     if(cutExp.isValid(t)>0.5){
                         nLinesWritten++;
                         for(int kk = 0; kk < branches.size();kk++)
                             b.putFloat(branches.get(kk), nLinesPerEvent-countDown, 
                                     (float) t.getValue(kk));
                         countDown--;
                     }
                 }
             }

             if(countDown==0){
                 event.reset();
                 event.write(b);
                 w.addEvent(event);
             } 
         }
         w.close();
         System.out.printf(" file : tree ==> %s exported rows = %d\n",file,nLinesWritten);
    }
    
    public static void fromCsv(String expression, String csvFile){
        
        String hipoFile = csvFile.replaceAll(".csv", ".h5");
        String[] names = expression.split(":");
        SchemaBuilder builder = new SchemaBuilder("t::tree",1120,1);
        for(int i = 0; i < names.length; i++)
            builder.addEntry(names[i], "F", "");
        
        Schema schema = builder.build();
        
        HipoWriter w = new HipoWriter();
        
        w.getSchemaFactory().addSchema(schema);
        w.open(hipoFile);
        
        Event event = new Event();
        
        int nLinesPerEvent = 300;
        int  nLinesWritten = 0;
        TextFileReader r = new TextFileReader();
        r.open(csvFile);
        
        List<String>  lines;
        
        do {
            lines = r.readLines(nLinesPerEvent);            
            Bank b = new Bank(schema,lines.size());
            for(int i = 0; i < lines.size(); i++){
                nLinesWritten++;
                if(lines.get(i).startsWith("#")==false){
                    String[] data = lines.get(i).split(",");
                    if(data.length>=names.length){
                        for(int item = 0; item < names.length; item++){
                            float value = Float.parseFloat(data[item].trim());
                            b.putFloat(item, i, value);
                        }
                    } else {
                        System.out.println(" error at line # " + i + " data size = " 
                                + data.length + ", variables size = " + names.length);
                    }
                }
            }
            event.reset();
            event.write(b);
            //b.show();
            //event.show();
            //System.out.println("event size = " + event.getEventBufferSize());
            w.addEvent(event,0);                        
        } while (lines.size()==nLinesPerEvent);
        w.close();
        System.out.printf(" file : %s ==> %s exported rows = %d\n",csvFile,hipoFile,nLinesWritten);
    }

    @Override
    public void configure() {
        OpenHipoDialog d = new OpenHipoDialog(null);
        d.show();
        String file = d.getFileName();
        String bank = d.getBankName();
        this.reader = new HipoReader();
        reader.open(file);
        treeBank  = reader.getBank(bank);
        treeBank.getSchema().show();
        bankGroup = treeBank.getSchema().getGroup();
        bankItem  = treeBank.getSchema().getItem();
        init();
    }
    
    
}
