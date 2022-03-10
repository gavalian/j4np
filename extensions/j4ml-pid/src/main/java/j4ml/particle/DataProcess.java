/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.particle;

import j4ml.extratrees.networks.ClassifierExtraTrees;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author gavalian
 */
public class DataProcess {
    
    List<ClasEvent> clasEvent = new ArrayList<>(); 
            
    ClassifierExtraTrees[] ct = null;//new ClassifierExtraTrees[3];
    String[] files = new String[]{"network_ert_3.net","network_ert_6.net","network_ert_9.net"};
    public DataProcess(){
        ct = new ClassifierExtraTrees[files.length];
        for(int i = 0; i < ct.length; i++){
            ct[i] = new ClassifierExtraTrees();
            ct[i].load(files[i]);
        }
    }
    
    public void initData(String file, int max){
        HipoReader hr = new HipoReader(file);
        ClasReader cr = new ClasReader();
        
        cr.init(hr);
        Event event = new Event();
        int counter = 0;
        while(hr.hasNext()){
            hr.nextEvent(event);
            List<ClasParticle> cpl = cr.read(event);
            
            List<ClasParticle> cpln = cpl.stream()
                    .filter(e -> e.getCharge()<0)
                    .collect(Collectors.toList());
            
            List<ClasParticle> cplp = cpl.stream()
                    .filter(e -> e.getCharge()>0)
                    .collect(Collectors.toList());
            
            if(cpln.size()==2&&cplp.size()==1){
                ClasEvent evt = new ClasEvent();
                evt.neg.addAll(cpln);
                evt.pos.addAll(cplp);
                clasEvent.add(evt);
            }
            counter++;
            if(counter>max) break;
        }
    }
    
    public void evaluate(){
        for(int i = 0; i < clasEvent.size(); i++){
            for(int k = 0; k < clasEvent.get(i).neg.size(); k++){
                int nec = clasEvent.get(i).neg.get(k).featureSize();
                double[] input = clasEvent.get(i).neg.get(k).getFeatures();
                int      netIndex = 0;
                switch(nec){
                    case 6: netIndex = 1; break;
                    case 9: netIndex = 2; break;
                    default: netIndex = 0;
                }
                
                double value = ct[netIndex].evaluate(input);
                if(value>0.5){
                    clasEvent.get(i).neg.get(k).setInferedPid(11);
                    clasEvent.get(i).neg.get(k).setInferedProb(value);
                } else {
                    clasEvent.get(i).neg.get(k).setInferedPid(-211);
                    clasEvent.get(i).neg.get(k).setInferedProb(value);
                }
            }
        }
    }
    
    
    public void show(){
        System.out.println("--- event");        
        clasEvent.stream().forEach(System.out::println);
    }
    
    public static class ClasEvent {
        public List<ClasParticle> neg = new ArrayList<>();
        public List<ClasParticle> pos = new ArrayList<>();
        
        public void show(){
            for(int i = 0; i < neg.size(); i++)
                System.out.println(neg.get(i));
            for(int i = 0; i < pos.size(); i++)
                System.out.println(pos.get(i));
        }
        
        public String toString(){
            StringBuilder str = new StringBuilder();
            str.append("----- clas event\n");
            for(int i = 0; i < neg.size(); i++)
                str.append(neg.get(i).toString()).append("\n");
            for(int i = 0; i < pos.size(); i++)
                str.append(pos.get(i).toString()).append("\n");
            return str.toString();
        }
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/software/project-10a.0.4/data/pid/d.h5";
        DataProcess proc = new DataProcess();
        proc.initData(file, 20000);
        proc.evaluate();
        proc.show();
    }
    
    
}
