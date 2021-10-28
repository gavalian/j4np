/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import j4np.graphics.Background2D;
import j4np.graphics.Canvas2D;
import j4np.graphics.Node2D;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;
import javax.swing.JFrame;
import twig.config.TStyle;
import twig.data.DataSet;
import twig.data.GraphErrors;
import twig.data.H1F;
import twig.math.Func1D;
import twig.widgets.StyleNode;
import twig.widgets.Widget;

/**
 *
 * @author gavalian
 */
public class TGRegion extends Node2D implements StyleNode {
    
    protected TGAxisFrame  axisFrame = new TGAxisFrame();
    protected TStyle          tStyle = null;
    
    public TGRegion(int x, int y, int w, int h){
        super(x,y,w,h);
        this.nodeBackground = null;
        axisFrame.setParent(this);
        getInsets().left(60).bottom(60).top(40).right(40);
    }
 
    public TGRegion(){
        super(0,0,50,50);
        this.nodeBackground = null;
        axisFrame.setParent(this);
        getInsets().left(60).bottom(60).top(40).right(40);
    }
    
    @Override
    public void drawLayer(Graphics2D g2d, int layer){ 
        axisFrame.drawLayer(g2d, layer);        
    }
    
        
    @Override
    public void setStyle(TStyle style) {
        this.tStyle = style;
        this.axisFrame.setStyle(style);
    }

    @Override
    public TStyle getStyle() {
        return tStyle;
    }
    
    public TGAxisFrame getAxisFrame(){return this.axisFrame;}
    
    
    public TGRegion setAxisLabelSize(int size){
        Font fx = getAxisFrame().getAxisX().getAttributes().getAxisLabelFont();
        getAxisFrame().getAxisX().getAttributes().setAxisLabelFont(fx.deriveFont(size));
        
        Font fy = getAxisFrame().getAxisY().getAttributes().getAxisLabelFont();
        getAxisFrame().getAxisY().getAttributes().setAxisLabelFont(fy.deriveFont(size));
        /*
        this.getAxisFrame().getAxisX().getAttributes().setAxisLabelFont(font);
        this.getAxisFrame().getAxisY().getAttributes().setAxisLabelFont(font);
        */
        return this;
    }
    
    public TGRegion setAxisTitleSize(int size){
        Font fx = getAxisFrame().getAxisX().getAttributes().getAxisTitleFont();
        getAxisFrame().getAxisX().getAttributes().setAxisTitleFont(fx.deriveFont(size));
        Font fy = getAxisFrame().getAxisY().getAttributes().getAxisTitleFont();
        getAxisFrame().getAxisY().getAttributes().setAxisTitleFont(fy.deriveFont(size));
        return this;
    }
    
    public TGRegion setAxisLabelFont(Font font){
        this.getAxisFrame().getAxisX().getAttributes().setAxisLabelFont(font);
        this.getAxisFrame().getAxisY().getAttributes().setAxisLabelFont(font);
        return this;
    }
    
    public TGRegion setAxisTitleFont(Font font){
        this.getAxisFrame().getAxisX().getAttributes().setAxisTitleFont(font);
        this.getAxisFrame().getAxisY().getAttributes().setAxisTitleFont(font);
        return this;
    }
    
    public void clear(){ this.axisFrame.clear();}
    
    
    public TGRegion setAxisTicksX(double[] values){
        /*
        getAxisFrame().getAxisX().getAttributes().getAxisTicksPosition().clear();
        getAxisFrame().getAxisX().getAttributes().getAxisTicksString().clear();
        for(int i = 0; i < values.length; i++){
            getAxisFrame().getAxisX().getAttributes().getAxisTicksPosition().add(values[i]);
        }*/
        if(values==null){
            getAxisFrame().getAxisX().getAttributes().getAxisTicksPosition().clear();
            getAxisFrame().getAxisX().getAttributes().getAxisTicksString().clear();
        }
        return this;
    }
    
    public TGRegion setAxisTicksY(double[] values){
        /*
        getAxisFrame().getAxisX().getAttributes().getAxisTicksPosition().clear();
        getAxisFrame().getAxisX().getAttributes().getAxisTicksString().clear();
        for(int i = 0; i < values.length; i++){
            getAxisFrame().getAxisX().getAttributes().getAxisTicksPosition().add(values[i]);
        }*/
        if(values==null){
            getAxisFrame().getAxisY().getAttributes().getAxisTicksPosition().clear();
            getAxisFrame().getAxisY().getAttributes().getAxisTicksString().clear();
        }
        return this;
    }
    
