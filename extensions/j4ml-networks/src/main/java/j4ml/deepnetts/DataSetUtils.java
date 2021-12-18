/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.deepnetts;

import deepnetts.data.TabularDataSet;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.visrec.ml.data.DataSet;
import twig.data.DataRange;
import twig.data.H1F;

/**
 *
 * @author gavalian
 */
public class DataSetUtils {
    
    public static String[] generateNames(int input, int output){
        String[] names = new String[input+output];
        for(int i = 0; i < input; i++) names[i] = "In_" + i;
        for(int i = 0; i < output; i++) names[i+input] = "Out_" + i;        
        return names;
    }
    
    public static DataSet getRandomSet(int count, int inputs, int outputs){
        
        Random r = new Random();
        
        TabularDataSet  dataset = new TabularDataSet(inputs,outputs);
        for(int i = 0; i < count; i++){
            float[] features = new float[inputs];
            float[]   labels = new float[outputs];
            for(int k = 0; k < inputs; k++)  features[k] = r.nextFloat();
            for(int k = 0; k < outputs; k++)   labels[k] = 0.0f;
            
            int    which = r.nextInt(outputs);
            //System.out.println("value = " + which);
            labels[which] = 1.0f;
            
            dataset.add(new TabularDataSet.Item(features, labels));
        }
        String[] names = DataSetUtils.generateNames(inputs, outputs);
        dataset.setColumnNames(names);
        return dataset;
    }
    
    public static List<DataRange> ranges(DataSet data){
        List<DataRange> ranges = new ArrayList<>();
        Iterator iter = data.iterator();
        int counter = 0;
        while(iter.hasNext()){
            TabularDataSet.Item  item = (TabularDataSet.Item) iter.next();
            float[] inputs = item.getInput().getValues();
            if(counter==0){
                for(int k = 0; k < inputs.length; k++){
                    ranges.add(new DataRange(inputs[k],inputs[k], 0.0,0.0));
                }
            } else {
                for(int k = 0; k < inputs.length; k++){
                    ranges.get(k).grow(inputs[k], 0.0);
                }
            }
            counter++;            
        }
        return ranges;
    }
    
    public static List<DataRange> rangesLabel(DataSet data){
        List<DataRange> ranges = new ArrayList<>();
        Iterator iter = data.iterator();
        int counter = 0;
        while(iter.hasNext()){
            TabularDataSet.Item  item = (TabularDataSet.Item) iter.next();
            float[] inputs = item.getTargetOutput().getValues();
            if(counter==0){
                for(int k = 0; k < inputs.length; k++){
                    ranges.add(new DataRange(inputs[k],inputs[k], 0.0,0.0));
                }
            } else {
                for(int k = 0; k < inputs.length; k++){
                    ranges.get(k).grow(inputs[k], 0.0);
                }
            }
            counter++;            
        }
        
        for(int i = 0; i < ranges.size(); i++) ranges.get(i).padX(0.05, 0.05);
        return ranges;
    }
    
    public static List<H1F> featureHist(DataSet data, boolean force){
        
        List<DataRange> ranges = DataSetUtils.ranges(data);
        List<H1F>        hists = new ArrayList<>();
        for(int k = 0; k < ranges.size(); k++){
            Rectangle2D rect = ranges.get(k).getRange();
            H1F h;
            if(force==true){
                h = new H1F(String.format("feature_%d", k+1),
                        120,0.0,1.0);
            } else {
                h = new H1F(String.format("feature_%d", k+1),
                        120,rect.getX(),rect.getX()+rect.getWidth());
            }
            h.attr().setTitleX(String.format("feature %d", k+1));
            h.attr().setFillColor(162);
            h.attr().setLineColor(2);
            
            hists.add(h);
        }
        
        Iterator iter = data.iterator();
        int counter = 0;
        while(iter.hasNext()){
            TabularDataSet.Item  item = (TabularDataSet.Item) iter.next();
            float[] inputs = item.getInput().getValues();
            for(int k = 0; k < inputs.length; k++) hists.get(k).fill(inputs[k]);            
        }
        
        return hists;
    }
    
    public static List<H1F> labelHist(DataSet data, boolean force){
        
        List<DataRange> ranges = DataSetUtils.rangesLabel(data);
        List<H1F>        hists = new ArrayList<>();
        for(int k = 0; k < ranges.size(); k++){
            Rectangle2D rect = ranges.get(k).getRange();
            H1F h;
            if(force==true){
                h = new H1F(String.format("feature_%d", k+1),
                        120,0.0,1.0);
            } else {
                h = new H1F(String.format("feature_%d", k+1),
                        120,rect.getX(),rect.getX()+rect.getWidth());
            }
            h.attr().setTitleX(String.format("feature %d", k+1));
            h.attr().setFillColor(164);
            h.attr().setLineColor(4);
            
            hists.add(h);
        }
        
        Iterator iter = data.iterator();
        int counter = 0;
        while(iter.hasNext()){
            TabularDataSet.Item  item = (TabularDataSet.Item) iter.next();
            float[] inputs = item.getTargetOutput().getValues();
            for(int k = 0; k < inputs.length; k++) hists.get(k).fill(inputs[k]);            
        }
        
        return hists;
    }
}
