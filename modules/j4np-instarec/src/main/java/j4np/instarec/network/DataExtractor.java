/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.network;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.instarec.core.InstaRecNetworks;
import j4np.instarec.core.TrackConstructor;
import j4np.instarec.core.TrackConstructor.CombinationCuts;
import j4np.instarec.core.TrackFinderNetwork.TrackBuffer;
import j4np.instarec.core.TrackFinderUtils;
import j4np.instarec.core.Tracks;
import j4np.physics.Vector3;
import j4np.utils.io.DataArrayUtils;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import twig.data.Axis;
import twig.data.H1F;

/**
 *
 * @author gavalian
 */
public class DataExtractor {
    
    public static record DataPair (float[] input, float[] output){}
   
    public static Schema getSchema(){
        Schema.SchemaBuilder schemaBuilder = new Schema.SchemaBuilder("mltr::segments",32100,1);        
        //schema.parse("5I4F3BLL");
        schemaBuilder.addEntry(            "id", "S", "particle id");
        schemaBuilder.addEntry(        "sector", "S", "x-component of momentum");
        schemaBuilder.addEntry(    "superlayer", "S", "y-component of momentum");
        schemaBuilder.addEntry(        "wireL1", "F", "z-component of momentum");
        schemaBuilder.addEntry(        "wireL6", "F", "particle charge");
        schemaBuilder.addEntry(        "status", "S", "particle charge");
        return schemaBuilder.build();
    }
    
    public static Bank getBank(CompositeNode node, Schema sch){
        Bank b = new Bank(sch,node.getRows());
        for(int i = 0; i < b.getRows();i++){
            b.putShort("id", i, (short) node.getInt(0, i));
            b.putShort("sector", i, (short) node.getInt(1, i));
            b.putShort("superlayer", i, (short) node.getInt(2, i));
            b.putFloat("wireL1", i, (float) node.getDouble(3, i));
            b.putFloat("wireL6", i, (float) node.getDouble(4, i));
            b.putShort("status", i, (short) 1);
        }
        return b;
    }
    
    public static void getClusters(CompositeNode node, Bank clusters, int sector){
        ///Map<Integer,Integer> link = clusters.getMap(column)
        node.setRows(0);
        for(int i = 0; i < clusters.getRows(); i++){
            int s = clusters.getInt("sector", i);
            if(s==sector){
                int row = node.getRows();
                if(row<2000){
                    node.putInt(  0, row, clusters.getInt("id", i));
                    node.putInt(  1, row, clusters.getInt("sector", i));
                    node.putInt(  2, row, clusters.getInt("superlayer", i));
                    node.putFloat(3, row, clusters.getFloat("wireL1", i));
                    node.putFloat(4, row, clusters.getFloat("wireL6", i));
                    node.setRows(row+1);
                }
            }
        }
    }
    
