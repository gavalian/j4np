/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.examples;

import j4ml.extratrees.data.Row;



/**
 *
 * @author gavalian
 */
public class DataRow implements Row {
    double[] data = null;
    
    public DataRow(double[] input){
        data = input;
    }
    
    @Override
    public double get(int col) {
        return data[col];
    }
    
}
