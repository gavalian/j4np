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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class CVTProcessor {
    
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
            int  sensor = getSensor(sector,layer)+1;
            if(smap.contains(sensor)==true) index.add(i);
        }
        Bank res = bst.reduce(index);
        return res;
    }
    
    public float[] getInput(int[][] buffer){
        float[] result = new float[87*256];

        int counter = 0;        
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
    
    public void process(String file, String network){
        
        
        try {
            ConvolutionalNetwork nnet = null;
            
            nnet = FileIO.createFromFile(network, ConvolutionalNetwork.class);
            
            
            HipoReader r = new HipoReader(file);
            HipoWriter w = new HipoWriter();
            w.getSchemaFactory().copy(r.getSchemaFactory());
            w.open("output_from_ai.hipo");
            Event event = new Event();
            Bank  bstHits = r.getBank("BSTRec::Hits");
            Bank    bmtCt = r.getBank("BMTRec::Clusters");
            Bank   bstADC = r.getBank("BST::adc");
            
            while(r.hasNext()){
                
                r.nextEvent(event);
                event.read(bmtCt);
                event.read(bstHits);
                event.read(bstADC);
                
                int[][]  map = new int[87][256];
                
                this.fillBMT(   bmtCt, map, false);
                this.fillBST( bstHits, map, false);
                
                float[]  input = this.getInput(map); 
                nnet.setInput(new Tensor(input));
                float[] output = nnet.getOutput();
                Set<Integer>  sensors = this.sensorMap(output, 0.01);
                
                /*System.out.println("***************************************************");
                for(Integer item : sensors){
                    System.out.printf("%d , ",item);
                }
                System.out.println();
                */
                Bank adc = this.skimBank(bstADC, sensors);
                //bstADC.show();
                //adc.show();
                event.remove(bmtCt.getSchema());
                event.remove(bstHits.getSchema());
                event.remove(bstADC.getSchema());
                event.write(adc);
                w.add(event);
            }
            
            w.close();
        } catch (IOException ex) {
            Logger.getLogger(CVTProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CVTProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/DataSpace/cvt/proton_95nA_20k.h5";
        String network = "cnn_logreg_retrained.nnet";
        
        CVTProcessor p = new CVTProcessor();
        p.process(file, network);
    }
}
