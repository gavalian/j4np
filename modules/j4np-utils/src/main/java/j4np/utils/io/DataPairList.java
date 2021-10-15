/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.io;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DataPairList {
    
    private List<DataPair> dataList = new ArrayList<>();
    private int       dataShowLimit = 10;
    
    public DataPairList(){
        
    }
        
    public void add(DataPair pair){
        dataList.add(pair);
    }
    
    public List<DataPair> getList(){ return dataList;}

    public void show(){
        
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
    public void  turnClassifier(int nclasses){
        for(int i = 0; i < getList().size(); i++){
            DataPair p = getList().get(i);
            double[] second = p.getSecond();
            double[] first  = p.getFirst();
            
            double[] output = new double[nclasses];
            for(int k = 0; k < output.length; k++) output[k] =0.0;
            output[(int) (first[0]-1)] = 1.0;
            p.set(output,second);
        }
    }
    
    public DataPairList getNormalized(double[] min, double[] max){
        DataPairList dList = new DataPairList();
        
        for(int i = 0; i < this.getList().size(); i++){
            DataPair p = getList().get(i);
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
            if(preserve==true) dList.add(new DataPair(first,second));
        }
        return dList;
    }
    
    public static DataPairList compare(DataPairList aList, DataPairList bList){
        return DataPairList.compare(aList, bList, false);
    }
    
    public static DataPairList compare(DataPairList aList, DataPairList bList, boolean normalize){
        if(aList.getList().size()!=bList.getList().size()){
            System.out.println("[DataPairList] data set sizes are not same : " 
            + aList.getList().size() + " : " + bList.getList().size());
            return null;
        }
        
        DataPairList  cList = new DataPairList();
        
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
    
    public void scan(){
        
        System.out.println("** scanning data pair list with entrie = " + dataList.size());
        DataPair pair = dataList.get(0);
        int    nFirst = pair.getFirst().length;
        int   nSecond = pair.getSecond().length;
        
        List<DataRange>  rangesFirst = new ArrayList<>();
        List<DataRange> rangesSecond = new ArrayList<>();
        
        for(int i = 0; i <  nFirst; i++){ rangesFirst.add(new DataRange());}
        for(int i = 0; i < nSecond; i++){ rangesSecond.add(new DataRange());}
        
        for(int i = 0; i < dataList.size(); i++){
            DataPair p = dataList.get(i);
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
