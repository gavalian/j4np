/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.rich;

import j4np.data.base.DataUtils;
import j4np.geom.prim.Vector3D;

import j4np.utils.io.DataArrayUtils;
import j4np.utils.io.TextFileReader;
import j4np.utils.io.TextFileWriter;
import twig.data.Range;

/**
 *
 * @author gavalian
 */
public class RichDataPrepare {
    
    public static boolean translate = true;
    
    /*public static Range[] ranges = new Range[]{
        new Range(1.0,9.0),
        new Range(-1050,1050),
        new Range(-2200,-100),
        new Range(-0.2,0.2),
        new Range(-4,0.0),
        new Range(0.9,1.0),
        new Range(-750,750),
        new Range(-2600,-1200),
        new Range(0.,0.35)
    };*/
    
     public static Range[] ranges = new Range[]{
        new Range(2.0,9.0),
        new Range(-1050,1050),
        new Range(-1750., -140.0),
        new Range(-0.24,0.24),
        new Range(-0.24,0.24),
        new Range(0.965,1.0),
        new Range(-750.0 , 1200.),
        new Range(-2600,-1200),
        new Range(0.24,0.35)
    };
     
    public static void export(String file){
        TextFileReader r = new TextFileReader(file);
        TextFileWriter wt = new TextFileWriter();
        wt.open("output_tr.csv");
        TextFileWriter we = new TextFileWriter();
        we.open("output_ev.csv");
        
        while(r.readNext()==true){
            String[] tokens = r.getString().split(",");
            int      nHits  = Integer.parseInt(tokens[1]);
            int pid = Integer.parseInt(tokens[0]);
            

            //System.out.println(" pid = " + Integer.parseInt(tokens[0]) + " nhits = " + nHits);
            Vector3D vec = new Vector3D(
                    Double.parseDouble(tokens[3]),
                            Double.parseDouble(tokens[4]),
                                    Double.parseDouble(tokens[5]
                    ));
            
            Vector3D dir = new Vector3D(
                    Double.parseDouble(tokens[6]),
                            Double.parseDouble(tokens[7]),
                                    Double.parseDouble(tokens[8]
                    ));
            
            //vec.show();
            double theta = vec.theta()*57.29;
            
            vec.rotateX(Math.toRadians(25));
            dir.rotateX(Math.toRadians(25));
            //vec.show();
            //dir.show();
            
            if(pid==321){
                //System.out.println(" nhits = " + nHits + " length = " + tokens.length);
                for(int k = 0; k < nHits; k++){
                    
                    Vector3D pos = new Vector3D(Double.parseDouble(tokens[9+k*4+1]),
                            Double.parseDouble(tokens[9+k*4+2]),
                                    Double.parseDouble(tokens[9+k*4+3]));
                    pos.rotateX(Math.toRadians(25));
                    double  eta = Double.parseDouble(tokens[9+k*4]);
                    double[] data = new double[]{
                        Double.parseDouble(tokens[2]),vec.x(),vec.y(),
                            dir.x(),dir.y(),dir.z(),
                            pos.x(),pos.y(), eta
                    };
                    double[] datan = new double[9];
                    String dataString = "";
                    if(RichDataPrepare.translate==true){
                        for(int i = 0; i < datan.length; i++) datan[i] = ranges[i].translate(data[i]);
                        dataString = DataArrayUtils.doubleToString(datan, ",");
                    } else {
                        dataString = DataArrayUtils.doubleToString(data, ",");
                    }
                    /*String data = String.format("%s,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f",
                            tokens[2],vec.x(),vec.y(),
                            dir.x(),dir.y(),dir.z(),
                            pos.x(),pos.y(), eta
                            );*/
                    wt.writeString(dataString);
                }
            }
            
            if(pid==2212||pid==211||pid==321){
                //System.out.printf(" theta = %f\n",theta);
                StringBuilder str = new StringBuilder();
                str.append(pid);
                double[] data = new double[]{
                        Double.parseDouble(tokens[2]),vec.x(),vec.y(),
                            dir.x(),dir.y(),dir.z()                           
                    };
                double[] datan = new double[6];
                for(int i = 0; i < datan.length; i++) datan[i] = ranges[i].translate(data[i]);
                
                str.append(",").append(DataArrayUtils.doubleToString(datan, ","));
                for(int k = 0; k < nHits; k++){
                    Vector3D pos = new Vector3D(Double.parseDouble(tokens[9+k*4+1]),
                            Double.parseDouble(tokens[9+k*4+2]),
                                    Double.parseDouble(tokens[9+k*4+3]));
                    pos.rotateX(Math.toRadians(25));
                    double  eta = Double.parseDouble(tokens[9+k*4]);
                    double[] hdata = new double[]{ pos.x(),pos.y(), eta};
                    double[] hdatan = new double[3];
                    if(RichDataPrepare.translate==true){
                        for(int i = 0; i < hdatan.length; i++) hdatan[i] = ranges[i+6].translate(hdata[i]);
                        str.append(",").append(DataArrayUtils.doubleToString(hdatan, ","));
                    } else {
                        for(int i = 0; i < hdatan.length; i++) hdatan[i] = hdata[i];
                        str.append(",").append(DataArrayUtils.doubleToString(hdatan, ","));
                    }
                }
                we.writeString(str.toString());
            }
        }
        
        wt.close();
        we.close();
    }
    
