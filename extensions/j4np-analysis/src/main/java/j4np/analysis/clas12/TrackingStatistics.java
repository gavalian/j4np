/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.analysis.clas12;

import j4np.data.base.DataWorker;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.io.HipoReader;

/**
 *
 * @author gavalian
 */
public  class TrackingStatistics extends DataWorker<HipoReader,Event> {
    private   Schema    schemaHB = null;
    private   Schema    schemaTB = null;
    private   String    bankNameHB = "HitBasedTrkg::HBClusters";
    private   String    bankNameTB = "TimeBasedTrkg::TBClusters";
    private   Bank      bankHB = null;
    private   Bank      bankTB = null;
    
    private   int[]     hbCL = new int[6];
    private   int[]     hbCLTR = new int[6];
    private   int[]     tbCL = new int[6];
    private   int[]     tbCLTR = new int[6];
    
    private   int       eventsProcessed = 0;
    
    @Override
    public boolean init(HipoReader src) {
        schemaHB = src.getSchemaFactory().getSchema(bankNameHB);
        schemaTB = src.getSchemaFactory().getSchema(bankNameTB);
        return true;
    }

    @Override
    public void execute(Event e) {
        Bank bhb = new Bank(schemaHB,40);
        Bank btb = new Bank(schemaTB,40);
        
        e.read(bhb);
        e.read(btb);
        eventsProcessed++;
        int nrows = bhb.getRows();
        for(int r = 0; r < nrows; r++){
            int sector = bhb.getInt("sector", r);
            if(sector>=1&&sector<7) hbCL[sector-1]++;            
        }
        
        nrows = btb.getRows();
        for(int r = 0; r < nrows; r++){
            int sector = btb.getInt("sector", r);
            if(sector>=1&&sector<7) tbCL[sector-1]++;            
        }
    }
    
    public void summary(){
        System.out.println("*********\n");
        for(int i = 0; i < 6; i++){
            System.out.printf(" %d : %12d %12d %12.4f %12.4f\n",
                    i+1,
                    hbCL[i],tbCL[i] , ((double) hbCL[i])/eventsProcessed,
                    ((double) tbCL[i])/eventsProcessed);
        }
    }
}
