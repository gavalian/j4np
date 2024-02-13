/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.et;

import j4np.data.base.FrameWorker;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import java.util.List;
import java.util.Random;


/*
 * internal data format of the nodes:
 *
 * -----------------------------------
 * TDC: format(bbsbil)
 *  0 >   b : sector
 *  1 >   b : layer
 *  2 >   s : component
 *  3 >   b : order
 *  4 >   i : time (tdc value)
 *  5 >   l : timestamp
 * -----------------------------------
 * ADC: format(bbsbifs)
 *  0 >   b : sector
 *  1 >   b : layer
 *  2 >   s : component
 *  3 >   b : order
 *  4 >   i : ADC value
 *  5 >   f : time (fitted from the pulse)
 *  6 >   s : pedestal 
 * ---------------------------------------
 */

/**
 *
 * @author gavalian
 */
public class DataBankCounter extends  FrameWorker<Event> {

    private long ec_adc_count = 0L;
    private long dc_tdc_count = 0L;

    private double av_adc_value = 0.0;
    private double av_tdc_value = 0.0;
    
    private long eventCounter = 0L;
    private long frameCounter = 0L;
    /*====================================================*/
    
    private final double iADC_to_MEV  = 1.0/10000.0;  
    private final Random rand = new Random();
    
    @Override
    public void execute(List<Event> eList) {
        
        int nEvents = eList.size();
        eventCounter += nEvents;
        frameCounter++;
        CompositeNode  tdcnode = new CompositeNode(1024*12); // allocate node with 12kb size
        CompositeNode  adcnode = new CompositeNode(1024*12); // allocate node with 12kb size
        
        for(int i = 0; i < nEvents; i++){
            Event evt = eList.get(i);
            evt.read(adcnode, 11, 2);
            evt.read(tdcnode, 12, 1);
            
            int nrows_tdc = tdcnode.getRows(); // the format for tdc banks i "bbsbil" 
            int nrows_adc = adcnode.getRows(); // the format for adc is "bbsbifs"
            
            this.ec_adc_count += nrows_adc;
            this.dc_tdc_count += nrows_tdc;

            double tdc_av = 0.0;
            
            for(int r = 0; r < nrows_tdc; r++){
                int sector = tdcnode.getInt(0,r);
                int  layer = tdcnode.getInt(1,r);
                int   comp = tdcnode.getInt(2,r);
                int  order = tdcnode.getInt(3,r);
                int    tdc = tdcnode.getInt(4,r);
                tdc_av += tdc; 
            }
            if(nrows_tdc>0){
                this.av_tdc_value += tdc_av/nrows_tdc;
                this.av_tdc_value = av_tdc_value*0.5;
            }
            double adc_av = 0.0;
            
            for(int r = 0; r < nrows_adc; r++){
                int  sector = adcnode.getInt(0,r);
                int   layer = adcnode.getInt(1,r);
                int    comp = adcnode.getInt(2,r);
                int   order = adcnode.getInt(3,r);
                int     adc = adcnode.getInt(4, r);//
                double time = adcnode.getDouble(5, r);
                
                adc_av += adc; 
                
                double energy = adc*this.iADC_to_MEV; // the energy deposited in the strip
            }
            if(nrows_adc>0){
                this.av_adc_value += adc_av/nrows_adc;
                this.av_adc_value = av_adc_value*0.5;
            }
        }
        if(frameCounter%100==0) this.printSummary();
        
        this.write(eList);
    }
    
    public void write(List<Event> eList){
        int nEvents = eList.size();
        for(int e = 0; e < nEvents; e++){
            int[] trigger = new int[]{0,0,0,0,0,0};
            for(int t = 0; t < trigger.length; t++){
                double v = rand.nextDouble();
                if(v>0.5) trigger[t] = 1;
            }
            Node node = new Node(4,1,trigger);
            eList.get(e).write(node);
        }
    }
    
    public void printSummary(){
        System.out.printf(":::: events %12d >>> averages : tdc = %9.2f , adc = %9.2f, rows : %8d %8d \n",
                this.eventCounter, this.av_tdc_value, 
                this.av_adc_value, this.dc_tdc_count, 
                this.ec_adc_count);
    }    
    
}
