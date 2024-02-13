/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.finder;

import j4np.data.base.DataFrame;
import j4np.data.base.DataSource;
import j4np.data.base.DataSync;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.neural.classifier.NeuralClassifierModel;
import j4np.neural.clustering.NeuralClusterModel;
import j4np.neural.regression.NeuralRegressionModel;
import j4np.utils.base.ArchiveProvider;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class NeuralTrackFinder {
    
    protected NeuralClassifierModel classifier = new NeuralClassifierModel();
    protected NeuralRegressionModel regression = new NeuralRegressionModel();
    protected NeuralClusterModel    clustering = new NeuralClusterModel();

    protected String classifierNetworkFile = "etc/networks/clas12rgd.network";
    protected String regressionNetworkFile = "etc/networks/clas12rgd.network";
    protected String clusteringNetworkFile = "etc/networks/clusterFinder.dnet";
    
    protected int    networkRun = 10;
    
    public NeuralTrackFinder(){}
    
    
    public void setRun(int run){
        networkRun = run;
    }
    
    
    public String getFileWithLocation(String filename){
        if(System.getenv("CLAS12ML")!=null){
            String clas12mlpath = System.getenv("CLAS12ML");
            if(clas12mlpath.endsWith("/")==true) 
                return (clas12mlpath+filename);
            else return (clas12mlpath+"/"+filename);
        }
        
        return filename;
    }
    
    public void loadNetwork(){
        ArchiveProvider ap = new ArchiveProvider(regressionNetworkFile);
        int run = ap.findEntry(27);
        System.out.println(" run = " + run);
        regression.loadFromFile(
                getFileWithLocation(regressionNetworkFile)
                , networkRun);
        classifier.loadFromFile(
                getFileWithLocation(classifierNetworkFile), 
                networkRun);
        clustering.initFromFile(
                getFileWithLocation(clusteringNetworkFile));
    }
    
    public void init(HipoReader r){
        classifier.init(r);
        regression.init(r);
        
    }
    
    public void processEvent(Event e){
        try {
            //clustering.process(e);
            classifier.process(e);
            regression.processEvent(e);
        } catch (Exception ex){
            System.out.println("::::: neural finder - something went wrong.....");
        }
        //e.scanShow();
    }
    
    public void processSource(DataSource dsource, DataSync dsync, int frameSize){
        
        try{
            DataFrame<Event> frame = new DataFrame<>();
            for(int kappa = 0; kappa < frameSize; kappa++) frame.addEvent(new Event());
            
            while(dsource.hasNext()==true){
                dsource.nextFrame(frame);
                for(Event evt : frame.getList()){
                    this.processEvent(evt);
                }
                for(Event evt : frame.getList()){
                    dsync.add(evt);
                }
            }        
            dsync.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dsync.close();
        }
    }
        public static void reconstruct(String filein, String fileout){

        File descriptor = new File(fileout);
        if(descriptor.exists()==false){
            HipoReader r = new HipoReader(filein);
            HipoWriter w = HipoWriter.create(fileout, r);
            
            NeuralTrackFinder tf = new NeuralTrackFinder();
            tf.loadNetwork();
            tf.processSource(r, w, 20);
        } else {
            System.out.println("\n\n");
            System.out.printf("::::: skip file (exists): %s\n\n\n",fileout);
        }
        
    }
    
    public static void reconstruct(String filein){
        Path file = Path.of(filein);
        String fileout = file.getFileName().toString() + ".rec.h5";
        NeuralTrackFinder.reconstruct(filein, fileout);
        //System.out.println("file : " + file.getFileName().toString());
        /*HipoReader r = new HipoReader(filein);
        HipoWriter w = HipoWriter.create(fileout, r);
        
        NeuralTrackFinder tf = new NeuralTrackFinder();        
        tf.loadNetwork();  
        tf.processSource(r, w, 20); */
    }
    
    public static void reconstruct(List<String> fileList){
       for(String file : fileList) NeuralTrackFinder.reconstruct(file);
    }
    
    
    public static void main(String[] args){
        
        //String file = "/Users/gavalian/Work/Software/project-10.7/distribution/caos/coda/decoder/clas_005197.evio.00003.h5";
        /*String[] files = new String[]{
            "/Users/gavalian/Work/Software/project-10.7/distribution/caos/coda/decoder/clas_005197.evio.00002.h5",
            "/Users/gavalian/Work/Software/project-10.7/distribution/caos/coda/decoder/clas_005197.evio.00007.h5",
            "/Users/gavalian/Work/Software/project-10.7/distribution/caos/coda/decoder/clas_005197.evio.00008.h5",
            "/Users/gavalian/Work/Software/project-10.7/distribution/caos/coda/decoder/clas_005197.evio.00009.h5",
            "/Users/gavalian/Work/Software/project-10.7/distribution/caos/coda/decoder/clas_005197.evio.00005.h5",
            "/Users/gavalian/Work/Software/project-10.7/distribution/caos/coda/decoder/clas_005197.evio.00001.h5",
            "/Users/gavalian/Work/Software/project-10.7/distribution/caos/coda/decoder/clas_005197.evio.00006.h5"
        };*/
        String[] files = new String[]{
            //"/Users/gavalian/Work/Software/project-10.7/distribution/caos/coda/decoder/clas_005197.evio.00004.h5",
            //"/Users/gavalian/Work/Software/project-10.7/distribution/caos/coda/decoder/clas_005197.evio.00009.h5"
            "/Users/gavalian/Work/DataSpace/rgd/clas_018369.evio.01185_dc.h5"
        };
        for(String t : files){
            NeuralTrackFinder.reconstruct(t);
        }
    }
}
