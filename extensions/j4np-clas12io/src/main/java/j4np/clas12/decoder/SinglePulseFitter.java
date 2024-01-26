/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.decoder;

import j4np.clas12.decoder.PulseFitter;
import j4np.data.base.DataUtils;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Node;
import twig.data.GraphErrors;
import twig.data.H1F;
import twig.graphics.TGCanvas;



/**
 *
 * @author gavalian
 */
public class SinglePulseFitter extends PulseFitter {

    private int pedistalMinBin = 1;
    private int pedistalMaxBin = 5;
    
    @Override
    public void fit(PulseFitterParams params, PulseFitterConfig config, CompositeNode pulse, int offset, int length) {
        params.ADC = 0; params.time = 0.0; params.pedestal = 0;
        int p1 = 1; int p2 = 15;
        
        PulsePedestal ped = findPedestal(p1+1,p2+1, pulse, offset, length);
        
        //System.out.println(ped);
        int tcross = findCrossing(p2+1, ped.ped+config.TET, pulse,offset,length);
        
        this.findIntegral(params, config, ped.ped, tcross, pulse, offset, length);
        params.pedestal = ped.ped;
        
        this.findTime(params, config, ped.ped, tcross, pulse, offset, length);
        
        int        timeFine = DataUtils.getInteger(params.timeWord, 0,  5);
        int pulseTimeCourse = DataUtils.getInteger(params.timeWord, 6, 15);
        params.time = pulseTimeCourse*4.0 + timeFine*0.0625;
        
        if(params.ADC<=0){params.ADC=0;params.time=0.0;}
        //System.out.printf(" ADC before = %d, after = ",params.ADC);
        //params.ADC = params.ADC ;//+ params.pedestal*(config.NSA + config.NSB);        
        //System.out.println( params.ADC);
        /*
        ExtendedFADCFitter f = new ExtendedFADCFitter();
        short[] p = new short[length];
        for(int i = 0; i < length; i++) p[i] = (short) pulse.getInt(0, offset+i);
        f.fit(config.NSA, config.NSB, config.TET, 0, p);
        
        System.out.println("\nfit-new " + params);
        System.out.printf(" \t fit-results = %d %d %d\n",f.adc,f.ped,f.t0);*/
        //System.out.println(" tcross = " + tcross);
        //System.out.println(params);
        
        
    }

    
    
    protected PulsePedestal findPedestal(int start, int end, CompositeNode pulse, int offset, int length){
        int p1=1; int p2=15; double baseline = 0.0;
        double noise  = 0; int pedsum = 0;
        for(int bin = start; bin < end; bin++){
            int value = pulse.getInt(0, bin+offset);
            pedsum += value;
            noise  +=  value*value;
        }
        baseline = ((double) pedsum)/(end-start);
        
        return new PulsePedestal((int) baseline,baseline,Math.sqrt(noise / (p2 - p1) - baseline * baseline));
    }
    
    protected void findIntegral( PulseFitterParams params, PulseFitterConfig config, int ped, int tcross, CompositeNode pulse, int offset, int length){
        params.ADC = 0; params.MAX = 0; params.position = 0;
        for (int bin=Math.max(0,tcross-config.NSB); bin<Math.min(length,tcross+config.NSA+1); bin++) { // sum should be up to tcross+nsa (without +1), this was added to match the old fit method
            int bv = pulse.getInt(0, bin+offset);
            params.ADC += bv-ped;
            //System.out.printf(" bin = %3d, %6d max = %d\n",bin,bv, params.MAX);
            if(bin>=tcross && bv>params.MAX) {
                params.MAX = bv; params.position = bin;
            }
        }
    }
    
