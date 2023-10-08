/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.data;

import j4np.neural.finder.NeuralTrackFinder;
import j4np.neural.trainers.RegressionTrainer;
import j4np.utils.io.OptionApplication;
import j4np.utils.io.OptionParser;
import j4np.utils.io.OptionStore;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class NeuralApplication extends OptionApplication {
    public NeuralApplication(){
        super("c12ml");
        OptionStore store = this.getOptionStore().setName("c12ml");
        store.addCommand("-extract", "extract data for track ml algorithms");
        store.getOptionParser("-extract").
                addRequired("-o","output file name to write training data")
                .addOption("-max", "64000", "maximum number of tracks per momentum bin");
        
        store.addCommand("-regression", "regression trainer");
        store.getOptionParser("-regression")
                .addRequired("-t", "training file name")
                .addRequired("-v", "validation file name")
                .addRequired("-n", "nerual network file name")
                .addRequired("-r", "run number")
                .addOption("-e", "125", "number of epochs to train")
                .addOption("-max", "15000", "maximum number of tracks per momentum bin");
        
        store.addCommand("-convert", "extract data for track ml algorithms");
        store.getOptionParser("-convert").addOption("-o", "output_converted.h5", "output file name");

        store.addCommand("-cook", "cook raw decoded file using ai");
        store.getOptionParser("-cook").addOption("-run", "5197", "the reference run number");
    }

    @Override
    public String getDescription() {
        return "new version of AI utils for combined denoise/regression/assited training"; 
    }

    @Override
    public boolean execute(String[] args) {
        OptionStore store = this.getOptionStore();
        
        store.parse(args);
        if(store.getCommand().compareTo("-extract")==0){
            Clas12DataExtract ext = new Clas12DataExtract();
            ext.outputFile = store.getOptionParser("-extract")
                    .getOption("-o").stringValue();
            ext.maxEvents = store.getOptionParser("-extract").getOption("-max").intValue();
            List<String> files = store.getOptionParser("-extract").getInputList();
            ext.processFiles(files);
        }
        
        if(store.getCommand().compareTo("-convert")==0){
            Clas12DataConverter.convert(store.getOptionParser("-convert").getInputList().get(0), 
                    store.getOptionParser("-convert")
                    .getOption("-o").stringValue());
        }
        
        if(store.getCommand().compareTo("-cook")==0){
            NeuralTrackFinder.reconstruct(store.getOptionParser("-cook").getInputList());
        }
        
        if(store.getCommand().compareTo("-regression")==0){
            OptionParser op = store.getOptionParser("-regression");
            RegressionTrainer t = new RegressionTrainer();
            t.nEpochs = op.getOption("-e").intValue();
            t.maxEvents = op.getOption("-max").intValue();
            t.networkFile = op.getOption("-n").stringValue();
            t.run = op.getOption("-r").intValue();
            t.trainAndValidate(op.getOption("-t").stringValue(),
                    op.getOption("-v").stringValue());
        }
        return true;
    }
    
}
