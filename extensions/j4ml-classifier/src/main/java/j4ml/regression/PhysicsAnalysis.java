/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.regression;

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
    
    public String directory = "/regression";
    
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
            if(tokens.length>col[i]){
                r[i] = Double.parseDouble(tokens[col[i]]);
            } else r[i] = 0.0;
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
        
        H1F hdata = new H1F("hdata_1n1p",120,0.4,3.2);
        H1F hreg  = new H1F( "hreg_1n1p",120,0.4,3.2);
        
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
                
                LorentzVector lves = PhysicsAnalysis.getParticleSector(
                        lines.get(0).trim(), new int[]{24,25,26}, 0.0005);
                LorentzVector lvps = PhysicsAnalysis.getParticleSector(
                        lines.get(1).trim(), new int[]{24,25,26}, 0.13957);
                
                LorentzVector  cm = PhysicsAnalysis.getCM();
                LorentzVector cms = PhysicsAnalysis.getCM();
                
                cm.sub(lve).sub(lvp);
                cms.sub(lves).sub(lvps);
                
                hdata.fill(cm.mass());
                hreg.fill(cms.mass());
            }
        }
        TDirectory dir = new TDirectory();
        dir.add(directory, hdata);
        dir.add(directory, hreg);
        dir.write(exportFile);
    }
    
    public void analyzeThree(String filename, String exportFile){
        TextFileReader r = new TextFileReader();
        r.open(filename);
        boolean flag = true;
        
        H1F hdata = new H1F("hdata_2n1p",120,0.4,3.2);
        H1F hreg  = new H1F( "hreg_2n1p",120,0.4,3.2);
        
        while(flag==true){
            //counter++;
            List<String> lines = r.readLines(3);
            if(lines.size()!=3){ 
                flag = false;                
            } else {                
                LorentzVector lve = PhysicsAnalysis.getParticle(
                        lines.get(0).trim(), new int[]{4,5,6}, 0.0005);
                LorentzVector lvpm = PhysicsAnalysis.getParticle(
                        lines.get(1).trim(), new int[]{4,5,6}, 0.13957);
                LorentzVector lvpp = PhysicsAnalysis.getParticle(
                        lines.get(2).trim(), new int[]{4,5,6}, 0.13957);
                
                LorentzVector lves = PhysicsAnalysis.getParticleSector(
                        lines.get(0).trim(), new int[]{24,25,26}, 0.0005);
                LorentzVector lvpms = PhysicsAnalysis.getParticleSector(
                        lines.get(1).trim(), new int[]{24,25,26}, 0.13957);
                LorentzVector lvpps = PhysicsAnalysis.getParticleSector(
                        lines.get(2).trim(), new int[]{24,25,26}, 0.13957);
                
                LorentzVector cm = PhysicsAnalysis.getCM();
                LorentzVector cms = PhysicsAnalysis.getCM();
                
                cm.sub(lve).sub(lvpm).sub(lvpp);
                cms.sub(lves).sub(lvpms).sub(lvpps);
                
                hdata.fill(cm.mass());
                hreg.fill(cms.mass());
                
                LorentzVector lve2 = PhysicsAnalysis.getParticle(
                        lines.get(1).trim(), new int[]{4,5,6}, 0.0005);
                LorentzVector lvpm2 = PhysicsAnalysis.getParticle(
                        lines.get(0).trim(), new int[]{4,5,6}, 0.13957);
                LorentzVector lvpp2 = PhysicsAnalysis.getParticle(
                        lines.get(2).trim(), new int[]{4,5,6}, 0.13957);
                
                LorentzVector lves2 = PhysicsAnalysis.getParticle(
                        lines.get(1).trim(), new int[]{24,25,26}, 0.0005);
                LorentzVector lvpms2 = PhysicsAnalysis.getParticle(
                        lines.get(0).trim(), new int[]{24,25,26}, 0.13957);
                LorentzVector lvpps2 = PhysicsAnalysis.getParticle(
                        lines.get(2).trim(), new int[]{24,25,26}, 0.13957);
                
                LorentzVector  cm2 = PhysicsAnalysis.getCM();
                LorentzVector cms2 = PhysicsAnalysis.getCM();
                
                cm2.sub(lve2).sub(lvpm2).sub(lvpp2);
                cms2.sub(lves2).sub(lvpms2).sub(lvpps2);
                
                //hdata.fill(cm2.mass());
                //hreg.fill(cms2.mass());
            }
        }
        TDirectory dir = new TDirectory();
        dir.add(directory, hdata);
        dir.add(directory, hreg);
        dir.write(exportFile);
    }
    
    public static LorentzVector getParticle(String line, int[] columns, double mass){
        double[] vec = PhysicsAnalysis.getColumns(line.trim(), columns);
        return PhysicsAnalysis.fromPTF(vec, mass);
        //return LorentzVector.withPxPyPzM(vec[0], vec[1], vec[2], mass);
    }
    
    public static LorentzVector getParticleSector(String line, int[] columns, double mass){
        double[] vec = PhysicsAnalysis.getColumns(line, columns);
        int   sector = PhysicsAnalysis.getSector(line);        
        LorentzVector vL = PhysicsAnalysis.fromPTFsec(sector, vec, mass);
        //vL.rotateZ(angle);
        return vL;
    }
    
    public static void main(String[] args){
        //String file = "/Users/gavalian/Work/dataspace/pid/results/c_extract_regression_data_1n1p_hb.txt.pred.norm";        
        //String file = "/Users/gavalian/Downloads/new_res/h_extract_regression_data_1n1p_hb_res_from_i.txt" ;
        //String file = "/Users/gavalian/Work/software/project-10a.0.4/j4np-1.0.4/c_extract_regression_data_1n1p_hb.txt" ;
        String file = "/Users/gavalian/Work/dataspace/regression/res_data3/c_extract_regression_data_1n1p_hb_res_from_b.txt" ;
        
        //String file2 = "/Users/gavalian/Work/dataspace/pid/results/d_extract_regression_data_1n1p_hb.txt.pred.norm";
        PhysicsAnalysis ana = new PhysicsAnalysis();
        ana.directory = "/cfromb";
        ana.analyze(file, "inference.twig");
        
        /*String data = """
                      
                      """;
*/
    }
}
