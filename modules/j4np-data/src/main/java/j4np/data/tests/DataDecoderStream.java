/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.tests;

import j4np.data.base.DataStream;
import j4np.data.evio.EvioEvent;
import j4np.data.evio.EvioFile;

/**
 *
 * @author gavalian
 */
public class DataDecoderStream {
    public static void main(String[] args){
        String filename = "/Users/gavalian/Work/DataSpace/evio/clas_011878.evio.00001";
        
        /*DataStream stream = new DataStream<EvioFile,EvioEvent>(new EvioFile(),new EvioEvent(),200);        
        stream.process(filename);
        
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("cores = " + cores);*/
    }
}
