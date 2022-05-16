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
public class DataAnalysisRegression {
    
    public String directory = "/regression";
    public boolean print = false;
    
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
        String[] tokens = line.trim().split("\\s+");
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
        String[] tokens = line.trim().split("\\s+");
        return Integer.parseInt(tokens[0].trim());
    }
        
    public void analyze(String filename, String exportFile){
        
        TextFileReader r = new TextFileReader();
        r.open(filename);
        
        boolean flag = true;
        
        H1F hdata = new H1F("hdata_1n1p",120,0.4,3.2);
        H1F hreg  = new H1F( "hreg_1n1p",120,0.4,3.2);
        H1F  hdata_emom = new H1F("hdata_emom",120,0.0,6.5);
        H1F    hdata_eth = new H1F("hdata_eth",120,0.0,1.0);
        H1F   hdata_ephi = new H1F("hdata_ephi",120,-Math.PI,Math.PI);
        
        H1F  hdata_pmom = new H1F("hdata_pmom",120,0.0,6.5);
        H1F    hdata_pth = new H1F("hdata_pth",120,0.0,1.0);
        H1F   hdata_pphi = new H1F("hdata_pphi",120,-Math.PI,Math.PI);
        
        H1F  hreg_emom = new H1F("hreg_emom",120,0.0,6.5);
        H1F    hreg_eth = new H1F("hreg_eth",120,0.0,1.0);
        H1F   hreg_ephi = new H1F("hreg_ephi",120,-Math.PI,Math.PI);
        
        H1F  hreg_pmom = new H1F("hreg_pmom",120,0.0,6.5);
        H1F    hreg_pth = new H1F("hreg_pth",120,0.0,1.0);
        H1F   hreg_pphi = new H1F("hreg_pphi",120,-Math.PI,Math.PI);
        
        while(flag==true){
            //counter++;
            List<String> lines = r.readLines(2);
            if(lines.size()!=2){ 
                flag = false;
            } else {
                
                LorentzVector lve = DataAnalysisRegression.getParticle(
                        lines.get(0).trim(), new int[]{4,5,6}, 0.0005);
                LorentzVector lvp = DataAnalysisRegression.getParticle(
                        lines.get(1).trim(), new int[]{4,5,6}, 0.13957);
                
                LorentzVector lves = DataAnalysisRegression.getParticleSector(
                        lines.get(0).trim(), new int[]{24,25,26}, 0.0005);
                LorentzVector lvps = DataAnalysisRegression.getParticleSector(
                        lines.get(1).trim(), new int[]{24,25,26}, 0.13957);
                
                LorentzVector  cm = DataAnalysisRegression.getCM();
                LorentzVector cms = DataAnalysisRegression.getCM();
                int sector_n = DataAnalysisRegression.getSector(lines.get(0));
                int sector_p = DataAnalysisRegression.getSector(lines.get(1));
                cm.sub(lve).sub(lvp);
                cms.sub(lves).sub(lvps);
                
                if(sector_n!=2&&sector_p!=2){
                    if(print==true){
                        System.out.println("*****"  );
                        System.out.println("e : " + lve);
                        System.out.println("p : " + lvp);
                        System.out.println("es: " + lves);
                        System.out.println("ps: " + lvps);
                    }
                    
                    hdata_emom.fill(lve.p());
                    hdata_eth.fill(lve.theta());
                    hdata_ephi.fill(lve.phi());
                    
                    hdata_pmom.fill(lvp.p());
                    hdata_pth.fill(lvp.theta());
                    hdata_pphi.fill(lvp.phi());
                    
                    hreg_emom.fill(lves.p());
                    hreg_eth.fill(lves.theta());
                    hreg_ephi.fill(lves.phi());
                    
                    hreg_pmom.fill(lvps.p());
                    hreg_pth.fill(lvps.theta());
                    hreg_pphi.fill(lvps.phi());
                    
                    hdata.fill(cm.mass());
                    hreg.fill(cms.mass());
                }
                String data_e = String.format("-1 : %8.5f %8.4f %8.3f : %8.5f %8.4f %8.3f", 
                        lve.p(),Math.toDegrees(lve.theta()),Math.toDegrees(lve.phi()),
                        lves.p(),Math.toDegrees(lves.theta()),Math.toDegrees(lves.phi())
                        );
                String data_p = String.format(" 1 : %8.5f %8.4f %8.3f : %8.5f %8.4f %8.3f", 
                        lvp.p(),Math.toDegrees(lvp.theta()),Math.toDegrees(lvp.phi()),
                        lvps.p(),Math.toDegrees(lvps.theta()),Math.toDegrees(lvps.phi())
                        );
                //System.out.println(data_e);
                //System.out.println(data_p);
            }
        }
        TDirectory dir = new TDirectory();
        
        dir.add(directory, hdata);
        dir.add(directory, hreg);
        dir.add(directory, hdata_emom).add(directory, hdata_eth).add(directory, hdata_ephi);
        dir.add(directory, hdata_pmom).add(directory, hdata_pth).add(directory, hdata_pphi);
        
        dir.add(directory, hreg_emom).add(directory, hreg_eth).add(directory, hreg_ephi);
        dir.add(directory, hreg_pmom).add(directory, hreg_pth).add(directory, hreg_pphi);
        dir.write(exportFile);
    }
    
    public void analyzeThree(String filename, String exportFile){
        TextFileReader r = new TextFileReader();
        r.open(filename);
        boolean flag = true;
        
        H1F hdata = new H1F("hdata_2n1p",120,0.4,3.2);
        H1F hreg  = new H1F( "hreg_2n1p",120,0.4,3.2);
        
        H1F hdatagt2p5 = new H1F("hdata_2n1p_gt2p5",120,0.4,2.0);
        H1F hreggt2p5  = new H1F( "hreg_2n1p_gt2p5",120,0.4,2.0);
        
        H1F hdatalt2p5 = new H1F("hdata_2n1p_lt2p5",120,0.4,2.0);
        H1F hreglt2p5  = new H1F( "hreg_2n1p_lt2p5",120,0.4,2.0);
        
        H2F hdata2e = new H2F("hdata_2n1p_2de",120,0.0,6.5,120,0.4,3.2);
        H2F hreg2e  = new H2F( "hreg_2n1p_2de",120,0.0,6.5,120,0.4,3.2);
        
        H2F hdata2p = new H2F("hdata_2n1p_2dp",120,0.0,6.5,120,0.4,3.2);
        H2F hreg2p  = new H2F( "hreg_2n1p_2dp",120,0.0,6.5,120,0.4,3.2);
        
        
        H2F h2_ep = new H2F("h2_ep",120,0.0,6.5,120,0.0,6.5);
        H2F h2_pp = new H2F("h2_pp",120,0.0,6.5,120,0.0,6.5);
        
        
        while(flag==true){
            //counter++;
            List<String> lines = r.readLines(3);
            if(lines.size()!=3){ 
                flag = false;                
            } else {                
                LorentzVector lve = DataAnalysisRegression.getParticle(
                        lines.get(0).trim(), new int[]{4,5,6}, 0.0005);
                LorentzVector lvpm = DataAnalysisRegression.getParticle(
                        lines.get(1).trim(), new int[]{4,5,6}, 0.13957);
                LorentzVector lvpp = DataAnalysisRegression.getParticle(
                        lines.get(2).trim(), new int[]{4,5,6}, 0.13957);
                
                LorentzVector lves = DataAnalysisRegression.getParticleSector(
                        lines.get(0).trim(), new int[]{24,25,26}, 0.0005);
                LorentzVector lvpms = DataAnalysisRegression.getParticleSector(
                        lines.get(1).trim(), new int[]{24,25,26}, 0.13957);
                LorentzVector lvpps = DataAnalysisRegression.getParticleSector(
                        lines.get(2).trim(), new int[]{24,25,26}, 0.13957);
                
                LorentzVector cm = DataAnalysisRegression.getCM();
                LorentzVector cms = DataAnalysisRegression.getCM();
                
                cm.sub(lve).sub(lvpm).sub(lvpp);
                cms.sub(lves).sub(lvpms).sub(lvpps);
                int sector_n1 = DataAnalysisRegression.getSector(lines.get(0));
                int sector_p1 = DataAnalysisRegression.getSector(lines.get(1));
                int sector_n2 = DataAnalysisRegression.getSector(lines.get(2));
                if(sector_n1!=2&&sector_n2!=2&&sector_p1!=2){
                    hdata.fill(cm.mass());                    
                    hreg.fill(cms.mass());
                    hdata2e.fill(lve.p(), cm.mass());
                    hdata2p.fill(lvpp.p(), cm.mass());
                    h2_ep.fill(lve.p(),lves.p());h2_pp.fill(lvpp.p(),lvpps.p());
                    hreg2e.fill(lves.p(), cms.mass());
                    hreg2p.fill(lvpps.p(), cms.mass());
                    if(lve.p()>2.5)
                    {
                        hdatagt2p5.fill(cm.mass());                    
                        hreggt2p5.fill(cms.mass());
                    } else {
                         hdatalt2p5.fill(cm.mass());                    
                        hreglt2p5.fill(cms.mass());
                    }
                }
                
                LorentzVector lve2 = DataAnalysisRegression.getParticle(
                        lines.get(1).trim(), new int[]{4,5,6}, 0.0005);
                LorentzVector lvpm2 = DataAnalysisRegression.getParticle(
                        lines.get(0).trim(), new int[]{4,5,6}, 0.13957);
                LorentzVector lvpp2 = DataAnalysisRegression.getParticle(
                        lines.get(2).trim(), new int[]{4,5,6}, 0.13957);
                
                LorentzVector lves2 = DataAnalysisRegression.getParticle(
                        lines.get(1).trim(), new int[]{24,25,26}, 0.0005);
                LorentzVector lvpms2 = DataAnalysisRegression.getParticle(
                        lines.get(0).trim(), new int[]{24,25,26}, 0.13957);
                LorentzVector lvpps2 = DataAnalysisRegression.getParticle(
                        lines.get(2).trim(), new int[]{24,25,26}, 0.13957);
                
                LorentzVector  cm2 = DataAnalysisRegression.getCM();
                LorentzVector cms2 = DataAnalysisRegression.getCM();
                
                cm2.sub(lve2).sub(lvpm2).sub(lvpp2);
                cms2.sub(lves2).sub(lvpms2).sub(lvpps2);
                                                
                //hdata.fill(cm2.mass());
                //hreg.fill(cms2.mass());
            }
        }
        TDirectory dir = new TDirectory();
        dir.add(directory, hdata,hreg,hdatagt2p5,hreggt2p5,hdatalt2p5,hreglt2p5);
        dir.add(directory, h2_pp,h2_ep);
        dir.add(directory, hdata2e,hdata2p,hreg2e,hreg2p);

        dir.write(exportFile);
    }
    
    public static LorentzVector getParticle(String line, int[] columns, double mass){
        double[] vec = DataAnalysisRegression.getColumns(line.trim(), columns);
        return DataAnalysisRegression.fromPTF(vec, mass);
        //return LorentzVector.withPxPyPzM(vec[0], vec[1], vec[2], mass);
    }
    
    public static LorentzVector getParticleSector(String line, int[] columns, double mass){
        double[] vec = DataAnalysisRegression.getColumns(line, columns);
        int   sector = DataAnalysisRegression.getSector(line);        
        LorentzVector vL = DataAnalysisRegression.fromPTFsec(sector, vec, mass);
        //vL.rotateZ(angle);
        return vL;
    }
    
    public static void main(String[] args){
        
        //double[] array = {"a","b"};
        
        //String file = "/Users/gavalian/Work/dataspace/pid/results/c_extract_regression_data_1n1p_hb.txt.pred.norm";        
        //String file = "/Users/gavalian/Downloads/new_res/h_extract_regression_data_1n1p_hb_res_from_i.txt" ;
        //String file = "/Users/gavalian/Work/software/project-10a.0.4/j4np-1.0.4/c_extract_regression_data_1n1p_hb.txt" ;
        String file = "/Users/gavalian/Work/Software/project-10.4/studies/jupyter/mc_e1pi_hb.txt.pred.norm" ;
        String file2 = "/Users/gavalian/Work/Software/project-10.4/studies/jupyter/mc_e2pi_hb.txt.pred.norm" ;
        String file3 = "/Users/gavalian/Work/Software/project-10.4/studies/jupyter/data_e1p_hb.txt.pred.norm" ;
//String file = "/Users/gavalian/Work/Software/project-10.4/data/res_data3/c_extract_regression_data_1n1p_hb_res_from_b.txt" ;
        String file4 = "/Users/gavalian/Work/Software/project-10.4/studies/jupyter/xboost/c_extract_regression_data_1n1p_hb.txt.pred.norm.a";
        String file5 = "/Users/gavalian/Work/Software/project-10.4/studies/jupyter/xboost/c_extract_regression_data_1n1p_hb.txt.pred.norm.a.xgb";

        String file6 = "/Users/gavalian/Work/Software/project-10.4/studies/jupyter/xboost/c_extract_regression_data_1n1p_hb.txt.pred.norm.b";
        String file7 = "/Users/gavalian/Work/Software/project-10.4/studies/jupyter/xboost/c_extract_regression_data_1n1p_hb.txt.pred.norm.b.xgb";
        String file8 = "/Users/gavalian/Work/Software/project-10.4/studies/regression/c_extract_regression_data_1n1p_hb.txt.pred.norm.xgb_n";
        String file9 = "/Users/gavalian/Work/Software/project-10.4/studies/regression/c_extract_regression_data_1n1p_hb.txt.pred.norm.xgb_n2";
        
        String file10 = "/Users/gavalian/Work/Software/project-10.4/distribution/artin/projects/pe/xgboost-java/mc_e1pi_hb.norm.eval.txt";
        //String file2 = "/Users/gavalian/Work/dataspace/pid/results/d_extract_regression_data_1n1p_hb.txt.pred.norm";
        DataAnalysisRegression ana = new DataAnalysisRegression();
        ana.directory = "/mc_e1p";
        ana.analyze(file, "inference.twig");
        
        ana.directory = "/mc_e2p";
        ana.analyzeThree(file2, "inference.twig");
        
        ana.directory = "/data_e1p";
        ana.analyze(file3, "inference.twig");
        
        ana.directory = "/data_e1p_a";
        ana.analyze(file4, "inference.twig");
        
        ana.directory = "/data_e1p_a_xgb";
        ana.analyze(file5, "inference.twig");
        
        ana.directory = "/data_e1p_b";
        ana.analyze(file6, "inference.twig");
        
        ana.directory = "/data_e1p_b_xgb";
        ana.analyze(file7, "inference.twig");
        
        ana.directory = "/data_e1p_b_xgb_new";
        ana.analyze(file8, "inference.twig");
        
        ana.directory = "/data_e1p_b_xgb_new_2";
        ana.analyze(file9, "inference.twig");
        
        ana.directory = "/mc_e1p_xgb";
        //ana.print = true;
        ana.analyze(file10, "inference.twig");
        /*String data = """
                      """;
        */
    }
}
