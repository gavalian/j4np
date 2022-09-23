/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.cvt;

import j4ml.data.DataList;
import j4np.utils.io.OptionApplication;
import j4np.utils.io.OptionStore;

/**
 *
 * @author gavalian
 */
public class CvtApplication extends OptionApplication {
    public CvtApplication(){
        super("cvt");
        OptionStore store = this.getOptionStore();
        
        store.addCommand("-extract", "extract data for track ml algorithms");
        store.getOptionParser("-extract").addRequired("-o",
                "output file name to write training data");
                
        store.addCommand("-train", "retrain existing network");
        store.getOptionParser("-train").addRequired("-o",
                "output network file name");
        store.getOptionParser("-train").addRequired("-i",
                "training data file (CSV)");       
        store.getOptionParser("-train").addOption("-max", "25000", "number of records to load from training file");                                
        store.getOptionParser("-train").addOption("-e", "42", "number of epochs"); 
        
        store.addCommand("-retrain", "retrain existing network");
        store.getOptionParser("-retrain").addRequired("-o",
                "output network file name");
        store.getOptionParser("-retrain").addRequired("-n",
                "trained network file name");
        store.getOptionParser("-retrain").addRequired("-i",
                "training data file (CSV)");       
        store.getOptionParser("-retrain").addOption("-max", "25000", "number of records to load from training file");                                
        store.getOptionParser("-retrain").addOption("-e", "42", "number of epochs");  
        
        store.addCommand("-test", "test data sample");
        store.getOptionParser("-test").addRequired("-n",
                "trained network file name");
        store.getOptionParser("-test").addRequired("-t",
                "trained network file name");
        
        store.addCommand("-process", "process an input file to reduce the ADC bank");
        store.getOptionParser("-process").addRequired("-n",
                "trained network file name");
        store.getOptionParser("-process").addOption("-t","0.25",
                "treshold");
        store.getOptionParser("-process").addRequired("-o",
                "output file name");
        
        store.addCommand("-reduce", "reduce the input file");        
        store.getOptionParser("-reduce").addRequired("-o",
                "output file name");
        store.getOptionParser("-reduce").addOption("-bst", "false","reduce BST::adc bank");
        store.getOptionParser("-reduce").addOption("-bmt", "false","reduce BMT::adc bank");
        
        
        store.addCommand("-type", "write out type 1 tracks");        
        store.getOptionParser("-type").addRequired("-o",
                "output file name");
        store.getOptionParser("-type").addOption("-bmt","false","require BMT to have hits");
    }
    
    @Override
    public String getDescription() {
        return "applicaations for CVT AI";
    }

    @Override
    public boolean execute(String[] args) {
        
        OptionStore store = this.getOptionStore();
        store.parse(args);
        if(store.getCommand().compareTo("-extract")==0){
            DataExtractor ext = new DataExtractor();
            ext.outputFile = store.getOptionParser("-extract").getOption("-o").stringValue();
            //ext.process(file);
            ext.processExtract(store.getOptionParser("-extract").getInputList().get(0));
            return true;
        }        
        
        if(store.getCommand().compareTo("-type")==0){
            DataExtractor ext = new DataExtractor();
            ext.outputFile = store.getOptionParser("-type").getOption("-o").stringValue();
            String  flag = store.getOptionParser("-type").getOption("-bmt").stringValue();
            boolean forceBMT = false;
            if(flag.compareTo("true")==0) forceBMT = true;
            ext.forceBmt = forceBMT;
            
            ext.processReduce(store.getOptionParser("-type").getInputList().get(0));
            
        }
        
        if(store.getCommand().compareTo("-reduce")==0){
            DataExtractor ext = new DataExtractor();
            ext.outputFile = store.getOptionParser("-reduce").getOption("-o").stringValue();
            String  flag = store.getOptionParser("-reduce").getOption("-bst").stringValue();
            String flag2 = store.getOptionParser("-reduce").getOption("-bmt").stringValue();
            boolean reduceBmt = false;
            boolean reduceBst = false;
            if(flag2.compareTo("true")==0) reduceBmt = true;
            if(flag.compareTo("true")==0) reduceBst = true;
            
            //ext.process(file);
            //if(flag.compareTo("true")==0){
            CVTProcessor.reduceFile(store.getOptionParser("-reduce").getInputList().get(0), reduceBst, reduceBmt);
            //} else {
            //    ext.processReduce(store.getOptionParser("-reduce").getInputList().get(0));
            //}
            return true;
        }
        if(store.getCommand().compareTo("-retrain")==0){

            String outNet = store.getOptionParser("-retrain").getOption("-o").stringValue();
            String  inNet = store.getOptionParser("-retrain").getOption("-n").stringValue();
            String   file = store.getOptionParser("-retrain").getOption("-i").stringValue();
            //String   test = store.getOptionParser("-retrain").getOption("-t").stringValue();
            int       max = store.getOptionParser("-retrain").getOption("-max").intValue();
            int    epochs = store.getOptionParser("-retrain").getOption("-e").intValue();
            
            CVTNetwork.retrain(inNet, outNet, file, epochs, max);
            //ext.process(file);
            
            return true;
        }
        
        if(store.getCommand().compareTo("-process")==0){


            String  network = store.getOptionParser("-process").getOption("-n").stringValue();
            String   file = store.getOptionParser("-process").getOption("-o").stringValue();
            //String   test = store.getOptionParser("-retrain").getOption("-t").stringValue();
            double threshold = store.getOptionParser("-process").getOption("-t").doubleValue();
            
            
            CVTProcessor p = new CVTProcessor();
            p.threshold = threshold;
            p.output = file;
            p.process(store.getOptionParser("-process").getInputList().get(0), network);
            //ext.process(file);            
            return true;
        }
        
        if(store.getCommand().compareTo("-train")==0){

            String outNet = store.getOptionParser("-train").getOption("-o").stringValue();
            String   file = store.getOptionParser("-train").getOption("-i").stringValue();
            //String   test = store.getOptionParser("-retrain").getOption("-t").stringValue();
            int       max = store.getOptionParser("-train").getOption("-max").intValue();
            int    epochs = store.getOptionParser("-train").getOption("-e").intValue();
            
            DataList     tr = CVTNetwork.readFile(file, max);
            DataList[] data = DataList.split(tr, 0.95,0.05);
            //tr.show();
            System.out.println("******************");
            System.out.printf("* training data set = %d\n", data[0].getList().size());
            System.out.printf("* testing  data set = %d\n", data[1].getList().size());
            System.out.println("******************\n");
            
            CVTNetwork.train(data[0],data[1],epochs);
           
            //ext.process(file);
            
            return true;
        }
        if(store.getCommand().compareTo("-test")==0){

            String  inNet = store.getOptionParser("-test").getOption("-n").stringValue();

            String   test = store.getOptionParser("-test").getOption("-t").stringValue();
            
            CVTNetwork.evaluate(inNet, test);
            //ext.process(file);
            
            return true;
        }
        
        //store.printUsage();;
        return true;
    }
     
}
