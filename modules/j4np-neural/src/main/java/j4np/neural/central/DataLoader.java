/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.central;

import j4ml.data.DataList;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DataLoader {
    
    public static class DataSegment {
        CompositeNode snode = null;
        public int[]  index = null;
        public DataSegment(CompositeNode node, int n1, int n2, int n3){
            snode = node;
            index = new int[]{n1,n2,n3};
        }
        
        public float[] getFeatures(){
            float[] data = new float[12];
            
            for(int i = 0; i < 4; i++)   data[i] = (float) snode.getDouble(2+i, index[0]);
            for(int i = 0; i < 4; i++) data[i+4] = (float) snode.getDouble(2+i, index[1]);
            for(int i = 0; i < 4; i++) data[i+8] = (float) snode.getDouble(2+i, index[2]);
            
            return data;
        }
        
        public float[] getLabel(){
            float[] label = new float[4];
            for(int i = 0; i < 4; i++) label[i] = (float) snode.getDouble(6+i, index[0]);
            
            for(int i = 0; i < 4; i++){
                float f1 = (float) snode.getDouble(6+i, index[1]);
                if(Math.abs(label[i]-f1)>0.0001) label[i] = 0.0f;
            }
            
            for(int i = 0; i < 4; i++){
                float f1 = (float) snode.getDouble(6+i, index[2]);
                if(Math.abs(label[i]-f1)>0.0001) label[i] = 0.0f;
            }
            return label;
        }
        
        public float[] getLabel3(){
            float[] label = new float[3];
            for(int i = 0; i < 3; i++) label[i] = (float) snode.getDouble(6+i, index[0]);
            
            for(int i = 0; i < 3; i++){
                float f1 = (float) snode.getDouble(6+i, index[1]);
                if(Math.abs(label[i]-f1)>0.0001) label[i] = 0.0f;
            }
            
            for(int i = 0; i < 3; i++){
                float f1 = (float) snode.getDouble(6+i, index[2]);
                if(Math.abs(label[i]-f1)>0.0001) label[i] = 0.0f;
            }
            return label;
        }
        
        public int[] getStatus(){
            return new int[]{
                snode.getInt(0, index[0]), 
                snode.getInt(0, index[1]),
                snode.getInt(0, index[2])
            };
        }
        
        public int countStatus(){
            return snode.getInt(0, index[0])+snode.getInt(0, index[1])+snode.getInt(0, index[2]);
        }
        
        @Override
        public String toString(){
            StringBuilder str = new StringBuilder();
            str.append(Arrays.toString(this.getStatus()));
            str.append(Arrays.toString(this.index));
            
            str.append(Arrays.toString(this.getFeatures()));
            str.append(Arrays.toString(this.getLabel()));
            
            return str.toString();
        }
    }
    
    public static class DataLayer {
        public List<Integer> layers = new ArrayList<>();
        public DataLayer(){}        
        public void addIndex(int index){layers.add(index);}        
        @Override
        public String toString(){
            StringBuilder str = new StringBuilder();
            for(int i = 0; i < layers.size();i++) str.append(String.format("%4d ", layers.get(i)));
            return str.toString();
        }
    };
    
    public static class DataEventSvt {
        
        CompositeNode enode = null;
        List<DataLayer> layers = new ArrayList<>();
        
        public DataEventSvt(CompositeNode node){
            enode = node;
            for(int i = 0; i < 6; i++) layers.add(new DataLayer());
        }
        
        public void analyze(){
            int nrows = enode.getRows();
            for(int i = 0; i < nrows; i++){
                int layer = enode.getInt(1, i);
                layers.get(layer-1).addIndex(i);
            }
        }
        
        public void show(){
            System.out.println(" DATA EVENT ===== ");
            for(int k = 0; k < this.layers.size(); k++){
                System.out.println("\t" + k + "  " + layers.get(k).toString());
            }
        }
        
        public List<DataSegment> getSegments(){
            return getSegments(new int[]{0,1,2});
        }
        
        public List<DataSegment> getSegments(int[] layout){
            
            List<DataSegment> list = new ArrayList<>();
            int n0 = this.layers.get(layout[0]).layers.size();
            int n1 = this.layers.get(layout[1]).layers.size();
            int n2 = this.layers.get(layout[2]).layers.size();
            
            for(int p0 = 0; p0 < n0; p0++){
                for(int p1 = 0; p1 < n1; p1++){
                    for(int p2 = 0; p2 < n2; p2++){
                        DataSegment s = new DataSegment(enode,
                                this.layers.get(layout[0]).layers.get(p0),
                                this.layers.get(layout[1]).layers.get(p1),
                                this.layers.get(layout[2]).layers.get(p2)                                
                        );
                        list.add(s);
                    }
                }
            }
            return list;
        }
    }
    
    public static  DataList loadData(String h5file){
        DataList list = new DataList();
        CompositeNode node = new CompositeNode(17,1,"2s4f4f",2048);
        HipoReader r = new HipoReader(h5file);
        Event event = new Event();
        
        while(r.hasNext()){
            r.next(event);
            event.read(node, 17, 1);
            System.out.println("new event");
            node.print();
            DataEventSvt data = new DataEventSvt(node);
            data.analyze();
            data.show();
            
            List<DataSegment> segments = data.getSegments(new int[]{0,1,2});
            List<DataSegment> segments2 = data.getSegments(new int[]{1,2,3});
            List<DataSegment> segments3 = data.getSegments(new int[]{2,3,4});
            List<DataSegment> segments4 = data.getSegments(new int[]{3,4,5});
            
            segments.addAll(segments2);
            segments.addAll(segments3);
            segments.addAll(segments4);
            for(DataSegment s :segments )
            {
                if(s.countStatus()==3||s.countStatus()==2) System.out.println(s);
            }
        }
        return list;
    }
    
    public static void main(String[] args){
        DataLoader.loadData("cvt_output.h5");
    }
}