    public TGRegion setAxisTicksX(double[] values, String[] labels){
        getAxisFrame().getAxisX().getAttributes().getAxisTicksPosition().clear();
        getAxisFrame().getAxisX().getAttributes().getAxisTicksString().clear();
        
        if(labels.length!=values.length){
            System.out.println("region:setaxisticks: error, inconsistent size of lables and values");
        } else {
            for(int i = 0; i < values.length; i++){
                getAxisFrame().getAxisX().getAttributes().getAxisTicksPosition().add(values[i]);
                getAxisFrame().getAxisX().getAttributes().getAxisTicksString().add(labels[i]);
            }
        }
        return this;
    }
    
    public TGRegion setAxisTicksY(double[] values, String[] labels){
        getAxisFrame().getAxisY().getAttributes().getAxisTicksPosition().clear();
        getAxisFrame().getAxisY().getAttributes().getAxisTicksString().clear();
        
        if(labels.length!=values.length){
            System.out.println("region:setaxisticks: error, inconsistent size of lables and values");
        } else {
            for(int i = 0; i < values.length; i++){
                getAxisFrame().getAxisY().getAttributes().getAxisTicksPosition().add(values[i]);
                getAxisFrame().getAxisY().getAttributes().getAxisTicksString().add(labels[i]);
            }
        }
        return this;
    }
    
    public TGRegion drawFrame(boolean flag){
        
        this.axisFrame.getAxisX().getAttributes().setAxisLineDraw(flag);
        this.axisFrame.getAxisX().getAttributes().setAxisTicksDraw(flag);
        this.axisFrame.getAxisX().getAttributes().setAxisLabelsDraw(flag);
        this.axisFrame.getAxisX().getAttributes().setAxisTitlesDraw(flag);
        this.axisFrame.getAxisX().getAttributes().setAxisBoxDraw(flag);
        
        this.axisFrame.getAxisY().getAttributes().setAxisLineDraw(flag);
        this.axisFrame.getAxisY().getAttributes().setAxisTicksDraw(flag);
        this.axisFrame.getAxisY().getAttributes().setAxisLabelsDraw(flag);
        this.axisFrame.getAxisY().getAttributes().setAxisTitlesDraw(flag);
        this.axisFrame.getAxisY().getAttributes().setAxisBoxDraw(flag);
        
        return this;
    }
    
    public TGRegion draw(DataSet ds){
        draw(ds,"*"); return this;
    }
    
    public TGRegion draw(List<? extends DataSet> list, String options){
        if(options.contains("same")==false){
            this.axisFrame.clear();
        }
        String localOpts = options;
        if(localOpts.contains("same")==false){
            localOpts = options + "same";
        }
        
        for(DataSet ds : list){
            draw(ds,localOpts);
        }
        return this;
    }
    
    public TGRegion draw(Widget w){
        this.axisFrame.addWidget(w);
        return this;
    }
    
    public TGRegion draw(DataSet ds, String options){
        if(options.contains("same")==false){
            this.axisFrame.clear();
        }
        if(ds instanceof H1F){
            axisFrame.addDataNode(new TGH1F((H1F) ds,options));
        }
        
        if(ds instanceof GraphErrors){
            axisFrame.addDataNode(new TGENode2D((GraphErrors) ds,options));
        }
        
        if(ds instanceof Func1D){
            axisFrame.addDataNode(new TGF1D((Func1D) ds,options));
        }
        return this;
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        Canvas2D canvas = Canvas2D.createFrame(frame, 600, 500);
        canvas.setBackground( Background2D.createBackground(255, 255, 255));
        //TGAxisFrame axisFrame = new TGAxisFrame();
        TGRegion  region = new TGRegion(0,0,500,500);
        region.getInsets().left(60).bottom(60).top(10).right(10);
        //region.addNode(axisFrame);
        region.getAxisFrame().getAxisX().getAttributes().setAxisLabelFont(new Font("Avenir",Font.PLAIN,14));
        region.getAxisFrame().getAxisY().getAttributes().setAxisLabelFont(new Font("Avenir",Font.PLAIN,14));
        region.getAxisFrame().getAxisX().getAttributes().setAxisTitleFont(new Font("Avenir",Font.PLAIN,18));
        //region.getAxisFrame().getAxisY().getAttributes().setAxisTickMarkSize(10);
        region.getAxisFrame().getAxisX().getAttributes().setAxisTitle("ALL THINGS ARE POSSIBLE");
        region.getAxisFrame().setLimits(0, 2, 0, 2);
        canvas.addNode(region);
        canvas.repaint();
    }


}
