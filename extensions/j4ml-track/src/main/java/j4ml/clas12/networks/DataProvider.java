/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.networks;

import j4ml.clas12.track.TrackConstrain;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.ejml.EJMLModel;
import j4ml.ejml.EJMLModel.ModelType;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.io.HipoReader;
import j4np.physics.Vector3;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import twig.data.H2F;

/**
 *
 * @author gavalian
 */
public class DataProvider {
    
    public static DataList readRegressionPositive(String file, TrackConstrain constrain, int sector, int max){
        DataList list = new DataList();
        for(int i = 21; i<=40; i++){
            DataList subList = DataProvider.readRegressionWithTag(file,constrain, sector, i, max);
            list.getList().addAll(subList.getList());
        }
        return list;
    }
    
    public static DataList readRegressionNegative(String file, TrackConstrain constrain, int sector, int max){
        DataList list = new DataList();
        for(int i = 1; i<=20; i++){
            DataList subList = DataProvider.readRegressionWithTag(file,constrain, sector, i, max);
            list.getList().addAll(subList.getList());
        }
        return list;
    }
    
    
    public static DataList readRegressionPositive4(String file, TrackConstrain constrain, int sector, int max){
        DataList list = new DataList();
        for(int i = 21; i<=40; i++){
            DataList subList = DataProvider.readRegressionWithTag4(file,constrain, sector, i, max);
            list.getList().addAll(subList.getList());
        }
        return list;
    }
    
    public static DataList readRegressionNegative4(String file, TrackConstrain constrain, int sector, int max){
        DataList list = new DataList();
        for(int i = 1; i<=20; i++){
            DataList subList = DataProvider.readRegressionWithTag4(file,constrain, sector, i, max);
            list.getList().addAll(subList.getList());
        }
        return list;
    }
    
    public static DataList readRegressionWithTag(String file, TrackConstrain constrain, int sector, int tag, int max){
        DataList list = new DataList();
        
        HipoReader reader = new HipoReader();
        reader.setTags(tag);
        reader.setDebugMode(0);
        reader.open(file);

        Event event = new Event();
        int counter = 0;
        
        while(reader.hasNext()&&counter<max){
            //counter++;                                                                                                                                       
            reader.nextEvent(event);

            Node params = event.read(1001,1);
            Node   chi2 = event.read(1001,2);
            //Node vector = event.read(1001,6);                                                                                                                
            Node vector = event.read(1001,6);
            Node vertex = event.read(1001,7);
            Node  means = event.read(1001,4);
            
            Vector3 vec = new Vector3(vector.getFloat(0),vector.getFloat(1),
                    vector.getFloat(2));
            
            Vector3 vrt = new Vector3(vertex.getFloat(0),vertex.getFloat(1),
                    vertex.getFloat(2));
            
            
            int ps = params.getInt(0);
            
            double rotationDeg = 60*(sector-2);
            vec.rotateZ(-Math.toRadians(rotationDeg));
            
            if(constrain.momentum.contains(vec.mag())
                    &&constrain.vertex.contains(vrt.z())&&
                    constrain.chiSquare.contains(chi2.getFloat(0))&&ps==sector){

                double[]  first = new double[6];
                for(int s = 0; s < 6; s++) first [s] = means.getFloat(s);
                
                double[] second = new double[]{
                    vec.mag(),
                    0.2 - (Math.cos(vec.theta())-0.8),
                    //Math.toDegrees(vec.theta()),
                    
                    Math.toDegrees(vec.phi())
                };
                
                if(constrain.isRegression(vec)==true){
                    list.add(new DataEntry(first,second));
                    counter++;
                }                
                
            }
        }
        return list;
    }
    
    
    
