/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.networks;

import deepnetts.net.layers.activation.ActivationType;
import j4ml.clas12.track.TrackConstrain;
import j4ml.data.DataList;
import j4ml.data.DataNormalizer;
import j4ml.deepnetts.DeepNettsClassifier;
import j4ml.deepnetts.DeepNettsNetwork;
import j4ml.deepnetts.DeepNettsRegression;
import j4ml.ejml.EJMLModel;
import j4np.utils.ProgressPrintout;
import j4np.utils.base.ArchiveUtils;
import java.util.Arrays;
import java.util.List;
import twig.data.AsciiPlot;
import twig.data.DataGroup;
import twig.data.DataSetSerializer;
import twig.data.H1F;
import twig.data.H2F;
import twig.data.TDirectory;

/**
 *
 * @author gavalian
 */
public class TrackNetworkTrainer {

    DataNormalizer dc6normalizer = new DataNormalizer(
            new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
            new double[]{112,112,112,112,112,112});    
    
    DataNormalizer regression3normalizer = new DataNormalizer(
            new double[]{ 0.5, 0.0, 40.0},
            new double[]{10.5, 0.2, 80.0}
    );
    
    DataNormalizer regression4normalizer = new DataNormalizer(
            new double[]{ 0.5, 0.0,  40.0,-15.0},
            new double[]{10.5, 0.2, 120.0,5.0}
    );
    
    DataNormalizer r3n_positive = new DataNormalizer(
            new double[]{ 0.5, 5.0,  40.0},
            new double[]{10.5,35.0, 120.0}
    );
    
    DataNormalizer r3n_negative = new DataNormalizer(
            new double[]{ 0.5, 5.0,  0.0},
            new double[]{10.5,35.0, 80.0}
    );
    
    public int maxBinsRead = 64000;
    public int     nEpochs = 125;
    public boolean drawAscii = true;

    public ActivationType  fixerActivation = ActivationType.RELU;
    public ActivationType  fixerActivationOutput = ActivationType.SIGMOID;
    
    public ActivationType  classifierActivation = ActivationType.RELU;
    public ActivationType  classifierActivationOutput = ActivationType.SOFTMAX;
    
    public TrackConstrain constrain = new TrackConstrain();
    
    private    int          referenceRun = 0;
    private String       referenceFlavor = "default";    
    private boolean  useReferenceNetwork = true;
    
    
    public TrackConstrain getConstrain(){ return constrain;}
    
    public void setFixerActivation(ActivationType fa, ActivationType fao){
        this.fixerActivation = fa;
        this.fixerActivationOutput = fao;
    }
    /**
     * This routine will train a fixer auto-encoder and save the network
     * inside an archive file for a given run number and flavor. The saved 
     * file is a text file with weights and biases to be evaluated by EJML
     * code present in j4ml-networks extension module.
     * @param hipoFile
     * @param archive
     * @param run
     * @param flavor 
     */
    
    public void encoderTrain(String hipoFile, String archive, int run, String flavor){                
        
        DataList list = DataProvider.readFixerData(hipoFile, constrain, maxBinsRead);
        
        DataList.normalizeInput( list, dc6normalizer);
        DataList.normalizeOutput(list, dc6normalizer);
        
        list.show();
        
        //DeepNettsRegression encoder = new DeepNettsRegression(
        //        this.fixerActivation,this.fixerActivationOutput);
        
        DeepNettsNetwork encoder = new DeepNettsNetwork();
        encoder.activation(fixerActivation)
                .outputActivation(fixerActivationOutput);
        
        list.shuffle();
        
        encoder.init(new int[]{6,12,12,6,12,12,6});        
        encoder.train(list, nEpochs);
        
        List<String>  networkContent = encoder.getNetworkStream();
        String archiveFile = String.format("network/%d/%s/trackFixer.network",run,flavor);
        ArchiveUtils.writeFile(archive, archiveFile, networkContent);
        
        archiveFile = String.format("network/%d/%s/corruptionEncoder.json",run,flavor);
        List<String>  networkJson = Arrays.asList(encoder.getJson());
        ArchiveUtils.writeFile(archive, archiveFile, networkJson);
    }
    