    public static void export2(String file){
        TextFileReader r = new TextFileReader(file);
        TextFileWriter wt = new TextFileWriter();
        wt.open("output_tr.csv");
        TextFileWriter we = new TextFileWriter();
        we.open("output_ev.csv");
        
        while(r.readNext()==true){
            String[] tokens = r.getString().split(",");
            int      nHits  = Integer.parseInt(tokens[1]);
            int pid = Integer.parseInt(tokens[0]);
            
            Vector3D particle = new Vector3D();
            particle.setMagThetaPhi(
                    Double.parseDouble(tokens[2]),
                    Double.parseDouble(tokens[3]),
                    Double.parseDouble(tokens[4])
            );
                
            //System.out.println(" pid = " + Integer.parseInt(tokens[0]) + " nhits = " + nHits);
            Vector3D vec = new Vector3D(
                    Double.parseDouble(tokens[5]),
                            Double.parseDouble(tokens[6]),
                                    Double.parseDouble(tokens[7]
                    ));
            
            Vector3D dir = new Vector3D(
                    Double.parseDouble(tokens[8]),
                            Double.parseDouble(tokens[9]),
                                    Double.parseDouble(tokens[10]
                    ));
            
            //vec.show();
            double theta = vec.theta()*57.29;
            
            vec.rotateX(Math.toRadians(25));
            dir.rotateX(Math.toRadians(25));
            //particle.rotateX(Math.toRadians(25));
            //vec.show();
            //dir.show();
            
            if(pid==321){
                //System.out.println(" nhits = " + nHits + " length = " + tokens.length);
                for(int k = 0; k < nHits; k++){
                    
                    Vector3D pos = new Vector3D(Double.parseDouble(tokens[9+k*4+1]),
                            Double.parseDouble(tokens[11+k*4+2]),
                                    Double.parseDouble(tokens[11+k*4+3]));
                    pos.rotateX(Math.toRadians(25));
                    double  eta = Double.parseDouble(tokens[11+k*4]);
                    double[] data = new double[]{
                        Double.parseDouble(tokens[2]),vec.x(),vec.y(),
                            dir.x(),dir.y(),dir.z(),
                            pos.x(),pos.y(), eta
                    };
                    double[] datan = new double[9];
                    String dataString = "";
                    if(RichDataPrepare.translate==true){
                        for(int i = 0; i < datan.length; i++) datan[i] = ranges[i].translate(data[i]);
                        dataString = DataArrayUtils.doubleToString(datan, ",");
                    } else {
                        dataString = DataArrayUtils.doubleToString(data, ",");
                    }
                    /*String data = String.format("%s,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f",
                            tokens[2],vec.x(),vec.y(),
                            dir.x(),dir.y(),dir.z(),
                            pos.x(),pos.y(), eta
                            );*/
                    wt.writeString(dataString);
                }
            }
            
            if(pid==2212||pid==211||pid==321){
                //System.out.printf(" theta = %f\n",theta);
                StringBuilder str = new StringBuilder();
                str.append(pid);
                str.append(",").append(particle.mag()).
                        append(",").append(particle.theta()*57.29)
                        .append(",").append(particle.phi());
                
                double[] data = new double[]{
                        Double.parseDouble(tokens[2]),vec.x(),vec.y(),
                            dir.x(),dir.y(),dir.z()                           
                    };
                double[] datan = new double[6];
                for(int i = 0; i < datan.length; i++) datan[i] = ranges[i].translate(data[i]);
                
                str.append(",").append(DataArrayUtils.doubleToString(datan, ","));
                for(int k = 0; k < nHits; k++){
                    Vector3D pos = new Vector3D(Double.parseDouble(tokens[11+k*4+1]),
                            Double.parseDouble(tokens[11+k*4+2]),
                                    Double.parseDouble(tokens[11+k*4+3]));
                    pos.rotateX(Math.toRadians(25));
                    double  eta = Double.parseDouble(tokens[11+k*4]);
                    double[] hdata = new double[]{ pos.x(),pos.y(), eta};
                    double[] hdatan = new double[3];
                    if(RichDataPrepare.translate==true){
                        for(int i = 0; i < hdatan.length; i++) hdatan[i] = ranges[i+6].translate(hdata[i]);
                        str.append(",").append(DataArrayUtils.doubleToString(hdatan, ","));
                    } else {
                        for(int i = 0; i < hdatan.length; i++) hdatan[i] = hdata[i];
                        str.append(",").append(DataArrayUtils.doubleToString(hdatan, ","));
                    }
                }
                we.writeString(str.toString());
            }
        }
        
        wt.close();
        we.close();
    }
    public static void main(String[] args){
        RichDataPrepare.export2("/Users/gavalian/Work/Software/project-10.6/study/rich/gen2/richdata.csv");
    }
}
