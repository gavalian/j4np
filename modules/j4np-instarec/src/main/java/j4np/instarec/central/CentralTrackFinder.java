/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.central;

import j4np.hipo5.data.Bank;
import j4np.hipo5.io.HipoReader;
import j4np.instarec.utils.EJMLModel;
import j4np.utils.io.TextFileReader;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class CentralTrackFinder {
    public EJMLModel model = null;
    public CentralTrackFinder(){
        List<String> lines = TextFileReader.readFile("centralclassifier24.network");
        model = EJMLModel.create(lines);
        System.out.println(model.summary());
    }
    
    public void process(Bank c, Bank seeds){
        CentralTracks tracks = new CentralTracks();
        System.out.println("--- new event");
        for(int i = 0; i < seeds.getRows(); i++){
            int[] index = seeds.getIntArray(12, "cl1", i);
            System.out.println(Arrays.toString(index));
            tracks.fill(index);
        }

        tracks.show();
        
        int[] subTrack = new int[6];
        for(int j = 0; j < tracks.node().getRows(); j++){
            tracks.getSubTrack(j, subTrack, 0);
            
            if(tracks.isComplete(subTrack)==true){
                int status = tracks.status(subTrack, c);
                System.out.printf(" %3d : %6s %s %d\n",j,tracks.isComplete(subTrack),Arrays.toString(subTrack),status);
                float[] features = tracks.getFeatures(subTrack, c);
                float[] result = new float[2];

                model.feedForwardSoftmax(features, result);
                System.out.println("\t"  + Arrays.toString(result) + " : " + Arrays.toString(features));
            }
        }
    }
    
    public static void main(String[] args){
        String   file = "/Users/gavalian/Work/Software/project-11.0/study/central/AISample_3.hipo";
        HipoReader  r = new HipoReader(file);
        
        Bank[] b = r.getBanks("cvtml::clusters","cvtml::seeds");
        CentralTrackFinder finder = new CentralTrackFinder();
        
        while(r.nextEvent(b)){
            finder.process(b[0], b[1]);
        }
    }
}
