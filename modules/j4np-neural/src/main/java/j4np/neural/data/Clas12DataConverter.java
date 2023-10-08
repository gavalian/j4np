/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.data;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;

/**
 *
 * @author gavalian
 */
public class Clas12DataConverter {
    
    
    public static CompositeNode getDCTDC(Bank dctdc){
        CompositeNode nodeDC = new CompositeNode( 12, 1,  "bbsbil", 4096);
        int nrowsdc = dctdc.getRows();
        if(nrowsdc<4000){
            nodeDC.setRows(nrowsdc);
            for(int row = 0; row < nrowsdc; row++){
                nodeDC.putByte(  0, row, (byte) dctdc.getInt("sector", row));
                nodeDC.putByte(  1, row, (byte) dctdc.getInt("layer", row));
                nodeDC.putShort( 2, row, (short) dctdc.getInt("component", row));
                nodeDC.putByte(  3, row, (byte) dctdc.getInt("order", row));
                nodeDC.putInt(   4, row,  dctdc.getInt("TDC", row));
            }
        }
        return nodeDC;
    }
    
    public static CompositeNode getClusters(Bank clusters){
        CompositeNode nodeCL = new CompositeNode( 32100, 1,  "3b2f", 1024);
        int nrows = clusters.getRows();
         nodeCL.setRows(nrows);
         for(int row = 0; row < nrows; row++){
             nodeCL.putByte(  0, row, (byte) (row+1));
             nodeCL.putByte(  1, row, (byte) clusters.getInt("sector", row));
             nodeCL.putByte(  2, row, (byte) clusters.getInt("superlayer", row));
             nodeCL.putFloat( 3,row, (float) clusters.getFloat("avgWire", row));
             nodeCL.putFloat( 4,row, (float) clusters.getFloat("fitSlope", row));
         }
         return nodeCL;
    }
    
    
    public static void convertClustering(String file, String output){
        HipoReader r = new HipoReader(file);
        Bank[] banks  = r.getBanks("DC::tdc","HitBasedTrkg::Clusters");
        
        Event ein = new Event();
        Event eout = new Event();
        HipoWriter w = new HipoWriter();
        w.open(output);
        
        while(r.hasNext()){
            r.next(ein);
            ein.read(banks);
            CompositeNode n1 = Clas12DataConverter.getDCTDC(banks[0]);
            CompositeNode n2 = Clas12DataConverter.getClusters(banks[1]);
            eout.reset();
            eout.write(n1);
            eout.write(n2);
            w.add(eout);
        }
        w.close();
    }
    
    public static void convertExtracted(String file, String output){
        SchemaFactory factory = new SchemaFactory();
        factory.readFile("etc/neuralnetwork.json");
        CompositeNode node = new CompositeNode(32000,1,"sss",4500);
        HipoReader r = new HipoReader();
        r.open(file);
        
        Event e = new Event();
        
        HipoWriter w = new HipoWriter();
        w.getSchemaFactory().copy(factory);
        w.open(output);
        
        while(r.hasNext()==true){
            r.nextEvent(e);
            e.read(node,32000,1);
            System.out.println("---");
            node.print();
            Bank b = factory.getBank("nnet::clusters", node.getRows()*6);
            int counter = 0;
            for(int row = 0; row < node.getRows(); row++){
                for(int s = 0; s < 6; s++){
                    b.putShort("id", counter, (short) (counter+1)); 
                    b.putShort("sector", counter, (short) node.getInt(2, row));
                    b.putShort("superlayer", counter, (short) (s+1));
                    b.putFloat("mean", counter, (float) node.getDouble(17+s, row));
                    counter++;
                }
            }
            b.show();
            e.write(b);
            w.add(e);            
        }
        
        w.close();
    }
    
    public static void convert(String inputFile, String outputFile){
    
        HipoReader r = new HipoReader(inputFile);
        HipoWriter w = HipoWriter.create(outputFile, r);
        Event e = new Event();
        
        Bank[] banks  = r.getBanks("DC::tdc","ECAL::adc");
        Bank[] tracks = r.getBanks("TimeBasedTrkg::TBClusters");
        
        CompositeNode nodeDC = new CompositeNode( 12, 1,  "bbsbil", 4096);
        CompositeNode nodeEC = new CompositeNode( 11, 2, "bbsbifs", 4096);
        
        CompositeNode nodeCL = new CompositeNode( 32100, 1,  "3b2f", 1024);
        
        while(r.hasNext()){
            
            r.nextEvent(e);
            e.read(banks);
            e.read(tracks);
            
            
            int nrowstr = tracks[0].getRows();
            nodeCL.setRows(0);
            if(nrowstr>0){
                nodeCL.setRows(nrowstr);
                for(int row = 0; row < nrowstr; row++){
                    nodeCL.putByte(  0, row, (byte) (row+1));
                    nodeCL.putByte(  1, row, (byte) tracks[0].getInt("sector", row));
                    nodeCL.putByte(  2, row, (byte) tracks[0].getInt("superlayer", row));
                    nodeCL.putFloat( 3,row, (float) tracks[0].getFloat("avgWire", row));
                    nodeCL.putFloat( 4,row, (float) tracks[0].getFloat("fitSlope", row));
                }
                e.write(nodeCL);
            }
            
            nodeEC.setRows(0);
            nodeDC.setRows(0);
            
            int nrowsec = banks[1].getRows();            
            if(nrowsec<4000){
                nodeEC.setRows(nrowsec);
                for(int row = 0; row < nrowsec; row++){
                    nodeEC.putByte(  0, row, (byte) banks[1].getInt("sector", row));
                    nodeEC.putByte(  1, row, (byte) banks[1].getInt("layer", row));
                    nodeEC.putShort( 2, row, (short) banks[1].getInt("component", row));
                    nodeEC.putByte(  3, row, (byte) banks[1].getInt("order", row));
                    nodeEC.putInt(   4, row,  banks[1].getInt("ADC", row));
                }
            } 
            
            int nrowsdc = banks[0].getRows();
            if(nrowsdc<4000){
                nodeDC.setRows(nrowsdc);
                for(int row = 0; row < nrowsdc; row++){
                    nodeDC.putByte(  0, row, (byte) banks[0].getInt("sector", row));
                    nodeDC.putByte(  1, row, (byte) banks[0].getInt("layer", row));
                    nodeDC.putShort( 2, row, (short) banks[0].getInt("component", row));
                    nodeDC.putByte(  3, row, (byte) banks[0].getInt("order", row));
                    nodeDC.putInt(   4, row,  banks[0].getInt("TDC", row));
                }
            }
            if(nodeEC.getRows()>0) e.write(nodeEC);
            if(nodeDC.getRows()>0) e.write(nodeDC);
            w.addEvent(e);                    
        }
        w.close();
    }
    
    public static void main(String[] args){
        //String file = "clas12_neural_data_va.h5";
        //Clas12DataConverter.convertExtracted(file, "output.h5");
        
        //String file = "/Users/gavalian/Work/DataSpace/decoded/005692/clas_005692.evio.00025-00029.hipo.h5";
        String file = "/Users/gavalian/Work/DataSpace/online-trakcing/rec_clas_005442.evio.00980-00984.dchb.hipo";
        Clas12DataConverter.convertClustering(file, "output_clusters_4.h5");
        
    }
}
