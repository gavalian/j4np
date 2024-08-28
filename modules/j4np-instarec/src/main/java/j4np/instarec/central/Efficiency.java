/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.central;

import j4np.geom.prim.Vector3D;
import j4np.hipo5.data.Bank;
import j4np.hipo5.io.HipoReader;
import j4np.physics.Vector3;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import twig.data.GraphErrors;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class Efficiency {
    
    public int layer = 2;
    public int counter = 0;
    public int counterMatch = 0;
    public double[] cuts = new double[]{0.5,2.0};
    public int sector = 1;
    
    public Efficiency(){
        
    }
    
    public Efficiency(int s, int l){
        this.setSector(s); this.setLayer(l);
    }
    
    public void setLayer(int l){
        if(l<1||l>6){
           System.out.println(" error: unknown layer " + l + "  setting layer to 1"); layer = 1; return;
        }
        layer = l;
    }
    
    public void setSector(int s){
        sector = s;
    }
    public boolean isValid(int[] ids){
        for(int i = 0; i < 3; i++) if(ids[i]<=0) return false;
        return true;
    }
    
    public List<Vector3D> getIntersection(Bank b, int layer, int id){
        List<Vector3D> list = new ArrayList<>();
        for(int i = 0; i < b.getRows(); i++){
            int tlayer = b.getInt("layer", i);
            int    tid = b.getInt("id", i);
            int    sec = b.getInt("sector", i);
            
            //if(sec==sector) System.out.printf(" sector = %d , layer = %d, id == %d passed = %d\n", sec,tlayer,tid,id);
            if(layer==tlayer&&id==tid&&sec==sector) {
                list.add(new Vector3D(b.getFloat("x", i), b.getFloat("y", i),b.getFloat("z", i)));
                //System.out.println("added");
            }
        }
        return list;
    }
    
    public int countExcept(int[] ids, int layer){
        int counter = 0;
        for(int i = 3; i < ids.length; i++) if(i!=layer&&ids[i]>0) counter++;
        return counter;
    }
    
    public List<Vector3D> getClusters(Bank b, int layer){
        List<Vector3D> list = new ArrayList<>();
        for(int i = 0; i < b.getRows(); i++){
            int tlayer = b.getInt("layer",i);
            if(layer==tlayer) list.add(new Vector3D(
                    b.getFloat("x1", i),b.getFloat("y1", i),b.getFloat("z1", i)
            ));
        }
        return list;
    }
    public double distance(Vector3D a, Vector3D b){
        return Math.sqrt(
                (a.x()-b.x())* (a.x()-b.x())
                + (a.y()-b.y())* (a.y()-b.y())
                + (a.z()-b.z())* (a.z()-b.z())
                );
    }
    public double distancePerp(Vector3D a, Vector3D b){
        return Math.sqrt(
                (a.x()-b.x())* (a.x()-b.x())
                + (a.y()-b.y())* (a.y()-b.y())                
                );
    }
    public String summary(){
        return String.format("sector = %4d, layer = %5d, efficiency = %9.6f", 
                sector,layer, ((double) counterMatch )/counter);
    }
    
    public void process(Bank[] b){
        
        if(b[0].getRows()!=1) return;
        
        
        //b[0].show();
        int[] ids = b[0].getIntArray(9, "Cross1_ID", 0);
        double quality = 10000.0;
        
        //System.out.println("****************************");        
        //int counter = 0;
        //int counterMatch = 0;

        int trkID = b[0].getInt("ID", 0);
        List<Vector3D> list = getIntersection(b[1],layer+6,trkID);
        if(list.size()>0){
            System.out.println("*****");
            System.out.println("  SIZE OF INTERSECTION BANK = " + list.size() + "  " + trkID);
            System.out.println(list.size() + "   " + Arrays.toString(ids) );
            System.out.println(countExcept(ids,layer+2));
        }
        if(isValid(ids)&&countExcept(ids,layer+2)>4){
            
            //System.out.println(Arrays.toString(ids));
            //System.out.println("TRACK ID " + b[0].getInt("ID", 0) );
            System.out.println(list.size() + "   " + Arrays.toString(ids));
            
            //int trkID = b[0].getInt("ID", 0);
            //List<Vector3D> list = getIntersection(b[1],layer+6,trkID);
            
            if(!list.isEmpty()){
                System.out.println(" we are inside");
                List<Vector3D> clusters = getClusters(b[2],layer);
                //System.out.println(list.get(0).toString());    
                counter++;
                boolean matchFound = false;
                for(int j = 0; j < clusters.size(); j++){
                    //double distance = this.distance(list.get(0), clusters.get(j));
                    //System.out.println(clusters.get(j));                    
                    if(layer==1||layer==4||layer==6){
                        quality = (clusters.get(j).z()-list.get(0).z());
                        if(quality<cuts[0]) matchFound = true;
                    } else {                        
                        quality = Math.abs(list.get(0).phi()*57.29-clusters.get(j).phi()*57.29);
                                //this.distancePerp(list.get(0),clusters.get(j));                        
                        if(quality<cuts[1]) matchFound = true;
                    }
                    //System.out.printf(" CLUSTER %5d , quality %f\n",j,quality);
                    //System.out.println("z distance = " + (clusters.get(j).z()-list.get(0).z()));
                    //System.out.println("\t " + j + "  distance = " + distance);
                    //if(quality<0.1) matchFound = true;
                }
                if(matchFound) {
                    counterMatch++;
                }
            }

    
            //System.out.println(list.size());
        }
        
        //System.out.printf("%d/%d \n",counter, counterMatch);
    }
    
    public static void main(String[] args){
        
        
        //String file = "/Users/gavalian/Work/Software/project-11.0/study/central/AISample_1.hipo";
        String file = "rec_clas_016878.evio.00000.hipo";
        
        
        if(args.length>0) file = args[0];
        
        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks("CVTRec::Tracks","CVTRec::Trajectory","BMTRec::Clusters");
        //Efficiency eff = new Efficiency();
        GraphErrors xy = new GraphErrors();
        GraphErrors xy2 = new GraphErrors();
        //TGCanvas c = new TGCanvas();
        //c.draw(xy);
        //c.draw(xy2,"same");
        xy2.attr().set("mc=5");
        
        int counter = 0;
        
        Efficiency e1 = new Efficiency(2,1);
        while(r.nextEvent(b)){ 
            e1.process(b);
            /*List<Vector3D> vec = e1.getIntersection(b[1], 7, 1);
            for(int i = 0; i < b[1].getRows(); i++){
                int sector = b[1].getInt("sector", i);
                int layer = b[1].getInt("layer", i);
                
                if(sector==2&&layer==7) 
                {

                    //System.out.println("oooopa  "  + vec.size());
                }
            }
            System.out.println( "size = " + vec.size());*/
        }
        
        System.out.println(e1.summary());
        /*
        List<Efficiency> eff = new ArrayList<>();
        for(int l = 1; l <=6; l++)
            for(int s = 1; s <= 3; s++) eff.add(new Efficiency(s,l));
        while(r.nextEvent(b)){
            //b[0].show();
            counter++; //if (counter>1000) break;
            if(b[0].getRows()==1){
                for(Efficiency e : eff) e.process(b);
            }
        }*/
        //System.out.printf("%d/%d efficiency = %f\n",eff.counter, eff.counterMatch, ((double) eff.counterMatch )/eff.counter);
        //for(Efficiency e : eff) System.out.println(e.summary());
    }
}
