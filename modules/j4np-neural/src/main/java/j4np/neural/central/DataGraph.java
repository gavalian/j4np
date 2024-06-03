/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.central;

import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.List;
import twig.data.GraphErrors;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class DataGraph {
    
    public static class DataChain {
        
        List<Integer> chain = new ArrayList<>();
        
        public DataChain() {}
        
        public DataChain copy(){ 
            DataChain nc = new DataChain();
            for(Integer v : chain) nc.chain.add(v);
            return nc;
        }
        
        public List<DataChain> copy(int size){
            List<DataChain> c = new ArrayList<>();
            for(int i = 0; i < size ; i++) c.add(this.copy());
            return c;
        }
        
        public void add(int index){
            this.chain.add(index);
        }
        
        public String getRowFeatures(CompositeNode node, int row){
            StringBuilder str = new StringBuilder();
            for(int i = 0; i < 4; i++){str.append(String.format("%9.5f,",node.getDouble(i+2, row)));}
            return str.toString();
        }
        
        public String getFeaturesString(int[] index, CompositeNode node){
           StringBuilder str = new StringBuilder();
           for(int i = 0; i < index.length; i++){
               str.append(getRowFeatures(node,chain.get(index[i])));
           }
           return str.toString();
        }
        
        public String getLabelString(int[] index, CompositeNode node){
            int counter = 0;
           for(int i = 0; i < index.length; i++){
               if(node.getInt(0,chain.get(index[i]))>0) counter++;
           }
           if(counter==index.length) return "1,0";
           return "0,1";
        }
        
        public static List<DataChain> filter(List<DataChain> chain, CompositeNode node, int count){
            List<DataChain> result = new ArrayList<>();
            for(DataChain c : chain) if(c.getCount(node)==count) result.add(c);
            return result;
        }
        
        public static List<DataChain> empty(int size){
            List<DataChain> c = new ArrayList<>();
            for(int i = 0; i < size; i++) c.add(new DataChain());
            return c;
        }
        
        public static void show(List<DataChain> cl){
            System.out.println("COLLECTION # " + cl.size());
            for(DataChain dc : cl) System.out.println(dc);
        }
        
        public static void show(List<DataChain> cl, CompositeNode node){
            System.out.println("COLLECTION # " + cl.size());
            for(DataChain dc : cl) System.out.printf("%d : %s\n", dc.getCount(node), dc);
        }
        
        protected double phiDiff(CompositeNode node, int i1, int i2){
            double phi1 = 57.29*2.0*Math.PI*node.getDouble(3, i1);
            double phi2 = 57.29*2.0*Math.PI*node.getDouble(3, i2);
            return Math.abs(phi2-phi1);
        }
        
        public int getCount(CompositeNode node){
            int count = 0; 
            for(int i = 0; i < this.chain.size(); i++){
                if(node.getInt(0, chain.get(i))>0) count ++;
            }
            return count;
        }
        
        public boolean isValid(CompositeNode node){
            double threshold = 10.0;
            if(this.chain.size()>=2){
                if(this.phiDiff(node, chain.get(0), chain.get(1))>threshold) return false;
            } 
            /*if(this.chain.size()>=3){
                if(this.phiDiff(node, chain.get(1), chain.get(2))>threshold) return false;
            }*/
            if(this.chain.size()>=4){
                if(this.phiDiff(node, chain.get(2), chain.get(3))>threshold) return false;
            }
            /*if(this.chain.size()>=5){
                if(this.phiDiff(node, chain.get(3), chain.get(4))>threshold) return false;
            }*/
            if(this.chain.size()>=6){
                if(this.phiDiff(node, chain.get(4), chain.get(5))>threshold) return false;
            }
            return true;
        }
        @Override
        public String toString(){
            StringBuilder str = new StringBuilder();
            str.append("chain [");
            for(int i = 0; i < chain.size(); i++) str.append(String.format(" %5d ", chain.get(i)));
            str.append("]");
            return str.toString();
        }
    }
    
    public static List<Integer> getLayers(CompositeNode n, int layer){
        List<Integer> iter = new ArrayList<>();
        for(int i = 0; i < n.getRows(); i++){ if(n.getInt(1, i)==layer) iter.add(i); }
        return iter;
    }
    
    
    public static List<DataChain> filter(List<DataChain> list, CompositeNode node){
        List<DataChain> result = new ArrayList<>();
        for(DataChain c : list) if(c.isValid(node)==true) result.add(c);
        return result;
    }
    
    public static List<DataChain> addLayer(List<DataChain> chain, CompositeNode node, int layer){
        List<DataChain>  result = new ArrayList<>();
        List<Integer>      iter = DataGraph.getLayers(node, layer);
        for(int j = 0; j < chain.size(); j++){
            List<DataChain> ext = chain.get(j).copy(iter.size());
            for(int i = 0; i < ext.size(); i++) ext.get(i).add(iter.get(i));
            result.addAll(ext);
        }
        return result;
    }
    
    public static List<GraphErrors> createGraphs(List<DataChain> chain, CompositeNode node){
        List<GraphErrors> graphs = new ArrayList<>();
        for(DataChain c: chain){
            GraphErrors g = new GraphErrors();
            for(int i = 0; i < c.chain.size(); i++){
                double rho = node.getDouble(2, c.chain.get(i));
                double phi = 2.*Math.PI*node.getDouble(3, c.chain.get(i))-Math.PI;
                g.addPoint(rho*Math.cos(phi),rho*Math.sin(phi));
            }
            g.attr().set("lc=5,mc=2,lw=1");

            int count = c.getCount(node);
            if(count==c.chain.size()){
                g.attr().set("lc=2,lw=2,mc=3");
                graphs.add(0, g);
            } else {
                graphs.add(g);
            }
        }
        
        GraphErrors g = graphs.get(0);
        graphs.remove(0); graphs.add(g);
        return graphs;
    }
    
    public static List<DataChain> createChain(CompositeNode node){
        List<DataChain> result = new ArrayList<>();
        
        List<Integer> iter = DataGraph.getLayers(node, 1);
        List<DataChain> chain = DataChain.empty(iter.size());
        
        for(int i = 0; i < iter.size(); i++) chain.get(i).add(iter.get(i));
        
        for(int i = 2; i <= 6; i++){
            chain = DataGraph.addLayer(chain, node, i);
        }
        
        /*
        iter = DataGraph.getLayers(node, 2);
        
        
        System.out.println(" SIZE = " + chain.size() + " iter size = " + iter.size());
        DataChain.show(chain);
        
        for(int j = 0; j < chain.size(); j++){
            List<DataChain> ext = chain.get(j).copy(iter.size());
            for(int i = 0; i < ext.size(); i++) ext.get(i).add(iter.get(i));
            result.addAll(ext);
        }
        */
        //for(int i = 0; i < iter.size(); i++){
        //    List<DataChain> ext = chain.get(i).copy();
        //    for(int j = 0; j < iter.size(); j++)
        //        for(int e = 0 ; e < ext.size(); e++)
        //            ext.get(e).add(iter.get(j));            
        //    result.addAll(ext);
        //}
        result.addAll(chain);
        //DataChain.show(result);
        
        result = DataGraph.filter(result, node);
        //System.out.println(" after filter " + result.size());
        
        //DataChain.show(result);
        return result;
    }
    
    public static void crateSample(String h5file){
        HipoReader r = new HipoReader("cvt_output.h5");
        Event event = new Event();
        CompositeNode node = new CompositeNode(17,1,"2s4f4f",2048);
        TextFileWriter w = new TextFileWriter("cvt_tr.csv");
        while(r.hasNext()){
            r.next(event);        
            event.read(node,17,1);
            List<DataChain> chain = DataGraph.createChain(node);
            List<DataChain> track = DataChain.filter(chain, node, 6);
            List<DataChain> noise = DataChain.filter(chain, node, 4);
            
            //System.out.println(" track size " + track.size() + " noise = " + noise.size());
            
            List<int[]> index = new ArrayList<>();
            
            index.add(new int[]{0,1,2,3});
            index.add(new int[]{0,1,4,5});
            index.add(new int[]{2,3,4,5});
            if(track.size()>0&&noise.size()>0){
                for(int i = 0; i < index.size();i++){
                    String f = track.get(0).getFeaturesString(index.get(i), node);
                    String l = track.get(0).getLabelString(index.get(i), node);
                    //System.out.println(f+l);
                    w.writeString(f+l);
                    String fn = noise.get(0).getFeaturesString(index.get(i), node);
                    String ln = noise.get(0).getLabelString(index.get(i), node);
                    if(ln.contains("0,1")){
                        //System.out.println(fn+ln);
                        w.writeString(fn+ln);
                    }
                }
            }
        }
        w.close();
    }
    public static void main(String[] args){
        //DataGraph.crateSample("cvt_output.h5");
        
        DataChain c = new DataChain();
        c.add(0);c.add(1);c.add(2);
        
        System.out.println(c);
        HipoReader r = new HipoReader("cvt_output.h5");
        Event event = new Event();
        CompositeNode node = new CompositeNode(17,1,"2s4f4f",2048);
        
        r.getEvent(event, 53);
        
        event.read(node,17,1);
        
        node.print();
        
        List<DataChain> chain = DataGraph.createChain(node);
        DataChain.show(chain, node);
        
        
        
        List<DataChain> track = DataChain.filter(chain, node, 6);
        List<DataChain> noise = DataChain.filter(chain, node, 4);
        
        System.out.println(" track size " + track.size() + " noise = " + noise.size());
        
        List<int[]> index = new ArrayList<>();
        
        index.add(new int[]{0,1,2,3});
        index.add(new int[]{0,1,4,5});
        index.add(new int[]{2,3,4,5});
        
        for(int i = 0; i < index.size();i++){
            String f = track.get(0).getFeaturesString(index.get(i), node);
            String l = track.get(0).getLabelString(index.get(i), node);
            System.out.println(f+l);
            String fn = noise.get(0).getFeaturesString(index.get(i), node);
            String ln = noise.get(0).getLabelString(index.get(i), node);
            if(ln.contains("0,1")){
                System.out.println(fn+ln);
            }
        }
        
        List<GraphErrors> gr = DataGraph.createGraphs(chain, node);
        
        TGCanvas c2 = new TGCanvas();
        for(GraphErrors g : gr) c2.draw(g, "APLsame");
        c2.region().axisLimitsX(-1, 1).axisLimitsY(-1, 1);
        
        
        /*
        List<Integer> iter = DataGraph.getLayers(node, 1);
        List<DataChain> chain = DataChain.empty(iter.size());
        
        for(int i = 0; i < iter.size(); i++) chain.get(i).add(iter.get(i));
        
        DataChain.show(chain);        
        
        for(int l = 1; l <= 6; l++){
            
            
        }*/
        
    }
}
