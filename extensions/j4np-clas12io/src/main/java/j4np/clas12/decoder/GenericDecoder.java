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
import j4np.data.evio.EvioNode;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
        //dataNodes.add(Clas12NodeUtils.createNodeTDC(12, 1, 8192));
        //for(int k = 41; k<=58;k++) crates.add(k);
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
            store.cache.setRows(0);
            this.decodeCompositeTDC(identity[0],node, store.cache);
            store.cache.setGroup(identity[0]).setItem(21);
            hipoEvent.write(store.cache);
        }
        
        if(identity[2]==57651){
            //store.cache.setRows(0);
            //this.decodeHelicity(identity[0],node, store.cache);
            //store.cache.setGroup(identity[0]).setItem(21);
            //hipoEvent.write(store.cache);
        }
                
        if(identity[2]==57607){
            //if(this.containsCrate(identity[0])==true) {
                store.cache.setRows(0);
                this.decodeArrayTDC(identity[0],node, store.cache);
                store.cache.setGroup(identity[0]).setItem(21);
                hipoEvent.write(store.cache);
                //System.out.println("got it - " + identity[0]);
           // }
        }
        if(identity[2]==0xe101){
            store.pulse.setRows(0);
            this.decodeCompositeADC(identity[0], node, store.pulse);
            store.pulse.setGroup(identity[0]).setItem(23);
            //hipoEvent.write(store.adcCache);
            hipoEvent.write(store.pulse);
        }
        
        if(identity[2]==57638){
            store.pulse.setRows(0);
            this.decodeCompressedCompositeADC(identity[0], node, store.pulse);
            store.pulse.setGroup(identity[0]).setItem(23);
            //hipoEvent.write(store.adcCache);
            hipoEvent.write(store.pulse);
        }
    }
    
    public void decodeHelicity(int crate, EvioNode node, CompositeNode bank){
        int size = node.bufferLength();
        System.out.println(" helicity node length = " + size*4);
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
     * @param pulses 
     */
    protected void decodeCompositeADC(int crate, EvioNode node,  CompositeNode pulses){
        int length = node.bufferLength()*4;
        boolean doLoop = true;
        //System.out.printf("[tdc decoder] : length = %d %08X N-repeat = %d data position = %d\n",length,node.getIntAt(33), node.getIntAt(33), node.getDataPosition());
        int pos = 28;
        //node.show();
        //System.out.println(node.format());
        //System.out.println(" data offset  = " + node.getDataOffset());
        //node.show(32);
        //Node             adc = new Node(25,1,new short[65]);        
        pulses.setRows(0);
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
                    pulses.putShort(0, prows + 4 + s, (short) node.getShortAt(pos+s*2));
                }
                //System.out.printf("\trepeat = %4d, channel = %4d, samples = %5d\n",n,channel,samples);                
                //fitter.fit(params, config, adc);
                
                pos += 2*samples;
            }
            if((pos+17)>=length) doLoop = false;
        }
    }
    /*
    * 	<dictEntry name="FADC250 Window Raw Data (mode 1 packed)" tag="0xe126" num="0" type="composite">
    * <description format="c,m(c,ms)">
    *  c 	"slot number"
    * m	"number of channels fired"
    * c	"channel number"
    * m	"number of shorts in packed array"
    * s	"packed fadc data"
    * </description>
    * </dictEntry>
    */
    protected void decodeCompressedCompositeADC(int crate, EvioNode node,  CompositeNode pulses){
        int length = node.bufferLength()*4;
        boolean doLoop = true;
        //System.out.printf("[tdc decoder] : length = %d %08X N-repeat = %d data position = %d\n",length,node.getIntAt(33), node.getIntAt(33), node.getDataPosition());
        //node.show(28);
        
        int pos = 22;
        pulses.setRows(0);
        //System.out.printf("decoding crate %d, length %d\n " , crate, length);
        while(doLoop==true){
            int    slot = node.getByteAt( pos);
            int nrepeat = node.getByteAt( pos+1);
            //System.out.printf("\t slot %d, nrepeat = %d\n",slot,nrepeat);
            pos += 2;
            //System.out.printf(" fadc %5d , slot %5d , nrepeat = %5d\n",crate,slot,nrepeat);
            for(int n = 0; n < nrepeat; n++){
                int channel = node.getByteAt( pos);
                int samples = node.getByteAt( pos+1);  
                pos += 2;
                
                short[] buffer = new short[samples];
                for(int s = 0; s < samples; s++) buffer[s] = node.getShortAt(pos+s*2);
                pos += 2*samples;
                List<Short> data = this.getDecoded(buffer);
                
                int prows = pulses.getRows();
                pulses.setRows(prows+4+data.size());
                pulses.putShort(0, prows,   (short) data.size());
                pulses.putShort(0, prows+1, (short) crate);
                pulses.putShort(0, prows+2, (short) slot);
                pulses.putShort(0, prows+3, (short) channel);
                //System.out.printf(" CRATE : %d -> prows = %d, nsample = %d, new rows = %d\n",crate,prows,samples,pulses.getRows());
                //adc.setShort(0, (short) samples);
                //System.out.printf(" CRATE: %d / SLOT %d / CHANNEL %d / nsamples = %d, size = %d, position = %d, length = %d\n",
                //        crate,slot,channel, samples,data.size(),pos,length);
                
                for(int s = 0; s < data.size(); s++){
                    pulses.putShort(0, prows + 4 + s, data.get(s));
                } 
            }
            if((pos+7)>=length) doLoop = false;
        }
    }
    
    
    public List<Short> getDecoded(short[] adcBuffer){

        List<Short> result = new ArrayList<>();

        short[]  bucket = new short[16];

        int nwords;
        int nskip;
        int position = 0;
        int headerWord;
        int pedestal;
        int compressedWord;
        byte[] array = new byte[4];

        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        while(position<adcBuffer.length){

            short dataH = adcBuffer[position];
            short dataL = adcBuffer[position+1];

            buffer.putShort(0, dataH);
            buffer.putShort(2, dataL);

            headerWord = buffer.getInt(0);
            nwords   = (headerWord&0x07);
            nskip    = (dataH>>4) &0x0F;
            pedestal = (headerWord>>8)&0x0FFF;
            compressedWord = (headerWord>>27)&0x0F;

            position+=2;
            if(compressedWord==5){

                short value;
                for(int i = 0; i < 4; i++){
                    value = (short) (adcBuffer[position+i]&0x000F);                    
                    bucket[i*4] = (short) (value+pedestal);                                        
                    value = (short) ((adcBuffer[position+i] >> 4)&0x000F);                    
                    bucket[i*4+1] = (short) (value+pedestal);                    
                    value = (short) ((adcBuffer[position+i] >> 8)&0x000F);
                    bucket[i*4+2] = (short) (value+pedestal);
                    value = (short) ((adcBuffer[position+i] >> 12)&0x000F);
                    bucket[i*4+3] = (short) (value+pedestal);
                }

                position += 4;

                if(nwords>0){

                    for(int i = 0; i < nwords*2; i++){
                        short  first = (short)  ( (adcBuffer[i+position]&0x00FF) << 4);
                        short second = (short) ( ((adcBuffer[i+position] >>8)&0x00FF)<<4);
                        if( (nskip+i*2)<=15  ) bucket[nskip+i*2]   +=  first;
                        if( (nskip+i*2+1)<=15) bucket[nskip+i*2+1] +=  second;
                    }                    
                    position += nwords*2;
                }

                for(int k = 0 ; k < bucket.length; k++){
                    result.add(bucket[k]);
                }
            }
        }
        return result;
    }
}
