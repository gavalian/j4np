/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.cvt;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.SchemaFactory;
import j4np.physics.Vector3;

/**
 *
 * @author gavalian
 */
public class CvtTrack {
    
    public int[]      bstWires = new int[6*3];
    public int[]     bmtZwires = new int[3];
    public double[]  bmtZfi    = new double[3];
    public Vector3    vector   = new Vector3();
    public Vector3    vertex   = new Vector3();
    
    
    
    public void reset(){    
        for(int i = 0; i < bmtZwires.length; i++) bmtZwires[i] = 0;
        for(int i = 0; i < bstWires.length; i++) bstWires[i] = 0;
    }
    
    
    
    
}
