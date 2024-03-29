/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.regression.analysis;

import j4np.physics.LorentzVector;
import j4np.physics.Vector3;
import j4np.utils.io.TextFileReader;
import java.util.List;
import twig.data.*;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class PhysicsAnalysis {
    
    public static LorentzVector getVector(double mass,String[] data){
        double px = Double.parseDouble(data[1]);
        double py = Double.parseDouble(data[2]);
        double pz = Double.parseDouble(data[3]);
        return LorentzVector.withPxPyPzM(px, py, pz, mass);
    }
    
    public static LorentzVector getVectorInf(double mass,String[] data){
        int sector = (int) Double.parseDouble(data[4]);
        double p   = Double.parseDouble(data[11]);
        double th  = Double.parseDouble(data[12]);
        double fi  = Double.parseDouble(data[13]);
        System.out.printf("(%9.5f %9.5f %9.5f)\n",p,th,fi);
        return null;//LorentzVector.withPxPyPzM(px, py, pz, mass);
    }
    public static double[] getColumns(String line,int... col){
        double[] r = new double[col.length];
        String[] tokens = line.split("\\s+");
        for(int i = 0; i < col.length;i++){
            r[i] = Double.parseDouble(tokens[col[i]]);
        }
        return r;
    }
    
    public static LorentzVector getCM(){
        LorentzVector b = LorentzVector.withPxPyPzM(0, 0, 6.6, 0.0005);
        return b.add(0, 0, 0, 0.938);
    }
    
    public static LorentzVector fromPTF(double[] v,double mass){
        Vector3 vec = new Vector3();
        vec.setMagThetaPhi(v[0], Math.toRadians(v[1]), Math.toRadians(v[2]));
        return LorentzVector.withPxPyPzM(vec.x(), vec.y(), vec.z(), mass);
    }
    
    public static LorentzVector fromPTFsec(int sector, double[] v,double mass){
        Vector3 vec = new Vector3();
        double angle = (sector-2)*60.0;
        vec.setMagThetaPhi(v[0], Math.toRadians(v[1]), Math.toRadians(v[2]));
        vec.rotateZ(Math.toRadians(angle));
        return LorentzVector.withPxPyPzM(vec.x(), vec.y(), vec.z(), mass);
    }
    
    public static int  getSector(String line){
        String[] tokens = line.split("\\s+");
        return Integer.parseInt(tokens[0]);
    }
    
    
    public void analyze(String filename, String exportFile){
        TextFileReader r = new TextFileReader();
        r.open(filename);
        boolean flag = true;
        
        H1F hdata = new H1F("hdata_1n1p",120,0.4,1.8);
        H1F hreg  = new H1F( "hreg_1n1p",120,0.4,1.8);
        
        while(flag==true){
            //counter++;
            List<String> lines = r.readLines(2);
            if(lines.size()!=2){ 
                flag = false;                
            } else {
                
                LorentzVector lve = PhysicsAnalysis.getParticle(
                        lines.get(0).trim(), new int[]{4,5,6}, 0.0005);
                LorentzVector lvp = PhysicsAnalysis.getParticle(
                        lines.get(1).trim(), new int[]{4,5,6}, 0.13957);
                
                LorentzVector lves = PhysicsAnalysis.getParticle(
                        lines.get(0).trim(), new int[]{24,25,26}, 0.0005);
                LorentzVector lvps = PhysicsAnalysis.getParticle(
                        lines.get(1).trim(), new int[]{24,25,26}, 0.13957);
                
                LorentzVector cm = PhysicsAnalysis.getCM();
                LorentzVector cms = PhysicsAnalysis.getCM();
                
                cm.sub(lve).sub(lvp);
                cms.sub(lves).sub(lvps);
                hdata.fill(cm.mass());
                hreg.fill(cms.mass());
            }
        }
        TDirectory dir = new TDirectory();
        dir.add("/regression", hdata);
        dir.add("/regression", hreg);
        dir.write(exportFile);
    }
    
    public static LorentzVector getParticle(String line, int[] columns, double mass){
        double[] vec = PhysicsAnalysis.getColumns(line, columns);
        return LorentzVector.withPxPyPzM(vec[0], vec[1], vec[2], mass);
    }
    
    public static LorentzVector getParticleSector(String line, int[] columns, double mass){
        double[] vec = PhysicsAnalysis.getColumns(line, columns);
        int   sector = PhysicsAnalysis.getSector(line);
        double angle = (sector-2)*60.0;
        LorentzVector vL = LorentzVector.withPxPyPzM(vec[0], vec[1], vec[2], mass);
        vL.rotateZ(angle);
        return vL;
    }
    
    public static void main(String[] args){
        
        TextFileReader r = new TextFileReader();
        //r.open("/Users/gavalian/Work/dataspace/pid/extractedDataPredNorm4.txt");
        r.open("/Users/gavalian/Work/dataspace/pid/results/b_extract_regression_mc_1n1p_hb.txt.pred.norm");
        

        H1F h  = new H1F("h",120,0.5,2.5);
        H1F hr = new H1F("hr",120,0.5,2.5);
        
        H1F hs  = new H1F("hs",120,0.5,2.5);
        H1F hb  = new H1F("hb",120,0.5,2.5);
        
        H1F hsns2  = new H1F("hsns2",120,0.5,2.5);
        H1F hbns2  = new H1F("hbns2",120,0.5,2.5);
        
        boolean   flag = true;
        
        int    counter = 0;
        
        while(flag==true){
            //counter++;
            List<String> lines = r.readLines(2);
            //System.out.println("lines read = " + lines.size());
            if(lines.size()!=2||counter>100){
                flag=false;
            } else {
                double[]  vel = PhysicsAnalysis.getColumns(lines.get(0).trim(), 4,5,6);
                double[]  vpi = PhysicsAnalysis.getColumns(lines.get(1).trim(), 4,5,6);
                
                LorentzVector lve = PhysicsAnalysis.fromPTF(vel,0.0005);
                LorentzVector lvp = PhysicsAnalysis.fromPTF(vpi,0.139);
                
                double[]  vel2 = PhysicsAnalysis.getColumns(lines.get(0).trim(), 24,25,26);
                double[]  vpi2 = PhysicsAnalysis.getColumns(lines.get(1).trim(), 24,25,26);                                
                
                int sec_el = PhysicsAnalysis.getSector(lines.get(0).trim());
                int sec_pi = PhysicsAnalysis.getSector(lines.get(1).trim());
                
                LorentzVector lves = PhysicsAnalysis.fromPTFsec(sec_el,vel2,0.0005);
                LorentzVector lvps = PhysicsAnalysis.fromPTFsec(sec_pi,vpi2,0.139);
                
                //System.out.println("=======");
                //System.out.println(">>> REAL : " + lve.toString());
                //System.out.println(">>> INFR : " + lves.toString());
                
                LorentzVector  cm = PhysicsAnalysis.getCM();
                LorentzVector cmr = PhysicsAnalysis.getCM();
                
                cm.sub(lve).sub(lvp);                
                cmr.sub(lves).sub(lvps);
                
                h.fill(cm.mass());
                hr.fill(cmr.mass());
                if(cmr.mass()<1.35){
                    hs.fill(cm.mass());
                } else hb.fill(cm.mass());
                
                if(sec_el!=2&&sec_pi!=2){
                    hsns2.fill(cmr.mass());
                }
                //System.out.printf("mass = %8.4f\n",cm.mass());
            }
            //String      line = r.getString();
            //String[]  tokens = line.split("\\s+");
            //PhysicsAnalysis.getVectorInf(0.0005, tokens);
        }
        
        TGCanvas c = new TGCanvas(800,550);
        
        c.view().divide(2,3);
        
        c.view().region(0).draw(h);
        c.view().region(1).draw(hr);
        
        c.view().region(2).draw(hs);
        c.view().region(3).draw(hb);
        c.view().region(4).draw(hsns2);
        
        TDirectory dir = new TDirectory();
        dir.add("/airec", hr);
        dir.write("rec.twig");

    }
}
