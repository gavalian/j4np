/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.network;

import j4np.utils.io.DataArrayUtils;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DataTrainer {
    
    public int run = 0;
    public String variation = "default";
    public String archive = "clas12temp.network";
    
    public void trainClassifier(String file, int max){
        
        List<float[]> listPos = new ArrayList<>();
        List<float[]> listNeg = new ArrayList<>();
        List<float[]> listFalse = new ArrayList<>();
        
        for(int i =  1; i <= 20; i++) listNeg.addAll(DataExtractor.load(file, i, max, false));
        for(int i = 21; i <= 40; i++) listPos.addAll(DataExtractor.load(file, i, max, false));
        for(int i =  1; i <= 40; i++) listFalse.addAll(DataExtractor.loadFalse(file, i, max));
        
        System.out.printf("List Size : negative = %9d, positove = %9d, false = %9d\n",listNeg.size(), listPos.size(), listFalse.size());
        TextFileWriter w = new TextFileWriter("classifier.csv");
        for(int i = 0; i < listNeg.size(); i++){ w.writeString(DataArrayUtils.floatToString(listNeg.get(i), ",") + DataArrayUtils.floatToString(new float[]{0.0f,1.0f,0.0f}, ","));}
        for(int i = 0; i < listPos.size(); i++){ w.writeString(DataArrayUtils.floatToString(listPos.get(i), ",") + DataArrayUtils.floatToString(new float[]{0.0f,0.0f,1.0f}, ","));}
        for(int i = 0; i < listFalse.size(); i++){ w.writeString(DataArrayUtils.floatToString(listFalse.get(i), ",") + DataArrayUtils.floatToString(new float[]{1.0f,0.0f,0.0f}, ","));}
        w.close();
    }
    
    public static void main(String[] args){
        DataTrainer t = new DataTrainer();
        t.trainClassifier("ml_1.h5", 45000);
    }
}
