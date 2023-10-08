/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import j4np.graphics.Node2D;
import j4np.graphics.Translation2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import twig.config.TAxisAttributes.AxisType;
import twig.config.TStyle;
import twig.data.DataRange;
import twig.data.H2F;
import twig.widgets.StyleNode;
import twig.widgets.Widget;

/**
 *
 * @author gavalian
 */
public class TGAxisFrame extends Node2D implements StyleNode {
    
    private TGAxis axisX = new TGAxis(AxisType.AXIS_X);
    private TGAxis axisY = new TGAxis(AxisType.AXIS_Y);
    private TGAxis axisZ = new TGAxis(AxisType.AXIS_Z);

    private TStyle tStyle = null;

    private final Translation2D axisFrameRange = new Translation2D(0.0,1.0,0.0,1.0);
    private final Translation2D  ndfFrameRange = new Translation2D(0.0,1.0,0.0,1.0);
    
    protected   List<TDataNode2D>       dataNodes = new ArrayList<>();
    protected   List<Widget>          widgetNodes = new ArrayList<>();
    
    private DataRange             axisDataRange = new DataRange();
    private DataRange             tempDataRange = new DataRange();
    
    private DataRange            fixedDataRange = null;
    private Color                axisBackgroundColor = new Color(240,240,240);
    
    
    public TGAxisFrame(){
        
        super(0,0,500,500);
        TStyle style = TStyle.getInstance();
        if(style!=null) {
            this.axisBackgroundColor = style.getDefaultAxisBackgroundColor();
            this.nodeBackground      = style.getDefaultAxisBackgroundColor();
        }
        /*
        axisX.getAttributes().getAxisTicksPosition().add(0.2);
        axisX.getAttributes().getAxisTicksPosition().add(0.45);
        axisX.getAttributes().getAxisTicksPosition().add(0.90);
     
        axisX.getAttributes().getAxisTicksString().add("5 SL");
        axisX.getAttributes().getAxisTicksString().add("6 SL");
        axisX.getAttributes().getAxisTicksString().add("ALL");
        
        axisY.getAttributes().getAxisTicksPosition().add(0.1);
        axisY.getAttributes().getAxisTicksPosition().add(0.35);
        axisY.getAttributes().getAxisTicksPosition().add(0.75);
     
        axisY.getAttributes().getAxisTicksString().add("0.1");
        axisY.getAttributes().getAxisTicksString().add("0.35");
        axisY.getAttributes().getAxisTicksString().add("0.75");
        */
        //this.initDefaultFonts();
    }
    
    public TGAxisFrame setLogY(boolean flag){ 
        this.axisFrameRange.setLogY(flag);
        this.axisY.isLogarithmic = flag;
        return this;
    }
    
    private void initDefaultFonts(){
        try {
           Font lf = TStyle.getInstance().getDefaultAxisLabelFont();
           Font tf = TStyle.getInstance().getDefaultAxisTitleFont();
           this.axisX.getAttributes().setAxisLabelFont(lf);
           this.axisY.getAttributes().setAxisLabelFont(lf);
           this.axisZ.getAttributes().setAxisLabelFont(lf);
           
           this.axisX.getAttributes().setAxisTitleFont(tf);
           this.axisY.getAttributes().setAxisTitleFont(tf);
           this.axisZ.getAttributes().setAxisTitleFont(tf);
        } catch (Exception e){
            System.err.println(":: error. can not find default TStyle to load fonts");            
        }
    }
    @Override
    public void drawLayer(Graphics2D g2d, int layer){
        
        Node2D parent = this.getParent();
        Rectangle2D r = parent.getBounds().getBounds();
        
        //System.out.println("axis frame bounds = " + r);
        if(this.nodeBackground!=null){
            g2d.setColor(this.nodeBackground);
            g2d.fill(r);
        }
        
        
        if(dataNodes.size()>0){
            
            axisX.getAttributes().setAxisTitle(dataNodes.get(0).dataSet.attr().getTitleX());
            axisY.getAttributes().setAxisTitle(dataNodes.get(0).dataSet.attr().getTitleY());
            //System.out.println("titles are X " + dataNodes.get(0).dataSet.attr().getTitleX());
            //System.out.println("titles are Y " + dataNodes.get(0).dataSet.attr().getTitleY());
            dataNodes.get(0).getDataBounds(axisDataRange);
            for(int d = 1; d < dataNodes.size(); d++){
                dataNodes.get(d).getDataBounds(tempDataRange);
                axisDataRange.grow(tempDataRange.getRange());
            }
            
            
            if(this.axisFrameRange.isLogY()==true){
                //System.out.println(axisDataRange.getRange());
                axisDataRange.growY(10.);
                //System.out.println(axisDataRange.getRange());
            }
            
            this.setLimits(
                    axisDataRange.getRange().getX(), 
                    axisDataRange.getRange().getX() + axisDataRange.getRange().getWidth(),
                    axisDataRange.getRange().getY(), 
                    axisDataRange.getRange().getY() + axisDataRange.getRange().getHeight()
            );
            
            
            updateLimits();
            //axisX.draw(g2d, r, axisFrameRange);
            //axisY.draw(g2d, r, axisFrameRange);
            //axisZ.draw(g2d, r, axisFrameRange);
            
            g2d.setClip(r.getBounds2D());
            for(int d = 0; d < dataNodes.size(); d++){
                dataNodes.get(d).draw(g2d, r, axisFrameRange);
            } 
            
            
            g2d.setClip(null);
            axisX.draw(g2d, r, axisFrameRange);
            axisY.draw(g2d, r, axisFrameRange);
            
            if(this.dataNodes.get(0).getDataSet() instanceof H2F){
                //System.out.println(" drawing Z axis with type = " + axisZ.getStyle());
                H2F h = (H2F) this.dataNodes.get(0).getDataSet();
                axisZ.setLimits(0, h.getMaximum());
                axisZ.draw(g2d, r, axisFrameRange);
                
            }
            
        }
        
        //this.setLimits(axisFrameRange., layer, layer, layer)
        //System.out.print(" AXIS range = " );axisFrameRange.show();
        
        //System.out.println(parent.getTranslation());
        //System.out.println("border = " + parent.getBounds().getBounds());
        
        /*
        if(axisX.getAttributes().getAxisBoxDraw()==true||
                axisY.getAttributes().getAxisBoxDraw()==true
                ){
            g2d.drawRect((int) r.getX(), (int) r.getY(), 
                    (int) r.getWidth(), (int) r.getHeight());
        }*/
        
        //axisX.draw(g2d, r, axisFrameRange);
        //axisY.draw(g2d, r, axisFrameRange);
        //axisZ.draw(g2d, r, axisFrameRange);
        
        for(int w = 0; w < widgetNodes.size(); w++){
            if(widgetNodes.get(w).isNDF()==true){
                widgetNodes.get(w).draw(g2d, r, ndfFrameRange);
            } else {
                widgetNodes.get(w).draw(g2d, r, axisFrameRange);
            }
        }
        
        
        
    }
    
