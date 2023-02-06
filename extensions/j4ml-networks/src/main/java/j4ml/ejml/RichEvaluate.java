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

/**
 *
 * @author gavalian
 */
public class RichEvaluate {
    
    public static List<float[]> getInputs(String line, int limit){
        List<float[]> inputs = new ArrayList<>();
        String[] tokens = line.split(",");
        int counter = 0;
        
        int nhits = (tokens.length - 7)/3;
        for(int i = 0 ; i < nhits; i++){
            float[] data = new float[8];
            data[0] = Float.parseFloat(tokens[1]);
            data[1] = Float.parseFloat(tokens[2]);
            data[2] = Float.parseFloat(tokens[3]);
            data[3] = Float.parseFloat(tokens[4]);
            data[4] = Float.parseFloat(tokens[5]);
            data[5] = Float.parseFloat(tokens[6]);
            data[6] = Float.parseFloat(tokens[7+i*3]);
            data[7] = Float.parseFloat(tokens[7+i*3+1]);
            counter++;
            if(counter<limit)
                inputs.add(data);
        }
        return inputs;
    }
    
    public static List<float[]> getOutputs(String line){
        List<float[]> outputs = new ArrayList<>();
        String[] tokens = line.split(",");
        int nhits = (tokens.length - 7)/3;
        for(int i = 0 ; i < nhits; i++){
            float[] data = new float[1];
            data[0] = Float.parseFloat(tokens[7+i*3+2]);
            outputs.add(data);
        }
        return outputs;
    }
    
    public static double average(List<float[]> data){
        double summ = 0.0;
        for(int i = 0; i < data.size(); i++) summ += data.get(i)[0];
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
    
        EJMLModel model = new EJMLModel("ejml_rich.network",EJMLModel.ModelType.TANH_LINEAR);
        TextFileReader r = new TextFileReader(filename);
        int counter = 0;
        H1F[] h = new H1F[]{
        new H1F("h0",120,-1.0,1.0),
         new H1F("h0",120,-1.0,1.0),
         new H1F("h0",120,-1.0,1.0)
        };
        
        H2F[] h2 = new H2F[]{
        new H2F("h0",80,0.0,1.0,120,-1.0,1.0),
         new H2F("h0",80,0.0,1.0,120,-1.0,1.0),
         new H2F("h0",80,0.0,1.0,120,-1.0,1.0)
        };
        
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
        while(r.readNext()==true&&counter<50000){
            counter++;
            String   line = r.getString();
            String[] tokens = line.split(",");
            int pid = Integer.parseInt(tokens[0]);
            double mom = Double.parseDouble(tokens[1]);
            
            if(pid==321||pid==211||pid==2212){
            List<float[]>   inputs = RichEvaluate.getInputs(line,8);
            List<float[]>  outputs = RichEvaluate.getOutputs(line);
            System.out.println("input size -> " + inputs.size());
            //for(float[] data : inputs) System.out.println(Arrays.toString(data));
            //for(float[] data : outputs) System.out.println(Arrays.toString(data));
            
            List<float[]> inferred = new ArrayList<>();
            for(int i = 0; i < inputs.size();i++){
                float[] result = new float[1];
                model.feedForwardTanhLinear(inputs.get(i), result);
                inferred.add(result);
            }
            //for(float[] data : inferred) System.out.println(Arrays.toString(data));
            
            //System.out.println(" event # , pid = " + pid + " " 
            //        + RichEvaluate.average(outputs) + " " + RichEvaluate.average(inferred));
            
            
            int bin = RichEvaluate.getBin(pid);
            h[bin].fill(RichEvaluate.average(inferred)-RichEvaluate.average(outputs));
            h2[bin].fill(mom,RichEvaluate.average(inferred)-RichEvaluate.average(outputs));
            double av_o = RichEvaluate.average(outputs);
            double av_i = RichEvaluate.average(inferred);
            
            double cut = 0.009*1.5;
            if(pid==321){
                hk[0].fill(mom*8+1);                
                if(Math.abs(av_i-av_o)<cut) hk[1].fill(mom*8+1);
            }
            if(pid==211){
                hpi[0].fill(mom*8+1);                
                if(Math.abs(av_i-av_o)<cut) hk[2].fill(mom*8+1);
            }
            if(pid==2212){
                hp[0].fill(mom*8+1);                
                if(Math.abs(av_i-av_o)<cut) hk[3].fill(mom*8+1);
            }
            
            }
            
        }
        
        TGCanvas c = new TGCanvas(900,900);
        c.view().divide(3, 3);
        for(int cc =0; cc < 3; cc++) c.cd(cc).draw(h[cc]);
        for(int cc =0; cc < 3; cc++) c.cd(cc+3).draw(h2[cc]);
        
        H1F hkeff = H1F.divide(hk[1], hk[0]);
        H1F hkpi = H1F.divide(hk[2], hpi[0]);
        H1F hkp = H1F.divide(hk[3], hp[0]);
        c.cd(6).draw(hkeff,"EP");
        c.cd(7).draw(hkpi,"EP");
        c.cd(8).draw(hkp,"EP");
    }
    
    public static void main(String[] args){
        RichEvaluate.evaluate("output_ev_miss.csv");
        
    }
}