    protected void findTime( PulseFitterParams params, PulseFitterConfig config, int ped, int tcross, CompositeNode pulse, int offset, int length){
        //params.ADC = 0; params.MAX = 0; params.position = 0;
        double halfMax = (params.MAX+params.pedestal)/2;
        int s0 = -1;
        int s1 = -1;
        for (int bin=tcross-1; bin<Math.min(length-1,params.position+1); bin++) {
            int p = pulse.getInt(0,bin+offset);
            int p1 = pulse.getInt(0,bin+offset+1);
            if (p<=halfMax && p1>halfMax) { s0 = bin; break; }
        }
        
        for (int bin=params.position; bin<Math.min(length-1,tcross+config.NSA); bin++) {
            int p = pulse.getInt(0,bin+offset);
            int p1 = pulse.getInt(0,bin+offset+1);
            if (p>halfMax && p1<=halfMax) { s1 = bin; break;}
        }
        int tcourse = 0; int tfine = 0; int t0= 0; 
        params.time = 0.0;
        if(s0>-1) {
                int a0 = pulse.getInt(0, s0 + offset);
                int a1 = pulse.getInt(0, s0 + offset + 1);
                // set course time to be the sample before the 50% crossing
                tcourse = s0;
                // set the fine time from interpolation between the two samples before and after the 50% crossing (6 bits resolution)
                tfine   = ((int) ((halfMax - a0)/(a1-a0) * 64));
                t0      = (tcourse << 6) + tfine;
                params.timeWord = t0;
        }
        
    }
    
    protected int findCrossing(int start, int level, CompositeNode pulse, int offset, int length){
        for(int i = start; i < length; i++){
            //System.out.printf("%d %d %d\n",i,pulse.getInt(0, i));
            if(pulse.getInt(0, offset+i)>level) return i;
        }
        return -1;
    }
    
    public record PulsePedestal (int ped, double baseline, double rms) {}
    
    
    
    public static void main(String[] args){
        String data = "0.0327,0.0330,0.0335,0.0335,0.0333,0.0335,0.0333,0.0338,0.0338,0.0330,0.0335,0.0335,0.0335,0.0338,0.0338,0.0333,0.0327,0.0335,0.0335,0.0335,0.0327,0.0333,0.0338,0.1207" +
",0.9992,0.9744,0.3375,0.1025,0.1317,0.0709,0.0456,0.0750,0.0393,0.0264,0.0583,0.0294,0.0283,0.0528,0.0264,0.0330,0.0613,0.0456,0.0412,0.0393,0.0316,0.0415,0.0404,0.000,0.00";
        
        String[] tokens = data.split(",");
        
        CompositeNode n = new CompositeNode(1,1,"s",128);
        n.setRows(tokens.length);
        double[] x = new double[tokens.length];
        double[] y = new double[tokens.length];
        short[]  p = new short[tokens.length];
        
        for(int i = 0; i < tokens.length; i++){
            n.putShort(0, i, (short) (1000*Float.parseFloat(tokens[i])));
            x[i] = i+0.5; y[i] = 1000*Float.parseFloat(tokens[i]);            
            p[i] = (short) (1000*Float.parseFloat(tokens[i]));
        }
        
        SinglePulseFitter fitter = new SinglePulseFitter();
        PulseFitterConfig config = new PulseFitterConfig();
        PulseFitterParams params = new PulseFitterParams();
        
        config.pedestal = 0;
        config.TET = 20;
        config.NSA = 5;
        config.NSB = 30;
        
        int iter = 1;
        long then = System.currentTimeMillis();
        for(int i = 0; i < iter; i++){
            fitter.fit(params, config, n, 0, n.getRows());
        }
        System.out.println(params);
        long now = System.currentTimeMillis();
        System.out.printf("iterations %d, time = %d msec\n",iter, now -then);
        TGCanvas c = new TGCanvas();
        GraphErrors g = new GraphErrors("g",x,y);
        H1F h = new H1F("h", 0.5,48.5 , y);
        c.view().divide(1, 2);
        c.cd(0).draw(g,"PL");
        c.cd(1).draw(h);
        
        
        ExtendedFADCFitter f = new ExtendedFADCFitter();
        f.fit(config.NSA, config.NSB, config.TET, 0, p);
        System.out.printf("%d %d %d",f.adc,f.t0,f.ped);
    }
}
