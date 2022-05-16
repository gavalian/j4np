/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.cvt;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.SchemaFactory;
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
}
