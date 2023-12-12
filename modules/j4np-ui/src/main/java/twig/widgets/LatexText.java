/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import javax.swing.JFrame;


/**
 *
 * @author gavalian
 */
public class LatexText {
    
    public enum TextAlign {
        LEFT, CENTER, RIGHT, TOP, BOTTOM,
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT,
        BOTTOM_RIGHT
    };
    
    public enum TextRotate {
        NONE, LEFT, RIGHT, UPSIDEDOWN
    };
    
    public  static int  ALIGN_LEFT    = 0;
    public  static int  ALIGN_CENTER  = 1;
    public  static int  ALIGN_RIGTH   = 2;
    public  static int  ALIGN_TOP     = 3;
    public  static int  ALIGN_BOTTOM  = 4;
    
    public  static int  ROTATE_NONE   = 11;
    public  static int  ROTATE_LEFT   = 12;
    public  static int  ROTATE_RIGHT  = 13;
    
    //private String  textFamily    = "Avenir";//"Avenir";//"SansSerif";
    //private int     textFontSize  = 14;
    private Double  positionX     = 0.0;
    private Double  positionY     = 0.0;
    
    private AttributedString   latexString = null;
    private String             asciiString = "";
    private Color              latexTextColor = Color.BLACK;    
    private Font               textFont = new Font("Avenir",Font.PLAIN,14);
    //private Font               textFont = new Font("Ariel",Font.PLAIN,18);
    
    
    public LatexText(String text, double xc, double yc){
        this.setText(text);
        this.setPosition(xc, yc);
        //this.setFont(textFamily);
        //this.setFontSize(textFontSize);
    }
    
     public LatexText(String text){
        this.setText(text);
        this.setPosition(0.0,0.0);
        //this.setFont(textFamily);
        //this.setFontSize(textFontSize);
    }
     
    public final void setText(String text){
    	asciiString = text;
        if(asciiString.length()>0){
            String ltx  = LatexTextTools.convertUnicode(text);
            latexString = LatexTextTools.convertSubAndSuperscript(ltx);
            //latexString.addAttribute(TextAttribute.FONT,textFont);
            setAttributedStringFont(textFont);
        }
    }
    
    public final String getTextString(){
        return asciiString;
    }
    
   public final void setPosition(double xr, double yr){
        this.positionX = xr;
        this.positionY = yr;
    }
   
    public final void setColor(Color color){
        this.latexTextColor = color;
    }
    
    public final void setFont(Font font){
        textFont = font;
        setAttributedStringFont(font);
        //latexString.addAttribute(TextAttribute.FONT, font);
    }
    
    public final  Font getFont(){ return textFont;}
    
    public Color    getColor(){return latexTextColor;}
            
    public double getX(){ return this.positionX;}
    public double getY(){ return this.positionY;}
    
    public AttributedString getText(){ return latexString;}
    
    protected void setAttributedStringFont(Font font){
        if(this.asciiString.length()>0){
            latexString.addAttribute(TextAttribute.FAMILY, font.getFontName());
            latexString.addAttribute(TextAttribute.SIZE, font.getSize());
            int style = font.getStyle();
            if(style==1) latexString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            //if(style==2) latexString.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
            //latexString.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
            //latexString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            //latexString.addAttribute(TextAttribute.WEIGHT, font.getStyle());
        }
    }
    /*
    public final void setFont(String fontname){
        this.textFamily = fontname;
        if(this.latexString.getIterator().getEndIndex()>0){
        //System.out.println("INDEX = " + this.latexString.getIterator().getEndIndex());
            latexString.addAttribute(TextAttribute.FAMILY, fontname);            
            latexString.addAttribute(TextAttribute.WEIGHT,TextAttribute.WEIGHT_LIGHT);
        }
    }
    
    public final void setFont(String fontname, int size, int weight){
        this.textFamily = fontname;
        if(this.latexString.getIterator().getEndIndex()>0){
        //System.out.println("INDEX = " + this.latexString.getIterator().getEndIndex());
            latexString.addAttribute(TextAttribute.FAMILY, fontname);
            latexString.addAttribute(TextAttribute.SIZE, size);
            
            switch(weight){
                case 0: latexString.addAttribute(TextAttribute.WEIGHT,TextAttribute.WEIGHT_LIGHT); break;
                case 1: latexString.addAttribute(TextAttribute.WEIGHT,TextAttribute.WEIGHT_MEDIUM); break;
                case 2: latexString.addAttribute(TextAttribute.WEIGHT,TextAttribute.WEIGHT_BOLD); break;
                case 3: latexString.addAttribute(TextAttribute.WEIGHT,TextAttribute.WEIGHT_EXTRABOLD); break;
                default: latexString.addAttribute(TextAttribute.WEIGHT,TextAttribute.WEIGHT_LIGHT); break;                
            }
        }
    }
    */
    
