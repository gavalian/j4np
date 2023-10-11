/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.regression;

import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.deepnetts.DeepNettsNetwork;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.neural.data.Tracks;
import j4np.physics.Vector3;
import j4np.utils.base.ArchiveUtils;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class NeuralVertexTrainer {
    
    double[]   normalizePosMin = new double[]{ 0., 0.0,  -1.5};
    double[]   normalizePosMax = new double[]{10., 1.0,   0.5};    
    double[]   normalizeNegMin = new double[]{0.,  0.0 , -0.5};
    double[]   normalizeNegMax = new double[]{10., 1.0  , 1.5};
    
    public NeuralVertexTrainer(){
        
    }

    public void init(){
        
    }
    
    public void train(DataList list, DataList validate){
        
        DeepNettsNetwork encoder = new DeepNettsNetwork();
        encoder.activation(ActivationType.TANH)
                .outputActivation(ActivationType.LINEAR);
                //.lossType(LossType.MEAN_SQUARED_ERROR);
        
        encoder.init(new int[]{6,12,6,6,1});
        
        encoder.train(list, 128);
        
        encoder.evaluate(validate);
        
        validate.export("vertex_evaluate.csv");
    }
    
    public void train9(DataList list, DataList validate){
        
        DeepNettsNetwork encoder = new DeepNettsNetwork();
        encoder.activation(ActivationType.TANH)
                .outputActivation(ActivationType.LINEAR)
                .lossType(LossType.MEAN_SQUARED_ERROR);
        
        encoder.init(new int[]{9,12,12,12,1});
        
        encoder.train(list, 750);
        
        encoder.evaluate(validate);
        
        validate.export("vertex_evaluate_9.csv");
        
        List<String>  networkContent = encoder.getNetworkStream();
        String archiveFile = String.format("network/%d/%s/1/neg/vertex.network",5197,"default");
        ArchiveUtils.writeFile("clas12default.netowrk", archiveFile, networkContent);
    }
    
    protected void transform(Vector3 v, int sector){
        v.rotateZ(Math.toRadians(rotateMatrix[sector-1]));
    }
    double[]   rotateMatrix = new double[]{0.0,-60.0,-120.,-180.0,-240.0,-300.0};
    protected double   getNormalized(double x, double min, double max){
        return (x-min)/(max-min);
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
    
    public DataList getData9(String file, int max){
        DataList list = new DataList();
        Event e = new Event();
        HipoReader r = new HipoReader(file);
        Tracks tracks = new Tracks();
        int counter = 0;
        while(r.hasNext()==true&&counter<max){
            r.next(e);
            e.read(tracks.dataNode(), 32000, 1);
            
            //tracks.show();
            if(tracks.size()==1){
                int sector = tracks.sector(0);
                int charge = tracks.charge(0);
                if(sector==1&&charge<0){
                    float[] input = new float[6];
                    tracks.getInput(input, 0);
                    
                    Vector3 vec = new Vector3();
                    Vector3 vrt = new Vector3();
                    
                    tracks.vector(vec, 0);
                    tracks.vertex(vrt, 0);
                    if(vrt.z()>-6&&vrt.z()<1){
                        //System.out.println("Z = " + vrt.z());
                        float[] vinput = this.getOutput(vec, 1, -1);
                        float[] cinput = new float[9];
                        for(int i = 0; i < 6; i++) cinput[i] = input[i];
                        for(int i = 0; i < 3; i++) cinput[6+i] = vinput[i];
                        double vrtz = (vrt.z() + 6)/7.0;
                        boolean write = true;
                        for(int i = 0; i < 3; i++) if(vinput[i]<0||vinput[i]>1.0) write = false;
                        if(write) list.add(new DataEntry(cinput,new float[]{(float) vrtz}));
                    }
                    
                }
            }            
            counter++;
        }
        return list;
    }
    
    public DataList getData(String file, int max){
        DataList list = new DataList();
        Event e = new Event();
        HipoReader r = new HipoReader(file);
        Tracks tracks = new Tracks();
        int counter = 0;
        while(r.hasNext()==true&&counter<max){
            r.next(e);
            e.read(tracks.dataNode(), 32000, 1);
            
            //tracks.show();
            if(tracks.size()==1){
                int sector = tracks.sector(0);
                int charge = tracks.charge(0);
                if(sector==1&&charge<0){
                    float[] input = new float[6];
                    tracks.getInput(input, 0);
                    Vector3 vec = new Vector3();
                    Vector3 vrt = new Vector3();
                    
                    tracks.vector(vec, 0);
                    tracks.vertex(vrt, 0);
                    if(vrt.z()>-15&&vrt.z()<5){
                        //System.out.println("Z = " + vrt.z());
                        double vrtz = (vrt.z() + 15)/20.0;
                        list.add(new DataEntry(input,new float[]{(float) vrtz}));
                    }
                    
                }
            }
            
            counter++;
        }
        return list;
    }
    
    public static void main(String[] args){
                
        String file  = "/Users/gavalian/Work/DataSpace/neural/clas_neural_005197_tr.h5";
        String file2 = "/Users/gavalian/Work/DataSpace/neural/clas_neural_005197_va.h5";
        NeuralVertexTrainer tr = new NeuralVertexTrainer();
        
        DataList data = tr.getData9(file,1500000);
        DataList datav = tr.getData9(file2,1500000);
        
        data.scan();
        
        tr.train9(data,datav);
    }
}
