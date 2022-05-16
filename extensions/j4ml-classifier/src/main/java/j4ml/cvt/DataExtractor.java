/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.cvt;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.physics.Vector3;
import java.util.Arrays;

/**
 *
 * @author gavalian
 */
public class DataExtractor {
    public DataExtractor(){
        
    }
    
    public int countcvt(int[][] a){
        int n = 0;
        for(int x = 0; x < 84; x++){
            int c = 0;
            for(int y = 0 ; y < 256; y++){
                if(a[x][y]>0) c++;
            }
            if(c>0) n++;
        }
        return n;
    }
    
    public int countbmt(int[][] a){
        int n = 0;
        for(int x = 84; x < 87; x++){
            int c = 0;
            for(int y = 0 ; y < 256; y++){
                if(a[x][y]>0) c++;
            }
            if(c>0) n++;
        }
        return n;
    }
    
    public void fillBMT(Bank b, int[][] a, boolean select){
        int nrows = b.getRows();
        
        for(int i = 0; i < nrows;i++){
            int tid = b.getInt("trkID", i);
            int layer = b.getInt("layer", i);
            boolean write = true;
            
            if(select==true&&tid!=1) write = false;
            
            if(write&&(layer==2||layer==3||layer==5)){
              double xc = b.getFloat("x1",i);
              double yc = b.getFloat("y1",i);
              double phi = Math.atan2(yc, xc);
              double fideg = Math.toDegrees(phi);
              int coord = (int) (256*((fideg+180)/360));
              //System.out.printf(" fi angle = %f  <=> %d\n",Math.toDegrees(phi), coord);
              if(layer==2) a[84][coord] = 1;
              if(layer==3) a[85][coord] = 1;
              if(layer==5) a[86][coord] = 1;
            }
        }
    }
    
    public void fillBST(Bank b, int[][] a, boolean select){
        int nrows = b.getRows();
        
        for(int i = 0; i < nrows;i++){
            int tid = b.getInt("trkID", i);
            boolean write = true;
            
            if(select==true&&tid!=1) write = false;
            
            if(write){
                int layer = b.getInt("layer", i);
                int sector = b.getInt("sector", i);
                int strip = b.getInt("strip", i);
                if(strip>=1){
                    if(layer==1||layer==2){
                        int coordx = strip-1;
                        int coordy = (sector-1)+(layer-1)*10;
                        a[coordy][coordx] = 1;
                    }
                    if(layer==3||layer==4){
                        int coordx = strip-1;
                        int coordy = 20 + (sector-1)+(layer-3)*14;
                        a[coordy][coordx] = 1;
                    }
                    if(layer==5||layer==6){
                        int coordx = strip-1;
                        int coordy = 48 + (sector-1)+(layer-5)*18;
                        a[coordy][coordx] = 1;
                    }
                }
            }
        }
    }
    
    public String getLSVM(int[][] a){
        StringBuilder str = new StringBuilder();
        int counter = 0;        
        for(int x = 0; x < 90; x++){
           for(int y = 0; y < 256; y++){
               counter++;
               if(a[x][y]>0) str.append(String.format("%d:1.0 ", counter));
           } 
        }
        return str.toString();
    }
    
    public float[] getOutput(int[][] array){
        float[] output = new float[84];
        for(int y = 0; y < 256; y++){
            for(int x = 0; x < 84; x++){
                if(array[x][y]>0) output[x] = 1.0f;
            }
        }
        return output;
    }
    
    public Vector3 getVectorCVT(Bank b){
        double pt = b.getFloat("pt", 0);
        double phi0 = b.getFloat("phi0", 0);
        double tandip = b.getFloat("tandip", 0);

        return new Vector3(pt*Math.cos(phi0), 
                    pt*Math.sin(phi0), pt*tandip);
    }
    
    public Vector3 getVectorMC(Bank b){
        return new Vector3(b.getFloat("px", 0),b.getFloat("py", 0),b.getFloat("pz", 0));
    }
    