    public static void getTracks(Tracks tracks, Bank trkg, Bank clusters){
        
        tracks.dataNode().setRows(0);
        int index = trkg.getSchema().getEntryOrder("Cluster1_ID");
        int[] ids = new int[6];
        Map<Integer,Integer> map = clusters.getMap("id");
        int nodeRows = 0;
        for(int row = 0; row < trkg.getRows(); row++){       
            double chi2 = trkg.getFloat("chi2", row);
            double vz = trkg.getFloat("Vtx0_z", row);
            if(chi2<500&&vz>-15&&vz<5&&chi2<500){
                for(int i = 0; i < ids.length; i++) ids[i] = trkg.getInt(index+i, row);
                tracks.dataNode().setRows(nodeRows+1);                
                tracks.dataNode().putShort(2, nodeRows, (short)trkg.getInt("sector", row));
                tracks.dataNode().putShort(3, nodeRows, (short) trkg.getInt("q", row));
                tracks.dataNode().putFloat(4, nodeRows, trkg.getFloat("chi2", row));
                tracks.dataNode().putFloat(5, nodeRows, trkg.getFloat("p0_x", row));
                tracks.dataNode().putFloat(6, nodeRows, trkg.getFloat("p0_y", row));
                tracks.dataNode().putFloat(7, nodeRows, trkg.getFloat("p0_z", row));
                tracks.dataNode().putFloat(8, nodeRows, trkg.getFloat("Vtx0_x", row));
                tracks.dataNode().putFloat(9, nodeRows, trkg.getFloat("Vtx0_y", row));
                tracks.dataNode().putFloat(10, nodeRows, trkg.getFloat("Vtx0_z", row));
                                
                for(int i = 0 ; i < ids.length; i++){
                    tracks.dataNode().putInt(  11+i,   nodeRows,  ids[i]);
                                        
                    if(map.containsKey(ids[i])){
                        tracks.dataNode().putFloat(17+i, nodeRows, clusters.getFloat("wireL1", map.get(ids[i])));
                        tracks.dataNode().putFloat(23+i, nodeRows, clusters.getFloat("wireL6", map.get(ids[i])));                
                    } else {
                        tracks.dataNode().putFloat(17+i, nodeRows, 0.0f);
                        tracks.dataNode().putFloat(23+i, nodeRows, 0.0f);
                    }
                }
                nodeRows++;
            }
        }
    }
    
    
    public static void debug(String file, int tag){
        HipoReader r = new HipoReader(file,tag);        
        Tracks tr = new Tracks(120);        
        Event  ev = new Event();
        Vector3 vec = new Vector3();
        
        for(int i = 0; i < 150; i++){
            r.nextEvent(ev);
            ev.read(tr.dataNode());
            tr.show();
            System.out.println(" Similarity = " + tr.contains(0, 1) + "   distance = " + tr.distance(0, 1));
            //Vector3 v = tr.getVector(0);
            tr.vector(vec, 0);
            float[] output = tr.getVectorOutput(vec, 0);
            System.out.println(" OUTPUT : " + Arrays.toString(output));
            System.out.println(" VECTOR : " + vec);
            Vector3 v = tr.getVector(0, output);
            System.out.println(" VECTOR (D) : " + v);
        }
    }
    
    public static void extract(List<String> files, int max, int mode){
        
        HipoWriter[] w = new HipoWriter[2];
        
        w[0] = new HipoWriter();
        w[1] = new HipoWriter();
        
        w[0].open("ml_1.h5");
        w[1].open("ml_2.h5");
        
        CompositeNode nodec= new CompositeNode(32000,2,"3i2f",2024);
        
        for(String file : files){
            
            HipoReader r = new HipoReader(file);
            String[] data = null;
            if(mode >0) data = new String[]{"TimeBasedTrkg::TBTracks","TimeBasedTrkg::TBClusters"};
            if(mode==0) data = new String[]{"TimeBasedTrkg::TBTracks","HitBasedTrkg::Clusters"};
            
            Bank[] b = r.getBanks(data);
            
            Axis momentum = new Axis(20,0.0,10);
            H1F   uniform = new H1F("uniform",40,0,10);
            
            Vector3 vec = new Vector3();        
            Tracks t = new Tracks(10000);
            long channel = 0L;
            Event event = new Event();
            //for(int i = 0; i < 1500; i++){
            while(r.nextEvent(b)==true){
                //r.nextEvent(b);
                DataExtractor.getTracks(t, b[0], b[1]);
                //System.out.println("event");
                if(t.getRows()==2||t.getRows()==1){
                    //t.show();
                    t.vector(vec, 0);
                    DataExtractor.getClusters(nodec, b[1], t.sector(0));
                    int bin = momentum.getBin(vec.mag());
                    if(bin>=0){
                        if(t.charge(0)>0) bin += 20;
                        uniform.incrementBinContent(bin);
                        channel++;
                        if(uniform.getBinContent(bin)<max){
                            int wf = (int) (channel%2);
                            event.reset();
                            event.setEventTag(bin+1);
                            event.write(t.dataNode());
                            event.write(nodec);
                            w[wf].addEvent(event);
                        }
                    }
                }
            }
        }
        w[0].close();
        w[1].close();
    }
    
