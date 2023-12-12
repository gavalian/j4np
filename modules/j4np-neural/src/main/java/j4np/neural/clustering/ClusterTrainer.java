/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.clustering;

import deepnetts.data.TabularDataSet;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.opt.OptimizerType;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.deepnetts.DataSetUtils;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.visrec.ml.data.DataSet;
import twig.data.AsciiPlot;
import twig.data.H1F;
import twig.data.H2F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class ClusterTrainer {
    
    Random rand = new Random();
    ConvolutionalNetwork neuralNet;
    public int nEpochs = 50;
    
    public ClusterTrainer(){
        
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
    
    public void initNetwork(){
        neuralNet = ConvolutionalNetwork.builder()
                .addInputLayer(112,6,1)                
                .addConvolutionalLayer(3, 3, 2)
                .addMaxPoolingLayer(3, 1)
                //.addMaxPoolingLayer(3, 1)
                .addFullyConnectedLayer(224)
                .hiddenActivationFunction(ActivationType.RELU)
                .addOutputLayer(112, ActivationType.SIGMOID)
                .lossFunction(LossType.MEAN_SQUARED_ERROR)
                .randomSeed(123)
                .build();
    }
    
    public List<H2F> getRandomLayer(){
        H2F h2 = new H2F("h2",112,0.5,112.5,6,0.5,6.5);
        H2F h1 = new H2F("h1",112,0.5,112.5,1,0.5,1.5);
        for (int i = 0; i < 2; i++){
            int pos = rand.nextInt(112)+1;
            for(int j = 0; j < 6; j++) h2.setBinContent(pos-1, j, 1.0);
            h1.setBinContent(pos-1, 0, 1.0);
        }
        return Arrays.asList(h2,h1);
    }
    
    public int[] get(float[] numbers){
        float highest1 = numbers[0];
        float highest2 = numbers[1];
        int   hi1 = 0;
        int   hi2 = 1;
        // Iterate through the array
        for (int i = 2; i < numbers.length; i++) {
            // If the current number is higher than the highest number, update the highest number
            if (numbers[i] > highest1) {
                highest2 = highest1;
                highest1 = numbers[i];
                hi1 = i;
            } else if (numbers[i] > highest2) {
                highest2 = numbers[i];
                hi2=i;
            }
        }
        return new int[]{hi1,hi2};
    }
    
    public DataSet getRandom(int count){
        int  nInputs = 112*6;
        int nOutputs = 112;
        TabularDataSet  dataset = new TabularDataSet(112*6,112);
        for(int k = 0; k < count; k++){
            List<H2F> row = this.getRandomLayer();
            float[]  inBuffer = row.get(0).getContentArrayFloat();
            float[] outBuffer = row.get(1).getContentArrayFloat();
            //System.out.println(inBuffer.length + " " + outBuffer.length);
            dataset.add(new TabularDataSet.Item(inBuffer, outBuffer));                
        }
        
        String[] names = DataSetUtils.generateNames(nInputs,nOutputs);
        dataset.setColumnNames(names);
        return dataset;
    }
    
    public DataList getFromFile(String file,  int max){
        DataList dataList = new DataList();
        for(int i = 0; i < 40; i++){
            DataList list = this.getFromFile(file, i+1, max);
            dataList.getList().addAll(list.getList());
        }
        return dataList;
    }
    
    public DataList getFromFile(String file, int tag, int max){        
        DataList dataList = new DataList();
        HipoReader r = new HipoReader();
        r.setTags(tag);
        r.setDebugMode(0);
        r.open(file);
        Event e = new Event();
        CompositeNode  node = new CompositeNode(32144,2,"s",5000);
        CompositeNode track = new CompositeNode(32000,1,"s",5000);
        int counter = 0;
        while(r.hasNext()==true&&counter<max){
            counter++;
            r.next(e);
            e.read( node, 32144, 2);
            e.read(track, 32000, 1);
            //track.print();            
            float[]   data = new float[112*6];
            float[] output = new float[112];
            int nrows = track.getRows();
            for(int row = 0; row < nrows; row++){
              int bin =  (int) Math.round(track.getDouble(17, row));
              output[bin-1] = 1.0f;
            }
            
            nrows = node.getRows();
            for(int row = 0; row < nrows; row++){
                int index = node.getInt(0, row) - 1;
                if(index<112*6) data[index] = 1.0f;                
            }
            /*H2F h2 = H2F.create(112, 6, data);
            H1F h1 = new H1F("",0.5,112.5,output);
            AsciiPlot.drawh2(h2);
            AsciiPlot.drawh1box(h1);*/
            dataList.add(new DataEntry(data,output));            
        }
        return dataList;
    }
    
    public H2F getSuperlayer(CompositeNode node, int sector, int superlayer){
        H2F h = new H2F("h",112,0.5,112.5, 6, 0.5, 6.5);
        for(int i = 0; i < node.getRows(); i++){
            int s = node.getInt(0, i);
            int l = node.getInt(1, i);
            int w = node.getInt(2, i);
            if(s==sector){
                int li = (l - 1) - superlayer*6;
                if(li>=0&&li<6)
                    h.setBinContent(w-1, li, 1.0);
            }
        }
        return h;
    }
    
    public H1F getClusters(CompositeNode node, int sector, int superlayer){
        H1F h = new H1F("h",112,0.5,112.5);
        for(int i = 0; i < node.getRows(); i++){
            int  s = node.getInt(1, i);
            int sl = node.getInt(2, i);
            double mean = node.getDouble(3, i);
            if((sl-1)==superlayer&&s==sector){
                int bin = ((int) Math.round(mean)) - 1;
                h.setBinContent(bin, 1.0);
            }
        }
        return h;
    }
    
    public DataList getDatasetFromFile(String file, int max){
        HipoReader r = new HipoReader();
        r.setDebugMode(0); r.open(file);
        
        CompositeNode nodeDC = new CompositeNode(    12, 1,  "bbsbil", 4096);
        CompositeNode nodeCL = new CompositeNode( 32100, 1,    "3b2f", 1024);
        
        DataList list = new DataList();
        int counter = 0;
        Event e = new Event();
        while(r.hasNext()&&counter<max){
            counter++;
            r.next(e);
            e.read(nodeDC,12,1);
            e.read(nodeCL,32100,1);
            H2F h2 = this.getSuperlayer(nodeDC, 1, 1);
            H1F h1 = this.getClusters(nodeCL, 1, 1);
            if(h1.getIntegral()>0.0){
                list.add(new DataEntry(h2.getContentArrayFloat(),h1.getData()));
            }
            //System.out.println("-----------------------");
            //AsciiPlot.drawh2(h2);
            //System.out.println("integral = " + h1.getIntegral());
            //AsciiPlot.drawh1box(h1);                        
        }
        return list;
    }
    
    public void train(DataSet trset){
        BackpropagationTrainer trainer = neuralNet.getTrainer();
        trainer.setMaxError(0.000004f);
        trainer.setLearningRate(0.01f);
        trainer.setMomentum(0.7f);
        
        trainer.setOptimizer(OptimizerType.SGD);
        trainer.setMaxEpochs(nEpochs);
        
        trainer.train(trset);
        
        try {
            FileIO.writeToFile(neuralNet, "clusterFinder.dnet");
        } catch (IOException ex) {
            Logger.getLogger(ClusterTrainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public boolean isMatch(double value, List<Double> array){
        for(int i = 0; i < array.size(); i++){
            if(Math.abs(value-array.get(i))<0.5) return true;
        }
        return false;
    }
    
    public void evaluate( DataList list){
        
        H1F h100  = new H1F("h100",112,0.5,112.5);
        H1F h100f = new H1F("h200f",112,0.5,112.5);
        
        H1F h200  = new H1F("h200",112,0.5,112.5);
        H1F h200f  = new H1F("h200f",112,0.5,112.5);
        h100.attr().set("lc=3");
        h200.attr().set("lc=3");
        TGCanvas c = new TGCanvas(900,500);
        c.view().divide(2,1);
        c.cd(0).draw(h100).draw(h100f,"same");
        c.cd(1).draw(h200).draw(h200f,"same");
        
        
        int counter = list.getList().size();
        ClusterFinder finder = new ClusterFinder(0.1);
        finder.setNormalize(false);
        long then = System.currentTimeMillis();
        for(int i = 0; i < counter; i++){

            Tensor t = new Tensor(list.getList().get(i).features());
            neuralNet.setInput(t);
            float[] output = neuralNet.getOutput();
            //System.out.println("-----------------");
            //System.out.println(Arrays.toString(h.get(1).getContentArrayFloat()));
            //System.out.println(Arrays.toString(output));
            finder.normalizeLog(output);
            
            H1F hi = new H1F("hi",0.5,112.5,list.getList().get(i).labels());
            H1F ho = new H1F("ho",0.5,112.5,output);
            H2F h2 = H2F.create(112, 6, list.getList().get(i).features());
            
            
            System.out.println("===========================");
            AsciiPlot.drawh2(h2);
            AsciiPlot.drawh1box(hi);
            System.out.println("max = " + ho.getMax());
            AsciiPlot.drawh1box(ho);
            
            //List<Double> cls = finder.find(output);
            //System.out.println(Arrays.toString(finder.fromList(cls)));
            
             int bm = finder.findMax(output);
            System.out.println(" MAX = " + bm + "  value = " + output[bm]);
            System.out.println("OUT = " + Arrays.toString(output));
            List<Double> posR = finder.find(list.getList().get(i).labels());
            List<Double> posI = finder.find(output); 
            
            //System.out.print("DESIRED : ");

            //for(Double d : posR ) System.out.printf(" %8.5f ",d);
            
            //System.out.print("\nINFERED : ");
            //for(Double d : posI ) System.out.printf(" %8.5f ",d);
            //System.out.println();
            
            for(Double d : posR){
                h100.fill(d);
                if(isMatch(d,posI)) h100f.fill(d);
            }
            
            for(Double d : posI){
                h200.fill(d);
                if(isMatch(d,posR)) h200f.fill(d);
            }
            
            //System.out.println(Arrays.toString(hnr) + " ==> " + Arrays.toString(hno));
        }
        
        long now = System.currentTimeMillis();
        double time = now - then;
        System.out.printf("evaluater %d in %d msec, rate = %.4f msec/event\n",counter,now - then, time/counter);
        
    }
    
    public void evaluate( int counter){
        long then = System.currentTimeMillis();
        for(int i = 0; i < counter; i++){
            List<H2F> h = getRandomLayer();
            Tensor t = new Tensor(h.get(0).getContentArrayFloat());
            neuralNet.setInput(t);
            float[] output = neuralNet.getOutput();
            //System.out.println("-----------------");
            //System.out.println(Arrays.toString(h.get(1).getContentArrayFloat()));
            //System.out.println(Arrays.toString(output));
            int[] hnr = this.get(h.get(1).getContentArrayFloat());
            int[] hno = this.get(output);
            //System.out.println(Arrays.toString(hnr) + " ==> " + Arrays.toString(hno));
        }
        long now = System.currentTimeMillis();
        double time = now - then;
        System.out.printf("evaluater %d in %d msec, rate = %.4f msec/event\n",counter,now - then, time/counter);
    }
    
    public static void runTrain(String file, int nepochs){
        ClusterTrainer ct = new ClusterTrainer();        
        DataList list = ct.getDatasetFromFile(file,250000);
        DataList[] data = DataList.split(list, 0.8,0.2);
        
        DataSet ds = DataList.convert(list); 
        //list.scan();
        list.show();
        ct.initNetwork();
        ct.nEpochs = nepochs;
        ct.train(ds);
    }
    
    public static void runEvaluate(String file){
        ClusterTrainer ct = new ClusterTrainer();        
        DataList list = ct.getDatasetFromFile(file,15000);
        DataList[] data = DataList.split(list, 0.8,0.2);        
        
        DataSet ds = DataList.convert(list); 
        //ct.initFromFile("etc/networks/clusterFinder.dnet");
        ct.initFromFile("etc/networks/clusterFinder.dnet");
        //ct.initFromFile("clusterFinder.dnet");
        ct.evaluate(list);
        //list.scan();
        //list.show();
        //ct.initNetwork();
        //ct.train(ds);
    }
    
    public static void main(String[] args){   
        String file1 = "output_clusters_4.h5_000000";
        String file2 = "output_clusters_4.h5_000001";
        int nepochs = 750;
        if(args.length>0) nepochs = Integer.parseInt(args[0]);

        //ClusterTrainer.runTrain(file1, nepochs);
        ClusterTrainer.runEvaluate(file2);
        //ct.evaluate(list);
        
        /*ct.nEpochs = 50;        
        ct.initNetwork();
        ct.train(ds);
        */
        
        
        /*ClusterTrainer ct = new ClusterTrainer();
        String file = "clas12_ai_tr.h5";
        String file2 = "clas12_ai_va.h5";
        
        ct.initNetwork();
        DataList  list = ct.getFromFile(file, 25000);
        DataList list2 = ct.getFromFile(file2, 5000);
        
        System.out.println(">> Loaded data sample count = " + list.getList().size());
        
        DataSet ds = DataList.convert(list);
        ct.nEpochs = 50;               
        
        //ct.train(ds);
        
        ct.initFromFile("clusterFinder.dnet");        
        ct.evaluate(list2);*/
        /*DataSet ds = ct.getRandom(15500);
        ct.train(ds);
        ct.evaluate( 50000);*/
    }
}
