/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.deepnetts.train;

import j4np.physics.LorentzVector;
import j4np.physics.Vector3;
import j4np.utils.io.TextFileReader;
import org.jlab.groot.data.H1F;
import org.jlab.jnp.groot.graphics.TDataCanvas;
import org.jlab.jnp.groot.settings.GRootColorPalette;
import org.jlab.jnp.groot.settings.GRootTheme;

/**
 *
 * @author gavalian
 */
public class TrainPhysics {
    
    public TrainPhysics(){
        
    }
    
    public static void main(String[] args){
        
        H1F h1 = new H1F("h1","",120,0.0,1.5);
        H1F h2 = new H1F("h2","",120,0.0,1.5);
        
        TextFileReader reader = new TextFileReader();
        reader.open("dc_physics_features_36.csv");
        int count = 0;
        while(reader.readNext()==true){
            String[] tokens = reader.getString().split(",");
            double   p = Double.parseDouble(tokens[2])*10.0;
            double  th = Math.toRadians(Double.parseDouble(tokens[3])*40+5);
            double phi = Double.parseDouble(tokens[4])*2*Math.PI-Math.PI;
            
            Vector3 ve3 = new Vector3();
            LorentzVector ve = new LorentzVector();
            ve3.setMagThetaPhi(p, th, phi);            
            ve.setVectM(ve3, 0.0005);
            
            p = Double.parseDouble(tokens[8])*10.0;
            th = Math.toRadians(Double.parseDouble(tokens[9])*40+5);
            phi = Double.parseDouble(tokens[10])*2*Math.PI-Math.PI;
            
            Vector3 vp3 = new Vector3();
            LorentzVector vp = new LorentzVector();
            vp3.setMagThetaPhi(p, th, phi);            
            vp.setVectM(vp3, 0.139);
            
            LorentzVector vb = LorentzVector.withPxPyPzM(0.0, 0.0, 10.5, 0.0005);
            LorentzVector vt = LorentzVector.withPxPyPzM(0.0, 0.0,  0.0, 0.938);
            
            vb.add(vt).sub(ve).sub(vp);
            h1.fill(vb.mass());
            count++;
            if(count%3==0) h2.fill(vb.mass());
            //System.out.println(ve3.mag());
            //System.out.println(vp3.mag());
            //System.out.println("mass = " + vb.mass());
        }
        
        //GRootTheme.getInstance().getPalette().setColorScheme("gold10");
        GRootColorPalette.getInstance().setColorPalette();
        GRootColorPalette.getInstance().setColorScheme("gold10");
        TDataCanvas c = new TDataCanvas();
        h1.setFillColor(4);
        h2.setFillColor(5);
        c.draw(h1).draw(h2,"same");
    }
}
