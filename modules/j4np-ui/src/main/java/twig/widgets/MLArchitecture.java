/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.widgets;

import j4np.graphics.Translation2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import twig.config.TStyle;
import twig.graphics.TGCanvas;
import twig.widgets.LatexText.TextAlign;

/**
 *
 * @author gavalian
 */
public class MLArchitecture implements Widget {

    private int[] nodes = null;
    private String[]  inputLabels = null;
    private String[] outputLabels = null;
    
    private Color fillColor = Color.CYAN;
    private Color fillColorHL = new Color(172,196,222);
    private Color fillColorIN = new Color(227,166,0);
    private Color fillColorOUT = new Color(0,138,27);
    private Color connectionColor = new Color(180,180,180);
    
    private Color outlineColor = Color.BLACK;
    private double   nodeSize     = 50;
    
    public MLArchitecture(int[] layers){
        nodes = layers;
    }
    
    public double getSpacingY(double space){
        int max = nodes[0];
        for(int i = 0; i < nodes.length; i++) 
            if(nodes[i]>max) max = nodes[i];
        return space/max;
    }
    
    public MLArchitecture setInputLabels(String[] args){
        this.inputLabels = args; return this;
    }
    
    public MLArchitecture setOutputLabels(String[] args){
        this.outputLabels = args; return this;
    }
    
    public double getPositionX(int column, int row,Rectangle2D r, Translation2D tr){
        double spaceX = 0.9/nodes.length;
        double spaceY = this.getSpacingY(0.9);
        double rowX = 0.1+column*spaceX;
        double startY = 0.5 - nodes[column]*spaceY/2.0;
        double rowY   = startY + row*spaceY;
        double coordX = tr.getX(rowX,r); 
        return coordX;
    }
    
    public double getPositionY(int column, int row,Rectangle2D r, Translation2D tr){
        double spaceX = 0.9/nodes.length;
        double spaceY = this.getSpacingY(0.9);
        double rowX = 0.1+column*spaceX;
        double startY = 0.5 - nodes[column]*spaceY/2.0;
        double rowY   = startY + row*spaceY;
        double coordY = tr.getY(rowY,r);
        return coordY;
    }
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        
        Random rnd = new Random();
        
        for(int i = 0; i < this.nodes.length-1; i++){
            for(int j = 0; j < nodes[i]; j++){
                double x = this.getPositionX(i, j, r, tr);
                double y = this.getPositionY(i, j, r, tr);
                
                g2d.drawRect((int) x, (int) y, 10,10);
                for(int k = 0; k < nodes[i+1]; k++){
                    double xr = this.getPositionX(i+1, k, r, tr);
                    double yr = this.getPositionY(i+1, k, r, tr);
                    
                    double threshold = rnd.nextDouble();
                    g2d.setColor(connectionColor);
                    Stroke stroke = TStyle.getInstance().getLineStroke(8, 1);
                    g2d.setStroke(stroke);
                    if(threshold<1.2){
                        g2d.drawLine( (int) x, (int) y, (int) xr, (int) yr);
                    }
                }
            }
        }
        
        LatexText textL = new LatexText("Input Layer");
        textL.setFont(new Font("Avenir",Font.PLAIN,18));
        
        g2d.setStroke(new BasicStroke(1));
        
        for(int i = 0; i < this.nodes.length; i++){
            for(int j = 0; j < nodes[i]; j++){
                double x = this.getPositionX(i, j, r, tr);
                double y = this.getPositionY(i, j, r, tr);
                
                g2d.setColor(fillColorHL);
                if(i==0) g2d.setColor(fillColorIN);
                if(i==nodes.length-1) g2d.setColor(fillColorOUT);
                g2d.fillArc((int) (x - nodeSize*0.5), (int) (y - nodeSize*0.5),
                        (int) nodeSize, (int) nodeSize,0,360);
                g2d.setColor(this.outlineColor);
                g2d.drawArc((int) (x - nodeSize*0.5), (int) (y - nodeSize*0.5),
                        (int) nodeSize, (int) nodeSize,0,360);
                
                if(i==0&&inputLabels!=null){
                    System.out.println("draw input label " 
                            + j + "  > " + inputLabels[j] + " "
                                    + ((int) (x-10)) + " : "
                    + ((int) y));
                    textL.setColor(fillColorIN);
                    textL.setText(inputLabels[j]);
                    textL.drawString(g2d, (int) (x-80), (int) y, TextAlign.LEFT, TextAlign.CENTER, 1);
                }
                
                if(i==nodes.length-1&&outputLabels!=null){
                    /*System.out.println("draw input label " 
                            + j + "  > " + inputLabels[j] + " "
                                    + ((int) (x-10)) + " : "
                    + ((int) y));*/
                    textL.setColor(fillColorOUT);
                    textL.setText(outputLabels[j]);
                    textL.drawString(g2d, (int) (x+50), (int) y, TextAlign.LEFT, TextAlign.CENTER, 1);
                }
            }
        }
        
                
        LatexText text = new LatexText("Input Layer");
        text.setFont(new Font("Avenir",Font.PLAIN,24));
        double xt = this.getPositionX(0, 0, r, tr);
        text.setText("Input Layers");
        text.setColor(fillColorIN);
        text.drawString(g2d, (int) xt, 0, TextAlign.CENTER,TextAlign.TOP,1);
        xt = this.getPositionX(nodes.length/2, 0, r, tr);
        text.setText("Hidden Layers");
        text.setColor(fillColorHL);
        text.drawString(g2d, (int) xt, 0, TextAlign.CENTER,TextAlign.TOP,1);
        
