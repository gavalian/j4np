/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.cvt;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import j4np.physics.Vector3;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class CvtTrackReader {
    
    Bank tb = null;
    Bank mb = null;
    
    public void init(SchemaFactory schf){
        tb = schf.getBank("CVTRec::Tracks");
        mb = schf.getBank("MC::Particle");
    }
    
    public List<CvtTrack>  read(Event ev){
        List<CvtTrack>  list = new ArrayList<>();
        ev.read(tb);
        int nrows = tb.getRows();
        for(int i = 0; i < nrows; i++){
            double pt = tb.getFloat("pt", i);
            double phi0 = tb.getFloat("phi0", i);
            double tandip = tb.getFloat("tandip", i);
            CvtTrack trk = new CvtTrack();
            trk.vector.setXYZ(pt*Math.cos(phi0), 
                    pt*Math.sin(phi0), pt*tandip);
            list.add(trk);
        }
        return list;
    }
    
    public List<Vector3>  readMC(Event ev){
        ev.read(mb);
        List<Vector3> list = new ArrayList<>();
        int nrows = mb.getRows();
        for(int i = 0; i < nrows; i++){
            list.add(new Vector3(
                    mb.getFloat("px", i),mb.getFloat("py", i),mb.getFloat("pz", i)
            ));
            
        }
        return list;
    }
    public int findMatch(List<CvtTrack> trks, Vector3 mc){
        for(int i = 0; i < trks.size(); i++){
            double res = (trks.get(i).vector.mag()-mc.mag())/mc.mag();
            if(res<0.08){
                if(Math.abs(trks.get(i).vector.theta()-mc.theta())<(6/57.29)
                        &&Math.abs(trks.get(i).vector.phi()-mc.phi())<(6/57.29))
                    return i;
            }
        }
        return -1;
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/Software/project-10.5/distribution/CVTmonitoring/neural_50_nA_50k_type_1.hipo.rec.hipo";
        //String file = "/Users/gavalian/Work/Software/project-10.5/distribution/CVTmonitoring/proton_50_nA_50k_type_1.hipo.rec.hipo";
        if(args.length>0) file = args[0];
        
        HipoReader r = new HipoReader(file);
        CvtTrackReader t = new CvtTrackReader();
        
        t.init(r.getSchemaFactory());
        
        Event e = new Event();
        int counter = 0;
        int counterPos = 0;
        int nTracks = 0;
        while(r.hasNext()){
            r.nextEvent(e);
            List<CvtTrack> tracks = t.read(e);
            List<Vector3>  mc = t.readMC(e);
            int index = t.findMatch(tracks, mc.get(0));
            if(index>=0) counterPos++;
            counter++;
            nTracks += tracks.size();
            //System.out.println(tracks.size() + "  " + mc.size());
        }
        System.out.printf(" effiency = %f, multiplicity = %f\n",
                ((double) counterPos)/counter,
                ((double) nTracks)/counter);
    }
}
