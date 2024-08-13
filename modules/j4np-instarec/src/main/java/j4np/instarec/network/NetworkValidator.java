/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.network;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.instarec.core.InstaRecNetworks;
import j4np.instarec.core.TrackFinderNetwork;
import j4np.instarec.core.TrackFinderNetwork.TrackBuffer;
import j4np.instarec.core.TrackFinderUtils;
import j4np.instarec.core.Tracks;
import j4np.instarec.utils.EJMLLoader;
import j4np.instarec.utils.EJMLModel;
import j4np.physics.Vector3;
import j4np.utils.io.TextFileReader;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import twig.data.DataGroup;
import twig.data.H1F;
import twig.data.H2F;
import twig.data.TDirectory;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class NetworkValidator {
    
    public static List<String> fixer(List<float[]> data, List<String> network){
        List<String> lines = new ArrayList<>();
        
        EJMLModel model = EJMLModel.create(network);
        Random r = new Random();
        float[] saved = new float[2];
        
        for(int i = 0; i < data.size(); i++){
            int which = r.nextInt(6);            
            float[] input  = data.get(i);
            float[] output = new float[12];
            saved[0] = input[which*2]; saved[1] = input[which*2+1];
            input[2*which] = 0.0f; input[2*which+1] = 0.0f;
            model.feedForwardReLULinear(input, output);
            String result = String.format("%d,%.6f,%.6f,%.6f,%.6f", which,
                    saved[0],saved[1],output[which*2],output[which*2+1]
            );
            lines.add(result);
        }
        return lines;
    }
    
    public static boolean checkSectors(int[] sectors){
        int ref = sectors[0];
        for(int i = 0; i < sectors.length; i++) if(sectors[i]!=ref) return false;
        return true;
    }
    public static boolean checkLayers(int[] layers){
        for(int i = 0; i < layers.length; i++) if(layers[i]!=(i+1)) return false;
        return true;
    }
    
    
    
    
    public static int[] shares(int[] clusters, Tracks trk){
        int[] stats = new int[]{0,0};
        //System.out.println("\n***********\nTRACK = " + Arrays.toString(clusters));
        float[] f = new float[12];
        for(int k = 0; k < trk.getRows(); k++){
            int howMany = trk.contains(k, clusters);
            if(howMany==6) stats[0]++;
            if(howMany>0&&howMany<6) {
                stats[1]++;
                //trk.show(k);
                //trk.getInput12(f, k);
                //System.out.println(Arrays.toString(f));
            }
        }

        return stats;
    }
    
    public static void getStatsInference(Tracks trk, TrackFinderNetwork net, Histograms hist){
        Vector3 vec = new Vector3();
        float[]   feat = new float[12];
        float[]    out = new float[3];
        
        for(int i = 0; i < trk.getRows(); i++){
            int charge = trk.charge(i);
            int sector = trk.sector(i);
            trk.vector(vec, i);
            if(trk.count(i)==6){
                trk.getInput12(feat, i);
                net.network().getClassifier().feedForwardSoftmax(feat, out);
                int label = EJMLModel.getLabel(out);
                int index = 0; if(charge>0) index = 1;
                hist.data[index].fill(sector, vec.mag());
                if(out[index+1]<0.5) {
                    hist.data[index+2].fill(sector, vec.mag());
                }
            }
            
            if(trk.count(i)==5){
                
            }
        }
    }
    
    public static void getStatsReconstruction(Tracks cvtrk, Tracks aitrk, Histograms hist){
        Vector3    vec = new Vector3();
        
        int[]     clusters = new int[6];
        float[]       feat = new float[12];
        float[]        out = new float[3];
        
        for(int i = 0; i < cvtrk.getRows(); i++){
            int charge = cvtrk.charge(i);
            int sector = cvtrk.sector(i);
            cvtrk.getClusters(clusters, i);
            cvtrk.vector(vec, i);
            
            int index = 0; if(charge>0) index = 1;
            hist.data[4+index].fill(sector, vec.mag());
            
            if(cvtrk.count(i)==6){
               int[] stats = NetworkValidator.shares(clusters, aitrk);
               if(stats[0]==0){ hist.data[6+index].fill(sector, vec.mag());}
               if(stats[1]>0) { hist.data[8+index].fill(sector, ((double)stats[1]));}
            }
        }
    }
    
    public static void classifier(String file, String network, int run) throws Exception{
        
        HipoReader r = new HipoReader(file);                
        Bank [] b = r.getBanks("TimeBasedTrkg::TBTracks","HitBasedTrkg::Clusters","RUN::config");
        
        Tracks cvTracks = new Tracks(100);        
        TrackFinderNetwork net = new TrackFinderNetwork();        
        net.init(network, run);
        
        Histograms histo = new Histograms(12,6,0.5,6.5,20,0.0,10.0);
        
        Vector3 vec = new Vector3();
        Vector3 vrt = new Vector3();
        
        int[] clusters = new int[6];
        
        while(r.nextEvent(b)==true){            
            DataExtractor.getTracks(cvTracks, b[0],b[1]);
            NetworkValidator.getStatsInference(cvTracks, net, histo);
            Tracks aiTracks = net.processBank(b[1]);
            
            NetworkValidator.getStatsReconstruction(cvTracks, aiTracks, histo);
            //aiTracks.show();
        }
        
        DataGroup ginf = histo.getGroup("Performance", new String[]{"fc=2","fc=32","fc=6","fc=86"} , new int[]{0,2,1,3});
        DataGroup grec = histo.getGroup("PerformanceRec", new String[]{"fc=2","fc=32","fc=6","fc=86"} , new int[]{4,6,5,7});
        DataGroup gmul = histo.getGroup("MultiplicityRec", new String[]{"fc=32","fc=86"} , new int[]{8,9});
        
        TGCanvas c = new TGCanvas();
        ginf.draw(c.view(), true);
        
        TGCanvas c1 = new TGCanvas();
        grec.draw(c1.view(), true);
        
        TGCanvas c2 = new TGCanvas("multiplicity",1200,900);
        gmul.draw(c2.view(), true);
        
    }
    public static void classifier2(String file, String network, int run) throws Exception{
        
        HipoReader r = new HipoReader(file);                
        Bank [] b = r.getBanks("TimeBasedTrkg::TBTracks","HitBasedTrkg::Clusters","RUN::config");
        
        Tracks cvTracks = new Tracks(100);
        
        TrackFinderNetwork net = new TrackFinderNetwork();
        
        net.init(network, run);
        
        float[]  features = new float[12];
        float[] features6 = new float[12];
        float[]    output = new float[3];
        
        H2F[] hrec   = new H2F[]{ 
            new H2F("hnegrec",6,0.5,6.5,20,0.0,10.0),
            new H2F("hposrec",6,0.5,6.5,20,0.0,10.0)
        };
        
        H2F[] hunrec = new H2F[]{ 
            new H2F("hnegunrec",6,0.5,6.5,20,0.0,10.0),
            new H2F("hposunrec",6,0.5,6.5,20,0.0,10.0)
        };
        
        H2F[] htracks = new H2F[]{ 
            new H2F("htrackspos",6,0.5,6.5,20,0.0,10.0),
            new H2F("htracksneg",6,0.5,6.5,20,0.0,10.0)
        };
        
        H2F[] htracksunrec = new H2F[]{ 
            new H2F("htracksposunrec",6,0.5,6.5,20,0.0,10.0),
            new H2F("htracksnegunrec",6,0.5,6.5,20,0.0,10.0)
        };
        
        H2F[] hmult = new H2F[]{ 
            new H2F("hmultpos",6,0.5,6.5,100,-0.5,99.5),
            new H2F("hmultneg",6,0.5,6.5,100,-0.5,99.5)
        };
        
        hrec[0].attr().setTitleY("P (+) [GeV]");
        hrec[1].attr().setTitleY("P (-) [GeV]");
        hunrec[0].attr().setTitleY("P (+) [GeV]");
        hunrec[1].attr().setTitleY("P (-) [GeV]");
        
        
        H2F[] hrec5   = new H2F[]{ 
            new H2F("hnegrec5",6,0.5,6.5,20,0.0,10.0),
            new H2F("hposrec5",6,0.5,6.5,20,0.0,10.0)
        };
        
        H2F[] hunrec5 = new H2F[]{ 
            new H2F("hnegunrec5",6,0.5,6.5,20,0.0,10.0),
            new H2F("hposunrec5",6,0.5,6.5,20,0.0,10.0)
        };
        
        hrec5[0].attr().setTitleY("P (+) [GeV]");
        hrec5[1].attr().setTitleY("P (-) [GeV]");
        hunrec5[0].attr().setTitleY("P (+) [GeV]");
        hunrec5[1].attr().setTitleY("P (-) [GeV]");
        
        Vector3 vec = new Vector3();
        Vector3 vrt = new Vector3();
        int[] clusters = new int[6];
        
        while(r.nextEvent(b)==true){
            
            DataExtractor.getTracks(cvTracks, b[0],b[1]);
            Tracks aiTracks = net.processBank(b[1]);
            //b[2].show();
            //System.out.println("-------------------------");
            //cvTracks.show();
            //aiTracks.show();
            //tr.show();
            for(int i = 0; i < cvTracks.getRows(); i++){
                if(cvTracks.count(i)==6){
                    cvTracks.getInput12(features, i);
                    cvTracks.vector(vec, i);
                    net.network().getClassifier().feedForwardSoftmax( features, output);
                    
                    int index = 1; if(cvTracks.charge(i)>0) index = 2;
                    //System.out.printf(" %3d : %s\n",i, Arrays.toString(output));

                    if(output[index]>0.5) hrec[index-1].fill(cvTracks.sector(i), vec.mag());
                        else { 
                        hunrec[index-1].fill(cvTracks.sector(i), vec.mag());
                        //System.out.println("features 6 : " + Arrays.toString(features) + " array " + Arrays.toString(output));
                        //System.out.println("ROW = " + i);
                        //tr.show();
                    }
                    cvTracks.getClusters(clusters, i);
                    //cvTracks.show(i);
                    int[] stats = NetworkValidator.shares(clusters, aiTracks);
                    if(stats[0]==1){ htracks[index-1].fill(cvTracks.sector(i), vec.mag());}
                    else { 
                        htracksunrec[index-1].fill(cvTracks.sector(i), vec.mag());
                    }
                    hmult[index-1].fill(cvTracks.sector(i), stats[1]);
                }
                
                if(cvTracks.count(i)==5){
                    cvTracks.getInput12(features, i);
                    cvTracks.vector(vec, i);
                    net.network().getFixer().feedForwardReLULinear(features, features6);
                    //System.out.println(" features 5 = " + Arrays.toString(features));
                    //System.out.println(" features 6 = " + Arrays.toString(features6));
                    for(int j = 0; j < features.length; j++) if(features[j]<0.00001) features[j] = features6[j];
                    net.network().getClassifier().feedForwardSoftmax(features, output);
                    //System.out.println(Arrays.toString(output));
                    int index = 1; if(cvTracks.charge(i)>0) index = 2;
                    if(output[index]>0.5) hrec5[index-1].fill(cvTracks.sector(i), vec.mag());
                        else { 
                        hunrec5[index-1].fill(cvTracks.sector(i), vec.mag());                        
                    }                      
                }
            }
        }
        
        DataGroup group = new DataGroup("Performance",6,4);
        
        group.setRegionAttributes("fc=#EEEDED,mt=15,mb=45,mr=15");
        List<H1F> list0 = hrec[0].getSlicesX("fc=42,lw=2,lc=2");
        List<H1F> list1 = hrec[1].getSlicesX("fc=46,lw=2,lc=6");
        List<H1F> list2 = hunrec[0].getSlicesX("fc=82,lw=2,lc=2");
        List<H1F> list3 = hunrec[1].getSlicesX("fc=86,lw=2,lc=6");
        
        for(int i = 0; i < list0.size(); i++) group.add(list0.get(i), i   , "");
        for(int i = 0; i < list2.size(); i++) group.add(list2.get(i), i+6, "");        
        for(int i = 0; i < list1.size(); i++) group.add(list1.get(i), i+12 , "");
        for(int i = 0; i < list3.size(); i++) group.add(list3.get(i), i+18, "");
        
        DataGroup group5 = new DataGroup("Performance5",6,4);
        group5.setRegionAttributes("fc=#EEEDED,mt=15,mb=45,mr=15");
        List<H1F> list05 = hrec5[0].getSlicesX("fc=42,lw=2,lc=2");
        List<H1F> list15 = hrec5[1].getSlicesX("fc=46,lw=2,lc=6");
        List<H1F> list25 = hunrec5[0].getSlicesX("fc=82,lw=2,lc=2");
        List<H1F> list35 = hunrec5[1].getSlicesX("fc=86,lw=2,lc=6");
        
        for(int i = 0; i < list05.size(); i++) group5.add(list05.get(i), i   , "");
        for(int i = 0; i < list25.size(); i++) group5.add(list25.get(i), i+6, "");        
        for(int i = 0; i < list15.size(); i++) group5.add(list15.get(i), i+12 , "");
        for(int i = 0; i < list35.size(); i++) group5.add(list35.get(i), i+18, "");        
        
        
        DataGroup groupE = new DataGroup("Efficiency",6,6);
        groupE.setRegionAttributes("fc=#EEEDED,mt=15,mb=45,mr=15");
        
        List<H1F> list0E = htracks[0].getSlicesX("fc=42,lw=2,lc=2");
        List<H1F> list1E = htracks[1].getSlicesX("fc=46,lw=2,lc=6");
        List<H1F> list2E = hmult[0].getSlicesX("fc=82,lw=2,lc=2");
        List<H1F> list3E = hmult[1].getSlicesX("fc=86,lw=2,lc=6");
        List<H1F> list4E = htracksunrec[0].getSlicesX("fc=82,lw=2,lc=2");
        List<H1F> list5E = htracksunrec[1].getSlicesX("fc=86,lw=2,lc=6");
        
        for(int i = 0; i < list0E.size(); i++) groupE.add(list0E.get(i), i   , "");
        for(int i = 0; i < list2E.size(); i++) groupE.add(list2E.get(i), i+6, "");        
        for(int i = 0; i < list4E.size(); i++) groupE.add(list4E.get(i), i+12, "");
        for(int i = 0; i < list1E.size(); i++) groupE.add(list1E.get(i), i+18 , "");
        for(int i = 0; i < list3E.size(); i++) groupE.add(list3E.get(i), i+24, "");  
        for(int i = 0; i < list5E.size(); i++) groupE.add(list5E.get(i), i+30, "");  
        
        TDirectory.export("validation.twig", 
                String.format("validation/%d/classifier",run), group);
        
        TDirectory.export("validation.twig", 
                String.format("validation/%d/classifier",run), group5);
        
        TDirectory.export("validation.twig", 
                String.format("validation/%d/classifier",run), groupE);
        
        TGCanvas c = new TGCanvas();
        group.draw(c.view(), true);
    }
    
    public static class Histograms {
        public H2F[] data = null;
        public String region = "fc=#EEEDED,mt=15,mb=45,mr=15";
        public Histograms(int count, int binsx, double xmin, double xmax, int binsy, double ymin, double ymax){
            data = new H2F[count];
            for(int i = 0; i < data.length; i++)
                data[i] = new H2F("h2_"+i,binsx,xmin,xmax,binsy,ymin,ymax);
        }
        
        public DataGroup getGroup(String name, String[] options, int[] order){
            DataGroup group = new DataGroup(name,6,order.length);
            group.setRegionAttributes(region);
            
            int counter = 0;
            for(int i = 0; i < order.length; i++){
                List<H1F> hx = data[order[i]].getSlicesX(options[i]);
                for(int k = 0; k < hx.size(); k++){
                    group.add(hx.get(k), counter, "");
                    counter++;
                }
            }
            return group;
        }
    }
    
    public static void filter(String file){
        Tracks tml = new Tracks(100);
        Tracks tcv = new Tracks(100);
        
        HipoReader r = new HipoReader(file);
        HipoWriter w = HipoWriter.create("wout_out_2.h5", r);
        
        Bank[] b = r.getBanks("TimeBasedTrkg::TBTracks","HitBasedTrkg::Clusters");
        Event ev = new Event();
        while(r.next(ev)){
            try {
                ev.read(b);
                tml.dataNode().setRows(0);
                if(ev.scan(tml.dataNode().getGroup(), tml.dataNode().getItem())>0)
                    ev.read(tml.dataNode());
                
                DataExtractor.getTracks(tcv, b[0], b[1]);
                System.out.println("sizes = " +  tcv.size() + "  " + tml.size());
                if(tcv.size()==2&&tml.size()==0) w.addEvent(ev);
            } catch (Exception e){ System.out.println("ooops");}
        }
        w.close();
    }
    
    public static void checkInference(String file){
        Tracks tml = new Tracks(100);
        Tracks tcv = new Tracks(100);
        
        HipoReader r = new HipoReader(file);
        
        Bank[] b = r.getBanks("TimeBasedTrkg::TBTracks","HitBasedTrkg::Clusters");
        float[] f = new float[12]; float[] out = new float[3];
        InstaRecNetworks net = new InstaRecNetworks("etc/networks/clas12default.network",2);
        net.show();
        Event ev = new Event();
        while(r.next(ev)){
            ev.read(b);                        
            DataExtractor.getTracks(tcv, b[0], b[1]);
            for(int i = 0; i < tcv.getRows(); i++){
                if(tcv.count(i)==6){
                    tcv.getInput12(f, i);

                    net.getClassifier().feedForwardSoftmax(f, out);
                    if(Float.isNaN(out[0])){
                        System.out.println(Arrays.toString(f) + "  " + Arrays.toString(out));
                    }
                }
            }
        }
    
    }
    public static void main(String[] args){
        //String file = "rec_clas_005342.evio.00000.hipo";
        String file = "rec_clas_005342.evio.00370.hipo";
        String file2 = "wout.h5";
        //NetworkValidator.filter("wout.h5");
        
        //file = "wout_out_2.h5";
        
        //NetworkValidator.multiplicity(file2,"etc/networks/clas12default.network",2);
        
        //NetworkValidator.checkInference(file);
        
        try {
            NetworkValidator.classifier(file, "etc/networks/clas12default.network", 2);
           
        } catch (Exception ex) {
            Logger.getLogger(NetworkValidator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
