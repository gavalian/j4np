/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.deepnetts.ejml;

/**
 *
 * @author gavalian
 */
public class ClassifierEvaluate {
    
    private int[][] matrix = null;
    
    public ClassifierEvaluate(int nclasses){
        matrix = new int[nclasses][nclasses];
        for(int i = 0; i < nclasses; i++)
            for(int j = 0; j < nclasses; j++)
                matrix[i][j] = 0;
    }
    
    public int getClass(float[] output){
        double  max = output[0]; 
        int    item = 0; 
        for(int i = 0; i < output.length; i++){
            if(output[i]>max){
                max = output[i]; item = i;
            }
        }
        return item;
    }
    
    public void process(float[] desired, float[] output){
        int c_d = getClass(desired);
        int c_o = getClass(output);
        matrix[c_d][c_o]++;
    }
    
    public void show(){
        StringBuilder str = new StringBuilder();
        int nrows = matrix[0].length;
        for(int row = 0; row < nrows; row++ ){
            for(int col = 0; col < nrows; col++){
                str.append(String.format("%10d"
                        , matrix[row][col]));
            } str.append("\n");
        }
        
        System.out.println("CONFUSION MATRIX: ");
        System.out.println(str.toString());
    }
}
