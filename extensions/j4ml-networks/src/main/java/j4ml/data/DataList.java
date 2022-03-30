/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.data;

import j4np.utils.io.DataArrayUtils;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DataList {
    
    private List<DataEntry> dataList = new ArrayList<>();
    private int       dataShowLimit = 10;
    
    public DataList(){
        
    }
    
    public void add(DataEntry pair){
        dataList.add(pair);
    }
    
    public List<DataEntry> getList(){ return dataList;}

    public void show(){
        System.out.printf("\n>>>>> data pair list, size = %d\n",this.dataList.size());
        int size = dataList.size();
        if(size<2*dataShowLimit+1){
            for(int i = 0; i < size; i++){
                dataList.get(i).show();
            }
        } else {
           for(int i = 0; i < dataShowLimit; i++){
                dataList.get(i).show();
           }
           System.out.println("..........");
           for(int i = 0; i < dataShowLimit; i++){
                dataList.get(size-1-dataShowLimit+i).show();
            }
        }
        
    }
    
    public static DataList fromCSV(String file, int[] inputs, int[] outputs){
        CSVReader r = new CSVReader();
        r.setInputOutput(inputs, outputs);
        r.open(file);
        return r.getData();
    }
    
    public void  turnClassifier(int nclasses){
        for(int i = 0; i < getList().size(); i++){
            DataEntry p = getList().get(i);
            double[] second = p.getSecond();
            double[] first  = p.getFirst();
            
            double[] output = new double[nclasses];
            for(int k = 0; k < output.length; k++) output[k] =0.0;
            output[(int) (first[0]-1)] = 1.0;
            p.set(output,second);
        }
    }
    
    public void transform(int nclasses){
        for(DataEntry dp : this.dataList)
            dp.transform(nclasses);
    }
    
    
    public static void normalizeInput(DataList list, DataNormalizer norm){
        for(int i = 0; i < list.getList().size(); i++){
            float[] first = list.getList().get(i).floatFirst();
            for(int k = 0; k < first.length; k++){
                first[k] = norm.normalize(first[k], k);
            }
            list.getList().get(i).setInput(first);
        }
    }
    
    public static void normalizeOutput(DataList list, DataNormalizer norm){
        for(int i = 0; i < list.getList().size(); i++){
            float[] second = list.getList().get(i).floatSecond();
            for(int k = 0; k < second.length; k++){
                second[k] = norm.normalize(second[k], k);
            }
            list.getList().get(i).setOutput(second);
        }
    }
    
    public static void denormalizeOutput(DataList list, DataNormalizer norm){
        for(int i = 0; i < list.getList().size(); i++){
            float[] second = list.getList().get(i).floatSecond();
            for(int k = 0; k < second.length; k++){
                second[k] = norm.denormalize(second[k], k);
            }
            list.getList().get(i).setOutput(second);
        }
    }
    
    public static void denormalizeInfered(DataList list, DataNormalizer norm){
        for(int i = 0; i < list.getList().size(); i++){
            float[] second = list.getList().get(i).getInfered();
            for(int k = 0; k < second.length; k++){
                second[k] = norm.denormalize(second[k], k);
            }
            list.getList().get(i).setInfered(second);
        }
    }
    
    public String toCSVString(){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < this.dataList.size(); i++) 
            str.append(dataList.get(i).toCSVString()).append("\n");
        return str.toString();
    }
    /*
    public DataList getNormalizedFirst(double[] min, double[] max){
        DataList dList = new DataList();
        
        for(int i = 0; i < this.getList().size(); i++){
            DataEntry p = getList().get(i);
            boolean  preserve = true;
            double[]  first = new double[p.getFirst().length];
            double[] second = new double[p.getSecond().length];
            for(int f = 0; f < second.length; f++) second[f] = p.getSecond()[f];
            
            for(int c = 0; c < min.length; c++){
                double value = p.getFirst()[c];
                double normalized = (value-min[c])/(max[c]-min[c]);
                first[c] = normalized;
                if(value<min[c]||value>max[c]) preserve = false;
            }
            if(preserve==true) dList.add(new DataEntry(first,second));
        }
        return dList;
    }
    */
    
    /*
    public DataList getNormalized(double[] min, double[] max){
        DataList dList = new DataList();        
        for(int i = 0; i < this.getList().size(); i++){
            DataEntry p = getList().get(i);
            boolean  preserve = true;
            double[]  first = new double[p.getFirst().length];
            double[] second = new double[p.getSecond().length];
            for(int f = 0; f < first.length; f++) first[f] = p.getFirst()[f];
            for(int c = 0; c < min.length; c++){
                double value = p.getSecond()[c];
                double normalized = (value-min[c])/(max[c]-min[c]);
                second[c] = normalized;
                if(value<min[c]||value>max[c]) preserve = false;
            }
            if(preserve==true) dList.add(new DataEntry(first,second));
        }
        return dList;
    }*/
    
    public static DataList compare(DataList aList, DataList bList){
        return DataList.compare(aList, bList, false);
    }
    
    public static DataList compare(DataList aList, DataList bList, boolean normalize){
        if(aList.getList().size()!=bList.getList().size()){
            System.out.println("[DataPairList] data set sizes are not same : " 
            + aList.getList().size() + " : " + bList.getList().size());
            return null;
        }
        
        DataList  cList = new DataList();
        
        for(int i = 0 ; i < aList.getList().size(); i++){
            double[]  first = DataArrayUtils.copy(aList.getList().get(i).getFirst());
            double[] second = DataArrayUtils.copy(aList.getList().get(i).getSecond());
            double[]  aData = aList.getList().get(i).getSecond();
            double[]  bData = bList.getList().get(i).getSecond();
            for(int k = 0; k < second.length; k++){ 
                second[k] = aData[k] - bData[k];
                if(normalize==true){
                    if(bData[k]<0.00000000001){
                        second[k] = 0.0;
                    } else {
                        second[k] = (aData[k] - bData[k])/bData[k];
                    }
                }
            }
        }
        return cList;
    }
    
    public void shuffle(){
        Collections.shuffle(dataList);
        Collections.shuffle(dataList);
    }
    
    public static DataList[] split(DataList list, double... fractions){
        DataList[] lists = new DataList[fractions.length];
        for(int i = 0; i < lists.length; i++)
            lists[i] = new DataList();
        
        int   nrows = list.getList().size();
        int[]   nev = new int[fractions.length];
        nev[0] = (int) (nrows*fractions[0]);
        for(int i = 1; i < fractions.length; i++){
            nev[i] = nev[i-1] + (int) (nrows*fractions[i]);
        }
        
        int current = 0;
        for(int i = 0; i < list.getList().size(); i++){
            if(i>nev[current]) current++;
            if(current>=0&&current<lists.length){
                lists[current].add(list.getList().get(i));                
            }
        }
        return lists;
    }
    
    public void export(String file){
        TextFileWriter w = new TextFileWriter();
        w.open(file);
        
        for(int i = 0; i < dataList.size(); i++)
            w.writeString(dataList.get(i).toCSVString());
        w.close();
    }
    public void scan(){
        
        System.out.println("** scanning data pair list with entrie = " + dataList.size());
        DataEntry pair = dataList.get(0);
        int    nFirst = pair.getFirst().length;
        int   nSecond = pair.getSecond().length;
        
        List<DataRange>  rangesFirst = new ArrayList<>();
        List<DataRange> rangesSecond = new ArrayList<>();
        
        for(int i = 0; i <  nFirst; i++){ rangesFirst.add(new DataRange());}
        for(int i = 0; i < nSecond; i++){ rangesSecond.add(new DataRange());}
        
        for(int i = 0; i < dataList.size(); i++){
            DataEntry p = dataList.get(i);
            for(int j = 0; j <  nFirst; j++) rangesFirst.get(j).add(p.getFirst()[j]);
            for(int j = 0; j < nSecond; j++) rangesSecond.get(j).add(p.getSecond()[j]);
        }
        
        for(int i = 0; i <  nFirst; i++){ 
            System.out.printf("%4d : %s\n",i,rangesFirst.get(i).toString());
        }
        System.out.println("=>");
        for(int i = 0; i <  nSecond; i++){ 
            System.out.printf("%4d : %s\n",i,rangesSecond.get(i).toString());
        }
    }

    
    
    public static class DataRange {
        
        private double min = 0.0;
        private double max = 0.0;
        
        private int overflowCounter = 0;
        private int underflowCounter = 0;
        
        private double  overflowLimit = 1.0;
        private double underflowLimit = 0.0;
        
        private int    initialized = 0;
        
        public DataRange(){
            
        }
        
        public void add(double value){
            
            if(value<underflowLimit) underflowCounter++;
            if(value>overflowLimit) overflowCounter++;
            
            if(initialized==0){
                min = value; max = value;
                initialized = 1;
            } else {
                if(value<min) min = value;
                if(value>max) max = value;
            }
        }
        
        public double min(){return min;}
        public double max(){return max;}
        
        public int overflow(){return overflowCounter;}
        public int underflow(){return underflowCounter;}
        
        @Override
        public String toString(){
            return String.format("%8.5f - %8.5f : %8d, %8d", 
                    min,max,underflowCounter,overflowCounter);
        }
    }
}
