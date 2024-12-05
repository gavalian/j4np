/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Leaf;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import twig.config.TPalette2D;
import twig.config.TStyle;
import twig.data.Graph;
import twig.data.H1F;
import twig.data.H2F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class DriftChamber3 {
    
    
    List<long[]> patterns = null;
    double[]     averages = null;
    
    boolean useSanitizer = true;
    boolean useDenoising = true;
    
    public DriftChamber3(){
        patterns = patternsLong();
        averages = new double[patterns.size()];
        for(int j = 0; j < averages.length; j++) 
            averages[j] = this.average(patterns.get(j));
    }
    /**
     * creates an array of longs with given strings in binary format. used
     * for generating patterns.
     * @param binary - stings in binary format (0's and 1's)
     * @return long[] array containing values of longs
     */
    public long[] createLong(String... binary){
        long[] array = new long[binary.length];
        for(int i = 0; i < array.length; i++)
            array[i] = Long.parseLong(binary[i], 2);
        return array;
    }
    /**
     * created segment patterns for comparing them with DC data
     * @return List of different segment patterns
     */
    public final List<long[]>  patternsLong(){
        List<long[]> p = new ArrayList<>();

        p.add(createLong("00001","00001","00001","00001","00001","00001"));
        p.add(createLong("00010","00001","00010","00001","00010","00001"));
        p.add(createLong("00001","00010","00001","00010","00001","00010"));
        p.add(createLong("00001","00001","00001","00010","00010","00010"));
        p.add(createLong("00001","00001","00010","00010","00010","00010"));
        p.add(createLong("00001","00001","00010","00010","00100","00100"));
        p.add(createLong("00001","00010","00010","00010","00100","00100"));
        p.add(createLong("00001","00010","00010","00100","00100","001000"));
        //----- the reverse slope
        p.add(createLong("01000","00100","00100","00010","00010","00001"));
        return p;
    }
    /**
     * Scan the data with each pattern and record number of hits that match
     * each pattern.
     * @param data - container with DC data
     * @param shift - right shift of the provided data
     * @param p - patterns
     * @param results - result containing the match with the current pattern
     */
    public void scanPatterns(long[] data, int shift, List<long[]> p, int[] results){
        for(int j = 0; j < data.length; j++) data[j] = data[j]>>shift;
        Arrays.fill(results, 0);
        //int[] results = new int[p.size()];
        for(int j = 0; j < p.size(); j++){
            long[] pattern = p.get(j);
            for(int i = 0; i < data.length; i++){
                long r = data[i]&pattern[i];
                if(r>0) results[j]++;
            }
        }
    }
    /**
     * calculates average wire value for given pattern
     * @param pattern
     * @return 
     */
    protected double average(long[] pattern){
        int[] bins = new int[8];
        int   summ = 0;
        for(int l = 0; l < pattern.length; l++){
            for(int i = 0; i < 8 ; i++){
               long value = (0x01L<<i)&pattern[l];
               if(value>0){ bins[i]++; summ++;}
            }
        }
        int average = 0;
        for(int i = 0; i < bins.length; i++) average += (i)*bins[i];
        return ((double) average)/summ;
    }
    /**
     * 
     * @param array
     * @return 
     */
    public int findmax(int[] array){
        int bin = 0; int max = array[0];
        for(int i = 0; i < array.length;i++){ 
            if(array[i]>max) { bin = i; max = array[i];}
        }
        return bin;
    }
    /**
     * Sanitizing requires more thinking. I will come back to this later.
     * @param data - data structure produced by drift chamber scan code
     */
    public void sanitize(ChamberData data){
        for(int i = 0; i < data.scan.length; i++){
            if(data.scan[i]<=3) data.scan[i] = 0;
            if(data.scan[i]==4){
                if(i<data.scan.length-1&&i>0){
                    if(data.scan[i+1]==5||data.scan[i+1]==6){ data.scan[i] = 0; }
                    else { data.scan[i+1] = 0;}
                    if(data.scan[i-1]<=4) data.scan[i-1] = 0;
                }
            }
            
            if(data.scan[i]==5){
                if(i<data.scan.length-1&&i>0){
                    if(data.scan[i+1]==6){ data.scan[i] = 0; }
                    else { data.scan[i+1] = 0;}
                    if(data.scan[i-1]<=5) data.scan[i-1] = 0;
                }
            }
            
            if(data.scan[i]==6){
                if(i<data.scan.length-1&&i>0){
                    if(data.scan[i-1]<=6) data.scan[i-1] = 0;
                    if(data.scan[i+1]<=6) data.scan[i+1] = 0;
                }
            }
        }
    }
    /**
     * 
     * @param data
     * @param sector
     * @param superlayer 
     */
    public void scan(ChamberData data, int sector, int superlayer){
        int offset = 12*6*(sector-1);
        int bunch  = (superlayer-1)*12;
        if(data.results==null) data.results = new int[patterns.size()];
        
        for(int j = 0; j < 6; j++) data.temp[j] = data.hits[offset+bunch+j*2+1];
        int shift = 0;
        for(int i = 0; i < 56; i++){
            scanPatterns(data.temp,shift,this.patterns, data.results);
            if(shift==0) shift=1;
            int max = findmax(data.results);
            data.type[i] = max;
            data.scan[i] = data.results[max];
        }
        
        for(int j = 0; j < 6; j++) data.temp[j] = data.hits[offset+bunch+j*2];
        shift = 0;
        for(int i = 0; i < 56; i++){
            scanPatterns(data.temp,shift,this.patterns,data.results);
            if(shift==0) shift=1;
            int max = findmax(data.results);
            data.type[i+56] = max;
            data.scan[i+56] = data.results[max];
        }
    }
    /**
     * finds segments in given superlayer and sector
     * @param data
     * @param segments
     * @param sector
     * @param superlayer 
     */
    public void segmentFinder(ChamberData data, Leaf segments, int sector, int superlayer){
        this.scan(data, sector, superlayer);
        this.sanitize(data);
        System.out.println(">>>>>");
        System.out.println(Arrays.toString(data.scan));
        System.out.println(Arrays.toString(data.type));
        
        //for(int i = 0; i < data.scan.length;i++){
            for(int i = data.scan.length-1; i >=0; i--){
            if(data.scan[i]>=4){
                int nrows = segments.getRows();
                double position = 111 - i + 1;
                segments.putShort(1, nrows, (short) (nrows+1));
                segments.putByte( 2, nrows, (byte) (sector));
                segments.putByte( 3, nrows, (byte) (superlayer));
                segments.putFloat(4, nrows, (float) (position-averages[data.type[i]]));
                segments.putFloat(5, nrows, (float) (position-averages[data.type[i]]));
                segments.putByte( 6, nrows, (byte) (111-i+1));
                segments.putByte( 7, nrows, (byte) data.scan[i]);
                segments.putByte( 8, nrows, (byte) data.type[i]);
                
                /*segments.putByte( 1, nrows, (byte) (sector));
                segments.putByte( 2, nrows, (byte) (superlayer));
                segments.putByte( 3, nrows, (byte) (111-i+1));
                segments.putByte( 4, nrows, (byte) data.scan[i]);
                segments.putByte( 5, nrows, (byte) data.type[i]);
                segments.putFloat(6, nrows, (float) (position-averages[data.type[i]]));
                segments.setRows(nrows+1);*/
            }
        }
    }
    /**
     * Finds segments in all sectors and super-layers.
     * @param data
     * @param segments 
     */
    public void segmentFinder(ChamberData data, Leaf segments){
        segments.setRows(0);
        for(int s = 0; s < 6; s++){
            for(int c = 0; c < 6; c++){
                this.scan(data, s+1, c+1);
                this.sanitize(data);
                //for(int i = 0; i < data.scan.length;i++){
                for(int i = data.scan.length-1 ; i >=0 ; i--){
                    if(data.scan[i]>=4){
                        int nrows = segments.getRows();
                        double position = 111 - i + 1;
                        segments.putShort(1, nrows, (short) (nrows+1));
                        segments.putByte( 2, nrows, (byte) (s+1));
                        segments.putByte( 3, nrows, (byte) (c+1));
                        segments.putFloat(4, nrows, (float) (position-averages[data.type[i]]));
                        segments.putFloat(5, nrows, (float) (position-averages[data.type[i]]));
                        segments.putByte( 6, nrows, (byte) (111-i+1));
                        segments.putByte( 7, nrows, (byte) data.scan[i]);
                        segments.putByte( 8, nrows, (byte) data.type[i]);

                        segments.setRows(nrows+1);
                    }
                }
            }
        }
    }       
    public Graph     getGraph(ChamberData data){
        Graph graph = new Graph();
        double shift = 120.0;
        double   gap = 10;
        
        for(int s = 0; s < 6; s++){
            for(int w = 0; w < 112; w++){
                for(int l = 0; l < 36; l++){
                    long value = data.get(s+1, l+1, w+1);
                    if(value>0){
                        //h2[s].setBinContent(w, l, 1.0);
                        double   r = shift + l*gap;
                        double phi = s*60.0 + w*60.0/112.0;
                        double frad = Math.toRadians(phi);
                        graph.addPoint(r*Math.cos(frad), r*Math.sin(frad));
                    }
                }
            }                
        }
        return graph;
    }
    public List<H2F> geth2(ChamberData data){        
        H2F[] h2 = H2F.duplicate(6,"DC",112,0.5,112.5,36,0.5,6.5);
        for(int s = 0; s < 6; s++){
            for(int w = 0; w < 112; w++){
                for(int l = 0; l < 36; l++){
                    long value = data.get(s+1, l+1, w+1);
                    if(value>0) h2[s].setBinContent(w, l, 1.0);
                }
            }                
        }
        return Arrays.asList(h2);
    }
    
    public static void main(String[] args){
        
        TStyle.getInstance().getPalette().palette2d().setPalette(TPalette2D.PaletteName.kBlueYellow);
        //String file = "/Users/gavalian/Work/DataSpace/decoded/clas_006595.evio.00625-00629_denoised.hipo";
        
        String file = "/Users/gavalian/Work/Software/project-11.0/distribution/coatjava/filter_output_1.h5";
        
        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks("DC::tdc");
        
        
        
        Event e = new Event();
        r.getEvent(e, 6);
        
        e.read(b);
        b[0].show();
        
        ChamberData data = new ChamberData();
        for(int i = 0; i < b[0].getRows(); i++){
            int s = b[0].getInt("sector", i);
            int l = b[0].getInt("layer", i);
            int c = b[0].getInt("component", i);
            int o = b[0].getInt("order", i);
            if(o==0||o==40|o==50) data.set(s, l, c);
            //data.set(s, l, c);
        }
        
        DriftChamber3 dc3 = new DriftChamber3();
        Leaf leaf = new Leaf(32101,10,"2s2b2f3b",1024);
        dc3.useDenoising = false;
        //for(int i = 0; i < 6; i++) dc3.segmentFinder(data, leaf, 2, i+1);
        dc3.segmentFinder(data, leaf);
        System.out.println(" ROWS = " + leaf.getRows());
        leaf.print();
        
        List<H2F> h2 = dc3.geth2(data);
        Graph     g2 = dc3.getGraph(data);
        g2.attr().setMarkerSize(4);
        //g2.attr().setMarkerColor(4);
        System.out.println("size = " + g2.getVectorX().getSize());
        TGCanvas c = new TGCanvas(500,500);
        //c.draw(h2.get(3),"F");
        c.view().region().set("bc=#000033");
        c.draw(g2,"P");
        c.view().region().axisLimitsX(-550, 550);
        c.view().region().axisLimitsY(-550, 550);
        
        e.write(leaf);
        
        TrackFinderNetwork net = new TrackFinderNetwork();
        net.init("etc/networks/clas12default.network", 2);
        
        net.process8(e);
        
        //e.scanShow();
        
        Leaf tracks = new Leaf(2,1,"i",4096);
        e.read(tracks,32000,1);
        
        tracks.print();
        /*for(int i = 0; i < dc3.patterns.size(); i++){
            System.out.println( " PATTERN " + i + "  " + dc3.average(dc3.patterns.get(i)));
        }*
        /*
        int iter = 100000; 
        long then = System.currentTimeMillis();
        for(int i = 0; i < iter; i++){
            dc3.segmentFinder(data, leaf);
        }
        long now = System.currentTimeMillis();
        System.out.printf("iteration %d, time %d , average = %.6f , frequency = %.2f Hz\n",
                iter,now-then, ((double) (now-then))/iter, ((double) (iter*1000)) / (now-then));
        */
        /*dc3.scan(data, 1, 1);
        dc3.sanitize(data);
        System.out.println(Arrays.toString(data.scan));
        System.out.println(Arrays.toString(data.type));
        */
    }
}
