/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.ejml;

import deepnetts.data.TabularDataSet;
import j4ml.deepnetts.CsvDataProvider;
import j4ml.deepnetts.DataProvider;
import j4ml.deepnetts.DataSetUtils;
import j4ml.deepnetts.DeepNettsClassifier;
import j4ml.ejml.EJMLModel.ModelType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.visrec.ml.data.DataSet;
import twig.data.Axis;
import twig.data.GraphErrors;
import twig.data.H1F;
import twig.data.H2F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class EJMLModelAnalysis {

    List<H1F> dataHist = new ArrayList<>();
    List<GraphErrors> efficiency = new ArrayList<>();
    
    EJMLModel    model = null;
    
    public int   defaultBinsFeature = 40;
    
    public EJMLModelAnalysis(String file){
        model = new EJMLModel(file,ModelType.SOFTMAX);
        model.printSummary();
    }
    
    protected void init(){
        dataHist.clear();
        int noutputs = model.getOutputSize();
        for(int i = 1; i <= noutputs; i++) {
            H1F h = new H1F("LABEL_"+i,120,0.0,1.0);
            h.attr().setLineColor(i+1);
            h.attr().setFillColor(150+i+1);
            dataHist.add(h);
        }
    }
    
    protected void getMetrics(DataSet data, int label, double threshold, int feature, double min, double max, double[] metrics){

        if(metrics.length!=3) {
            System.out.printf("[getMetrics] error, must provide matrics with length = 3\n");
            return;
        }        

        int totalCounter  = 0;
        int totalCounterPositive  = 0;
        int truePositive  = 0;
        int trueNegative  = 0;
        int falsePositive = 0;
        int falseNegative = 0;
        
        float[] output = new float[model.getOutputSize()];
        //System.out.printf("min %f, max=%f\n",min,max);
        Iterator iter = data.iterator();
        while(iter.hasNext()){            
            totalCounter++;
            
            TabularDataSet.Item  item = (TabularDataSet.Item) iter.next();
            float[]  input  = item.getInput().getValues();
            float[] desired = item.getTargetOutput().getValues();
            //System.out.printf(Arrays.toString(input) + " =>  " + Arrays.toString(desired));
            //System.out.printf("feature %d = %f\n",feature,input[feature]);
            if(input[feature]>min&&input[feature]<max){
                //System.out.printf("----> input value = %f\n",input[feature]);
                model.getOutput(input,output);
                
                int desiredClass = model.getClass(desired);
                int  outputClass = model.getClass(output);
                
                if(desiredClass==label){
                    totalCounterPositive++;
                    if(output[label]>threshold){
                        truePositive++;
                    } else {
                        falseNegative++;
                    }
                } else {
                    if(output[label]>threshold){
                        falsePositive++;
                } else {
                        trueNegative++;
                    }
                }
            }
        }
        
        //metrics[2] = ((double) truePositive)/totalCounterPositive;
        
        metrics[0] = ((double) truePositive)/(truePositive+falseNegative);
        metrics[1] = ((double) falsePositive)/(falsePositive+trueNegative);
        metrics[2] = ((double) trueNegative)/(falsePositive+trueNegative);
        
        /*System.out.printf("%9.4f %9d %9d %9d %9d %9.5f\n",threshold, 
                totalCounter, totalCounterPositive,truePositive,falseNegative,
                metrics[2]
                );*/
    }
    
    protected void getMetrics(DataSet data, int index, double[] metrics,double threshold){

        if(metrics.length!=3) {
            System.out.printf("[getMetrics] error, must provide matrics with length = 3\n");
            return;
        }        

        int totalCounter  = 0;
        int totalCounterPositive  = 0;
        int truePositive  = 0;
        int trueNegative  = 0;
        int falsePositive = 0;
        int falseNegative = 0;
        
        float[] output = new float[model.getOutputSize()];
        
        Iterator iter = data.iterator();
        while(iter.hasNext()){            
            totalCounter++;
            
            TabularDataSet.Item  item = (TabularDataSet.Item) iter.next();
            float[]  input  = item.getInput().getValues();
            float[] desired = item.getTargetOutput().getValues();            

            model.getOutput(input,output);
            
            int desiredClass = model.getClass(desired);
            int  outputClass = model.getClass(output);
            
            if(desiredClass==index){
                totalCounterPositive++;
                if(output[index]>threshold){
                    truePositive++;
                } else {
                    falseNegative++;
                }
            } else {
                if(output[index]>threshold){
                    falsePositive++;
                } else {
                    trueNegative++;
                }
            }
        }
        
        //metrics[2] = ((double) truePositive)/totalCounterPositive;
        
        metrics[0] = ((double) truePositive)/(truePositive+falseNegative);
        metrics[1] = ((double) falsePositive)/(falsePositive+trueNegative);
        metrics[2] = ((double) trueNegative)/(falsePositive+trueNegative);
        
        /*System.out.printf("%9.4f %9d %9d %9d %9d %9.5f\n",threshold, 
                totalCounter, totalCounterPositive,truePositive,falseNegative,
                metrics[2]
                );*/
    }
    
    public H2F rocFeature(DataSet ds, int feature, int label){
        Axis a = new Axis(defaultBinsFeature,0.0,1.0);
        Axis t = new Axis(defaultBinsFeature,0.0,1.0);
        H2F  h = new H2F("feature_rock",defaultBinsFeature,0.0,1.0,
                defaultBinsFeature,0.0,1.0
        );
        System.out.printf("----- analyzing feature %d and label = %d \n",feature, label);        
        for(int i = 0; i < a.getNBins(); i++){
            double[] metrics = new double[3];
            double min = a.getBinCenter(i)-a.getBinWidth(i)*0.5;
            double max = a.getBinCenter(i)+a.getBinWidth(i)*0.5;
            System.out.printf("----- analyzing bin %d at %.5f \n",i,a.getBinCenter(i));
            for(int k = 0; k < t.getNBins(); k++){
                this.getMetrics(ds, label, t.getBinCenter(k), feature, min, max, metrics);
                h.fill(a.getBinCenter(i), t.getBinCenter(k), metrics[0]);
                //System.out.printf("metrics = %f\n",metrics[0]);
            }
        }
        return h;
    }
    
    protected GraphErrors rocCurve(DataSet ds, int label){
        GraphErrors gr = new GraphErrors("ROC");
        gr.attr().setMarkerSize(8);        
        double[] metrics = new double[3];
        for(double x = 0.0; x < 1.0; x += 0.05){
            this.getMetrics(ds, label, metrics, x);
            gr.addPoint(x, metrics[0], 0.0, 0.0);
            gr.attr().setTitleX("Threshold");
            gr.attr().setTitleY("Efficiency");
        }
        return gr;
    }
    
    protected GraphErrors sensitivityCurve(DataSet ds, int label){
        GraphErrors gr = new GraphErrors("ROC");
        gr.attr().setMarkerSize(8);        
        double[] metrics = new double[3];
        for(double x = 0.0; x < 1.0; x += 0.05){
            this.getMetrics(ds, label, metrics, x);
            gr.addPoint(x, metrics[1], 0.0, 0.0);
            gr.attr().setTitleX("Threshold");
            gr.attr().setTitleY("Sensitivity");
        }
        return gr;
    }
    
    protected GraphErrors falseCurve(DataSet ds, int label){
        GraphErrors gr = new GraphErrors("ROC");
        gr.attr().setMarkerSize(8);        
        double[] metrics = new double[3];
        for(double x = 0.0; x < 1.0; x += 0.05){
            this.getMetrics(ds, label, metrics, x);
            gr.addPoint(x, metrics[2], 0.0, 0.0);
            gr.attr().setTitleX("Threshold");
            gr.attr().setTitleY("False Rate");
        }
        return gr;
    }
    
    protected List<GraphErrors> falseCurves(DataSet ds){
        List<GraphErrors> gL = new ArrayList<>();
                
        for(int i = 0; i < model.getOutputSize(); i++){
            GraphErrors gr = falseCurve(ds,i);
            gr.attr().setLineColor(i+2);
            gr.attr().setMarkerColor(i+2);
            gL.add(gr);
        }
        return gL;
    }
    
    protected List<GraphErrors> sensitivityCurves(DataSet ds){
        List<GraphErrors> gL = new ArrayList<>();
                
        for(int i = 0; i < model.getOutputSize(); i++){
            GraphErrors gr = sensitivityCurve(ds,i);
            gr.attr().setLineColor(i+2);
            gr.attr().setMarkerColor(i+2);
            gL.add(gr);
        }
        return gL;
    }
    
    protected List<GraphErrors> rocCurves(DataSet ds){
        List<GraphErrors> gL = new ArrayList<>();
                
        for(int i = 0; i < model.getOutputSize(); i++){
            GraphErrors gr = rocCurve(ds,i);
            gr.attr().setLineColor(i+2);
            gr.attr().setMarkerColor(i+2);
            gL.add(gr);
        }
        return gL;
    }
    
    protected GraphErrors rocCurve(int ni, int pi){
        int bins = dataHist.get(ni).getxAxis().getNBins();
        GraphErrors gr = new GraphErrors("ROC");
        gr.attr().setMarkerSize(8);
        gr.attr().setTitle("Efficiency " + ni + " vs " + pi);
        double ptotal = dataHist.get(pi).integral();
        double ntotal = dataHist.get(ni).integral();
        
        for(int i = 1; i < bins; i++){
            double     x = dataHist.get(ni).getxAxis().getBinCenter(i);
            double    tp = dataHist.get(pi).integral(i, bins-1);
            double    fn = dataHist.get(ni).integral(i, bins-1);
            double roc = 0.0;
            if((tp+fn)>0.000001) roc = tp/(tp+fn);
            //System.out.printf("%8.5f %8.5f %8.5f\n",x,tp,roc);
            gr.addPoint(x, roc);
        }
        return gr;
    }
    
    public List<H1F> histograms(){ return this.dataHist;}
    
    public void createData(DataSet    ds){
        
        Iterator iter = ds.iterator();
        init();
        
        float[] output = new float[model.getOutputSize()];
                
        while(iter.hasNext()){
            TabularDataSet.Item  item = (TabularDataSet.Item) iter.next();
            float[]  input  = item.getInput().getValues();
            float[] desired = item.getTargetOutput().getValues();
            
            //System.out.printf("in %d, out %d\n",input.length,output.length);
            model.getOutput(input,output);
            
            int desiredClass = model.getClass(desired);
            int  outputClass = model.getClass(output);
            
            //dataHist.get(desiredClass).fill(output[desiredClass]);
            /*if(desiredClass==outputClass){
                dataHist.get(1).fill(output[desiredClass]);
            } else {
                dataHist.get(0).fill(output[outputClass]);
            }*/
            
            for(int i = 0; i < output.length; i++) dataHist.get(i).fill(output[i]);
            
        } 
    }
    
    
    public static void analyzeFeatureMetrics(){
        CsvDataProvider provider = new CsvDataProvider("dc_classifier2.csv",6,3);
        provider.isReversed = true;
        EJMLModelAnalysis    ana = new EJMLModelAnalysis("trcl.network");
        DataSet data  = provider.getData();

        //ana.createData(data);
        H2F h2 = ana.rocFeature(data, 1, 1);
        TGCanvas c = new TGCanvas(500,500);        
        c.view().region().draw(h2);
    }
    public static void main(String[] args){
        
        EJMLModelAnalysis.analyzeFeatureMetrics();
        
        /*
        CsvDataProvider provider = new CsvDataProvider("dc_classifier1.csv",6,3);
        
        provider.isReversed = true;
        DataSet ds = provider.getData();
        
        ds.shuffle();;
        //System.out.println(ds);
        
        DataSet[] data = ds.split(0.8,0.2);
        
        DeepNettsClassifier cl = new DeepNettsClassifier();
        cl.init(new int[]{6,24,24,12,3});        
        
        List<H1F> features = DataSetUtils.featureHist(data[0], false);
        TGCanvas cc = new TGCanvas(700,900);
        cc.view().divide(2, 3);
        
        for(int i = 0; i < 6; i++) cc.view().region(i).draw(features.get(i));
        
        cl.train(data[0], 125);
        cl.save("trcl.network");
        cl.evaluate(data[1]);
        //DataSet dst = DataSetGenerator.getRandomSet(2000, 24, 2);
        
        EJMLModelAnalysis ana = new EJMLModelAnalysis("trcl.network");
        ana.createData(data[1]);
        
        GraphErrors gr = ana.rocCurve(0, 1);
        TGCanvas c = new TGCanvas(500,900);
        c.view().divide(1, 3);
        c.view().cd(0);
        
      
        //c.view().cd(1).region().draw(gr,"PL");        
        
        //c.view().cd(2).region().draw(g2,"PL");        
        //c.view().region().getAxisFrame().setLimits(0, 1.0, 0, 1.0);
        
        List<GraphErrors>  graphs = ana.rocCurves(data[1]);
        List<GraphErrors>   sense = ana.sensitivityCurves(data[1]);
        List<GraphErrors> grfalse = ana.falseCurves(data[1]);
        
        c.view().cd(0);
        for(GraphErrors g : graphs)
            c.view().region().draw(g,"samePL");
        c.view().region(0).getAxisFrame().getAxisY().setFixedLimits(0.0, 1.12);
        
        c.view().cd(1);
        for(GraphErrors g : sense)
            c.view().region().draw(g,"samePL");
        c.view().region(1).getAxisFrame().getAxisY().setFixedLimits(0.0, 1.12);
        
        c.view().cd(2);
        for(GraphErrors g : grfalse)
            c.view().region().draw(g,"samePL");
        c.view().region(2).getAxisFrame().getAxisY().setFixedLimits(0.0, 1.12);        
        c.repaint();*/
    }
}
