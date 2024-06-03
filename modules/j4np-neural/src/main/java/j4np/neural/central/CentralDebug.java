/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.central;

import j4ml.ejml.EJMLModelRegression;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.neural.central.DataLoader.DataEventSvt;
import j4np.neural.central.DataLoader.DataSegment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 * @author gavalian
 */
public class CentralDebug {
    
    public static class SegmentCombo{
        public DataSegment seg1 = null;
        public DataSegment seg2 = null;
        public SegmentCombo(DataSegment s1, DataSegment s2){
            seg1 = s1; seg2 = s2;
        }
    }
    
    public static void main(String[] args){
        HipoReader r = new HipoReader("cvt_output.h5");
        Event evt = new Event();
        
        r.getEvent(evt, 245000);
        
        evt.scanShow();
        
        CompositeNode node = new CompositeNode(17,1,"2s4f4f",2048);
        evt.read(node, 17, 1);
        DataEventSvt data = new DataLoader.DataEventSvt(node);
        data.analyze();
        
        List<DataSegment>  segments = data.getSegments(new int[]{0,1,2});
        List<DataSegment> segments2 = data.getSegments(new int[]{1,2,3});
        
        System.out.println(" segments 1");
        for(DataSegment s : segments){
            System.out.println(s);
        }
        
        System.out.println(" segments 2");
        for(DataSegment s : segments2){
            System.out.println(s);
        }
    
        List<SegmentCombo> comboRaw = new ArrayList<>();
        List<SegmentCombo> comboRes = new ArrayList<>();
        for(int i = 0; i < segments.size(); i++){
            for(int j = 0; j < segments2.size(); j++){
                int[] index1 = segments.get(i).index;
                int[] index2 = segments2.get(j).index;
                if(index2[0]==index1[1]&&index2[1]==index1[2]){
                //if(index2[0]==index1[2]){
                    comboRaw.add(new SegmentCombo(segments.get(i),segments2.get(j)));
                }
            }
        }
        
        EJMLModelRegression rm = new EJMLModelRegression("regression6.network");
        System.out.println("======\ncombo row size = " + comboRaw.size());
        for(int i = 0; i < comboRaw.size();i++){
            float[] bottom = new float[3];
            float[] top = new float[3];
            rm.getModel().feedForwardTanhLinear(comboRaw.get(i).seg1.getFeatures(), bottom);
            rm.getModel().feedForwardTanhLinear(comboRaw.get(i).seg2.getFeatures(), top);
            double distance = CentralRegression.distance(top, bottom);
            if(distance<0.1){
                System.out.printf("*********\ncombo %d, distance = %f\n",i,distance);
                System.out.println(Arrays.toString(bottom) + "  " + Arrays.toString(top));
                System.out.println(comboRaw.get(i).seg1);
                System.out.println(comboRaw.get(i).seg2);
            }
        }
    }
}
