/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.classifier.mlp;

import j4ml.classifier.data.DataLoader;
import j4ml.deepnetts.DeepNettsClassifier;
import j4ml.extratrees.networks.ClassifierExtraTrees;
import j4np.utils.io.DataPair;
import j4np.utils.io.DataPairList;
import java.util.List;
import twig.data.GraphErrors;
import twig.data.H1F;
import twig.data.TDirectory;

/**
 *
 * @author gavalian
 */
public class TrackMultiPres {
    
    public static double[] normalized(double[] a){
        double[] b = new double[a.length];
        for(int i = 0; i < b.length; i++) b[i] = a[i]/112.0;
        return b;
    }
    public static DataPairList convert(DataPairList list){
        DataPairList result = new DataPairList();
        for(DataPair item : list.getList()){
            if(item.getSecond()[1]>0.5){                
                result.add(new DataPair(
                        TrackMultiPres.normalized(item.getFirst()),new double[]{0.0,1.0}));
            } else {
                result.add(new DataPair(
                        TrackMultiPres.normalized(item.getFirst())
                        ,new double[]{1.0,0.0}));
            }
        }
        return result;
    }
    
    public static void train(String filename, DeepNettsClassifier network){
        
        DataPairList list = DataLoader.loadPos(filename, 25000);        
        list.shuffle();

        DataPairList[] lists = DataPairList.split(list, 0.7,0.3);
        DataPairList[] listsfull = new DataPairList[2];
        
        listsfull[0] = DataLoader.generateFalse(lists[0]);
        listsfull[1] = DataLoader.generateFalse(lists[1]);
        
        DataPairList[] data = new DataPairList[2];
        
        data[0] = TrackMultiPres.convert(listsfull[0]);
        data[1] = TrackMultiPres.convert(listsfull[1]);

        data[0].show();
        data[1].show();
        
        network.train(data[0], 125);
        network.test(data[1]);
        
    }
    
    public static int getHighestIndex(DataPairList list){
        int index = 0;
        double max = list.getList().get(0).floatSecond()[1];
        for( int i = 0; i < list.getList().size(); i++){
            if(list.getList().get(i).getSecond()[1]>max){
                max   = list.getList().get(i).getSecond()[1];
                index = i;
            }
        }
        return index;
    }
    
    public static int[]  evaluate(String file,DeepNettsClassifier ct,  int tag){
        
        List<DataPairList>  list = DataLoader.loadCombinatoricsPos(file, tag, 15000);
        System.out.println(" LOADED DATA SIZE = " + list.size());
        int[] counter = new int[]{0,0};
        
        for(int i = 0; i < list.size(); i++){
            
            DataPairList item = TrackMultiPres.convert(list.get(i));
            
            if(item.getList().size()>0){
                int trueIndex  = TrackMultiPres.getHighestIndex(item);
                DataPairList r = ct.evaluate(item);
                int  resIndex  = TrackMultiPres.getHighestIndex(r);
                
                //System.out.printf("combinatorics = %5d/%5d index = %5d, infered = %5d\n" ,
                //        r.getList().size(),
                //        item.getList().size(),trueIndex,resIndex);                
                if(resIndex==trueIndex) counter[0]++; else {
                    System.out.println("===== false");
                    item.getList().get(trueIndex).show();
                    r.getList().get(resIndex).show();
                    r.getList().get(trueIndex).show();
                    counter[1]++;
                }
                //r.show();
                //list.get(i).show();
            //System.out.printf(" %5d : %8d %8d\n",i,trueIndex,resIndex);
            }
        }
        System.out.printf(" tag = %5d, %8d %8d  %8.5f\n",tag,counter[0],counter[1], ((double) counter[1])/(counter[0]+counter[1]));
        return counter;
    }
    public static void evaluate(String filename, DeepNettsClassifier network){
        TDirectory dir = new TDirectory();
        H1F  htrue = new H1F( "htrue",20,-0.5,19.5);
        H1F hfalse = new H1F("hfalse",20,-0.5,19.5);
        
        int[] counter = new int[]{0,0};
        
        for(int i = 0; i < 19; i++){
            int[] r = TrackMultiPres.evaluate(filename,network,  i+1);
            counter[0] += r[0];
            counter[1] += r[1];
            for(int k = 0; k < r[0]+r[1] ; k++) htrue.fill(i);
            for(int k = 0; k < r[1] ; k++) hfalse.fill(i);
        }
        H1F hratio = H1F.divide(hfalse, htrue);
        hratio.setName("hratio");
        
        
        dir.add("/classifier/evaluate/mlp", hratio);
        dir.add("/classifier/evaluate/mlp", htrue);
        dir.add("/classifier/evaluate/mlp", hfalse);
        GraphErrors graph = hratio.getGraph(); graph.setName("gratio");
        dir.add("/classifier/evaluate/mlp", graph);
        dir.write("ert_classifier.twig");
    }
    
    public static void main(String[] args){
        String filename = "/Users/gavalian/Work/software/project-10a.0.4/data/classifier/data_extract_classifier_4209_full.hipo";
        DeepNettsClassifier cn = new DeepNettsClassifier();
        cn.init(new int[]{6,12,12,12,2});
        TrackMultiPres.train(filename, cn);
        TrackMultiPres.evaluate(filename, cn,1);
        //TrackMultiPres.evaluate(filename, cn);        
    }
}
