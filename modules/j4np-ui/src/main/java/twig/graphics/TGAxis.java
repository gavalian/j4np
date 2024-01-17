/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import j4np.graphics.Translation2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import twig.config.TAxisAttributes;
import twig.config.TAxisAttributes.AxisType;
import twig.config.TStyle;
import twig.data.Range;
import twig.widgets.LatexText;
import twig.widgets.LatexText.TextAlign;
import twig.widgets.LatexText.TextRotate;
import twig.widgets.StyleNode;

/**
 * 
 * @author gavalian
 */
public class TGAxis implements StyleNode {
    
    private TAxisAttributes attributes = new TAxisAttributes();
    private LatexText       textWidget = new LatexText("1",0,0);
    private TStyle              tStyle = null;
    
    private List<Double>  axisTicksBuffer = new ArrayList<>();
    private List<Double>  axisMinorTicksBuffer = new ArrayList<>();
    private List<String>  axisTextsBuffer = new ArrayList<>();    
    private Range               axisRange = new Range();
    private Range          fixedAxisRange = null;
    private TGNiceScale     axisScaleTool = new TGNiceScale(0.0,1.0);
    
    public boolean isLogarithmic = false;
    
    public TGAxis(){}
    
    public TGAxis(AxisType type){
        if(type == AxisType.AXIS_X){
            try {
                attributes = TStyle.getInstance().getAxisAttrX().clone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(TGAxis.class.getName()).log(Level.SEVERE, null, ex);
            }
            attributes.setAxisType(type);
        }
        
        if(type == AxisType.AXIS_Y){
            try {
                attributes = TStyle.getInstance().getAxisAttrY().clone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(TGAxis.class.getName()).log(Level.SEVERE, null, ex);
            }
            attributes.setAxisType(type);
        }
        if(type == AxisType.AXIS_Z){
             try {
                attributes = TStyle.getInstance().getAxisAttrZ().clone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(TGAxis.class.getName()).log(Level.SEVERE, null, ex);
            }
            attributes.setAxisType(type);
        }
        
    }
    
    public TAxisAttributes getAttributes(){ return this.attributes;}
    
    public void setLimits(double min, double max){
        if(this.fixedAxisRange==null)
            this.axisRange.set(min,max);
    }
    
    public void drawAxisGridX(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        TStyle style = getStyle();
        int x1 = ( int ) r.getX();
        int x2 = ( int ) (r.getWidth()+r.getX());
        int y1 = ( int ) (r.getY()+r.getHeight());
        int y2 = ( int ) (r.getY());
        Color lineColor = style.getPalette().getColor(attributes.getAxisGridLineColor());

        int lstyle = this.attributes.getAxisGridLineStyle();
        int lwidth = this.attributes.getAxisGridLineWidth();

        BasicStroke stroke = style.getLineStroke(lstyle, lwidth);

        g2d.setColor(lineColor);
        g2d.setStroke(stroke);

        this.axisScaleTool.setMinMaxPoints(axisRange.min(), axisRange.max());
        this.axisScaleTool.setMaxTicks(attributes.getAxisTickMarkCount());
        this.axisScaleTool.getTicks(axisTicksBuffer, axisTextsBuffer);
        
        for(int i = 0; i < this.axisTicksBuffer.size(); i++){
            int xpos = (int) tr.getX(axisTicksBuffer.get(i),r);
            g2d.drawLine(xpos, y1, xpos, y2);
        }
    }
    