    public  List<Widget>       getWidgets(){ return this.widgetNodes;}
    public  List<TDataNode2D>  getDataNodes(){return this.dataNodes;}
    
    private void updateLimits(){
        //System.out.println(" updating limits");
        this.axisFrameRange.set(
                axisX.getRange().min(),
                axisX.getRange().max(),
                axisY.getRange().min(),
                axisY.getRange().max()
                );
    }        
    
    public void addWidget(Widget wi){
        widgetNodes.add(wi);
    }
    
    public void addDataNode(TDataNode2D dn){
        dataNodes.add(dn);
    }
    
    /*public static void main(String[] args){
        JFrame frame = new JFrame();
        Canvas2D canvas = Canvas2D.createFrame(frame, 600, 500);
        TGAxisFrame axisFrame = new TGAxisFrame();
        TGRegion  region = new TGRegion(0,0,500,500);
        region.getInsets().left(120);
        region.addNode(axisFrame);
        canvas.addNode(region);
        canvas.repaint();
    }*/
    public TGAxisFrame setLimits(double xmin, double xmax, double ymin, double ymax){
        this.axisFrameRange.set(xmin, xmax, ymin, ymax);
        this.axisX.setLimits(xmin, xmax);
        this.axisY.setLimits(ymin, ymax);
        return this;
    }
    
    public TGAxisFrame setFixedLimits(double xmin, double xmax, double ymin, double ymax){
        fixedDataRange = new DataRange(xmin,xmax,ymin,ymax);
        return this;
    }
    
    public TGAxisFrame unsetFixedLimits(){
        fixedDataRange = null; return this;
    } 
    
    public TGAxis getAxisX(){ return this.axisX;}
    public TGAxis getAxisY(){ return this.axisY;}
    public TGAxis getAxisZ(){ return this.axisZ;}

    protected void clear(){
        this.getAxisX().getAttributes().setAxisTitle("");
        this.getAxisX().getAttributes().getAxisTicksPosition().clear();
        this.getAxisX().getAttributes().getAxisTicksString().clear();
        this.getAxisX().unsetFixedLimits();
        
        this.getAxisY().getAttributes().setAxisTitle("");        
        this.getAxisY().getAttributes().getAxisTicksPosition().clear();
        this.getAxisY().getAttributes().getAxisTicksString().clear();        
        this.getAxisY().unsetFixedLimits();
        
        this.dataNodes.clear();
        this.widgetNodes.clear();
        this.axisDataRange.set(0, 1, 0, 1);
        this.axisFrameRange.set(0, 1, 0, 1);
    }
    
    @Override
    public void setStyle(TStyle style) {
        this.tStyle = style;
        this.axisX.setStyle(style);
        this.axisY.setStyle(style);
        this.axisZ.setStyle(style);
    }

    @Override
    public TStyle getStyle() {
        return this.tStyle;
    }
   
}
