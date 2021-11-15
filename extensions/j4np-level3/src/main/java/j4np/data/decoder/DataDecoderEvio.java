/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.decoder;

import j4np.data.base.DataEvent;
import j4np.data.base.DataFrame;
import j4np.data.base.DataNodeCallback;
import j4np.data.base.DataUtils;
import j4np.data.evio.EvioEvent;
import j4np.data.evio.EvioFile;
import j4np.data.evio.EvioNode;
import j4np.data.structure.DataStructure;
import j4np.data.structure.DataStructureUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import j4np.level3.data.DecoderEvent;

/**
 *
 * @author gavalian
 */
public class DataDecoderEvio implements Consumer<DecoderEvent> {
    
    //DataStructure   tdcData = new DataStructure("bbbsbi",20000);
    DataTranslator tdcTable = new DataTranslator();
    DataTranslator ecalTable = new DataTranslator();
    
    public DataDecoderEvio(){
        tdcTable.read("etc/db/dc_tt.txt");
        ecalTable.read("etc/db/ec_tt.txt");
    }
    
    
    @Override
    public void accept(DecoderEvent event) {
        processDataEvent(event);
    }
    
    public void processDataEvent(DecoderEvent event){
        
        //System.out.println("---- processing event ");
        
        DataStructure   tdcData = event.getTDC();
        DataStructure   adcData = event.getADC();
        
        tdcData.setRows(0);
        adcData.setRows(0);
        //DataStructure tdcData = new DataStructure("bbbsbi",1200); 
        
        DataNodeCallback callback = new DataNodeCallback(){
            @Override
            public void apply(DataEvent event, int position, int[] identification) {
                if(identification[2]==57622){
                    //System.out.println("--------- decoding event TDC ----- initial = " + tdcData.getRows());
                    EvioNode node = new EvioNode(2*1024);
                    event.getAt(node, position);
                    int crate = identification[0];
                    DataDecoder.decode_57622(node,crate,tdcData);                    
                }                
                if(identification[2]==57638){
                    System.out.println("--------- decoding event ADC -----");
                    EvioNode node = new EvioNode(2*1024);
                    event.getAt(node, position);
                    int crate = identification[0];
                    DataDecoder.decode_57638(node,crate,adcData, ecalTable);
                }
            }   
        };
        
        event.getEvioEvent().setCallback(callback);
        event.getEvioEvent().scan();

        int    rows = tdcData.getRows();
        //System.out.println("rows found = " + rows);
        //System.out.printf("rows = %d\n",rows);
        int[]  haddr = new int[]{0,0,0,0};
        int[]  saddr = new int[4];
        
        //long value = DataTranslator.getHash(15,4,5,6);
        
        for(int i = 0; i < rows; i++){
            haddr[0] = tdcData.getInt(i, 1);
            haddr[1] = tdcData.getInt(i, 2);
            haddr[2] = tdcData.getInt(i, 3);
            long hash = DataTranslator.getHash(haddr);
            if(tdcTable.getMap().containsKey(hash)==true){
                long value = tdcTable.getMap().get(hash);
                DataTranslator.decodeHash(value, saddr);
                tdcData.putByte(  i, 1, (byte) saddr[0]);
                tdcData.putByte(  i, 2, (byte) saddr[1]);
                tdcData.putShort( i, 3, (short) saddr[2]);
                tdcData.putByte(  i, 4, (byte) saddr[3]);
            } else {
                System.out.printf(" error : failed to find the key %016X\n",hash);
            }
        }
        //DataStructureUtils.print(tdcData);//, new int[]{0,2,4,5});
        //System.out.println(tdcData);
        //tdcData.show();        
    }               
}
