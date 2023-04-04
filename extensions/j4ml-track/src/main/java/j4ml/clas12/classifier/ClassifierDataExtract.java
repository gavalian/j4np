/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.classifier;

import j4ml.clas12.networks.TrackNetworkTrainer;
import j4ml.clas12.networks.TrackNetworkValidator;
import j4ml.clas12.track.ClusterCombinations;
import j4ml.clas12.track.ClusterStore;
import j4ml.clas12.track.Track;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.physics.Vector3;
import j4np.utils.ProgressPrintout;
import j4np.utils.io.OptionApplication;

import j4np.utils.io.OptionExecutor;
import j4np.utils.io.OptionParser;
import j4np.utils.io.OptionStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import twig.data.AsciiPlot;
import twig.data.H2F;
import twig.data.Range;


/**
 *
 * @author gavalian
 */
public class ClassifierDataExtract extends OptionApplication {
        
        //implements OptionExecutor {

    //HipoWriter writer = new HipoWriter();
    HipoWriter[] writers = new HipoWriter[2];
    
    Random     r = new Random();
    
    Bank    tbCL = null;
    Bank    hbCL = null;
    Bank    tbTR = null;
    
    String  outputFileName = "data_extract_classifier.hipo";
    
    public int     binMaxWrite    = 75000;
    int[]   occupancy = new int[40];
    H2F     occupancy2D = null;
    
    int     activeEventTag = 0;
    
    
    public double pMinimum = 0.0;
    public double pMaximum = 0.0;
    
    public Range  particleAngleRange = new Range(5.0,35.0);
    
    public ClassifierDataExtract(){
        
        super("clas12ml");
        OptionStore store = this.getOptionStore().setName("clas12ml");        
        
        store.addCommand("-train", "train network");
        store.getOptionParser("-train")
                .addRequired("-t", "training data file (hipo)")
                .addRequired("-v", "validation data file (hipo)")
                .addRequired("-a", "archive file name")
                .addRequired("-r", "run number")
                .addOption("-vertex", "-15.0:5.0", "vertex range for the tracks to train")
                .addOption("-e", "125", " number of epochs to train")
                .addOption("-max", "45000", "maximum number of tracks for training");
        
        store.addCommand("-extract", "extract data for track ml algorithms");
        store.getOptionParser("-extract").addRequired("-o",
                 "output file name to write training data");   
        store.getOptionParser("-extract").addOption("-max", "64000", "maximum number of tracks per momentum bin");
        store.getOptionParser("-extract").addOption("-pmin",  "0.0", "minimum momentum");
        store.getOptionParser("-extract").addOption("-pmax", "11.0", "maximum momentum");
        
        for(int i = 0; i < occupancy.length; i++) occupancy[i] = 0;
    }
    
    
    public void init(HipoReader chain){
        //writer.open(this.outputFileName);
        writers[0] = new HipoWriter();
        writers[1] = new HipoWriter();
        
        writers[0].open(outputFileName+"_tr.h5");
        writers[1].open(outputFileName+"_va.h5");
        
        tbTR = chain.getBank("TimeBasedTrkg::TBTracks");
        tbCL = chain.getBank("TimeBasedTrkg::TBClusters");
        
        hbCL = chain.getBank("TimeBasedTrkg::TBClusters");
        
        //tbCL = chain.getBank("HitBasedTrkg::HBClusters");
        //hbCL = chain.getBank("HitBasedTrkg::HBClusters");        
    }
    
    
    public void initOccupancyContainer(){        
        this.occupancy2D = new H2F("occ",40,0.5,40.5,24,5.0,35.0);
    }
    
    public static float[] toFloat(List<Float> list){
        float[] data = new float[list.size()];
        for(int i = 0; i < data.length; i++) data[i] = list.get(i);
        return data;
    }
    
    public static int[] toInt(List<Integer> list){
        int[] data = new int[list.size()];
        for(int i = 0; i < data.length; i++) data[i] = list.get(i);
        return data;
    }
    
