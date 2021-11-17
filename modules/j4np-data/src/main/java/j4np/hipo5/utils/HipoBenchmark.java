/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.utils;

import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class HipoBenchmark {
    public static HipoWriter benchmarkWriter(String file, int cycle, int compression){
        
        HipoReader reader = new HipoReader(file);
        HipoWriter.checkFile("writer_bench.hipo", true);
        HipoWriter writer = new HipoWriter("writer_bench.hipo",
                reader.getSchemaFactory());
        writer.setCompressionType(compression);
        Event event = new Event();
        for(int loop = 0; loop < cycle; loop++){
            reader.rewind();
            while(reader.hasNext()==true){
                reader.nextEvent(event);
                writer.addEvent(event);
            }
        }
        writer.close();
        writer.stats();
        return writer;
    }
    
    public static String makeSummary(String name, long time, long bytes){
        double timeMS = ((double)time)/1_000_000;
        double timeSEC = timeMS/1_000;
        double timePerByte = timeMS/bytes;
        int    bytesPerSec = (int) (bytes/timeSEC);
        return String.format("%18s : bytes %14d, time %12.5f , %12d bytes/sec", 
                name, bytes,timeSEC,bytesPerSec);
    }
    
    public static void main(String[] args){
        String[] algo = new String[]{"no-compression","lz4-fast","lz4-best","gzip"};
        List<String> stats = new ArrayList<>();
        
        for(int i = 0; i < 4; i++){
            HipoWriter w = HipoBenchmark.benchmarkWriter(
                    "/Users/gavalian/Downloads/sidis_sample_14.hipo", 4,i);
            stats.add(HipoBenchmark.makeSummary(algo[i], w.getWriterTime(), w.getBytesWritten()));
        }
        
        System.out.println("\n");        
        for(String s : stats){System.out.println(s);}
        System.out.println("\n");
    }
}
