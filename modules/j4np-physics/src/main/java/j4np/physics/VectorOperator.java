/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class VectorOperator {
    
    public enum OperatorType {
      MASS,MASS2,THETA,PHI,P,E,THETA_DEG,PHI_DEG,PX,PY,PZ, PT, VX, VY, VZ
    };
    
    protected LorentzVector      vec = new LorentzVector();
    protected LorentzVector opVector = new LorentzVector();
    protected int[]           particleID = null;
    protected double[]      particleMass = null;
    protected int[]        particleOrder = null;
    protected int[]         particleSign = null;
    
    public VectorOperator(LorentzVector start){
        vec.copy(start);
    }
    
    public VectorOperator(LorentzVector start, String format){
        vec.copy(start); parse(format);
    }
    
    public VectorOperator(LorentzVector start, int[] pid, int[] order, int[] sign) {
        vec.copy(start);
        particleID    = pid;
        particleOrder = order;
        particleSign  = sign;
        try {
            initMass(pid);
        } catch (Exception ex) {
            Logger.getLogger(VectorOperator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public VectorOperator(LorentzVector start, int[] pid, int[] order, int[] sign, double[] mass) {
        vec.copy(start);
        particleID    = pid;
        particleOrder = order;
        particleSign  = sign;        
        particleMass = mass;
    }
    
    public VectorOperator(List<Integer> pid, List<Integer> order, List<Double> mass, List<Integer> sign){
       this.particleID = new int[pid.size()];
       this.particleOrder = new int[order.size()];
       this.particleMass = new double[mass.size()];
       this.particleSign = new int[sign.size()];
       for(int loop = 0; loop < this.particleID.length; loop++){
           this.particleID[loop] = pid.get(loop);
           this.particleOrder[loop] = order.get(loop);
           this.particleSign[loop] = sign.get(loop);
           this.particleMass[loop] = mass.get(loop);
       }
    }
    
    public VectorOperator(int[] pid, int[] order, int[] sign) {
        vec.setPxPyPzM(0, 0, 0, 0);   
        particleID    = pid;
        particleOrder = order;
        particleSign  = sign;
        try {
            initMass(pid);
        } catch (Exception ex) {
            Logger.getLogger(VectorOperator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public double getValue(OperatorType type){
        switch(type){
            case MASS: return opVector.mass();
            case MASS2: return opVector.mass2();
            case P: return opVector.p();
            case PX: return opVector.px();
            case PY: return opVector.py();
            case PZ: return opVector.pz();
            case VZ: return opVector.vector.z();
            case PT: return opVector.pt();
            case E: return opVector.e();
            case THETA: return opVector.theta();
            case THETA_DEG: return Math.toDegrees(opVector.theta());
            case PHI: return opVector.phi();
            case PHI_DEG: return Math.toDegrees(opVector.phi());
            default: return 0.0;
        }
    }
    protected void setVector(LorentzVector lv){this.vec.copy(lv);}
    
    public static VectorOperator parseOperator(String oper){
        
        List<Integer>   pid = new ArrayList<>();
        List<Integer> order = new ArrayList<>();
        List<Integer>  sign = new ArrayList<>();
        List<Double>   mass = new ArrayList<>();
        
        String dataString = oper.replaceAll("\\s", "");
        if(dataString.startsWith("[")==true){
            dataString = "+"+dataString;
        }
        System.out.printf("analyzing : {%s}\n", dataString);
        
        int position = dataString.indexOf("[", 0);
        
        while(position<oper.length()&&position>=0){
            
            int where = dataString.indexOf("]",position);
            
            String item = dataString.substring(position+1,where);
            String charSign = dataString.substring(position-1,position);
            int    __pid = 0;//Integer.parseInt(item);
            
            if(charSign.compareTo("+")==0){
                sign.add(1);
            } else {
                sign.add(-1);
            }
            if(item.contains(",")==false){
                
                __pid = Integer.parseInt(item);
                pid.add(__pid);                
                order.add(0);
                System.out.println(" pid = " + __pid);
                try {
                    double __mass = PDGDatabase.getParticleById(__pid).mass();
                    System.out.println(" mass = " + __mass);
                    mass.add(__mass);
                } catch (Exception ex) {
                    Logger.getLogger(VectorOperator.class.getName()).log(Level.SEVERE, null, ex);
                    
                } 
                
            } else {
                String[] tokens = item.split(",");
                __pid = Integer.parseInt(tokens[0]);
                pid.add(Integer.parseInt(tokens[0]));
                order.add(Integer.parseInt(tokens[1]));
                if(tokens.length>2){
                    mass.add(Double.parseDouble(tokens[2]));
                } else {
                    try {
                        double __mass = PDGDatabase.getParticleById(__pid).mass();
                        mass.add(__mass);
                    } catch (Exception ex) {
                        Logger.getLogger(VectorOperator.class.getName()).log(Level.SEVERE, null, ex);
                        
                    }   
                }
            }        
            //System.out.printf("next (%4d) : sign %s : (%s)\n",position, charSign,item);
            position = dataString.indexOf("[", where+1);            
            
        }
        //System.out.println(Arrays.toString(pid.toArray()));
        //System.out.println(Arrays.toString(order.toArray()));
        //System.out.println(Arrays.toString(sign.toArray()));
        return new VectorOperator(pid,order,mass,sign);
    }
    
    public void show(){
        System.out.println("-- vector operator");
        for(int k = 0; k < this.particleID.length; k++){
            System.out.printf("\t%4d , %5d , %2d, %9.5f \n",
                    this.particleSign[k], this.particleID[k],
                    this.particleOrder[k], this.particleMass[k]);
        }
    }
    
    public final void parse(String oper){
        
        List<Integer>   pid = new ArrayList<>();
        List<Integer> order = new ArrayList<>();
        List<Integer>  sign = new ArrayList<>();
        
        
        String dataString = oper.replaceAll("\\s", "");
        if(dataString.startsWith("[")==true){
            dataString = "+"+dataString;
        }
        System.out.printf("analyzing : {%s}\n", dataString);
        
        int position = dataString.indexOf("[", 0);
        
        while(position<oper.length()&&position>=0){
            
            int where = dataString.indexOf("]",position);
            
            String item = dataString.substring(position+1,where);
            String charSign = dataString.substring(position-1,position);
            if(charSign.compareTo("+")==0){
                sign.add(1);
            } else {
                sign.add(-1);
            }
            if(item.contains(",")==false){
                pid.add(Integer.parseInt(item));
                order.add(0);
            } else {
                String[] tokens = item.split(",");
                pid.add(Integer.parseInt(tokens[0]));
                order.add(Integer.parseInt(tokens[1]));
            }            
            //System.out.printf("next (%4d) : sign %s : (%s)\n",position, charSign,item);
            position = dataString.indexOf("[", where+1);
        }
        //System.out.println(Arrays.toString(pid.toArray()));
        //System.out.println(Arrays.toString(order.toArray()));
        //System.out.println(Arrays.toString(sign.toArray()));
        this.particleID = new int[pid.size()];
        this.particleOrder = new int[pid.size()];
        this.particleSign = new int[pid.size()];
        for(int loop = 0; loop < pid.size(); loop++){
            particleID[loop]    = pid.get(loop);
            particleOrder[loop] = order.get(loop);
            particleSign[loop]  = sign.get(loop);
        }
        try {
            this.initMass(particleID);
        } catch (Exception ex) {
            Logger.getLogger(VectorOperator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public LorentzVector vector(){ return opVector; };    

    private void initMass(int[] pid) throws Exception {
        particleMass = new double[pid.length];
        for(int i = 0; i < particleMass.length; i++){
            PDGParticle p = PDGDatabase.getParticleById(pid[i]);
            if(p==null) 
                throw new Exception("ERROR: no particle it ID = " + pid[i] + " in the database.");
            particleMass[i] = p.mass();            
        }
    }
    
    public void apply(PhysicsEvent event){
        opVector.reset();
        event.makeVector(opVector, particleID, particleMass, particleOrder, particleSign);
        opVector.add(vec);
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(">>>>> operator : ");
        str.append(Arrays.toString(this.particleID));
        str.append(Arrays.toString(this.particleMass));
        str.append(Arrays.toString(this.particleOrder));
        str.append(Arrays.toString(this.particleSign));
        return str.toString();
    }
    public static void main(String[] args){
        //VectorOperator op = new VectorOperator(new LorentzVector(),"- [11] + [22] + [22,0] + [2212,1]");
        VectorOperator op = new VectorOperator(new LorentzVector(),"- [11] + [22] + [22,0] + [2212,1]");
        op.show();
        
        VectorOperator op2 =  VectorOperator.parseOperator("[2212]-[211,0,0.497]-[-211,1]");
        op2.show();
    }
}
