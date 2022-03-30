/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.regression;

import deepnetts.net.layers.activation.ActivationType;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.data.DataNormalizer;
import j4ml.deepnetts.DeepNettsClassifier;
import j4ml.deepnetts.DeepNettsEncoder;
import j4ml.extratrees.networks.ClassifierExtraTrees;
import j4np.physics.LorentzVector;
import j4np.physics.Vector3;

import java.util.List;
import java.util.stream.Collectors;
import twig.data.H1F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class RegressionKinFit {
    
    public RegressionKinFit(){
        
    }
    
    public Vector3 getVector(double[] array, int start){
        double p  = array[start]*6.0 + 0.5;
        double th = array[start+1]*40.0+5;
        double phi = array[start+2]*360.0-180.0;
        Vector3 v = new Vector3();
        v.setMagThetaPhi(p, Math.toRadians(th), Math.toRadians(phi));
        return v;
    }
    
    public LorentzVector getVectorL(double[] array, int start, double mass){
        Vector3 v = getVector(array,start);
        return LorentzVector.withPxPyPzM(v.x(),v.y(),v.z(), mass);
    }
    
    public H1F getMx(DataList pl){
        
        int rows = pl.getList().size();
        H1F h = new H1F("h",120,0.4,1.8);
        
        for(int i = 0; i < rows; i++){
            LorentzVector b = LorentzVector.withPxPyPzM(0.0, 0.0, 6.6, 0.0005);
            double[]   input = pl.getList().get(i).getFirst();
            LorentzVector ve = getVectorL(input,0,0.0005);
            LorentzVector vp = getVectorL(input,3,0.139);
            b.add(0.0, 0.0, 0.0, 0.938).sub(vp).sub(ve);
            h.fill(b.mass());
        }
        return h;
    }
    
    public H1F getMx(DataList pl, int which){
        
        int rows = pl.getList().size();
        H1F h = new H1F("h",120,0.4,1.8);
        
        for(int i = 0; i < rows; i++){
            LorentzVector b = LorentzVector.withPxPyPzM(0.0, 0.0, 6.6, 0.0005);
            double[]   input = pl.getList().get(i).getFirst();
            if(which!=0) input = pl.getList().get(i).getSecond();
            
            LorentzVector ve = getVectorL(input,0,0.0005);
            LorentzVector vp = getVectorL(input,3,0.139);
            b.add(0.0, 0.0, 0.0, 0.938).sub(vp).sub(ve);
            h.fill(b.mass());
        }
        return h;
    }
    
    public DataList filter(DataList pl){
        DataList p = new DataList();
        
        int rows = pl.getList().size();               
        for(int i = 0; i < rows; i++){
            LorentzVector b = LorentzVector.withPxPyPzM(0.0, 0.0, 6.6, 0.0005);
            double[]   input = pl.getList().get(i).getFirst();           
            LorentzVector ve = getVectorL(input,0,0.0005);
            LorentzVector vp = getVectorL(input,3,0.139);
            b.add(0.0, 0.0, 0.0, 0.938).sub(vp).sub(ve);
            if(b.mass()<1.2&&b.mass()>0.75) p.add(pl.getList().get(i));
        }
        
        return p;
    }
    
    public DataList normalize(DataList dl){
        DataNormalizer dn = new DataNormalizer(new double[]{0.5,5.0,-180.0,0.5,5.0,-180.0}, new double[]{6.5,45,180.0,6.5,45,180.0});
        //DataNormalizer dn = new DataNormalizer(new double[]{0.5,5.0,-180.0,0.5,5.0,-180.0}, new double[]{6.5,45,180.0,6.5,45,180.0});
        
        DataList.normalizeInput(dl, dn);
        DataList.normalizeOutput(dl, dn);
        //DataList pl = dl.getNormalized(new double[]{0.5,5.0,-180.0,0.5,5.0,-180.0}, new double[]{6.5,45,180.0,6.5,45,180.0});
        return dl;
    }
    
    public DataList normalizeClassifier(DataList dl){        
        DataNormalizer dn = new DataNormalizer(new double[]{0.5,5.0,-180.0,0.5,5.0,-180.0}, new double[]{6.5,45,180.0,6.5,45,180.0});
        DataList.normalizeInput(dl, dn);
        return dl;
    }
    
    
    public void train(String input, String outputNetwork, int nEpochs){
        
        //String file = "/Users/gavalian/Work/Software/project-10.4/studies/kinfit/b.csv";
        DataList dsTrain = DataList.fromCSV(input, new int[]{6,7,8,18,19,20},new int[]{3,4,5,15,16,17});
        DataList  normTrain = normalize(dsTrain);
        normTrain.shuffle();
        DataList[] train = DataList.split(normTrain, 0.7,0.3);
        
        DeepNettsEncoder encoder = new DeepNettsEncoder();
        
        encoder.init(new int[]{6,12,24,12,6,12,24,12,6}, ActivationType.LINEAR);
        encoder.train(train[0], nEpochs);
        encoder.test(train[1]);
        
        encoder.save(outputNetwork);
    }
    
    public void reactionMLP(){
        String fileS = "/Users/gavalian/Work/Software/project-10.4/studies/kinfit/tr_signal.csv";
        String fileB = "/Users/gavalian/Work/Software/project-10.4/studies/kinfit/tr_background.csv";    
        DataList dsSig = DataList.fromCSV(fileS, new int[]{6,7,8,18,19,20},new int[]{3,4,5,15,16,17});
        DataList dsBkg = DataList.fromCSV(fileB, new int[]{6,7,8,18,19,20},new int[]{3,4,5,15,16,17});
        dsSig.show();
        
        for(DataEntry dp : dsSig.getList()) dp.set(dp.getFirst(), new double[]{0.0,1.0});
        for(DataEntry dp : dsBkg.getList()) dp.set(dp.getFirst(), new double[]{1.0,0.0});
        
        DataList normSig = this.normalizeClassifier(dsSig);
        DataList normBkg = this.normalizeClassifier(dsBkg);
        
        DeepNettsClassifier mlp = new DeepNettsClassifier();
       
        mlp.init(new int[]{6,12,12,12,2});
        DataList[] sig = DataList.split(normSig, 0.7,0.3);
        DataList[] bkg = DataList.split(normBkg, 0.7,0.3);
        
        sig[0].getList().addAll(bkg[0].getList());
        sig[1].getList().addAll(bkg[1].getList());
        
        sig[0].shuffle(); sig[1].shuffle();
        
        mlp.train(sig[0], 75);
        
        mlp.test(sig[1]);
        
        mlp.save("reaction.network");
        DataList pList = mlp.evaluate(sig[1]);
        this.draw(pList);
        //dsSig.show();
        //dsBkg.show();
    }

    
    public void reaction(){
        
        String fileS = "/Users/gavalian/Work/Software/project-10.4/studies/kinfit/tr_signal.csv";
        String fileB = "/Users/gavalian/Work/Software/project-10.4/studies/kinfit/tr_background.csv";    
        DataList dsSig = DataList.fromCSV(fileS, new int[]{6,7,8,18,19,20},new int[]{3,4,5,15,16,17});
        DataList dsBkg = DataList.fromCSV(fileB, new int[]{6,7,8,18,19,20},new int[]{3,4,5,15,16,17});
        dsSig.show();
        
        for(DataEntry dp : dsSig.getList()) dp.set(dp.getFirst(), new double[]{1.0});
        for(DataEntry dp : dsBkg.getList()) dp.set(dp.getFirst(), new double[]{0.0});
        
        DataList normSig = this.normalizeClassifier(dsSig);
        DataList normBkg = this.normalizeClassifier(dsBkg);
        
        ClassifierExtraTrees et = new ClassifierExtraTrees(6,1);
        et.setK(2);
        et.setNMin(50);
        et.setNumTrees(150);
        
        DataList[] sig = DataList.split(normSig, 0.7,0.3);
        DataList[] bkg = DataList.split(normBkg, 0.7,0.3);
        
        sig[0].getList().addAll(bkg[0].getList());
        sig[1].getList().addAll(bkg[1].getList());
        
        sig[0].shuffle(); sig[1].shuffle();
        
        et.train(sig[0]);
        
        et.test(sig[1]);
       
        //dsSig.show();
        //dsBkg.show();
    }
    
    public void draw(DataList pl){
        List<DataEntry> listSig = pl.getList().stream().
                filter(e -> e.getSecond()[1]>0.5).collect(Collectors.toList());
        List<DataEntry> listBkg = pl.getList().stream().
                filter(e -> e.getSecond()[1]<0.5).collect(Collectors.toList());
        
        DataList pos = new DataList();
        DataList neg = new DataList();
        
        pos.getList().addAll(listSig);
        neg.getList().addAll(listBkg);
        
        H1F hP = this.getMx(pos);
        H1F hN = this.getMx(neg);
        H1F hA = this.getMx(pos);
        
        hA.add(hN);
        hA.attr().set("lc=1,lw=2");
        hP.attr().set("fc=3,lc=3,fs=2");
        hN.attr().set("fc=2,lc=2,fs=12");
        
        hA.attr().setLegend("raw distribution");
        hA.attr().setTitleX("M(e^-,e^-#pi^+)X [GeV]");
        
        hP.attr().setLegend("identified signal (ML) ");
        hN.attr().setLegend("identified background (ML)");
        
        TGCanvas c = new TGCanvas();
        c.view().region().draw(hA).draw(hN,"same").draw(hP,"same");
    }
    
    public static void main(String[] args){
        
        String file = "/Users/gavalian/Work/Software/project-10.4/studies/kinfit/tr_signal.csv";
        
        RegressionKinFit kFit = new RegressionKinFit();
        
//kFit.train(file, "kfit.network",250);
        kFit.reactionMLP();
        /*
        DataPairList  dsHit = DataPairList.fromCSV(file, new int[]{6,7,8,18,19,20} , new int[]{9,10,11,21,22,23});        
        DataPairList dsTime = DataPairList.fromCSV(file, new int[]{3,4,5,15,16,17} , new int[]{9,10,11,21,22,23});
        
        
         DataPairList dsTrain = DataPairList.fromCSV(file, new int[]{6,7,8,18,19,20},new int[]{3,4,5,15,16,17});
         
        RegressionKinFit kFit = new RegressionKinFit();
        dsHit.show(); 
        
        DataPairList normTime = kFit.normalize(dsTime);
        DataPairList  normHit = kFit.normalize(dsHit);
        
        DataPairList  normTrain = kFit.normalize(dsTrain);
        
        //DataPairList normHit = kFit.filter(normHitF);
        
        normHit.shuffle();
        
        DataPairList[] train = DataPairList.split(normTrain, 0.7,0.3);
        
        DeepNettsEncoder encoder = new DeepNettsEncoder();
        
        encoder.init(new int[]{6,12,24,12,6,12,24,12,6}, ActivationType.LINEAR);
        encoder.train(train[0], 6500);
        
        H1F hh = kFit.getMx(normHit,0);
        H1F ht = kFit.getMx(normHit,1);
        
        DataPairList results = encoder.evaluate(normHit);
        
        H1F hr = kFit.getMx(results,1);
        H1F htt = kFit.getMx(normTime,0);
        
        TGCanvas c = new TGCanvas(900,900);
        c.view().divide(2, 2);
        
        c.view().region(0).draw(hh);
        c.view().region(1).draw(ht);
        c.view().region(2).draw(hr);
        c.view().region(3).draw(htt);*/
        
    }
}
