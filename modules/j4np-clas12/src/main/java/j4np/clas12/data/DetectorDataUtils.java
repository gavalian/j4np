/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.data;

import j4np.hipo5.data.Bank;
import twig.data.H3F;

/**
 *
 * @author gavalian
 */
public class DetectorDataUtils {
    
    public static H3F getDetectorMap(int layers, int components, Bank b){
        H3F h3 = new H3F(components, 0.5, components + 0.5, 
                layers, 0.5, layers + 0.5,
                6,0.5,6.5);
        int s_ = b.getSchema().getEntryOrder("sector");
        int l_ = b.getSchema().getEntryOrder("layer");
        int c_ = b.getSchema().getEntryOrder("component");
        for(int i = 0; i < b.getRows(); i++){
            h3.setBinContent( b.getInt(c_, i)-1, b.getInt(l_, i)-1,  b.getInt(s_, i)-1, 1);
        }
        return h3;
    }
    
    public static String toLSVM(float[] data, double threshold){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < data.length; i++)
            if(data[i]>threshold) str.append(String.format(" %d:1", i+1));
        
        return str.toString();
    }
}