    public static List<Node> getNode(Track trk){
        
        List<Node> nodeList = new ArrayList<Node>();
        short[] desc = new short[]{(short) trk.sector,(short) trk.charge};
        
        nodeList.add(new Node(1001,1,desc));
        nodeList.add(new Node(1001,2,new float[]{ (float) trk.chi2}));
        nodeList.add(new Node(1001,3,new short[]{
            (short) trk.clusters[0],
            (short) trk.clusters[1],
            (short) trk.clusters[2],
            (short) trk.clusters[3],
            (short) trk.clusters[4],
            (short) trk.clusters[5]
        }));
        
        nodeList.add(new Node(1001,4,new float[] {
            (float) trk.means[0],
            (float) trk.means[1],
            (float) trk.means[2],
            (float) trk.means[3],
            (float) trk.means[4],
            (float) trk.means[5]
        }));
        nodeList.add(new Node(1001,5,new float[]{
            (float) trk.slopes[0],
            (float) trk.slopes[1],
            (float) trk.slopes[2],
            (float) trk.slopes[3],
            (float) trk.slopes[4],
            (float) trk.slopes[5]
        }));
        
        nodeList.add(new Node(1001,6, new float[]{ (float) trk.vector.x(), (float)trk.vector.y(), (float)trk.vector.z()}));
        nodeList.add(new Node(1001,7, new float[]{ (float) trk.vertex.x(),(float) trk.vertex.y(),(float) trk.vertex.z()}));
                        
        return nodeList;
    }
    
    
    public void export(){
        //TextFileWriter writer 
    }
    
    public void readStore(){
        
    }
    
    public static int getTrackBin(Track t){
        
        double p = t.vector.mag();
        int bin = -1;
        if(p>0.0&&p<10.0){
            bin = (int) (p/0.5);
        }
        return bin;
    }
    
    public static int getTrackBin(Track t, double min, double max){
        double binSize = (max-min)/20.0;
        double p = t.vector.mag();
        int bin = -1;
        if(p>=min&&p<=max){
            bin = (int) ((p-min)/binSize);
        }
        return bin;
    }
    
    public static void read(ClusterStore store, Bank bank, int sector){
        store.reset();
        //this.resolvedTracks.reset();
        int nrows = bank.getRows();
        
        for(int i = 0; i < nrows; i++){
                int sec = bank.getInt("sector", i);
                int id     = bank.getInt("id", i);
                int superlayer = bank.getInt("superlayer", i);
                double wire = bank.getFloat("avgWire", i);
                if(sector==sec){
                    store.add(superlayer-1, id, wire);
                }
        }
    }
    public int getActiveTag(){ return this.activeEventTag;}
    
    public List<Track>  getTracksForSector(List<Track> tracks,int sector){
        List<Track> list = new ArrayList<>();
        for(Track tr : tracks) if(tr.sector==sector) list.add(tr);
        return list;
    }
    
    public List<Track>  getTracksSelected(List<Track> tracks){
        List<Track> list = new ArrayList<>();
        for(Track tr : tracks){
            if(tr.chi2<10&&tr.vertex.z()>-50&&tr.vertex.z()<50&&tr.complete()) list.add(tr);
        }
        return list;
    }
    
