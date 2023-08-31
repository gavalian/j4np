/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.data;

import j4np.hipo5.data.CompositeBank;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.io.HipoReader;
import j4np.neural.networks.NeuralClassifier;
import j4np.neural.networks.NeuralDataList;
import j4np.physics.Vector3;
import java.util.ArrayList;
import java.util.List;
import twig.data.H1F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class Tracks implements NeuralDataList {

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
     * @return 
     */
    
    public Tracks(){
        bank =  new CompositeNode(32000,1,"sfssf3f3f6i6f",100000);
        bank.setRows(0);
    }
    
    public Tracks(int rows){
        bank =  new CompositeNode(32000,1,"sfssf3f3f6i6f",rows);
        bank.setRows(0);
    }
    
    @Override
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
    
    public int getHighestIndex(int start, int end){
        double  prob = probability(start);
        int    index = start;
        for(int row = start; row <= end; row++){
            double p = probability(row);
            if(p>=prob){prob = p; index = row;}
        }
        return index;
    }
    
    public void getClusters(int[] clusters, int row){
        for(int i = 0; i < 6; i++){
            clusters[i] = this.bank.getInt(11+i, row);
        }
    }
    
    @Override
    public void getInput(float[] input, int row) {
        int nrows = bank.getRows();
        for(int i = 0; i < 6; i++) input[i] = (float) (bank.getDouble(17+i, row)/112.0);
    }
    
    protected int maximumBin(float[] array){
        int   bin = 0;
        float max = array[0];
        for(int i = 0; i < array.length; i++){
            if(array[i]>max){max=array[i];bin=i;}
        }
        return bin;
    }
    
    public CompositeNode dataNode(){return bank;}
    
    @Override
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

    @Override
    public void show() {
        System.out.println("LIST SIZE = " + bank.getRows());
        bank.print();
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