    public final void setFontSize(int size){
        /*this.textFontSize = size;
        if(this.latexString.getIterator().getEndIndex()>0){
            latexString.addAttribute(TextAttribute.SIZE, (float) size);
        }*/
    }
    
    public int drawString(String text, Graphics2D  g2d, int x, int y, TextAlign alignX, TextAlign alignY, int type){
        
        g2d.setFont(textFont);
        
        FontMetrics fm = g2d.getFontMetrics(textFont);
        Rectangle2D rect = fm.getStringBounds(text,g2d);
        
        int   ascend  = fm.getAscent();
        int  leading  = fm.getLeading();
        int  descent = fm.getDescent();
        int   height  = fm.getHeight();
        
        //System.out.printf(" ascent = %3d, leading = %3d, descent = %3d, height = %3d (%3d) \n",
        //        ascend,leading,descent,height,(int) rect.getHeight());
        int posX = x;
        int posY = y - descent;
       
        if(alignX==TextAlign.CENTER) posX = (int) (posX-0.5*rect.getWidth());
        if(alignX==TextAlign.RIGHT) posX = (int) (posX-rect.getWidth());
        if(alignY==TextAlign.TOP) posY = (int) (y + ascend);
        if(alignY==TextAlign.CENTER) posY = (int)  (y + ascend - height/2);
        
        g2d.setColor(latexTextColor);
        g2d.setFont(textFont);
        g2d.drawString(text, posX, posY);
        if(type==0) return (int) rect.getHeight();
        return (int) rect.getWidth();
    }
    
    public int drawString(String text, Graphics2D  g2d, int x, int y, int alignX, int alignY, int type){
        
        g2d.setFont(textFont);
        
        FontMetrics fm = g2d.getFontMetrics(textFont);
        Rectangle2D rect = fm.getStringBounds(text,g2d);
        
        int   ascend  = fm.getAscent();
        int  leading  = fm.getLeading();
        int  descent = fm.getDescent();
        int   height  = fm.getHeight();
        
        //System.out.printf(" ascent = %3d, leading = %3d, descent = %3d, height = %3d (%3d) \n",
        //        ascend,leading,descent,height,(int) rect.getHeight());
        int posX = x;
        int posY = y - descent;
       
        if(alignX==1) posX = (int) (posX-0.5*rect.getWidth());
        if(alignX==2) posX = (int) (posX-rect.getWidth());
        if(alignY==LatexText.ALIGN_TOP) posY = (int) (y + ascend);
        if(alignY==LatexText.ALIGN_CENTER) posY = (int)  (y + ascend - height/2);
        
        g2d.setColor(latexTextColor);
        g2d.setFont(textFont);
        g2d.drawString(text, posX, posY);                
        if(type==0) return (int) rect.getHeight();
        return (int) rect.getWidth();
    }
        