    private int findZero(float[] r){
        int index = 0;
        for(int k = 0; k < r.length; k++)
            if(r[k]<0.0000001) index = k;
        return index;
    }
    
    public void encoderTest(String hipoFile, String archive, int run, String flavor){
        
        String archiveFile = String.format("network/%d/%s/trackFixer.network",run,flavor); 
        String     dataDir = String.format("network/%d/%s",run,flavor);
        
        List<String> networkContent = ArchiveUtils.getFileAsList(archive,archiveFile);
        
        EJMLModel model = EJMLModel.create(networkContent);
        model.setType(EJMLModel.ModelType.RELU_SIGMOID);
        
        DataList list = DataProvider.readFixerData(hipoFile, constrain, maxBinsRead);
        
        DataList.normalizeInput(list, dc6normalizer);
        DataList.normalizeOutput(list, dc6normalizer);
        
        H2F h2 = new H2F("fixerPerformance",6,0.5,6.5,80,-10,10);
        h2.attr().setTitleX("Super Layer");
        h2.attr().setTitleY("Wtrue-Wai");
        
        long then = System.nanoTime();

        for(int loop = 0; loop < list.getList().size(); loop++){
            float[] input = list.getList().get(loop).floatFirst();
            float[] result = new float[6];
            model.getOutput(input, result);
            list.getList().get(loop).setInfered(result);
            float[] output = list.getList().get(loop).floatSecond();
            int zero = findZero(input);
            if(zero>=0&&zero<6){
                h2.fill(zero+1, 112*(output[zero]-result[zero]));
            }
        }
        
        long now = System.nanoTime();
        
        list.show();       
        TDirectory dir = new TDirectory();
        dir.add("/"+dataDir, h2);
        dir.write(archive);
        
        DataGroup group = new DataGroup(2,3);
        group.setName("autoencoder");
        group.add(h2, 0, "");
        for(int i = 0; i < 5; i++){
            H1F h = h2.sliceX(i);
            h.setName("region_"+(i+1));
            group.add(h, i+1, "");
        }
        DataSetSerializer.exportDataGroup(group, archive, dataDir);
        if(this.drawAscii == true){
           /* for(int bin = 0; bin < h2.getXAxis().getNBins(); bin++){
                H1F h = h2.sliceX(bin);
                AsciiPlot.draw(h);                
            }*/
           H1F h = h2.projectionY();
           AsciiPlot.draw(h);
        }
        
        System.out.println("\n::: network evaluation >>> " + 
                ProgressPrintout.processRateNano(list.getList().size(), then, now));
//EJMLModel model = new EJMLModel(ModelType.RELU_SIGMOID.name());
    }
    
    
    public void classifierTrain(String hipoFile, String archive, int run, String flavor){
        
        DataList list = DataProvider.readClassifierData(hipoFile, constrain, maxBinsRead);
        
        DataList.normalizeInput( list, dc6normalizer);
        list.show();
        
        DeepNettsClassifier classifier = new DeepNettsClassifier();
        classifier.init(new int[]{6,12,24,24,12,3});
        
        list.shuffle();
        
        classifier.train(list, nEpochs);
        List<String>  networkContent = classifier.getNetworkStream();
        String archiveFile = String.format("network/%d/%s/trackClassifier.network",run,flavor);
        ArchiveUtils.writeFile(archive, archiveFile, networkContent); 
        
        List<String>  networkJson = Arrays.asList(classifier.getJson());
        archiveFile = String.format("network/%d/%s/candidateClassifier.json",run,flavor);
        ArchiveUtils.writeFile(archive, archiveFile, networkJson);
    }
    