    public static List<float[]> loadFalse(String file, int tag, int max){
        List<float[]> data = new ArrayList<>();
        
        HipoReader  r = new HipoReader(file,tag);
        Tracks     tr = new Tracks(128);
        Tracks    trc = new Tracks(1250);
        Event      ev = new Event();

        CombinationCuts cuts = new CombinationCuts() {
            @Override
            public boolean validate(double m1, double m2, double m3, double m4, double m5, double m6) {
                if(Math.abs(m1-m2)>12.0) return false;
                if(Math.abs(m3-m4)>12.0) return false;
                if(Math.abs(m5-m6)>12.0) return false;
                return true;
            }        
        };
        TrackConstructor tc = new TrackConstructor();
        
        CompositeNode nodec = new CompositeNode(32000,2,"3i2f",2048);
        int counter = 0;
        int[] clusters  = new int[6];
        float[] features = new float[12];
        while(r.next(ev)==true&&counter<max){
            counter++;
            ev.read(tr.dataNode());
            ev.read(nodec);
            //System.out.println(" new event ********");
            if(tr.size()>1){
                int sector = tr.sector(0);
                tc.reset();
                for(int k = 0; k < nodec.getRows(); k++)
                    tc.add(nodec.getInt(1, k),nodec.getInt(2, k), nodec.getInt(0, k), 
                            nodec.getDouble(3, k), nodec.getDouble(4, k));
                tc.sectors[sector-1].create(trc, sector, cuts);
                
                tr.getClusters(clusters, 0);
                tr.getInput12(features, 0);
                if(trc.getRows()<120){
                    
                    
                    int order = 0; 
                    for(int i = 0; i < trc.getRows(); i++){
                        
                        int cont = trc.contains(i, clusters);              
                        double distance = trc.distance(i, features);
                        if((cont==5||cont==4)&&distance>2){                            
                            float[] ff = new float[12];
                            trc.getInput12(ff, i); data.add(ff);
                            //System.out.printf(" row = %4d %4d contains = %4d %f\n",i, order, cont, distance);
                            
                            order++;
                            
                        }
                        if(order>=3) break;                        
                        //nodec.print();
                        //trc.show();
                    }

                }
            }
        }
        return data;
    }
    /**
     * Loading data from file for AI training. 
     * @param file
     * @param tag
     * @param max
     * @return 
     */
    public static List<float[]> load(String file, int tag, int max, boolean regression){
        HipoReader r = new HipoReader(file,tag);
        List<float[]> dataList = new ArrayList<>();
        
        Tracks tr = new Tracks(120);        
        Event  ev = new Event();
        int counter = 0;
        
        while(r.next(ev)==true&&counter<max){
            counter++;
            ev.read(tr.dataNode());
            if(tr.getRows()>0&&tr.count(0)==6){
                float[] data = new float[12];
                //System.out.println(" count = " + tr.count(0));
                //for(int i = 0; i < data.length; i++) data[i] = (float) (tr.dataNode().getDouble(17+i, 0)/112.);
                tr.getInput12(data, 0);
                if(regression==true){
                    float[] datar = new float[18];
                    for(int i = 0; i < data.length; i++) datar[i] = data[i];
                    Vector3 v = new Vector3();
                    tr.vector(v, 0);
                    float[] reg = tr.getVectorOutput(v, 0);
                    for(int i = 12; i < 15; i++) datar[i] = reg[i-12];
                    datar[15] = tr.sector(0);
                    datar[16] = tr.charge(0);
                    tr.vertex(v, 0);
                    datar[17] = (float) ((v.z() + 15)/20);
                    dataList.add(datar);
                } else {
                    dataList.add(data);
                }
            }
        }
        return dataList;
    }
    
    public static List<DataPair> loadDataTrue(String file, int tag, int max){
        HipoReader r = new HipoReader(file,tag);        
        List<DataPair> dataList = new ArrayList<>();  
        Tracks  tr = new Tracks(120);
         Event  ev = new Event();
        int counter = 0;
        
        while(r.next(ev)==true&&counter<max){

            if(counter>max) break;
            ev.read(tr.dataNode());
            if(tr.getRows()>0&&tr.count(0)==6){
                counter++;
                float[] ft = new float[12];
                tr.getInput12(ft, 0);
                if(tag<=20) dataList.add(new DataPair(ft,new float[]{0.0f,1.0f,0.0f}));
                else dataList.add(new DataPair(ft,new float[]{0.0f,0.0f,1.0f}));
            }
        }
        return dataList;
    }
    
