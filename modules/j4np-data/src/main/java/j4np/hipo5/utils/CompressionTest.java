/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.utils;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;

/**
 *
 * @author gavalian
 */
public class CompressionTest {
    
    public static int convert(float value){
        if(Math.abs(value)>10) return 0;
        double v = (value + 10)*24000;
        return (int) v;
    }
    
    public static void main(String[] args){
        SchemaFactory f = new SchemaFactory();
        f.readFile("event.json");
        f.show();
        f.getSchema("RECI::Particle").show();
        
        HipoReader r = new HipoReader("rec_particle.h5");
        HipoWriter w = new HipoWriter();
        w.setCompressionType(0);
        w.getSchemaFactory().copy(f);
        
        w.open("output.h5");
        
        Event e = new Event();
        Bank[] b = r.getBanks("REC::Particle");
        double factor = 12000;
        while(r.nextEvent(b)){
            //b[0].show();
            Bank bi = new Bank(f.getSchema("RECI::Particle"),b[0].getRows());
            for(int i = 0; i < b[0].getRows(); i++){
                bi.putInt("pid", i, b[0].getInt("pid", i));
                bi.putByte("charge", i, b[0].getByte("charge", i));
                bi.putShort("status", i, b[0].getShort("status", i));
                bi.putInt("px", i, CompressionTest.convert(b[0].getFloat("px", i)));
                bi.putInt("py", i, CompressionTest.convert(b[0].getFloat("py", i)));
                bi.putInt("pz", i, CompressionTest.convert(b[0].getFloat("pz", i)));
                bi.putInt("vx", i, CompressionTest.convert(b[0].getFloat("vx", i)));
                bi.putInt("vy", i, CompressionTest.convert(b[0].getFloat("vy", i)));
                bi.putInt("vz", i, CompressionTest.convert(b[0].getFloat("vz", i)));
                bi.putInt("vt", i, (int) (b[0].getFloat("vt", i)*factor));
                bi.putInt("beta", i, (int) (b[0].getFloat("beta", i)*factor));
                bi.putInt("chi2pid", i, (int) (b[0].getFloat("chi2pid", i)*factor));
            }
            //bi.show();
            e.reset();
            e.write(bi);
            w.addEvent(e);
        }
        w.close();
    }
}
