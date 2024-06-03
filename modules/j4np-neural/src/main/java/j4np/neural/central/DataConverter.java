/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.central;

import j4np.geom.prim.Vector3D;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.utils.io.TextFileReader;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author gavalian
 */
public class DataConverter {
    
    public static class DataPoint {
        
        int         layer = 0;
        int        status = 0;
        float[] features = null;
        float[]   values = null;
        
        public DataPoint(String line){
            features = new float[4];     
            values = new float[4];
            this.fromString(line);
        }
        
        public final void fromString(String line){
            String[] tokens = line.split("\\s+");
            layer = Integer.parseInt(tokens[1]);
            Vector3D v = new Vector3D();
            
            v.setXYZ(tokens[3], tokens[4], tokens[5]);
            
            features[0] = (float) ((v.rho()-60)/80);
            features[1] = (float )((v.phi()+Math.PI)/(2.0*Math.PI));
            
            v.setXYZ(tokens[6], tokens[7], tokens[8]);
            
            features[2] = (float) ((v.rho()-60)/80);
            features[3] = (float )((v.phi()+Math.PI)/(2.0*Math.PI));
            
            double p = Double.parseDouble(tokens[9]);
            double t = Double.parseDouble(tokens[10]);
            double f = Double.parseDouble(tokens[11]);
            double z = Double.parseDouble(tokens[12]);
            
            values[0] = (float) ((p-0.3)/1.3);
            values[1] = (float) (t/Math.PI);
            values[2] = (float) ((f+Math.PI)/(2*Math.PI));
            values[3] = (float) ((z+5.5)/5.0);
            
            if(p>2.0){ values[0] = 0.0f; values[1] = 0.0f; values[2] = 0.0f;values[3]=0.0f;}
            
            int s1 = Integer.parseInt(tokens[13]);
            int s2 = Integer.parseInt(tokens[14]);
            int s3 = Integer.parseInt(tokens[15]);
            if(s1==1&&s3==1) status = 1;
        }
        
        public static float[] combine(DataPoint... p){
            float[] f = new float[p.length*4];
            int counter = 0;
            for(int i = 0; i < p.length; i++){
                for(int k = 0; k < 4; k++){
                    f[counter] = p[i].features[k]; counter++;
                }
            } 
            return f;
        }
        
        public static float[] label(DataPoint... p){
            for(int i = 0; i < p.length; i++) if(p[i].status==0) return new float[]{0.0f,1.0f};
            return new float[]{1.0f,0.0f};
        }
        
        @Override
        public String toString(){
            return String.format("L %3d (%2d) [ %8.5f %8.5f %8.5f %8.5f]", 
                    layer,status,features[0],features[1],features[2],features[3]);
        }
    }
    
    
    public static class DataEvent {
        List< List<DataPoint> > pLayers = new ArrayList<>();
        Random r = new Random();
        public DataEvent(){
           for(int i = 0; i < 6; i++){
               pLayers.add(new ArrayList<>());
           }                       
        }
        
        public void add(DataPoint p){
            int l = p.layer-1;
            pLayers.get(l).add(p);
        }
        
        public void adjust(){
            for(int i = 0; i < pLayers.size();i++){
                while(pLayers.get(i).size()>2){
                    pLayers.get(i).remove(2);
                }
            }
        }
        public void show(){
            for(int i = 0; i < pLayers.size(); i++){
                System.out.println(" LAYER " + i + " size = " + pLayers.get(i).size());
                for(int j = 0; j < pLayers.get(i).size();j++){
                    System.out.println("\t" + pLayers.get(i).get(j));
                }
            }
        }
        public String get(int[] idx, boolean flag){
            int[] k = new int[]{0,0,0};
            
            if(flag==false){
                int which = r.nextInt(3); k[which] = 1;
            }
            float[] features = DataPoint.combine(
                    pLayers.get(idx[0]).get(k[0]),
                    pLayers.get(idx[1]).get(k[1]),
                    pLayers.get(idx[2]).get(k[2])                    
            );
            float[] output = DataPoint.label( 
                    pLayers.get(idx[0]).get(k[0]),
                    pLayers.get(idx[1]).get(k[1]),
                    pLayers.get(idx[2]).get(k[2])
            );
            String out = Arrays.toString(features)+", " + Arrays.toString(pLayers.get(idx[0]).get(0).values) +  ", "+Arrays.toString(output);
            //String out = Arrays.toString(pLayers.get(idx[0]).get(0).values) ;
            return out.replace("[", "").replace("]", "");
        }
        
        public boolean complete(){
            for(int i = 0; i < pLayers.size(); i++) 
                if(pLayers.get(i).size()<2) return false;
            return true;
        }
        
