/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.data;

import java.util.ArrayList;
import java.util.List;
import twig.graphics.TGCanvas;
import twig.graphics.TGDataCanvas;

/**
 *
 * @author gavalian
 */
public class DataGroup {
    
    private List<DataSet>      groupData = new ArrayList<>();
    private List<Double>   axisTickMarks = new ArrayList<>();
    private List<String>  axisTickLabels = new ArrayList<>();
    
    public DataGroup(){
        
    }
    
    public DataGroup[] duplicate(String... exts){
        DataGroup[] groups = new DataGroup[exts.length];
        for(int i = 0; i < groups.length; i++)
            groups[i] = this.duplicateDataGroup(exts[i]);
        return groups;
    }
    
    private DataGroup duplicateDataGroup(String ext){
        DataGroup group = new DataGroup();
        for(int k = 0; k < groupData.size(); k++){
            DataSet ds = groupData.get(k);
            if(ds instanceof H1F) { 
                H1F h = ((H1F) ds).copy();
                h.setName(ds.getName()+ext);
                h.attr().setTitle(ds.attr().getTitle());
                h.attr().setTitleX(ds.attr().getTitleX());
                h.attr().setTitleY(ds.attr().getTitleY());
                h.reset();
                group.groupData.add(h);
            }
            
            if(ds instanceof H2F) { 
                H2F h = ((H2F) ds).histClone(ds.getName()+ext);
                //h.setName(ds.getName()+ext);
                h.reset();
                group.groupData.add(h);
            }
            if(ds instanceof GraphErrors) { 
                GraphErrors gr = ((GraphErrors) ds).copy();
                gr.setName(ds.getName()+ext);
            }
            
        }
        return group;
    }
    
    
    public void lineColors(int... colors){
        for(int i = 0; i < colors.length; i++){ 
            this.groupData.get(i).attr().setLineColor(colors[i]);
        }
    }
    
    public void fillColors(int... colors){
        for(int i = 0; i < colors.length; i++){ 
            this.groupData.get(i).attr().setFillColor(colors[i]);
        }
    }
    
    public void fill(double... values){
        int index = 0;
        int  data = 0;
        while(index<values.length&&data<groupData.size()){
            DataSet ds = this.groupData.get(data);
            data++;
            if(ds instanceof H1F){
                ((H1F) ds).fill(values[index]);
                index++;
            } else {            
                if(ds instanceof H2F){
                    ((H2F) ds).fill(values[index],values[index+1]);
                    index+=2;
                }
            }
        }
    }
    
    public void draw(TGDataCanvas c){
        c.cd(0);
        for(DataSet ds : this.groupData){
            c.region().draw(ds,"same");
            c.next();
        }
    }
    
    public void draw(TGCanvas c){
        this.draw(c.view());
    }
    
    public DataGroup  add(DataSet d){this.groupData.add(d);return this;}
    public List<DataSet> getData(){ return groupData;}
    public List<Double> getAxisTickMarks(){return this.axisTickMarks;}
    public List<String> getAxisTickLabels(){return this.axisTickLabels;}
}
