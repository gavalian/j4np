/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.networks;

import j4ml.data.DataList;

/**
 *
 * @author gavalian
 */
abstract public class AbsNeuralNetwork {
    
    protected int  nInputs = 0;
    protected int nOutputs = 0;
    
    public AbsNeuralNetwork(){
        
    }
    public int getInputSize(){ return nInputs;}
    public int getOutputSize(){return nOutputs;}
    
    abstract void train(DataList list, int epochs);
    abstract void test(DataList list);
    
    abstract void evaluate(DataList list);
    abstract void save(String filename);
    abstract void load(String filename);    
    abstract void getOutput(float[] input, float[] output);
    
    
}