        public CompositeNode getNode(){
            CompositeNode node = new CompositeNode(17,1,"ss4f4f",2048);
            node.setRows(0);
            for(int i = 0; i < 6; i++){
                for(int j = 0; j < this.pLayers.get(i).size(); j++){
                    DataPoint p = this.pLayers.get(i).get(j);
                    int row = node.getRows();
                    node.putShort(0, row, (short) p.status);
                    node.putShort(1, row, (short) p.layer);
                    for(int k = 0; k < 4; k++){ 
                        node.putFloat(k+2, row, p.features[k]);
                        node.putFloat(k+6, row, p.values[k]);
                    }
                    node.setRows(row+1);
                }
            }
            return node;
        }
        public void reset(){ for(int i = 0; i < pLayers.size(); i++) pLayers.get(i).clear(); }
    }
    
    public static void createFile(String input, String output){
        TextFileReader r = new TextFileReader(input);
        HipoWriter w = new HipoWriter();
        w.open(output);
        int run = 0;
        DataEvent de = new DataEvent();
        Event event = new Event();
        int counter = 0;
        //for(int i = 0; i < 125000; i++){
         while(   r.readNext()==true){
             counter++;
            String s = r.getString();
            String[] t = s.split("\\s+");
            try {
            DataPoint p = new DataPoint(s);
            int lrun = Integer.parseInt(t[0]);
            if(lrun!=run){
                
                CompositeNode node = de.getNode();
                event.reset();
                event.write(node);
                w.addEvent(event);
                de.reset();           
                de.add(p);
                run = lrun;   
            } else { de.add(p);}
            
            } catch (Exception e){
                System.out.println("exception at line # " + counter);
            }
        }
        w.close();
    }
    
    
    public static void reduceFile(String h5file){
        HipoReader r = new HipoReader(h5file);
        HipoWriter w = new HipoWriter();
        
        w.open(h5file+"_f.h5");
        
        Event event = new Event();
        CompositeNode node = new CompositeNode(17,1,"ss4f4f",2048);
        CompositeNode nodef = new CompositeNode(17,1,"ss4f4f",2048);
        while(r.hasNext()){
            r.next(event);
            event.read(node);
            List<Integer> trk = new ArrayList<>();
            for(int i = 0; i < node.getRows(); i++){
                if(node.getInt(0, i)==1) trk.add(i);
            }
            List<Integer> rows = new ArrayList<>();
            for(int i = 0; i < trk.size(); i++){
                int index = trk.get(i);
                int layer = node.getInt(1,index);
                double vmin = 1.57;
                int    imin = 0;
                for(int n = 0; n < node.getRows(); n++){
                    if(n!=index&&node.getInt(1, n)==layer){
                        double diff = node.getDouble(3, n)-node.getDouble(3, index);
                        if(diff<vmin){ vmin=diff;imin=n;}
                    }
                }
                rows.add(imin);
            }
            
            //rows.addAll(trk);
            trk.addAll(rows);
            nodef.setRows(trk.size());
            for(int i = 0; i < trk.size(); i++){ nodef.copyRow(node, trk.get(i), i);}
            
            if(nodef.getRows()>0){
                event.reset();            
                event.write(nodef);
                w.addEvent(event);
            }
            //System.out.println("*******************");
            //node.print();
            //System.out.println(">>>>>>>");
            //nodef.print();
        }
        w.close();
    }
    public static void main(String[] args){
        
        
        //DataConverter.createFile("munosecSVTVz.txt", "cvt_output.h5");
        DataConverter.reduceFile("cvt_output.h5");
        
        
        /*
        TextFileReader r = new TextFileReader("munosecSVT1.txt");
        TextFileWriter w = new TextFileWriter();
        w.open("central.csv");
        
        int run = 0;
        DataEvent de = new DataEvent();
        
        for(int i = 0; i < 125000; i++){
            r.readNext();
            String s = r.getString();
            String[] t = s.split("\\s+");
            DataPoint p = new DataPoint(s);
            int lrun = Integer.parseInt(t[0]);
            if(lrun!=run){

                System.out.println("---- completed event ----");
                de.adjust();
                de.show();
                if(run!=0&&de.complete()){
                    //String datat = de.get(new int[]{0,1,2}, true);
                    //String dataf = de.get(new int[]{0,1,2}, false);
                    String datat = de.get(new int[]{0,1,4}, true);
                    String dataf = de.get(new int[]{0,1,4}, false);
                    System.out.println(datat);
                    System.out.println(dataf);
                    w.writeString(datat);
                    w.writeString(dataf);
                    
                    //String datat2 = de.get(new int[]{1,2,3}, true);
                    //String dataf2 = de.get(new int[]{1,2,3}, false);
                    String datat2 = de.get(new int[]{1,4,5}, true);
                    String dataf2 = de.get(new int[]{1,4,5}, false);
                    w.writeString(datat2);
                    w.writeString(dataf2);
                    /*
                    String datat3 = de.get(new int[]{2,3,4}, true);
                    String dataf3 = de.get(new int[]{2,3,4}, false);
                    w.writeString(datat3);
                    w.writeString(dataf3);
                    
                    String datat4 = de.get(new int[]{3,4,5}, true);
                    String dataf4 = de.get(new int[]{3,4,5}, false);
                    w.writeString(datat4);
                    w.writeString(dataf4);
                    
                }
                
                de.reset();                
                de.add(p);
                run = lrun;
            } else { de.add(p);}
            
        }
        w.close();*/
    }
}
