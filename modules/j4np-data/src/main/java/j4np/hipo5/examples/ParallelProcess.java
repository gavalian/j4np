/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.examples;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Evaluator;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.io.HipoChain;
import j4np.hipo5.io.HipoDataStream;
import j4np.hipo5.io.HipoDataWorker;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class ParallelProcess {
    
    public void processFile(String file, int nThreads){
        
        HipoDataStream stream = new HipoDataStream(file,64);//, "outputfile.h5");
        
        HipoDataWorker worker = new HipoDataWorker(){
            Bank dc = null;
            
            @Override
            public boolean init(HipoChain src) {
                dc = src.getReader().getBank("DC::tdc"); return true;
            }

            @Override
            public void execute(Event e) {
                
                List<Integer> index = new ArrayList<>();
                Bank b = new Bank(dc.getSchema(),500);
                e.read(b);
                for(int i = 0; i < b.getRows(); i++){
                    if(b.getInt("order", i)==0) index.add(i);
                }
                Bank reduced = b.reduce(index);
                e.remove(b.getSchema());
                e.write(reduced);
            }
        
        };
        
        stream.consumer(worker);
        stream.threads(nThreads);stream.run();
        stream.show();
    }
    
    
    public void bankSelector(String file, int nThreads, int limit){
        
        HipoDataStream stream = new HipoDataStream(file,64);
        //H1F h = new H1F("h",120,0.0,10.0);
        HipoDataWorker worker = new HipoDataWorker(){
            Schema[] schemas = null;
            @Override
            public boolean init(HipoChain src) {
                schemas = src.getReader().getSchemas("REC::Particle"); return true;
            }

            @Override
            public void execute(Event e) {

                Evaluator eval = new Evaluator(schemas[0],"sqrt(px*px+py*py+pz*pz)");
                //Bank bc = new Bank(schemas[0]);
                //e.read(bc);
                //bc.show();
                Bank[] b = e.read(schemas);
                int nrows = b[0].getRows();
                //b[0].show();
                //System.out.println(" rows = " + nrows);
                for(int row = 0; row < nrows; row++){
                    double momentum = eval.evaluate(b[0], row);
                    //System.out.printf(" %4d - %8.5f\n",row,momentum);
                    
                }
            }
        
        };
        
        stream.consumer(worker).threads(nThreads).limit(limit);        
        stream.run();
        stream.show();
    
    }
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/DataSpace/rga/rec_005988.00005.00009.hipo";
        //String file = "output.hipo";
        ParallelProcess proc = new ParallelProcess();
        //proc.processFile(file, 8);
        proc.bankSelector(file, 1, -1);
    }
}
