/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.config;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class TStyle {
    
    protected TPalette          palette = new TPalette();
    private static TStyle globalStyle = new TStyle();
    
    protected TAxisAttributes defaultAxisAttributes = new TAxisAttributes();
    
    /**
     * Default fonts to be used with different components
     * of the graphics.
     */
    protected TAttributesFont defaultAxisLabelFont  = 
            new TAttributesFont("Avenir Next",Font.PLAIN,14);
    
    protected TAttributesFont defaultAxisTitleFont  = 
            new TAttributesFont("Avenir Next",Font.PLAIN,18);
    
    protected TAttributesFont defaultPaveTextFont  = 
            new TAttributesFont("Avenir Next",Font.PLAIN,18);
    
    protected TAttributesFont defaultLegendFont  = 
            new TAttributesFont("Avenir Next",Font.PLAIN,18);
    
    protected TAttributesFont defaultStatsFont  = 
            new TAttributesFont("Avenir Next",Font.PLAIN,18);
    
    protected TAttributesMarker  defaultMarkerStyle = 
            new TAttributesMarker();
    
    
    protected int  canvasBackgroundColor = 0;
    protected int          axisLineColor = 0;
    protected int         axisLabelColor = 0;
    protected int         axisTitleColor = 0;
    
    
    public static List<float[]> dashPatterns = Arrays.asList(
            new float[]{10.0f,0.0f},
            new float[]{10.0f,5.0f},
            new float[]{5.0f,1.0f}, 
            new float[]{5.0f,5.0f},
            new float[]{5.0f,10.0f},
             
            new float[]{10.0f,5.0f,2.0f,5.0f},
            new float[]{14.0f,4.0f,3.0f,5.0f},
            
            new float[]{3.0f,5.0f,3.0f,5.0f},
            
            new float[]{3.0f,3.0f,3.0f,3.0f},
            
            new float[]{2.0f,8.0f,2.0f,4.0f},
            new float[]{2.0f,6.0f,2.0f,2.0f},
            new float[]{1.0f,10.0f},
            new float[]{1.0f,1.0f},                   
           
            new float[]{3.0f,10.0f,1.0f,10.0f},
            new float[]{3.0f,5.0f,1.0f,5.0f},
            new float[]{3.0f,10.0f,1.0f,10.0f,1.0f,10.0f},
            new float[]{3.0f,1.0f,1.0f,1.0f,1.0f,1.0f}                                    
    );
    

    public TStyle(){
        
    }
    
    public static TStyle getInstance(){ 
        /*if(globalStyle==null){
            globalStyle = new TStyle();
        }*/
        return globalStyle;
    }


    public int getCanvasBackgroundColor(){
        return this.canvasBackgroundColor;
    }
    
    public void setCanvasBackgroundColor(int col){
        canvasBackgroundColor = col;
    }
    
    public int getAxisLineColor() {
        return axisLineColor;
    }

    public void setAxisLineColor(int axisLineColor) {
        this.axisLineColor = axisLineColor;
    }

    public int getAxisLabelColor() {
        return axisLabelColor;
    }

    public void setAxisLabelColor(int axisLabelColor) {
        this.axisLabelColor = axisLabelColor;
    }

    public int getAxisTitleColor() {
        return axisTitleColor;
    }

    public void setAxisTitleColor(int axisTitleColor) {
        this.axisTitleColor = axisTitleColor;
    }
    
    public TPalette getPalette(){ return palette; }
    
    public BasicStroke getLineStroke(int style, int width){        
        if(style<1) return new BasicStroke(width);
        int strokeStyle = style;
        if(style>=dashPatterns.size())
            strokeStyle= style%dashPatterns.size();
        return new BasicStroke(width, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 20.0f, dashPatterns.get(strokeStyle), 0.0f);        
    }
    
    public static Font createFont(String resource){
        try {
            InputStream is = TStyle.class.getResourceAsStream(resource);
            //Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font;
        } catch (FontFormatException ex) {
            Logger.getLogger(TStyle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TStyle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void setDarkMode(){
        
    }
    
    public static Font createFont(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            InputStream is = TStyle.class.getResourceAsStream("fonts/Brushed.ttf");
            //Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            ge.registerFont(font);
            return font;
        } catch (FontFormatException ex) {
            Logger.getLogger(TStyle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TStyle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