    public List<Node> getClusters(Bank bank, int sector){
        
        List<Integer> superLayers = new ArrayList<>();
        List<Float>   avgWires    = new ArrayList<>();
        List<Float>   slopes      = new ArrayList<>();
        
        int nrows = bank.getRows();
        
        for(int i = 0; i < nrows; i++){
                int sec = bank.getInt("sector", i);
                int id     = bank.getInt("id", i);
                int superlayer = bank.getInt("superlayer", i);
                double wire = bank.getFloat("avgWire", i);
                double slope = bank.getFloat("fitSlope", i);
                if(sector==sec){
                    superLayers.add(superlayer);
                    avgWires.add((float) wire);
                    slopes.add((float) slope);
                }
        }
        
        float[] f_slopes = ClassifierDataExtract.toFloat(slopes);
        float[] f_wires = ClassifierDataExtract.toFloat(avgWires);
        int[]   i_slayers = ClassifierDataExtract.toInt(superLayers);
        
        Node n_slayers = new Node(2001,1,i_slayers);
        Node n_wires = new Node(2001,2,f_wires);
        Node n_slopes = new Node(2001,3,f_slopes);
        
        return Arrays.asList(n_slayers,n_wires,n_slopes);
    }
    
    
    public List<Node> processEvent(Event event, int sector){
        
        event.read(tbCL);
        event.read(hbCL);
        event.read(tbTR);

        
        List<Node> result = new ArrayList<>();
        
        List<Node> clusters = this.getClusters(hbCL, sector);
        List<Track>  trkList = Track.read(tbTR,tbCL);
        
        List<Track>  trkSector = this.getTracksForSector(trkList, sector);
        
        if(trkSector.size()==1){
            List<Track> trkSelect = this.getTracksSelected(trkSector);
            
            if(trkSelect.size()==1){
                
                int  charge = trkSelect.get(0).charge;
                double  mom = trkSelect.get(0).vector.mag();
                Vector3 trkVec = trkSelect.get(0).vector;
                
                int bin = ClassifierDataExtract.getTrackBin(trkSelect.get(0),this.pMinimum,this.pMaximum);
                int chargeBin = bin+1;
                if(charge<0) chargeBin += 20;
                int thetaBin = this.occupancy2D.getYAxis().getBin(
                        Math.toDegrees(trkVec.theta())
                );
                
                if(chargeBin>=1&&chargeBin<=40&&thetaBin>=0&&thetaBin<24){
                    
                    int bc =  (int) occupancy2D.getBinContent(chargeBin-1, thetaBin);
                    
                    
                    occupancy[chargeBin-1] = occupancy[chargeBin-1] + 1;
                    
                    boolean writeStatus = true;
                    if(occupancy[chargeBin-1]>this.binMaxWrite) writeStatus=false;
                    //if(bc>this.binMaxWrite) writeStatus=false;
                    if(writeStatus==true){
                        occupancy2D.setBinContent(chargeBin-1, thetaBin, bc+1);
                        //System.out.println(">>> " + trkSelect.get(0));
                        List<Node> trkNodes = ClassifierDataExtract.getNode(trkSelect.get(0));                        
                        result.addAll(clusters);
                        result.addAll(trkNodes);
                        activeEventTag = chargeBin;
                    }
                }
            }
        }
        return result;
    }
    
    public void write(Event event, int tag){
        double p = r.nextDouble();
        if(p<0.5) writers[0].addEvent(event,tag); else writers[1].addEvent(event,tag);
    }
    
    public void close(){
        writers[0].close(); writers[1].close();
    }
    
    public static void processFiles(List<String> files, String output, double min, double max, int nevents){
        HipoReader chain = new HipoReader();
        
        //chain.addFiles(files);
        chain.open(files.get(0));
        
        ClassifierDataExtract ce = new ClassifierDataExtract();
        ce.outputFileName = output;
        ce.binMaxWrite = nevents;
        ce.pMinimum = min;
        ce.pMaximum = max;
        
        ce.init(chain);
        ce.initOccupancyContainer();
        Event    event = new Event();
        Event outEvent = new Event();
        int counter = 0;
        int counterWrite = 0;
        ProgressPrintout progress = new ProgressPrintout();
        for(int k = 0; k < files.size(); k++){
            chain = new HipoReader(files.get(k));
            while(chain.hasNext()==true){
                
                progress.updateStatus();
                chain.nextEvent(event);
                for(int s = 1; s <= 6; s++){
                    List<Node> nodes =  ce.processEvent(event, s);
                    if(nodes.size()>0){
                        outEvent.reset();
                    for(Node node : nodes)
                        outEvent.write(node);
                    ce.write(outEvent, ce.getActiveTag());
                    
                    counterWrite++;
                    }
                }
                counter++;            
            }
        }
        ce.close();
        
        //AsciiPlot.setSize(80, 25);
        //AsciiPlot.draw(ce.occupancy2D.projectionX());
        //AsciiPlot.draw(ce.occupancy2D.projectionY());
        System.out.printf("extracted %d / %d (%.2f ) \n",counterWrite,counter,( (double) counterWrite*100)/counter);
    }
    
