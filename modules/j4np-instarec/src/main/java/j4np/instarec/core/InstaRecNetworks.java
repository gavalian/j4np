/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

import j4np.instarec.utils.EJMLLoader;
import j4np.instarec.utils.EJMLModel;
import j4np.instarec.utils.NeuralModel;
import j4np.utils.asciitable.Table;

/**
 *
 * @author gavalian
 */
public class InstaRecNetworks {
    
    EJMLModel classifier = null;
    EJMLModel fixer = null;
    EJMLModel classifier6 = null;
    EJMLModel fixer6 = null;
    
    NeuralModel regression[][] = new NeuralModel[2][6];
    
    int       runnumber = -1;
        
    NeuralModel  classifierModel = null;
    NeuralModel classifierModel6 = null;
    
    public InstaRecNetworks(){
        
    }
    
    public InstaRecNetworks(String networkFile, int run){
        init(networkFile,run);
    }
    
    public final void init(String networkFile, int run){
        this.init(networkFile, run,"default");
    }
    
    public final void initJson(String networkFile, int run, String variation){
        classifierModel = NeuralModel.archiveFile(networkFile, "trackclassifier12.json", run, variation);
    }
    
    public NeuralModel getClassifierModel(){
        return this.classifierModel;
    }
    
    
    public final void init(String networkFile, int run, String variation){
        
        runnumber = EJMLLoader.getRun( networkFile, run);
        
        /*try {
            classifier = EJMLLoader.load(networkFile, "trackclassifier12.network", run , "default");
        } catch (Exception e){
            System.out.println("error loading classifier"); classifier = null;
        }
       try { 
        fixer = EJMLLoader.load(networkFile, "trackfixer12.network", run , "default");
       } catch (Exception e){
           System.out.println("error loading fixer"); fixer = null;
       }*/
       
       try {
            classifierModel6 = NeuralModel.archiveFile(networkFile, "trackclassifier6.json", run , "default");
        } catch (Exception e){
            System.out.println("error loading classifier"); classifierModel6 = null;
        }
       /*
       try { 
        fixer6 = EJMLLoader.load(networkFile, "trackfixer6.network", run , "default");
       } catch (Exception e){
           System.out.println("error loading fixer"); fixer6 = null;
       }*/
       
       String[] names = new String[]{"n","p"};
       for(int c = 0; c < 2; c++){
           for(int s = 1; s <= 6; s++){
               String file = String.format("%d/%s/trackregression6.json",s,names[c]);
               try {
                   this.regression[c][s-1] = NeuralModel.archiveFile(networkFile, file, run , "default");
               } catch (Exception ex) {
                   System.out.println("InstaRec:: unsuccessful loading network " + file);
                   this.regression[c][s-1] = null;
               }
           }
       }
    }
    public NeuralModel getClassifier6(){return classifierModel6;}
    public NeuralModel[][] getRegression(){return this.regression;};
    public EJMLModel getClassifier(){ return classifier;}
    public EJMLModel      getFixer(){ return fixer;}
    
    public void show(){
        String[]  header = new String[]{"network","architecture", "run", "status"};
        String[][]  data = new String[16][4];
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
        
        
        data[2][0] = "classifier6";
        data[2][1] = "n/a";
        data[2][2] = "" + runnumber;
        data[2][3] = "disabled";
        if(classifierModel6!=null){ data[2][3] = "ok"; data[2][1] = classifierModel6.info();}
        data[3][0] = "fixer6";
        data[3][1] = "n/a";
        data[3][2] = "" + runnumber;
        data[3][3] = "disabled";
        if(fixer6!=null){ data[3][3] = "ok"; data[3][1] = fixer6.summary();} 
        
        int counter = 0;
        String[] names = new String[]{"n","p"};
        for(int c = 0; c < 2; c++){
            for(int s = 0; s < 6; s++){
                data[counter+4][0] = String.format("%s : sector %d", names[c],s+1);
                data[counter+4][1] = "n/a";
                data[counter+4][2] = "" + runnumber;
                data[counter+4][3] = "disables";
                if(this.regression[c][s]!=null) {data[counter+4][3] = "ok"; data[counter+4][1] = this.regression[c][s].info();}
                counter++;
            }
        }
        
        String table = Table.getTable(header,data, new Table.ColumnConstrain(0,24), 
                new Table.ColumnConstrain(1,24), new Table.ColumnConstrain(2,12), new Table.ColumnConstrain(3,12));
        
        
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
    
    public static void main(String[] args){
        InstaRecNetworks net = new InstaRecNetworks();
        net.init("etc/networks/clas12default.network", 12);
        net.show();
    }
}
