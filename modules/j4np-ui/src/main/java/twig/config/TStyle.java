/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.config;

import java.awt.BasicStroke;
import java.awt.Color;
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

    public static enum TwigStyle {
        PRESENTATION, PRESENTATION_DARK, PRESENTATION_BOLD, 
        ARTICLE, MONITOR, MONITOR_DARK, MATPLOTLIB
    }
    
    public int getDefaultGridLineColor() {
        return defaultGridLineColor;
    }

    public void setDefaultGridLineColor(int defaultGridLineColor) {
        this.defaultGridLineColor = defaultGridLineColor;
    }

    public int getDefaultGridLineStyle() {
        return defaultGridLineStyle;
    }

    public void setDefaultGridLineStyle(int defaultGridLineStyle) {
        this.defaultGridLineStyle = defaultGridLineStyle;
    }

    public int getDefaultGridLineWidth() {
        return defaultGridLineWidth;
    }

    public void setDefaultGridLineWidth(int defaultGridLineWidth) {
        this.defaultGridLineWidth = defaultGridLineWidth;
    }

    public Color getDefaultAxisBackgroundColor() {
        return defaultAxisBackgroundColor;
    }

    public void setDefaultAxisBackgroundColor(Color defaultAxisBackgroundColor) {
        this.defaultAxisBackgroundColor = defaultAxisBackgroundColor;
    }
    
    protected TPalette          palette = new TPalette();
    private static TStyle   globalStyle = new TStyle();
    
    protected TAxisAttributes defaultAxisAttributes = new TAxisAttributes();
    
    /**
     * Default fonts to be used with different components
     * of the graphics.
     */
    /*protected TAttributesFont defaultAxisLabelFont  = 
            new TAttributesFont("Avenir Next",Font.PLAIN,14);
    
    protected TAttributesFont defaultAxisTitleFont  = 
            new TAttributesFont("Avenir Next",Font.PLAIN,18);
    */
    /*protected TAttributesFont defaultPaveTextFont  = 
            new TAttributesFont("Avenir Next",Font.PLAIN,18);
    */
    protected TAttributesFont defaultLegendFont  = 
            new TAttributesFont("Avenir Next",Font.PLAIN,18);
    
    protected TAttributesFont defaultStatsFont  = 
            new TAttributesFont("Avenir Next",Font.PLAIN,18);
    
    protected TAttributesMarker  defaultMarkerStyle = 
            new TAttributesMarker();
    
    
    protected int  canvasBackgroundColor = 0;
    protected int          axisLineColor = 1;
    protected int         axisLabelColor = 1;
    protected int         axisTitleColor = 1;
    
    
    protected Font  defaultPaveTextFont = new Font("Avenir",Font.PLAIN,14);

    public Color getDefaultCanvasColor() {
        return defaultCanvasColor;
    }

    public void setDefaultCanvasColor(Color defaultCanvasColor) {
        this.defaultCanvasColor = defaultCanvasColor;
    }
    protected Font defaultAxisLabelFont = new Font("Avenir",Font.PLAIN,18);
    protected Font defaultAxisTitleFont = new Font("Avenir",Font.PLAIN,18);
    
    protected Color defaultAxisBackgroundColor = null;
    protected Color defaultCanvasColor = Color.WHITE;
    
    protected int   defaultGridLineColor = 171;
    protected int   defaultGridLineStyle = 1;
    protected int   defaultGridLineWidth = 1;
        
    
    
    protected TAxisAttributes attributeAxisX = new TAxisAttributes();
    protected TAxisAttributes attributeAxisY = new TAxisAttributes();
    protected TAxisAttributes attributeAxisZ = new TAxisAttributes();
    
    public Font getDefaultAxisLabelFont() {
        return defaultAxisLabelFont;
    }

    public final void setDefaultAxisLabelFont(Font defaultAxisLabelFont) {
        this.defaultAxisLabelFont = defaultAxisLabelFont;
    }

    public Font getDefaultAxisTitleFont() {
        return defaultAxisTitleFont;
    }

    
    public TAxisAttributes getAxisAttrX(){ return this.attributeAxisX;}
    public TAxisAttributes getAxisAttrY(){ return this.attributeAxisY;}
    public TAxisAttributes getAxisAttrZ(){ return this.attributeAxisZ;}
    
    public final void setDefaultAxisTitleFont(Font defaultAxisTitleFont) {
        this.defaultAxisTitleFont = defaultAxisTitleFont;
    }
    
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
        this.setDefaultAxisLabelFont(new Font("Palatino",Font.PLAIN,18));
        this.setDefaultAxisTitleFont(new Font("Palatino",Font.PLAIN,20));
        this.setDefaultPaveTextFont(new Font("PT Sans",Font.PLAIN,20));
        
        String fn = "PT Serif";
        this.attributeAxisX.setAxisLabelFont(new Font(fn,Font.PLAIN,16) );
        this.attributeAxisY.setAxisLabelFont(new Font(fn,Font.PLAIN,16) );
        this.attributeAxisX.setAxisTitleFont(new Font(fn,Font.PLAIN,18) );
        this.attributeAxisY.setAxisTitleFont(new Font(fn,Font.PLAIN,18) );        
        this.attributeAxisZ.setAxisLabelFont(new Font(fn,Font.PLAIN,16) );
        this.attributeAxisZ.setAxisTitleFont(new Font(fn,Font.PLAIN,18) );
    }
    
    
    public static void setStyle(TwigStyle type){
        TStyle style = TStyle.getInstance();
        if(type == TwigStyle.PRESENTATION){
           style.setDefaultAxisLabelFont(new Font("Palatino",Font.PLAIN,18));
           style.setDefaultAxisTitleFont(new Font("Palatino",Font.PLAIN,20));
           
           style.getAxisAttrX().setAxisLabelFont(new Font("Palatino",Font.PLAIN,18));
           style.getAxisAttrY().setAxisLabelFont(new Font("Palatino",Font.PLAIN,18));
           style.getAxisAttrX().setAxisTitleFont(new Font("Palatino",Font.PLAIN,20));
           style.getAxisAttrY().setAxisTitleFont(new Font("Palatino",Font.PLAIN,20));
           style.setDefaultPaveTextFont(new Font("Palatino",Font.PLAIN,20));
        }
        
        if(type == TwigStyle.PRESENTATION_BOLD){           
           
           style.getAxisAttrX().setAxisLabelFont(new Font("Palatino",Font.BOLD,18));
           style.getAxisAttrY().setAxisLabelFont(new Font("Palatino",Font.BOLD,18));
           style.getAxisAttrX().setAxisTitleFont(new Font("Palatino",Font.BOLD,20));
           style.getAxisAttrY().setAxisTitleFont(new Font("Palatino",Font.BOLD,20));
           
           style.setDefaultPaveTextFont(new Font("Palatino",Font.BOLD,20));
        }
        
        if(type == TwigStyle.PRESENTATION_DARK){
           style.setDefaultAxisLabelFont(new Font("Palatino",Font.PLAIN,18));
           style.setDefaultAxisTitleFont(new Font("Palatino",Font.PLAIN,20));
           
           style.getAxisAttrX().setAxisLabelFont(new Font("Palatino",Font.PLAIN,18));
           style.getAxisAttrY().setAxisLabelFont(new Font("Palatino",Font.PLAIN,18));
           style.getAxisAttrX().setAxisTitleFont(new Font("Palatino",Font.PLAIN,20));
           style.getAxisAttrY().setAxisTitleFont(new Font("Palatino",Font.PLAIN,20));
           style.setDefaultPaveTextFont(new Font("Palatino",Font.PLAIN,20));
        }
        
        if(type == TwigStyle.MONITOR){
           style.setDefaultAxisLabelFont(new Font("Avenir",Font.PLAIN,12));
           style.setDefaultPaveTextFont(new Font("Avenir",Font.PLAIN,12));
           style.getAxisAttrX().setAxisLabelFont(new Font("Avenir",Font.PLAIN,12));
           style.getAxisAttrY().setAxisLabelFont(new Font("Avenir",Font.PLAIN,12));
           style.getAxisAttrX().setAxisTitleFont(new Font("Avenir",Font.PLAIN,12));
           style.getAxisAttrY().setAxisTitleFont(new Font("Avenir",Font.PLAIN,12));
           
           style.setDefaultAxisTitleFont(new Font("Avenir",Font.PLAIN,14));
           
        }
        
        if(type == TwigStyle.MONITOR_DARK){
           style.setDefaultAxisLabelFont(new Font("Avenir",Font.PLAIN,12));
           style.setDefaultPaveTextFont(new Font("Avenir",Font.PLAIN,12));
           style.getAxisAttrX().setAxisLabelFont(new Font("Avenir",Font.PLAIN,12));
           style.getAxisAttrY().setAxisLabelFont(new Font("Avenir",Font.PLAIN,12));
           style.getAxisAttrX().setAxisTitleFont(new Font("Avenir",Font.PLAIN,12));
           style.getAxisAttrY().setAxisTitleFont(new Font("Avenir",Font.PLAIN,12));
           
           style.getAxisAttrX().setAxisGridLineColor(51);
           style.getAxisAttrY().setAxisGridLineColor(51);
           
           style.getAxisAttrX().setAxisLineColor(31);
           style.getAxisAttrY().setAxisLineColor(31);
           style.getAxisAttrX().setAxisLabelColor(31);
           style.getAxisAttrX().setAxisTitleColor(31);
           style.getAxisAttrY().setAxisLabelColor(31);
           style.getAxisAttrY().setAxisTitleColor(31);
           style.setDefaultAxisBackgroundColor(new Color(50,53,50));
           style.setDefaultCanvasColor(new Color(40,43,40));

           style.setDefaultAxisTitleFont(new Font("Avenir",Font.PLAIN,14));
           
        }
    }
    
    public static void setStyle(int type){
        switch(type){

            case 1: TStyle.setStyle(TwigStyle.PRESENTATION); break;
            case 2: TStyle.setStyle(TwigStyle.PRESENTATION_DARK); break;
            case 3: TStyle.setStyle(TwigStyle.MONITOR); break;
            case 4: TStyle.setStyle(TwigStyle.MONITOR_DARK); break;
            case 5: TStyle.setStyle(TwigStyle.MATPLOTLIB); break;

            default: TStyle.setStyle(TwigStyle.PRESENTATION); break;
        }
    }
    
    public final void setDefaultPaveTextFont(Font f){
        this.defaultPaveTextFont = f;
    }
    
    public Font getDefaultPaveTextFont(){
        return this.defaultPaveTextFont;
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