    public static DataList readRegressionWithTag4(String file, TrackConstrain constrain, int sector, int tag, int max){
        DataList list = new DataList();
        
        HipoReader reader = new HipoReader();
        reader.setTags(tag);
        reader.setDebugMode(0);
        reader.open(file);

        Event event = new Event();
        int counter = 0;
        
        while(reader.hasNext()&&counter<max){
            //counter++;                                                                                                                                       
            reader.nextEvent(event);

            Node params = event.read(1001,1);
            Node   chi2 = event.read(1001,2);
            //Node vector = event.read(1001,6);                                                                                                                
            Node vector = event.read(1001,6);
            Node vertex = event.read(1001,7);
            Node  means = event.read(1001,4);
            
            Vector3 vec = new Vector3(vector.getFloat(0),vector.getFloat(1),
                    vector.getFloat(2));
            
            Vector3 vrt = new Vector3(vertex.getFloat(0),vertex.getFloat(1),
                    vertex.getFloat(2));
            int ps = params.getInt(0);
            
            if(constrain.momentum.contains(vec.mag())
                    &&constrain.vertex.contains(vrt.z())&&
                    constrain.chiSquare.contains(chi2.getFloat(0))&&ps==sector){

                double[]  first = new double[6];
                for(int s = 0; s < 6; s++) first [s] = means.getFloat(s);
                
                double[] second = new double[]{
                    vec.mag(),
                    Math.toDegrees(vec.theta()),
                    Math.toDegrees(vec.phi()),
                    vrt.z()
                };
                if(constrain.isRegression(vec)==true){
                    list.add(new DataEntry(first,second));
                }
                
                counter++;
            }
        }
        return list;
    }
    
    public static DataList readFixerData(String file, TrackConstrain constrain, int max){
        DataList list = new DataList();
        for(int i = 1; i <= 40; i++){
            DataList  dsn = DataProvider.readFixerDataWithTag(file, constrain, i, max);
            list.getList().addAll(dsn.getList());
        }
        return   list;
    }
    
    public static H2F getTheta(String file,  int tag){
        return DataProvider.getTheta(file, new TrackConstrain(), tag);
    }
    
    public static H2F getTheta(String file, TrackConstrain constrain, int tag){
        H2F h = new H2F("hTheta",40,0.0,10.0,50,0.0,45.0);
        HipoReader reader = new HipoReader();
        reader.setTags(tag);
        reader.setDebugMode(0);
        reader.open(file);

        Event event = new Event();
        int counter = 0;
        
        while(reader.hasNext()){
            //counter++;                                                                                                                                       
            reader.nextEvent(event);


            Node params = event.read(1001,1);
            Node   chi2 = event.read(1001,2);
            //Node vector = event.read(1001,6);                                                                                                                
            Node vector = event.read(1001,6);
            Node vertex = event.read(1001,7);
            Node  means = event.read(1001,4);
            
            Vector3 vec = new Vector3(vector.getFloat(0),vector.getFloat(1),
                    vector.getFloat(2));
            
            Vector3 vrt = new Vector3(vertex.getFloat(0),vertex.getFloat(1),
                    vertex.getFloat(2));
            if(constrain.momentum.contains(vec.mag())
                    &&constrain.vertex.contains(vrt.z())&&
                    constrain.chiSquare.contains(chi2.getFloat(0))){
                h.fill(vec.mag(), vec.theta()*57.29);
                counter++;
            }
        }
        return h;
    }
    
    public static DataList readFixerDataWithTag(String file, TrackConstrain constrain, int tag, int max){
        DataList list = new DataList();
        
        HipoReader reader = new HipoReader();
        reader.setTags(tag);
        reader.setDebugMode(0);
        reader.open(file);

        Event event = new Event();
        int counter = 0;
        
        while(reader.hasNext()&&counter<max){
            //counter++;                                                                                                                                       
            reader.nextEvent(event);


            Node params = event.read(1001,1);
            Node   chi2 = event.read(1001,2);
            //Node vector = event.read(1001,6);                                                                                                                
            Node vector = event.read(1001,6);
            Node vertex = event.read(1001,7);
            Node  means = event.read(1001,4);
            
            Vector3 vec = new Vector3(vector.getFloat(0),vector.getFloat(1),
                    vector.getFloat(2));
            
            Vector3 vrt = new Vector3(vertex.getFloat(0),vertex.getFloat(1),
                    vertex.getFloat(2));
            if(constrain.momentum.contains(vec.mag())
                    &&constrain.vertex.contains(vrt.z())&&
                    constrain.chiSquare.contains(chi2.getFloat(0))){
                for(int k = 0; k < 6; k++){
                    double[]  first = new double[6];
                    double[] second = new double[6];
                    for(int s = 0; s < 6; s++){
                        first [s] = means.getFloat(s);
                        second[s] = means.getFloat(s);                        
                    }
                    first[k] = 0;
                    list.add(new DataEntry(first,second));
                }
                counter++;
            }
        }
        return list;
    }
    
    private static double[]  applyShift(double[] array, int index, double shift){
        double value = array[index] + shift;
        if(value<0||value>111) value = array[index] - shift;        
        array[index] = value;
        return array;
    }
    