    public void drawAxisGridY(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        
        TStyle style = getStyle();
        int x1 = ( int ) r.getX();
        int x2 = ( int ) (r.getWidth()+r.getX());
        int y1 = ( int ) (r.getY()+r.getHeight());
        int y2 = ( int ) (r.getY());
        Color lineColor = style.getPalette().getColor(attributes.getAxisGridLineColor());

        int lstyle = this.attributes.getAxisGridLineStyle();
        int lwidth = this.attributes.getAxisGridLineWidth();

        BasicStroke stroke = style.getLineStroke(lstyle, lwidth);

        g2d.setColor(lineColor);
        g2d.setStroke(stroke);

        this.axisScaleTool.setMinMaxPoints(axisRange.min(), axisRange.max());
        this.axisScaleTool.setMaxTicks(attributes.getAxisTickMarkCount());
        this.axisScaleTool.getTicks(axisTicksBuffer, axisTextsBuffer);
        
        for(int i = 0; i < this.axisTicksBuffer.size(); i++){
            int ypos = (int) ( y1 + r.getY() - tr.getY(axisTicksBuffer.get(i),r) );
            g2d.drawLine(x1, ypos, x2, ypos);
        }
    }
    
    public void drawAxisZ(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        
        TStyle style = getStyle();
        int x1 = ( int ) r.getX();
        int x2 = ( int ) (r.getWidth()+r.getX()) + 4;
        int y1 = ( int ) (r.getY()+r.getHeight());
        int y2 = ( int ) (r.getY());
        int ncolors = style.getPalette().palette2d().getPaletteSize();
                

        g2d.setColor(Color.yellow);
        g2d.fillRect(x2, y2, 20, 20);
        
        int     step = (int) (Math.abs(y2-y1)/ncolors);
        
        int leftover = Math.abs(y2-y1) - step*ncolors;
        //System.out.println("y2 = " + y2 + " y1 = " + y1 + " step = " 
        //        + step + " , leftover = " + leftover + " ncolors = " + ncolors);
        int       ypos = (int) (y2);
        //System.out.println(" RECT = " + r);
        
        
        for(int i = 0; i < ncolors; i++){
            Color c = style.getPalette().palette2d().getColor(ncolors-1-i);
            g2d.setColor(c);
            if(i>leftover){
                g2d.fillRect(x2, ypos, 20, (int) (step));
                ypos = ypos + step;
            } else {
                g2d.fillRect(x2, ypos, 20, (int) (step+1));
                ypos = ypos + step+1;
            }
            
            //System.out.printf(" n = %4d, ypos = %5d\n",i,ypos);
        }
        
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x2, y2, 20, Math.abs(y2-y1));
        