    public void drawString(String text, Graphics2D  g2d, int x, int y, TextAlign alignX, TextAlign alignY, TextRotate rotate){
        
        g2d.setFont(textFont);
        
        if(rotate==TextRotate.NONE){
            drawString(text,g2d, x, y, alignX, alignY,0);
            return;
        }
        
        FontMetrics fmg = g2d.getFontMetrics(textFont);
        //Rectangle2D rect = fmg.getStringBounds(this.latexString.getIterator(), 0,
        //        this.latexString.getIterator().getEndIndex(),g2d);
        Rectangle2D rect =  fmg.getStringBounds(asciiString,g2d);
        //fmg.getStringBounds(this.latexString.getIterator(), 0,
        //this.latexString.getIterator().getEndIndex(),g2d);
        int  ascend   = fmg.getAscent();
        int leading   = fmg.getLeading();
        int  descent  = fmg.getDescent();
        int  height   = fmg.getHeight();
        
        int posX = y;
        int posY = x;
        
        if(alignX==TextAlign.CENTER) posX = posX + (int) (0.5*rect.getWidth());
        //if(alignX==LatexText.ALIGN_LEFT)   posX = posX + (int) rect.getWidth();
        if(alignY==TextAlign.TOP)    posY = posY + (int) (rect.getHeight());
        //if(alignY==LatexText.ALIGN_TOP)    posY = posY + (int) (rect.getHeight());
        //if(alignY==LatexText.ALIGN_CENTER)    posY = posY + (int) (0.5*rect.getHeight());
        AffineTransform orig = g2d.getTransform();
        g2d.rotate(-Math.PI/2);
        g2d.setColor(this.latexTextColor);
        g2d.drawString(latexString.getIterator(),-posX ,posY - descent);
        g2d.setTransform(orig);
    }
    
    public void drawString( Graphics2D  g2d, int x, int y, TextAlign alignX, TextAlign alignY, TextRotate rotate){
        
        g2d.setFont(textFont);
        
        if(rotate==TextRotate.NONE){            
            drawString(g2d,x,y,alignX,alignY);
            return;
        }
        
        FontMetrics fmg = g2d.getFontMetrics(textFont);
        Rectangle2D rect = fmg.getStringBounds(this.latexString.getIterator(), 0,
                this.latexString.getIterator().getEndIndex(),g2d);
        //Rectangle2D rect =  fmg.getStringBounds(asciiString,g2d);
        //fmg.getStringBounds(this.latexString.getIterator(), 0,
        //this.latexString.getIterator().getEndIndex(),g2d);
        int  ascend   = fmg.getAscent();
        int leading   = fmg.getLeading();
        int  descent  = fmg.getDescent();
        int  height   = fmg.getHeight();
        
        int posX = y;
        int posY = x;
        
        if(alignX==TextAlign.CENTER) posX = posX + (int) (0.5*rect.getWidth());
        //if(alignX==LatexText.ALIGN_LEFT)   posX = posX + (int) rect.getWidth();
        if(alignY==TextAlign.TOP)    posY = posY + (int) (rect.getHeight());
        //if(alignY==LatexText.ALIGN_TOP)    posY = posY + (int) (rect.getHeight());
        //if(alignY==LatexText.ALIGN_CENTER)    posY = posY + (int) (0.5*rect.getHeight());
        
        double angle = -Math.PI/2.0;
        if(rotate==TextRotate.LEFT) angle = Math.PI/2;
        if(rotate==TextRotate.UPSIDEDOWN) angle = Math.PI;
        AffineTransform orig = g2d.getTransform();
        
        g2d.rotate(angle);
        g2d.setColor(this.latexTextColor);
        g2d.drawString(latexString.getIterator(),-posX ,posY - descent);
        g2d.setTransform(orig);
    }
    
