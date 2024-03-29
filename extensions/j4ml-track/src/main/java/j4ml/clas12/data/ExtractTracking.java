/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.data;

import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.utils.ProgressPrintout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 * @author gavalian
 */
public class ExtractTracking {
    List<ExtractModule> modules = new ArrayList<>();
    BankStore banks = null;
    
    public ExtractTracking(){
        banks = new BankStore(new String[]
            {"TimeBasedTrkg::TBTracks",
            "TimeBasedTrkg::TBClusters",
            "TimeBasedTrkg::TBHits",
            "REC::Particle",
            ""}
        );
    }
    
    public void addModule(ExtractModule module){
        modules.add(module);
    }
    
    public void processFile(List<String> list){
        HipoReader chain = new HipoReader();
        //chain.addFiles(list);
        chain.open(list.get(0));
        
        banks.init(chain);
        Event event = new Event();
        ProgressPrintout progress = new ProgressPrintout();
        while(chain.hasNext()==true){
            chain.nextEvent(event);
            banks.read(event);
            progress.updateStatus();
            for(ExtractModule m : modules) m.process(banks);
        }
        
        for(ExtractModule m : modules) m.finish();
    }
    
    public static void main(String[] args){
        
        List<String> inputs = new ArrayList<>();
        /*inputs.addAll(Arrays.asList(
                "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00050-00054.hipo",
                "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00055-00059.hipo",
                "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00060-00064.hipo",
                "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00065-00069.hipo",
                "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00070-00074.hipo",
                "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00075-00079.hipo",
                "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00080-00084.hipo",
                "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00085-00089.hipo"
        ));*/
        inputs.addAll(Arrays.asList(
                "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00050-00054.hipo"
        ));
        
        /*
        inputs.addAll(Arrays.asList(               
                "/Users/gavalian/Work/DataSpace/ml/exclusive_epiX_005038.hipo"
        ));*/
        
        if(args.length>0){
            inputs.clear();
            for(int i = 0; i < args.length; i++)
                inputs.add(args[i]);
        }
        
        ExtractTracking extract = new ExtractTracking();
        /*extract.addModule(new ExtractPhysicsCSV(
                "dc_physics_features_36.csv"));
        */
        
        extract.addModule(new ExtractTestingLSVM(
                "dc_classifier_testing_06_pos.lsvm",
                "dc_classifier_testing_06_neg.lsvm",
                "dc_classifier_testing_12_pos.lsvm",
                "dc_classifier_testing_12_neg.lsvm",
                "dc_classifier_testing_36_pos.lsvm",
                "dc_classifier_testing_36_neg.lsvm",
                "dc_classifier_testing_cnn_pos.lsvm",
                "dc_classifier_testing_cnn_neg.lsvm"
        ));
        
        /*
        extract.addModule(new ExtractFeaturesLSVM(
                "dc_classifier_06_pos.lsvm",
                "dc_classifier_06_neg.lsvm",
                "dc_classifier_12_pos.lsvm",
                "dc_classifier_12_neg.lsvm",
                "dc_classifier_36_pos.lsvm",
                "dc_classifier_36_neg.lsvm"
        ));*/
        /*
        extract.addModule(new ExtractFeaturesCNN(
                "dc_classifier_cnn_pos.lsvm",
                "dc_classifier_cnn_neg.lsvm"
        ));*/
        
        /*
        extract.addModule(new ExtractFeaturesCSV(
                "dc_regression_06_pos.csv",
                "dc_regression_06_neg.csv",
                "dc_regression_12_pos.csv",
                "dc_regression_12_neg.csv",
                "dc_regression_36_pos.csv",
                "dc_regression_36_neg.csv"
        ));*/
       
       
        
        /*extract.addModule(new ExtractFeaturesTwelve("dc_features_12.lsvm"));
        extract.addModule(new ExtractFeaturesThirtySix("dc_features_36.lsvm"));*/
        
        extract.processFile(inputs);                
    }
}
