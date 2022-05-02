/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.networks;

import j4ml.clas12.track.TrackConstrain;
import j4ml.data.DataList;
import j4ml.data.DataNormalizer;
import j4ml.ejml.EJMLModel;
import j4np.data.base.DataUtils;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.utils.ProgressPrintout;
import j4np.utils.base.ArchiveUtils;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class TrackNetworkValidator {
    
    DataNormalizer dc6normalizer = new DataNormalizer(
            new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
            new double[]{112,112,112,112,112,112});
    
    int   counterTruePositive = 0;
    int  counterFalsePositive = 0;    
    int  counterFalseNegative = 0;
    
    EJMLModel model = null;
    TrackConstrain constrain = new TrackConstrain();
    public boolean      writeOutput = true;
    public String        outputFile = "validator_output.h5";
    public String            archiv = "clas12ejml.network";
    public String           network = "network/6302/default/trackClassifier.network";
    
    public TrackNetworkValidator(){
        
    }
    
    public TrackConstrain getConstrain(){return constrain;};
    
    private int trueIndex(DataList list){
        int index = -1;
        for(int i = 0; i < list.getList().size(); i++){
            if(list.getList().get(i).getSecond()[0]<0.5) index = i;
        }
        return index;
    }
    
    private int highestIndex(DataList list){
        int index = 0;
        double maxProb = 0.0;        
        for(int i = 0; i < list.getList().size(); i++){
            float[] infered = list.getList().get(i).getInfered();
            for(int k = 1; k < infered.length; k++)
                if(infered[k]>maxProb){
                    maxProb = infered[k];
                    index = i;
                }
        }
        return index;
    }        
    
    public void eventAnalyzer(EJMLModel model, Event event){
        
        DataList list = DataProvider.readClassifierEvent(event, constrain);
        int trueIndex = trueIndex(list);
        
        DataList.normalizeInput(list, dc6normalizer);
                
        for(int i = 0; i < list.getList().size(); i++){
            
            float[] result = new float[list.getList().get(i).getSecond().length];
            float[] input  = list.getList().get(i).floatFirst();
            model.getOutput(input, result);
            list.getList().get(i).setInfered(result);
        
        }
        
        int highIndex = this.highestIndex(list);
        
        if(highIndex==trueIndex){
            counterTruePositive++;
        } else {
            if(highIndex>=0&&trueIndex>=0){
                double[] meansTrue = list.getList().get(trueIndex).getFirst();
                double[] meansHigh = list.getList().get(highIndex).getFirst();
                int shareCount = Combinatorics.shareCount(meansHigh, meansTrue);
                //System.out.printf(" share count = %d\n",shareCount);
                //System.out.println(Arrays.toString(meansTrue));
                //System.out.println(Arrays.toString(meansHigh));
                if(shareCount==0){
                    this.counterTruePositive++;
                } else {
                    float[] meansFloat = new float[meansHigh.length];
                    for(int k = 0; k < meansFloat.length; k++) 
                        meansFloat[k] = (float) meansHigh[k]*112;
                    Node fn = new Node(3001,4,meansFloat);
                    event.remove(3001, 4);                    
                    event.write(fn);
                    this.counterFalsePositive++;
                }
            }
        }
        //list.show();
    }
    
    public void show(){
        System.out.println("");
        System.out.printf("%15s | %15s | %15s \n","TP","FP","FN");
        System.out.printf("%15d | %15d | %15d \n",
                counterTruePositive,counterFalsePositive,
                counterFalseNegative
        );
    }
    
    
    public void processFile(String file){
        
        List<String> netContent = ArchiveUtils.getFileAsList(archiv, network);
        
        EJMLModel model = EJMLModel.create(netContent);
        model.setType(EJMLModel.ModelType.SOFTMAX);
        TrackNetworkValidator v = new TrackNetworkValidator();
        HipoWriter w = new HipoWriter();
        if(this.writeOutput==true){
            w.open(outputFile);
        }
        
        HipoReader r = new HipoReader();
        r.open(file);
        ProgressPrintout p = new ProgressPrintout();
        Event event = new Event();
        int counter = 0;
        while(r.hasNext()){
            r.nextEvent(event);
            //System.out.println(" event # " + counter);
            p.updateStatus();
            v.eventAnalyzer(model, event);
            if(this.writeOutput==true) w.addEvent(event);
            counter++;
        }
        
        v.show();
        if(this.writeOutput==true) w.close();
    }
    
    public static void main(String[] args){
        String file   = "/Users/gavalian/Work/Software/project-10.4/studies/clas12nn/testing_sample_006302.evio.01600-02626.hipo";
        String archiv = "/Users/gavalian/Work/Software/project-10.4/studies/clas12nn/clas12ejml.network";
        String afile  = "network/6302/default/trackClassifier.network";
        
        
        List<String> netContent = ArchiveUtils.getFileAsList(archiv, afile);
        
        EJMLModel model = EJMLModel.create(netContent);
        model.setType(EJMLModel.ModelType.SOFTMAX);
        TrackNetworkValidator v = new TrackNetworkValidator();
        
        HipoReader r = new HipoReader();
        r.open(file);
        ProgressPrintout p = new ProgressPrintout();
        Event event = new Event();
        int counter = 0;
        while(r.hasNext()){
            r.nextEvent(event);
            //System.out.println(" event # " + counter);
            p.updateStatus();
            v.eventAnalyzer(model, event);
            counter++;
        }
        v.show();
    }
}
