/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.instarec.utils;


import j4np.utils.io.DataArrayUtils;
import j4np.utils.io.TextFileReader;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import twig.data.DataVector;
import twig.data.H1F;
import twig.tree.Tree;

/**
 *
 * @author gavalian
 */
public class DataList extends Tree {
    
    private List<DataEntry>  dataList = new ArrayList<>();
    private int         dataShowLimit = 10;
    private int      iteratorPosition = 0;
    
    public DataList(){
        
    }
    
    public void add(DataEntry pair){
        dataList.add(pair);
    }
    
    public void add(DataEntry pair, boolean check){
        
        if(check==false) { dataList.add(pair); return; }
        boolean pass = true;
        for(int i = 0; i < pair.features().length; i++){
            if(pair.features()[i]<0||pair.features()[i]>1.00){
                pass = false; break;
            }
        }
        
        for(int i = 0; i < pair.labels().length; i++){
            if(pair.labels()[i]<0||pair.labels()[i]>1.00){
                pass = false; break;
            }
        }
        
        if(pass==true) dataList.add(pair);
    }
    
    
    public void collisions(){
        int size = this.dataList.size();
        for(int i = 0; i < size-1; i++){
            if(i%100==0) System.out.println("--- " + i);
            for(int j = i+1; j< size; j++){
                double distance = this.dataList.get(i).distance(this.dataList.get(j));
                
                if(distance<2.0){
                    if(DataEntry.getLabelClass(this.dataList.get(i).labels())
                            != DataEntry.getLabelClass(this.dataList.get(j).labels())){
                        System.out.printf("collision : %12d %12d (%e) : %s %s\n",
                                i,j, distance,
                                Arrays.toString(this.dataList.get(i).labels()),
                                Arrays.toString(this.dataList.get(j).labels())
                        );
                    }
                }
            }
        }
    }
    public List<DataEntry> getList(){ return dataList;}
    
    public static DataList csv(String file, int[] inputs, int[] outputs){
        DataList list = new DataList();
        TextFileReader r = new TextFileReader(file);
        while(r.readNext()==true){
            float[] input = r.getAsFloat(inputs);
            float[] output = r.getAsFloat(outputs);
            list.add(new DataEntry(input,output));
        }
        return list;
    }
    
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
    

    public static DataList generateRandom(DataList list, int minReplace, int maxReplace, float[] label){
        DataList result = new DataList();
        Random r = new Random();
        for (int i = 0; i < list.getList().size(); i++){
            int which = r.nextInt(15)+2;
            if(which+i>=list.getList().size()){
                which = i-which;
            } else {
                which = which + i;
            }
            float[] original  = list.getList().get(i).featuresCopy();
            float[] reference = list.getList().get(which).features();
            int howMany = r.nextInt(maxReplace-minReplace) + minReplace;
            for(int k = 0; k < howMany; k++){
                int order = r.nextInt(original.length);
                original[order] = reference[order];
            }
            result.add(new DataEntry(original,label));
        }
        return result;
    }
    
    public static int[] range(int start, int end){
        int size = end-start;
        int[] index = new int[size];
        for(int i = 0 ; i < size; i++) index[i] = start + i;
        return index;
    }
    
    public void reduce(int size){
        Random r = new Random();
        //System.out.println(" reducing ......");
        while(this.dataList.size()>size){
            //System.out.println(" size = " + this.dataList.size() + "  requred = " + size);
            int which = r.nextInt(dataList.size());
            this.dataList.remove(which);
        }
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
    
    @Override
    public void export(String file){
        TextFileWriter w = new TextFileWriter();
        w.open(file);        
        for(int i = 0; i < dataList.size(); i++)
            w.writeString(dataList.get(i).toCSVString());
        w.close();
    }
    
    @Override
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

    public H1F diff(int label){
        DataVector x = new DataVector();
        for(int i = 0; i < getList().size(); i++){
            if(this.getList().get(i).getInfered()!=null){
                x.add(getList().get(i).getInfered()[label]-getList().get(i).getSecond()[label]);
            }
        }
        H1F h = H1F.create("label_"+label, 100, x);
        return h;
    }
    
    public H1F diff(int label, boolean norm){
        DataVector x = new DataVector();
        for(int i = 0; i < getList().size(); i++){
            if(this.getList().get(i).getInfered()!=null){
                double value = getList().get(i).getInfered()[label]-getList().get(i).getSecond()[label];
                if(norm==true&&getList().get(i).getSecond()[label]!=0.0){
                    x.add(value/getList().get(i).getSecond()[label]);
                }
            }
        }
        H1F h = H1F.create("label_"+label, 100, x);
        return h;
    }
    
    public H1F label(int label){
        DataVector x = new DataVector();
        for(int i = 0; i < getList().size(); i++){
            x.add(getList().get(i).getSecond()[label]);
        }
        H1F h = H1F.create("label_"+label, 100, x);
        return h;
    }

    @Override
    public double getValue(int order) {
        double[] first = dataList.get(this.iteratorPosition).getFirst();
        int nsize = first.length;
        if(order<nsize) return first[order];
        
        int index = order - first.length;
        return dataList.get(this.iteratorPosition).getSecond()[index];
    }

    @Override
    public double getValue(String branch) {
        int order = this.getBranchOrder(branch);
        return this.getValue(order);
    }

    @Override
    public List<String> getBranches() {
        List<String> branches = new ArrayList<>();
        int  n_inputs = this.dataList.get(this.iteratorPosition).getFirst().length;
        int n_outputs = this.dataList.get(this.iteratorPosition).getSecond().length;
        for(int i = 0; i <  n_inputs; i++) branches.add("i"+i);
        for(int i = 0; i < n_outputs; i++) branches.add("o"+i);
        return branches;
    }
    
    @Override
    public int getBranchOrder(String name) {
        if(name.startsWith("i")==true){
            int number = Integer.parseInt(name.replace("i", ""));
            return number;
        } 
        int shift = Integer.parseInt(name.replace("o", ""));
        return shift + this.dataList.get(this.iteratorPosition).getFirst().length;
    }
    

    @Override
    public void reset() {
        this.iteratorPosition = 0;
    }

    @Override
    public boolean next() {
        if(iteratorPosition>=(dataList.size()-1)) return false;
        iteratorPosition++;
        return true;
    }

    @Override
    public void configure() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
