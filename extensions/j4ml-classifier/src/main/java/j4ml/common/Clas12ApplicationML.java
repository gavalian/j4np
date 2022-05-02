/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.common;

import j4np.utils.io.OptionApplication;
import j4np.utils.io.OptionStore;

/**
 *
 * @author gavalian
 */
public class Clas12ApplicationML extends OptionApplication {

    public Clas12ApplicationML(){
        super("clas12-ml");
        OptionStore store = this.getOptionStore();
        store.addCommand("-extract", "extract data for track ml algorithms");
        store.getOptionParser("-extract").addRequired("-o",
                 "output file name to write training data");   
        store.getOptionParser("-extract").addOption("-max", "32000", "maximum number of tracks per momentum bin");
    }
    
    @Override
    public String getDescription() {
        return "Applications to deal with CLAS12 AI/ML needs\n"
                + "  inclding extracting data for training, training and testing.....";        
    }
    
    @Override
    public boolean execute(String[] args) {
        OptionStore store = this.getOptionStore();
        
        store.parse(args);
        if(store.getCommand().compareTo("-extract")==0){
            TrackDataExtractor.extract(
                    store.getOptionParser("-extract").getOption("-o").stringValue(), 
                    store.getOptionParser("-extract").getInputList(),
                    store.getOptionParser("-extract").getOption("-max").intValue());
        }
        return true;
    }
    
}
