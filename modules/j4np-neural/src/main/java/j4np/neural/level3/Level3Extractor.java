/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.level3;

import j4np.hipo5.data.Bank;
import j4np.hipo5.io.HipoReader;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import twig.data.AsciiPlot;
import twig.data.Axis;
import twig.data.H1F;
import twig.data.H2F;

/**
 *
 * @author gavalian
 */
public class Level3Extractor {
    public int getSector(Bank part, Bank track){
        if(part.getRows()>0&&track.getRows()>0){
            int pid = part.getInt("pid",0);
            int status = part.getInt("status",0);
            if(Math.abs(status)>=2000&&Math.abs(status)<3000&&pid==11){
                for(int i = 0; i < track.getRows();i++){
                    if(track.getInt("pindex", i)==0) return track.getInt("sector", i);
                }
            }
        }
        return -1;
    }
    
    public H2F getdc(Bank tdc, int sector){
        H2F h = new H2F("h2",112,0.5,112.5,36,0.5,36.5);
        for(int i = 0; i < tdc.getRows();i++){
            int sec = tdc.getInt("sector", i);
            if(sec==sector){
                int layer = tdc.getInt("layer", i);
                int wire = tdc.getInt("component", i);
                h.setBinContent(wire-1,layer-1, 1.0);
            }
        }
        return h;
    }
    public List<Integer> charged(Bank part){
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < part.getRows(); i++){
            if(part.getInt("charge", i)!=0) list.add(i);
        }
        return list;
    }
    
    public int getcaloindex(Bank b){
        for(int i = 0; i < b.getRows(); i++){
            
        }
        return -1;
    }
    public H1F getcalo(Bank calo, int sector, List<Integer> charged){
        H1F h = new H1F("h",108,0.5,108.5);
        Axis x = new Axis(36,0.0,450);
        
        for(int i = 0; i < calo.getRows(); i++){
            int pindex = calo.getInt("pindex",i);
            int layer  = calo.getInt("layer", i);
            if(charged.contains(pindex)&&layer==4){
                float lu = calo.getFloat("lu", i);
                float lv = calo.getFloat("lv", i);
                float lw = calo.getFloat("lw", i);
                int bu = x.getBin(lu);
                int bv = x.getBin(lv);
                int bw = x.getBin(lw);
                h.setBinContent(bu, 1);
                h.setBinContent(bv+36, 1);
                h.setBinContent(bw+72, 1.0);
            }
        }
        return h;
    }
    public static void main(String[] args){
        HipoReader r = new HipoReader("infile.h5");
        Level3Extractor l3 = new Level3Extractor();
        Bank[] banks = r.getBanks("REC::Particle","REC::Track","DC::tdc","REC::Calorimeter");
        TextFileWriter w = new TextFileWriter();
        w.open("output_l3.csv");
        
        while(r.nextEvent(banks)==true){
            int sector = l3.getSector(banks[0],banks[1]);
            if(sector>0){
                //System.out.println("sector = " + sector);
                H2F h2 = l3.getdc(banks[2], sector);
                //AsciiPlot.drawh2(h2);
                List<Integer> charged = l3.charged(banks[0]);
                H1F h1 = l3.getcalo(banks[3], sector, charged);
                //AsciiPlot.drawh1box(h1);
                
                float[] v2 = h2.getContentArrayFloat();
                float[] v1 = h1.getData();
                //System.out.println(Arrays.toString(v2).replace("[", "").replace("]", ""));
                //System.out.println(Arrays.toString(v1));
                w.writeString(
                Arrays.toString(v2).replace("[", "").replace("]", "")+"," +
                        Arrays.toString(v1).replace("[", "").replace("]", ""));
            }
            
        }        
        w.close();
    }
}
