/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

import j4np.hipo5.data.CompositeBank;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.io.HipoReader;

import j4np.physics.Vector3;
import java.util.ArrayList;
import java.util.List;
import twig.data.H1F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class Tracks  {

    CompositeNode bank = null; 
    /**
     * s - status (0)
     * f - probability (1)
     * s - sector (2)
     * s - charge (3)
     * f - chi2   (4)
     * 3f - momentum (5,6,7)
     * 3f - vertex   (8,9,10)
     * 6i - cluster numbers (11,12,13,14,15,16)
     * 6f - cluster means   (17,18,19,20,21,22)
     * 6f - cluster position on wire L6
     * @return 
     */
    
    
    double[]   rotateMatrix    = new double[]{0.0,-60.0,-120.,-180,-240.0,-300.0};    
    double[]   normalizePosMin = new double[]{ 0., 0.0,  -1.5};
    double[]   normalizePosMax = new double[]{10., 1.0,   0.5};    
    double[]   normalizeNegMin = new double[]{0.,  0.0 , -0.5};
    double[]   normalizeNegMax = new double[]{10., 1.0  , 1.5};
    
    
    public Tracks(){
        bank =  new CompositeNode(32000,1,"sfssf3f3f6i6f6f",100000);
        bank.setRows(0);
    }
    
    public Tracks(int rows){
        bank =  new CompositeNode(32000,1,"sfssf3f3f6i6f6f",rows);
        bank.setRows(0);
    }
    
    public int count(int row){
        int counter = 0; 
        for(int i = 0; i < 6; i++) if(bank.getInt(11+i, row)>0) counter++;
        return counter;
    }
    
    public int size() {
        return this.bank.getRows();
    }
    
    public double probability(int row){
        return this.bank.getDouble(1, row);
    }
    
    public int getRows(){ return bank.getRows();}
    
    public void vertex(Vector3 v, int row){
        v.setXYZ(
                bank.getDouble(8,row), 
                bank.getDouble(9,row), 
                bank.getDouble(10,row)
                );
    }
    public int sector(int row){ return bank.getInt(2, row); }
    public void vector(Vector3 v, int row){
        v.setXYZ(
                bank.getDouble(5,row), 
                bank.getDouble(6,row), 
                bank.getDouble(7,row)
                );
    }
    
    public double chi2(int row){ return bank.getDouble(4, row);}
    public int    charge(int row){return bank.getInt(3, row);}
    public int    status(int row){ return bank.getInt(0, row);}
    
    public void setStatus(int row, int status){ bank.putShort(0, row, (short) status);}
    
    public int getHighestIndex(int start, int end){
        double  prob = probability(start);
        int    index = start;
        for(int row = start; row <= end; row++){
            double p = probability(row);
            if(p>=prob){prob = p; index = row;}
        }
        return index;
    }
    
    public int getHighestIndex(int start, int end, int[] statusTable){
        //double  prob = probability(start);
        //int    index = start;
        double   prob = 0.0;
        int     index = -1;
        for(int row = start; row <= end; row++){
            int status = this.dataNode().getInt(0, row);
            boolean consider = false;
            for(int s = 0; s < statusTable.length; s++){
                if(status==statusTable[s]) consider = true;
            }
            if(consider){
                double p = probability(row);
                if(p>=prob){prob = p; index = row;}            
            }
        }
        return index;
    }
    
    public void getClusters(int[] clusters, int row){
        for(int i = 0; i < 6; i++){
            clusters[i] = this.bank.getInt(11+i, row);
        }
    }
    
    public static int match(int[] a, int[] b){
        int count = 0; 
        for(int i  = 0; i < a.length; i++) if(a[i]==b[i]) count++;
        return count;
    }
    
    public void getInput(float[] input, int row) {
        int nrows = bank.getRows();
        for(int i = 0; i < 6; i++) input[i] = (float) (bank.getDouble(17+i, row)/112.0);
    }
    
    public void getInput12(float[] input, int row) {
        int nrows = bank.getRows();
        for(int i = 0; i < 6; i++){
            input[i*2  ] = (float) (bank.getDouble(17+i, row)/112.0);
            input[i*2+1] = (float) (bank.getDouble(23+i, row)/112.0);
        }
    }
    
    public void getInput6(float[] input, int row) {
        int nrows = bank.getRows();
        for(int i = 0; i < 6; i++){
            float w1 = (float) bank.getDouble(17+i, row);
            float w6 = (float) bank.getDouble(23+i, row);
            input[i] = (0.5f*(w6+w1))/112.0f;
        }
    }
    
    protected int maximumBin(float[] array){
        int   bin = 0;
        float max = array[0];
        for(int i = 0; i < array.length; i++){
            if(array[i]>max){max=array[i];bin=i;}
        }
        return bin;
    }
    
    public boolean match(int source, int row){
        for(int i = 0; i < 6; i++){
            if(dataNode().getInt(i+11, source)==
                  dataNode().getInt(i+11, row)) return true;
        }
        return false;
    }
    
    public CompositeNode dataNode(){return bank;}
    
    public int contains(int row1, int row2){
        int counter = 0;
        for(int i = 0; i < 6; i++)
            if(bank.getInt(11+i, row1)==bank.getInt(11+i, row2)) counter++;
        return counter;
    }
        
    public int contains(int row, int[] clusters){
        int counter = 0;
        for(int i = 0; i < 6; i++)
            if(bank.getInt(11+i, row)==clusters[i]) counter++;
        return counter;
    }
    public int findMatch(int[] clusters){
        for(int i = 0; i < this.getRows(); i++){
            if(this.contains(i, clusters)==6) return i;
        }
        return -1;
    }
    
    public static int countClusters(int[] cid){
        int count = 0; for(int i = 0; i < cid.length; i++)
            if(cid[i]>0) count++;
        return count;
    }
    public double distance(int row, float[] features){
        //int counter = 0;
        double distance = 0.0;
        for(int i = 0; i < 12; i++){
            double f = bank.getDouble(i+17, row)/112.0;
            distance += Math.sqrt((features[i]-f)*(features[i]-f));
        }
        return distance;
    }
    public static double distance(Tracks a, int a_i, Tracks b, int b_i){
        double distance = 0.0;
        for(int i = 0; i < 12; i++){
            double a_f = a.dataNode().getDouble(17+i, a_i);
            double b_f = b.dataNode().getDouble(17+i, b_i);
            distance += Math.sqrt((a_f-b_f)*(a_f-b_f));
        }
        return distance;
    }
    /**
     * Calculates euclidean distance between the tracks
     * @param row1
     * @param row2
     * @return 
     */
    public double distance(int row1, int row2){
        double distance = 0.0;
        for(int i = 0; i < 12; i++) 
            distance += 
                    (bank.getDouble(i+17, row1)-bank.getDouble(i+17, row2))*
                    (bank.getDouble(i+17, row1)-bank.getDouble(i+17, row2))
                    ; 
        return distance;
    }
    
    public void applyOutput(float[] output, int row) {
        int bin = this.maximumBin(output);
        if(bin!=0){
            //System.out.println("bins = " + bin);
            this.bank.putFloat(1, row, output[bin]);
            this.bank.putShort(0, row, (short) bin);
            
        } else {
            this.bank.putShort(0, row, (short) -1);
            this.bank.putFloat(1, row,  (float) 0.0);
        }
    }


    public void show() {
        System.out.println("LIST SIZE = " + bank.getRows());
        bank.print();
    }
    
    public void show(int row) {
        bank.print(row);
    }
    
    public  Vector3 getVector(int row, float[] vector){
        int sector = this.sector(row);
        int charge = this.charge(row);
        return getVector(sector,charge, vector);
    }
    
    public  float[] getVectorOutput(Vector3 v,int row){
        int sector = this.sector(row);
        int charge = this.charge(row);
        return this.getOutput(v, sector, charge);
    }
    
    private Vector3 getVector(int sector, int charge, float[] vec){
        double p = vec[0]*10.0;
        double t = vec[1];
        double f = vec[2];
        if(charge>0){
            f = vec[2]*2 - 1.5;
        } else {
            f = vec[2]*2 - 0.5;
        }
        Vector3 v = new Vector3();
        v.setMagThetaPhi(p, t, f);
        v.rotateZ(-Math.toRadians(rotateMatrix[sector-1]));
        return v;
    }
    
    protected float[]  getOutput(Vector3 v, int sector, int charge){
        float[] output = new float[3];
        this.transform(v, sector);
        if(charge>0){
            output[0] = (float) getNormalized(v.mag(),normalizePosMin[0],normalizePosMax[0]);
            output[1] = (float) getNormalized(v.theta(),normalizePosMin[1],normalizePosMax[1]);
            output[2] = (float) getNormalized(v.phi(),normalizePosMin[2],normalizePosMax[2]);
        } else {
            output[0] = (float) getNormalized(v.mag(),normalizeNegMin[0],normalizeNegMax[0]);
            output[1] = (float) getNormalized(v.theta(),normalizeNegMin[1],normalizeNegMax[1]);
            output[2] = (float) getNormalized(v.phi(),normalizeNegMin[2],normalizeNegMax[2]);
        }        
        return output;
    }
    
    protected boolean checkOutput(float[] output){
        if(output[0]>1.0||output[0]<0.0) return false;
        if(output[1]>1.0||output[1]<0.0) return false;
        return (output[2]>0.0&&output[2]<1.0);
        //return (output[1]>0.0&&output[1]<1.0);
    }
    
    protected double   getNormalized(double x, double min, double max){
        return (x-min)/(max-min);
    }
    
    protected void transform(Vector3 v, int sector){
        v.rotateZ(Math.toRadians(rotateMatrix[sector-1]));
    }
    
    public static List<Tracks> read(String file, int max,  long... tags){
        HipoReader r = new HipoReader();
        r.setTags(tags);
        r.open(file);
        List<Tracks> list = new ArrayList<>();
        int counter = 0;
        Event e = new Event();
        while(r.hasNext()==true&&counter<max){
            
            counter++;
            
            r.nextEvent(e);
            Node  ndesc = e.read(1001, 1);
            Node  nchi2 = e.read(1001, 2);
            Node nmeans = e.read(1001, 4);
            
            Node nodep = e.read(1001, 6);            
            Node nodev = e.read(1001, 7);

            Tracks t = new Tracks();
            t.bank.setRows(1);
            t.bank.putShort(0, 0, (short) 2);
            t.bank.putFloat(1, 0, (float) 0.0);
            
            t.bank.putShort(2, 0, (short) ndesc.getInt(0));
            t.bank.putShort(3, 0, (short) ndesc.getInt(1));
            t.bank.putFloat(4, 0, (float) nchi2.getFloat(0));
            
            t.bank.putFloat(5, 0, (float) Math.sqrt(
                    nodep.getFloat(0) * nodep.getFloat(0)+
                    nodep.getFloat(1) * nodep.getFloat(1)+
                    nodep.getFloat(2) * nodep.getFloat(2)                    
            ));
            t.bank.putFloat(8,  0, nodev.getFloat(0));
            t.bank.putFloat(9,  0, nodev.getFloat(1));
            t.bank.putFloat(10, 0, nodev.getFloat(2));
            
            for(int k = 0; k<6; k++) t.bank.putFloat(k+17, 0, (float) (nmeans.getFloat(k)/112.));
            list.add(t);
        }
        return list;
    }
    
    
    
    public static void main(String[] args){
        
        Tracks list = new Tracks();
        list.bank.putFloat(5, 0, 5.6f);
        list.bank.putFloat(6, 0, 4.6f);
        list.bank.putFloat(7, 0, 3.6f);
        list.bank.setRows(1);
        list.show();
        list.bank.setRows(2);
        list.show();
        //CompositeNode bank =  new CompositeNode(1,2,"sfssf3f3f6i6f",4);
        //bank.show();
        //bank.print();
        /*
        TrackDataList list = new TrackDataList();
        list.bank.show();
        NeuralClassifier nc = new NeuralClassifier();
        nc.load("clas12rga.network", 5442);
        List<TrackDataList> tracks = TrackDataList.read("run_5442_out_tr.h5",2500,3);
        
        for(TrackDataList t : tracks) t.show();
        
        for(TrackDataList t : tracks) nc.evaluate(t);
        System.out.println("------ results");
        H1F h = new H1F("h",120,-80,80);
        TGCanvas c = new TGCanvas();
        c.draw(h);
        for(TrackDataList t : tracks) { 
            t.show(); h.fill(t.bank.getDouble(10, 0));
        }*/
        
        
    }
}
