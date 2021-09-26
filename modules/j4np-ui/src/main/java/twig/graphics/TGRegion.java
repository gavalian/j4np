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
import javax.swing.JFrame;
import twig.config.TStyle;
import twig.widgets.StyleNode;

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
