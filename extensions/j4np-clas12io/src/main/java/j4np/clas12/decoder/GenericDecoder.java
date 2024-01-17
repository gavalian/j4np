/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.decoder;

import j4np.clas12.ccdb.DataUtils;
import j4np.clas12.ccdb.DatabaseProvider;
import j4np.clas12.ccdb.DetectorTools;
import j4np.clas12.io.Clas12NodeUtils;
import j4np.clas12.decoder.PulseFitter.PulseFitterConfig;
import j4np.clas12.decoder.PulseFitter.PulseFitterParams;
import j4np.clas12.io.SinglePulseFitter;
import j4np.data.evio.EvioNode;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class GenericDecoder extends EvioNodeDecoder {
    
    protected List<Integer> crates = new ArrayList<>();
    protected Node             adc = new Node(25,1,new short[65]);
    protected String   decoderName = "generic";
    
    protected String   translationTable = null;
    protected String       fittingTable = null;
    
    protected Map<Long,Long> translationMap = null;    
    protected Map<Long,Long>     fittingMap = null;
    
    protected PulseFitter    fitter = new SinglePulseFitter();
    
    public GenericDecoder(){
        dataNodes.add(Clas12NodeUtils.createNodeTDC(12, 1, 8192));
        for(int k = 41; k<=58;k++) crates.add(k);
    }
    
    public boolean containsCrate(int crate){
        int index = Collections.binarySearch(crates, crate);
        if(index>=0) return true;
        return false;
    }
    
    @Override
    public void decode(EvioNode node, int[] identity) {
        if(identity[2]==57622){
            if(this.containsCrate(identity[0])==true) 
                this.decodeCompositeTDC(identity[0],node, dataNodes.get(0));
        }
    }
    
    @Override
    public  void decode(EvioNode node, int[] identity, DataBankStore store, Event hipoEvent){
        
        if(identity[2]==57610){
            this.decodeTimeStamp(identity[0], node, store.timeStamp);
        }
        
        if(identity[2]==57615){
            this.decodeHeader(identity[0], node, store.header);
        }
        
        if(identity[2]==57622){
            store.tdcCache.setRows(0);
            this.decodeCompositeTDC(identity[0],node, store.tdcCache);
            store.tdcCache.setGroup(identity[0]).setItem(21);
            hipoEvent.write(store.tdcCache);
        }
        if(identity[2]==57607){
            //if(this.containsCrate(identity[0])==true) {
                store.tdcCache.setRows(0);
                this.decodeArrayTDC(identity[0],node, store.tdcCache);
                store.tdcCache.setGroup(identity[0]).setItem(21);
                hipoEvent.write(store.tdcCache);
                //System.out.println("got it - " + identity[0]);
           // }
        }
        if(identity[2]==0xe101){
            store.adcCache.setRows(0);
            store.adcCachePulse.setRows(0);
            this.decodeCompositeADC(identity[0], node, store.adcCache, store.adcCachePulse);
            store.adcCache.setGroup(identity[0]).setItem(22);
            store.adcCachePulse.setGroup(identity[0]).setItem(23);
            hipoEvent.write(store.adcCache);
            hipoEvent.write(store.adcCachePulse);
        }
    }
    
    public void decodeHeader(int crate, EvioNode node, CompositeNode bank){
        bank.setRows(1);
        bank.putInt(0, 0, node.getIntAt(4));
        bank.putInt(1, 0, node.getIntAt(8));
        //for(int i = 0; i < 4; i++) System.out.printf("HEADER %5d : %5d\n", i , node.getIntAt(i*4));
        //bank.print();
    }
    
    public void decodeTimeStamp(int crate, EvioNode node, CompositeNode bank){
        long ts = node.getLongAt(8)&0x0000ffffffffffffL;
        int nrows = bank.getRows(); 
        bank.putInt( 0, nrows, crate);
        bank.putLong(1, nrows, ts);
        bank.setRows(nrows+1);
        //System.out.printf("size = %d, crate = %d, ts = %d\n",node.getSize(), crate,node.getLongAt(8)&0x0000ffffffffffffL);
    }
    public void decodeTI(int crate, EvioNode node, CompositeNode bank){
        //System.out.printf("TI-node-size  (%5d) = %d buffewr Length = %d\n",crate, node.getSize(), node.bufferLength());
    }
    
    @Override
    public void initDecoder(DatabaseProvider provider){
        if(translationTable!=null){
            this.translationMap = provider.getTranslation(0, 12,translationTable);
            this.crates.clear();
            this.crates.addAll(DetectorTools.getCreates(translationMap));
        }
         if(fittingTable!=null){
            //this.translationMap = provider.getTranslation(0, translationTable);
        }
    }
    
    @Override
    public void finilize(){
        int size = dataNodes.size();
        int[] address = new int[4];
        for(int i = 0; i < size; i++){
            CompositeNode node = dataNodes.get(i);
            int rows = node.getRows();
            for(int r = 0; r < rows; r++){
                long key = DetectorTools.hardwareEncoder(node.getInt(0, r), node.getInt(1, r), node.getInt(2, r));
                //System.out.printf(" key = %016X, c : %5d, s : %5d, c : %5d , contains = %s\n",
                //        key,node.getInt(0, r), node.getInt(1, r), node.getInt(2, r),translationMap.containsKey(key));
                if(translationMap.containsKey(key)==true){
                    long value = translationMap.get(key);
                    DetectorTools.softwareDecoder(value, address);
                    node.putByte(  1, r, (byte)  address[0]);
                    node.putByte(  2, r, (byte)  address[1]);
                    node.putShort( 3, r, (short) address[2]);
                    node.putByte(  4, r, (byte)  address[3]);
                    //System.out.printf("%016X -> %s\n", value,Arrays.toString(address));
                }
            }
        }
    }    
    /*
    @Override
    public void translate(Map<Long,Long> translation){
        int size = dataNodes.size();
        int[] address = new int[4];
        for(int i = 0; i < size; i++){
            CompositeNode node = dataNodes.get(i);
            int rows = node.getRows();
            for(int r = 0; r < rows; r++){
                long key = DetectorTools.hardwareEncoder(node.getInt(0, r), node.getInt(1, r), node.getInt(2, r));
                if(translation.containsKey(key)==true){
                    long value = translation.get(key);
                    DetectorTools.softwareDecoder(value, address);
                    //System.out.printf("%016X -> %s\n", value,Arrays.toString(address));
                }
            }
        }
    }
        
    @Override
    public Map<Long,Long>  loadTranslation(DatabaseProvider provider, String table){
        Map<Long,Long>  map = provider.getTranslation(0, table);
        List<Integer>  list = DetectorTools.getCreates(map);
        this.crates.clear();
        this.crates.addAll(list);
        return map;
    }
    */
    @Override
    public void show(){
        System.out.printf("%12s : ", decoderName);
        for(Integer crate : crates) System.out.printf("%4d", crate);
        System.out.println();
    }
    
    /**
     * Parsing the EVIO bank with TAG=0xe116 (57607)
     * This is the REGULAR TDC Bank, stored as Array of integers
     * @param crate - the crate number
     * @param node - evio node
     * @param bank - composite HIPO-5 bank
     */
    protected void decodeArrayTDC(int crate, EvioNode node, CompositeNode bank){
        int length = node.bufferLength();
        //System.out.println(" node length = " + length);
        for(int row = 0; row < length-2; row++){
            int dataEntry = node.getIntAt(row*4);
            int  slot      = DataUtils.getInteger(dataEntry, 27, 31 );
            int  chan      = DataUtils.getInteger(dataEntry, 19, 25);
            int  value     = DataUtils.getInteger(dataEntry,  0, 18);
            int nrows = bank.getRows();
            if(nrows<8124){
                bank.setRows(nrows+1);
                bank.putByte(  1, nrows, (byte) crate);
                bank.putByte(  2, nrows, (byte) slot);
                bank.putShort( 3, nrows, (short) chan);
                bank.putInt(   5, nrows, value);
            }
            //System.out.printf("\t c : %3d, s : %3d, c : %5d, tdc = %6d\n",crate, slot, chan, value);
        }
    }
    /**
     * Parsing the EVIO bank with TAG=0xe116 (57622)
     * This is the REGULAR TDC Bank 
     * @param crate - the crate number
     * @param node - evio node
     * @param bank - composite HIPO-5 bank
     * 
     */
    protected void decodeCompositeTDC(int crate, EvioNode node, CompositeNode bank){
        //node.show(24);
        //System.out.println(node.format());
        int length = node.bufferLength()*4;
        boolean doLoop = true;
        //System.out.printf("[tdc decoder] : length = %d %08X N-repeat = %d data position = %d\n",length,node.getIntAt(33), node.getIntAt(33), node.getDataPosition());
        int pos = 28;
        
        while(doLoop==true){
            int    slot = node.getByteAt( pos);
            int nrepeat = node.getIntAt(  pos+13);
            pos += 17;
            //System.out.println("n-repeat " + nrepeat);
            for(int n =  0; n < nrepeat; n++){
                int  channel =  node.getByteAt(pos);
                int     tdc =  node.getShortAt(pos+1);
                //System.out.printf("\tSLOT = %4d, CHANNEL = %4d, TDC = %5d\n",slot,channel,tdc);
                pos += 3;
                int row = bank.getRows();
                if(row<8124){
                    bank.setRows(row+1);
                    bank.putByte( 1, row, (byte) crate);
                    bank.putByte(2, row, (byte) slot);
                    bank.putShort(3, row, (short) channel);
                    bank.putInt(5, row, tdc);
                }
            }
            if((pos+17)>=length) doLoop = false;
            //int slot = 
        }
    }
    /**
     * Decoder for generic ADC bank with tag = 0xe101 (57601)
     * @param crate
     * @param node
     * @param bank 
     */
    protected void decodeCompositeADC(int crate, EvioNode node, CompositeNode bank, CompositeNode pulses){
        int length = node.bufferLength()*4;
        boolean doLoop = true;
        //System.out.printf("[tdc decoder] : length = %d %08X N-repeat = %d data position = %d\n",length,node.getIntAt(33), node.getIntAt(33), node.getDataPosition());
        int pos = 28;
        //node.show();
        //System.out.println(node.format());
        //System.out.println(" data offset  = " + node.getDataOffset());
        //node.show(32);
        //Node             adc = new Node(25,1,new short[65]);
        PulseFitterConfig  config = new PulseFitterConfig();
        PulseFitterParams  params = new PulseFitterParams();
        
        while(doLoop==true){
            int    slot = node.getByteAt( pos);
            int nrepeat = node.getIntAt(  pos+13);
            pos += 17;
            //System.out.printf(" fadc %5d , slot %5d , nrepeat = %5d\n",crate,slot,nrepeat);
            for(int n = 0; n < nrepeat; n++){
                int channel = node.getByteAt( pos);
                int samples = node.getIntAt(  pos+1);                
                pos += 5;
                int prows = pulses.getRows();
                
                pulses.setRows(prows+4+samples);
                pulses.putShort(0, prows, (short) samples);
                pulses.putShort(0, prows+1, (short) crate);
                pulses.putShort(0, prows+2, (short) slot);
                pulses.putShort(0, prows+3, (short) channel);
                //System.out.printf(" CRATE : %d -> prows = %d, nsample = %d, new rows = %d\n",crate,prows,samples,pulses.getRows());
                //adc.setShort(0, (short) samples);
                for(int s = 0; s < samples; s++){
                    pulses.putShort(0, prows + 4, (short) node.getShortAt(pos+s*2));
                }
                //System.out.printf("\trepeat = %4d, channel = %4d, samples = %5d\n",n,channel,samples);
                
                //fitter.fit(params, config, adc);
                int nrows = bank.getRows();
                if(nrows<8124){
                    bank.setRows(nrows+1);
                    bank.putByte(  1, nrows, (byte) crate);
                    bank.putByte(  2, nrows, (byte) slot);
                    bank.putShort( 3, nrows, (short) channel);
                    bank.putInt(   5, nrows,  0);
                    bank.putFloat( 6, nrows, (float) 0.0);
                    bank.putShort( 7, nrows, (short) 0);                  
                }
                pos += 2*samples;
            }
            if((pos+17)>=length) doLoop = false;
        }
    }
}
