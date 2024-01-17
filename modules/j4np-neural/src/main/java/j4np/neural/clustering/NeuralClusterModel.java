/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.clustering;

import deepnetts.net.ConvolutionalNetwork;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.neural.data.DataNodes;
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
public class NeuralClusterModel {
    ConvolutionalNetwork neuralNet;
    
    public NeuralClusterModel(){
        
    }
    
    public void initFromFile(String networkFile){
        try {
            neuralNet = FileIO.createFromFile(networkFile, ConvolutionalNetwork.class);
            System.out.println(">>>> successfully loaded network from file : " + networkFile);
        } catch (IOException ex) {
            Logger.getLogger(ClusterTrainer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClusterTrainer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public H2F getData(CompositeNode node, int sector){
        H2F h = new H2F("h",112,0.5,112.5,36,0.5,36.5);        
        for(int k = 0; k < node.getRows(); k++){
            int sec = node.getInt(0, k);
            int x   = node.getInt(1, k)-1;
            int y   = node.getInt(2, k)-1;            
            //System.out.printf("sec = %5d, x/y = %5d, %5d\n",node.getInt(0, k),x,y);
            if(sec==sector) h.setBinContent( node.getInt(2,k)-1,node.getInt(1, k)-1, 1.0);
        }
        return h;        
    }
    
    public H2F getData2(CompositeNode node, int sector){
        H2F h = new H2F("h",112,0.5,112.5,36,0.5,36.5);        
        for(int k = 0; k < node.getRows(); k++){
            int det = node.getInt(0, k);
            int sec = node.getInt(1, k);
            int x   = node.getInt(2, k)-1;
            int y   = node.getInt(3, k)-1;            
            //System.out.printf("sec = %5d, x/y = %5d, %5d\n",node.getInt(0, k),x,y);
            if(sec==sector&&det==6) h.setBinContent( node.getInt(3,k)-1,node.getInt(2, k)-1, 1.0);
        }
        return h;        
    }
    
    public void process(Event event){
        CompositeNode node = new CompositeNode(12,1,"bbsbil",4060);        
        //event.read(node,12,1);
        event.read(node,33,1);
        
        ClusterFinder finder = new ClusterFinder(0.2);
        //node.show();
        //node.print();
        CompositeNode cl = DataNodes.getNodeClusters();
        cl.setRows(0);
        
        for(int s = 0; s < 6; s++){
            H2F h = getData2(node, s+1);
            //AsciiPlot.drawh2(h);
            for(int l = 0; l < 6; l++){
                H2F hs = h.sliceY(l*6, l*6+5);
                //AsciiPlot.drawh2(hs);
                Tensor t = new Tensor(hs.getContentArrayFloat());
                neuralNet.setInput(t);
                float[] output = neuralNet.getOutput();
                List<Double> list = finder.find(output);
                int rows = cl.getRows();
                
                for(int j = 0; j < list.size(); j++){
                    cl.setRows(rows+1);
                    cl.putByte(0, rows, (byte) (rows+1));
                    cl.putByte(1, rows, (byte) (s+1));
                    cl.putByte(2, rows, (byte) (l+1));
                    cl.putFloat(3, rows, (float) list.get(j).floatValue());
                    rows++;
                }
                //H1F ho = new H1F("ho",0.5,112.5,output);
                //AsciiPlot.drawh1box(ho);
            }
        }
        //System.out.println("one event " + cl.getRows());
        //cl.print();        
        event.write(cl);
    }
    public void benchmark(Event e, int counter){
        CompositeNode node = new CompositeNode(12,1,"bbsbil",4060);        
        e.read(node,12,1);
        H2F  h = this.getData(node, 1);
        H2F hl = h.sliceY(0, 5);
        
        float[] input = hl.getContentArrayFloat();
        long then = System.currentTimeMillis();
        for(int i = 0; i < counter; i++){
             Tensor t = new Tensor(input);
             neuralNet.setInput(t);
             float[] output = neuralNet.getOutput();             
        }
        long now = System.currentTimeMillis();
        
        double time = now - then;
        System.out.printf(" N = %d, time = %.4f, rate = %.6f, event rate = %.6f\n ",
                counter,time, time/counter,36*time/counter);
    }
    public static void main(String[] args){
        
        String file = "/Users/gavalian/Work/DataSpace/online-trakcing/filter_output_0_converted.h5";
        
        HipoReader r = new HipoReader(file);
        Event event = new Event();
        NeuralClusterModel model = new NeuralClusterModel();
        model.initFromFile("etc/networks/clusterFinder.dnet");
        int counter = 0;
        
        while(r.hasNext()&&counter<10){
            counter++;
            r.next(event);
            model.process(event);
        }
        
        System.out.println("benchmarking \n\n");
        model.benchmark(event, 25000);
        
    }
}
