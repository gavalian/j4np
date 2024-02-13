/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import j4np.graphics.CanvasLayout;
import java.util.List;
import twig.data.DataGroup;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class LayoutDebug {
    public static void divideWithLayout(){
        TGCanvas c = new TGCanvas();        
        
        CanvasLayout l = new CanvasLayout();
        l.addRow(0., 0.25, new double[]{0.5,0.5})
                .addRow(0.25, 0.5, new double[]{0.25,0.5,0.25})
                .addRow(0.75, 0.25, new double[]{0.2,0.2,0.4,0.2});
        
        l.show();
        //c.view().divide(3, 3);
        c.view().divide(l);
        for(int i = 0; i < 9; i++){
            H1F h = TDataFactory.createH1F(3000);
            c.cd(i).draw(h);
        }
    }
    
    public static void dataGroupLayout(){
        TGCanvas c = new TGCanvas();        
        
        //CanvasLayout l = new CanvasLayout();
        //l.addRow(0., 0.25, new double[]{0.5,0.5})
        //        .addRow(0.25, 0.5, new double[]{0.25,0.5,0.25})
        //        .addRow(0.75, 0.25, new double[]{0.2,0.2,0.4,0.2});
        
        //l.show();
        //c.view().divide(3, 3);
        //c.view().divide(l);
        CanvasLayout layout = new CanvasLayout();
        layout.addRow(0, 0.25, new double[]{0.5,0.25,0.25});
        layout.addRow(0.25, 0.25, new double[]{0.25,0.25,0.5});
        layout.addRow(0.5, 0.25, new double[]{0.5,0.25,0.25});
        
        layout.addRow(0.75, 0.25, new double[]{0.2,0.4,0.4});
        
        //DataGroup grp = new DataGroup("Test",2,3);
        DataGroup grp = new DataGroup("Test",layout);
        grp.setRegionAttributes("mt=10,mr=15,fc=#DDDDDD");
        for(int i = 0; i < 12; i++){
            H1F h = TDataFactory.createH1F(3000);
            grp.add(h, i, "");
            //c.cd(i).draw(h);
        }
        
        grp.draw(c.view(), true);
    }
    
    public static void gridView(){
        
        CanvasLayout layout = CanvasLayout.grid(3, 6);        
        List<H1F> h = TDataFactory.createHistograms(18,true);
        layout.getInsets().left(60).bottom(60).top(10).right(10);
        TGCanvas c = new TGCanvas(800,800);
        c.view().divide(layout);
        for(int i = 0; i < h.size(); i++) c.cd(i).draw(h.get(i));
        for(int i = 0; i < 18; i++){
            c.region(i).set("ml=0,mr=10,mt=10,mb=0,fc=#FAF5ED,al=n");
        }
        
        for(int i = 0; i < 4; i++) c.region(i+12).set("al=x");
        for(int i = 0; i < 4; i++) c.region(i*4).set( "al=y");
        c.region(12).set("al=xy");
    }
    
    public static void combinedGrid(){
        CanvasLayout layout = new CanvasLayout();
        layout.addColumn(0, 0.4, new double[]{0.33,0.33,0.33});
        layout.addGrid(  0.4, 0.00, 0.6, 0.33, 3, 2);
        layout.addGrid(  0.4, 0.66, 0.6, 0.33, 3, 2);
        TGCanvas c = new TGCanvas(800,800);
        c.view().divide(layout);
        List<H1F> h = TDataFactory.createHistograms(15,true);
        for(int i = 0; i < h.size(); i++) c.cd(i).draw(h.get(i));
        for(int i = 0; i < h.size(); i++){
            c.region(i).set("ml=15,mr=10,mt=10,mb=0,fc=#FAF5ED,al=n");
        }
    }
    
    public static void main(String[] args){
        //LayoutDebug.divideWithLayout();
        //LayoutDebug.dataGroupLayout();
        //LayoutDebug.gridView();
        LayoutDebug.combinedGrid();
    }
}
