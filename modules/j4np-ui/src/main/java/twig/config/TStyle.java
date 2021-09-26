/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.config;

import java.awt.BasicStroke;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class TStyle {
    
    protected TPalette palette = new TPalette();
    protected static TStyle globalStyle = new TStyle();
    protected TAxisAttributes defaultAxisAttributes = new TAxisAttributes();
    
    public static List<float[]> dashPatterns = Arrays.asList(
            new float[]{10.0f,0.0f},
            new float[]{10.0f,5.0f},
            new float[]{10.0f,5.0f,2.0f,5.0f},
            new float[]{2.0f,5.0f,2.0f,5.0f},
            new float[]{2.0f,8.0f,2.0f,4.0f},
            new float[]{2.0f,6.0f,2.0f,2.0f},
            new float[]{1.0f,10.0f},
            new float[]{1.0f,1.0f},
            new float[]{5.0f,10.0f},
            new float[]{5.0f,5.0f},
            new float[]{5.0f,1.0f},            
            new float[]{3.0f,10.0f,1.0f,10.0f},
            new float[]{3.0f,5.0f,1.0f,5.0f},
            new float[]{3.0f,10.0f,1.0f,10.0f,1.0f,10.0f},
            new float[]{3.0f,1.0f,1.0f,1.0f,1.0f,1.0f}                                    
    );
    
    public TStyle(){
        
    }
    
    public static TStyle getInstance(){ return globalStyle;}
    
    public TPalette getPalette(){ return palette; }
    
    public BasicStroke getLineStroke(int style, int width){
        
        if(style<2) return new BasicStroke(width);
        int strokeStyle = style;
        if(style>=dashPatterns.size())
            strokeStyle= style%dashPatterns.size();
            return new BasicStroke(width, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 20.0f, dashPatterns.get(strokeStyle), 0.0f);
            
    }
}