    public static List<DataPair> loadData(String file, int tag, int max){
        
        HipoReader r = new HipoReader(file,tag);
        
        List<DataPair> dataList = new ArrayList<>();  
        
        
        Tracks  tr = new Tracks(120);
        TrackBuffer buffer = new TrackBuffer();
        int[] clusters = new int[6];
        
        CompositeNode nodec = new CompositeNode(32000,2,"3i2f",2048);
        
        Event  ev = new Event();
        int counter = 0;
        
        while(r.next(ev)==true&&counter<max){
            
            counter++;
            ev.read(tr.dataNode());
            ev.read(nodec);
            
            if(tr.getRows()>0&&tr.count(0)==6){
                int sector = tr.sector(0);
                int charge = tr.charge(0);
                
                buffer.constructor.reset();
                for(int i = 0; i < nodec.getRows();i++) 
                    buffer.constructor.add(nodec.getInt(1, i), nodec.getInt(2, i) , 
                            nodec.getInt(0, i),nodec.getDouble(3, i),nodec.getDouble(4, i));
                
                buffer.constructor.sectors[sector-1].create(buffer.tracks, sector);
                //nodec.print();
                //tr.show();

                //buffer.tracks.show();
                tr.getClusters(clusters, 0);
                for(int j = 0; j < buffer.tracks.getRows(); j++){
                    double distance = Tracks.distance(tr, 0, buffer.tracks,j);
                    int    count = buffer.tracks.contains(j, clusters);
                    if(distance>2.0&&(count==4||count==5||count==3)){ 
                        //System.out.println("*******************");
                        //tr.show(0);
                        //buffer.tracks.show(j);
                        //System.out.println(" distance = " + distance + " count = " + count);
                        float[] ft = new float[12];
                        float[] ff = new float[12];
                        tr.getInput12(ft, 0);
                        buffer.tracks.getInput12(ff, j);
                        /*
                        if(charge>0){
                            dataList.add(new DataPair(ft,new float[]{0.0f,0.0f,1.0f}));
                        } else {
                            dataList.add(new DataPair(ft,new float[]{0.0f,1.0f,0.0f}));
                        }*/
                        dataList.add(new DataPair(ff,new float[]{1.0f,0.0f,0.0f}));
                    }
                }
                float[] data = new float[12];
                //System.out.println(" count = " + tr.count(0));
                //for(int i = 0; i < data.length; i++) data[i] = (float) (tr.dataNode().getDouble(17+i, 0)/112.);                                
            }
        }
        return dataList;
    }
    
    public static List<float[]> load(String file, int max, boolean regression){
        List<float[]> dataList = new ArrayList<>();
        for(int i = 1; i <= 40; i++){
            List<float[]> entry = DataExtractor.load(file, i, max,regression);
            dataList.addAll(entry);
        }
        return dataList;
    }
    
