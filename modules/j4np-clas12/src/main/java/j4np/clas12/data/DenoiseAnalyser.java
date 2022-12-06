/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.data;

import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import twig.data.DataSetSerializer;
import twig.data.H1F;
import twig.data.H2F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class DenoiseAnalyser {
    
    public String archive = "";
    TGCanvas c = new TGCanvas(500,900);
    HipoReader r = new HipoReader();
    Event e = new Event();
    
    public DenoiseAnalyser(String file){
        //r.setTags(2);
        r.open(file);
        c.setTitle(file);        
    }
    /*
    public List<H2F> load(int which){
        
        String dirname = "sample/"+which;
        H2F h1 = (H2F) DataSetSerializer.load(archive, dirname+"/input");
        H2F h2 = (H2F) DataSetSerializer.load(archive, dirname+"/desired");
        H2F h3 = (H2F) DataSetSerializer.load(archive, dirname+"/output");
        return Arrays.asList(h1,h2,h3);
    }*/
    
    public List<float[]> loadNext(){        
        if(r.hasNext()){
            r.nextEvent(e);
            Node n1 = e.read(340, 1);
            Node n2 = e.read(340, 2);
            Node n3 = e.read(340, 4);
            return Arrays.asList(n1.getFloat(),n2.getFloat(),n3.getFloat());
        }
        return new ArrayList<float[]>();
    }
    
    public int  redo(float[] out, float[] input, double threshold){
        int counter = 0;
        for(int x = 0; x < out.length; x++){
            //double content = out.getBinContent(x, y);
            //double    real = input.getBinContent(x, y);
            if(input[x]>threshold) counter++;
            if(out[x]<threshold){
                out[x] = 0.0f;
            } else {
                if(input[x]>0.0){
                    out[x] = 1.0f;//.setBinContent(x, y, 1.0);
                } else {
                    out[x] = 0.0f;
                    //out.setBinContent(x, y, 0.0);
                }                
            }
        }
        return counter;
    }
    
    public void  redo(H2F out, H2F input, double threshold){
        for(int x = 0; x < out.getXAxis().getNBins(); x++){
            for(int y = 0; y < out.getYAxis().getNBins(); y++){
                double content = out.getBinContent(x, y);
                double    real = input.getBinContent(x, y);
                if(content<threshold){
                    out.setBinContent(x, y, 0.0);
                } else {
                    if(real>0){
                        out.setBinContent(x, y, 1.0);
                    } else {
                        out.setBinContent(x, y, 0.0);
                    }
                }
            }
        }
    }
    
    
    public int[] countMatch(float[] desired, float[] out){
        int[] counter = new int[]{0,0,0,0}; 
        double level = 0.1;
        for(int x = 0; x < out.length; x++){ 
            if(desired[x]>level){
                counter[3]++;
                if(out[x]>level) counter[0]++; else counter[1]++;
            } else {
                if(out[x]>level) counter[2]++;
            }
            
        }
        return counter;
    }
    
    public int[] countMatch(H2F desired, H2F out){
        int[] counter = new int[]{0,0,0}; 
        
        for(int x = 0; x < out.getXAxis().getNBins(); x++){
            for(int y = 0; y < out.getYAxis().getNBins(); y++){
                if(desired.getBinContent(x, y)>0){
                    if(out.getBinContent(x, y)>0.1){
                        counter[0]++;
                    } else counter[1]++;
                } else {
                    if(out.getBinContent(x, y)>0.1) counter[2]++;
                }
                
            }
        }
        return counter;
    }
    
    public void analyse(int min, int max, double threshold){
        
        H1F hm = new H1F("hm",42,-0.5,41.5);
        H1F hf = new H1F("hf",42,-0.5,41.5);
        H1F hn = new H1F("hn",42,-0.5,41.5);
        
        H1F hnoise = new H1F("hn",120,-0.5,119.5);
        H1F  hhits = new H1F("hn",120,-0.5,119.5);
        
        
        
        H2F h2f = new H2F("h2f",10,0.5,10.5,50,-0.2,1.2);
                
        hm.attr().set("lc=1,fc=144");
        hf.attr().set("lc=4,fc=174");
        hn.attr().set("lc=1,fc=145");
        hhits.attr().set("lc=6,fc=176");
        
        hf.attr().setTitleX(String.format("threshold = %.8f", threshold));
        for(int r = min; r< max; r++){
            //List<H2F> hlist = this.load(r);
            List<float[]> hlist = this.loadNext();      
            if(hlist.size()>0){
                int totalhits = redo(hlist.get(2),hlist.get(1),threshold);
                int[] match = this.countMatch(hlist.get(0),hlist.get(2));
                if(match[0]>6){
                    //System.out.printf("%.9f order = %5d , match = %5d , miss = %5d, noise = %5d , total = %5d\n",
                    //        threshold,r,match[0],match[1], match[2], totalhits);
                
               h2f.fill(1, ((double) match[0])/match[3]);
               h2f.fill(2, ((double) match[2])/(totalhits-match[3]));
               
               hm.fill(match[0]);
               hf.fill(match[1]);
               hn.fill(match[2]);
               hhits.fill( totalhits - match[0] - match[1]);
                }
            }
        }
        
        //TGCanvas c = new TGCanvas();
        /*c.draw(hf,"F")
                .draw(hm,"Fsame")
                .draw(hn,"Fsame")
                .draw(hhits,"Fsame");
        */
        for(int i = 1; i < 3; i++){
            H1F h = h2f.sliceX(i-1);
            h.attr().setTitleX(String.format("threshold %.8f", threshold));
            h.attr().setLineColor(1);
            h.attr().setFillColor(i+3+120);
            c.draw(h,"Fsame");
        }
    }
    
    public void run(int iter, List<Double> thr){
        
        c.view().divide(1, thr.size());
        c.view().top(5).bottom(45);
        
        for(int i = 0; i < thr.size(); i++){
            c.cd(i);
            r.rewind();
            this.analyse(1,iter,thr.get(i));
        }
    }
        
    
    public static void main(String[] args){
        
        //dn.archive = "denoise_eval_25_epoch.twig";
        //dn.archive = "denoise_eval_relu-250.twig";
        //dn.archive = "evaluation_model.twig";
        String file = "dn_evaluate_epoch_12.h5";
        int iter = 5000;
        
        
        String[] files = {
            "dn_evaluate_epoch_1.h5",
            //"dn_evaluate_epoch_2.h5",
            "dn_evaluate_epoch_8.h5",
            "dn_evaluate_epoch_13.h5",
            "dn_evaluate_epoch_16.h5"
           // "dnbce_evaluate_epoch_10.h5",
            //"dnbce_evaluate_epoch_15.h5",
            //"dnbce_evaluate_epoch_20.h5",
            //"dnbce_evaluate_epoch_25.h5"
        };
        for(String f : files){
            DenoiseAnalyser dn = new DenoiseAnalyser(f);
            dn.run(iter, Arrays.asList(0.05,0.01,0.005,0.001, 0.0005));//,0.2,0.05,0.01,0.005,0.001));
        }
        //dn.run(iter, Arrays.asList(0.0001, 0.00005, 0.00001,0.000005));

        //dn.analyse(1,iter,0.4);
       /* dn.analyse(1,iter,0.2);
        dn.analyse(1,iter,0.1);
        dn.analyse(1,iter,0.05);
        dn.analyse(1,iter,0.01);
        dn.analyse(1,iter,0.005);
        dn.analyse(1,iter,0.001);
        dn.analyse(1,iter,0.0005);
        dn.analyse(1,iter,0.0001);*/
    }
}
