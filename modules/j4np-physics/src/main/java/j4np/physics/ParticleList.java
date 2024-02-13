/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class ParticleList extends PhysicsEvent {
    
    List<Particle> particles = new ArrayList<>();

    public void reset(){
        particles.clear();
    }
    
    public ParticleList addParticle(Particle p){
       particles.add(p); return this;
    }
    
    @Override
    public int count() {
        return particles.size();
    }

    @Override
    public int charge(int index) {
        return particles.get(index).charge();
    }

    @Override
    public int pid(int index) {
        return particles.get(index).pid();
    }

    @Override
    public int status(int index) {
        return 1;
    }

    @Override
    public void status(int index, int value) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void vector(Vector3 v, int index) {
        v.copy(particles.get(index).vector().vector);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void vertex(Vector3 v, int index) {
        v.copy(particles.get(index).vertex());
    }
}
