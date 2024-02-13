/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics;

import java.util.Random;

/**
 *
 * @author gavalian
 */
public class Particle {
    
    private LorentzVector vector = new LorentzVector();
    private       Vector3 vertex = new Vector3();
    private          int     pid = 0;
    private          int pcharge = 0;
    
    public Particle(){
    }
    
    public LorentzVector vector(){ return this.vector;}
    public Vector3       vertex(){ return this.vertex;}
    public int              pid(){ return pid; }
    public int           charge(){ return pcharge;}
    public Particle      charge(int __c){pcharge = __c; return this;}
    
    public static Particle withPid(int __p, double px, double py, double pz){
        Particle  p = new Particle(); p.pid = __p;
        PDGParticle pt = PDGDatabase.getParticleById(__p);
        double mass = PDGDatabase.getParticleMass(p.pid);        
        p.vector.setPxPyPzM(px, py, pz, mass);
        p.pcharge = pt.charge();
        return p;
    }
    
    public static Particle withPid(int __p, Vector3 v){
        Particle  p = new Particle(); p.pid = __p;
        PDGParticle pt = PDGDatabase.getParticleById(__p);
        double mass = PDGDatabase.getParticleMass(p.pid);        
        p.vector.setPxPyPzM(v.x(),v.y(),v.z(), mass);
        p.pcharge = pt.charge();
        return p;
    }
    public static Particle withPidMagThetaPhi(int __p, double mag, double theta, double phi){
        Particle  p = new Particle(); p.pid = __p;
        PDGParticle pt = PDGDatabase.getParticleById(__p);
        p.pcharge = pt.charge();
        double mass = PDGDatabase.getParticleMass(p.pid); 
        p.vector.setMagThetaPhiM(mag, theta, phi, mass);
        return p;
    }
    
    public static Particle withMass(double px, double py, double pz, double mass){
        Particle  p = new Particle();
        p.vector.setPxPyPzM(px, py, pz, mass);
        return p;
    }
    
    
    public static Particle generate(int pid, double[] p, double[] theta, double[] phi){
        Random r = new Random();
        double mom = r.nextDouble()*(p[1]-p[0]) + p[0];
        double the = r.nextDouble()*(theta[1]-theta[0]) + theta[0];
        double ph  = r.nextDouble()*(phi[1]-phi[0]) + phi[0];
        return Particle.withPidMagThetaPhi(pid, mom, the, ph);
    }
    /*
    private String name = "default";
    
    private LorentzVector       pVector = new LorentzVector();
    private LorentzVector pVectorHelper = new LorentzVector();
    
    private int[] particleIDs   = null;
    private int[] particleOrder = null;
    private int[] particleSign  = null;
    private double[] particleMass = null; 
    
    public Particle(){
        
    }
    
    protected void initialize(int[] ids, int[] order, int[] sign){
        
        particleIDs   = new int[ids.length];
        particleOrder = new int[ids.length];
        particleSign  = new int[ids.length];
        particleMass  = new double[ids.length];
                
        for(int i = 0; i < ids.length; i++){
            
            PDGParticle  p = PDGDatabase.getParticleById(ids[i]);
            if(p==null){
                particleMass[i] = 0.0;
            } else {
                particleMass[i] = p.mass();
            }
            particleIDs  [i]  = ids[i];
            particleOrder[i]  = order[i];
            particleSign [i]  = sign[i];
        }
    }
    
    public void calculate(PhysicsEvent event){
        pVector.setPxPyPzM(0, 0, 0, 0);
        for(int i = 0; i < particleIDs.length; i++){
            event.vector(pVectorHelper, particleMass[i], particleIDs[i], particleOrder[i]);
            if(particleSign[i]>0){
                pVector.add(pVectorHelper);
            } else{
                pVector.sub(pVectorHelper);
            }
        }
    }
    
    public LorentzVector vector(){ return pVector;}
    */
    
}