    public static void loadFalse(String file, int tag, String network, int run, int max){
        
        HipoReader r = new HipoReader(file,tag);
        Tracks    dataTracks = new Tracks(120);
        Tracks    tmp = new Tracks(120000);
        Tracks   aitracks = new Tracks(1200);
        InstaRecNetworks net = new InstaRecNetworks("etc/networks/clas12default.network",0);
        CompositeNode node = new CompositeNode(12,1,"ff",450);
        
        Schema sch = DataExtractor.getSchema();
        TrackBuffer buffer = new TrackBuffer();
        
        Event event = new Event();
        int counter = 0;
        
        while(r.hasNext()){
            r.next(event);
            event.read(dataTracks.dataNode());
            event.read(node, 32000,2);
            
            if(dataTracks.getRows()>0&&dataTracks.count(0)==6){
                
                int sector = dataTracks.sector(0);
                
                Bank b = DataExtractor.getBank(node, sch);//node.print();
                TrackFinderUtils.fillConstructor(buffer.constructor, b);
                aitracks.dataNode().setRows(0);
                tmp.dataNode().setRows(0);
                buffer.constructor.sectors[sector-1].create(tmp, sector);
                TrackFinderUtils.evaluate(net, tmp);
                TrackFinderUtils.copyFromTo(tmp, aitracks);
                                
                if(aitracks.getRows()>1){
                    
                    System.out.println(" event  " + tmp.getRows() + "   " + aitracks.getRows());                
                    b.show();
                    dataTracks.show();
                    aitracks.show();    
                    for(int jj = 0 ; jj < aitracks.getRows(); jj++){
                        double distance = Tracks.distance(dataTracks, 0, aitracks, jj);
                        System.out.printf(" %d : %f \n",jj,distance);
                    }
                }
            }
            counter++; if(counter>max) break;
        }
        
    }
    public static List<String> convert(List<DataPair> pairs){
        List<String> lines = new ArrayList<>();
        for(DataPair p : pairs)
            lines.add(DataArrayUtils.floatToString(p.input, ",")+ DataArrayUtils.floatToString(p.output, ","));
        return lines;
    }
    
    public static List<DataPair> generateFalse(List<DataPair> original){
        List<DataPair> gen = new ArrayList<>();
        Random r = new Random();
        for(int i = 0; i < original.size(); i++){
            float[] datat = new float[12];
            float[] datas = new float[12];
            int which = r.nextInt(original.size());
            System.arraycopy(original.get(i).input, 0, datat, 0, datat.length);
            System.arraycopy(original.get(which).input, 0, datas, 0, datas.length);
            int howMany = r.nextInt(3)+1;
            for(int k = 0; k < howMany; k++){
                int order = r.nextInt(6);
                datat[2*order] = datas[2*order]; datat[2*order+1] = datas[2*order+1];
            }
            gen.add(new DataPair(datat,new float[]{1.0f,0.0f,0.0f}));
            //System.out.println("*********");
            //System.out.println( Arrays.toString(datat));
            //System.out.println( Arrays.toString(datas));
            
        }
        return gen;
    }
    
    public static void main(String[] args){

        String file = "../ml_data_1.hipo";
        
       // --- DataExtractor.loadFalse(file, 1,"",2,24000);
        
       TextFileWriter w = new TextFileWriter("data.csv");
       
       for(int i = 0; i < 40; i++){
           List<DataPair>  pairs = DataExtractor.loadDataTrue(file,i+1,12000);           
           List<DataPair>  pairsf = DataExtractor.generateFalse(pairs);
           pairs.addAll(pairsf);
           Collections.shuffle(pairs);
           
           List<String> lines = DataExtractor.convert(pairs);
           for(String l : lines){ w.writeString(l);}

           
           /*    List<DataPair>  pairs = DataExtractor.loadData(file,i+1,2200);
           List<DataPair> pairs2 = DataExtractor.loadData(file,i+21,2200);
           
           System.out.println(" tag = " + i + "  pairs = " + pairs.size());
           //System.out.println(" data size = " + pairs.size());
       
           List<String> lines = DataExtractor.convert(pairs);
           for(String l : lines){ w.writeString(l);}
           List<String> lines2 = DataExtractor.convert(pairs2);
           for(String l : lines2){ w.writeString(l);}*/
       }
       
       w.close();
        /*
        if(args.length>0){
            List<String> files = Arrays.asList(args);
            Collections.sort(files);
            for(String file : files) System.out.println("===>>> " + file);
            DataExtractor.extract(files, 75000,0);
        }*/
        
        
        //List<float[]> data = DataExtractor.loadFalse(file, 4, 4500);
        
        /*
        TextFileWriter w = new TextFileWriter("output.csv");

        for(int bin = 1; bin <=40; bin++){
            List<float[]> data = DataExtractor.load(file, bin, 35000, true); 
            for(float[] d : data){
                //System.out.println(DataArrayUtils.floatToString(d, ","));
                w.writeString(DataArrayUtils.floatToString(d, ","));
            }
        }
        w.close();*/
    }
}
