package j4ml.deepnetts.projects;


import j4ml.deepnetts.ejml.EJMLModelEvaluator;
import j4np.physics.LorentzVector;
import java.util.Arrays;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.hipo4.data.DataType;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.Node;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.hipo4.io.HipoReader;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gavalian
 */
public class PartClassifierTest {
    
    public static double energy(float[] features){
        return (features[1]+features[2]+features[3]);
    }
    
    public static void main(String[] args){
        
        EJMLModelEvaluator model = new EJMLModelEvaluator("network/clas12pid.network");
        H1F h = new H1F("h",60,0.5,1.1);
        H1F ha = new H1F("h",60,0.5,1.1);
        H1F ht = new H1F("h",60,0.5,1.1);
        H1F h1 = new H1F("h",60,0.5,1.1);
        H1F h2 = new H1F("h",60,0.5,1.1);
        
        H2F dr = new H2F("dr",100,-500,500,100,-500,500);
        H2F dp = new H2F("dr",100,-500,500,100,-500,500);
        
        HipoChain reader = new HipoChain();
        //reader.addDir("/Users/gavalian/Work/DataSpace/pid/mcprocessed/", "*redu*.hipo");
        reader.addFile("pid_reduced_output.hipo");
        reader.open();
        
        
        Event event = new Event();

        
        /*Node fn1 = new Node(200,1,DataType.FLOAT,28);
        Node fn2 = new Node(200,2,DataType.FLOAT,28);
        Node fn3 = new Node(200,3,DataType.FLOAT,28);
        Node ids = new Node(80,1,DataType.INT,3);
        */
        
        float[] output = new float[2];
        float[] output2 = new float[2];
        int counter = 0;
        
        while(reader.hasNext()){
            reader.nextEvent(event);
            
            Node ids = event.read(80, 1);
            Node fn1 = event.read(200, 1);
            Node fn2 = event.read(200, 2);
            Node fn3 = event.read(200, 3);
            
            /*System.out.println("-------");
            
            System.out.println(Arrays.toString(fn1.getFloat()));
            System.out.println(Arrays.toString(fn2.getFloat()));
            System.out.println(Arrays.toString(fn3.getFloat()));
            */
            model.feedForwardSoftmax(fn1.getFloat(), output);
            model.feedForwardSoftmax(fn2.getFloat(), output2);
            int[] pids = ids.getInt();
            
            
            if(pids[0]==11){
                
                Node vn1 = event.read(100, 1);
                Node vn2 = event.read(100, 2);
                Node vn3 = event.read(100, 3);
                
                float[] vn1c = vn1.getFloat();
                float[] vn2c = vn2.getFloat();
                float[] vn3c = vn3.getFloat();
                
                LorentzVector beam = LorentzVector.withPxPyPzM(0.0, 0.0, 10.6, 0.0005);
                LorentzVector targ = LorentzVector.withPxPyPzM(0.0, 0.0,  0.0, 0.938);
                
                LorentzVector vL1 = LorentzVector.withPxPyPzM(vn1c[0],vn1c[1],vn1c[2], 0.0005);
                LorentzVector vL2 = LorentzVector.withPxPyPzM(vn2c[0],vn2c[1],vn2c[2], 0.139);
                LorentzVector vL3 = LorentzVector.withPxPyPzM(vn3c[0],vn3c[1],vn3c[2], 0.139);
                
                beam.add(targ).sub(vL1).sub(vL2).sub(vL3);
                //System.out.printf("%8.5f\n",beam.mass());
                h.fill(beam.mass());
                ht.fill(beam.mass());
                if(output[1]>output[0]){ ha.fill(beam.mass()); }
                else { 
                    
                    Node pn1 = event.read(300, 1);
                    
                    float[] pn1f = pn1.getFloat();
                    
                    /*if(beam.mass()>0.95&&beam.mass()<1.05)
                    dp.fill(pn1f[0], pn1f[1]);*/
                    System.out.printf("%8.5f %8.5f %8.5f\n",vL1.p(),vL1.theta()*57.29,
                            vL1.phi()*57.29);
                    System.out.println(Arrays.toString(output) + " ==> " +Arrays.toString(fn1.getFloat()) ); 
                }
            }
            
            if(pids[1]==11){
                Node vn1 = event.read(100, 1);
                Node vn2 = event.read(100, 2);
                Node vn3 = event.read(100, 3);
                
                float[] vn1c = vn1.getFloat();
                float[] vn2c = vn2.getFloat();
                float[] vn3c = vn3.getFloat();
                
                LorentzVector beam = LorentzVector.withPxPyPzM(0.0, 0.0, 10.6, 0.0005);
                LorentzVector targ = LorentzVector.withPxPyPzM(0.0, 0.0,  0.0, 0.938);
                
                LorentzVector vL1 = LorentzVector.withPxPyPzM(vn1c[0],vn1c[1],vn1c[2], 0.139);
                LorentzVector vL2 = LorentzVector.withPxPyPzM(vn2c[0],vn2c[1],vn2c[2], 0.0005);
                LorentzVector vL3 = LorentzVector.withPxPyPzM(vn3c[0],vn3c[1],vn3c[2], 0.139);
                
                beam.add(targ).sub(vL1).sub(vL2).sub(vL3);
                //System.out.printf("%8.5f\n",beam.mass());
                h.fill(beam.mass());
                ht.fill(beam.mass());
                if(output[1]>output[0]){ ha.fill(beam.mass()); }
                else { 
                    System.out.printf("%8.5f %8.5f %8.5f\n",vL1.p(),vL1.theta()*57.29,
                            vL1.phi()*57.29);
                    //System.out.println(Arrays.toString(output) + " ==> " +Arrays.toString(fn1.getFloat()) ); 
                }
            }
            double prob1 = output[1];
            double prob2 = output2[1];
            double en1   = PartClassifierTest.energy(fn1.getFloat());
            double en2   = PartClassifierTest.energy(fn2.getFloat());
            
            if(pids[0]!=11){                 
                if(prob1>0.5&&en1>0.0001){
                    
                    Node vn1 = event.read(100, 1);
                    Node vn2 = event.read(100, 2);
                    Node vn3 = event.read(100, 3);
                    
                    Node pn1 = event.read(300, 1);
                    
                    float[] pn1f = pn1.getFloat();
                    
                    float[] vn1c = vn1.getFloat();
                    float[] vn2c = vn2.getFloat();
                    float[] vn3c = vn3.getFloat();
                    
                    LorentzVector beam = LorentzVector.withPxPyPzM(0.0, 0.0, 10.6, 0.0005);
                    LorentzVector targ = LorentzVector.withPxPyPzM(0.0, 0.0,  0.0, 0.938);
                    
                    LorentzVector vL1 = LorentzVector.withPxPyPzM(vn1c[0],vn1c[1],vn1c[2], 0.0005);
                    LorentzVector vL2 = LorentzVector.withPxPyPzM(vn2c[0],vn2c[1],vn2c[2], 0.139);
                    LorentzVector vL3 = LorentzVector.withPxPyPzM(vn3c[0],vn3c[1],vn3c[2], 0.139);
                    
                    beam.add(targ).sub(vL1).sub(vL2).sub(vL3);
                    //System.out.printf("%8.5f\n",beam.mass());
                    h1.fill(beam.mass());// ht.fill(beam.mass());
                    
                    if(beam.mass()>0.95&&beam.mass()<1.05){
                        dr.fill(pn1f[0], pn1f[1]);
                        counter++;
                        System.out.printf("%8.5f %8.5f\n",pn1f[0],pn1f[1]);
                    }
                    
                    dp.fill(pn1f[0], pn1f[1]);
                }
            }
            
            
            if(pids[1]!=11){
                
                if(prob2>0.5&&en2>0.0001){
                    
                    Node vn1 = event.read(100, 1);
                    Node vn2 = event.read(100, 2);
                    Node vn3 = event.read(100, 3);
                    
                    
                    Node pn1 = event.read(300, 2);
                    
                    float[] pn1f = pn1.getFloat();
                    
                    float[] vn1c = vn1.getFloat();
                    float[] vn2c = vn2.getFloat();
                    float[] vn3c = vn3.getFloat();
                    
                    LorentzVector beam = LorentzVector.withPxPyPzM(0.0, 0.0, 10.6, 0.0005);
                    LorentzVector targ = LorentzVector.withPxPyPzM(0.0, 0.0,  0.0, 0.938);
                    
                    LorentzVector vL1 = LorentzVector.withPxPyPzM(vn1c[0],vn1c[1],vn1c[2], 0.139);
                    LorentzVector vL2 = LorentzVector.withPxPyPzM(vn2c[0],vn2c[1],vn2c[2], 0.0005);
                    LorentzVector vL3 = LorentzVector.withPxPyPzM(vn3c[0],vn3c[1],vn3c[2], 0.139);
                    
                    beam.add(targ).sub(vL1).sub(vL2).sub(vL3);
                    //System.out.printf("%8.5f\n",beam.mass());
                    h2.fill(beam.mass()); 
                    ht.fill(beam.mass());
                    
                    if(beam.mass()>0.95&&beam.mass()<1.05){
                        dr.fill(pn1f[0], pn1f[1]);
                        counter++;
                        System.out.printf("%8.5f %8.5f\n",pn1f[0],pn1f[1]);
                    }
                   /* System.out.println(Arrays.toString(output) 
                            + " }{ " + Arrays.toString(output2) +  " >>>> " 
                            + Arrays.toString(ids.getInt()));    
                    counter++;*/
               
                }
            }
            
        }
        System.out.println("counter = " + counter);
        TCanvas c = new TCanvas("c",800,500);
        ha.setLineColor(3);
        ht.setLineColor(2);
        h1.setLineColor(6);
        h2.setLineColor(3);
        c.divide(2, 2);
        H1F hdiv = H1F.divide(ht, h);
        c.cd(0).draw(h).draw(ht,"same");//.draw(ha,"same");
        c.cd(1).draw(h1).draw(h2,"same");
        c.cd(2).draw(hdiv);
        //c.cd(2).draw(dp);
        c.cd(3).draw(dr);
    }
}
