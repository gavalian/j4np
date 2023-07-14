/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.clas12.services;

import j4ml.clas12.networks.RegressionNetwork;
import j4ml.clas12.track.Track;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.physics.Vector3;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class RegressionService {
    
    Bank bankTrk = null;
    Bank bankClt = null;
    RegressionNetwork network = null;
    Schema   mlSchema = null;
    
    public RegressionService(){
        
    }
    
    public void initNetwork(String file, int run, String flavor){
        network = new RegressionNetwork();
        network.load(file, run, flavor);
    }
    
    public void init(HipoReader r){
        bankTrk = r.getBank("TimeBasedTrkg::TBTracks");
        bankClt = r.getBank("TimeBasedTrkg::TBClusters");
        mlSchema = r.getSchemaFactory().getSchema("ml::tracks");
    }
    
    public void processEvent(Event event){
        event.read(bankTrk,bankClt);
        
        List<Track> tracks = Track.read(bankTrk, bankClt);
        System.out.println("--- event ");
        if(tracks.size()==1){
            float[] features = tracks.get(0).getFeatures();
            Vector3 vec = network.getVector(features, +1);
            Bank b = new Bank(mlSchema,1); 
            b.putByte("sector", 0, (byte) tracks.get(0).sector);
            
            b.putFloat("p", 0, (float) vec.mag());
            b.putFloat("theta", 0, (float) vec.theta());
            b.putFloat("phi", 0, (float) vec.phi());
            b.putFloat("vz", 0, (float) tracks.get(0).vector.mag());
            System.out.println(tracks.get(0));
            for(int i = 0; i < 6; i++) b.putFloat(i+11, 0, features[i]);
                event.write(b);
        }
        /*for(Track t : tracks) {
            System.out.println(Arrays.toString(t.getFeatures()));
            Vector3 vec = network.getVector(t.getFeatures(), +1);
            
            System.out.println("momentum = " + vec.mag() +  "  theta = " + vec.theta()*57.29 + "  phi = " + vec.phi()*57.29);
            System.out.println(t);
        }*/
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/Software/project-10.6/study/regression/recon_origin_without_uRWell.hipo";
        HipoReader r = new HipoReader(file);
        HipoWriter w = new HipoWriter();
        SchemaFactory scf = new SchemaFactory();
        scf.readFile("neuralNetwork.json");
        scf.show();
        
        w.getSchemaFactory().copy(r.getSchemaFactory());
        for(Schema sch : scf.getSchemaList()) w.getSchemaFactory().addSchema(sch);
        for(Schema sch : scf.getSchemaList()) r.getSchemaFactory().addSchema(sch);
        
        w.open("output_mc.h5");
        
        RegressionService rs = new RegressionService();

        rs.initNetwork("clas12mc.network", 11, "default");
        rs.init(r);
        Event e = new Event();
        
        while(r.hasNext()){
            r.next(e);
            rs.processEvent(e);
            w.addEvent(e);
        }
        w.close();
    }
}
