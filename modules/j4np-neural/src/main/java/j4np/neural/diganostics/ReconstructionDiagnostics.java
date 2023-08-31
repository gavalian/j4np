/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.diganostics;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoChain;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoDataWorker;
import j4np.physics.Vector3;
import twig.data.DataGroup;
import twig.data.DataSetSerializer;
import twig.data.H1F;
import twig.data.H2F;

/**
 *
 * @author gavalian
 */
public class ReconstructionDiagnostics extends HipoDataWorker {
    
    private String      partBank = "REC::Particle";
    private String     trackBank = "TimeBasedTrkg::TBTracks";
    private Bank[]     diagBanks = null;
    private String    outputFile = "diagnostics.twig";
    private String     directory = "default";
    
    protected DataGroup  dataGroup = null;
    
    public ReconstructionDiagnostics(){}

    public ReconstructionDiagnostics(String pname){
        this.partBank = pname;
    }
    public ReconstructionDiagnostics(String pname,String tname){
        this.partBank = pname;this.trackBank=tname;
    }
    
    
    public ReconstructionDiagnostics setDirectory(String dir){
        directory = dir; return this;
    }
    
    @Override
    public boolean init(HipoChain src) {
        diagBanks = src.getReader().getBanks(partBank,partBank);
        
        dataGroup = new DataGroup(2,2);
        dataGroup.setName("recon");
        dataGroup.add(new H2F("electron",110,0.0,11.0,45,0.0,90), 0,"");
        dataGroup.add(new H2F("positive",110,0.0,11.0,45,0.0,90), 1,"");
        dataGroup.add(new H2F("negative",110,0.0,11.0,45,0.0,90), 2,"");        
        
        return true;
    }

    private Vector3 vector(Bank b, int index, int start){
        Vector3 v = new Vector3(
                b.getFloat(start  , index),
                b.getFloat(start+1, index),
                b.getFloat(start+2, index)
        );
        return v;
    }
    
    @Override
    public void execute(Event e) {
        e.read(diagBanks);
        if(diagBanks[0].getRows()==0) return;
        if(diagBanks[0].getInt(0, 0)!=11) return;
    
        Vector3 pp = vector(diagBanks[0],0,1);
        Vector3 pv = vector(diagBanks[0],0,4);
        
        if(pv.z()>-15&&pv.z()<5) 
            ((H2F) dataGroup.getData().get(0)).fill(pp.mag(),pp.theta()*57.29);
        
        int nrows = diagBanks[0].getRows();
        for(int r = 0; r < nrows; r++){
            int    status = diagBanks[0].getInt("status",r);
            float chi2pid = diagBanks[0].getFloat("chi2pid",r);
            if(status>=2000&&status<3000&&Math.abs(chi2pid)<3.0){
                Vector3 rp = vector(diagBanks[0],r,1);
                Vector3 rv = vector(diagBanks[0],r,4);
                int charge = diagBanks[0].getInt("charge",r);
                if(pv.z()>-15&&pv.z()<5){              
                    if(charge>0)  ((H2F) dataGroup.getData().get(1)).fill(rp.mag(),rp.theta()*57.29);
                    if(charge<0)  ((H2F) dataGroup.getData().get(2)).fill(rp.mag(),rp.theta()*57.29);
                }
            }
        }
    }
    
    public void close(){
        DataSetSerializer.exportDataGroup(dataGroup, this.outputFile, 
                String.format("/diagnosis/%s/%s",directory,partBank));
    }
    
    public static class ReconstructionCompare extends HipoDataWorker {
        
        String oneBank = "REC::Particle";
        String twoBank = "RECAI::Particle";
        ReconstructionDiagnostics oneDiag = null;
        ReconstructionDiagnostics twoDiag = null;
        String outputFile = "diagnostics.twig";
        String directory  = "dir";
        String title = "";
        
        public ReconstructionCompare(){}
        
        public ReconstructionCompare(String dir){ directory = dir;}
        
        public void setTitle(String t){title = t;}
        @Override
        public boolean init(HipoChain src) {
            oneDiag = new ReconstructionDiagnostics(oneBank);
            twoDiag = new ReconstructionDiagnostics(twoBank);
            oneDiag.init(src);
            twoDiag.init(src);
           return true; 
        }

        @Override
        public void execute(Event e) {
            this.oneDiag.execute(e);
            this.twoDiag.execute(e);
        }
        
        public void close(){            
            DataGroup group = new DataGroup(2,3);
            
            H1F hele1 = ((H2F) oneDiag.dataGroup.getData().get(0)).projectionX();
            H1F hele2 = ((H2F) twoDiag.dataGroup.getData().get(0)).projectionX();
            H1F hpos1 = ((H2F) oneDiag.dataGroup.getData().get(1)).projectionX();
            H1F hpos2 = ((H2F) twoDiag.dataGroup.getData().get(1)).projectionX();
            H1F hneg1 = ((H2F) oneDiag.dataGroup.getData().get(2)).projectionX();
            H1F hneg2 = ((H2F) twoDiag.dataGroup.getData().get(2)).projectionX();
            
            H1F hrele = H1F.divide(hele2, hele1);
            H1F hrpos = H1F.divide(hpos2, hpos1);
            H1F hrneg = H1F.divide(hneg2, hneg1);
            hrele.attr().setLegend("electron ratio " + title);
            hrpos.attr().setLegend("positive ratio " + title);
            hrneg.attr().setLegend("negative ratio " + title);
            
            hrele.attr().setTitleX("P [GeV]");
            hrpos.attr().setTitleX("P [GeV]");
            hrneg.attr().setTitleX("P [GeV]");
            
            group.add(hrele, 0, "");
            group.add(hrpos, 1, "");
            group.add(hrneg, 2, "");
            
            DataSetSerializer.exportDataGroup(group, this.outputFile, 
                String.format("/diagnosis/compare/%s",directory));
        }
        
    }
    
    public static void main(String[] args){
        //String file = "rec_clas_005921.00000-00004.nn_rgk.filtered.hipo";
        String file = "output22_filtered.hipo";
        //String file = "rec_clas_005920.00000-00004.nn_default.filtered.hipo";
        HipoChain r = new HipoChain(file);
        //ReconstructionDiagnostics d = new ReconstructionDiagnostics("RECAI::Particle");
        //d.setDirectory("rgk");
        
        //d.init(r);
        ReconstructionCompare c = new ReconstructionCompare("rgk-dcrc");
        c.setTitle("dcrc");
        c.init(r);
        
        Event e = new Event();
        while(r.hasNext()==true){
            r.next(e);
            c.execute(e);
        }
        c.close();
    }
}
