/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.display;

import j4ml.temp.DataStudio;
import j4np.utils.dsl.DSLCommand;
import j4np.utils.dsl.DSLSystem;
import j4np.utils.io.DataArrayUtils;
import j4np.utils.io.LSVMFileReader;
import j4np.utils.io.TextFileReader;
import java.util.ArrayList;
import java.util.List;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.data.H1F;
import org.jlab.jnp.groot.graphics.TDataCanvas;

/**
 *
 * @author gavalian
 */
@DSLSystem (system="ml", info="tools for displaying Machine Lerning Data")
public class MLDisplayModule {
    /*
    @DSLCommand(
            command="read",
            info="read a data file into memory",
            defaults={"file.lsvm","!","!"},
            descriptions={"","format (default csv) (lsvm,csv,tab)","max number of rows to read"}
    )
    public void read(String when){ 
        
    }*/
    
    @DSLCommand(
            command="showdc",
            info="read dc data from LSVM file and display",
            defaults={"file.lsvm","1", "1","1","2","PL"},
            descriptions={"filename in LSVM format",
                "the vector length in LSVM", "skip rows","number of rows to read",
            "high light color index", "draing options"}
    )
    public void showdc(String file,int nLength, int skip, int length, int highLightColor, String options){
        List<String> data = TextFileReader.readFile(file, skip, length);
        LSVMFileReader lsvm = new LSVMFileReader();
        List<GraphErrors> graphs = new ArrayList<>();
        
        for(int i = 0; i < data.size(); i++){
            GraphErrors gr = new GraphErrors();
            
            double[] vec = lsvm.toDouble(data.get(i), nLength);
            String[] tokens = data.get(i).trim().split("\\s+");
            int label = Integer.parseInt(tokens[0]);
            for(int j = 0; j < vec.length; j++) gr.addPoint(vec[j], j+1, 0.0, 0.0);
            System.out.printf("%3d : (%2d) : %s\n",i+1,label,DataArrayUtils.doubleToString(vec, " "));
            for(int j = 0; j < vec.length; j++) System.out.printf("%.2f:%d,",vec[j]*112,j+1);
            System.out.println();
            gr.setMarkerSize(8);
            if(label>0){
                gr.setMarkerColor(highLightColor);
                gr.setLineColor(highLightColor);
            }
            graphs.add(gr);
        }
        
        TDataCanvas c = DataStudio.getInstance().getDefaultCanvas();
        
        for(int i = 0; i < graphs.size(); i++){
            
            if(i==0){
                graphs.get(i).setTitle(String.format("combinations %d ", length));
                graphs.get(i).setTitleX(String.format("combinations %d ", length));
                c.draw(graphs.get(i),options);
            } else {
                c.draw(graphs.get(i),"same"+options);
            }
        }
        
        for(int i = 0; i < graphs.size(); i++){
            if(graphs.get(i).getLineColor()==highLightColor){
                c.draw(graphs.get(i),"same" + options);
            }
        }
        
        c.setAxisLimits(0.0, 1.0, 0.5, nLength+0.5);
        c.repaint();
    }
    
    
    @DSLCommand(
            command="regression",
            info="show results of regression evaluation",
            defaults={"file.txt","1", "1"},
            descriptions={"filename with space separated format",
                "number of output nodes", "starting column",}
    )    
    public void regression(String file,int nparams, int skip){
        List<RegressionGraphs>  graphs = new ArrayList<>();
        for(int i = 0; i < nparams; i++) graphs.add(new RegressionGraphs());
        
        TextFileReader reader = new TextFileReader();
        reader.open(file);
        int counter = 0;
        while(reader.readNext()==true){
            String[] tokens = reader.getString().split("\\s+");
            for(int i = 0 ; i < nparams; i++){
                double infer = Double.parseDouble(tokens[i*2+skip]);
                double real = Double.parseDouble(tokens[i*2+skip+1]);
                graphs.get(i).addEvaluation(real, infer);
            }
            counter++;
        }
        
        System.out.println("[regression] results loaded = " + counter);
        TDataCanvas c = DataStudio.getInstance().getDefaultCanvas();
        c.divide(3, nparams);
        
        for(int p = 0; p < nparams; p++){
            H1F h = graphs.get(p).getResolution();
            c.cd(p*3).draw(graphs.get(p).getH1().get(0));
            c.cd(p*3+1).draw(graphs.get(p).getH1().get(1));
            c.cd(p*3+2).draw(graphs.get(p).getH1().get(2));
            //c.cd(p*3+1).draw(graphs.get(p).getH2().get(0));
            //c.getDataCanvas().getRegion(p*3+1).getGraphicsAxis().setAxisLimits(0.0, 1.0, -1.0, 1.0);
            //c.cd(p*3+2).draw(graphs.get(p).getH2().get(1));
            //c.getDataCanvas().getRegion(p*3+2).getGraphicsAxis().setAxisLimits(0.0, 1.0, 0.0, 1.0);
        }
    }
    
}
