/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.trainers;

import deepnetts.net.layers.activation.ActivationType;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.deepnetts.DeepNettsNetwork;
import j4ml.ejml.EJMLModel;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.neural.data.TrackCondition;
import j4np.neural.data.Tracks;
import j4np.neural.data.TrackReader;
import j4np.physics.Vector3;
import j4np.utils.base.ArchiveUtils;
import j4np.utils.io.TextFileWriter;
import java.util.Arrays;
import java.util.List;
import twig.data.H2F;
import twig.data.TDirectory;

/**
 *
 * @author gavalian
 */
public class RegressionTrainer {
    
    public String networkFile = "clas12default.network";
    public int            run = 1;
    public int        nEpochs = 750;
    public int      maxEvents = 15000;
    
    
    double[]   rotateMatrix = new double[]{0.0,-60.0,-120.,-180.0,-240.0,-300.0};
    
    double[]   normalizePosMin = new double[]{ 0., 0.0,  -1.5};
    double[]   normalizePosMax = new double[]{10., 1.0,   0.5};
    
    double[]   normalizeNegMin = new double[]{0.,  0.0 , -0.5};
    double[]   normalizeNegMax = new double[]{10., 1.0  , 1.5};
    
    public RegressionTrainer(){
        
    }
    
    protected void transform(Vector3 v, int sector){
        v.rotateZ(Math.toRadians(rotateMatrix[sector-1]));
    }
    
    protected double   getNormalized(double x, double min, double max){
        return (x-min)/(max-min);
    }
    
    protected boolean checkOutput(float[] output){
        if(output[0]>1.0||output[0]<0.0) return false;
        if(output[1]>1.0||output[1]<0.0) return false;
        return (output[2]>0.0&&output[2]<1.0);
        //return (output[1]>0.0&&output[1]<1.0);
    }
    
    protected float[]  getOutput(Vector3 v, int sector, int charge){
        float[] output = new float[3];
        this.transform(v, sector);
        if(charge>0){
            output[0] = (float) getNormalized(v.mag(),normalizePosMin[0],normalizePosMax[0]);
            output[1] = (float) getNormalized(v.theta(),normalizePosMin[1],normalizePosMax[1]);
            output[2] = (float) getNormalized(v.phi(),normalizePosMin[2],normalizePosMax[2]);
        } else {
            output[0] = (float) getNormalized(v.mag(),normalizeNegMin[0],normalizeNegMax[0]);
            output[1] = (float) getNormalized(v.theta(),normalizeNegMin[1],normalizeNegMax[1]);
            output[2] = (float) getNormalized(v.phi(),normalizeNegMin[2],normalizeNegMax[2]);
        }
        
        return output;
    }
    
    protected DataList getDataSet(String file, int pBin, int sector, int charge, int max){
        HipoReader r = new HipoReader();
        r.setDebugMode(0);
        r.setTags((long) pBin);
        r.open(file);
        int counter = 0;
        Event e = new Event();
        Tracks list = new Tracks();
        DataList dataList = new DataList();
        Vector3 v = new Vector3();
        while(r.hasNext()&&counter<max){
            r.nextEvent(e);
            //TrackReader.event2track(e, list);
            e.read(list.dataNode(), 32000, 1);
            if(TrackCondition.isValid(list, 0)==true&&list.sector(0)==sector){
                list.vector(v, 0);
                //list.dataNode().print();
                float[] input = new float[6]; list.getInput(input, 0);
                int ch = list.charge(0);
                float[] output = this.getOutput(v, sector, charge);
                
                if(checkOutput(output)==true){
                    dataList.add(new DataEntry(input,output));
                    counter++;
                }
            }
        }
        return dataList;
    }
    
