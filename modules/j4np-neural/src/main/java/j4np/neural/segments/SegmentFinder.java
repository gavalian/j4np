/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.segments;

import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;

/**
 *
 * @author gavalian
 */
public class SegmentFinder {
    
    int[]  hits    = new int[112*6];
    int[]  edge    = new int[112*6];
    int[]  zeros   = new int[112*6];
    float[] crop   = new float[112*3];
    double[] output = new double[112];
    
    private WireStore  store = new WireStore();
    
    public SegmentFinder(){
       for(int i = 0; i < zeros.length; i++) zeros[i] = 0; 
    }
    
    
    public void analyze(int sector, int superlayer){
        reset();
        store.getData(sector, superlayer, hits);
        edgeDetection();
        
        //this.print(edge, true);
    }

    public int nonZero(int[] array, int startIndex, int endIndex){
        int index = startIndex;
        while(index<=endIndex&&array[index]==0) index++;
        return index;
    }
    
    /*
    private int findClosest(int[] array, int index){
        int index2 = index+112;
        int distance = 100;
        int bi = -1;
        int range = 2;
        for(int i = -range; i<=range; i++){
            
            if(array[i+index2]!=0){
                int d2 = Math.abs(range-i);
                //System.out.printf("found one at %d , from %d distance = %d\n",index2+i,index,d2);
                if(d2<distance){ distance = d2; bi = i+index2;}
            }
        }
        return bi;
    } */
    
    private int findClosest(int[] array, int index, int start, int end){
        double distance = 100.0;
        int bi = -1;
        for(int i = start; i <=end; i++){ 
            //System.out.printf(" position %d, value = %d\n",i,array[i]);
            if(array[i]!=0){
                double d2 = Math.abs(index-i);
                //System.out.printf("found one at %d , from %d distance = %d\n",index2+i,index,d2);
                if(d2<distance){ distance = d2; bi = i;}
            }                
        }
        return bi;
    } 
    /*
    public void distance(int[] array, int index){
        for(int i = 112; i < array.length; i++){
            if(array[i]!=0){
                int w = i%112;
                System.out.printf(" i = %d, adjusted = %d, distance = %d\n",i, w, w-index);
            }
        }
    }*/
    /*
    public void distance(int index){
        distance(edge,index);
    }*/
    
    public void segmentAt(int index){
        int start = - 3;
        int   end = + 3;
        if(index<3) start = -index;
        if(index>=108) end = 111 - index;
        //System.out.printf("index = %d, start = %d , end = %d\n",index, start,end);
        int count = 0;
        double chi2 = 0.0;
        for(int i = 0; i < 6; i++){
            int offset  = i*112 + index;
            int closest = findClosest(edge,offset,offset+start, offset+end);
            //System.out.printf(" offset = %4d , closest = %3d\n",offset,closest);
            if(closest>=0){
                int wire = closest%112;
                //System.out.printf(" index = %d, layer = %d, closest = %d\n",index,i+1,wire);
                count++;
                chi2 += wire - index;
            }
        }
        if(count>=4) output[index] = Math.abs(chi2/count); else output[index] = 112.0;
        //if(count>=4) System.out.printf(" at %5d, count = %4d, chi2 = %8.5f, normalized = %9.5f\n",index,count,chi2, chi2/count);
    }
    
    private int findMinimumAbs(double[] array){
        double min = array[0]; int bin = 0;
        for(int i = 1; i < array.length; i++){
            if(array[i]<min){ min = array[i]; bin = i;}
        }
        return bin;
    }
    
    public void showSegments(){
        int    index = this.findMinimumAbs(output);
        double   min = output[index];
        while(min<1.0){
            //System.out.printf("found segment at %d, with chi2 = %8.5f\n",index,min);
            output[index] = 112; if(index!=0) output[index-1] = 112; if(index!=111) output[index+1] = 112;
            index = this.findMinimumAbs(output);
            min = output[index];
        }
    }
    /*
    public void crop(int order){
        int start = order*112;
        int   end = (order+1)*112-1;
        int index = nonZero(edge,start,end);
        int low = 0;
        int high = 0;
        
        while(index<=end){
            int bi = this.findClosest(edge, index);
            System.out.printf(" index = %d, value = %d\n",index-order*112,edge[index]);
            if(bi>=0) System.out.printf("\t matched index = %d, value = %d\n",bi-112-order*112,edge[bi]);
            start = index+1;
            index = nonZero(edge,start,end);
        }
    }*/
    
    public void edgeDetection(){
        int w = 0;
        while(w<hits.length){
            if(hits[w]==0){
                w++;
            } else {
                int wr = w;
                while(wr<hits.length&&hits[wr]!=0) wr++;
                edge[w] = wr-w; w = wr;
            }
        }
    }
    
    public void print(int[] array, boolean value){
        for(int i = 0; i < array.length; i++){
            if(array[i]==0) System.out.print("- "); 
            else {
                System.out.printf("%d ",array[i]);
            }
            
            if((i+1)%112==0) System.out.println();
        }
        for(int i = 0; i < 112; i++){
            if((i+1)%10==0) System.out.print("| "); else System.out.print("  ");
        }
        System.out.println();
    }
    
    
    
    public void fill(CompositeNode node){
        store.reset();
        store.fill(node);
    }
    
    public void reset(){
        System.arraycopy(zeros, 0, hits, 0, hits.length);
        System.arraycopy(zeros, 0, edge, 0, edge.length);
    }
    
    public static void main(String[] args){
        
        HipoReader r = new HipoReader("output_clusters_4.h5_000000");
        //Bank[] b = r.getBanks("DC::tdc");
        Event e = new Event();
        
        r.getEvent(e, 14);
        
        CompositeNode node = new CompositeNode(12,1,"bbsbil",4096);
        
        e.read(node);
        
        SegmentFinder sf = new SegmentFinder();
        
        sf.fill(node);

        
        sf.analyze(1, 1);

        //sf.segmentAt(111);
        for(int i = 0; i < 112; i++){
            sf.segmentAt(i);
        }
        
        sf.showSegments();
        
        
        int iter = 150000;
        
        long then = System.currentTimeMillis();
        for(int i = 0; i < iter; i++){
            sf.fill(node);

            for(int s = 1; s <= 6; s++){
                for(int sl = 1; sl <= 6; sl++){
                    //store.getData(s, sl, data);
                    sf.analyze(s, sl);
                    for(int ii = 0; ii < 112; ii++){
                        sf.segmentAt(ii);
                    }
                    //sf.showSegments();
                }
            }
        }
        
        long now = System.currentTimeMillis();
        System.out.printf(" iterations = %d, time = %d , ms per event %f\n",iter,now-then, ((double) (now-then))/iter);
        
        
    }
}