    public void classifierTest(String hipoFile, String archive, int run, String flavor){
        
        String archiveFile = String.format("network/%d/%s/trackClassifier.network",run,flavor); 
        String     dataDir = String.format("network/%d/%s",run,flavor);
        
        List<String> networkContent = ArchiveUtils.getFileAsList(archive,archiveFile);
        
        EJMLModel model = EJMLModel.create(networkContent);
        model.setType(EJMLModel.ModelType.SOFTMAX);
        
        H1F  hTruePositive = new H1F(  "hTruePositive",40,0.5,40.5);
        H1F  hTrue = new H1F(  "hTrue",40,0.5,40.5);
        H1F  hTrueNegative = new H1F(    "hTrueNegative",40,0.5,40.5);
        H1F hFalsePositive = new H1F(   "hFalsePositive",40,0.5,40.5);
        H1F  hFalseNegative = new H1F(  "hFalseNegative",40,0.5,40.5);
        
        hTruePositive.attr().set("fc=2,fs=2,lc=2");
        hFalsePositive.attr().set("fc=4,fs=12,lc=4");
        hTrueNegative.attr().set("fc=2,fs=2,lc=2");
        hFalseNegative.attr().set("fc=4,fs=12,lc=4");
        
        
        hTrue.attr().set("lc=1");
        
        
        long candidateCount = 0L;
        long readTime = 0L;
        long normTime = 0L;
        long evalTime = 0L;
        
        for(int i = 1; i <=40; i++){
            long then = System.currentTimeMillis();
            List<DataList> dataList = DataProvider.readClassifierDataWithTagEvents(hipoFile, 
                    constrain, i, maxBinsRead);
            
            long now = System.currentTimeMillis();
            
            readTime += (now-then);
            System.out.printf("read time = %d ms\n",now-then);
            for(int k = 0; k < dataList.size(); k++){
                
                DataList list = dataList.get(k);
                
                candidateCount += list.getList().size();
                
                long t1 = System.nanoTime();
                
                DataList.normalizeInput(list, dc6normalizer);
                long n1 = System.nanoTime();
                normTime += n1-t1;
                
                int trueIndex = trueIndex(list);
                
                long t2 = System.nanoTime();
                this.classifierEvaluate(model, list);
                long n2 = System.nanoTime();
                
                evalTime += (n2-t1);
                int maxIndex = highestIndex(list);
                
                if(trueIndex>=0){
                    hTrue.incrementBinContent(i);
                }
                
                if(trueIndex>=0&&maxIndex>=0){
                    double[] meansTrue = list.getList().get(trueIndex).getFirst();
                    double[] meansHigh = list.getList().get(maxIndex).getFirst();
                    
                    int shareCount = Combinatorics.shareCount(meansHigh, meansTrue);
                    //if(shareCount!=6){
                        //System.out.println("event # " + k);
                        //System.out.println("::: true index = " + trueIndex 
                        //        + " high index = " + maxIndex + "  share = " + shareCount);
                        //list.show();
                    //}
                   
                //} else {
                
                if(trueIndex==maxIndex){
                    hTruePositive.incrementBinContent(i); 
                } else {
                    //System.out.println("event # " + k);
                    //System.out.println("::: true index = " + trueIndex 
                    //        + " high index = " + maxIndex + "  share = " + shareCount);
                    //list.show();
                    if(shareCount>0){
                        hFalsePositive.incrementBinContent(i);
                    } else { hTrueNegative.incrementBinContent(i);}
                    
                } 
                
                
              }
            }
        }
        System.out.printf("timess = read %d norm %.1f eval %.1f, count = %d\n",
                readTime,normTime*1e-6,evalTime*1e-6,candidateCount);
        
        H1F hEfficiencyPos = H1F.divide(hFalsePositive, hTruePositive);
        hEfficiencyPos.attr().set("fc=5,fs=14,lc=5");
        hEfficiencyPos.setName("hEffciencyPos");
        
         H1F hEfficiencyNeg = H1F.divide(hFalseNegative, hTruePositive);
        hEfficiencyNeg.attr().set("fc=5,fs=14,lc=5");
        hEfficiencyNeg.setName("hEffciencyNeg");
        
        TDirectory dir = new TDirectory();
        DataGroup group = new DataGroup(2,2);
        group.setName("classifier");
        
        group.add(hEfficiencyPos, 3, "");
        //group.add(hEfficiencyNeg, 5, "");
        
        group.add(hTrue, 0, "");
        group.add( hTruePositive, 0, "same");
        
        group.add(hFalsePositive, 1, "");
        group.add( hTrueNegative, 2, "same");
       // group.add(hFalseNegative, 2, "");
        //group.add( hTrueNegative, 3, "");
        
        DataSetSerializer.exportDataGroup(group, archive, dataDir);
        //dir.add(dataDir, hFalsePositive);
        //dir.add(dataDir, hTruePositive);
        //dir.add(dataDir, hEfficiency);
        //dir.write(archive);
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
    
    private int trueIndex(DataList list){
        int index = -1;
        for(int i = 0; i < list.getList().size(); i++){
            if(list.getList().get(i).getSecond()[0]<0.5) index = i;
        }
        return index;
    }
    
    private void classifierEvaluate(EJMLModel model, DataList dl){
        for(int i = 0; i < dl.getList().size(); i++){
            float[] result = new float[3];
            float[] input  = dl.getList().get(i).floatFirst();
            model.getOutput(input, result);
            dl.getList().get(i).setInfered(result);
        }
    }
    
    
    public void regressionTrain4(String hipoFile, String testFile,  String archive, int run, String flavor){
        
        DataList data = DataProvider.readRegressionPositive4(hipoFile, constrain, 2, maxBinsRead);
        data.show();
        DataList.normalizeInput(data, dc6normalizer);
        DataList.normalizeOutput(data, regression4normalizer);
        data.shuffle();
        data.shuffle();
        data.show();
        data.scan();
        
        DeepNettsRegression net = new DeepNettsRegression(ActivationType.TANH, ActivationType.LINEAR);
        net.init(new int[]{6,12,12,12,12,12,4});
        
        net.train(data, nEpochs);                
        net.evaluate(data);        
        data.show();
        
        List<String>  networkContent = net.getNetworkStream();
        String archiveFile = String.format("network/%d/%s/trackRegression4_pos_2.network",run,flavor);
        ArchiveUtils.writeFile(archive, archiveFile, networkContent);
    }
     
    public void regressionTrain(String hipoFile, String testFile,  String archive, int run, String flavor){
                
        for(int sector = 1; sector <= 1; sector++){
            DataList data = DataProvider.readRegressionPositive(hipoFile, constrain, sector, maxBinsRead);
            data.show();
            DataList.normalizeInput(data, dc6normalizer);
            DataList.normalizeOutput(data, regression3normalizer);
            data.shuffle();
            data.shuffle();
            data.show();
            data.scan();
            DeepNettsRegression net = new DeepNettsRegression(ActivationType.TANH, ActivationType.LINEAR);
            net.init(new int[]{6,12,12,12,12,12,3});
            net.train(data, nEpochs);                
            net.evaluate(data);        
            data.show();
            
            List<String>  networkContent = net.getNetworkStream();
            String archiveFile = String.format("network/%d/%s/trackRegression_pos_%d.network",run,flavor,sector);
            ArchiveUtils.writeFile(archive, archiveFile, networkContent);
        }
        /*
            DataList data2 = DataProvider.readRegressionNegative(hipoFile, constrain, 2, maxBinsRead);
            data2.show();
            DataList.normalizeInput(data2, dc6normalizer);
            DataList.normalizeOutput(data2, this.r3n_negative);
            data2.shuffle();
            data2.show();
            data2.scan();
            DeepNettsRegression net2 = new DeepNettsRegression(ActivationType.TANH, ActivationType.LINEAR);
            net2.init(new int[]{6,12,12,12,12,12,3});
            
            net2.train(data2, nEpochs);                
            net2.evaluate(data2);        
        data2.show();
        List<String>  networkContent2 = net2.getNetworkStream();
        String archiveFile2 = String.format("network/%d/%s/trackRegression_neg_2.network",run,flavor);
        ArchiveUtils.writeFile(archive, archiveFile2, networkContent2);
        //this.regressionTestDebug(net, testFile, archive, run, flavor);*/
    }
    
    public void regressionTest(String hipoFile, String archive, int run, String flavor){
        
        
        for(int sector = 1; sector <= 1; sector++){
            String archiveFile = String.format("network/%d/%s/trackRegression_pos_%d.network",run,flavor,sector); 
            String     dataDir = String.format("network/%d/%s",run,flavor);
        
            List<String> networkContent = ArchiveUtils.getFileAsList(archive,archiveFile);
        
        
            EJMLModel model = EJMLModel.create(networkContent);
            model.setType(EJMLModel.ModelType.TANH_LINEAR);
            
            
            DataList list = DataProvider.readRegressionPositive(hipoFile, constrain, sector, maxBinsRead);
            list.show();
            DataList.normalizeInput(list, dc6normalizer);
            DataList.normalizeOutput(list, this.regression3normalizer);
            list.show();
            list.scan();
        
            long then = System.nanoTime();

            for(int loop = 0; loop < list.getList().size(); loop++){
                float[] input = list.getList().get(loop).floatFirst();
                float[] result = new float[3];
                model.getOutput(input, result);
                list.getList().get(loop).setInfered(result);
            }
            
            long now = System.nanoTime();
            
            list.show();
        
            H2F hmom = new H2F("hmom_"+sector,80,0.5,10.5,80,-0.2,0.2);
            H2F hthe = new H2F("hthe_"+sector,80,0.0,40.0,80,-2.5,2.5);
            H2F hphi = new H2F("hphi_"+sector,80,40,80,80,-4.5,4.5);
            
            DataList.denormalizeOutput(  list, regression3normalizer);
            DataList.denormalizeInfered( list, regression3normalizer);
            
            for(int loop = 0; loop < list.getList().size(); loop++){
                float[] output = list.getList().get(loop).floatSecond();
                float[] infered = list.getList().get(loop).getInfered();
                hmom.fill(output[0], (output[0]-infered[0])/output[0]);
                hthe.fill(
                        Math.acos(output[1])*57.29, 
                        (Math.acos(output[1])-Math.acos(infered[1]))*57.29);
                hphi.fill(output[2], (output[2]-infered[2]));
            }
            TDirectory dir = new TDirectory();
            dir.add("/"+dataDir, hmom);
            dir.add("/"+dataDir, hthe);
            dir.add("/"+dataDir, hphi);
            dir.write(archive);            
        }
    
    }
    
    public void regressionTestDebug(DeepNettsRegression network,String hipoFile, String archive, int run, String flavor){
        
        String archiveFile = String.format("network/%d/%s/trackRegression_neg_2.network",run,flavor); 
        String     dataDir = String.format("network/%d/%s",run,flavor);
        
        List<String> networkContent = ArchiveUtils.getFileAsList(archive,archiveFile);
        
        EJMLModel model = EJMLModel.create(networkContent);
        model.setType(EJMLModel.ModelType.TANH_LINEAR);
        
        DataList list = DataProvider.readRegressionPositive(hipoFile, constrain, 2, maxBinsRead);
        list.show();
        DataList.normalizeInput(list, dc6normalizer);
        DataList.normalizeOutput(list, regression3normalizer);
        list.show();
        list.scan();
        
        network.evaluate(list);
        
        list.show();
        
        H2F hmom = new H2F("hmom_nn",80,0.5,10.5,80,-0.2,0.2);
        H2F hthe = new H2F("hthe_nn",80,0.0,0.25,80,-2.5,2.5);
        H2F hphi = new H2F("hphi_nn",80,35,125,80,-2.5,2.5);
        
        DataList.denormalizeOutput(list, regression3normalizer);
        DataList.denormalizeInfered(list, regression3normalizer);
        
        for(int loop = 0; loop < list.getList().size(); loop++){
            float[] output = list.getList().get(loop).floatSecond();
            float[] infered = list.getList().get(loop).getInfered();
            hmom.fill(output[0], (output[0]-infered[0])/output[0]);
            hthe.fill(output[1], (output[1]-infered[1]));
            hphi.fill(output[2], (output[2]-infered[2]));
        }
        TDirectory dir = new TDirectory();
        dir.add("/"+dataDir, hmom);
        dir.add("/"+dataDir, hthe);
        dir.add("/"+dataDir, hphi);
        dir.write(archive);
    }
}