    public void evaluate(String file, int sector, int charge, int maxEvents){
        DataList positive = new DataList();
        int start = 1; if(charge>0) start = 21;
        for(int i = start; i < start+20; i++){
            DataList list = this.getDataSet(file, i+1, sector, charge, maxEvents);
            positive.getList().addAll(list.getList());
        }
        
        positive.show();
        positive.scan();
        positive.shuffle();
        
        String archiveFile = String.format("network/%d/%s/%d/%s/regression.network",
                run,"default",sector,getNameForCharge(charge)); 

                
        List<String> networkContent = ArchiveUtils.getFileAsList(networkFile,archiveFile);
        
        String directory = String.format("network/%d/%s/%d/%s",run,"default",sector,getNameForCharge(charge));
        
        EJMLModel model = EJMLModel.create(networkContent);
        System.out.println(model.summary());
        H2F hP     = new H2F("hP",    120,0.0,1.0,240,-0.4,0.4);
        H2F hTheta = new H2F("hTheta",120,0.0,1.0,240,-0.4,0.4);
        H2F hPhi   = new H2F("hPhi",  120,0.0,1.0,240,-0.4,0.4);
        
        model.setType(EJMLModel.ModelType.TANH_LINEAR);
        //float[] results = new float[3];
        for(int i = 0; i < positive.getList().size(); i++){
            float[] input = positive.getList().get(i).features();
            float[] desired = positive.getList().get(i).labels();
            //model.feedForwardTanhLinear(input, results);
            float[] results = new float[3];
            
            model.feedForwardTanhLinear(input, results);
            
            positive.getList().get(i).setInfered(results);
            hP.fill(     desired[0], (desired[0]-results[0])/desired[0]);
            hTheta.fill( desired[1], (desired[1]-results[1]));
            hPhi.fill(   desired[2], (desired[2]-results[2]));
//System.out.println(Arrays.toString(desired) + "  ==>>>> " + Arrays.toString(results));
        }
        TDirectory.export(networkFile, directory, hP);
        TDirectory.export(networkFile, directory, hTheta);
        TDirectory.export(networkFile, directory, hPhi);
        //positive.export("evaluate_output.csv");
    }
    
    private String getNameForCharge(int charge){
        if(charge>0) return "p";
        return "n";
    }
    
    public void trainFile(String file,int sector, int charge, int maxEvents){
        
        System.out.printf("NEURAL NETWORK: training sector = %d, charge = %d\n",sector,charge);
        int start = 1; if(charge>0) start = 21;
        DataList positive = new DataList();
        
        for(int i = start; i < start+20; i++){
            DataList list = this.getDataSet(file, i, sector, charge, maxEvents);
            positive.getList().addAll(list.getList());
        }
        
        positive.show();
        positive.scan();
        positive.shuffle();
        
        DeepNettsNetwork encoder = new DeepNettsNetwork();
        encoder.activation(ActivationType.TANH)
                .outputActivation(ActivationType.LINEAR);
                //.lossType(LossType.MEAN_SQUARED_ERROR);
        
        encoder.init(new int[]{6,12,12,12,3});
        
        encoder.train(positive, nEpochs);
        
        List<String>  networkContent = encoder.getNetworkStream();
        String archiveFile = String.format("network/%d/%s/%d/%s/regression.network",
                run,"default",sector,getNameForCharge(charge));
        ArchiveUtils.writeFile(networkFile, archiveFile, networkContent); 
        
        List<String>    jsonContent = Arrays.asList(encoder.getJson());
        
        /*TextFileWriter.writeFile("regression.json", jsonContent);
        if(evFile!=null){
            DataList testing = new DataList();
        
            for(int i = 0; i < 20; i++){
                DataList list = this.getDataSet(evFile, i+1, 1, 2500);
                testing.getList().addAll(list.getList());
            }
            testing.shuffle();
            testing.show();
            encoder.evaluate(testing);
            testing.show();
            testing.export("regression_output.csv");
        }
        */
        
    }
    
    public void trainAndValidate(String trFile, String vaFile){
        for(int s = 1 ; s <=6 ; s++){
            trainFile(trFile, s,   1, this.maxEvents);
            evaluate( vaFile, s,   1, this.maxEvents);
            trainFile(trFile, s,  -1, this.maxEvents);
            evaluate( vaFile, s,  -1, this.maxEvents);
        }
    }
    
    public void train(DataList data){
        
    }
    
    public static void main(String[] args){
        String file1 = "clas12_neural_data_tr.h5";
        String file2 = "clas12_neural_data_va.h5";
        
       
        
        RegressionTrainer t = new RegressionTrainer();
        t.nEpochs = 125;
        
        for(int sector = 1; sector<=6; sector++){
            //if(sector!=3){
                t.trainFile(file1, sector,  1, 15000);
                t.trainFile(file1, sector, -1, 15000);
                t.evaluate( file2, sector,  1, 15000);
                t.evaluate( file2, sector, -1, 15000);
            //}
        }
        //t.trainFile(file1, 2,  1, 15000);
        //t.trainFile(file1, 2, -1, 15000);
        
        t.evaluate(file1, 2,  1, 15000);
        t.evaluate(file1, 2, -1, 15000);
       // t.evaluate(file1);
        //DataList list = t.getDataSet(file, 1, 1, 40);
        //list.show();
        //list.scan();
    }
}
