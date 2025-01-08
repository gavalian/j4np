/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import java.util.Random;
import twig.data.H1F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class H1FPerformance {
    public static void benchmark(){
        float[] data = new float[128000];
        Random r = new Random();
        for(int i = 0; i < data.length; i++) data[i] = (float) (r.nextGaussian()*0.1+0.5);
        
        int iter = 1000;
        H1F h = new H1F("h",120,0.0,1.0);
        long then = System.currentTimeMillis();
        for(int i = 0; i < iter; i++){
            for(int d = 0; d < data.length; d++){
                h.fill(data[d]);
            }
        }
        long now = System.currentTimeMillis();
        
        System.out.printf(" entries = %d, time = %d\n",h.getEntries(), now-then);
        TGCanvas c = new TGCanvas();
        c.draw(h);
    }
    
    public static void main(String[] args){

       for(int i = 0; i < 5; i++) H1FPerformance.benchmark();

    }
}
