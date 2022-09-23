/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.clas12.track;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class TrackMatrixOutput {
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/DataSpace/denoise/out_45_flt.h5";
        HipoReader r = new HipoReader(file);
        Bank b = r.getBank("TimeBasedTrkg::TBHits");
        Bank h = r.getBank("HitBasedTrkg::Hits");
        
        DcMatrix2D  m = new DcMatrix2D();
        DcMatrix2D mh = new DcMatrix2D();
        
        Event e = new Event();
        int counter = 0;
        
        TrackMatrixIO mio = new TrackMatrixIO();
        
        while(r.next()==true&&counter<25){
            r.nextEvent(e);
            e.read(b);
            e.read(h);
            //b.show();
            List<Integer> index = new ArrayList<>();
            for(int j = 0; j < b.getRows(); j++) 
                if(b.getInt("sector", j)==1) index.add(j);
            
            List<Integer> index_h = new ArrayList<>();
            for(int j = 0; j < h.getRows(); j++) 
                if(h.getInt("sector", j)==1) index_h.add(j);
            
            if(index.size()>32){
                m.reset(); 
                //index.clear(); index.add(1); index.add(4);
                //m.fill(b, index);
                m.fill(b, index);
                mh.reset();
                mh.fill(h, index_h);
                //m.set(1, 1, 1.0f);
                //m.set(2, 2, 1.0f);
                System.out.println("----> " + index.size() + " non-zero >>> " + m.count());
                m.show();
                mio.saveImage(  m, "dc_image_trkg"+counter+".png");
                mio.saveImage( mh, "dc_image_back"+counter+".png");
                counter++;
            }
        }
    }
}
