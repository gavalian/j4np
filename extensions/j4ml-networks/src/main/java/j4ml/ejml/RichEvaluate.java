/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.ejml;

import j4np.utils.io.TextFileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import twig.data.H1F;
import twig.data.H2F;
import twig.graphics.TGCanvas;
import twig.math.F1D;

/**
 *
 * @author gavalian
 */
public class RichEvaluate {
    
    public static List<float[]> getInputs(String line, int limit){
        List<float[]> inputs = new ArrayList<>();
        String[] tokens = line.split(",");
        int counter = 0;
        
        int nhits = (tokens.length - 10)/3;
        for(int i = 0 ; i < nhits; i++){
            float[] data = new float[8];
            data[0] = Float.parseFloat(tokens[4]);
            data[1] = Float.parseFloat(tokens[5]);
            data[2] = Float.parseFloat(tokens[6]);
            data[3] = Float.parseFloat(tokens[7]);
            data[4] = Float.parseFloat(tokens[8]);
            data[5] = Float.parseFloat(tokens[9]);
            data[6] = Float.parseFloat(tokens[10+i*3]);
            data[7] = Float.parseFloat(tokens[10+i*3+1]);
            counter++;
            if(counter<limit)
                inputs.add(data);
        }
        return inputs;
    }
    
    public static List<float[]> getOutputs(String line){
        List<float[]> outputs = new ArrayList<>();
        String[] tokens = line.split(",");
        int nhits = (tokens.length - 10)/3;
        for(int i = 0 ; i < nhits; i++){
            float[] data = new float[1];
            data[0] = Float.parseFloat(tokens[10+i*3+2]);
            outputs.add(data);
        }
        return outputs;
    }
    
    public static List<float[]> getParticle(String line){
        List<float[]> outputs = new ArrayList<>();
        String[] tokens = line.split(",");
        float[] data = new float[3];
        data[0] = Float.parseFloat(tokens[1]);
        data[1] = Float.parseFloat(tokens[2]);
        data[2] = Float.parseFloat(tokens[3]);
        outputs.add(data);return outputs;
    }
    
    public static double average(List<float[]> data){
        double summ = 0.0;
        for(int i = 0; i < data.size(); i++) summ += data.get(i)[0];
        return summ/data.size();
    }
    
    public static double rms(List<float[]> data, double average){
        double summ = 0.0;
        for(int i = 0; i < data.size(); i++) 
            summ += (data.get(i)[0]-average)*(data.get(i)[0]-average);
        return summ/data.size();
    }
    
    public static int getBin(int pid){
        switch(pid){
            case 211: return 0;
            case 321: return 1;
            case 2212: return 2;
            default: return 0;
        }
    }
    
