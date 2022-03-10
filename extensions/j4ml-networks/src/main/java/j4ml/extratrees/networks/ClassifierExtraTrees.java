/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.extratrees.networks;

import j4ml.examples.DataRow;
import j4ml.extratrees.ExtraTrees;
import j4ml.extratrees.data.Matrix;
import j4np.utils.io.DataPairList;
import j4np.utils.io.DataPair;
import j4np.utils.io.DataArrayUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import twig.data.H1F;
import twig.data.TDirectory;

/**
 *
 * @author gavalian
 */
public class ClassifierExtraTrees {
    
    private int        nInputs = 0; 
    private int       nOutputs = 0;
    
    ExtraTrees            tree = null;
    TDirectory   evaluationDir = null;
    
    
    private  int     nMin = 25;
    private  int     K    = 5;
    private  int numTrees = 50;
    
    private String outputFilename = "extraTreeOutput.twig";
    
    Logger logger = Logger.getLogger(ClassifierExtraTrees.class.getName());
    
    
    public ClassifierExtraTrees(){
        
    }
    
    public ClassifierExtraTrees(int nIn, int nOut){
        nInputs = nIn; nOutputs = nOut;
    }
    
    public final ClassifierExtraTrees setInputs(int n){ nInputs = n; return this;}
    public final ClassifierExtraTrees setOutputs(int n){nOutputs = n; return this;}
    
    public final ClassifierExtraTrees setOutputFile(String file){
        this.outputFilename = file; return this;
    }
    
    public void setNumTrees(int ntree){
        this.numTrees = ntree;
    }
    
    public void setK(int _k){
        K = _k;
    }
    
    public void setNMin(int nmin){
        nMin = nmin;
    }
    
    public ExtraTrees getTree(DataPairList list){
        
        int ndata = list.getList().size();
        int ndim  = nInputs;
        
        double[] output = new double[ndata];
        double[] v = new double[ndata*ndim];                
        Matrix m = new Matrix(v, ndata, ndim);
        
        for(int row = 0; row < ndata; row++){
            double[] first = list.getList().get(row).getFirst();
            double[] second = list.getList().get(row).getSecond();
            for(int col = 0; col < 6; col++){
                m.set(row, col, first[col]);
            }
            
            output[row] = second[0];
        }
        logger.info(
                String.format("\ncreated tree : inputs = %d, outputs = %d, dimension = %dx%d\n",
                        nInputs,nOutputs,ndata,ndim));        
        return new ExtraTrees(m,output);
    }
    
    public void train(DataPairList list){
        long then = System.currentTimeMillis();
        
        tree = this.getTree(list);
        tree.learnTrees(nMin, K, numTrees);
        long now = System.currentTimeMillis();
        //System.out.println(tree);
        logger.info(String.format("training time : %d ms", now-then));
    }
    
    public double evaluate(double[] input){
        double value  = tree.getValue(new DataRow(input));
        return value;
    }
    
    public DataPairList evaluate(DataPairList list){
        DataPairList results = new DataPairList();
        for(int i = 0; i < list.getList().size(); i++){
            double[] input = list.getList().get(i).getFirst();
            double   value = this.evaluate(input);
            results.add(new DataPair(DataArrayUtils.copy(input),new double[]{value}));
        }
        return results;
    }
    
    public void test(DataPairList list){
        
        int nrows = list.getList().size();
        int[][] matrix = new int[2][2];
        
        double error = 0.0;
        evaluationDir = new TDirectory();
        
        H1F hconf = new H1F("hconf",4,0.5,4.5);
        long then = System.currentTimeMillis();
        for(int i = 0; i < nrows; i++){
            
            double[] first = list.getList().get(i).getFirst();
            double[] second = list.getList().get(i).getSecond();
            
            DataRow row   = new DataRow(first); 
            double value  = tree.getValue(row);
        
            if(second[0]>0.5){
                if(value>=0.5) hconf.fill(4); else hconf.fill(3);
            } else {
                if(value<0.5) hconf.fill(1); else hconf.fill(2);
            }
            //System.out.printf("%9.5f %9.5f %9.5f %s\n",second[0],value,
            //        second[0]-value, Arrays.toString(first));
            //error += Math.abs(second[0]-value);
        }
        long now = System.currentTimeMillis();
        logger.log(Level.INFO, ">>>>> confusion matrix\n{0}{1}", 
                new Object[]{String.format("negatives (true | false) %12d | %12d\n",
                (int) hconf.getBinContent(0),(int) hconf.getBinContent(1)), 
                    String.format("positives (true | false) %12d | %12d\n",
                            (int) hconf.getBinContent(2),(int) hconf.getBinContent(3))});
        //System.out.printf("\n\n Absolute Error %.6f, relative = %.6f\n",
        //        error,error/nrows);
        evaluationDir.add("/extratree/confmatrix", hconf);
        evaluationDir.write(outputFilename);
        logger.info(String.format("evaluatetion time : %d ms for %d rows, average %.2f", 
                now-then,nrows, ((double) (now-then))/nrows  ));
        
    }
    
    public void export(String filename){
        
        logger.log(Level.INFO, "writing network to file => {0}", filename);
        FileOutputStream f = null;
        try {
            f = new FileOutputStream(new File(filename));
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(tree);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClassifierExtraTrees.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClassifierExtraTrees.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                f.close();
            } catch (IOException ex) {
                Logger.getLogger(ClassifierExtraTrees.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void load(String filename){
        logger.log(Level.INFO, "loading network from file => {0}", filename);
        FileInputStream fi = null;
        try {
            fi = new FileInputStream(new File(filename));
            ObjectInputStream oi = new ObjectInputStream(fi);
            tree = (ExtraTrees) oi.readObject();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClassifierExtraTrees.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClassifierExtraTrees.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClassifierExtraTrees.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fi.close();
            } catch (IOException ex) {
                Logger.getLogger(ClassifierExtraTrees.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
