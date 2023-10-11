/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.monitor;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.io.HipoReader;
import java.util.Arrays;
import twig.data.H1F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class Level3Analysis {
    
    public double threshold = 0.06;
    
    public int[] getTrigger(long triggerLong){
        
        int[] trigger = new int[7];
        long     bits =triggerLong;
        for(int i = 0; i < trigger.length; i++) {
            trigger[i] = 0;
            if( ((bits>>i)&(1L)) != 0L) trigger[i] = 1;
            //System.out.println(Arrays.toString(trigger));
        }
        return trigger;
    }
    
    public void analyze(String file){
        
        HipoReader r = new HipoReader(file);
        Bank[] banks = r.getBanks("REC::Particle","REC::Track","RUN::config");
        Event e = new Event();
        
        H1F hHD_COUNTER = new H1F("hHD_TRIGEFF",6,0.5,6.5);
        H1F hHD_COUNTER_ZERO = new H1F("hHD_TRIGEFF",6,0.5,6.5);
        H1F hAI_COUNTER_ZERO = new H1F("hHD_TRIGEFF",6,0.5,6.5);

         
        H1F hHD_TRIGEFF = new H1F("hHD_TRIGEFF",6,0.5,6.5);
        H1F hAI_TRIGEFF = new H1F("hAH_TRIGEFF",6,0.5,6.5);
        
        H1F hHD_TRIGPUR = new H1F("hHD_PURITY",6,0.5,6.5);
        H1F hAI_TRIGPUR = new H1F("hAH_PURITY",6,0.5,6.5);
        
        
        H1F h1 = new H1F("h1",120,0.0,1.0);
        H1F h2 = new H1F("h2",6,0.5,6.5);
        
        while(r.hasNext()){
            
            r.nextEvent(e);
            e.read(banks);
//            r.nextEvent(banks);
            
            long trigger = banks[2].getLong("trigger", 0);
            
            int[] tbits = this.getTrigger(trigger);
            int sector = 0;
            if(banks[0].getRows()>0){
                int pid = banks[0].getInt("pid", 0);
                if(pid==11){
                    sector = banks[1].getInt("sector", 0);
                }
            }
            
            Node aibits = e.read(5, 5); 
            float[] aitrigger = aibits.getFloat();
            if(sector>0){
                hHD_COUNTER.fill(sector);
                if(tbits[sector]>0){
                    hHD_TRIGEFF.fill(sector);
                    h1.fill(aitrigger[sector]);
                } else {
                    //h2.fill(aitrigger[sector]);
                }
                if(aitrigger[sector]>threshold) hAI_TRIGEFF.fill(sector);                
            }
            int k = 0;
            if(tbits[0]>0){
                if(sector>0) hHD_COUNTER_ZERO.fill(3);
                if(sector==0) {
                    hHD_COUNTER_ZERO.fill(1);

                    //if(aitrigger.length>0) if(aitrigger[0]>threshold) hHD_COUNTER_ZERO.fill(2);
                }
            }
            if(tbits[0]>0){
                if(aitrigger.length>0){
                    if(aitrigger[0]>threshold){
                        if(sector==0)  {hHD_COUNTER_ZERO.fill(4);}
                        else  {hHD_COUNTER_ZERO.fill(5);h2.fill(sector);}
                    }
                }
            }
                //else  hHD_TRIGPUR.fill(sector);
            
            /*
            if(aitrigger.length>0){
                if(aitrigger[0]>threshold){
                    if(sector==0) hAI_COUNTER_ZERO.fill(1);
                    else  hAI_TRIGPUR.fill(sector);

                }
            }*/
            
            System.out.println(" sector = " + sector + "  " + Arrays.toString(tbits) + " " + Arrays.toString(aibits.getFloat()));
            
        }
        
        hHD_TRIGEFF.divide(hHD_COUNTER);
        
        hHD_TRIGPUR.divide(hHD_COUNTER_ZERO.getBinContent(0));
        hAI_TRIGPUR.divide(hAI_COUNTER_ZERO.getBinContent(0));
        
        hAI_TRIGEFF.divide(hHD_COUNTER);
        
        TGCanvas c = new TGCanvas();
        c.view().divide(2, 3);
        
        c.cd(0).draw(hHD_TRIGEFF);
        c.cd(1).draw(hAI_TRIGEFF);
        c.cd(2).draw(hHD_TRIGPUR);
        c.cd(3).draw(hAI_TRIGPUR);
        
        c.cd(4).draw(hHD_COUNTER_ZERO);
        //c.cd(4).draw(h1);
        c.cd(5).draw(h2);
    }
    
    public static void main(String[] args){
        //String file = "output_level3_decoded_rec.h5";
        //String file = "output_level3_decoded_v3_rec.h5";
        String file = "output_level3_decoded_rec_v7.h5";
        Level3Analysis ana = new Level3Analysis();
        ana.analyze(file);
    }
}
