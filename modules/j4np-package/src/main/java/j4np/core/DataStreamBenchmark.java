/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.core;

import j4np.data.evio.EvioStreamDebug;

/**
 *
 * @author gavalian
 */
public class DataStreamBenchmark {
    public static void main(String[] args){
        String filename = "/Users/gavalian/Work/DataSpace/evio/clas_003852.evio.981";
        if(args.length>0){
            filename = args[0];
        }        
        EvioStreamDebug.EvioEventConsumer consumer = new EvioStreamDebug.EvioEventConsumer();
        //EvioStreamDebug.benchmarkEvioStream(filename, consumer, 20, 4);
        EvioStreamDebug.benchmarkEvioStream(filename, consumer);
    }
}
