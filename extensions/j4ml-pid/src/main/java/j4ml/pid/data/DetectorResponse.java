/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.pid.data;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DetectorResponse {
    
    double[]  responses = new double[28];
    int          pindex = 0;
    
    public DetectorResponse(){
        
    }
    
    public void setIndex(int pi){pindex = pi;}
    
    public void reset(){
        for(int i = 0; i < responses.length; i++) responses[i] =0.0;
    }
    public double[] getData(){return this.responses; }
    
    
    public double[] read3(Bank calo, Bank calib, int pindex, double pmom){
        List<Integer> idx = getIndex(calo,pindex);
        
        if(idx.size()!=3) return null;
        double[] results = new double[27];
        for(int i = 0; i < results.length; i++) results[i] = 0.0;
        //results[0] = 0.0; results[1] = 0.0; results[2] = 0.0;        

        for(int i = 0; i < idx.size(); i++){            
            results[0+i] = calib.getFloat("recEU", idx.get(i))/pmom;
            results[3+i] = calib.getFloat("recEV", idx.get(i))/pmom;
            results[6+i] = calib.getFloat("recEW", idx.get(i))/pmom;
        }
        
        for(int i = 0; i < idx.size(); i++){            
            results[9+i] = calo.getFloat("lu", idx.get(i));
            results[12+i] = calo.getFloat("lv", idx.get(i));
            results[15+i] = calo.getFloat("lw", idx.get(i));
        }
        
        for(int i = 0; i < idx.size(); i++){            
            results[18+i] = calo.getFloat("m2u", idx.get(i));
            results[21+i] = calo.getFloat("m2v", idx.get(i));
            results[24+i] = calo.getFloat("m2w", idx.get(i));
        }
        return results;
    }
    
    public List<Integer> getIndex(Bank calo, int pindex){
        List<Integer> idx = new ArrayList<>();
        int nrows = calo.getRows();
        for(int i = 0; i < nrows; i++){
            int pi = calo.getInt("pindex", i);
            if(pi==pindex) idx.add(i);
        }
        return idx;
    }
    
    public void read(Bank calo, Bank moments, Bank calib, double pmom){
        
        reset();
        
        double[] rps = new double[28];
        
        rps[0] = pmom;
        
        double mom = pmom/10.0;
        
        int rows = calo.getRows();
        
        for(int i = 0; i < rows; i++){
            int pi = calo.getInt("pindex", i);            
            if(pi==pindex){
                int  layer = calo.getInt("layer", i);
                int offset = 1;
                if(layer==4) offset = 10;
                if(layer==7) offset = 19;
                
                rps[offset + 0] = calib.getFloat("recEU", i)/mom;
                rps[offset + 1] = calib.getFloat("recEV", i)/mom;
                rps[offset + 2] = calib.getFloat("recEW", i)/mom;
                
                rps[offset + 3] = moments.getFloat("distU", i);
                rps[offset + 4] = moments.getFloat("distV", i);
                rps[offset + 5] = moments.getFloat("distW", i);
                
                rps[offset + 6] = moments.getFloat("m2u", i);
                rps[offset + 7] = moments.getFloat("m2v", i);
                rps[offset + 8] = moments.getFloat("m2w", i);
            }
        }
        responses = DataProvider.getNormalizer().normalize(rps);
    }
    
    public float[] features(){
        float[] data = new float[responses.length];
        for(int i = 0; i < data.length; i++) data[i] = (float) responses[i];
        return data;
    }
    
    public double[] featuresDouble(){
        double[] data = new double[responses.length];
        for(int i = 0; i < data.length; i++) data[i] = responses[i];
        return data;
    }
    
    public void read(Bank calo, Bank moments, Bank calib){
        reset();
        int rows = calo.getRows();
        for(int i = 0; i < rows; i++){
            int pi = calo.getInt("pindex", i);            
            if(pi==pindex){
                int  layer = calo.getInt("layer", i);
                int offset = 1;
                if(layer==4) offset = 10;
                if(layer==7) offset = 19;
                responses[offset + 0] = calib.getFloat("recEU", i);
                responses[offset + 1] = calib.getFloat("recEV", i);
                responses[offset + 2] = calib.getFloat("recEW", i);
                
                responses[offset + 3] = moments.getFloat("distU", i);
                responses[offset + 4] = moments.getFloat("distV", i);
                responses[offset + 5] = moments.getFloat("distW", i);
                
                responses[offset + 6] = moments.getFloat("m2u", i);
                responses[offset + 7] = moments.getFloat("m2v", i);
                responses[offset + 8] = moments.getFloat("m2w", i);
            }
        }
    }
    
    public double normalize(double value,double min, double max){
        if(value<min||value>max) return -1000.0;
        return (value-min)/(max-min);
    }
    
    public int count(){
        int count = 0;
        for(int i = 0; i < responses.length; i++) 
            if(responses[i]>0.00001) count++;
        return count;
    }
            
    public String getString(){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < responses.length; i++) str.append(String.format("%8.4f", responses[i]));
        return str.toString();
    }
    
    
    public static void process28(String file){
        HipoReader r = new HipoReader(file);
        Event event = new Event();
        
        Bank  bpart = r.getBank("REC::Particle");
        Bank  bcalo = r.getBank("REC::Calorimeter");
        Bank bcalib = r.getBank("ECAL::calib");
        Bank   bmom = r.getBank("ECAL::moments");
        
        DetectorResponse res = new DetectorResponse();
        res.setIndex(0);
        
        int[][] matrix = new int[2][2];
        
        DataNormalizer normalizer = new DataNormalizer();
        
        for(int i = 0; i < 100000; i++){
            r.nextEvent(event);
            
            event.read(bmom);
            event.read(bpart);
            event.read(bcalo);
            event.read(bcalib);
            
            res.read(bcalo, bmom, bcalib);
            if(bpart.getRows()>0){
                int pid = bpart.getInt("pid", 0);
                int charge = bpart.getInt("charge", 0);
                
                if(charge!=0&&res.count()>25){
                    if(pid==11) matrix[1][1]++;
                    if(pid!=11) matrix[0][1]++;
                    normalizer.probe(res.getData());
                    //System.out.printf("%5d [%3d] : %s\n",pid, res.count(), res.getString());
                }
            } 
        }
        System.out.printf("%d %d \n",matrix[0][1],matrix[1][1]);
        System.out.println(normalizer.suggestion());
    }
    
    
    public static void main(String[] args){
        
        String file = "/Users/gavalian/Work/dataspace/pid/rec_out_electron.hipo";
        DetectorResponse.process28(file);
    }
}