    public void drawString(Graphics2D  g2d, int x, int y, int alignX, int alignY, int rotate){
        
        g2d.setFont(textFont);
        
        if(rotate==LatexText.ROTATE_NONE){
            drawString(g2d, x, y, alignX, alignY);
            return;
        }
        
        FontMetrics fmg = g2d.getFontMetrics(textFont);
        Rectangle2D rect = fmg.getStringBounds(asciiString,g2d);
        //fmg.getStringBounds(this.latexString.getIterator(), 0,
        //this.latexString.getIterator().getEndIndex(),g2d);
        int  ascend   = fmg.getAscent();
        int leading   = fmg.getLeading();
        int  descent  = fmg.getDescent();
        int  height   = fmg.getHeight();
        
        int posX = y;
        int posY = x;
        
        if(alignX==LatexText.ALIGN_CENTER) posX = posX + (int) (0.5*rect.getWidth());
        //if(alignX==LatexText.ALIGN_LEFT)   posX = posX + (int) rect.getWidth();
        if(alignY==LatexText.ALIGN_TOP)    posY = posY + (int) (rect.getHeight());
        //if(alignY==LatexText.ALIGN_TOP)    posY = posY + (int) (rect.getHeight());
        //if(alignY==LatexText.ALIGN_CENTER)    posY = posY + (int) (0.5*rect.getHeight());
        AffineTransform orig = g2d.getTransform();
        g2d.rotate(-Math.PI/2);
        g2d.setColor(this.latexTextColor);
        g2d.drawString(latexString.getIterator(),-posX ,posY - descent);
        g2d.setTransform(orig);
        
    }
    
    public void drawString(Graphics2D  g2d, int x, int y, int alignX, int alignY){  
        g2d.setFont(textFont);
        FontMetrics fmg = g2d.getFontMetrics(textFont);
        Rectangle2D rect = fmg.getStringBounds(this.latexString.getIterator(), 0,
                this.latexString.getIterator().getEndIndex(),g2d);
        
        //Rectangle2D rect = fmg.getStringBounds(asciiString,g2d);
        //Rectangle2D asciiRect = fmg.getStringBounds(asciiString,g2d);
        
        int  ascend   = fmg.getAscent();
        int leading   = fmg.getLeading();
        int  descent  = fmg.getDescent();
        int  height   = fmg.getHeight();
        //int leading   = fmg.getLeading();
        
        //System.out.println("ascend = " + ascend + " leading = " + leading);
        int  xp     = x;
        int  yp     = y - descent;
        if(alignX==1) xp = (int) (xp-0.5*rect.getWidth());
        if(alignX==2) xp = (int) (xp-rect.getWidth());
        if(alignY==LatexText.ALIGN_TOP) yp = (int) (y + ascend);
        if(alignY==LatexText.ALIGN_CENTER) yp = (int)  (y + ascend - height/2);
        //if(alignY==1) yp = (int) (y + 0.5*(height));
        //if(alignY==2) yp = (int)  y;
        g2d.setFont(textFont);
        g2d.setColor(latexTextColor);
        g2d.drawString(latexString.getIterator(), xp, yp);     
        //if(type==0) return rect.getHeight();
        //return rect.getWidth();
    }
    
    public void drawString(Graphics2D  g2d, int x, int y, TextAlign alignX, TextAlign alignY){  
        g2d.setFont(textFont);
        FontMetrics fmg = g2d.getFontMetrics(textFont);
        Rectangle2D rect = fmg.getStringBounds(this.latexString.getIterator(), 0,
                this.latexString.getIterator().getEndIndex(),g2d);
        
        //Rectangle2D rect = fmg.getStringBounds(asciiString,g2d);
        //Rectangle2D asciiRect = fmg.getStringBounds(asciiString,g2d);
        
        int  ascend   = fmg.getAscent();
        int leading   = fmg.getLeading();
        int  descent  = fmg.getDescent();
        int  height   = fmg.getHeight();
        //int leading   = fmg.getLeading();
        
        //System.out.println("ascend = " + ascend + " leading = " + leading);
        int  xp     = x;
        int  yp     = y - descent;
        if(alignX==TextAlign.CENTER) xp = (int) (xp-0.5*rect.getWidth());
        if(alignX==TextAlign.RIGHT) xp = (int) (xp-rect.getWidth());
        if(alignY==TextAlign.TOP) yp = (int) (y + ascend);
        if(alignY==TextAlign.CENTER) yp = (int)  (y + ascend - height/2);
        //if(alignY==1) yp = (int) (y + 0.5*(height));
        //if(alignY==2) yp = (int)  y;
        g2d.setFont(textFont);
        g2d.setColor(latexTextColor);
        g2d.drawString(latexString.getIterator(), xp, yp);     
        //if(type==0) return rect.getHeight();
        //return rect.getWidth();
    }
    
