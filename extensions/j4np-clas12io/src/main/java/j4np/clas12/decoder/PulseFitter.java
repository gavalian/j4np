/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.decoder;

import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Node;

/**
 *
 * @author gavalian
 */
public abstract class PulseFitter {
    
    public abstract void fit(PulseFitterParams params, PulseFitterConfig config, CompositeNode pulse, int offset, int length);
    
    public static class PulseFitterParams {
       public int             ADC = 0;
       public int             MAX = 0;
       public int        position = 0;
       public int        pedestal = 0;
       public double         time = 0.0;
       public int        timeWord = 0;
       
       @Override
       public String toString(){
           return String.format("ADC = %d, MAX = %d, PED = %d,  POS = %d, time = %8.5f", 
                   ADC,MAX, pedestal, position, time);
       }
    }
    
    public static class PulseFitterConfig {
        public int    NSA = 0;
        public int    NSB = 0;
        public int    TET = 0;
        public double pedestal = 0.0;
        
        @Override
        public String toString(){
            return String.format("%4d %4d %4d %8.5f", NSA,NSB,TET,pedestal);
        }
    }
}
