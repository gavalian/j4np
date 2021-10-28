/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics.analysis;

import j4np.physics.LorentzVector;
import j4np.physics.PhysicsReaction;
import j4np.physics.Vector3;
import java.util.ArrayList;
import java.util.List;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.hipo4.io.HipoWriter;
import twig.data.H1F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class ClasAnalysis {
    
    protected List<? extends PhysicsReaction> reactions = new ArrayList<>();
    
    public ClasAnalysis(){
        
    }
    
    
    public static void filter(String file){
        HipoChain chain = new HipoChain();
        chain.addFile(file);
        chain.open();
        
        Bank mc = chain.getBank("MC::Particle");
        
        ClasEvent clasEvent = ClasEvent.with(chain, new String[]{"MC::Particle"});
        Event event = new Event();
        //LorentzVector vE = LorentzVector.withPxPyPzM(0, 0, 10.5, 0.0005);
        //vE.add(0, 0, 0, 0.938);
        LorentzVector vCombo = new LorentzVector();

        //H1F hMass = new H1F("hMass",120,0.5,1.05);
        
        while(chain.hasNext()==true){
            chain.nextEvent(event);
            clasEvent.read(event);
            if(clasEvent.countByPid(211)==1&&clasEvent.countByPid(-211)==1&&
                    clasEvent.countByPid(11)==1&&clasEvent.countByPid(2212)==1&&clasEvent.count()==4){
                
                //clasEvent.show();
                //System.out.println(clasEvent.toLundString());
                LorentzVector vE = LorentzVector.withPxPyPzM(0, 0, 10.5, 0.0005);
                vE.add(0, 0, 0, 0.938);
                
                clasEvent.makeVector(vCombo, new int[]{11,211,-211}, 
                        new double[]{0.0005,0.1396,0.1396}, new int[]{0,0,0}, 
                        new int[]{-1,-1,-1});
                vE.add(vCombo);
                if(vE.mass()>0.5&&vE.mass()<1.1){
                    System.out.print(clasEvent.toLundString());
                }
                //System.out.println("mass = " + vE.mass());
                //hMass.fill(vE.mass());
            }
        }
        
        //TGCanvas c = new TGCanvas();
        //c.view().region().draw(hMass);
        //System.out.println("integral = " + hMass.integral());
    }
    
    public static void main(String[] args){
        
        String file = "/Users/gavalian/Work/DataSpace/pid/mc_sidis.hipo";

        ClasAnalysis.filter(file);
        /*
        HipoChain reader = new HipoChain();
        
        if(args.length>0){
            for(int i = 0; i < args.length; i++)
                reader.addFile(args[i]);
        //reader.addFile("/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00055-00059.hipo");
        } else {
            reader.addDir("/Users/gavalian/Work/DataSpace/ml","*rec_clas_*");
        }
        //reader.addDir();
        reader.open();
        
        ClasEvent clasEvent = ClasEvent.with(reader);
        HipoWriter writer = new HipoWriter();
        
        //reader.getSchemaFactory().copy(writer.getSchemaFactory());
        
        writer.getSchemaFactory().copy(reader.getSchemaFactory());
        
        writer.getSchemaFactory().show();
        writer.open("filtered_epiX.hipo");
        
        Event event = new Event();
        
        int counter = 0;
        H1F h1 = new H1F("h1",180,0.0,2.5);
        H1F h2 = new H1F("h2",180,0.0,2.5);
        H1F h3 = new H1F("h3",180,-30,30);
        H1F h4 = new H1F("h4",180,-30,30);
        
        h1.setFillColor(5);
        h2.setFillColor(4);
        h3.setFillColor(5);
        h4.setFillColor(4);
        
        while(reader.hasNext()==true){
            reader.nextEvent(event);
            clasEvent.read(event);
            //clasEvent.show();
            
            //System.out.printf("%9d :  type = %s\n",counter, clasEvent.getEventType());
            counter++;
            if(clasEvent.getEventType()==ClasEvent.EventType.FWD_TRIGGER){
                
                //clasEvent.show();
                if(clasEvent.countByPid(211)==1&&clasEvent.countByPid(2212)==0){
                    //&&clasEvent.countByPid(2212)==1&&clasEvent.countByCharge(-1)<2){
                    //clasEvent.show();
                    LorentzVector vB = LorentzVector.withPxPyPzM(0.0, 0.0, 10.5, 0.0005);
                    LorentzVector vT = LorentzVector.withPxPyPzM(0.0, 0.0, 0.0,0.938);
                    LorentzVector vE = new LorentzVector();
                    LorentzVector vPi = new LorentzVector();
                    clasEvent.vector( vE,  0.0005,  11, 0);
                    clasEvent.vector(vPi, 0.13957, 211, 0);
                    vB.add(vT).sub(vPi).sub(vE);                    
                    Vector3  vrtE = new Vector3();
                    Vector3  vrtPi = new Vector3();
                                        
                    clasEvent.vertex(vrtE, 11,0);
                    clasEvent.vertex(vrtPi, 211,0);

                 
                    if(Math.abs(vrtE.z()-vrtPi.z())<2.0
                            &&vrtE.z()>-15.0&&vrtE.z()<5){
                        h1.fill(vB.mass());
                        if(vB.mass()>0.5&&vB.mass()<1.5){
                            writer.addEvent(event);
                        }
                    }
                    h3.fill(vrtPi.z()-vrtE.z());
                    h4.fill(vrtPi.z());
                }
                
                if(clasEvent.countByPid(211)==1&&clasEvent.countByPid(-211)==1){
                    //&&clasEvent.countByPid(2212)==1&&clasEvent.countByCharge(-1)<2){
                    //clasEvent.show();
                    LorentzVector vB = LorentzVector.withPxPyPzM(0.0, 0.0, 10.5, 0.0005);
                    LorentzVector vT = LorentzVector.withPxPyPzM(0.0, 0.0, 0.0,0.938);
                    LorentzVector vE = new LorentzVector();
                    LorentzVector vPi = new LorentzVector();
                    LorentzVector vPim = new LorentzVector();
                    clasEvent.vector( vE,  0.0005,  11, 0);
                    clasEvent.vector(vPi, 0.13957, 211, 0);
                    clasEvent.vector(vPim, 0.13957, -211, 0);
                    vB.add(vT).sub(vPi).sub(vE).sub(vPim);
                    Vector3  vec = new Vector3();
                                        
                    clasEvent.vector(vec, 11,0);

                  
                    Vector3  vrtE = new Vector3();
                    Vector3  vrtPi = new Vector3();
                                        
                    clasEvent.vertex(vrtE, 11,0);
                    clasEvent.vertex(vrtPi, 211,0);
                    
                    if(vrtE.z()>-15.0&&vrtE.z()<5){                    
                        h2.fill(vB.mass());
                    }
                   
                }
            }
            
        } 
        writer.close();
        TDataCanvas c1 = new TDataCanvas(500,600);
        c1.divide(2,2);
        c1.cd(0).draw(h1).cd(1).draw(h2);
        c1.cd(2).draw(h3).cd(3).draw(h4);*/
    }
}