    private static double[]  randomNeighbour(Random r, double[] origin, double min, double max){
       double  howMany = r.nextDouble();
       double[] result = new double[origin.length];
       for(int i = 0; i < result.length; i++) result[i] = origin[i];
       
       if(howMany<0.5){
           double      shift = min + r.nextDouble()*(max-min);
           double        dir = r.nextDouble();
           int    superlayer = r.nextInt(6);
           if(dir<0.5) shift = -shift;           
           result = applyShift(result,superlayer,shift);
       } else {
           for(int k = 0; k < 2; k++){
               double      shift = min + r.nextDouble()*(max-min);
               double        dir = r.nextDouble();
               int    superlayer = r.nextInt(6);
               if(dir<0.5) shift = -shift;           
               result = applyShift(result,superlayer,shift);
           }
       }
       return result;
    }
        
    public static DataList readClassifierData(String file, TrackConstrain constrain, int max){
        DataList list = new DataList();
        for(int i = 1; i <= 40; i++){
            DataList  dsn = DataProvider.readClassifierDataWithTag(file, constrain, i, max);
            list.getList().addAll(dsn.getList());
        }
        return   list;
    }
    
    
    public static DataList readClassifierEvent(Event event, TrackConstrain constrain){

        DataList list = new DataList();
        
        Node c = event.read(2001,1);
        Node m = event.read(2001,2);
        
        Combinatorics comb = new Combinatorics();
        
        Node params = event.read(1001,1);
        Node   chi2 = event.read(1001,2);
        //Node vector = event.read(1001,6);                                                                                                                
        Node vector = event.read(1001,6);
        Node vertex = event.read(1001,7);
        Node  means = event.read(1001,4);
        
        
        Vector3 vec = new Vector3(vector.getFloat(0),vector.getFloat(1),
                vector.getFloat(2));
        
        Vector3 vrt = new Vector3(vertex.getFloat(0),vertex.getFloat(1),
                vertex.getFloat(2));
        if(constrain.momentum.contains(vec.mag())==false) return list;
        if(constrain.vertex.contains(vrt.z())==false) return list;
        if(constrain.chiSquare.contains(chi2.getFloat(0))==false) return list;
        
        int charge = params.getInt(1);
        float[]  input = means.getFloat();
        double[] first = new double[input.length];
        for(int i = 0; i < first.length; i++) first[i] = input[i];        
        
        comb.reset();
        int size = c.getDataSize();
        for(int i = 0; i < size; i++){
            comb.add(c.getInt(i)-1, m.getFloat(i));
        }
        List<double[]>  items = comb.getCombinations();
        for(int i = 0; i < items.size(); i++)
            list.add(new DataEntry(items.get(i),new double[]{1.0,0.0,0.0}));
        
       
        
        for(int i = 0; i < list.getList().size(); i++){
            if(Combinatorics.distance(first, list.getList().get(i).getFirst())<0.000001){
                if(charge<0){
                    list.getList().get(i).setOutput(new float[]{0.0f,1.0f,0.0f});                       
                } else {
                    list.getList().get(i).setOutput(new float[]{0.0f,0.0f,1.0f});
                }
            }
        }
        return list;
    }
    
