/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Leaf;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DriftChamber {
    
    protected long[] data = new long[12*6*6];
    protected boolean useDenoised = true;
    protected int     minMultiplicity = 3;
    protected int     maxMultiplicity = 10;
    protected int            maxWidth = 4;
    
    List<Schema>      schemas = new ArrayList<>();
    
    Pattern[] patterns = null;
    
    public DriftChamber(){patterns = Pattern.create();}
    
    public void reset(){ Arrays.fill(data, 0L);}
    
    public void show(){}
    
    public int sectorOffset(int sector){ return 12*6*(sector-1);}
    public int layerOffset(int layer){   return 1; }
    public int wireWord(int wire){ return wire<=56?0:1;}
    public int wireShift( int wire){
        int shift = wire<=56?56-wire:56-(wire-56);
        return shift;
    }
    public void init(SchemaFactory factory){
        schemas.add(factory.getSchema("DC::tdc"));
    }
    
    public String long2(long num){
        String d = Long.toBinaryString(num);
        StringBuilder str = new StringBuilder();        
        for(int i = 0; i < 64-d.length(); i++) str.append('0');
        str.append(d);
        return str.toString().replaceAll("0", ".");
    }
    
    public void fill(int sector, int layer, int wire){
        int soffset = sectorOffset(sector);
        int loffset = (layer-1)*2;
        int    word = wireWord(wire);
        int    wshift = wireShift(wire);
        data[soffset+loffset+word] = data[soffset+loffset+word]|(1L<<wshift);
    }

    
    public void show(int sector){
        int offset = 12*6*(sector-1);
        for(int i = 0; i < 18*2; i++){
            System.out.printf("%s %s\n",long2(data[offset+i*2]),long2(data[offset+i*2+1]));
            if((i+1)%6==0) System.out.println();
        }
    }
    
    public int next(int[] positions, int[] info){
        int start = info[0];
        while(start<112&&positions[start]==0){ start++;}
        if(start==111){ info[0]=111; info[1]=0; info[2]=0; return start;}
        info[0] = start; info[1] = 0;
        while(start<112&&positions[start]!=0){info[1]+= positions[start]; start++;}
        info[2] = start - info[0];
        return start;
    }
    
    
    private void fillSegment(Leaf leaf, int sector, int superlayer, int[] positions, int[] info){
        
        if(info[1]>minMultiplicity&&info[1]<maxMultiplicity&&info[2]<maxWidth){
            int row = leaf.getRows();
            int start = info[0];
            double r = 0.0, w = 0.0;
            for(int i = 0; i < info[2]; i++){
                r += (start+1)*((double)positions[start]);
                w += (double) positions[start];
                start++;
            }
            leaf.putByte( 1, row, (byte)  sector);
            leaf.putByte( 2, row, (byte)  superlayer);
            leaf.putByte( 3, row, (byte)  info[1]);
            leaf.putByte( 4, row, (byte)  info[2]);
            leaf.putFloat(5, row, (float) (r/w));
            leaf.putFloat(6, row, (float) (r/w));
            leaf.setRows(row+1);
            
        }
    }
    public void findSegments(Leaf leaf){
        int[] positions = new int[112];
        int[] info      = new int[3];
        leaf.setRows(0);
        for(int sector = 0; sector <6; sector++){
            for(int superlayer = 0; superlayer<6; superlayer++){
                this.analyze(sector+1, superlayer+1, positions);
                //System.out.printf(" SECTOR %d , SUPER-LAYER %d\n",sector+1,superlayer+1);
                //System.out.println(Arrays.toString(positions));
                info[0] = 0;
                do {
                    int offset = next(positions,info);
                    this.fillSegment(leaf, sector+1, superlayer+1, positions, info);
                    //System.out.println(Arrays.toString(info));
                    info[0] = offset;
                } while(info[1]>0&&info[2]>0);
            }
        }
    }
    public void segmentsFromBank(Leaf leaf, Bank b){
        fillBank(b,useDenoised);
        
        int[] positions = new int[112];
        int[] info      = new int[3];
        leaf.setRows(0);
        for(int sector = 0; sector <6; sector++){
            for(int superlayer = 0; superlayer<6; superlayer++){
                this.analyze(sector+1, superlayer+1, positions);
                //System.out.printf(" SECTOR %d , SUPER-LAYER %d\n",sector+1,superlayer+1);
                //System.out.println(Arrays.toString(positions));
                info[0] = 0;
                do {
                    int offset = next(positions,info);
                    this.fillSegment(leaf, sector+1, superlayer+1, positions, info);
                    //System.out.println(Arrays.toString(info));
                    info[0] = offset;
                } while(info[1]>0&&info[2]>0);
            }
        }
    }
    
    private void analyze(int sector, int superlayer, int[] positions){
        int soffset = sectorOffset(sector);
        int boffset = (superlayer-1)*12;
        int   start = soffset + boffset;
        //System.out.printf(" sector offset = %d, superlayer offset = %d\n",soffset,boffset);
        
        Arrays.fill(positions, 0);
        
        long[] result = new long[6];
        
        for(int i = 0; i < 56; i++){
            int  c = 0;
            int c2 = 0;
            for(int k = 0; k < 6; k++){
                result[k] = (data[start+k*2]>>i)&patterns[0].mask[k];
                c += result[k]>0?1:0;
                result[k] = (data[start+k*2+1]>>i)&patterns[0].mask[k];
                c2 += result[k]>0?1:0;
            }
            positions[55-i] = c;
            positions[55-i+56] = c2;
        }
    }
    
    public void processEvent(Event e){
        Bank b = new Bank(schemas.get(0),4048);
        e.read(b);
        Leaf leaf = new Leaf(32101,10,"sbbbbff",2048);
        
        this.segmentsFromBank(leaf, b);
        e.write(leaf);
    }
    
    public void processEventRaw(Event e){
        int position = e.scan(42, 11);
        if(position>0){
            int size = e.scanLengthAt(42,1, position);
            Leaf tdc = new Leaf(42,11,"bbbsbil",size+128);
            e.read(tdc);
            this.reset();
            for(int i = 0; i < tdc.getRows(); i++){
                int sector = tdc.getInt(1, i);
                int  layer = tdc.getInt(2, i);
                int   wire = tdc.getInt(3, i);
                int  order = tdc.getInt(4, i);
                if(useDenoised==true) {
                    if(order==0||order==40||order==50) 
                        this.fill(sector, layer, wire);
                } else {
                    this.fill(sector, layer, wire);
                }
            }
            
            Leaf leaf = new Leaf(32101,10,"sbbbbff",2048);            
            this.findSegments(leaf);
            //this.segmentsFromBank(leaf, b);
            e.write(leaf);
        }
    }
    
    public void fillBank(Bank b, boolean denoised){
        this.reset();
        for(int i = 0; i < b.getRows(); i++){
            int sector = b.getInt(0, i);
            int  layer = b.getInt(1, i);
            int   wire = b.getInt(2, i);
            int  order = b.getInt(3, i);
            if(denoised==true) {
                if(order==0||order==40||order==50) 
                    this.fill(sector, layer, wire);
            } else {
                this.fill(sector, layer, wire);
            }
        }
    }
    
    
    public static class Pattern {
        public long[] mask = new long[6];
        public static Pattern[] create(){
            Pattern[] p = new Pattern[5];
            for(int i = 0; i < p.length; i++) p[i] = new Pattern();
            
            p[0].mask[0] = Long.parseLong("000001", 2);
            p[0].mask[1] = Long.parseLong("000001", 2);
            p[0].mask[2] = Long.parseLong("000001", 2);
            p[0].mask[3] = Long.parseLong("000001", 2);
            p[0].mask[4] = Long.parseLong("000001", 2);
            p[0].mask[5] = Long.parseLong("000001", 2);
            
            p[1].mask[0] = Long.parseLong("000001", 2);
            p[1].mask[1] = Long.parseLong("000001", 2);
            p[1].mask[2] = Long.parseLong("000010", 2);
            p[1].mask[3] = Long.parseLong("000001", 2);
            p[1].mask[4] = Long.parseLong("000010", 2);
            p[1].mask[5] = Long.parseLong("000001", 2);
            
            p[2].mask[0] = Long.parseLong("000001", 2);
            p[2].mask[1] = Long.parseLong("000001", 2);
            p[2].mask[2] = Long.parseLong("000010", 2);
            p[2].mask[3] = Long.parseLong("000010", 2);
            p[2].mask[4] = Long.parseLong("000010", 2);
            p[2].mask[5] = Long.parseLong("000010", 2);
            
            p[3].mask[0] = Long.parseLong("000001", 2);
            p[3].mask[1] = Long.parseLong("000001", 2);
            p[3].mask[2] = Long.parseLong("000010", 2);
            p[3].mask[3] = Long.parseLong("000010", 2);
            p[3].mask[4] = Long.parseLong("000100", 2);
            p[3].mask[5] = Long.parseLong("000100", 2);
            
            p[4].mask[0] = Long.parseLong("000010", 2);
            p[4].mask[1] = Long.parseLong("000010", 2);
            p[4].mask[2] = Long.parseLong("000001", 2);
            p[4].mask[3] = Long.parseLong("000001", 2);
            p[4].mask[4] = Long.parseLong("000001", 2);
            p[4].mask[5] = Long.parseLong("000001", 2);
            
            return p;
        }
    }
    
    public static void main(String[] args){
        
        
        
        
        DriftChamber dc = new DriftChamber();
        
        HipoReader r = new HipoReader("/Users/gavalian/Work/DataSpace/decoded/clas_006595.evio.00625-00629_denoised.hipo");
        Bank[] b = r.getBanks("DC::tdc");
        
        r.nextEvent(b);
        r.nextEvent(b);
        r.nextEvent(b);
        r.nextEvent(b);
        //r.nextEvent(b);
        
        Leaf leaf = new Leaf(112,34,"sbbbbff",2048);
        
        int iter = 1;
        
        long then = System.currentTimeMillis();
        for(int i = 0; i < iter; i++){
            dc.segmentsFromBank(leaf, b[0]);
        }
        long now = System.currentTimeMillis();
        double time = now - then;
        System.out.printf("iter = %d, time = %d, rate = %f\n",iter, now-then, time/iter);
        leaf.print();
        /*
        System.out.println(dc.wireShift(57));
        
        dc.fill(1, 1, 1);
        dc.fill(1, 1, 5);
        dc.fill(1, 1, 56);
        dc.fill(1, 1, 57);
        dc.fill(1, 1, 112);
        
        dc.show(1);
        */
        /*
        HipoReader r = new HipoReader("/Users/gavalian/Work/DataSpace/decoded/clas_006595.evio.00625-00629_denoised.hipo");
        Bank[] b = r.getBanks("DC::tdc");
        
        r.nextEvent(b);
        r.nextEvent(b);
        //r.nextEvent(b);
        int count = 5;
        long then = System.currentTimeMillis();
        for(int iter = 0; iter < count; iter++){
            dc.reset();
            for(int i = 0; i < b[0].getRows(); i++){
                int sector = b[0].getInt(0, i);
                int  layer = b[0].getInt(1, i);
                int   wire = b[0].getInt(2, i);
                int  order = b[0].getInt(3, i);
                if(order==0||order==40||order==50) 
                    dc.fill(sector, layer, wire);
            }
        }
        long now = System.currentTimeMillis();
        
        
        dc.show(1);
        //dc.show(2);
        //dc.show(3);
        
        System.out.printf("%d -> %d msec, %f \n",count, now -then, ((double) (now-then))/count );
        
        SegmentFinder sf = new SegmentFinder();
        int count2 = 1;
        long then2 = System.currentTimeMillis();
        for(int i = 0; i < count2; i++){
            sf.analyze(dc, 4, 1);        
            sf.analyze(dc, 4, 2);
            sf.analyze(dc, 4, 3);        
            sf.analyze(dc, 4, 4);
            sf.analyze(dc, 4, 5);        
            sf.analyze(dc, 4, 6);
        }
        
        long now2 = System.currentTimeMillis();
        System.out.printf("%d -> %d msec, %f \n",count2, now2-then2, ((double) (now2-then2))/count2 );*/
    }
}
