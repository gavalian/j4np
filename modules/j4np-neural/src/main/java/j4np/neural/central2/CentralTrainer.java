/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.central2;

import deepnetts.net.layers.activation.ActivationType;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.deepnetts.DeepNettsNetwork;
import j4ml.ejml.EJMLModel;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author gavalian
 */
public class CentralTrainer {
   
    public DataList readData(String file, int max){
        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks("cvtml::clusters");
        DataList data = new DataList();
        int counter = 0;
        //for(int i = 0; i < max; i++){
        while(r.hasNext()){            
            counter++; if(counter>max) break;
            r.nextEvent(b);
            List<Trajectory2> traj = Constructor.getTrajectories(b[0]);
            //System.out.printf(" event #%d trajectort size = %d\n", counter,traj.size());
            
            Constructor.updateTrajectory(b[0], traj); 
            List<Trajectory2> traj2 = Constructor.filter(traj, 6);
            for(int i = 0; i < traj2.size(); i++){
                List<float[]> features = Constructor.getFeatures(b[0], traj2.get(i));
                data.add(new DataEntry(DataEntry.combine(features),new float[]{1.0f,0.0f}),true);
            }
            
            List<Trajectory2> traj3 = Constructor.filter(traj, 4);
            List<Trajectory2> traj4 = Constructor.crop(traj3, traj2.size());
            for(int i = 0; i < traj4.size(); i++){
                List<float[]> features = Constructor.getFeatures(b[0], traj4.get(i));
                data.add(new DataEntry(DataEntry.combine(features),new float[]{0.0f,1.0f}),true);
            }
            
        }
        return data;
    }
    public void debug(String file, int nevent){
        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks("cvtml::clusters");
        Event event = new Event();
        r.getEvent(event, nevent);
        
        event.read(b);
        List<Trajectory2> traj2 = Constructor.getTrajectories(b[0]);
        List<Trajectory2> traj = new ArrayList<>();
        
        //for(int i = 0 ; i < traj2.size();i++){
        //    if(traj2.get(i).segments.get(0).ring==1&&traj2.get(i).segments.get(0).sector==1) traj.add(traj2.get(i));
        //}
        
        Constructor.updateTrajectory(b[0], traj);
        System.out.printf("event #%d trajectory %d\n",nevent,traj.size());
        for(int i = 0; i < traj.size(); i++){
            //if(traj.get(i).status!=0){ 
                //System.out.printf("%4d : %4d    %f %f, %f\n",i, traj.get(i).status,
                //        57.29*traj.get(i).segments.get(0).theta(),57.29*traj.get(i).segments.get(1).theta(),
                //        57.29*traj.get(i).segments.get(0).theta()-57.29*traj.get(i).segments.get(1).theta()
                //);
                System.out.println(traj.get(i));
            //}
            
        }
        
    }
    
    public void evaluate(String file, int max){
        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks("cvtml::clusters");
        DataList data = new DataList();
        
        HipoWriter w = HipoWriter.create("reduced.h5", r);
        
        int counter = 0;
        
        Event event = new Event();
        //for(int i = 0; i < max; i++){
        EJMLModel model = new EJMLModel("cvt6.network");
        while(r.hasNext()){      
            counter++; if(counter>max) break;
            r.nextEvent(event);
            event.read(b);
            List<Trajectory2> traj = Constructor.getTrajectories(b[0]);
            Constructor.updateTrajectory(b[0], traj);
            float[] output = new float[2];
            for(int i = 0; i < traj.size(); i++){
                List<float[]> features = Constructor.getFeatures(b[0], traj.get(i));
                float[] input = DataEntry.combine(features);
                model.feedForwardSoftmax(input, output);
                traj.get(i).probability = output[0];
                
            }
            
            
            Set<Integer> indicies = new HashSet<>();
            for(Trajectory2 t : traj){
                if(t.probability>0.5){
                  indicies.add(t.index[0]);
                  indicies.add(t.index[1]);
                  indicies.add(t.segments.get(0).reference[0]);
                  indicies.add(t.segments.get(0).reference[1]);
                  indicies.add(t.segments.get(1).reference[0]);
                  indicies.add(t.segments.get(1).reference[1]);
                }
                //System.out.println(t);
            }
            
            //System.out.println(" Probability Printout: Index Set Size = " + indicies.size() + " / " + b[0].getRows());
            
            
            for(Integer index : indicies){
                int status = b[0].getInt("status", index);
                if(status<10) b[0].putInt("status", index, status+10);
            }
            
            event.remove(b[0].getSchema());
            
            event.write(b[0]);
            
            w.addEvent(event);
            /*
            List<Trajectory2> traj2 = Constructor.filter(traj, 6);
            for(int i = 0; i < traj2.size(); i++){
                List<float[]> features = Constructor.getFeatures(b[0], traj2.get(i));
                data.add(new DataEntry(DataEntry.combine(features),new float[]{1.0f,0.0f}),true);
            }
            
            List<Trajectory2> traj3 = Constructor.filter(traj, 4);
            List<Trajectory2> traj4 = Constructor.crop(traj3, traj2.size());
            for(int i = 0; i < traj4.size(); i++){
                List<float[]> features = Constructor.getFeatures(b[0], traj4.get(i));
                data.add(new DataEntry(DataEntry.combine(features),new float[]{0.0f,1.0f}),true);
            }*/
            
        }
        w.close();

    }
    public void train(String file, int max){
        
        DataList data = this.readData(file, max);
        DeepNettsNetwork regression = new DeepNettsNetwork();
        regression.activation(ActivationType.RELU); // or ActivationType.TANH
        regression.outputActivation(ActivationType.SOFTMAX);
        regression.learningRate(0.001);
        
        data.scan();
        data.show();
        data.shuffle();
        regression.init(new int[]{24,24,24,8,2});
        
        regression.train(data, 2024);
        
        regression.save("cvt6.network");
    }
    public static void main(String[] args){
        CentralTrainer tr = new CentralTrainer();
        String file = "/Users/gavalian/Work/Software/project-10.8/study/central/MLSample1_wHits.hipo";
        String file2 = "/Users/gavalian/Work/Software/project-10.8/study/central/MLSample2_wHits.hipo";
        String file3 = "/Users/gavalian/Work/Software/project-10.8/study/central/MLSample_1_r.hipo";
        String file4 = "/Users/gavalian/Work/Software/project-10.8/study/central/MLSample_2_r.hipo";
        //tr.train(file3, 12000);
        //tr.evaluate(file3, 1000);
        tr.debug(file, 208);
    }
}
