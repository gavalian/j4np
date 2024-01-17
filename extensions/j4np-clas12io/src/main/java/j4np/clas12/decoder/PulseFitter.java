/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.decoder;

import j4np.hipo5.data.Node;

/**
 *
 * @author gavalian
 */
public abstract class PulseFitter {
    
    public abstract void fit(PulseFitterParams params, PulseFitterConfig config, Node pulse);
    
    public static class PulseFitterParams {
        int             ADC = 0;
        int        pedestal = 0;
        double         time = 0.0;
    }
    
    public static class PulseFitterConfig {
        int    NSA = 0;
        int    NSB = 0;
        int    TET = 0;
        double pedestal = 0.0;
    }
}
