/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.data;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import twig.config.TPalette;
import twig.config.TStyle;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class BarChartBuilder {
    
    private List<BarChartEntry> entries = new ArrayList<>();
    private String[]             labels = null;
    private int[]                colors = null;
    private String               yTitle = "";
    
    public BarChartBuilder addEntry(String name, double... values){
        List<Double> list = new ArrayList<>();
        for(int i = 0; i < values.length; i++) list.add(values[i]);
        BarChartEntry entry = new BarChartEntry(name, list);
        entries.add(entry);
        return this;
    }
    
    private int getMaxEntries(){
        int max = 0;
        for(int i = 0; i < entries.size(); i++)
            if(entries.get(i).entryValues.size()>max) max = entries.get(i).entryValues.size();
        return max;
    }
    
    public BarChartBuilder setLabels(String[] dl){
        this.labels = dl; return this;
    }
    
    public BarChartBuilder setTitleY(String yt){
        this.yTitle = yt; return this;
    }
    
    public BarChartBuilder setColors(int[] dc){
        this.colors = dc; return this;
    }
    
    public BarChartBuilder setColors(String... sc){
        this.colors = new int[sc.length];
        for(int i = 0; i < this.colors.length; i++){
            this.colors[i] = TPalette.createColorFromString(sc[i]);
        }
        return this;
    }
    public DataGroup build(){
        int  max = getMaxEntries();
        int bins = (entries.size()+1)*max + 1;
        int size = entries.size();
        
        DataGroup group = new DataGroup();
        
        for(int i = 0; i < entries.size(); i++){
            H1F h = new H1F("h"+i,bins,0.5,bins+0.5);
            h.attr().setFillColor(i+3+40);
            h.attr().setTitleY(yTitle);
            if(this.colors!=null){
                if(colors.length>i) h.attr().setFillColor(colors[i]);
            }
            //h.attr().set("lc=0");
            h.attr().setLegend(entries.get(i).entryName);
            for(int b = 0; b < entries.get(i).entryValues.size(); b++){
                int cbin = 1 + b*(size+1) + i;
                h.setBinContent(cbin , entries.get(i).entryValues.get(b) );
            }
            System.out.println(h);
            group.getData().add(h);
        }
        
        int step = size + 1;
        int tbin = step/2;
        H1F h = (H1F) group.getData().get(0);
        for(int i = 0; i < max; i++){
            Double v = h.getXaxis().getBinCenter(tbin+step*i);
            group.getAxisTickMarks().add(v);
            if(this.labels!=null){
                if(labels.length>i) group.getAxisTickLabels().add(labels[i]);
            } else {
                group.getAxisTickLabels().add(v.toString());
            }
        }
        
        return group;
    }
    
    public static class BarChartEntry {
        String entryName = "";
        List<Double> entryValues = null;
        BarChartEntry(String name, List<Double> values){
            entryName = name; entryValues = values;
        }
    }
    
    public static void main(String[] args){
        
        TStyle.getInstance().setDefaultPaveTextFont( new Font("Paletino",Font.PLAIN,18));
        TStyle.getInstance().setDefaultAxisLabelFont(new Font("Paletino",Font.PLAIN,18));
        TStyle.getInstance().setDefaultAxisTitleFont(new Font("Paletino",Font.PLAIN,20));
        
        BarChartBuilder b = new BarChartBuilder();
        
        DataGroup group = b.addEntry("ROOT Read, C++", 4.50,3.5,7.8,2.4)
                .addEntry("ROOT RNTuple, C++", 3.2,1.2,2.45,2)
                //.addEntry("ROOT RNTuple, C++", 3.2,1.2,2.45)
                //.addEntry("HIPO Read, C++", 3.2,1.2,2.45)
                .addEntry("HIPO Read, Java", 3.7,6.2,1.6,2)
                
                .setTitleY("Time (sec)")
                .setColors(new int[]{56,57,58})
                .setLabels(new String[]{"Macbook M1","AMD","Intel","Macbook i9"})
                .build();
        
        System.out.println("size = " + group.getData().size());
        TGCanvas c = new TGCanvas();
        for(DataSet ds : group.getData()) c.draw(ds, "same");
        c.view().region().draw(group);//.showLegend(0.05, 0.95);
        c.view().region().showLegend(0.05, 0.95);
    }
}
