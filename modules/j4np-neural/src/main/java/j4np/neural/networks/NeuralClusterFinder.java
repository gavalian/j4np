/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.networks;

import deepnetts.net.ConvolutionalNetwork;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import twig.data.AsciiPlot;
import twig.data.H1F;
import twig.data.H2F;

/**
 *
 * @author gavalian
 */
public class NeuralClusterFinder {
    CompositeNode dcHits = null;
    CompositeNode dcClusters = null;
    ConvolutionalNetwork neuralNet;
    SchemaFactory  factory = new SchemaFactory();
    protected String networkFile = "etc/clusterFinder2.dnet";
    
    public NeuralClusterFinder(){
        this.init();
    }
    
    public final void init(){
        dcHits = new CompositeNode(12,1,"sssfff",4000);
        dcClusters = new CompositeNode(24,1,   "ssf",4000);
        
        factory.readFile("etc/neuralnetwork.json");
        factory.show();
        try {
            neuralNet = FileIO.createFromFile(networkFile, ConvolutionalNetwork.class);
        } catch (IOException ex) {
            Logger.getLogger(NeuralClusterFinder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NeuralClusterFinder.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(">>>> successfully loaded network from file : " + networkFile);
    }
    
    private void getData(H2F h, CompositeNode n, int sector, int superlayer){
        int nrows = n.getRows();
        for(int i = 0; i < nrows; i++){
            int s = n.getInt(0, i);
            if(s==sector) {
                int l = n.getInt(1, i);
                int sl = ((l-1)/6)+1;
                if(sl==superlayer){
                    int ll = (l-1)%6;                    
                    int w = n.getInt(2, i);
                    h.setBinContent(w-1, ll, 1.0);
                }                
            }
        }
    }
    
    public void normalize(float[] data, double threshold){
        double max = 0.0;
        for(int i = 0; i < data.length; i++) if(data[i]>max) max = data[i];
        if(max>threshold) for(int i = 0; i < data.length; i++) data[i] = (float) (data[i]/max);
    }
    public void clusterList(CompositeNode cnode, float[] data, float[] data2d, int sector, int slayer, double threshold){
        int index = 1;
        
        while(index<data.length-1){
            if(data[index]>threshold){
                int row = cnode.getRows();
                cnode.putShort(0, row, (short) sector);
                cnode.putShort(1, row, (short) slayer);
                //double summ = 0.0;
                double summ = data[index-1]*(index-1) + data[index]*(index) + data[index+1]*(index+1);
                double denom = data[index-1]+data[index]+data[index+1];
                //System.out.println("   value = " + index + "  normalized = " + summ/denom);
                cnode.putFloat(2, row, (float) (summ/denom + 1.0));
                int windowCount = this.count(data2d, index);
                //System.out.printf(" sector = %4d, slayer = %4d, index = %4d, count = %4d\n,",
                //        sector,slayer,index,windowCount);
                if(windowCount>=3){
                    cnode.setRows(row+1);
                } 
                index += 2;
            } else { index++; }
            
        }
    }
    
    public int count(float[] data2d, int index){
        int counter = 0;
        for(int i = 0; i < 6; i++){
            int r = i*112 + index;
            if(data2d[r-1]>0.001) counter++;
            if(data2d[  r]>0.001) counter++;
            if(data2d[r+1]>0.001) counter++;
        }
        return counter;
    }
    
    public void processEvent(Event e){
        e.read(dcHits,12,1);
        dcClusters.setRows(0);
        
        for(int sector = 1; sector <=6; sector++){
            for(int slayer = 1; slayer <= 6; slayer++){
                H2F h = new H2F("h1",112,0.5,112.5,6,0.5,6.5);
                this.getData(h, dcHits, sector, slayer);
                float[] input = h.getContentArrayFloat();
                Tensor t = new Tensor(input);
                neuralNet.setInput(t);
                float[] output = neuralNet.getOutput();
                //this.normalize(output, 0.0001);
                
                this.clusterList(dcClusters, output, input, sector, slayer, 0.005);
                
                H1F h1 = new H1F("h",0.5,112.5,output);
                
                System.out.println("sector = " + sector + " superlayer = " + slayer);
                AsciiPlot.drawh2(h);
                System.out.printf(" maximum = %.5f\n",h1.getMax());
                AsciiPlot.drawh1box(h1);
            }
        }
        
        int nrows = dcClusters.getRows();
        Bank b = new Bank(factory.getSchema("nnet::clusters"),nrows);
        for(int i = 0; i < nrows; i++){
            b.putShort(0, i,(short) (i+1));
            b.putShort(1, i,(short) dcClusters.getInt(0, i));
            b.putShort(2, i,(short) dcClusters.getInt(1, i));
            b.putFloat(3, i, (float) dcClusters.getDouble(2, i));
            b.putFloat(4, i, 0.0f);            
        }
        //b.show();
        dcClusters.print();
        e.write(b);
    }
    
    public static void main(String[] args){
        NeuralClusterFinder cf = new NeuralClusterFinder();
        HipoReader r = new HipoReader("rec_clas_006152.00055-00059.h5");
        Event e = new Event();
        
        for(int i = 0; i < 10; i++){
            r.nextEvent(e);
            cf.processEvent(e);
        }
    }
}
