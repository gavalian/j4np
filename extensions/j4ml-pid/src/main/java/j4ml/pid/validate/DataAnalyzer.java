/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.pid.validate;

import j4ml.deepnetts.ejml.EJMLModelEvaluator;
import j4ml.pid.data.DataProvider;
import j4ml.pid.data.DetectorResponse;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.physics.LorentzVector;
import j4np.physics.Particle;
import java.util.ArrayList;
import java.util.List;
import twig.data.H1F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class DataAnalyzer {
    
    Bank caloRec = null;
    Bank caloMom = null;
    Bank caloCal = null;
    Bank partRec = null;
    
    EJMLModelEvaluator model = null;
    
    public DataAnalyzer(HipoReader r){
        caloRec = r.getBank("REC::Calorimeter");
        caloMom = r.getBank("ECAL::moments");
        caloCal = r.getBank("ECAL::calib");
        partRec = r.getBank("REC::Particle");
        model = new EJMLModelEvaluator("pidClassifier.network");
    }
    
    public DataAnalyzer(HipoReader r, String nfile){
        caloRec = r.getBank("REC::Calorimeter");
        caloMom = r.getBank("ECAL::moments");
        caloCal = r.getBank("ECAL::calib");
        partRec = r.getBank("REC::Particle");
        model = new EJMLModelEvaluator(nfile);
    }
    
    public List<DetectorResponse> getResponses(int[] index, double[] mom, Event event){
        List<DetectorResponse> list = new ArrayList<>();
        event.read(caloRec);
        event.read(caloMom);
        event.read(caloCal);
        for(int i = 0; i < index.length; i++) {
            DetectorResponse res = new DetectorResponse();
            res.setIndex(index[i]);
            res.read(caloRec, caloMom, caloCal, mom[i]);
            list.add(res);
        }

        return list;
    }
    
    public double evaluate(DetectorResponse r){
        float[] inputRaw = r.features();
        float[] input = inputRaw;//DataProvider.getNormalizer().normalize(inputRaw);
        float[] output = new float[2];
        model.feedForwardSoftmax(input, output);
        return output[1];
    }
    
    public List<Particle> getParticles(Event event, List<Integer> index){
        event.read(partRec);
        List<Particle> part = new ArrayList<>();
        for(int i = 0; i < index.size(); i++){
            int   pid = partRec.getInt("pid", index.get(i));
            double px = partRec.getFloat("px", index.get(i));
            double py = partRec.getFloat("py", index.get(i));
            double pz = partRec.getFloat("pz", index.get(i));
            Particle p = Particle.withPid(pid, px, py, pz);
            part.add(p);
        }
        return part;
    }
    
    public List<Integer> getNegativeIndex(Event event){
        event.read(partRec);
        List<Integer> index = new ArrayList<>();
        for(int i = 0; i < partRec.getRows(); i++){
            int charge = partRec.getInt("charge", i);
            if(charge<0) index.add(i);
        }
        return index;        
    }
    
    public List<Integer> getPositiveIndex(Event event){
        event.read(partRec);
        List<Integer> index = new ArrayList<>();
        for(int i = 0; i < partRec.getRows(); i++){
            int charge = partRec.getInt("charge", i);
            if(charge>0) index.add(i);
        }
        return index;        
    }
    
    public LorentzVector getMass(List<Particle> nPart, List<Particle> pPart, int index){
         LorentzVector n0v = nPart.get(0).vector();
         LorentzVector n1v = nPart.get(1).vector();
         LorentzVector p0v = pPart.get(0).vector();
            
        if(index==0){
            LorentzVector  vMx = LorentzVector.withPxPyPzM(0.0, 0.0, 10.5, 0.0005);
            vMx.add(0.0, 0.0, 0.0, 0.938)
                    .sub(n0v.px(),n0v.py(),n0v.pz(),0.0005)
                    .sub(n1v.px(),n1v.py(),n1v.pz(),0.139)
                    .sub(p0v.px(),p0v.py(),p0v.pz(),0.139);
            return vMx;
        }
        
        LorentzVector  vMx = LorentzVector.withPxPyPzM(0.0, 0.0, 10.5, 0.0005);
            vMx.add(0.0, 0.0, 0.0, 0.938)
                    .sub(n0v.px(),n0v.py(),n0v.pz(),0.139)
                    .sub(n1v.px(),n1v.py(),n1v.pz(),0.0005)
                    .sub(p0v.px(),p0v.py(),p0v.pz(),0.139);
        return vMx;
    }
    
    
    public static void process28(String file){
        HipoReader r = new HipoReader(file);
        
        DataAnalyzer ana = new DataAnalyzer(r);
        Event event = new Event();
        
        H1F      hc = new H1F("hc",120,0.5,1.6);
        H1F      hd = new H1F("hd",120,0.5,1.6);
        H1F      hr = new H1F("hr",120,0.5,1.6);
        H1F      he = new H1F("he",120,0.5,1.6);
        
        TGCanvas  c = new TGCanvas(400,800);
        hd.attr().setLineColor(2);
        c.view().divide(1, 3);
        c.view().cd(0).region().draw(hc).draw(hd,"same");
        c.view().cd(1).region().draw(he);
        c.view().cd(2).region().draw(hr);
        
        while(r.hasNext()==true){
        //for(int i = 0; i < 10000; i++){
            r.nextEvent(event);
            
            List<Integer> negIndex = ana.getNegativeIndex(event);
            List<Integer> posIndex = ana.getPositiveIndex(event);
            //System.out.printf("%4d %4d \n",negIndex.size(),posIndex.size());
            if(negIndex.size()==2&&posIndex.size()==1){
                List<Particle> pneg = ana.getParticles(event, negIndex);
                List<Particle> ppos = ana.getParticles(event, posIndex);
                
                if(pneg.get(0).pid()==11){
                    LorentzVector  vMx = ana.getMass(pneg, ppos, 1);
                            
                            /*LorentzVector.withPxPyPzM(0.0, 0.0, 10.5, 0.0005);                    
                    vMx.add(0.0, 0.0, 0.0, 0.938)
                            .sub(pneg.get(0).vector())
                            .sub(pneg.get(1).vector())
                            .sub(ppos.get(0).vector());*/
                    
                    int[]    index = new int[]{negIndex.get(0),negIndex.get(1),posIndex.get(0)};
                    double[]   mom = new double[]{
                        pneg.get(0).vector().p(), pneg.get(1).vector().p(),
                        ppos.get(0).vector().p()};
                    List<DetectorResponse>  detectors = ana.getResponses(index, mom, event);
                    if(detectors.get(0).count()>20){                        
                        hc.fill(vMx.mass());hd.fill(vMx.mass());
                        hr.fill(vMx.mass());
                    }
                    
                } else {
                    
                    int[]    index = new int[]{negIndex.get(0),negIndex.get(1),posIndex.get(0)};
                    double[]   mom = new double[]{
                        pneg.get(0).vector().p(), pneg.get(1).vector().p(),
                        ppos.get(0).vector().p()};

                    List<DetectorResponse>  detectors = ana.getResponses(index, mom, event);
                    
                    if(detectors.get(1).count()>20){
                        System.out.printf("[0] >>>>> %5d (%.5f) \n",
                                detectors.get(1).count(),ana.evaluate(detectors.get(1)));
                        double prob = ana.evaluate(detectors.get(1));
                        if(prob>=0.0){
                            LorentzVector vec = ana.getMass(pneg, ppos, 1);
                            hd.fill(vec.mass()); he.fill(vec.mass()); 
                            hr.fill(vec.mass());
                        }
                        
                    }
                    
                    if(detectors.get(0).count()>20){
                        System.out.printf("[0] >>>>> %5d (%.5f) \n",
                                detectors.get(0).count(),ana.evaluate(detectors.get(0)));
                        double prob = ana.evaluate(detectors.get(0));
                        if(prob>=0.0){
                            LorentzVector vec = ana.getMass(pneg, ppos, 0);
                            hd.fill(vec.mass()); he.fill(vec.mass());
                        }
                        
                    } else {
                        if(detectors.get(1).count()>20){
                            System.out.printf("[1] >>>>> %5d (%.5f) \n",
                                    detectors.get(1).count(),ana.evaluate(detectors.get(1)));
                        }
                    
                    }
                    //System.out.println(detectors.get(0).getString());
                    //System.out.println(detectors.get(1).getString());
                }
                
                
            }
            
        }
        hr.divide(hc);
        
    }
    public static void process19(String file){
        HipoReader r = new HipoReader(file);
        
        DataAnalyzer ana = new DataAnalyzer(r,"pidClassifier19.network");
        Event event = new Event();
        H1F      hc = new H1F("hc",120,0.5,1.5);
        H1F      hd = new H1F("hc",120,0.5,1.5);
        H1F      he = new H1F("he",120,0.5,1.5);
        
        TGCanvas  c = new TGCanvas();
        hd.attr().setLineColor(2);
        c.view().divide(1, 2);
        c.view().cd(0).region().draw(hc).draw(hd,"same");
        c.view().cd(1).region().draw(he);
        while(r.hasNext()==true){
        //for(int i = 0; i < 10000; i++){
            r.nextEvent(event);
            
            List<Integer> negIndex = ana.getNegativeIndex(event);
            List<Integer> posIndex = ana.getPositiveIndex(event);
            //System.out.printf("%4d %4d \n",negIndex.size(),posIndex.size());
            if(negIndex.size()==2&&posIndex.size()==1){
                List<Particle> pneg = ana.getParticles(event, negIndex);
                List<Particle> ppos = ana.getParticles(event, posIndex);
                
                if(pneg.get(0).pid()==11){
                    LorentzVector  vMx = LorentzVector.withPxPyPzM(0.0, 0.0, 10.5, 0.0005);                    
                    vMx.add(0.0, 0.0, 0.0, 0.938)
                            .sub(pneg.get(0).vector())
                            .sub(pneg.get(1).vector())
                            .sub(ppos.get(0).vector());
                    int[]    index = new int[]{negIndex.get(0),negIndex.get(1),posIndex.get(0)};
                    double[]   mom = new double[]{
                        pneg.get(0).vector().p(), pneg.get(1).vector().p(),
                        ppos.get(0).vector().p()};
                    List<DetectorResponse>  detectors = ana.getResponses(index, mom, event);
                    if(detectors.get(0).count()>15&&detectors.get(0).count()<25){
                        hc.fill(vMx.mass());hd.fill(vMx.mass());
                    }
                    
                } else {
                    
                    int[]    index = new int[]{negIndex.get(0),negIndex.get(1),posIndex.get(0)};
                    double[]   mom = new double[]{
                        pneg.get(0).vector().p(), pneg.get(1).vector().p(),
                        ppos.get(0).vector().p()};

                    List<DetectorResponse>  detectors = ana.getResponses(index, mom, event);
                    
                    if(detectors.get(1).count()>17&&detectors.get(1).count()<20){
                        System.out.printf("[0] >>>>> %5d (%.5f) \n",
                                detectors.get(1).count(),ana.evaluate(detectors.get(1)));
                        double prob = ana.evaluate(detectors.get(1));
                        if(prob>0.5){
                            LorentzVector vec = ana.getMass(pneg, ppos, 1);
                            hd.fill(vec.mass()); he.fill(vec.mass());
                        }
                        
                    }
                    
                    if(detectors.get(0).count()>16&&detectors.get(0).count()<20){
                        System.out.printf("[0] >>>>> %5d (%.5f) \n",
                                detectors.get(0).count(),ana.evaluate(detectors.get(0)));
                        double prob = ana.evaluate(detectors.get(0));
                        if(prob>0.5){
                            LorentzVector vec = ana.getMass(pneg, ppos, 0);
                            hd.fill(vec.mass()); he.fill(vec.mass());
                        }
                        
                    } else {
                        if(detectors.get(1).count()>20){
                            System.out.printf("[1] >>>>> %5d (%.5f) \n",
                                    detectors.get(1).count(),ana.evaluate(detectors.get(1)));
                        }
                    
                    }
                    //System.out.println(detectors.get(0).getString());
                    //System.out.println(detectors.get(1).getString());
                }
                
                
            }
            
        }
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/dataspace/pid/rec_output_filtered_8.hipo";
        
        DataAnalyzer.process28(file);
        DataAnalyzer.process19(file);
        
    }
}