    public static void processFiles(List<String> files){
        HipoReader chain = new HipoReader();
        //chain.addFiles(files);
        chain.open(files.get(0));
        
        ClassifierDataExtract ce = new ClassifierDataExtract();
        ce.init(chain);
        Event    event = new Event();
        Event outEvent = new Event();
        int counter = 0;
        int counterWrite = 0;
        ProgressPrintout progress = new ProgressPrintout();
        
        while(chain.hasNext()==true){
            
            progress.updateStatus();
            chain.nextEvent(event);
            for(int s = 1; s <= 6; s++){
                List<Node> nodes =  ce.processEvent(event, s);
                if(nodes.size()>0){
                    outEvent.reset();
                    for(Node node : nodes)
                        outEvent.write(node);                    
                    ce.write(outEvent, ce.getActiveTag());
                    
                    counterWrite++;
                }
            }
            counter++;            
        }
        ce.close();
        System.out.printf("extracted %d / %d (%.2f ) \n",counterWrite,counter,( (double) counterWrite*100)/counter);
    }
    /*
    @Override
    public void execute(String[] args) {
        
        OptionParser parser = new OptionParser("exctract-data");
        parser.addOption("-pmin", "0.0", "minimum momentum for the particles");
        parser.addOption("-pmax", "10.0", "maximum momentum for the particles");
        parser.addOption("-n", "25000","maximum number of events per momentum bin");
        
        if(args.length<1) {
            parser.printUsage(); System.exit(0);
        }
        parser.parse(args);
        
        List<String>  inputs = parser.getInputList();
        double pmin = parser.getOption("-pmin").doubleValue();
        double pmax = parser.getOption("-pmax").doubleValue();
        int nevents = parser.getOption("-n").intValue();
        
        ClassifierDataExtract.processFiles(inputs, pmin,  pmax, nevents);
    }*/
    
    public static void trainClassifier(String file_tr, String file_ev, String networkArchive, int run, double vertexmin, double vertexmax, int max, int epochs){
        TrackNetworkTrainer t = new TrackNetworkTrainer();

        t.nEpochs = epochs;

        t.maxBinsRead = max;

        t.getConstrain().momentum.set(0.5,10.5);
        //t.getConstrain().vertex.set( -15.0,  5.0); // RG-A
        t.getConstrain().vertex.set( vertexmin,vertexmax); // RG-F
        t.getConstrain().chiSquare.set(0,10);// the chi2 is normalized to NDF

        t.getConstrain().show();

        t.classifierTrain( file_tr,networkArchive,run,"temp");
        t.classifierTest(  file_ev,networkArchive,run,"temp");

        TrackNetworkValidator v = new TrackNetworkValidator();

        v.archiv     = networkArchive;
        v.network    = "network/" + run + "/temp/trackClassifier.network";
        v.outputFile = "validation_postprocess.h5";
        v.getConstrain().momentum.set(0.5,10.5);
        v.getConstrain().vertex.set(vertexmin,vertexmax); // RG-A 
        v.getConstrain().chiSquare.set(0,10);// the chi2 is normalized to NDF

        v.getConstrain().show();
        v.processFile(file_tr);

        t.nEpochs = epochs;
        t.classifierTrain("validation_postprocess.h5",networkArchive,run,"default");
        t.classifierTest(   file_ev,networkArchive,run,"default");
    }
    
