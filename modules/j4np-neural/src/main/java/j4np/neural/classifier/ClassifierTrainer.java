/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.classifier;

import j4np.neural.finder.NeuralClassifierModel;
import deepnetts.net.layers.activation.ActivationType;
import j4ml.data.ConfusionMatrix;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.deepnetts.DeepNettsClassifier;
import j4ml.deepnetts.DeepNettsNetwork;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.neural.data.TrackConstructor;
import j4np.neural.data.Tracks;
import j4np.utils.base.ArchiveUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author gavalian
 */
public class ClassifierTrainer {
    
    NeuralClassifierModel model = null;
    Random rndm = new Random();
        
    public double  minOffsetFalse = 3;
    public double  maxOffsetFalse = 25;
    public int     maxRandomize = 4;
    
    public ClassifierTrainer(){
        
    }
    public static DataList getDataList(String file,  int max){
        DataList  dlist = new DataList();
        for(int i = 1; i <=40; i++){
            DataList list = ClassifierTrainer.getDataList(file, i, max);
            dlist.getList().addAll(list.getList());
        }
        return dlist;
    }
    
    
    public void evaluate12(String network, int run, DataList list){
        NeuralClassifierModel model = new NeuralClassifierModel();
        model.loadFromFile(network, run);
        
        ConfusionMatrix matrix = new ConfusionMatrix(3);
        for(int j = 0; j < list.getList().size(); j++){
            float[] output = new float[3];
            model.getModel().feedForwardSoftmax(list.getList().get(j).features(), output);
            list.getList().get(j).setInfered(output);            
        }
        
        matrix.apply(list);
        System.out.println("---------------");
        System.out.println(Arrays.toString(matrix.getMatrix()[0]));
        System.out.println(Arrays.toString(matrix.getMatrix()[1]));
        System.out.println(Arrays.toString(matrix.getMatrix()[2]));
        System.out.println("---------------");
        System.out.println(Arrays.toString(matrix.getConfusionMatrix()[0]));
        System.out.println(Arrays.toString(matrix.getConfusionMatrix()[1]));
        System.out.println(Arrays.toString(matrix.getConfusionMatrix()[2]));
    }
    
    public void evaluate(String network, int run, DataList list){
        NeuralClassifierModel model = new NeuralClassifierModel();
        model.loadFromFile(network, run);
        ConfusionMatrix matrix = new ConfusionMatrix(3);
        long then = System.currentTimeMillis();
        
        for(int j = 0; j < list.getList().size(); j++){
            float[] output = new float[3];
            model.getModel().feedForwardSoftmax(list.getList().get(j).features(), output);
            list.getList().get(j).setInfered(output);            
        }
        
        long now = System.currentTimeMillis();
        double time = now - then;
        double rate = time/list.getList().size();
       
        matrix.apply(list);
        System.out.println("---------------");
        System.out.println(Arrays.toString(matrix.getMatrix()[0]));
        System.out.println(Arrays.toString(matrix.getMatrix()[1]));
        System.out.println(Arrays.toString(matrix.getMatrix()[2]));
        System.out.println("---------------");
        System.out.println(Arrays.toString(matrix.getConfusionMatrix()[0]));
        System.out.println(Arrays.toString(matrix.getConfusionMatrix()[1]));
        System.out.println(Arrays.toString(matrix.getConfusionMatrix()[2]));
        System.out.println("\n\n**********");
        System.out.printf(" processed %d events in %d msec, with rate = %.6f msec/event",
                list.getList().size(),now-then,rate);
    }
    
    
    public static void benchmark(DataList list){
        
        long then = System.currentTimeMillis();
        for(int j = 0; j < list.getList().size(); j++){
            float[] output = new float[3];
            
            //model.getModel().feedForwardSoftmax(list.getList().get(j).features(), output);
            list.getList().get(j).setInfered(output);            
        }
        long now = System.currentTimeMillis();
        double time = now - then;
        double rate = time/list.getList().size();
        System.out.println("\n\n**********");
        System.out.printf(" processed %d events in %d msec, with rate = %.6f msec/event",
                list.getList().size(),now-then,rate);
    }
    public static DataList getDataListFalse(String file,  int max){
        DataList  dlist = new DataList();
        for(int i = 1; i <=40; i++){
            DataList list = ClassifierTrainer.getDataListFalse(file, i, max);
            dlist.getList().addAll(list.getList());
        }
        return dlist;
    }
    
