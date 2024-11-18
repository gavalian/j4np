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
import j4np.instarec.utils.NeuralModel;
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
import twig.data.H3F;
import twig.data.TDirectory;
import twig.graphics.TGCanvas;
import twig.graphics.TTabCanvas;

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
    
    
    public static List<Integer> getShares(Bank b, int[] cid){
        List<Integer> iter = new ArrayList<>();        
        for(int i = 0; i < b.getRows(); i++){
            int[] lid = b.getIntArray(6, "c1", i);
            if(Tracks.match(lid, cid)>0) iter.add(i);
        }
        return iter;
    }
    
    public static int highest(Bank b, List<Integer> iter){
        if(b.getRows()==0) return -1;
        int     max = 0;
        double prob = b.getFloat("prob", 0);
        for(int i = 0; i < iter.size(); i++){
            int idx = iter.get(i);
            if(b.getFloat("prob", idx)>prob){ prob = b.getFloat("prob", idx); max = idx;}
        }
        return max;
    }
    
    public static void multiplicity(String file, int run){
        
        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks("TimeBasedTrkg::TBTracks","HitBasedTrkg::Clusters"
                ,"instarec::tracks");
        
        Tracks cvtr = new Tracks(128);
        Vector3 vec = new Vector3();
        
        H3F h = new H3F(6,0.5,6.5,40,0.0,10.0,6,0.5,6.5);
        h.setName("multiplicity");
        h.attr().setTitleX("Sector");
        h.attr().setTitleY("P [GeV]");
        
        int[] cid = new int[6];
        int counter = 0; int ohno = 0; int miss = 0;
        while(r.nextEvent(b)){
            DataExtractor.getTracks(cvtr, b[0], b[1]);  
            
            /*
            System.out.println("---event ");
            for(int j = 0; j < b[2].getRows(); j++){
                int[] ids = b[2].getIntArray(6, "c1", j);
                int index = cvtr.findMatch(ids);
                int count = Tracks.countClusters(cid);
                System.out.printf("--- %d %d  count = %d %f %s\n",j,index, count,
                        b[2].getFloat("prob", j),Arrays.toString(ids));
            }
            cvtr.show();
            */

            //cvtr.show();
            for(int i = 0; i < cvtr.getRows(); i++){
                
                if(cvtr.count(i)==6){
                
                    cvtr.vector(vec, i);
                    int sector = cvtr.sector(i);
                    
                    cvtr.getClusters(cid, i);
                    
                    List<Integer> iter = NetworkValidator.getShares(b[2], cid);
                    counter++;
                    h.fill(sector, vec.mag(), 1);
                    if(iter.size()==0){
                        System.out.println(" Ohhh nooooo.... "); ohno++;
                        h.fill(sector, vec.mag(), 3);
                    } else if (iter.size()>3){
                        int  highest = NetworkValidator.highest(b[2], iter);
                        int[] aid = b[2].getIntArray(6, "c1", highest);
                        int match = Tracks.match(aid, cid);
                        if(match<6&&match>0) {
                            h.fill(sector, vec.mag(), 2);
                            miss++;
                            System.out.println(iter.size() + " " + b[2].getFloat("prob",highest) + "  " + match);
                        }
                    }
                }
            }
            
        }        
        
        List<H2F> slicesZ = h.getSlicesZ();
        DataGroup grp1 = NetworkValidator.createGroup(slicesZ, new int[]{0,1,2},
                new String[]{"fc=86","fc=46","fc=82"}, "fc=#EEEDED,mt=15,mb=45,mr=15");
        grp1.setName("Multiplicity");
        TDirectory.export("validation.twig", 
                String.format("validation/%d/classifier",run), grp1);
        System.out.println("processed " + counter + "  oh noo = "  + ohno + " miss = " + miss);
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
        
    public static DataGroup createGroup(List<H2F> h, int[] index, String[] attrs, String options){
        int xbins = h.get(0).getAxisX().getNBins();
        int ybins = index.length;
        DataGroup group = new DataGroup(xbins,ybins);
        group.setRegionAttributes(options);
        
        int region = 0;
        for(int i = 0; i < index.length; i++){
            List<H1F> h1l = h.get(index[i]).getSlicesX(attrs[i]);
            for(H1F h1 : h1l){
                group.add(h1, region, "");region++;
            }
        }
        return group;
    }
    public static void regression(String file, String networkFile, int run, int sector, int charge){
        HipoReader r = new HipoReader(file);                
        Bank [] b = r.getBanks("TimeBasedTrkg::TBTracks","HitBasedTrkg::Clusters","RUN::config");
        String name = charge>0?"p":"n";
        
        Tracks cvtr = new Tracks(100); 
        
        InstaRecNetworks net = new InstaRecNetworks();
        net.init(networkFile, run);
        net.show();
        List<String> lines = TextFileReader.readFile(networkFile);
        EJMLModel model = null;
        try {
            model = EJMLLoader.load(networkFile, String.format("%d/%s/trackregression12.network",sector,name),run,"default");
        } catch (Exception ex) {
            Logger.getLogger(NetworkValidator.class.getName()).log(Level.SEVERE, null, ex);
        }
        float[] feat = new float[12];
        float[]  out = new float[3];
        Vector3 vec = new Vector3();
        
        while(r.nextEvent(b)){
            DataExtractor.getTracks(cvtr, b[0], b[1]);
            for(int i = 0; i < cvtr.getRows(); i++){
                if(cvtr.count(i)==6&&cvtr.sector(i)==sector&&cvtr.charge(i)==charge){
                    cvtr.getInput12(feat, i);
                    cvtr.vector(vec, i);
                    //model.feedForwardTanhLinear(feat, out);
                    
                    int which = charge<0?0:1;
                    net.getRegression()[which][sector-1].feedForwardTanhLinear(feat, out);
                    Vector3 vrec = cvtr.getVector(i, out);
                    System.out.println(Arrays.toString(feat) + "  ==> " + Arrays.toString(out));
                    System.out.println(vec);
                    System.out.println(vrec);
                }
            }
        }
    }
    
    public static void reconstruction(String file){
        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks("TimeBasedTrkg::TBTracks","HitBasedTrkg::Clusters","TimeBasedTrkg::AITracks", "instarec::tracks");
        Tracks cvTracks = new Tracks(100);
        Tracks aiTracks = new Tracks(100);
        Tracks prTracks = new Tracks(100);
        
        int[]  clusters = new int[6];
        int[] countersP = new int[]{0,0,0,0};
        int[] countersN = new int[]{0,0,0,0};
        
        int[] countersP2 = new int[]{0,0,0,0};
        int[] countersN2 = new int[]{0,0,0,0};
        int[] countersAI = new int[]{0,0,0,0};
        
        int counter = 0; int counterev = 0; int c2= 0;
        while(r.nextEvent(b)==true){
            DataExtractor.getTracks(cvTracks, b[0], b[1]);
            DataExtractor.getTracks(aiTracks, b[2], b[1]);
            
            DataExtractor.getTracksInstarec(prTracks, b[3]);
            
            System.out.println("---- event\n");
            for(int i = 0; i < cvTracks.getRows(); i++){
                cvTracks.getClusters(clusters, i);
                if(cvTracks.count(i)==6){
                    if(cvTracks.charge(i)<0) countersN[0]++; else countersP[0]++;
                    int match  = aiTracks.findMatch(clusters);
                    int match2 = prTracks.findMatch(clusters);
                    if(match <0) if(cvTracks.charge(i)<0) countersN[1]++; else countersP[1]++;
                    if(match2<0) if(cvTracks.charge(i)<0) countersN[2]++; else countersP[2]++;
                    //System.out.printf("track %d --> %5d %5d\n",i,match, match2);
                }
            }
            
            for(int i = 0; i < aiTracks.getRows(); i++){
                aiTracks.getClusters(clusters, i);
                if(aiTracks.count(i)==6){
                    if(aiTracks.charge(i)<0) countersN2[0]++; else countersP2[0]++;
                    int match  = cvTracks.findMatch(clusters);
                    int match2 = prTracks.findMatch(clusters);
                    if(match <0) if(aiTracks.charge(i)<0) countersN2[1]++; else countersP2[1]++;
                    if(match2<0) if(aiTracks.charge(i)<0) countersN2[2]++; else countersP2[2]++;
                    //System.out.printf("track %d --> %5d %5d\n",i,match, match2);
                }
            }
            
            System.out.println("---- Nu Event");
            //prTracks.show();
            prTracks.reduce();
            //System.out.println(" AFTER");
            //prTracks.show();
            
            
            //prTracks.reduce();
            //int[] tid = new int[6];            
            for(int j = 0; j < prTracks.getRows(); j++){
                if(prTracks.status(j)>0&&prTracks.count(j)==6){
                    countersAI[0]++;
                    prTracks.getClusters(clusters, j);
                    int matchcv = cvTracks.findPartial(clusters);
                    int matchai = aiTracks.findPartial(clusters);
                    if(matchcv==0&&matchai==0&&prTracks.probability(j)>0.99) { 
                        System.out.println(" BIG DIAL.... " + j); countersAI[1]++;
                        prTracks.show();
                        aiTracks.show();
                        cvTracks.show();
                    }
                }
            }
            
            /*
            System.out.println("------- event");
            boolean flag2 = false;
            for(int i = 0; i < cvTracks.getRows(); i++){
                cvTracks.getClusters(clusters, i);
                if(cvTracks.count(i)==5){           
                    int match = aiTracks.findMatch(clusters);
                    //System.out.println("row = " + i + "  match = " + match);
                    if(match<0) { 
                        flag2 = true;
                        //cvTracks.show(i); aiTracks.show();
                    }
                }
            }
            if(flag2) c2++;
            boolean flag = false;
            for(int i = 0; i < aiTracks.getRows(); i++){
                aiTracks.getClusters(clusters, i); 
                
                if(aiTracks.count(i)==5){
                    int match = cvTracks.findMatch(clusters);
                    if(match<0&&aiTracks.probability(i)>0.9){
                        aiTracks.show(i);
                        System.out.println("--");
                        cvTracks.show();
                        counter++; flag = true;
                    }
                }
                
            }*/
            //if(flag) counterev++;
            //cvTracks.show();
            //aiTracks.show();
        }
        System.out.println(" number = " + counter + "  " + counterev + "   " + c2);
        System.out.println("POSITIVE  : " + Arrays.toString(countersP));
        System.out.println("NEAGATIVE : " + Arrays.toString(countersN));
        System.out.println("------");
        System.out.println("POSITIVE  : " + Arrays.toString(countersP2));
        System.out.println("NEAGATIVE : " + Arrays.toString(countersN2));
        System.out.println("------");
        System.out.println("AI INFERENCE : " + Arrays.toString(countersAI));
        
    }
    
    public static void classifier(String file, String network, int run, boolean flag){
        
        HipoReader r = new HipoReader(file);                
        Bank [] b = r.getBanks("TimeBasedTrkg::TBTracks","HitBasedTrkg::Clusters","RUN::config");
        
        Tracks cvTracks = new Tracks(100);        
        InstaRecNetworks net = new InstaRecNetworks();
        net.init(network, run);
        
        net.show();
        
        float[] f12 = new float[12];
        float[]  f6 = new float[6];
        float[] out = new float[3];
        
        H3F h = new H3F(6,0.5,6.5,40,0.0,10.0,8,0.5,8.5);
        h.setName("classifier");
        h.attr().setTitleX("Sector");
        h.attr().setTitleY("P [GeV]");
        Vector3 vec = new Vector3();
        int count5 = 0;
        int count5m = 0;
        
        while(r.nextEvent(b)==true){
            
            DataExtractor.getTracks(cvTracks, b[0],b[1]);
            
            
            //cvTracks.show();
            
            for(int row = 0; row < cvTracks.getRows(); row++){
                if(cvTracks.count(row)==6){
                    cvTracks.getInput6( f6, row);
                    cvTracks.getInput12(f12, row);
                    cvTracks.vector(vec, row);
                    
                    int charge = cvTracks.charge(row);
                    int sector = cvTracks.sector(row);                 
                    int bin = charge<0?1:2;                    
                    
//--                    net.getClassifier6().feedForwardSoftmax(f6, out);
                    int label6 = EJMLModel.getLabel(out);
                    
                    int statsBin = label6==bin?1:2;
                    if(charge>0) statsBin += 2;
                    h.fill(sector, vec.mag(), statsBin);
                    if(net.getClassifier()!=null )net.getClassifier().feedForwardSoftmax(f12, out);
                    
                    int label12 = EJMLModel.getLabel(out);
                    
                    statsBin = label12==bin?5:6;
                    if(charge>0) statsBin += 2;
                    h.fill(sector, vec.mag(), statsBin);
                }
                if(cvTracks.count(row)==5){
                    count5++;
                    int[] cid = new int[6];
                    cvTracks.getInput12(f12, row);
                    cvTracks.getClusters(cid, row);
                    int which = TrackFinderUtils.which(f12);
                    
                    
                   // System.out.println("---\n---");
                   // System.out.printf(" %d  - %s\n", which, Arrays.toString(f12));
                    float[] fixed = new float[12];
                    net.getFixer().feedForwardReLULinear(f12,fixed);

                    //System.out.println(" fixed - " + Arrays.toString(fixed));
                    f12[which] = fixed[which]; f12[which+1] = fixed[which+1];
                    float[] input =  new float[6];
                    for(int k = 0; k < 6; k++) input[k] = 0.5f*(f12[2*k]+f12[2*k+1]);
                    float[] output = new float[3];
//--                    net.getClassifier6().feedForwardSoftmax(input, output);
                    if(output[0]>0.5){
                        System.out.println("oooops");
                        cvTracks.show(row); count5m++;
                    }
                    //System.out.println(Arrays.toString(output));
                }
            }
            

        }
        System.out.println("----- " + count5 + "  - " + count5m);
        List<H2F> slicesZ = h.getSlicesZ();
        
        TTabCanvas c = new TTabCanvas("NET-6","NET-12");
        
        DataGroup grp1 = NetworkValidator.createGroup(slicesZ, new int[]{0,1,2,3},
                new String[]{"fc=86","fc=46","fc=82","fc=42"}, "fc=#EEEDED,mt=15,mb=45,mr=15");
        DataGroup grp2 = NetworkValidator.createGroup(slicesZ, new int[]{4,5,6,7},
                new String[]{"fc=86","fc=46","fc=82","fc=42"},"fc=#EEEDED,mt=15,mb=45,mr=15");
        
        grp1.draw(c.getDataCanvas().activeCanvas(), true);
        c.getDataCanvas().setSelected(1);
        grp2.draw(c.getDataCanvas().activeCanvas(), true);
        
        grp1.setName("Efficiency");
        grp2.setName("Efficiency12");
        
        TDirectory.export("validation.twig", 
                String.format("validation/%d/classifier",run), grp1);
        TDirectory.export("validation.twig", 
                String.format("validation/%d/classifier",run), grp2);
        //c.getDataCanvas().activeCanvas()
        /*c.getDataCanvas().activeCanvas().divide(2, 4);
        System.out.println(slicesZ.size());
        for(int i = 0; i < 8; i++)
            c.getDataCanvas().activeCanvas().region(i).draw(slicesZ.get(i).projectionY());
        */
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
        HipoWriter w = new HipoWriter();
        w.open("missed.h5");
        Bank[] b = r.getBanks("TimeBasedTrkg::TBTracks","HitBasedTrkg::Clusters");
        float[] f = new float[12]; float[] out = new float[3];
        
        NeuralModel model = NeuralModel.jsonFile("trackclassifier12.json");
        System.out.println(model.summary());
        
        Event  ev = new Event();
        Event ev2 = new Event();
        
        int[] counter = new int[4];
        Vector3 v = new Vector3();
        while(r.next(ev)){
            ev.read(b);                        
            DataExtractor.getTracks(tcv, b[0], b[1]);
            for(int i = 0; i < tcv.getRows(); i++){
                if(tcv.count(i)==6){
                    tcv.getInput12raw(f, i);
                    model.predict(f, out);
                    if(tcv.charge(i)<0) counter[0]++; else counter[1]++;
                    tcv.vertex(v, i);
                    if(out[0]>0.5){//&&Math.abs(v.x())<2.5&&Math.abs(v.y())<2.5) {
                        if(tcv.charge(i)<0) counter[2]++; else counter[3]++;
                        tcv.show(i);
                        ev2.reset();
                        Tracks tout = new Tracks(3);
                        tout.dataNode().setRows(1);
                        tout.dataNode().copyRow(tcv.dataNode(), i, 0);
                        //System.out.println(Arrays.toString(out));
                        //System.out.print(" new row = "); tout.show(0);
                        ev2.write(tout.dataNode());
                        w.add(ev2);
                    }

                    /*
                    net.getClassifier().feedForwardSoftmax(f, out);
                    if(Float.isNaN(out[0])){
                        System.out.println(Arrays.toString(f) + "  " + Arrays.toString(out));
                    }*/
                }
            }
            
        }
        w.close();
        System.out.println("\n\n " + Arrays.toString(counter));
    }
        
    public static void cook(String file){
        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks("");
    }
    
    public static void main(String[] args){
        //String file = "../rec_clas_005342.evio.00000.hipo";
        String file = "rec_clas_005342.evio.00370.hipo";
        String file2 = "wout.h5";

        String file3 = "recon_denoising_after_update15b_10k.hipo";
        String file4 = "cook_very_new.h5";
        //NetworkValidator.filter("wout.h5");
        
        //NetworkValidator.classifier(file4, "etc/networks/clas12default.network", 16, true);
        
        //NetworkValidator.reconstruction("recon_00666_20k.h5");
        //NetworkValidator.multiplicity(file2,16); 
        //NetworkValidator.regression(file, "clas12default.network", 15, 1, 1);
        //NetworkValidator.compare(file3);
        //file = "wout_out_2.h5";
        
        //NetworkValidator.multiplicity(file2,"etc/networks/clas12default.network",2);
        
        NetworkValidator.checkInference(file);
        /*
        try {
            NetworkValidator.classifier2(file, "etc/networks/clas12default.network", 2);
           
        } catch (Exception ex) {
            Logger.getLogger(NetworkValidator.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
}
