/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.data;

/**
 *
 * @author gavalian
 */
public class ConfusionMatrix {
    int[][]  matrix = null;
    int  labelCount;
    public ConfusionMatrix(int size){
        matrix = new int[size][size];
        labelCount = size;
    }
    
    public void apply(float[] desired, float[] output){
        int desbin = DataEntry.getLabelClass(desired);
        int outbin = DataEntry.getLabelClass(output);
        matrix[desbin][outbin]++;
    }
    
    public void apply(DataList list){
        for(int k = 0; k < list.getList().size(); k++){
            this.apply(list.getList().get(k).labels(),list.getList().get(k).getInfered());
        }
    }
    
    private int getCountInRow(int row){
        int sum = 0;
        for(int i = 0; i < matrix[row].length; i++)
            sum += matrix[row][i];
        return sum;
    }
    public int[][] getMatrix(){ return matrix; }
    
    public double[][] getConfusionMatrix(){
        double[][] mc = new double[labelCount][labelCount];
        for(int r = 0; r < labelCount; r++){
            int count = getCountInRow(r);
            for(int c = 0; c < labelCount; c++)
                mc[r][c] = (double) (100.0*matrix[r][c])/count;
        }
        return mc;
    }
    
    public double[] getConfusionMatrixFlat(){
        double[][] mc = this.getConfusionMatrix();
        double[] mcflat = new double[labelCount*labelCount];
        int index = 0;
        for(int r = 0; r < labelCount; r++){
            int count = getCountInRow(r);
            for(int c = 0; c < labelCount; c++){
                mcflat[index] = mc[r][c];
                index++;
            }
        }
        return mcflat;
    }
}