        xt = this.getPositionX(nodes.length-1, 0, r, tr);
        text.setText("Output Layer");
        text.setColor(fillColorOUT);
        text.drawString(g2d, (int) xt, 0, TextAlign.CENTER,TextAlign.TOP,1);
        /*
        double spaceX = 0.9/nodes.length;
        double spaceY = this.getSpacingY(0.9);
        System.out.printf("space = %.5f, %.5f\n",spaceX,spaceY);
        for(int i = 0; i < this.nodes.length; i++){
            double rowX = 0.1+i*spaceX;
            double coordX = tr.getX(rowX,r);
            //double spaceY = 0.8/nodes[i];
            double startY = 0.5 - nodes[i]*spaceY/2.0;
            System.out.printf("nodes =  %d %f\n",nodes[i],nodes[i]*spaceY);
            for(int j = 0; j < nodes[i]; j++){
                double rowY   = startY + j*spaceY;
                double coordY = tr.getY(rowY,r);

                g2d.setColor(fillColorHL);
                if(i==0) g2d.setColor(fillColorIN);
                if(i==nodes.length-1) g2d.setColor(fillColorOUT);
                g2d.fillArc((int) coordX, (int) coordY, nodeSize, nodeSize, 0, 360);
                g2d.setColor(outlineColor);
                g2d.drawArc((int) coordX, (int) coordY, nodeSize, nodeSize, 0, 360);
            }
        }*/
    }

    @Override
    public boolean isNDF() {
        return true;
    }
    
    public static void main(String[] args){
        
        TGCanvas c = new TGCanvas("network_arch",900,500);
        
        c.view().region().drawFrame(false);
        
       /* MLArchitecture arch = new MLArchitecture(new int[] {6,12,12,6,12,12,6} );
        arch.setInputLabels( new String[]{"W_c_1","W_c_2","0","W_c_4","W_c_5","W_c_5"});
        arch.setOutputLabels(new String[]{"W_c_1","W_c_2","W_c_3","W_c_4","W_c_5","W_c_5"});
        */
        
        MLArchitecture arch = new MLArchitecture(new int[] {6,12,24,24,12,3} );
        arch.setInputLabels( new String[]{"W_c_1","W_c_2","W_c_3","W_c_4","W_c_5","W_c_5"});
        //arch.setOutputLabels(new String[]{"0 - no track", "+ track","- track"});
        arch.setOutputLabels(new String[]{"P", "#theta","#phi"});
        
        /*c.view().region().getAxisFrame().getAxisX().getAttributes().setAxisLineDraw(false);
        c.view().region().getAxisFrame().getAxisX().getAttributes().setAxisBoxDraw(false);
        c.view().region().getAxisFrame().getAxisX().getAttributes().setAxisTicksDraw(false);
        c.view().region().getAxisFrame().getAxisX().getAttributes().setAxisLabelsDraw(false);
        
        c.view().region().getAxisFrame().getAxisY().getAttributes().setAxisLineDraw(false);
        c.view().region().getAxisFrame().getAxisY().getAttributes().setAxisBoxDraw(false);
        c.view().region().getAxisFrame().getAxisY().getAttributes().setAxisTicksDraw(false);
        c.view().region().getAxisFrame().getAxisY().getAttributes().setAxisLabelsDraw(false);
        */
        c.view().region().draw(arch);
    }

    @Override
    public void configure() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
