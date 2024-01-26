/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.decoder;

import j4np.clas12.ccdb.DatabaseManager;
import j4np.clas12.ccdb.DatabaseManager.ParameterDatabase;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;

/**
 *
 * @author gavalian
 */
public class Clas12ConvertService extends DataWorker<HipoReader,Event> {
    SchemaFactory factory = new SchemaFactory();
    //CompositeNode    iter = null;
    DatabaseManager manager = new DatabaseManager();
    
    public Clas12ConvertService(){
        //iter = new CompositeNode(1,1,"i",8196); iter.setRows(0);
    }
    
    @Override
    public boolean init(HipoReader src) {
        //factory.initFromDirectory("/Users/gavalian/Work/Software/project-10.8/distribution/coatjava/etc/bankdefs/hipo4/");
        String env = System.getenv("CLAS12DIR");
        factory.initFromDirectory(env+"/etc/bankdefs/hipo4");
        factory.show();
        return true;
    }
    
    @Override
    public void execute(Event e) {
        CompositeNode   cnode = new CompositeNode(1,1,"i",100*8196);
        CompositeNode   anode = new CompositeNode(1,1,"i",100*8196);
        CompositeNode   hnode = new CompositeNode(1,1,"i",120);
        CompositeNode    iter = new CompositeNode(1,1,"i",8196); iter.setRows(0);
        CompositeNode      ts = new CompositeNode(1,1,"il",8196);
        
        cnode.setRows(0);
        e.read(hnode, 42, 1);
        e.read(   ts, 42, 2);
        e.read(cnode, 42, 11);
        e.read(anode, 42, 12);
        
        e.reset();        

        if(hnode.getRows()==1){
            Bank b = factory.getBank("RUN::config", 1);
            ParameterDatabase pars = manager.parameter(hnode.getInt(0, 0));
            b.putInt(0, 0, hnode.getInt(0, 0));
            b.putInt(1, 0, hnode.getInt(1, 0));
            if(ts.getRows()>0) b.putLong(4,0,ts.getLong(1, 0));
            b.putFloat(7, 0, (float) pars.torus);
            b.putFloat(8, 0, (float) pars.solenoid);
            e.write(b);
        }
        
        

        //System.out.println("------------");

        //getIterator(cnode,17,iter);
        
        Bank rf = this.createTDC(cnode, 17, "RF::tdc", iter);
        e.write(rf);
        Bank dc = this.createTDC(cnode,  6, "DC::tdc", iter);
        e.write(dc);
        Bank ft = this.createTDC(cnode,  12, "FTOF::tdc", iter);
        e.write(ft);
        
        Bank fta = this.createADC(anode,  12, "FTOF::adc", iter);
        e.write(fta);
        
        Bank ect = this.createTDC(cnode,  7, "ECAL::tdc", iter);
        e.write(ect);
        
        Bank eca = this.createADC(anode,  7, "ECAL::adc", iter);
        e.write(eca);
        
        Bank hta = this.createADC(anode,  15, "HTCC::adc", iter);
        e.write(hta);
        //System.out.printf(" get Rows = %d , position %d , row 17 = %d\n" , cnode.getRows(),  e.scan(33, 1), iter.getRows());
        //cnode.print();
        /*
        getIterator(cnode, 6, iter);
        int nrows6 = iter.getRows();
        Bank bdc = factory.getBank("DC::tdc", nrows6);
        for(int r = 0; r < nrows6; r++){
            int row = iter.getInt(0, r);
            bdc.putByte(  0, r, (byte) cnode.getInt(1, row));
            bdc.putByte(  1, r, (byte) cnode.getInt(2, row));
            bdc.putShort( 2, r, (short) cnode.getInt(3, row));
            bdc.putByte(  3, r, (byte) cnode.getInt(4, row));
            bdc.putInt(   4, r,   cnode.getInt(5, row));
            
        }
        e.write(bdc);
        
        getIterator(cnode, 12, iter);
        int nrows12 = iter.getRows();
        Bank bft = factory.getBank("FTOF::tdc", nrows12);
        for(int r = 0; r < nrows12; r++){
            int row = iter.getInt(0, r);
            bft.putByte(  0, r, (byte) cnode.getInt(1, row));
            bft.putByte(  1, r, (byte) cnode.getInt(2, row));
            bft.putShort( 2, r, (short) cnode.getInt(3, row));
            bft.putByte(  3, r, (byte) cnode.getInt(4, row));
            bft.putInt(   4, r,   cnode.getInt(5, row));
            
        }
        e.write(bft);
        */
        
        
    }
 
    protected Bank createADC(CompositeNode adc, int detector, String bank, CompositeNode iterator){

        getIterator(adc, detector, iterator);
        int nrows6 = iterator.getRows();
        
        //tdc.print();
        //iter.print();
        Bank bdc = factory.getBank(bank, nrows6);
        for(int r = 0; r < nrows6; r++){
            int row = iterator.getInt(0, r);
            bdc.putByte(0, r, (byte) adc.getInt(1, row));
            bdc.putByte(1, r, (byte) adc.getInt(2, row));
            bdc.putShort(2, r, (short) adc.getInt(3, row));
            bdc.putByte(3, r, (byte) (adc.getInt(4, row)-1));
            bdc.putInt(4, r,    adc.getInt(5, row));
            bdc.putFloat(5, r,   (float) adc.getDouble(6, row));
            bdc.putShort(6, r,   (short) adc.getInt(7, row)); 
        }
        return bdc;
    }
    
    protected Bank createTDC(CompositeNode tdc, int detector, String bank, CompositeNode iterator){
        getIterator(tdc, detector, iterator);
        int nrows6 = iterator.getRows();
        
        //tdc.print();
        //iter.print();
        Bank bdc = factory.getBank(bank, nrows6);
        for(int r = 0; r < nrows6; r++){
            int row = iterator.getInt(0, r);
            bdc.putByte(  0, r, (byte) tdc.getInt(1, row));
            bdc.putByte(  1, r, (byte) tdc.getInt(2, row));
            bdc.putShort( 2, r, (short) tdc.getInt(3, row));
            bdc.putByte(  3, r, (byte) (tdc.getInt(4, row)-1));
            bdc.putInt(   4, r,   tdc.getInt(5, row));            
        }
        return bdc;
    }
    
    public void getIterator(CompositeNode node, int detector, CompositeNode iterator){
        iterator.setRows(0);
        int nrows = node.getRows();
        //System.out.println(" getrows = " + nrows);
        for(int r = 0; r < nrows; r++){
            if(node.getInt(0, r)==detector) {
                int row = iterator.getRows();
                iterator.putInt(0, row, r);
                iterator.setRows(row+1);
            }
        }
    }
}
