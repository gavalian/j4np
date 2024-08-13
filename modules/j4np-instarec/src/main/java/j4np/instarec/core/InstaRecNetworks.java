/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

import j4np.instarec.utils.EJMLLoader;
import j4np.instarec.utils.EJMLModel;
import j4np.utils.asciitable.Table;

/**
 *
 * @author gavalian
 */
public class InstaRecNetworks {
    
    EJMLModel classifier = null;
    EJMLModel fixer = null;
    int       runnumber = -1;
    
    public InstaRecNetworks(){
        
    }
    
    public InstaRecNetworks(String networkFile, int run){
        init(networkFile,run);
    }
    
    public final void init(String networkFile, int run){
        this.init(networkFile, run,"default");
    }
    
    public final void init(String networkFile, int run, String variation){
        
        runnumber = EJMLLoader.getRun( networkFile, run);
        
        try {
            classifier = EJMLLoader.load(networkFile, "trackclassifier12.network", run , "default");
        } catch (Exception e){
            System.out.println("error loading classifier"); classifier = null;
        }
       try { 
        fixer = EJMLLoader.load(networkFile, "trackfixer12.network", run , "default");
       } catch (Exception e){
           System.out.println("error loading fixer"); fixer = null;
       }
    }
    
    public EJMLModel getClassifier(){ return classifier;}
    public EJMLModel      getFixer(){ return fixer;}
    
    public void show(){
        String[]  header = new String[]{"network","architecture", "run", "status"};
        String[][]  data = new String[2][4];
        data[0][0] = "classifier";
        data[0][1] = "n/a";
        data[0][2] = "" + runnumber;
        data[0][3] = "disabled";
        if(classifier!=null){ data[0][3] = "ok"; data[0][1] = classifier.summary();}
        data[1][0] = "fixer";
        data[1][1] = "n/a";
        data[1][2] = "" + runnumber;
        data[1][3] = "disabled";
        if(fixer!=null){ data[1][3] = "ok"; data[1][1] = fixer.summary();}                
        String table = Table.getTable(header,data, new Table.ColumnConstrain(0,24), new Table.ColumnConstrain(2,12), new Table.ColumnConstrain(3,12));
        
        System.out.println(table);
    } 
    
    public float[] analyze(float[] data, int which){
        float[] input  = new float[12];
        float[] fixed  = new float[12];
        float[] output = new float[3];
        for(int i = 0; i < data.length; i++){ input[i]=data[i];}
        input[2*which] = 0.0f; input[2*which+1] = 0.0f;
        this.fixer.feedForwardReLULinear(input, fixed);
        input[2*which] = fixed[2*which]; input[2*which+1] = fixed[2*which+1];
        this.classifier.feedForwardSoftmax(input, output);
        return output;
    }
    
    public float[] analyze(String data, int which){
        String[] tokens = data.split(",");
        if(tokens.length!=12) { System.out.println("error::::"); return null;} 
        float[] dataFloat = new float[12];
        for(int i = 0; i < dataFloat.length; i++) dataFloat[i] = Float.parseFloat(tokens[i]);
        return this.analyze(dataFloat, which);
    }
}