    public static DataList getDataListFalse(String file, long tag, int max){
        HipoReader r = new HipoReader();
        r.setDebugMode(0); r.setTags(tag); r.open(file);
        int counter = 0;
        TrackConstructor tc = new TrackConstructor();
        DataList  dlist = new DataList();
        Event event = new Event();
        Tracks tracks = new Tracks();
        Tracks  combi = new Tracks(256);
        
        while(counter<max&&r.hasNext()){
            r.next(event);
            event.read(tracks.dataNode(),tracks.dataNode().getGroup(), tracks.dataNode().getItem());
            if(tracks.size()>1){
                tc.reset();
                float[] input1 = new float[6];
                tracks.getInput(input1, 0);
                
                float[] input2 = new float[6];
                tracks.getInput(input2, 1);
                int id = 1;
                for(int e = 0; e < input1.length; e++){
                    tc.add(1, e+1, id, input1[e]*112);
                    id++;
                    tc.add(1, e+1, id, input2[e]*112);
                }
                
                tc.sectors[0].create(combi, 1);
                
                for(int t = 1; t < combi.size()-1; t++){
                    float[] input = new float[6];
                    combi.getInput(input, t);
                    dlist.add(new DataEntry(input,new float[]{1.0f,0.0f,0.0f}));
                }
                //combi.show();
                /*int charge = tracks.charge(0);
                float[] input = new float[6];
                tracks.getInput(input, 0);
                float[] output = new float[]{0.0f,0.0f,0.0f};
                if(charge<0) output[1] = 1.0f; else output[2] = 1.0f;
                dlist.add(new DataEntry(input,output));*/
                counter++;
            }
            //tracks.show();
        }
        return dlist;
    }
    
    public static DataList getDataList(String file, long tag, int max){
        HipoReader r = new HipoReader();
        r.setDebugMode(0); r.setTags(tag); r.open(file);
        int counter = 0; 
        DataList  dlist = new DataList();
        Event event = new Event();
        Tracks tracks = new Tracks();
        while(counter<max&&r.hasNext()){
            r.next(event);
            event.read(tracks.dataNode(),tracks.dataNode().getGroup(), tracks.dataNode().getItem());
            if(tracks.size()>0){
                int charge = tracks.charge(0);
                float[] input = new float[6];
                tracks.getInput(input, 0);
                float[] output = new float[]{0.0f,0.0f,0.0f};
                if(charge<0) output[1] = 1.0f; else output[2] = 1.0f;
                //if(tracks.sector(0)==1) 
                dlist.add(new DataEntry(input,output));
                counter++;
            }
            //tracks.show();
        }
        return dlist;
    }
    
    public void init(int run){
        model = new NeuralClassifierModel();
        model.loadFromFile("clas12test.network", run);        
    }
    
    
    public void train(DataList list, String archive, int run, int epochs){        
        DeepNettsClassifier classifier = new DeepNettsClassifier();
        classifier.init(new int[]{6,12,24,24,12,3});
        list.shuffle();        
        classifier.train(list, epochs);
        List<String>  networkContent = classifier.getNetworkStream();
        String archiveFile = String.format("network/%d/%s/trackClassifier.network",run,"default");
        ArchiveUtils.writeFile(archive, archiveFile, networkContent); 
        
        
        
    }
    
    public int getHighestIndex(float[] output){
        int index = 0; float max = output[0];
        for(int i = 0; i < output.length; i++)
            if(output[i]>max){ max = output[i]; index = i;}
        return index;
    }
    
