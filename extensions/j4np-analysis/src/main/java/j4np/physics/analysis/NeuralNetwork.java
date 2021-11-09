/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics.analysis;

import j4np.physics.Vector3;
import java.util.Arrays;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.Node;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.hipo4.io.HipoWriter;
import org.jlab.jnp.utils.benchmark.ProgressPrintout;

/**
 *
 * @author gavalian
 */
public class NeuralNetwork {
    
    /**
     *
     * @param args
     */
    public static void main(String[] args){
        
        HipoChain chain = new HipoChain();
        
        /*chain.addFile("/Users/gavalian/Work/DataSpace/pid/mc_epipip_0001.hipo");
        chain.addFile("/Users/gavalian/Work/DataSpace/pid/mc_epipip_0002.hipo");
        chain.addFile("/Users/gavalian/Work/DataSpace/pid/mc_epipip_0003.hipo");
        chain.addFile("/Users/gavalian/Work/DataSpace/pid/mc_epipip_0004.hipo");
        chain.addFile("/Users/gavalian/Work/DataSpace/pid/mc_epipip_0005.hipo");
        */
        
        chain.addDir("/Users/gavalian/Work/DataSpace/pid/mcprocessed/", "*redu*.hipo");
        chain.open();
        
        ClasEvent clas = ClasEvent.with(chain);
        clas.setEventType(ClasEvent.EventType.FWD_TRIGGER);
        ClasCalorimeter calo = ClasCalorimeter.with(chain);
        
        Event event = new Event();
        Event outEvent = new Event();
        
        Vector3 v1 = new Vector3();
        Vector3 v2 = new Vector3();
        Vector3 v3 = new Vector3();
        
        HipoWriter writer = new HipoWriter();
        writer.open("pid_reduced_output.hipo");
        
        ProgressPrintout progress = new ProgressPrintout();
        
        while(chain.hasNext()==true){
            progress.updateStatus();
            chain.nextEvent(event);
            clas.read(event);
            calo.read(event);
            int nneg = clas.countByCharge(-1);
            int npos = clas.countByCharge(1);
            
                        
            if(nneg==2&&npos==1){
                
                int index1 = clas.getOrderByCharge(-1, 0);
                int index2 = clas.getOrderByCharge(-1, 1);
                int index3 = clas.getOrderByCharge( 1, 0);
                
                //System.out.printf("positive = %4d, negative = %4d [%4d, %4d]\n",clas.countByCharge(1),
                //        clas.countByCharge(-1), index1, index2);
                //System.out.println(clas.toLundString());
                int pid1 = clas.pid(index1);
                int pid2 = clas.pid(index2);
                //double[] r1 = calo.getResponse(index1);
                //double[] r2 = calo.getResponse(index2);
                clas.vector(v1, index1);
                clas.vector(v2, index2);
                clas.vector(v3, index3);
                float[] r1 = calo.getResponseFloat(v1.mag(),index1);
                float[] r2 = calo.getResponseFloat(v2.mag(),index2);
                float[] r3 = calo.getResponseFloat(v3.mag(),index3);
                //System.out.printf(" 1 : %5d : %s\n",pid1, Arrays.toString(r1));
                //System.out.printf(" 2 : %5d : %s\n",pid2, Arrays.toString(r2));
                
                Node  vn1 = new Node(100,1,new float[]{(float) v1.x(), (float) v1.y(), (float) v1.z()});
                Node  vn2 = new Node(100,2,new float[]{(float) v2.x(), (float) v2.y(), (float) v2.z()});
                Node  vn3 = new Node(100,3,new float[]{(float) v3.x(), (float) v3.y(), (float) v3.z()});
                
                Node  ids = new Node(80,1,new int[]{clas.pid(index1),clas.pid(index2),clas.pid(index3)});
                
                Node  fn1 = new Node(200,1,r1);
                Node  fn2 = new Node(200,2,r2);
                Node  fn3 = new Node(200,3,r3);

                Node  pn1 = new Node(300,1,calo.getHitPosition(index1));
                Node  pn2 = new Node(300,2,calo.getHitPosition(index2));
                Node  pn3 = new Node(300,3,calo.getHitPosition(index3));
                
                outEvent.reset();
                outEvent.write(ids);

                outEvent.write(vn1);
                outEvent.write(vn2);
                outEvent.write(vn3);
                
                outEvent.write(fn1);
                outEvent.write(fn2);
                outEvent.write(fn3);
                
                outEvent.write(pn1);
                outEvent.write(pn2);
                outEvent.write(pn3);                   
                
                writer.addEvent(outEvent);
            }
            
            
        }
        writer.close();
    }
}
