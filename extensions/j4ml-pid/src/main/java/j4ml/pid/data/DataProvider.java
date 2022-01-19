/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.pid.data;

import deepnetts.data.TabularDataSet;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.utils.io.DataArrayUtils;
import j4np.utils.io.DataPair;
import j4np.utils.io.DataPairList;
import javax.visrec.ml.data.DataSet;

/**
 *
 * @author gavalian
 */
public class DataProvider {
    
    double[] dataMin = new double[] { 0.0,
        0.00,     0.00,     0.00,     0.00,     0.00,     0.00,   0.0,    0.0,     0.0,
        0.00,     0.00,     0.00,     0.00,     0.00,     0.00,   0.0,    0.0,     0.0,
        0.00,     0.00,     0.00,     0.00,     0.00,     0.00,   0.0,    0.0,     0.0
            
    };
    double[] dataMax = new double[] { 10.0,
        0.75,     0.75,     0.75,   450.0,   450.0,   450.0,   330.0,   330.0,   330.0,
        0.75,     0.75,     0.75,   450.0,   450.0,   450.0,   500.0,   500.0,   500.0,
        0.75,     0.75,     0.75,   450.0,   450.0,   450.0,   620.0,   620.0,   620.0
            
    };

    public static double[] gDataMin = new double[] { 0.0,
        0.00,     0.00,     0.00,     0.00,     0.00,     0.00,   0.0,    0.0,     0.0,
        0.00,     0.00,     0.00,     0.00,     0.00,     0.00,   0.0,    0.0,     0.0,
        0.00,     0.00,     0.00,     0.00,     0.00,     0.00,   0.0,    0.0,     0.0
            
    };
    public static double[] gDataMax = new double[] { 10.0,
        0.75,     0.75,     0.75,   450.0,   450.0,   450.0,   330.0,   330.0,   330.0,
        0.75,     0.75,     0.75,   450.0,   450.0,   450.0,   500.0,   500.0,   500.0,
        0.75,     0.75,     0.75,   450.0,   450.0,   450.0,   620.0,   620.0,   620.0
            
    };
    
    public static DataNormalizer gNormalizer = new DataNormalizer(gDataMin,gDataMax);
    
    public static DataNormalizer getNormalizer(){ return gNormalizer;}
    
    public DataPairList loadData(String file, int label){
        
        DataPairList list = new DataPairList();
        HipoReader r = new HipoReader(file);
        Event event = new Event();
        
        Bank  bpart = r.getBank("REC::Particle");
        Bank  bcalo = r.getBank("REC::Calorimeter");
        Bank bcalib = r.getBank("ECAL::calib");
        Bank   bmom = r.getBank("ECAL::moments");
        
        DetectorResponse res = new DetectorResponse();
        res.setIndex(0);
        
        DataNormalizer normalizer = new DataNormalizer(dataMin,dataMax);
        
        
        for(int i = 0; i < 100000; i++){
            r.nextEvent(event);
            
            event.read(bmom);
            event.read(bpart);
            event.read(bcalo);
            event.read(bcalib);
            
            
            if(bpart.getRows()>0){
                int pid = bpart.getInt("pid", 0);
                int charge = bpart.getInt("charge", 0);
                double px = bpart.getFloat("px", 0);
                double py = bpart.getFloat("py", 0);
                double pz = bpart.getFloat("pz", 0);
                
                double p = Math.sqrt(px*px+py*py+pz*pz);
                res.read(bcalo, bmom, bcalib,p);
                
                if(charge!=0&&res.count()>25){
                    
                    
                    res.read(bcalo, bmom, bcalib,p);

                    //System.out.printf("%5d [%3d] : %s\n",pid, res.count(), res.getString());
                    double[]   data = res.getData(); 
                    //data[0] = Math.sqrt(px*px+py*py+pz*pz);


                    double[]  input = res.featuresDouble();//normalizer.normalize(data);
                    double[] output = new double[]{0.0,0.0};
                    output[label] = 1.0;
                    if(normalizer.isValid(input)){
                        list.add(new DataPair(input,output));
                    }
                }
            } 
        }
        return list;
    }
    
    public DataPairList loadData19(String file, int label){
        
        DataPairList list = new DataPairList();
        HipoReader r = new HipoReader(file);
        Event event = new Event();
        
        Bank  bpart = r.getBank("REC::Particle");
        Bank  bcalo = r.getBank("REC::Calorimeter");
        Bank bcalib = r.getBank("ECAL::calib");
        Bank   bmom = r.getBank("ECAL::moments");
        
        DetectorResponse res = new DetectorResponse();
        res.setIndex(0);
        
        DataNormalizer normalizer = new DataNormalizer(dataMin,dataMax);
        
        
        for(int i = 0; i < 100000; i++){
            r.nextEvent(event);
            
            event.read(bmom);
            event.read(bpart);
            event.read(bcalo);
            event.read(bcalib);
            
            
            if(bpart.getRows()>0){
                int pid = bpart.getInt("pid", 0);
                int charge = bpart.getInt("charge", 0);
                double px = bpart.getFloat("px", 0);
                double py = bpart.getFloat("py", 0);
                double pz = bpart.getFloat("pz", 0);
                
                double p = Math.sqrt(px*px+py*py+pz*pz);
                res.read(bcalo, bmom, bcalib,p);
                
                if(charge!=0&&res.count()<25&&res.count()>15){
                    
                    
                    res.read(bcalo, bmom, bcalib,p);

                    //System.out.printf("%5d [%3d] : %s\n",pid, res.count(), res.getString());
                    double[]   data = res.getData(); 
                    //data[0] = Math.sqrt(px*px+py*py+pz*pz);


                    double[]  input = res.featuresDouble();//normalizer.normalize(data);
                    double[] output = new double[]{0.0,0.0};
                    output[label] = 1.0;
                    if(normalizer.isValid(input)){
                        list.add(new DataPair(input,output));
                    }
                }
            } 
        }
        return list;
    }
    public static String[] generateNames(int input, int output){
        String[] names = new String[input+output];
        for(int i = 0; i < input; i++) names[i] = "in" + i;
        for(int i = 0; i < output; i++) names[i+input] = "out" + i;        
        return names;
    }
    
    public DataSet convert(DataPairList list){
        TabularDataSet  dataset = new TabularDataSet(28,2);
        
        for(int i = 0; i < list.getList().size(); i++){
            float[]  input = DataArrayUtils.toFloat(list.getList().get(i).getFirst());
            float[] output = DataArrayUtils.toFloat(list.getList().get(i).getSecond());
            dataset.add(new TabularDataSet.Item(input, output));
        }
        String[] columns = DataProvider.generateNames(28,2);
        dataset.setColumnNames(columns);
        
        System.out.printf("INPUT/OUTPUT = %5d/%5d\n",
                dataset.getNumInputs(),dataset.getNumOutputs());
        return dataset;
    }
}
