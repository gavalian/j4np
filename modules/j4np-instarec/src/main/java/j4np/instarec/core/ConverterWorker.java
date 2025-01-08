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
    Schema ecSchema = null;
    Schema ftSchema = null;
    Schema htSchema = null;
    
    public ConverterWorker(){
        //DC::tdc/20600/12,"sector/B,layer/B,component/S,order/B,TDC/I";
        SchemaBuilder b = new SchemaBuilder("DC::tdc",20600,12);
        dcSchema = b.addEntry("sector", "B", "")
                .addEntry("layer", "B", "")
                .addEntry("component", "S", "")
                .addEntry("order", "B", "")
                .addEntry("TDC", "I", "").build();
        
        SchemaBuilder b2 = new SchemaBuilder("ECAL::adc",20700,11);
        ecSchema = b2.addEntry("sector", "B", "")
                .addEntry("layer", "B", "")
                .addEntry("component", "S", "")
                .addEntry("order", "B", "")
                .addEntry("ADC", "I", "")
                .addEntry("time", "F", "pulse time")
                .addEntry("ped", "S", "pedestal").build();
        
        SchemaBuilder b3 = new SchemaBuilder("FTOF::tdc",21200,12);
        ftSchema = b3.addEntry("sector", "B", "")
                .addEntry("layer", "B", "")
                .addEntry("component", "S", "")
                .addEntry("order", "B", "")
                .addEntry("TDC", "I", "").build();
        
        SchemaBuilder b4 = new SchemaBuilder("HTCC::adc",21500,11);
        htSchema = b4.addEntry("sector", "B", "")
                .addEntry("layer", "B", "")
                .addEntry("component", "S", "")
                .addEntry("order", "B", "")
                .addEntry("ADC", "I", "")
                .addEntry("time", "F", "pulse time")
                .addEntry("ped", "S", "pedestal").build();
       
        //dcSchema.show();
    }


    @Override
    public boolean init(DataSource src) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return true;
    }

    @Override
    public void execute(DataEvent event) {
        
        Bank b = new Bank(dcSchema,2048);
        ((Event) event).read(b);
        Leaf tdc = new Leaf(42,11,"bbbsbil",4096);

        tdc.setRows(b.getRows());
        //System.out.printf(" rows = %d , leaf = %d\n",b.getRows(), tdc.getRows());
        for(int r = 0; r < b.getRows(); r++){
            tdc.putByte(0, r,(byte) 6); // this is DC type
            tdc.putByte(1, r, b.getByte(0, r));
            tdc.putByte(2, r, b.getByte(1, r));
            tdc.putShort(3, r, b.getShort(2, r));
            tdc.putByte(4,  r, b.getByte(3, r));
            tdc.putInt(5,  r, b.getInt(4, r));
        }
        
        Bank bft = new Bank(ftSchema,1024);
        ((Event) event).read(bft);
        int tdcRows = tdc.getRows();
        tdc.setRows(tdcRows + bft.getRows());
        for(int r = 0; r < bft.getRows(); r++){
            int rj = r + tdcRows;
            tdc.putByte(  0,  rj,(byte) 12); // this is DC type
            tdc.putByte(  1,  rj, bft.getByte(0, r));
            tdc.putByte(  2,  rj, bft.getByte(1, r));
            tdc.putShort( 3, rj, bft.getShort(2, r));
            tdc.putByte(  4,  rj, bft.getByte(3, r));
            tdc.putInt(   5,   rj, bft.getInt(4, r));
        }
        
        Bank bec = new Bank(ecSchema,1024);
        ((Event) event).read(bec);
        
        Leaf adc = new Leaf(42,12,"bbbsbifs",4096);
        adc.setRows(bec.getRows());
        for(int r = 0; r < bec.getRows(); r++){
            adc.putByte(  0,  r,(byte) 7); // this is ECAL type
            adc.putByte(  1,  r, bec.getByte(  0, r));
            adc.putByte(  2,  r, bec.getByte(  1, r));
            adc.putShort( 3,  r, bec.getShort( 2, r));
            adc.putByte(  4,  r, bec.getByte(  3, r));
            adc.putInt(   5,  r, bec.getInt(   4, r));
            adc.putFloat( 6,  r, bec.getFloat( 5, r));
            adc.putShort( 7,  r, bec.getShort( 6, r));
        }
        
        Bank bht = new Bank(htSchema,1024);
        ((Event) event).read(bht);
        int adcRows = adc.getRows();
        adc.setRows(adcRows + bht.getRows());
       
        for(int r = 0; r < bht.getRows(); r++){
            int rj = r + adcRows;
            adc.putByte(  0,  rj ,(byte) 15); // this is HTCC type
            adc.putByte(  1,  rj, bht.getByte(  0, r));
            adc.putByte(  2,  rj, bht.getByte(  1, r));
            adc.putShort( 3,  rj, bht.getShort( 2, r));
            adc.putByte(  4,  rj, bht.getByte(  3, r));
            adc.putInt(   5,  rj, bht.getInt(   4, r));
            adc.putFloat( 6,  rj, bht.getFloat( 5, r));
            adc.putShort( 7,  rj, bht.getShort( 6, r));
        }
        
        
        ((Event) event).write(tdc);
        ((Event) event).write(adc);
    }
    /*
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
    }

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
            tdc.putByte(  0, r, (byte) 6);
            tdc.putByte(  1, r, b.getByte(  0, r));
            tdc.putByte(  2, r, b.getByte(  1, r));
            tdc.putShort( 3, r, b.getShort( 2, r));
            tdc.putByte(  4, r, b.getByte(  3, r));
            tdc.putInt(   5, r, b.getInt(   4, r));
        }
        //tdc.print();
        ((Event) event).write(tdc);
        
        try {
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConverterWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
}
