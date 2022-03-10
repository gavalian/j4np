/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.particle;

import deepnetts.data.TabularDataSet;
import j4ml.extratrees.networks.ClassifierExtraTrees;
import j4ml.pid.data.DetectorResponse;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.utils.io.DataArrayUtils;
import j4np.utils.io.DataPair;
import j4np.utils.io.DataPairList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.visrec.ml.data.DataSet;

/**
 *
 * @author gavalian
 */
public class DataProvider {
    
   
    public DataPairList convertData(List<ClasParticle> cplist, int label, int max){
        DataPairList list = new DataPairList();       
        int counter = 0;
        for(int i = 0; i < cplist.size(); i++){
            double[] input = cplist.get(i).getFeatures();
            list.add(new DataPair(input,new double[]{(double) label}));
            if(i>max) break;
        }        
        return list;
    }

    public void evaluateData(ClassifierExtraTrees ct, List<ClasParticle> list){
        for(int i = 0; i < list.size(); i++){
            double[] input = list.get(i).getFeatures();
            double value = ct.evaluate(input);
            if(value>0.1){
                list.get(i).setInferedPid(11);
            } else {
                list.get(i).setInferedPid(-211);
            }
        }
    }
    
    
    public List<ClasParticle> filter1(List<ClasParticle> list){
        List<ClasParticle> l2 = list.stream()
                .filter(e -> e.featureSize()==9)
                .filter(e -> e.getCharge()==-1)
                .collect(Collectors.toList());
        return l2;
    }
    
    
    public DataPairList loadData(String file, int label, int max){
        List<ClasParticle>  post = ClasReader.importData(file,max);
        List<ClasParticle>  postf = filter1(post);
        DataPairList  data_pos = convertData(postf, label, max);
        return data_pos;
    }
            
    public DataPairList loadData(String file_1, String file_2, int max){
        List<ClasParticle>  post = ClasReader.importData(file_1,max);
        List<ClasParticle>  negt = ClasReader.importData(file_2,max);
        
        List<ClasParticle>  postf = filter1(post);
        List<ClasParticle>  negtf = filter1(negt);
        DataPairList  data_pos = convertData(postf, 1, max);
        DataPairList  data_neg = convertData(negtf, 0, max);
        
        data_pos.getList().addAll(data_neg.getList());
        data_pos.shuffle();
        return data_pos;
    }
    
    public static void main(String[] args){
        
        String file_1 = "/Users/gavalian/Work/software/project-10a.0.4/data/pid/pid_elec.hipo_000000";
        String file_2 = "/Users/gavalian/Work/software/project-10a.0.4/data/pid/pid_pion.hipo_000000";
        String file_3 = "/Users/gavalian/Work/software/project-10a.0.4/data/pid/pid_elec.hipo_000001";
        String file_4 = "/Users/gavalian/Work/software/project-10a.0.4/data/pid/pid_pion.hipo_000001";
        
        ClasReader cr = new ClasReader();
        DataProvider provider = new DataProvider();
        
        DataPairList data = provider.loadData(file_1, file_2, 240000);
        
        ClassifierExtraTrees ct = new ClassifierExtraTrees(27,1); 
        ct.setNumTrees(150);
        ct.setK(25);
        ct.setNMin(2);
        
        ct.train(data);
        
        ct.export("network_ert_9.net");
        
        List<ClasParticle>  elecTest2 = ClasReader.importData(file_3, 250000);
        List<ClasParticle>  pionTest2 = ClasReader.importData(file_4, 250000);

        List<ClasParticle>  elecTest = provider.filter1(elecTest2);
        List<ClasParticle>  pionTest = provider.filter1(pionTest2);
        
        ClassifierExtraTrees ct2 = new ClassifierExtraTrees(); 
        ct2.load("network_ert.net");
        
        provider.evaluateData(ct2, elecTest);
        provider.evaluateData(ct2, pionTest);
        
        //samplef.stream().forEach(System.out::println);
        
        //elecTest.stream().filter(e -> e.getPid()==-211)
         //       .collect(Collectors.toList()).forEach(System.out::println);
                
        int ntotal = elecTest.size();
        int nrecover = elecTest.stream()
                .filter(e -> e.getInferedPid()==11&&e.getPid()==-211)
                .collect(Collectors.toList()).size();
        int nlost = elecTest.stream()
                .filter(e -> e.getInferedPid()==-211&&e.getPid()==11)
                .collect(Collectors.toList()).size();
        
        
        System.out.printf("\n\n\n>>>>> E  total = %d, recover = %d, lost = %d , recovery = %.2f percent\n",
                ntotal,nrecover,nlost, 100.0*((double) nrecover)/ntotal );
        
        
        //elecTest.stream().filter(e -> e.getPid()==-211)
        //        .collect(Collectors.toList()).forEach(System.out::println);
                
         ntotal   = pionTest.size();
         nrecover = pionTest.stream()
                .filter(e -> e.getInferedPid()==-211&&e.getPid()==-211)
                .collect(Collectors.toList()).size();
         nlost = pionTest.stream()
                .filter(e -> e.getInferedPid()==11&&e.getPid()==-211)
                .collect(Collectors.toList()).size();
        
         System.out.printf(">>>>> PI total = %d, recover = %d, lost = %d , recovery = %.2f percent\n\n\n",
                ntotal,nrecover,nlost, 100.0*((double) nrecover)/ntotal );
        /*System.out.println(" size = " + samplef.size());
        System.out.println(" total recovered size = " + samplef.stream()
                .filter(e -> e.getInferedPid()==11&&e.getPid()==-211)
                .collect(Collectors.toList()).size());
        System.out.println(" total lost size = " + samplef.stream()
                .filter(e -> e.getInferedPid()==-211&&e.getPid()==11)
                .collect(Collectors.toList()).size());
        */
        //samplef.stream().filter(e -> e.getPid()==11&&e.getInferedPid()==-211)
        //        .collect(Collectors.toList()).forEach(System.out::println);
        
        //for(ClasParticle a : postf) 
        //    System.out.println(a);
        
        //List<ClasParticle>  negt = ClasReader.importData(file_2);                
        /*DataProvider provider = new DataProvider();
        DataPairList  list1 = provider.loadData(file_1, 1, 100000);
        DataPairList  list2 = provider.loadData(file_2, 0, 100000);
        
        DataPairList data = new DataPairList();
        
        data.getList().addAll(list1.getList());
        data.getList().addAll(list2.getList());
        data.shuffle();
        
        DataPairList[]  tr = DataPairList.split(data,0.7, 0.3);

        list1.show();
        list2.show();
        ClassifierExtraTrees ct = new ClassifierExtraTrees(27,1);        
        ct.train(tr[0]);
        ct.test(tr[1]);*/
    }
}
