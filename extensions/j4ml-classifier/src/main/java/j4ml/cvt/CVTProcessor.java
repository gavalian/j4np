/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.cvt;

import deepnetts.net.ConvolutionalNetwork;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class CVTProcessor {
    
    public double  threshold = 0.5;
    public String     output = "output_cvt_ai.hipo";
    
    public void fillBMT(Bank b, int[][] a, boolean select){
        int nrows = b.getRows();
        
        for(int i = 0; i < nrows;i++){
            int tid = b.getInt("trkID", i);
            int layer = b.getInt("layer", i);
            boolean write = true;
            
            if(select==true&&tid!=1) write = false;
            
            if(write&&(layer==2||layer==3||layer==5)){
              double xc = b.getFloat("x1",i);
              double yc = b.getFloat("y1",i);
              double phi = Math.atan2(yc, xc);
              double fideg = Math.toDegrees(phi);
              int coord = (int) (256*((fideg+180)/360));
              //System.out.printf(" fi angle = %f  <=> %d\n",Math.toDegrees(phi), coord);
              if(layer==2) a[84][coord] = 1;
              if(layer==3) a[85][coord] = 1;
              if(layer==5) a[86][coord] = 1;
            }
        }
    }
    
    public void fillBST(Bank b, int[][] a, boolean select){
        int nrows = b.getRows();
        
        for(int i = 0; i < nrows;i++){
            int tid = b.getInt("trkID", i);
            boolean write = true;
            
            if(select==true&&tid!=1) write = false;
            
            if(write){
                int layer = b.getInt("layer", i);
                int sector = b.getInt("sector", i);
                int strip = b.getInt("strip", i);
                if(strip>=1){
                    if(layer==1||layer==2){
                        int coordx = strip-1;
                        int coordy = (sector-1)+(layer-1)*10;
                        a[coordy][coordx] = 1;
                    }
                    if(layer==3||layer==4){
                        int coordx = strip-1;
                        int coordy = 20 + (sector-1)+(layer-3)*14;
                        a[coordy][coordx] = 1;
                    }
                    if(layer==5||layer==6){
                        int coordx = strip-1;
                        int coordy = 48 + (sector-1)+(layer-5)*18;
                        a[coordy][coordx] = 1;
                    }
                }
            }
        }
    }    
    
    public int getSensor(int sector, int layer){
        if(layer==1||layer==2)
            return (sector-1)+(layer-1)*10;       
        if(layer==3||layer==4)
            return 20 + (sector-1)+(layer-3)*14;        
        return 48 + (sector-1)+(layer-5)*18;    
    }
    
    
    public Bank skimBank(Bank bst, Set<Integer> smap){
        List<Integer> index = new ArrayList<>();
        int nrows = bst.getRows();
        for(int i = 0; i < nrows; i++){
            int  sector = bst.getInt("sector", i);
            int   layer = bst.getInt("layer",i);
            int  sensor = CvtArrayIO.getSensor(sector, layer)+1;//getSensor(sector,layer)+1;
            if(smap.contains(sensor)==true) index.add(i);
        }
        Bank res = bst.reduce(index);
        return res;
    }
    
    public float[] getInput(int[][] buffer){
        float[] result = new float[87*256];
        int    counter = 0;        
        for(int x = 0; x < 87; x++){
           for(int y = 0; y < 256; y++){
               if(buffer[x][y]>0) {
                   result[counter] = 1.0f;
               } else {
                   result[counter] = 0.0f;
               }
               counter++;
           } 
        }                
        return result;
    }
    
    public Set<Integer> sensorMap(float[] output, double threshold){
        Set<Integer> sensors = new HashSet<>();
        for(int i = 0; i < output.length; i++)
            if(output[i]>threshold) sensors.add(i+1);
        return sensors;
    }
    
    public int count(Bank b){
        int c = 0;
        for(int k = 0; k < b.getRows();k++)
            if(b.getInt("order", k)==0) c++;
        return c;
    }
    
    public void process(String file, String network){
        
        int counter = 0;
        try {
            ConvolutionalNetwork nnet = null;
            
            nnet = FileIO.createFromFile(network, ConvolutionalNetwork.class);
                        
            HipoReader r = new HipoReader(file);
            HipoWriter w = new HipoWriter();
            w.getSchemaFactory().copy(r.getSchemaFactory());
            w.open(this.output);
            Event event = new Event();
            Bank  bstHits = r.getBank("BSTRec::Hits");
            Bank    bmtCt = r.getBank("BMTRec::Clusters");
            Bank   bstADC = r.getBank("BST::adc");
            double  counterNoise = 0.0;
            int    counterSignal = 0;
            double  counterAdditional = 0.0;
            double  counterHits = 0.0;
            double  counterHitsDenoised = 0.0;
            
            r.setProgressPrint(false);
            while(r.hasNext()){
                
                r.nextEvent(event);
                event.read(bmtCt);
                event.read(bstHits);
                event.read(bstADC);
                
                //int[][]  map = new int[87][256];
                
                //this.fillBMT(   bmtCt, map, false);
                //this.fillBST( bstHits, map, false);
                //CvtArray2D m = new CvtArray2D();
                CvtArray2D m = CvtArrayIO.createArray(bstHits, bmtCt, -1);
                
                float[]  input = m.getInput();
                nnet.setInput(new Tensor(input));
                float[] output = nnet.getOutput();
                Set<Integer>  sensors = this.sensorMap(output, this.threshold);
                //System.out.println(Arrays.toString(output));
                /*System.out.println("***************************************************");
                for(Integer item : sensors){
                    System.out.printf("%d , ",item);
                }
                System.out.println();
                */
                Bank adc = this.skimBank(bstADC, sensors);
                //bstADC.show();
                //adc.show();
                counterHits += bstADC.getRows();
                counterHitsDenoised += adc.getRows();
                
                double noise = ((double) adc.getRows())/(bstADC.getRows());
                double signal = ((double) count(adc))/count(bstADC);
                
                counterNoise += noise;
                if(signal>0.8) counterSignal++;
                //System.out.println(">>> " + bstADC.getRows() + " " + adc.getRows()
                 //+ " order = " + count(bstADC) + " " + count(adc) 
                 //       + " >>> sensors : " + Arrays.toString(sensors.toArray()));
                //System.out.printf("%8.4f %8.4f %8d %8d  %8d %8d \n",noise, 
                //        signal,adc.getRows(),bstADC.getRows(),count(bstADC),count(adc));
                event.remove(bmtCt.getSchema());
                event.remove(bstHits.getSchema());
                event.remove(bstADC.getSchema());
                
                event.write(adc);

                w.add(event);
                counter++;
                
            }
            System.out.printf("[sumary] >>>> %8.4f noise = %9.5f signal = %9.5f %9.5f %9.5f\n",
                    this.threshold,counterNoise/counter,
                   ( (double) counterSignal )/counter, 
                   counterHits/counter,counterHitsDenoised/counter);
            
            w.close();
        } catch (IOException ex) {
            Logger.getLogger(CVTProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CVTProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void reduceFile(String file, boolean reduceBank, boolean reduceBmt){
        HipoReader r = new HipoReader(file);
        HipoWriter w = new HipoWriter();
        
        w.getSchemaFactory().copy(r.getSchemaFactory());
        
        w.open("data_reduced.hipo");
        
        Bank   b = r.getBank("BST::adc");
        Bank  b2 = r.getBank("BMT::adc");
        
        Event e = new Event();
        System.out.println("\n\n\n");
        System.out.println("REDUCE BMT : " + reduceBmt);
        System.out.println("REDUCE BST : " + reduceBank);
        
        while(r.hasNext()==true){
            r.nextEvent(e);
            e.read(b);
            e.read(b2);
            
            Set<Integer> set = new HashSet();
            List<Integer> index = new ArrayList<>();
            List<Integer> index2 = new ArrayList<>();
            
            for(int k = 0; k < b.getRows(); k++) {
                if(b.getInt("order", k)==0){
                    set.add(b.getInt("layer", k));
                    index.add(k);
                }
            }
            
            for(int k = 0; k < b2.getRows(); k++) {
                if(b2.getInt("order", k)==0){
                    //set.add(b2.getInt("layer", k));
                    index2.add(k);
                }
            }
            //System.out.println(set.size());
            if(set.size()==6){
                if(reduceBank==true){
                    Bank nb = b.reduce(index);
                    e.remove(b.getSchema());
                    e.write(nb);
                    
                }
                if(reduceBmt==true){                    
                    Bank nb2 = b2.reduce(index2);
                    e.remove(b2.getSchema());
                    e.write(nb2);
                }
                
                w.addEvent(e);
            }
        }
        w.close();
    }
    
    public static void main(String[] args){
        
        String file = "/Users/gavalian/Work/DataSpace/cvt/proton_50_nA_50k_type_1.hipo.rec.hipo";
        //String file = "/Users/gavalian/Work/DataSpace/cvt/proton_45_nA.h5";
        String network = "cnn_logreg_200.nnet";
        
        CVTProcessor p = new CVTProcessor();
        for(double t = 0.9; t >0.02; t -=0.05){
            p.threshold = t;
            p.process(file, network);
        }
        /*
        String data = "/Users/gavalian/Work/DataSpace/cvt/proton_95nA_20k.hipo";
        CVTProcessor.reduceFile(data);*/
    }
}
