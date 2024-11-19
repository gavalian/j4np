/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

import j4np.data.base.DataActor;
import j4np.data.base.DataEvent;
import j4np.data.base.DataSource;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Leaf;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.Schema.SchemaBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class ConverterWorker extends DataWorker {
    Schema dcSchema = null;
    public ConverterWorker(){
        //DC::tdc/20600/12,"sector/B,layer/B,component/S,order/B,TDC/I";
        SchemaBuilder b = new SchemaBuilder("DC::tdc",20600,12);
        dcSchema = b.addEntry("sector", "B", "")
                .addEntry("layer", "B", "")
                .addEntry("component", "S", "")
                .addEntry("order", "B", "")
                .addEntry("TDC", "I", "").build();
        
        //dcSchema.show();
    }
    /*
    @Override
    public void accept(DataEvent event) {
        Bank b = new Bank(dcSchema,1024);
        ((Event) event).read(b);
        Leaf tdc = new Leaf(42,11,"bbbsbil",4096);

        tdc.setRows(b.getRows());
        //System.out.printf(" rows = %d , leaf = %d\n",b.getRows(), tdc.getRows());
        for(int r = 0; r < b.getRows(); r++){
            tdc.putByte(1, r, b.getByte(0, r));
            tdc.putByte(2, r, b.getByte(1, r));
            tdc.putShort(3, r, b.getShort(2, r));
            tdc.putByte(4,  r, b.getByte(3, r));
            tdc.putInt(5,  r, b.getInt(4, r));
        }
        ((Event) event).write(tdc);
    }*/

    @Override
    public boolean init(DataSource src) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return true;
    }

    @Override
    public void execute(DataEvent event) {
        Bank b = new Bank(dcSchema,1024);
        ((Event) event).read(b);
        Leaf tdc = new Leaf(42,11,"bbbsbil",4096);

        tdc.setRows(b.getRows());
        //System.out.printf(" rows = %d , leaf = %d\n",b.getRows(), tdc.getRows());
        for(int r = 0; r < b.getRows(); r++){
            tdc.putByte(1, r, b.getByte(0, r));
            tdc.putByte(2, r, b.getByte(1, r));
            tdc.putShort(3, r, b.getShort(2, r));
            tdc.putByte(4,  r, b.getByte(3, r));
            tdc.putInt(5,  r, b.getInt(4, r));
        }
        ((Event) event).write(tdc);
        /*
        try {
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConverterWorker.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    
}
