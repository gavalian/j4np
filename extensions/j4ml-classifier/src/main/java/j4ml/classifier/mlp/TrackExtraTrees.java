/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.classifier.mlp;

import j4ml.classifier.data.DataLoader;
import j4ml.extratrees.networks.ClassifierExtraTrees;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import twig.data.GraphErrors;
import twig.data.H1F;
import twig.data.TDirectory;

/**
 *
 * @author gavalian
 */
public class TrackExtraTrees {
    
    public static DataList convert(DataList list){
        DataList result = new DataList();
        for(DataEntry item : list.getList()){
            if(item.getSecond()[1]>0.5){
                result.add(new DataEntry(item.getFirst(),new double[]{1.0}));
            } else {
                result.add(new DataEntry(item.getFirst(),new double[]{0.0}));
            }
        }
        return result;
    }
    public static int neighbors(double[] a, double[] b){
        int counter = 0;
        for(int i = 0; i < a.length; i++){
            if( (a[i]-b[i])<0.001) counter++;
        }
        return counter;        
    }
    public static int getHighestIndex(DataList list){
        int index = 0;
        double max = list.getList().get(0).floatSecond()[0];
        for( int i = 0; i < list.getList().size(); i++){
            if(list.getList().get(i).getSecond()[0]>max){
                max   = list.getList().get(i).getSecond()[0];
                index = i;
            }
        }
        return index;
    }
    
    public static int[]  evaluate( String file, ClassifierExtraTrees ct, int tag){
        List<DataList>  list = DataLoader.loadCombinatoricsPos(file, tag, 15000);
        System.out.println(" LOADED DATA SIZE = " + list.size());
        int[] counter = new int[]{0,0};
        
        for(int i = 0; i < list.size(); i++){
            
            DataList item = TrackExtraTrees.convert(list.get(i));
            
            if(item.getList().size()>0){
                int trueIndex  = TrackExtraTrees.getHighestIndex(item);
                DataList r = ct.evaluate(item);
                int  resIndex  = TrackExtraTrees.getHighestIndex(r);
                
                /*System.out.printf("combinatorics = %5d/%5d index = %5d, infered = %5d\n" ,
                        r.getList().size(),
                        item.getList().size(),trueIndex,resIndex);
                */

                if(resIndex==trueIndex) counter[0]++; else {

                    System.out.println("===== false");
                    item.getList().get(trueIndex).show();
                    r.getList().get(resIndex).show();
                    r.getList().get(trueIndex).show();
                    /*System.out.println(
                            Arrays.toString(item.getList().get(trueIndex).getFirst())
                    + " ===> " + item.getList().get(trueIndex).getSecond()[0]);
                    System.out.println(
                            Arrays.toString(item.getList().get(resIndex).getFirst())
                    + " ===> "  );*/
                    
                    int coin = TrackExtraTrees.neighbors(item.getList().get(trueIndex).getFirst(), 
                            item.getList().get(resIndex).getFirst());
                    if(coin==0){
                        counter[0]++; 
                    } else {
                        counter[1]++;
                    }
                }
                //r.show();
                //list.get(i).show();
            //System.out.printf(" %5d : %8d %8d\n",i,trueIndex,resIndex);
            }

        }
        System.out.printf(" tag = %5d, %8d %8d  %8.5f\n",tag,counter[0],counter[1], ((double) counter[1])/(counter[0]+counter[1]));
        return counter;
    }
        
    public static void train(String filename, ClassifierExtraTrees ct){
        
        DataList list = DataLoader.loadPos(filename, 25000);
        
        list.shuffle();

        DataList[] lists = DataList.split(list, 0.7,0.3);
                
        lists[0].show();
        lists[1].show();
        
        DataList[] listsfull = new DataList[2];
        
        listsfull[0] = DataLoader.generateFalse(lists[0]);
        listsfull[1] = DataLoader.generateFalse(lists[1]);
        
        DataList[] data = new DataList[2];
        
        data[0] = TrackExtraTrees.convert(listsfull[0]);
        data[1] = TrackExtraTrees.convert(listsfull[1]);
        
        
        data[0].show();
        data[1].show();
                
        ct.train(data[0]);       
        ct.test(data[1]);
    }
    
    
    public static void evaluate(String filename,ClassifierExtraTrees ct){
        TDirectory dir = new TDirectory();
        H1F  htrue = new H1F( "htrue",20,-0.5,19.5);
        H1F hfalse = new H1F("hfalse",20,-0.5,19.5);
        
        int[] counter = new int[]{0,0};
        for(int i = 0; i < 19; i++){
            int[] r = TrackExtraTrees.evaluate( filename,ct, i+1);
            counter[0] += r[0];
            counter[1] += r[1];
            for(int k = 0; k < r[0]+r[1] ; k++) htrue.fill(i);
            for(int k = 0; k < r[1] ; k++) hfalse.fill(i);
        }
        H1F hratio = H1F.divide(hfalse, htrue);
        hratio.setName("hratio");
        
        dir.add("/classifier/evaluate/ert", hratio);
        dir.add("/classifier/evaluate/ert", htrue);
        dir.add("/classifier/evaluate/ert", hfalse);
        GraphErrors graph = hratio.getGraph(); graph.setName("gratio");
        dir.add("/classifier/evaluate/ert", graph);
        dir.write("ert_classifier.twig");
    }
    
    public static void main(String[] args){
        
        //String filename1 = "/Users/gavalian/Work/software/project-10a.0.4/data/training_file_4209.0.hipo";
        //String filename2 = "/Users/gavalian/Work/software/project-10a.0.4/data/training_file_4209.1.hipo";
        String filename1 = "/Users/gavalian/Work/software/project-10a.0.4/data/classifier/data_extract_classifier_4209_full.hipo";
        String filename2 = "/Users/gavalian/Work/software/project-10a.0.4/data/classifier/data_extract_classifier_4209_full.hipo";
        ClassifierExtraTrees ct = new ClassifierExtraTrees(6,1);
       
        TrackExtraTrees.train(filename1,ct);
        TrackExtraTrees.evaluate(filename2,ct,1);
        
        /*

        int[] counter = new int[]{0,0};
        for(int i = 0; i < 19; i++){
            int[] r = TrackExtraTrees.evaluate(ct, filename, i+1);
            counter[0] += r[0];
            counter[1] += r[1];
        }
        System.out.println("\n\n==================================================================");
        System.out.printf(" tag = %5d, %8d %8d  %8.5f\n",20,counter[0],counter[1], 
                ((double) counter[1])/(counter[0]+counter[1]));
        */
        //list.show();
        //System.out.println("------------");        
        //data.show();
        //System.out.println("------------");
        //data2.show();   
    }
}