    public void evaluate(DataList list, double threshold){
        int[][] matrix = new int[][]{{0,0,0},{0,0,0},{0,0,0}};
        float[] output = new float[3];
        for(int i = 0; i < list.getList().size(); i++){
            model.getModel().feedForwardSoftmax(list.getList().get(i).features(),output);
            float[] desired = list.getList().get(i).labels();
            
            for(int j = 0; j < 3; j++){
                if(desired[0]>threshold){
                    int high = this.getHighestIndex(output);
                    matrix[0][high]++;
                    //if(output[0]>threshold) matrix[0][0]++;
                    //if(output[1]>threshold) matrix[0][1]++;
                    //if(output[2]>threshold) matrix[0][2]++;
                }
                
                if(desired[1]>0.5){
                    int high = this.getHighestIndex(output);
                    matrix[1][high]++;
                    //if(output[0]>threshold) matrix[1][0]++;
                    //if(output[1]>threshold) matrix[1][1]++;
                    //if(output[2]>threshold) matrix[1][2]++;
                }
                if(desired[2]>0.5){
                    int high = this.getHighestIndex(output);
                    matrix[2][high]++;
                    //if(output[0]>threshold) matrix[2][0]++;
                    //if(output[1]>threshold) matrix[2][1]++;
                    //if(output[2]>threshold) matrix[2][2]++;
                }
            }
        }
        
        int total = 0;
        System.out.println("------------------------------------------");
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                total += matrix[i][j];
                System.out.printf("%12d ",matrix[i][j]);
            }
            System.out.println();
        }

        System.out.println("------------------------------------------");
        System.out.println("TOTAL = " + total);
        System.out.println("------------------------------------------");
        
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                //total += matrix[i][j];
                System.out.printf("%12.2f ", ((double) 100.0*matrix[i][j])/total);
            }
            System.out.println();
        }
        
    }
    
    public int[] getRandomIndex(int count){
        int[] index = new int[count];
        index[0] = rndm.nextInt(6);
        int current = 1;
        while(current<index.length){
            int value = rndm.nextInt(6);
            int accept = 1;
            for(int i = 0; i < current-1; i++) if(value==index[i]) accept = 0;
            if(accept==1){
                index[current] = value; current++;
            }
        }
        return index;
    }
    
    public DataEntry generateFalse(DataEntry entry){
        float[] data = new float[6];
        float[] desired = entry.features();
        for(int i = 0; i < 6; i++) data[i] = desired[i];
        int howMany = rndm.nextInt(this.maxRandomize);
        
        int[] index = getRandomIndex(howMany+1);
        
        for(int k = 0; k < index.length; k++){
            int shift = rndm.nextInt(35)+2;
            double sign = rndm.nextDouble();
            int factor = 1; 
            if(sign>0.5) factor = -1;
            
            double value = data[index[k]] + factor*(shift/112.0);
            if(value<0.0||value>0.99999) value = data[index[k]] - factor*(shift/112.0);
            data[index[k]] = (float) value;
        }
        
        return new DataEntry(data, new float[]{1.0f,0.0f,0.0f});
    }
    
    public static void evaluate6(String file, double threshold){
        ClassifierTrainer cltr = new ClassifierTrainer();
        cltr.init(10);
        //DataList list = ClassifierTrainer.getDataList(file, 1000);
        DataList  list = ClassifierTrainer.getDataListFalse(file, 1000);
        DataList list2 = ClassifierTrainer.getDataList(file, 10000);
        
        list.scan();
        list.getList().addAll(list2.getList());
        cltr.evaluate(list,threshold);
    }
    
    
    public static void evaluate12(String file, double threshold){
        ClassifierTrainer cltr = new ClassifierTrainer();
        cltr.init(25);
        //DataList list = ClassifierTrainer.getDataList(file, 1000);
        DataList  list = ClassifierTrainer.getDataListFalse(file, 1000);
        DataList list2 = ClassifierTrainer.getDataList(file, 10000);
        
        list.scan();
        list.getList().addAll(list2.getList());
        
        DataList listExtended = new DataList();
        
        for(int i = 0; i < list.getList().size(); i++){
            DataEntry entry = cltr.getFeatures12(list.getList().get(i));
            listExtended.add(entry);
        }
        
        
        cltr.evaluate(listExtended,threshold);
    }
    
    private float getDiff(float m, float n){
        double value = ((m-n)+1)/2.0;
        return (float) value;
    }
    
    public DataEntry getFeatures12(DataEntry entry){
        float[] input  = entry.features();
        float[] output = entry.labels();
        float[] output2 = new float[output.length];
        for(int i = 0; i < output2.length; i++) output2[i] = output[i];
        float[] input2 = new float[11];
        for(int i = 0; i < input.length; i++) input2[i] = input[i];

        input2[6]  = getDiff(input[0],input[1]);
        input2[7]  = getDiff(input[1],input[2]);
        input2[8]  = getDiff(input[2],input[3]);
        input2[9]  = getDiff(input[3],input[4]);
        input2[10] = getDiff(input[4],input[5]);
        
        return new DataEntry(input2,output2);
    }
    
    public static void train12(String file, int run, double min, double spread, int maxRandomize, int max){
        
        ClassifierTrainer cltr = new ClassifierTrainer();
        
        cltr.minOffsetFalse = min;
        cltr.maxOffsetFalse = spread;
        cltr.maxRandomize = maxRandomize;
        DataList list = ClassifierTrainer.getDataList(file, max);
        DataList list2 = ClassifierTrainer.getDataListFalse(file, max/20);
        
        
        
        int lsize = list.getList().size();
        for(int i = 0; i < lsize; i++){
            DataEntry entry = cltr.generateFalse(list.getList().get(i));
            //System.out.println("--------------------");
            //entry.show();
            //list.getList().get(i).show();
            list.add(entry);
        }
        
        list.getList().addAll(list2.getList());
        list.shuffle();
        list.shuffle();
        DataList listExtended = new DataList();
        
        for(int i = 0; i < list.getList().size(); i++){
            DataEntry entry = cltr.getFeatures12(list.getList().get(i));
            listExtended.add(entry);
        }
        
        listExtended.shuffle();
        listExtended.scan();
        
        DeepNettsNetwork classifier = new DeepNettsNetwork();
        classifier.activation(ActivationType.RELU)
                .outputActivation(ActivationType.SOFTMAX);
                //.lossType(LossType.MEAN_SQUARED_ERROR);
        
        classifier.init(new int[]{11,12,12,12,6,3});
        
        classifier.train(listExtended, 1024);
        
        List<String>  networkContent = classifier.getNetworkStream();
        String archiveFile = String.format("network/%d/%s/trackClassifier.network",
                run,"default");
        ArchiveUtils.writeFile("clas12rgd.network", archiveFile, networkContent); 
    }
    
    public static void train6(String file, int run, double min, double spread, int maxRandomize, int max){
        
        ClassifierTrainer cltr = new ClassifierTrainer();
        
        cltr.minOffsetFalse = min;
        cltr.maxOffsetFalse = spread;
        cltr.maxRandomize = maxRandomize;
        
        DataList list = ClassifierTrainer.getDataList(file, max);
        DataList list2 = ClassifierTrainer.getDataListFalse(file, max/20);
        int lsize = list.getList().size();
        for(int i = 0; i < lsize; i++){
            DataEntry entry = cltr.generateFalse(list.getList().get(i));
            //System.out.println("--------------------");
            //entry.show();
            //list.getList().get(i).show();
            list.add(entry);
        }
        
        list.getList().addAll(list2.getList());
        list.shuffle();
        
        DeepNettsNetwork classifier = new DeepNettsNetwork();
        
        classifier.activation(ActivationType.RELU)
                .outputActivation(ActivationType.SOFTMAX)
                .learningRate(0.001);
                
                //.lossType(LossType.MEAN_SQUARED_ERROR);
        
        //classifier.init(new int[]{6,12,12,12,6,3});
        classifier.init(new int[]{6,12,24,24,12,3});
        
        //classifier.train(list, 5);
        classifier.train(list, 1024);
        
        List<String>  networkContent = classifier.getNetworkStream();
        String archiveFile = String.format("network/%d/%s/trackClassifier.network",
                run,"default");
        ArchiveUtils.writeFile("newRGD_nue.network", archiveFile, networkContent); 
        
        
        long then = System.currentTimeMillis();
        
        classifier.evaluate(list);

        long now = System.currentTimeMillis();
        
        double time = now - then;
        double rate = time/list.getList().size();
        System.out.println("\n\n**********");
        System.out.printf(" processed %d events in %d msec, with rate = %.6f msec/event\n",
                list.getList().size(),now-then,rate);
    }
    
   
    public static void main(String[] args){
        
        //String file = "training_sample_tr.h5";
        //String file2 = "training_sample_va.h5";
        
        String file  = "ai_run_18325_tr.h5";
        String file2 = "ai_run_18325_va.h5";
        String file3 = "/Users/gavalian/Work/DataSpace/neural/run_012933_tracks_va.h5";
        
        
        //ClassifierTrainer.train6(file, 5197, 4, 45, 4, 15000);
        
        /*ClassifierTrainer.train6(file, 1, 2, 35, 3, 15000);
        ClassifierTrainer.train6(file, 2, 2, 35, 4, 15000);
        
        ClassifierTrainer.train6(file, 3, 3, 35, 3, 15000);
        ClassifierTrainer.train6(file, 4, 3, 35, 4, 15000);
        
        ClassifierTrainer.train6(file, 5, 4, 45, 3, 15000);*/
        //---- this is the best network configuration 
        // 4,45,4 - means random shift is 4 wires to 45 wires,
        // number of clusters replaced is up to 4 ( 1,2,3 or 4)
        //
        //** ClassifierTrainer.train6(file, 18305, 4, 45, 4, 5200);        
        
        //ClassifierTrainer.train12(file, 8, 4, 45, 4, 15000);        
        ClassifierTrainer ct = new ClassifierTrainer();
        DataList list = ClassifierTrainer.getDataList(file2, 1500);
        DataList listf = ClassifierTrainer.getDataListFalse(file2, 15);
        list.getList().addAll(listf.getList()); 
        list.shuffle();
        list.shuffle();
        list.export("mlp_training_sample.csv");
        
        ct.evaluate("newRGD_nue.network", 12933, list);

        /* DataList listExtended = new DataList();
        
        for(int i = 0; i < list.getList().size(); i++){
            DataEntry entry = ct.getFeatures12(list.getList().get(i));
            listExtended.add(entry);
        }*/
        
        /* DataList listExtended = new DataList();
        
        for(int i = 0; i < list.getList().size(); i++){
            DataEntry entry = ct.getFeatures12(list.getList().get(i));
            listExtended.add(entry);
        }*/
        //ct.evaluate("clas12test.network", 7, listExtended);
        //ct.evaluate("clas12test.network", 16, listExtended);
        /*
        ct.evaluate("clas12test.network", 16, list);
        
        
        for(int i = 1; i <=6; i++){
            System.out.println("NETWORK RUN # " + i);
            ct.evaluate("clas12test.network", i, list);
        }
        
        ct.evaluate("clas12test.network", 16, list);
        System.out.println("\n\n\nRGA network---\n");
        ct.evaluate("clas12rga.network", 5442, list);
        
        */
        //ClassifierTrainer.train6(file, 15000);
        //ClassifierTrainer.evaluate6(file2,0.5);
        //ClassifierTrainer.train12(file, 5000);
        //ClassifierTrainer.evaluate12(file2,0.2);
        //ClassifierTrainer.evaluate12(file2,0.5);
        //ClassifierTrainer.evaluate12(file2,0.8);
        /*
        ClassifierTrainer cltr = new ClassifierTrainer();
        DataList list = ClassifierTrainer.getDataList(file, 40);
        int lsize = list.getList().size();
        for(int i = 0; i < lsize; i++){
            DataEntry entry = cltr.generateFalse(list.getList().get(i));
            System.out.println("--------------------");
            entry.show();
            list.getList().get(i).show();
            list.add(entry);
        }
        
        
        list.scan();
        for(int i = 0; i < 25; i++){
            int[] index = cltr.getRandomIndex(3);
            System.out.println(Arrays.toString(index));
        }*/
    }
}

