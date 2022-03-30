/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.regression;

import j4np.utils.io.OptionApplication;
import j4np.utils.io.OptionStore;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class RegressionApp extends OptionApplication {
    //OptionStore store = new OptionStore();
    
    public RegressionApp(){
        super("regression");
        OptionStore store = this.getOptionStore();
        store.addCommand("-extract", "extract data for trainingregression algorithms");
        store.getOptionParser("-extract").addOption("-o",
                "extract_regression.txt", "output file name to write training data");
        store.getOptionParser("-extract").addOption("-r",
                "0", "reaction to write out, 0 - e-pi+, 1 - e-pi-pi+");
        store.getOptionParser("-extract").addOption("-n",
                "-1", "maximum number of events to process");
        store.getOptionParser("-extract").addRequired("-mode", "track mode 0 - hit based, 1 - time based");

        store.addCommand("-process", "analyze data from txt file");
        store.getOptionParser("-process").addOption("-r","0","reaction (0 - e-pi+, 1 - e-pi-pi+)");
        store.getOptionParser("-process").addOption("-d","/montecarlo","directory to save histograms in");
        store.getOptionParser("-process").addRequired("-o","output twig file for saving histograms");
    }
    
    @Override
    public String getDescription() {
        return "programs for regression ai for clas12 dc";
    }

    @Override
    public boolean execute(String[] args) {
        OptionStore store = this.getOptionStore();
        
        store.parse(args);
        if(store.getCommand().compareTo("-extract")==0){
            List<String> input = store.getOptionParser("-extract").getInputList();
            String      output = store.getOptionParser("-extract").getOption("-o").stringValue();
            int           mode = store.getOptionParser("-extract").getOption("-mode").intValue();
            int          react = store.getOptionParser("-extract").getOption("-r").intValue();
            int            max = store.getOptionParser("-extract").getOption("-n").intValue();
            DataExtractRegression extr = new DataExtractRegression();
            extr.setDCLevel(mode);
            extr.outputFile = output;
            
            switch (mode){
                case 0: extr.setDCLevel(0);extr.extract(input.get(0), max); break;
                case 1: extr.setDCLevel(1);extr.extract(input.get(0), max); break;
                case 2: extr.setDCLevel(1);extr.extractMCsingle(input.get(0), max); break;
                case 4: extr.setDCLevel(0);extr.extractThree(input.get(0), max); break;
                case 5: extr.setDCLevel(1);extr.extractThree(input.get(0), max); break;
                default: extr.extract(input.get(0), max); break;
            }
            /*
            if(mode>=0&&mode<2){
                if(react==0){
                    extr.extract(input.get(0), max);
                } else {
                    extr.extractThree(input.get(0), max);
                }
            } else {
                if(mode==2){
                    extr.extractTwo(input.get(0), max);
                }
            }*/
        }
        return true;
    }
    
}