    public static List<DataList> readClassifierDataWithTagEvents(String file,            
            TrackConstrain constrain, int tag, int max){
        List<DataList>  dataList = new ArrayList<>();
         HipoReader reader = new HipoReader();
        reader.setTags(tag);
        reader.setDebugMode(0);
        reader.open(file);

        Event event = new Event();
        int counter = 0;
        while(reader.hasNext()&&counter<max){
            //counter++;                                                                                                                                       
            reader.nextEvent(event);
            DataList list = DataProvider.processEvent(event, constrain);
            dataList.add(list);
        }
        return dataList;        
    }
    
    
    public static DataList readClassifierDataWithTag(String file,            
            TrackConstrain constrain, int tag, int max){
        
        DataList list = new DataList();
        
        HipoReader reader = new HipoReader();
        reader.setTags(tag);
        reader.setDebugMode(0);
        reader.open(file);

        Event event = new Event();
        int counter = 0;
        Random r = new Random();
        
        while(reader.hasNext()&&counter<max){
            //counter++;                                                                                                                                       
            reader.nextEvent(event);
            Node params = event.read(1001,1);
            Node   chi2 = event.read(1001,2);
            //Node vector = event.read(1001,6);                                                                                                                
            Node vector = event.read(1001,6);
            Node vertex = event.read(1001,7);
            Node  means = event.read(1001,4);
            
            Vector3 vec = new Vector3(vector.getFloat(0),vector.getFloat(1),
                    vector.getFloat(2));
            
            Vector3 vrt = new Vector3(vertex.getFloat(0),vertex.getFloat(1),
                    vertex.getFloat(2));
            if(constrain.momentum.contains(vec.mag())
                    &&constrain.vertex.contains(vrt.z())&&
                    constrain.chiSquare.contains(chi2.getFloat(0))){
                    double[]  first = new double[6];

                    for(int s = 0; s < 6; s++){
                        first [s] = means.getFloat(s);
                    }
                    int charge = params.getInt(1);
                    double[] firstFalse = DataProvider.randomNeighbour(r, first, 2.0, 45.0);
                    
                    if(event.scan(3001, 4)>0){
                       //System.out.println("yo, found a false positive");
                       Node  fm = event.read(3001,4);
                       for(int jj = 0; jj < firstFalse.length; jj++) 
                           firstFalse[jj] = fm.getFloat(jj);
                       list.add(new DataEntry( firstFalse,new double[]{1.0,0.0,0.0}));
                    } else {
                        list.add(new DataEntry(firstFalse,new double[]{1.0,0.0,0.0}));
                    }
                    if(charge<0){
                        list.add(new DataEntry(first,new double[]{0.0,1.0,0.0}));
                    } else { list.add(new DataEntry(first,new double[]{0.0,0.0,1.0}));}                    
                    counter++;
            }
        }
        return list;
    }
    
    
    
    public static DataList processEvent(Event event, TrackConstrain constrain){
        
        Node c = event.read(2001,1);
        Node m = event.read(2001,2);
        
        Combinatorics comb = new Combinatorics();
        
        Node params = event.read(1001,1);
        Node   chi2 = event.read(1001,2);
        //Node vector = event.read(1001,6);                                                                                                                
        Node vector = event.read(1001,6);
        Node vertex = event.read(1001,7);
        Node  means = event.read(1001,4);
        
        DataList list = new DataList();
        Vector3 vec = new Vector3(vector.getFloat(0),vector.getFloat(1),
                vector.getFloat(2));
        
        Vector3 vrt = new Vector3(vertex.getFloat(0),vertex.getFloat(1),
                vertex.getFloat(2));
        
        if(constrain.momentum.contains(vec.mag())
                &&constrain.vertex.contains(vrt.z())&&
                constrain.chiSquare.contains(chi2.getFloat(0))){
        
            double[]  first = new double[6];            
            for(int s = 0; s < 6; s++){
                first [s] = means.getFloat(s);
            }
            int charge = params.getInt(1);
            
            comb.reset();
            int size = c.getDataSize();
            for(int i = 0; i < size; i++){
                comb.add(c.getInt(i)-1, m.getFloat(i));
            }
            
            List<double[]>  items = comb.getCombinations();
            for(int i = 0; i < items.size(); i++)
                list.add(new DataEntry(items.get(i),new double[]{1.0,0.0,0.0}));
            
            for(int i = 0; i < list.getList().size(); i++){
                if(Combinatorics.distance(first, list.getList().get(i).getFirst())<0.000001){
                    if(charge<0){
                        list.getList().get(i).setOutput(new float[]{0.0f,1.0f,0.0f});                       
                    } else {
                        list.getList().get(i).setOutput(new float[]{0.0f,0.0f,1.0f});
                    }
                }
            }
        }
        return list;
    }    
    
    public static void main(String[] args){

        String file = "/Users/gavalian/Work/Software/project-10.4/studies/clas12nn/extract_005988.hipo";
    
        TrackConstrain constrain = new TrackConstrain();
        
        HipoReader reader = new HipoReader();
        
        reader.setTags(1);
        reader.setDebugMode(0);
        reader.open(file);

        
        Event event = new Event();
        int counter = 0;
        
        while(reader.hasNext()&&counter<3){
            //counter++;                                                                                        
            reader.nextEvent(event);
            DataList list = DataProvider.processEvent(event, constrain);
            counter++;
            System.out.println("event # " + counter);
            
            list.show();
        }
        
        DataList list2 = DataProvider.readRegressionWithTag(file, constrain, 5, 4, 50);
        list2.show();
    }
}
