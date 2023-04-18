/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import j4np.graphics.Background2D;
import j4np.graphics.Canvas2D;
import j4np.graphics.Node2D;
import j4np.graphics.NodeInsets;
import j4np.graphics.NodeRegion2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import twig.config.TStyle;
import twig.data.DataGroup;
import twig.data.DataSet;
import twig.data.GraphErrors;
import twig.data.H1F;
import twig.data.H2F;
import twig.math.Func1D;
import twig.widgets.LatexText;
import twig.widgets.Legend;
import twig.widgets.MultiPaveText;
import twig.widgets.PaveText;
import twig.widgets.PaveText.PaveTextStyle;
import twig.widgets.StyleNode;
import twig.widgets.Widget;

/**
 *
 * @author gavalian
 */
public class TGRegion extends Node2D implements StyleNode {
    
    protected TGAxisFrame  axisFrame = new TGAxisFrame();
    protected TStyle          tStyle = null;
    protected boolean  isInDebugMode = false;
    
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
    
    public TGRegion(boolean drawEmpty){
        super(0,0,50,50);
        this.nodeBackground = null;
        axisFrame.setParent(this);
        getInsets().left(60).bottom(60).top(40).right(40);
        isInDebugMode = drawEmpty;
    }
    
    @Override
    public void drawLayer(Graphics2D g2d, int layer){ 
        Color background = this.getBackgroundColor();

       
        
        if(background!=null){
            g2d.setColor(background);
            NodeRegion2D r = this.getBounds();
            NodeInsets   i = this.getInsets();
            g2d.fillRect( (int) (r.getX()-i.getLeft()), 
                    (int) (r.getY()-i.getTop()),
                    (int) (r.getWidth()+i.getLeft()+i.getRight()),
                    (int) (r.getHeight()+i.getTop()+i.getBottom()));
            //System.out.println(">>> t-region : draw : " + this.getInsets());
        }

        if(this.isInDebugMode==false){
            if(axisFrame.dataNodes.size()>0||axisFrame.widgetNodes.size()>0){
                axisFrame.drawLayer(g2d, layer);
            }
        } else {
            axisFrame.drawLayer(g2d, layer);
        }
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
    
    public void setDebugMode(boolean flag){
        this.isInDebugMode = flag;
    }
    
    public void setBlank(){
       axisFrame.getAxisX().getAttributes().setAxisLabelsDraw(Boolean.FALSE);
       axisFrame.getAxisX().getAttributes().setAxisLineDraw(Boolean.FALSE);
       axisFrame.getAxisX().getAttributes().setAxisTicksDraw(Boolean.FALSE);
       axisFrame.getAxisX().getAttributes().setAxisTitlesDraw(Boolean.FALSE);
       axisFrame.getAxisX().getAttributes().setAxisBoxDraw(Boolean.FALSE);
       
       axisFrame.getAxisY().getAttributes().setAxisLabelsDraw(Boolean.FALSE);
       axisFrame.getAxisY().getAttributes().setAxisLineDraw(Boolean.FALSE);
       axisFrame.getAxisY().getAttributes().setAxisTicksDraw(Boolean.FALSE);
       axisFrame.getAxisY().getAttributes().setAxisTitlesDraw(Boolean.FALSE);
       axisFrame.getAxisY().getAttributes().setAxisBoxDraw(Boolean.FALSE);
    }
    
    public TGRegion wrapX(){
        getInsets().top(0);
        getInsets().bottom(0);
        return this;
    }
    
    public TGRegion wrapY(){
        getInsets().left(0);
        getInsets().right(0);
        return this;
    }
    
    public TGRegion joinX(){
       axisFrame.getAxisX().getAttributes().setAxisLabelsDraw(Boolean.FALSE);
       axisFrame.getAxisX().getAttributes().setAxisTitlesDraw(Boolean.FALSE);       
       return this;       
    }
    
    public TGRegion joinY(){
       axisFrame.getAxisY().getAttributes().setAxisLabelsDraw(Boolean.FALSE);
       axisFrame.getAxisY().getAttributes().setAxisTitlesDraw(Boolean.FALSE);
       return this; 
    }
    
    
    public TGRegion addLabel(double x, double y, String label){
        PaveText ta = new PaveText(label,x,y);
        ta.setNDF(true);
        ta.setDrawBox(false);
        ta.setFillBox(false);
        ta.setFont(axisX().getAttributes().getAxisLabelFont());
        draw(ta); return this;
    }
    
    public TGAxisFrame getAxisFrame(){return this.axisFrame;}
    
    public TGRegion axisLimitsX(double min, double max){  
        this.axisFrame.getAxisX().setFixedLimits(min, max);
        return this;
    }
    
    public TGRegion axisLimitsY(double min, double max){  
        this.axisFrame.getAxisY().setFixedLimits(min, max);
        return this;
    }
    
    public TGRegion showLegend(double x, double y){
        Legend leg = this.getLegend();
        leg.setPosition(x, y);
        this.draw(leg);
        return this;
    }
    
    public TGRegion showStats(double x, double y){
       return this.showStats(x, y, "*");
    }
    
    public void editLegendPosition(){
        /*
        JTextField posX = new JTextField();
        JTextField posY = new JTextField();
        Object[] message = {
            "Position X:", posX,
            "Position Y:", posY
        };
        
        int option = JOptionPane.showConfirmDialog(null, 
                message, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            double x = Double.parseDouble(posX.getText());
            double y = Double.parseDouble(posY.getText());
            Legend l = null;
            for(Widget w : this.axisFrame.widgetNodes){
                if(w instanceof Legend) l = (Legend) w;
            }
            if(l!=null) l.setPosition(x, y);
        } else {
            System.out.println("Login canceled");
        }*/
        Legend l = null;
        for(Widget w : this.axisFrame.widgetNodes){
            if(w instanceof Legend) l = (Legend) w;
        }
        if(l!=null) l.configure(null);
    }
    
    public PaveText getStats(double x, double y, String options){
        
        PaveText stats = new PaveText(x,y);
        stats.setStyle(PaveTextStyle.STATS_MULTILINE);
        stats.setNDF(true);
        stats.fillBox = true;
        stats.drawBox = true;
        stats.setAlign(LatexText.TextAlign.TOP_RIGHT);
        //stats.setBorderColor(Color.black);
        List<String> statsStrings = new ArrayList<>();
        
        for(TDataNode2D dn : axisFrame.dataNodes){
            //List<String> lines = dn.getDataSet().getStats(options);
            statsStrings.addAll(dn.getDataSet().getStats(options));
        }
        stats.addLines(statsStrings);
        return stats;
    }
    
    public MultiPaveText getStatsMulti(double x, double y, String options){
        
        MultiPaveText stats = new MultiPaveText(x,y);

        //stats.setStyle(PaveTextStyle.STATS_MULTILINE);
        //stats.setNDF(true);
        //stats.fillBox = true;
        //stats.drawBox = true;
        //stats.setAlign(LatexText.TextAlign.TOP_RIGHT);
        //stats.setBorderColor(Color.black);
        
        
        for(TDataNode2D dn : axisFrame.dataNodes){
            List<String> lines = dn.getDataSet().getStats(options);
            for(String line : lines) stats.addText(line.split(":"));
        }
        stats.getBorder().borderAlign = LatexText.TextAlign.TOP_RIGHT;
        stats.getBorder().padding.setLocation(5, 5);
        stats.setAlignments("lrr");
        //stats.addLines(statsStrings);
        stats.setFont(TStyle.getInstance().getDefaultPaveTextFont());
        return stats;
    }
    
    public TGRegion showStats(double x, double y, String options){
        /*PaveText stats = new PaveText(x,y);
        stats.setStyle(PaveTextStyle.STATS_MULTILINE);
        stats.setNDF(true);
        stats.fillBox = false;
        stats.drawBox = true;
        //stats.setBorderColor(Color.black);
        List<String> statsStrings = new ArrayList<>();
        for(TDataNode2D dn : axisFrame.dataNodes){
            statsStrings.addAll(dn.getDataSet().getStats(options));
        }
        stats.addLines(statsStrings);*/
        //PaveText stats = this.getStats(x, y, options);
        MultiPaveText stats = this.getStatsMulti(x, y, options);
        //MultiPaveText stats = this.getStatsMulti(0.05, 0.05, options);
        //stats.getBorder().borderAlign = LatexText.TextAlign.BOTTOM_LEFT;
        this.draw(stats);
        return this;
    }
    
    public TGRegion hideStats(){
        
        for(Widget w : this.axisFrame.widgetNodes){
            if(w instanceof MultiPaveText) {
                axisFrame.widgetNodes.remove(w);
            }
        }

        return this;
    }
    
    public TGRegion hideLegend(){
        Legend l = null;
        for(Widget w : this.axisFrame.widgetNodes){
            if(w instanceof Legend) l = (Legend) w;
        }
        if(l!=null) axisFrame.widgetNodes.remove(l);
        return this;
    }
    
    public Legend getLegend(){
        Legend leg = new Legend(0,0);
        leg.setNDF(true);
        leg.drawBox = false;
        leg.fillBox = false;
        for(TDataNode2D node : this.getAxisFrame().dataNodes){
            leg.add(node);
        }
        return leg;
    }
    
    public TGAxis axisX(){ return this.axisFrame.getAxisX();}
    public TGAxis axisY(){ return this.axisFrame.getAxisY();}
    
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
    
    
    public TGRegion draw(DataGroup grp){
        this.axisFrame.clear();
        for(DataSet ds : grp.getData()){
            this.draw(ds, "same");
        }        
        if(!grp.getAxisTickLabels().isEmpty()&&
                !grp.getAxisTickMarks().isEmpty()){
            this.axisFrame.getAxisX().getAttributes().setAxisTicksPosition(grp.getAxisTickMarks());
            this.axisFrame.getAxisX().getAttributes().setAxisTicksString(grp.getAxisTickLabels());
        }
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
        
        String optionsData = options.replaceAll("same", "");
        if(ds instanceof H1F){
            axisFrame.addDataNode(new TGH1F((H1F) ds,optionsData)); return this;
        }
        
        if(ds instanceof H2F){
            axisFrame.addDataNode(new TGH2F( (H2F) ds,optionsData)); return this;
        }
        
        if(ds instanceof GraphErrors){
            axisFrame.addDataNode(new TGENode2D((GraphErrors) ds,optionsData)); return this;
        }
        
        if(ds instanceof Func1D){
            axisFrame.addDataNode(new TGF1D((Func1D) ds,optionsData)); return this;
        }
        System.out.println(" unknown type of data set : " + ds.getClass().getName());
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
