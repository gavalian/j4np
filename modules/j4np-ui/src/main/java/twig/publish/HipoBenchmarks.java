/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.publish;

import j4np.graphics.CanvasLayout;
import twig.data.BarChartBuilder;
import twig.data.DataGroup;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class HipoBenchmarks {
    public static void main(String[] args){
        BarChartBuilder b = new BarChartBuilder();
        b.addEntry("TTree ROOT C++ loop"  , 4.692)
                .addEntry("TTree ROOT C++ DataFrame", 5.175)
                .addEntry("RNTuple ROOT C++ loop", 2.624)
                .addEntry("Tuple Parquete numpy",1.812)
                .addEntry("Tuple HiPO C++  loop", 0.675)
                .addEntry("Tuple HiPO Java loop", 0.637)
                .addEntry("Tuple HiPO C++  DataFrame",0.424)
                .addEntry("Tuple HiPO Java DataFrame",0.452)
                .setTitleY("Time (sec)")
                .setColors("#FF9E7B","#EB6548","#C3375A","#3751AE","#afb47c","#808f4c","#455516","#303f2a")
                .setLabels(new String[]{"Data Formats"});
        DataGroup group = b.build();
        CanvasLayout l = new CanvasLayout();
        l.addRow(0., 1.0, new double[]{0.65,0.35});
        TGCanvas c = new TGCanvas(900,400);
        
        c.view().divide(l);
        //for(DataSet ds : group.getData()) c.draw(ds, "same");
        c.view().region(0).draw(group);//.showLegend(0.05, 0.95);
        c.view().region(0).showLegend(1.05, 0.95);
        c.view().region(0).axisLimitsY(0.01, 5.98);
        c.view().export("bench_root_vs_hipo_read.pdf","pdf");
    }
}
