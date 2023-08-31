/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.networks;

import j4ml.ejml.EJMLModel;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.io.HipoReader;
import j4np.physics.Vector3;
import j4np.utils.base.ArchiveProvider;
import j4np.utils.base.ArchiveUtils;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class NeuralRegression {
    
    private   EJMLModel[] models = new EJMLModel[12];
    private        Schema tracks = null;
    private        Schema particles = null;
    
    double[]   rotateMatrix = new double[]{0.0,-60.0,-120.,-180,-240.0,-300.0};
    
    double[]   normalizePosMin = new double[]{ 0., 0.0,  -1.5};
    double[]   normalizePosMax = new double[]{10., 1.0,   0.5};
    
    double[]   normalizeNegMin = new double[]{0.,  0.0 , -0.5};
    double[]   normalizeNegMax = new double[]{10., 1.0  , 1.5};
    
    public NeuralRegression(){
        
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
        
        Bank bt = new Bank(tracks,100);        
        e.read(bt);
        Bank bp = new Bank(particles,bt.getRows());
        //bt.show();
        float[] means = new float[6];
        float[] output = new float[3];
        
        for(int i = 0; i < bp.getRows(); i++){
            int charge = bt.getInt("charge", i);
            int sector = bt.getInt("sector", i);
            int offset = 0; if(charge<0) offset = 6;
            
            int networkIndex = (sector - 1) + offset;
            //System.out.println(" index " + networkIndex + "  s/c " + sector + "  " + charge);
            for(int m = 0; m < 6; m++) means[m] = bt.getFloat(m+10, i);
            models[networkIndex].feedForwardTanhLinear(means, output);
            Vector3 v = getVector(sector,charge,output);
            bp.putShort("id", i, (short) i);
            if(charge<0)
                bp.putInt("pid", i, 11); 
            else bp.putInt("pid", i,  211);
            
            bp.putShort("charge", i, (short) charge);
            bp.putFloat("px", i, (float) v.x());
            bp.putFloat("py", i, (float) v.y());
            bp.putFloat("pz", i, (float) v.z());
            bp.putInt("status", i, 2200);
        }
        
        e.write(bp);
    }
    
    public static void main(String[] args){
        NeuralRegression r = new NeuralRegression();
        r.loadFromFile("clas12default.network", 4451);
        
    }
}
