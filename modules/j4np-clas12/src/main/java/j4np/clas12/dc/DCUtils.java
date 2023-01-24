/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.dc;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.List;
import twig.data.DataGroup;
import twig.data.H2F;

/**
 *
 * @author gavalian
 */
public class DCUtils {
    
    public static boolean contains(int number, int[] store){
        for(int i = 0; i < store.length; i++) if(number==store[i]) return true;
        return false;
    }
    
    public static DataGroup getHistoHits(HipoReader r, int event, int[] orders){
        List<H2F> h = new ArrayList<>();
        for(int k = 1; k <= 6; k++) h.add(new H2F("dc-"+k,112,0.5,112.5,36,0.5,36.5));
        Bank  b = r.getBank("DC::tdc");
        Event e = new Event();
        r.getEvent(e, event);
        e.read(b);
        for(int k = 0; k < b.getRows(); k++){
            int sector = b.getInt("sector", k)-1;
            int  layer = b.getInt("layer", k);
            int   wire = b.getInt("component", k);
            int  order = b.getInt("order", k);
            if(orders.length==0){
                h.get(sector).fill(wire, layer);
            } else {
                if(DCUtils.contains(order, orders)==true){
                    h.get(sector).fill(wire, layer);
                }
            }
        }
        DataGroup grp = new DataGroup();
        for(int k = 0; k < h.size(); k++) grp.add(h.get(k));
        return grp;
    }
    
    public static DataGroup getHistoTracks(HipoReader r, int event, String bankname, boolean filter){
        List<H2F> h = new ArrayList<>();
        for(int k = 1; k <= 6; k++) h.add(new H2F("trk-"+k,112,0.5,112.5,36,0.5,36.5));
        Bank  b = r.getBank(bankname);
        
        Event e = new Event();
        r.getEvent(e, event);
        e.read(b);
        for(int k = 0; k < b.getRows(); k++){
            int sector = b.getInt("sector", k)-1;
            int   supl = b.getInt("superlayer", k);
            int    lay = b.getInt("layer", k);
            int   wire = b.getInt("wire", k);
            int    trk = b.getInt("trkID", k);
            if(filter==false){
                int layer = (supl-1)*6 + lay;
                h.get(sector).fill(wire, layer);
            } else {
                if(trk>0){
                    int layer = (supl-1)*6 + lay;
                    h.get(sector).fill(wire, layer);
                }
            }
        }
        DataGroup grp = new DataGroup();
        for(int k = 0; k < h.size(); k++) grp.add(h.get(k));
        return grp;
    }
}