    public void process(String file){
        HipoReader r = new HipoReader();        
        r.open(file);
        
        Event evt = new Event();
        Bank bstHits = r.getBank("BSTRec::Hits");
        Bank   bmtCt = r.getBank("BMTRec::Clusters");
        Bank      mc = r.getBank("MC::Particle");
        Bank     cvt = r.getBank("CVTRec::Tracks");
        
        int counter = 0;
        int counterTrue  = 0;
        int counterGhost = 0;
        
        while(r.hasNext()){
            
            counter++;
            r.nextEvent(evt);
            evt.read(bmtCt);
            evt.read(bstHits);
            evt.read(cvt);
            evt.read(mc);
            //evt.show();
            //System.out.println("--- event #");
            
            if(bmtCt.getRows()>0&&bstHits.getRows()>0){
                
                int[][] buffer_output = new int[90][256];
                int[][] buffer_input  = new int[90][256];
                
                this.fillBST(bstHits, buffer_output, true);
                this.fillBMT(bmtCt, buffer_output, true);
                
                this.fillBST(bstHits, buffer_input, false);
                this.fillBMT(bmtCt, buffer_input, false);
                
                int n_bst = countcvt(buffer_output);
                int n_bmt = countbmt(buffer_output);
                int n_tot = countcvt(buffer_input);
                
                if(n_bst>=4&&n_bmt>=1){
                    
                    Vector3  vmc = getVectorMC(mc);
                    Vector3 vcvt = getVectorCVT(cvt);
                    
                    String  inString = this.getLSVM(buffer_input);
                    String outString = this.getLSVM(buffer_output);
                    float[] regression = this.getOutput(buffer_output);
                    
                    //System.out.printf("input %8d <--> output %8d\n",count(buffer_input),count(buffer_output));
                    double res = Math.abs(vmc.mag()-vcvt.mag())/vmc.mag();
                    
                    //System.out.println("0 " + inString);
                    //System.out.println("1 " + outString);
                    double dphi = Math.abs(vmc.phi()-vcvt.phi());
                   double dtheta = Math.abs(vmc.theta()-vcvt.theta());
                    if(res<0.1&&dphi*57.29<1.0&&dtheta*57.29<1.5) System.out.println(n_bst+","+n_tot+","+
                            Arrays.toString(regression).replace("[", "").replace("]", "") 
                            + ";" + inString);
                    
                    //mc.show();
                    //cvt.show();
                    //System.out.println(vmc);
                    //System.out.println(vcvt);
                    
                   if(res<0.25&&dphi*57.29<1.5&&dtheta*57.29<2.5) counterTrue++; else counterGhost++;
                    //System.out.println("diff = " + Math.abs(vmc.mag()-vcvt.mag())/vmc.mag());
                    //System.out.println(outString);
                }
                //this.show(buffer);
            }
            //bmtCt.show();
            //bstHits.show();
            
        }
        
        System.out.printf(" processed = %d, ture = %d, ghost = %d\n",counter,counterTrue,counterGhost);
    }
    
    public int count(int[][] a){
        int counter = 0;
         for(int y = 0; y < 256; y++){
            for(int x = 0; x < 87; x++){
                if(a[x][y]>0) counter++;
            }         
        }
        return counter;
    }
    
    public void show(int[][] a){
        for(int y = 0; y < 256; y++){
            for(int x = 0; x < 87; x++){
                if(a[x][y]>0) System.out.print("X");
                else System.out.print("-");
            }
            System.out.println();
        }
    }
    
    public void processExtract(String file){
        HipoReader r = new HipoReader();        
        r.open(file);
        
        Event evt = new Event();
        Bank bstHits = r.getBank("BSTRec::Hits");
        Bank   bmtCt = r.getBank("BMTRec::Clusters");
        Bank      mc = r.getBank("MC::Particle");
        Bank     cvt = r.getBank("CVTRec::Tracks");
        
        int counter = 0;
        int counterTrue  = 0;
        int counterGhost = 0;
        
        while(r.hasNext()&&counter<18){
            
            counter++;
            r.nextEvent(evt);
            evt.read(bmtCt);
            evt.read(bstHits);
            evt.read(cvt);
            evt.read(mc);
            
            CvtArray2D array = CvtArrayIO.createArray(bstHits, bmtCt, 1);
            int type = array.getType();
            //System.out.println(" type = " + type);
            if(type==1){
                System.out.println(" type = " + type);
                array.show();
            }
        }
    }
    public static void main(String[] args){
        //String file = "/Users/gavalian/Work/DataSpace/cvt/cvt_proton.rec.hipo";
        String file = "/Users/gavalian/Work/DataSpace/cvt/out_proton_0.4_1.6GeV_rga-fall2018_bg45nA.hipo";
        
        DataExtractor ext = new DataExtractor();
        //ext.process(file);
        ext.processExtract(file);
    }
}
