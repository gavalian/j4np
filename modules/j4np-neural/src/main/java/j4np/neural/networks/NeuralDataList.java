/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package j4np.neural.networks;

/**
 *
 * @author gavalian
 */
public interface NeuralDataList {
    public   int size();
    public void  getInput(float[] input, int row);
    public void  applyOutput(float[] output, int row);
    public void  show();
}