    public int drawString(Graphics2D  g2d, int x, int y, TextAlign alignX, TextAlign alignY, int type){  
        g2d.setFont(textFont);
        FontMetrics fmg = g2d.getFontMetrics(textFont);
        Rectangle2D rect = fmg.getStringBounds(this.latexString.getIterator(), 0,
                this.latexString.getIterator().getEndIndex(),g2d);
        
        //Rectangle2D rect = fmg.getStringBounds(asciiString,g2d);
        //Rectangle2D asciiRect = fmg.getStringBounds(asciiString,g2d);
        
        int  ascend   = fmg.getAscent();
        int leading   = fmg.getLeading();
        int  descent  = fmg.getDescent();
        int  height   = fmg.getHeight();
        //int leading   = fmg.getLeading();
        
        //System.out.println("ascend = " + ascend + " leading = " + leading);
        int  xp     = x;
        int  yp     = y - descent;
        if(alignX==TextAlign.CENTER) xp = (int) (xp-0.5*rect.getWidth());
        if(alignX==TextAlign.RIGHT) xp = (int) (xp-rect.getWidth());
        if(alignY==TextAlign.TOP) yp = (int) (y + ascend);
        if(alignY==TextAlign.CENTER) yp = (int)  (y + ascend - height/2);
        //if(alignY==1) yp = (int) (y + 0.5*(height));
        //if(alignY==2) yp = (int)  y;
        g2d.setFont(textFont);
        g2d.setColor(latexTextColor);
        g2d.drawString(latexString.getIterator(), xp, yp);     
        if(type==0)  return  (int) rect.getHeight();
        return (int) rect.getWidth();
    }
    
    public  Rectangle2D  getBoundsNumber(Graphics2D g2d){
        /*FontMetrics fmg = g2d.getFontMetrics(new Font(this.textFamily,Font.BOLD,this.textFontSize));
        //System.out.println(" ACCEND = " + fmg.getAscent() + " LEAD " + fmg.getLeading()
        //+ "  DESCENT " + fmg.getDescent() + "  HEIGHT = " + fmg.getHeight());
        Rectangle2D rect = fmg.getStringBounds(this.latexString.getIterator(), 0,
                this.latexString.getIterator().getEndIndex(),g2d);
        //fmg.getHeight();
        return new Rectangle2D.Double(0,0,rect.getWidth(),fmg.getAscent());*/
        System.out.println("------------ WARNING ---- DELETED METHOD CALL -----");
        return new Rectangle2D.Double(0,0,0,0);
    }
    
    public  Rectangle2D getBounds( Graphics2D g2d){
        FontMetrics fmg = g2d.getFontMetrics(this.textFont);
        Rectangle2D rect = fmg.getStringBounds(this.latexString.getIterator(), 0,
                this.latexString.getIterator().getEndIndex(),g2d);
        return rect;
    }
    
    public  Rectangle2D getBounds(FontMetrics  fm, Graphics2D g2d){
        FontMetrics fmg = g2d.getFontMetrics(textFont);
        Rectangle2D rect = fmg.getStringBounds(this.latexString.getIterator(), 0,
                this.latexString.getIterator().getEndIndex(),g2d);
        return rect;
    }
    
    public static LatexText createFromDouble(double number, int order){
        //System.out.println("LatexUtils: number = " + number + "  order = " + order);
        String format = "%." + String.format("%df", order);
        String numString = String.format(format, number);
        //System.out.println("[opt] --> released string : " + numString);
        return new LatexText(numString);
    }
    
    public static void main(String[] args){
        LatexText lt = LatexText.createFromDouble(0.56789, 2);        
    }
}