    public static void trainAutoEncoder(String file_tr, String file_ev, String networkArchive, int run, double vertexmin, double vertexmax, int max, int epochs){
         TrackNetworkTrainer t = new TrackNetworkTrainer();
         t.nEpochs = epochs;
         t.maxBinsRead = max;
         
         //String    trainFile = "training_sample_max150000_012922-012925.hipo";
         //String     testFile = "testing_sample_max150000_012930-012932.hipo";
         String       flavor = "default";
         //String  networkFile = "clas12rgf.network";
         //int       runNumber = 12922;
         
         t.getConstrain().momentum.set(0.5,10.5);
         t.getConstrain().vertex.set( -35.0, 25.0); // RG-F
         t.getConstrain().chiSquare.set(0,10);// the chi2 is normalized to NDF
         
         t.encoderTrain(file_tr,networkArchive,run,"default");
         t.encoderTest(file_ev,networkArchive,run,"default");
    }
    
    public static void main(String[] args){
        List<String> files = new ArrayList<>();
        /*Arrays.asList(
                "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00075-00079.hipo",
                "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00080-00084.hipo",
                "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00085-00089.hipo"
        );*/
        
        if(args.length>0){
            files.clear();
            for(int k = 0; k < args.length; k++){
                files.add(args[k]);
            }
            //String filename = "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00050-00054.hipo";
            //ClassifierDataExtract.processFiles(Arrays.asList(filename));                
            
            /*ClassifierDataExtract.processFiles(Arrays.asList(
            "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00050-00054.hipo",
            "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00055-00059.hipo",
            "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00060-00064.hipo",
            "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00065-00069.hipo",
            "/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00070-00074.hipo"
            ));*/
            ClassifierDataExtract.processFiles(files);
        }

        System.out.println("\n AI tracking data extraction...\n");
        System.out.println("\n Please provide file names\n\n");
        
        /*
        OptionStore storeParser = new OptionStore("run-extract.sh");
        storeParser.addCommand("-extract", "extract training file from cooked data files");
        storeParser.getOptionParser("-extract").addRequired("-o","Output file name");
        storeParser.getOptionParser("-extract").addOption("-max","25000","maximum number of events in each bin");
        
        
        storeParser.parse(args);
        
        if(storeParser.getCommand().compareTo("-extract")==0){

            String output = storeParser.getOptionParser("-extract").getOption("-o").stringValue();
            int       max = storeParser.getOptionParser("-extract").getOption("-max").intValue();
            
            List<String> filesList = storeParser.getOptionParser("-extract").getInputList();//new ArrayList<>();
        
            int[] occupancy = new int[40];
            for(int i = 0; i < 40; i++) occupancy[i] =0;
            

            
            HipoChain chain = new HipoChain();
            
            //chain.addFile(filename);
            chain.addFiles(filesList);
            chain.open();
            
            Bank tBank = chain.getBank("TimeBasedTrkg::TBTracks");
            Bank cBank = chain.getBank("TimeBasedTrkg::TBClusters");
            Bank hBank = chain.getBank("HitBasedTrkg::HBClusters");
            
            Event event = new Event();
            Event outEvent = new Event();
            
            HipoWriterSorted writer = new HipoWriterSorted();
            
            writer.open(output);//"extract_output.hipo");
            
            int counter = 0;
            //for(int i = 0; i < 10000; i++){
        // while(chain.hasNext()&&counter<1000){  
        ClusterStore store = new ClusterStore();
        ClusterCombinations comb = new ClusterCombinations();
        
        while(chain.hasNext()){  

              counter++;
              chain.nextEvent(event);
              event.read(cBank);
              event.read(tBank);
              event.read(hBank);
              
              List<Track>  trkList = Track.read(tBank,cBank);
              store.reset();
              
              for(Track t : trkList){                  
                  if(t.complete()==true&&t.chi2<10.0&&t.vertex.z()>-25.0&&t.vertex.z()<35.0){
                      //System.out.println(t);
                      List<Node> nodes = ClassifierDataExtract.getNode(t);
                      //System.out.println(t);
                      //System.out.println("n nodes = " + nodes.size());
                      outEvent.reset();
                      int bin = ClassifierDataExtract.getTrackBin(t);
                      if(bin>=0&&bin<20){
                          ClassifierDataExtract.read(store, hBank, t.sector);
                          store.getCombinationsFull(comb);
                          //System.out.println(t);
                          //System.out.println("size = " + comb.getSize());
                          int index = comb.bestMatch(t.clusters, t.means);
                          if(index>=0) {
                              double distance = comb.distance(index, t.means);
                              if(distance<25.0&&distance>2.0){
                                  //System.out.printf("distance = %12.6f\n",comb.distance(index, t.means));
                                  //System.out.println(comb.getRowString(index));
                                  double[] means = comb.getFeatures(index);
                                  Node negativeNode = new Node(1001,8, 
                                          new float[]{ 
                                              (float)means[0],
                                              (float)means[1],
                                              (float)means[2],
                                              (float)means[3],
                                              (float)means[4],
                                              (float) means[5]}
                                  );                                  
                                  int chargeBin = bin+1;                                  
                                  if(t.charge<0) chargeBin += 20;
                                  if(chargeBin>=1&&chargeBin<=40){
                                      occupancy[chargeBin-1] = occupancy[chargeBin-1] + 1;
                                      boolean writeStatus = true;
                                      if(occupancy[chargeBin-1]>max) writeStatus=false;
                                      if(writeStatus==true){                                      
                                          outEvent.setEventTag(chargeBin);
                                          for(Node n : nodes) outEvent.write(n);
                                          outEvent.write(negativeNode);
                                          writer.addEvent(outEvent,chargeBin);
                                      }
                                  }                                 
                              }
                          }
                      }
                  }
              }
        }
        
        System.out.println("processed event = " + counter);
        writer.close();
        }
*/
    }