        //g2d.drawLine(x1,y1,x2,y2);
        //g2d.drawRect(x1,y2,Math.abs(x2-x1-10),Math.abs(y2-y1));
    }
    
    
    public void drawAxisX(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        
        TStyle style = getStyle();
        int x1 = ( int ) r.getX();
        int x2 = ( int ) (r.getWidth()+r.getX());
        int y1 = ( int ) (r.getY()+r.getHeight());
        int y2 = ( int ) (r.getY());
        
        int yend = y1 - attributes.getAxisTickMarkSize();
        
        int ytoplabel = y1 + attributes.getAxisLabelOffset();
        
        if(attributes.getAxisTickMarkSize()<0) 
            ytoplabel -= attributes.getAxisTickMarkSize();
        
        Color lineColor = style.getPalette().getColor(attributes.getAxisLineColor());
        Color textColor = style.getPalette().getColor(attributes.getAxisLabelColor());
        Color titleColor = style.getPalette().getColor(attributes.getAxisTitleColor());
        
        textWidget.setColor(textColor);
        g2d.setColor(lineColor);
        /*
        * Draw Axis line if the axis the draw box is set, then 
        * the oposite side will be drawn as well. 
        */
        if(this.attributes.getAxisLineDraw()==true){
            g2d.setStroke(new BasicStroke(attributes.getAxisLineWidth()));            
            g2d.drawLine(x1,y1,x2,y1);
        }
        
        if(this.getAttributes().getAxisBoxDraw()==true)
            g2d.drawLine(x1,y2,x2,y2);
        
        textWidget.setFont(attributes.getAxisLabelFont());
        int labelHeight = 0;
        
        if(attributes.getAxisTicksPosition().size()>0){            
            
            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(attributes.getAxisTicksLineWidth()));
            
            for(int i = 0; i < attributes.getAxisTicksPosition().size(); i++){
                int xpos = (int) tr.getX(attributes.getAxisTicksPosition().get(i),r);
                String xlabel = attributes.getAxisTicksString().get(i);                    
                if(this.attributes.getAxisTicksDraw()==true) 
                    g2d.drawLine( xpos, y1, xpos, yend);
                if(this.attributes.getAxisLabelsDraw()==true){
                    textWidget.setText(xlabel);
                    //labelHeight = textWidget.drawString(xlabel, g2d, xpos, ytoplabel, 1, 3, 0);
                    labelHeight = 
                            textWidget.drawString(g2d, xpos, ytoplabel,TextAlign.CENTER,TextAlign.TOP,0);
                }
            }
        }  else {
            
            g2d.setColor(lineColor);

            
            this.axisScaleTool.setMinMaxPoints(axisRange.min(), axisRange.max());
            this.axisScaleTool.setMaxTicks(attributes.getAxisTickMarkCount());
            this.axisScaleTool.getTicks(axisTicksBuffer, axisTextsBuffer);

            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(attributes.getAxisTicksLineWidth()));            
            for(int i = 0; i < axisTicksBuffer.size(); i++){
                int xpos = (int) tr.getX(axisTicksBuffer.get(i),r);
                if(this.attributes.getAxisTicksDraw()==true) g2d.drawLine( xpos, y1, xpos, yend);
            }
            
            int yendminor = y1 - attributes.getAxisTickMarkSize()/2;
            this.axisScaleTool.getMinorTicks(axisMinorTicksBuffer);
            for(int i = 0; i < axisMinorTicksBuffer.size(); i++){
                int xpos = (int) tr.getX(axisMinorTicksBuffer.get(i),r);
                if(this.attributes.getAxisTicksDraw()==true) g2d.drawLine( xpos, y1, xpos, yendminor);
            }
            
            for(int i = 0; i < axisTicksBuffer.size(); i++){
                if(this.attributes.getAxisLabelsDraw()==true){
                    int xpos = (int) tr.getX(axisTicksBuffer.get(i),r);
                    String xlabel = axisTextsBuffer.get(i);                    
                    textWidget.setText(xlabel);
                    //textWidget.setFont(this.attributes.getAxisLabelFont());
                    if(this.attributes.getAxisEndPointSupress()==true){
                        if(Math.abs(xpos-x1)>4&&Math.abs(xpos-x2)>4){                                                    
                            labelHeight = 
                                    textWidget.drawString(g2d, xpos, ytoplabel,TextAlign.CENTER,TextAlign.TOP,0);
                        }
                    } else {
                        labelHeight = 
                                textWidget.drawString(g2d, xpos, ytoplabel,TextAlign.CENTER,TextAlign.TOP,0);
                    }
                }
            }            
        }
        //double x = ( int ) r.getX();
        int titlePositionX = (int) (x1+(x2-x1)*0.5);
        int titlePositionY = (int) (ytoplabel-labelHeight);
        
        textWidget.setFont(attributes.getAxisTitleFont());
        titlePositionY = (int) (
                r.getY() + r.getHeight()                
                );
        
        
        if(attributes.getAxisTickMarkSize()<0) 
            titlePositionY += Math.abs(attributes.getAxisTickMarkSize());
        
        titlePositionY += attributes.getAxisLabelOffset() + 
                attributes.getAxisTitleOffset() + labelHeight;
        /*System.out.println(" Title offset = " + attributes.getAxisTitleOffset());
        System.out.println("Rect = " + r);
        System.out.printf("string @ : %8d %8d\n",titlePositionX,titlePositionY);
        */
        if(attributes.getAxisTitle().length()>0&&attributes.getAxisTitlesDraw()==true){
            textWidget.setColor(titleColor);
            textWidget.setText(attributes.getAxisTitle());
            textWidget.drawString(g2d,
                    titlePositionX ,  titlePositionY, LatexText.ALIGN_CENTER,LatexText.ALIGN_TOP);
        }
        /*
        textWidget.drawString(attributes.getAxisTitle(), g2d,
                titlePositionX , titlePositionY, TextAlign.CENTER, TextAlign.TOP, 0);*/
        //System.out.println(" printing : " + attributes.getAxisTitle());
    }
    
    public void drawAxisY(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        
        TStyle style = getStyle();
        
        int x1 = ( int ) r.getX();
        int x2 = ( int ) (r.getWidth()+r.getX());
        int y1 = ( int ) (r.getY()+r.getHeight());
        int y2 = ( int ) (r.getY());
        int xend = x1 + attributes.getAxisTickMarkSize();            
        
        Color lineColor = style.getPalette().getColor(attributes.getAxisLineColor());
        Color textColor = style.getPalette().getColor(attributes.getAxisLabelColor());
        
        textWidget.setColor(textColor);        
        g2d.setColor(lineColor);
        
        if(this.attributes.getAxisLineDraw()==true){
            g2d.setStroke(new BasicStroke(attributes.getAxisLineWidth()));            
            g2d.drawLine(x1,y1,x1,y2);
        }
        
        if(this.getAttributes().getAxisBoxDraw()==true)
            g2d.drawLine(x2,y1,x2,y2);
        
        
        int xrightlevel = x1 - attributes.getAxisLabelOffset();
        
        if(attributes.getAxisTickMarkSize()<0) 
            xrightlevel += attributes.getAxisTickMarkSize();
        
        
        textWidget.setFont(attributes.getAxisLabelFont());
        
        int maximumLabelSize = 0;
        
        if(attributes.getAxisTicksPosition().size()>0){
            //g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(attributes.getAxisTicksLineWidth()));
            for(int i = 0; i < attributes.getAxisTicksPosition().size(); i++){
                int ypos = (int)(y1 + r.getY() - tr.getY(attributes.getAxisTicksPosition().get(i),r));
                
                //System.out.println("y1 = " + y1  + " point = " 
                //        + attributes.getAxisTicksPosition().get(i) 
                //        + " pos = " + tr.getY(attributes.getAxisTicksPosition().get(i),r));
                //tr.show();
                //System.out.println(" R = " + r);

                String ylabel = attributes.getAxisTicksString().get(i);
                if(this.attributes.getAxisTicksDraw()==true) g2d.drawLine( x1, ypos, xend, ypos);
                if(this.attributes.getAxisLabelsDraw()==true){
                    textWidget.setText(ylabel);
                    int textWidth = textWidget.drawString( g2d, xrightlevel, ypos, TextAlign.RIGHT, TextAlign.CENTER ,1);
                    if(textWidth>maximumLabelSize) maximumLabelSize = textWidth;
                }
            } 
            //double x = ( int ) r.getX();
        } else {
            //g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(attributes.getAxisTicksLineWidth()));
            textWidget.setFont(this.attributes.getAxisLabelFont());
            this.axisScaleTool.setMinMaxPoints(axisRange.min(), axisRange.max());
            this.axisScaleTool.setMaxTicks(attributes.getAxisTickMarkCount());
            
            //System.out.println("----> getting axis ticks");
            if(this.isLogarithmic==true){
                   this.axisScaleTool.getTicksLog(axisTicksBuffer, axisTextsBuffer);
            } else {
                this.axisScaleTool.getTicks(axisTicksBuffer, axisTextsBuffer);
            }
            
            //System.out.println(axisTicksBuffer);
            
            for(int i = 0; i < axisTicksBuffer.size(); i++){
                int ypos = (int)(y1 + r.getY() - tr.getY(axisTicksBuffer.get(i),r));
                //System.out.println(" translating -> " + axisTicksBuffer.get(i) + "  " + tr.isLogY());
                //int ypos = (int)( tr.getY(axisTicksBuffer.get(i),r));
                String ylabel = axisTextsBuffer.get(i);
                if(this.attributes.getAxisTicksDraw()==true) g2d.drawLine( x1, ypos, xend, ypos);
                textWidget.setText(ylabel);
                if(this.attributes.getAxisLabelsDraw()==true){
                    if(this.attributes.getAxisEndPointSupress()==true){
                        if(Math.abs(ypos-y1)>4&&Math.abs(ypos-y2)>3){
                            int textWidth = textWidget.drawString( g2d, xrightlevel, ypos, TextAlign.RIGHT, TextAlign.CENTER ,1);                            
                            if(textWidth>maximumLabelSize) maximumLabelSize = textWidth;
                        }
                    } else {
                        int textWidth = textWidget.drawString( g2d, xrightlevel, ypos, TextAlign.RIGHT, TextAlign.CENTER ,1);                            
                        if(textWidth>maximumLabelSize) maximumLabelSize = textWidth;
                    }
                }
            } 
            int xendminor = x1 + attributes.getAxisTickMarkSize()/2;
            this.axisScaleTool.getMinorTicks(axisMinorTicksBuffer);
            for(int i = 0; i < axisMinorTicksBuffer.size(); i++){
                int ypos = (int)(y1 + r.getY() - tr.getY(axisMinorTicksBuffer.get(i),r));
                if(this.attributes.getAxisTicksDraw()==true) g2d.drawLine( x1, ypos, xendminor, ypos);
                
            }
        }
        
        
        if(attributes.getAxisTitle().length()>0&&attributes.getAxisTitlesDraw()==true){
            
            textWidget.setFont(attributes.getAxisTitleFont());
            
            int titlePositionX = (int) (r.getX());
            int titlePositionY = (int) (r.getY()+r.getHeight()*0.5);
            
            if(this.attributes.getAxisTickMarkSize()<0){
                titlePositionX -= Math.abs(attributes.getAxisTickMarkSize());
            }
            
            titlePositionX -= attributes.getAxisLabelOffset();
            titlePositionX -= attributes.getAxisTitleOffset();
            titlePositionX -= maximumLabelSize;
            
            if(attributes.getAxisTitle().length()>0){
                textWidget.setText(attributes.getAxisTitle());                
                //textWidget.drawString(attributes.getAxisTitle(), g2d,
                //        titlePositionX , titlePositionY, TextAlign.CENTER, TextAlign.BOTTOM, TextRotate.LEFT);
                
                //System.out.println(" DRAWING THE AXIS TITLE " + attributes.getAxisTitle());
                textWidget.drawString( g2d,
                        titlePositionX , titlePositionY, TextAlign.CENTER, TextAlign.BOTTOM, TextRotate.RIGHT);
                
                
            }
            /*textWidget.drawString("abra-catabra", g2d,
            titlePositionX , titlePositionY, TextAlign.CENTER, TextAlign.BOTTOM, TextRotate.LEFT);*/
            //double x = ( int ) r.getX();
        }
    }
    
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        
        TStyle style = getStyle();
        if(attributes.getAxisType()==AxisType.AXIS_X){
            if(this.attributes.getAxisGridDraw()==true)drawAxisGridX(g2d, r, tr);
            drawAxisX(g2d, r, tr);
        }
        
        if(attributes.getAxisType()==AxisType.AXIS_Y){
            if(this.attributes.getAxisGridDraw()==true) drawAxisGridY(g2d, r, tr);
            drawAxisY(g2d,r,tr);
        }
        
        if(attributes.getAxisType()==AxisType.AXIS_Z){
            //if(this.attributes.getAxisGridDraw()==true) drawAxisGridY(g2d, r, tr);
            if(this.attributes.getAxisBoxDraw()==true){
                //System.out.println(" >>>> drawing axis Z");
                drawAxisZ(g2d,r,tr);
            }
        }
    }

    public void setFixedLimits(double min, double max){
        fixedAxisRange = new Range(min, max);
        axisRange.set(min, max);
    }
    
    public void unsetFixedLimits(){
        fixedAxisRange = null;        
    }
    
    public Range getRange(){ return axisRange;}
    
    @Override
    public void setStyle(TStyle style) {
        tStyle = style;
    }

    @Override
    public TStyle getStyle() {
        if(tStyle==null){ return TStyle.getInstance();}
        return this.tStyle;
    }
    
}
