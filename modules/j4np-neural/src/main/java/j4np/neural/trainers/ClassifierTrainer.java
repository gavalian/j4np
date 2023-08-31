/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.trainers;

import j4ml.data.DataList;
import j4ml.deepnetts.DeepNettsClassifier;
import j4np.utils.base.ArchiveUtils;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class ClassifierTrainer {
    
    public ClassifierTrainer(){
        
    }
    
    public void init(){
        
    }
    
    public void train(DataList list, String archive, int run, int epochs){        
        DeepNettsClassifier classifier = new DeepNettsClassifier();
        classifier.init(new int[]{6,12,24,24,12,3});
        list.shuffle();        
        classifier.train(list, epochs);
        List<String>  networkContent = classifier.getNetworkStream();
        String archiveFile = String.format("network/%d/%s/trackClassifier.network",run,"default");
        ArchiveUtils.writeFile(archive, archiveFile, networkContent); 
    }
}