    @Override
    public String getDescription() {
        return "AI utilities for DC tracking for CLAS12"; 
    }

    @Override
    public boolean execute(String[] args) {
        OptionStore store = this.getOptionStore();
        
        store.parse(args);
        if(store.getCommand().compareTo("-extract")==0){
            ClassifierDataExtract.processFiles(
                    store.getOptionParser("-extract").getInputList(),
                    store.getOptionParser("-extract").getOption("-o").stringValue(),
                    store.getOptionParser("-extract").getOption("-pmin").doubleValue(),
                    store.getOptionParser("-extract").getOption("-pmax").doubleValue(),
                    store.getOptionParser("-extract").getOption("-max").intValue());
                    /*.extract(
                    store.getOptionParser("-extract").getOption("-o").stringValue(), 
                    store.getOptionParser("-extract").getInputList(),
                    store.getOptionParser("-extract").getOption("-max").intValue());*/
        }
        
        if(store.getCommand().compareTo("-train")==0){
            String[] tokens = store.getOptionParser("-train").getOption("-vertex").stringValue().split(":");
            double min = Double.parseDouble(tokens[0]);
            double max = Double.parseDouble(tokens[1]);
            
            ClassifierDataExtract.trainClassifier(
                    store.getOptionParser("-train").getOption("-t").stringValue(),
                    store.getOptionParser("-train").getOption("-v").stringValue(),
                    store.getOptionParser("-train").getOption("-a").stringValue(),
                    store.getOptionParser("-train").getOption("-r").intValue(),
                    min,max,store.getOptionParser("-train").getOption("-max").intValue(),
                    store.getOptionParser("-train").getOption("-e").intValue());
            
            
             ClassifierDataExtract.trainAutoEncoder(
                    store.getOptionParser("-train").getOption("-t").stringValue(),
                    store.getOptionParser("-train").getOption("-v").stringValue(),
                    store.getOptionParser("-train").getOption("-a").stringValue(),
                    store.getOptionParser("-train").getOption("-r").intValue(),
                    min,max,store.getOptionParser("-train").getOption("-max").intValue(),
                    store.getOptionParser("-train").getOption("-e").intValue());
                    /*.extract(
                    store.getOptionParser("-extract").getOption("-o").stringValue(), 
                    store.getOptionParser("-extract").getInputList(),
                    store.getOptionParser("-extract").getOption("-max").intValue());*/
        }
        
        return true;
    }

    
}
