/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.widgets;

import j4np.graphics.Translation2D;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import twig.config.TStyle;
import twig.data.DataSet;
import twig.graphics.TDataNode2D;

/**
 *
 * @author gavalian
 */
public class Legend extends PaveText {
    
    private List<DataSet>   dataStore = new ArrayList<>();
    
    private List<TDataNode2D>   dataStoreNodes = new ArrayList<>();
    
    public Legend(double x, double y){
        super(x,y);
        this.setNDF(true);
        this.drawBox = false;
        this.left(45);
        this.fillBox = false;
        this.editTextContent = false;
    }
    
    public void add(DataSet ds, String legend){
        this.dataStore.add(ds);
        ds.attr().setLegend(legend);
        this.addLine(legend);
    }
    
    public void add(TDataNode2D node, String legend){
        this.dataStoreNodes.add(node);
        node.getDataSet().attr().setLegend(legend);
        this.addLine(legend);
    }
    
    public void add(TDataNode2D node){
        this.dataStoreNodes.add(node);
        String legend = node.getDataSet().attr().getLegend();

        if(legend.length()<1){
            legend = node.getDataSet().attr().getTitle();
        }
        if(legend.length()<1) legend = node.getDataSet().getName();
        
        node.getDataSet().attr().setLegend(legend);
        this.addLine(legend);
    }
    
    public void add(DataSet ds){
        dataStore.add(ds);
        String legend = ds.attr().getLegend();
        
        if(legend.length()<1){
            legend = ds.attr().getTitle();
        }
        if(legend.length()<1) legend = ds.getName();
        
        ds.attr().setLegend(legend);
        addLine(legend);
    }
    
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        //System.out.println("style = " + paveStyle);
        //if(paveStyle == PaveTextStyle.MULTILINE) drawLayerMultiLine(g2d,r,tr);
        
        int height = 14;
        List<Point2D> points = drawLayerMultiLineNuevoCoord(g2d,r,tr);
        int c = 0;
        TStyle style = TStyle.getInstance();
        int np = points.size();
        for(int i = 0; i < np; i++ ){
        
            Point2D p = points.get(i);
            c++;
            
            p.setLocation(p.getX()-height*1.6, p.getY());
            this.dataStoreNodes.get(i).drawLegend(g2d, (int) points.get(i).getX(), 
                    (int) points.get(i).getY() , 22 , 14);
            //MarkerTools.drawSymbolAt(g2d, points.get(i), dataStore.get(i), style, height);
           // System.out.printf(" %d , %8.5f %8.5f\n",c,p.getX(),p.getY());            
        }        
    }
}
