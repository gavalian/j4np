/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.level3.data;

import j4np.data.base.DataEvent;
import j4np.data.base.DataNode;
import j4np.data.evio.EvioEvent;
import j4np.data.structure.DataStructure;
import java.nio.ByteBuffer;

/**
 *
 * @author gavalian
 */
public class DecoderEvent implements DataEvent {
    
    /**
     * The decoder event contains decoded ADC and TDC values
     * from the evioEvent that is part of the DecoderEvent in 
     * data structures described below.
     * The format is as follows;
     * b - detector
     * b - sector
     * b - layer
     * s - component
     * b - order (means left or right)
     * i - TDC value (or ADC value for ADC data structure)
     */
    
    DataStructure   tdcData = new DataStructure("bbbsbi",20000);
    DataStructure   adcData = new DataStructure("bbbsbi",20000);
    
    
    
    EvioEvent       evioEvent = new EvioEvent();
    
    public DecoderEvent(){
        
    }
    
    public EvioEvent getEvioEvent(){ return evioEvent;}
    
    public DataStructure  getADC(){ return adcData;}
    public DataStructure  getTDC(){ return tdcData;}
    
    public String summaryString(){
        return String.format("## event size %8d, adc %8d, tdc %8d", 
                evioEvent.bufferLength(),adcData.getRows(),tdcData.getRows());
    }

    @Override
    public void getAt(DataNode node, int position) {
        evioEvent.getAt(node, position);
    }

    @Override
    public ByteBuffer getBuffer() {
        return this.evioEvent.getBuffer();
    }

    @Override
    public int bufferLength() {
        return evioEvent.bufferLength();
    }

    @Override
    public boolean allocate(int size) {
        return evioEvent.allocate(size);
    }

    @Override
    public int identifier() {
        return 1001;
    }

    @Override
    public boolean verify() {
        return true;
    }
        
}