    public static void evaluate(String filename){
    
        EJMLModel model = new EJMLModel("ejml_model_2.network",EJMLModel.ModelType.TANH_LINEAR);
        TextFileReader r = new TextFileReader(filename);
        int counter = 0;
        H1F[] h = new H1F[]{
        new H1F("h0",120,-1.0,1.0),
         new H1F("h0",120,-1.0,1.0),
         new H1F("h0",120,-1.0,1.0)
        };
        
        H1F[] hrms = new H1F[]{
        new H1F("h0",120,0,.2),
         new H1F("h0",120,0,.2),
         new H1F("h0",120,0,.2)
        };
        
        h[0].attr().set("fc=42,lc=2,lw=2");
        h[1].attr().set("fc=45,lc=5,lw=2");
        h[2].attr().set("fc=43,lc=3,lw=2");
        
        h[0].attr().setTitleX("#eta_#pi-#eta_K");
        h[1].attr().setTitleX("#eta_K-#eta_K");
        h[2].attr().setTitleX("#eta_p-#eta_K");
        
        H2F[] h2 = new H2F[]{
        new H2F("h0",80,0.0,1.0,120,-1.0,1.0),
         new H2F("h0",80,0.0,1.0,120,-0.2,0.2),
         new H2F("h0",80,0.0,1.0,120,-1.0,1.0)
        };
        
        h2[0].attr().setTitleY("#eta_#pi-#eta_K");
        h2[1].attr().setTitleY("#eta_K-#eta_K");
        h2[2].attr().setTitleY("#eta_p-#eta_K");
        h2[0].attr().setTitleX("particle momentum [0-1]");
        h2[1].attr().setTitleX("particle momentum [0-1]");
        h2[2].attr().setTitleX("particle momentum [0-1]");
        
        H1F[] hk = new H1F[]{
            new H1F("h0",60,.0,9.0),
            new H1F("h0",60,.0,9.0),
            new H1F("h0",60,.0,9.0),
            new H1F("h0",60,.0,9.0)
        };
        
        H1F[] hpi = new H1F[]{
            new H1F("h0",60,.0,9.0),
            new H1F("h0",60,.0,9.0),
            new H1F("h0",60,.0,9.0),
            new H1F("h0",60,.0,9.0)
        };
        H1F[] hp = new H1F[]{
            new H1F("h0",60,.0,9.0),
            new H1F("h0",60,.0,9.0),
            new H1F("h0",60,.0,9.0),
            new H1F("h0",60,.0,9.0)
        };
        
        F1D f = new F1D("func","[p0]+[p1]*x+[p2]*x*x",0.05,0.85);
        f.setParameters(1.226190e-01,-2.410714e-01,1.488095e-01);
        F1D f2 = new F1D("func","[p0]+[p1]*x+[p2]*x*x",0.05,0.85);
        f2.setParameters(-1.226190e-01,2.410714e-01,-1.488095e-01);
        while(r.readNext()==true&&counter<50000){
            counter++;
            String   line = r.getString();
            String[] tokens = line.split(",");
            int pid = Integer.parseInt(tokens[0]);
            double mom = Double.parseDouble(tokens[4]);
            double theta = Double.parseDouble(tokens[2]);
            //if(theta<23||theta>25) continue;
            if(pid==321||pid==211||pid==2212){
            List<float[]>   inputs = RichEvaluate.getInputs(line,8);
            List<float[]>  outputs = RichEvaluate.getOutputs(line);
            //List<float[]>  pearticle = 
            //System.out.println("input size -> " + inputs.size());
            //for(float[] data : inputs) System.out.println(Arrays.toString(data));
            //for(float[] data : outputs) System.out.println(Arrays.toString(data));
            
            List<float[]> inferred = new ArrayList<>();
            for(int i = 0; i < inputs.size();i++){
                float[] result = new float[1];
                model.feedForwardTanhLinear(inputs.get(i), result);
                System.out.println(Arrays.toString(inputs.get(i)));
                System.out.println(Arrays.toString(result) + Arrays.toString(outputs.get(i)));
                inferred.add(result);
            }
            //for(float[] data : inferred) System.out.println(Arrays.toString(data));
            
            //System.out.println(" event # , pid = " + pid + " " 
            //        + RichEvaluate.average(outputs) + " " + RichEvaluate.average(inferred));
            
            
            int bin = RichEvaluate.getBin(pid);
            h[bin].fill(RichEvaluate.average(inferred)-RichEvaluate.average(outputs));
            //System.out.println();
            h2[bin].fill(mom,RichEvaluate.average(inferred)-RichEvaluate.average(outputs));
            double av_o = RichEvaluate.average(outputs);
            double av_i = RichEvaluate.average(inferred);
            double rms_o = RichEvaluate.rms(outputs, av_o);
            double rms_i = RichEvaluate.rms(inferred, av_i);
            hrms[bin].fill(rms_i*1000000);
            
            //System.out.printf("output = %8.5f, %8.5f, inferred = %8.5f %8.12f\n",
             //       av_o,rms_o,av_i,rms_i);
            
            double cut = 0.05;
            double ppart = mom*7+2;
            double fval = f.evaluate(mom);
            //System.out.printf("p = %f, fval = %f\n",ppart,fval);
            if(pid==321){
                hk[0].fill(ppart);
                if(Math.abs(av_i-av_o)<fval) hk[1].fill(ppart);
            }
            if(pid==211){
                hpi[0].fill(ppart);                
                if(Math.abs(av_i-av_o)<fval) hk[2].fill(ppart);
            }
            if(pid==2212){
                hp[0].fill(ppart);                
                if(Math.abs(av_i-av_o)<fval) hk[3].fill(ppart);
            }
            
            }
            
        }
        
        TGCanvas c = new TGCanvas(1400,1200);
        f.attr().set("lc=5,lw=3,ls=3");
        f2.attr().set("lc=5,lw=3,ls=3");
        c.view().divide(3, 3);
        
        for(int cc =0; cc < 3; cc++) c.cd(cc).draw(h[cc]);
        for(int cc =0; cc < 3; cc++) c.cd(cc+3).draw(h2[cc]).draw(f,"same").draw(f2,"same");
        //for(int cc =0; cc < 3; cc++) c.cd(cc+9).draw(hrms[cc]);
        H1F hkeff = H1F.divide(hk[1], hk[0]);
        H1F hkpi = H1F.divide(hk[2], hpi[0]);
        H1F hkp = H1F.divide(hk[3], hp[0]);
        
        hkeff.attr().set("mc=5,lc=5");
        hkpi.attr().set("mc=2,lc=2");
        hkp.attr().set("mc=3,lc=3");
        
        hkeff.attr().setTitleX("P [GeV]");
        hkeff.attr().setTitleY("K identification efficiency");
      
        hkpi.attr().setTitleX("P [GeV]");
        hkpi.attr().setTitleY("#pi identification efficiency");
        
        hkp.attr().setTitleX("P [GeV]");
        hkp.attr().setTitleY("p identification efficiency");
        
        c.cd(7).draw(hkeff,"EP");
        c.cd(6).draw(hkpi,"EP");
        c.cd(8).draw(hkp,"EP");
    }
    
    public static void main(String[] args){
        //RichEvaluate.evaluate("output_ev_miss.csv"); 
        RichEvaluate.evaluate("output_ev_fixed.csv"); 
        
    }
}
