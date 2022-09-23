/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.geom.detector;

import j4np.geom.prim.Path3D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class Detector {
    
    protected List<Layer> layers = new ArrayList<>();
    
    public List<Layer> getLayers(){return layers;}
    
    public List<DetectorHit> getHits(Path3D path){
        List<DetectorHit> hits = new ArrayList<>();
        for(Layer l : layers) hits.addAll(l.getHits(path));
        return hits;
    }
}
