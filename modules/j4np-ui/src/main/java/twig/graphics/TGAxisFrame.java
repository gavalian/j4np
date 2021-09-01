/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import j4np.graphics.Canvas2D;
import j4np.graphics.Node2D;
import j4np.graphics.Translation2D;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;
import twig.config.TAxisAttributes.AxisType;
import twig.config.TStyle;
import twig.widgets.Widget;

/**
 *
 * @author gavalian
 */
public class TGAxisFrame extends Node2D implements Widget {
    
    private TGAxis axisX = new TGAxis(AxisType.AXIS_X);
    private TGAxis axisY = new TGAxis(AxisType.AXIS_Y);
    private TGAxis axisZ = new TGAxis(AxisType.AXIS_Z);

    private TStyle tStyle = null;

    private final Translation2D axisFrameRange = new Translation2D(0.0,1.0,0.0,1.0);
    
    public TGAxisFrame(){
            
        super(0,0,500,500);
        
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
    }
    
    @Override
    public void drawLayer(Graphics2D g2d, int layer){
        Node2D parent = this.getParent();
        Rectangle2D r = parent.getBounds().getBounds();
        System.out.println(parent.getTranslation());
        System.out.println("border = " + parent.getBounds().getBounds());
        axisX.draw(g2d, r, axisFrameRange);
        axisY.draw(g2d, r, axisFrameRange);
        axisZ.draw(g2d, r, axisFrameRange);
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
        return this;
    }
    public TGAxis getAxisX(){ return this.axisX;}
    public TGAxis getAxisY(){ return this.axisY;}
    public TGAxis getAxisZ(){ return this.axisZ;}

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
