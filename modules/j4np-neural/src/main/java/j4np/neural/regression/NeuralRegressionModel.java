/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.regression;

import j4ml.ejml.EJMLModel;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.io.HipoReader;
import j4np.physics.Vector3;
import j4np.utils.base.ArchiveProvider;
import j4np.utils.base.ArchiveUtils;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class NeuralRegressionModel {
    
    private   EJMLModel[] models = new EJMLModel[12];
    private        Schema tracks = null;
    private        Schema particles = null;
    
    double[]   rotateMatrix = new double[]{0.0,-60.0,-120.,-180,-240.0,-300.0};
    
    double[]   normalizePosMin = new double[]{ 0., 0.0,  -1.5};
    double[]   normalizePosMax = new double[]{10., 1.0,   0.5};
    
    double[]   normalizeNegMin = new double[]{0.,  0.0 , -0.5};
    double[]   normalizeNegMax = new double[]{10., 1.0  , 1.5};
    
    
    NeuralVertexModel vertexModel = new NeuralVertexModel();
    
    public NeuralRegressionModel(){
        
    }
    public void init(HipoReader r){
        tracks    = r.getSchemaFactory().getSchema("nnet::tracks").copy();
        particles = r.getSchemaFactory().getSchema("nnet::particle").copy();                
    }
    
    public void loadFromFile(String networkFile, int run){
        ArchiveProvider ap = new ArchiveProvider(networkFile);
        int runNumber = ap.findEntry(run);
        System.out.printf(":::: archive provider found run # %d for requested (run=%d)\n",runNumber, run);
        
        String[] charges = new String[]{"p","n"};
        
        for(int c = 0; c < charges.length; c++){
            for(int s = 1; s <= 6; s++){
                String archiveFile = String.format("network/%d/%s/%d/%s/regression.network",
                        runNumber,"default",s,charges[c]); 
                int index = (s-1) + c*6;
                try {
                    List<String> networkContent = ArchiveUtils.getFileAsList(networkFile,archiveFile);
                    System.out.printf(":::: initialising [%3d] sector = %2d (%s) (lines = %4d) from file : %s\n", 
                            index,s,charges[c], networkContent.size(),archiveFile);
                    models[index] =  EJMLModel.create(networkContent);
                } catch (Exception e){
                    System.out.printf(":::: failes  [%3d] sector = %2d (%s) from file : %s\n", 
                            index,s,charges[c], archiveFile);
                }
            }
        }
        
        vertexModel.load(networkFile, run);
        
    }
    
    private Vector3 getVector(int sector, int charge, float[] vec){
        double p = vec[0]*10.0;
        double t = vec[1];
        double f = vec[2];
        if(charge>0){
            f = vec[2]*2 - 1.5;
        } else {
            f = vec[2]*2 - 0.5;
        }
        Vector3 v = new Vector3();
        v.setMagThetaPhi(p, t, f);
        v.rotateZ(-Math.toRadians(rotateMatrix[sector-1]));
        return v;
    }
    
    public void processEvent(Event e){
        
        //Bank bt = new Bank(tracks,100);        
        
        CompositeNode tnode = new CompositeNode(32100,2,"3sf6s6f",128);
        CompositeNode pnode = new CompositeNode(32100,3,"3si7fs",128);
        
        e.read(tnode,32100,2);
        
        //Bank bp = new Bank(particles,bt.getRows());
        //bt.show();
        pnode.setRows(tnode.getRows());
        
        float[] means = new float[6];
        float[] output = new float[3];
        
        float[] outputV = new float[1];
        float[] inputV  = new float[9];
        //tnode.print();
        for(int i = 0; i < tnode.getRows(); i++){

            int sector = tnode.getInt(1, i);
            int charge = tnode.getInt(2, i);

            int offset = 0; if(charge<0) offset = 6;
            
            int networkIndex = (sector - 1) + offset;
            //System.out.println(" index " + networkIndex + "  s/c " + sector + "  " + charge);
            for(int m = 0; m < 6; m++) means[m] = (float) (tnode.getDouble(m+10, i)/112.0);
            //System.out.println("analysing : " + Arrays.toString(means));
            models[networkIndex].feedForwardTanhLinear(means, output);
            Vector3 v = getVector(sector,charge,output);
            pnode.putShort(0, i, (short) i);
            if(charge<0)
                pnode.putInt(  3, i, 11); 
            else pnode.putInt( 3, i,  211);
            
            pnode.putShort(1, i, (short) charge);
            pnode.putShort(2, i, (short) sector);
            
            pnode.putFloat(4, i, (float) v.x());
            pnode.putFloat(5, i, (float) v.y());
            pnode.putFloat(6, i, (float) v.z());
            pnode.putShort( 11, i, (short) 2200);
            
            
            if(sector==1&&charge<0){
                for(int k = 0; k < 6; k++) inputV[k] = means[k];
                for(int k = 0; k < 3; k++) inputV[6+k] = output[k];
                vertexModel.getModel().feedForwardTanhLinear(inputV, outputV);
                pnode.putFloat(9, i, outputV[0]);
                //System.out.println( outputV[0]);
            }
        }
        
        if(tnode.getRows()>0){
            //System.out.println("-------- before");
            //e.scanShow();
            //pnode.print();
            e.write(pnode);
            //System.out.println("-------- after");
            //e.scanShow();
        }
    }
    
    public static void main(String[] args){
        NeuralRegressionModel r = new NeuralRegressionModel();
        r.loadFromFile("clas12default.network", 4451);
        
    }
}
