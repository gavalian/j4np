/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DriftChamber2 {
    
    protected long[] data = new long[12*6*6];
    protected List<long[]> patterns = new ArrayList<>();
    
    public DriftChamber2(){
        patterns.addAll(this.patternsLong());
    }
    
    public int[] create(String... binary){
        int[] array = new int[binary.length];
        for(int i = 0; i < array.length; i++)
            array[i] = Integer.parseInt(binary[i], 2);
        return array;
    }
    public long[] createLong(String... binary){
        long[] array = new long[binary.length];
        for(int i = 0; i < array.length; i++)
            array[i] = Long.parseLong(binary[i], 2);
        return array;
    }
    /**
     * Sanitizing requires more thinking. I will come back to this later.
     * @param data - data structure produced by drift chamber scan code
     */
    public void sanitize(ChamberData data){
        for(int i = 0; i < data.scan.length; i++){
            if(data.scan[i]<=3) data.scan[i] = 0;
            if(data.scan[i]==4){
                if(i<data.scan.length-1&&i>0){
                    if(data.scan[i+1]==5||data.scan[i+1]==6){ data.scan[i] = 0; }
                    else { data.scan[i+1] = 0;}
                    if(data.scan[i-1]<=4) data.scan[i-1] = 0;
                }
            }
            
            if(data.scan[i]==5){
                if(i<data.scan.length-1&&i>0){
                    if(data.scan[i+1]==6){ data.scan[i] = 0; }
                    else { data.scan[i+1] = 0;}
                    if(data.scan[i-1]<=5) data.scan[i-1] = 0;
                }
            }
            
            if(data.scan[i]==6){
                if(i<data.scan.length-1&&i>0){
                    if(data.scan[i-1]<=6) data.scan[i-1] = 0;
                    if(data.scan[i+1]<=6) data.scan[i+1] = 0;
                }
            }
        }
    }
    
    public void reset(){ Arrays.fill(data, 0L);}    
    public void show(){}    
    public int  sectorOffset(int sector){ return 12*6*(sector-1);}
    public int  layerOffset( int layer){   return 1; }
    public int  wireWord( int wire){ return wire<=56?0:1;}
    public int  wireShift( int wire){
        int shift = wire<=56?56-wire:56-(wire-56);
        return shift;
    }
    
    public void setWire(int sector, int layer, int wire){
        int soffset = sectorOffset(sector);
        int loffset = (layer-1)*2;
        int    word = wireWord(wire);
        int    wshift = wireShift(wire);
        data[soffset+loffset+word] = data[soffset+loffset+word]|(1L<<wshift);
    }
    
    public String long2(long num){
        String d = Long.toBinaryString(num);
        StringBuilder str = new StringBuilder();        
        for(int i = 0; i < 64-d.length(); i++) str.append('0');
        str.append(d);
        return str.toString().replaceAll("0", ".");
    }
    
    public void show(int sector){
        int offset = 12*6*(sector-1);
        for(int i = 0; i < 18*2; i++){
            System.out.printf("%s %s\n",long2(data[offset+i*2]),long2(data[offset+i*2+1]));
            if((i+1)%6==0) System.out.println();
        }
    }
    
    public String int2(int num){
        String d = Integer.toBinaryString(num);
        StringBuilder str = new StringBuilder();        
        for(int i = 0; i < 32-d.length(); i++) str.append('0');
        str.append(d);
        return str.toString().replaceAll("0", ".");
    }
    
    public void print(int[] array){
        System.out.println();
        for(int i = 0; i < array.length; i++)             
            System.out.printf("%s  : %8d \n",int2(array[i]),array[i]);
    }
    public int[] scanPatterns(long[] data, int shift, List<long[]> p){
        for(int j = 0; j < data.length; j++) data[j] = data[j]>>shift;
        int[] results = new int[p.size()];
        for(int j = 0; j < p.size(); j++){
            long[] pattern = p.get(j);
            //System.out.println(" LENGHTS = " + data.length + " " + pattern.length);
            for(int i = 0; i < data.length; i++){
                long r = data[i]&pattern[i];
                if(r>0) results[j]++;
            }
        }
        return results;
    }
    
    public void scan(ChamberData data, int sector, int superlayer){
        int offset = this.sectorOffset(sector);
        int bunch  = 0;
        
        for(int j = 0; j < 6; j++) data.temp[j] = this.data[offset+bunch+j*2+1];
        int shift = 0;
        for(int i = 0; i < 56; i++){
            int[] results = scanPatterns(data.temp,shift,this.patterns);
            if(shift==0) shift=1;
            int max = findmax(results);
            data.type[i] = max;
            data.scan[i] = results[max];
        }
        
        for(int j = 0; j < 6; j++) data.temp[j] = this.data[offset+bunch+j*2];
        shift = 0;
        for(int i = 0; i < 56; i++){
            int[] results = scanPatterns(data.temp,shift,this.patterns);
            if(shift==0) shift=1;
            int max = findmax(results);
            data.type[i+56] = max;
            data.scan[i+56] = results[max];
        }
    }
    
    public List<int[]> scan(){
        
        List<long[]>  p = this.patternsLong();
        
        int nonZero = 0;
        
        int   sector = this.sectorOffset(1);
        int   layer  = this.layerOffset(1);
        
        long[] ww = new long[6];
        
        ww[0] = this.data[sector+0+1];
        ww[1] = this.data[sector+2+1];
        ww[2] = this.data[sector+4+1];
        ww[3] = this.data[sector+6+1];
        ww[4] = this.data[sector+8+1];
        ww[5] = this.data[sector+10+1];
        
        int[] type = new int[112];
        int[] mult = new int[112];
        
        int shift = 0;
        for(int i = 0; i < 56; i++){
            //for(int w = 0; w < 6; w++) wwt[w] = ww[w];
            int[] results = this.scanPatterns(ww, shift, p);
            if(shift==0) shift = 1;
            int max = findmax(results);
            type[i] = max;
            mult[i] = results[max];
            //System.out.printf("%3d : %s -- [%2d] %d\n",i, Arrays.toString(results), max, results[max]);
        }
        ww[0] = this.data[sector+0];
        ww[1] = this.data[sector+2];
        ww[2] = this.data[sector+4];
        ww[3] = this.data[sector+6];
        ww[4] = this.data[sector+8];
        ww[5] = this.data[sector+10];
        shift = 0;
        for(int i = 0; i < 56; i++){
            //for(int w = 0; w < 6; w++) wwt[w] = ww[w];
            int[] results = this.scanPatterns(ww, shift, p);
            if(shift==0) shift = 1;
            int max = findmax(results);
            type[i+56] = max;
            mult[i+56] = results[max];
            //System.out.printf("%3d : %s -- [%2d] %d\n",i, Arrays.toString(results), max, results[max]);
        }
        return Arrays.asList(type,mult);
    }
    
    public int findmax(int[] array){
        int bin = 0; int max = array[0];
        for(int i = 0; i < array.length;i++){ 
            if(array[i]>max) { bin = i; max = array[i];}
        }
        return bin;
    }
    public int[] doAND(int[] a1, int[] a2, int shift){
        int[] b = new int[a1.length];
        for(int i = 0; i < b.length; i++) b[i] = (a1[i]>>shift)&a2[i];
        return b;
    }
    
    public long[] doAND(long[] a1, long[] a2, int shift){
        long[] b = new long[a1.length];
        for(int i = 0; i < b.length; i++) b[i] = (a1[i]>>shift)&a2[i];
        return b;
    }
    
    public void doAND(int[] a1, int[] a2, int shift, int[] result){        
        for(int i = 0; i < result.length; i++) result[i] = (a1[i]>>shift)&a2[i];
    }
    
    public int[] doXOR(int[] a1, int[] a2){
        int[] b = new int[a1.length];
        for(int i = 0; i < b.length; i++) b[i] = a1[i]^a2[i];
        return b;
    }
    
    public int[] doOR(int[] a1, int[] a2){
        int[] b = new int[a1.length];
        for(int i = 0; i < b.length; i++) b[i] = a1[i]|a2[i];
        return b;
    }
    
    public List<int[]>  patterns(){
        List<int[]> p = new ArrayList<>();

        p.add(create("0001","0001","0001","0001","0001","0001"));
        p.add(create("0001","0001","0001","0010","0010","0010"));
        p.add(create("0001","0001","0010","0010","0010","0010"));
        p.add(create("0001","0001","0010","0010","0100","0100"));
        p.add(create("0001","0010","0010","0010","0100","0100"));        
        return p;
    }
    
    public final List<long[]>  patternsLong(){
        List<long[]> p = new ArrayList<>();

        p.add(createLong("0001","0001","0001","0001","0001","0001"));
        p.add(createLong("0010","0001","0010","0001","0010","0001"));
        p.add(createLong("0001","0010","0001","0010","0001","0010"));
        p.add(createLong("0001","0001","0001","0010","0010","0010"));
        p.add(createLong("0001","0001","0010","0010","0010","0010"));
        p.add(createLong("0001","0001","0010","0010","0100","0100"));
        p.add(createLong("0001","0010","0010","0010","0100","0100"));
        p.add(createLong("00001","00010","00010","00100","00100","01000"));
        //----- the reverse slope
        p.add(createLong("01000","00100","00100","00010","00010","00001"));
        return p;
    }
    
    public int countNonZero(int[] array){
        int count = 0; for(int j = 0; j < array.length; j++)
            if(array[j]>0)count++;
        return count;
    }
    public int countNonZero(long[] array){
        int count = 0; for(int j = 0; j < array.length; j++)
            if(array[j]>0)count++;
        return count;
    }
    
    public int findposition(int[] dc, List<int[]> patterns){
        int shift = 0;
        int[] line = patterns.get(0);
        int c = 0;
        while(shift<12&&c == 0){
            int[] res = this.doAND(dc, line, shift);
            if(countNonZero(res)>0) c = 1; else shift++; 
        }
        System.out.println("foud potential @ " + shift + " with count " );
        return shift;
    }
    public static void main(String[] args){
        DriftChamber2 dc = new DriftChamber2();
        int[] a1 = dc.create("00010000","00010000","00000000","00000000","01000000","01000000");
        dc.print(a1);        
        int[] a2 = dc.create("0001","0001","0001","0001","0001","0001");                
        int[] b = dc.doXOR(a1, a2);                
        dc.print(b);
        List<int[]> p = dc.patterns();        
        for(int i = 0; i < p.size(); i++){
            int[] r = dc.doAND(a1, p.get(i),0);
            System.out.println(" PATTERN " + i);
            dc.print(r);
        }                
        int pos = dc.findposition(a1, p);
        
        dc.print(a1);
        
        for(int i = 0; i < p.size(); i++){
            int[] res = dc.doAND(a1, p.get(i), pos);
            System.out.println(" NON Zero = " + dc.countNonZero(res));
            dc.print(res);
        }
        /*
        for(int i = 0; i < p.size(); i++){
            int[] r = dc.doOR(a1, p.get(i));
            int[] d = dc.doXOR(r, p.get(i));
            System.out.println(" PATTERN " + i);
            dc.print(r);
        }*/
    }
}
