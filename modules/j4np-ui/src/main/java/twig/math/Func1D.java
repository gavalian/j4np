/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.math;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import twig.config.TDataAttributes;
import twig.data.DataPoint;
import twig.data.DataRange;
import twig.data.DataSet;
import twig.data.H1F;

/**
 *
 * @author gavalian
 */
public class Func1D implements DataSet {
    
    UserParameters      userPars = new UserParameters();
    String              funcName = "f1d";
    private int   defaultDrawingPoints = 250;
    protected Dimension1D  functionRange = new Dimension1D();
    private TDataAttributes   funcAttr      = new TDataAttributes();
    private double      funcChi2       = 0.0;
    private int         funcNDF        = 0;
    
    public Func1D(String name){
        this.funcName = name;
        initAttributes();
        //FunctionFactory.registerFunction(this);
    }
    
    public Func1D(String name, double min, double max){
        this.funcName = name;
        //FunctionFactory.registerFunction(this);
        this.setRange(min, max);
        initAttributes();
    }
    
    private void initAttributes(){
        /*
    	try {
			this.funcAttr = GStyle.getFunctionAttributes().clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	*/
    }
    
    public final void setRange(double min, double max){
        this.functionRange.setMinMax(min, max);
    }
    
    public void addParameter(String name){
        this.userPars.getParameters().add(new UserParameter(name,0.0));
    }
    
    public void addParameter(UserParameter par){
        this.userPars.getParameters().add(par);
    }
    
    @Override
    public  void setName(String name){ this.funcName = name;}
    
    @Override
    public String getName(){return this.funcName;}
    
    /*public void setParameters(double[] values){
        this.userPars.setParameters(values);
    }*/
    
    public void setParameters(double... values){
        this.userPars.setParameters(values);
    }
    
    public void setParameters(UserParameters upars){
        int npars = upars.getParameters().size();
        if(npars!=this.getNPars()){
            System.err.println("\nERROR in Func1D setPArameters\n");
            return;
        }
        
        for(int loop = 0; loop < npars; loop++) this.setParameter(loop, upars.getParameter(loop).value());
        //this.userPars.setParameters(values);
    }
    
    public void setParameter(int i, double value){
        userPars.getParameter(i).setValue(value);
    }
    
    public UserParameter  parameter(int index){
        return this.userPars.getParameter(index);
    }
    public int  getNPars(){
        return userPars.getParameters().size();
    }
    
    public void setParStep(int par, double step){
        this.parameter(par).setStep(step);
    }
    
    public void setParLimits(int par, double min, double max){
        userPars.getParameters().get(par).setLimits(min, max);
    }
    
    public double getParameter(int index){ 
        return userPars.getParameter(index).value();
    }
    
    public double evaluate(double x){
        return 1;
    }
    
    public String getExpression(){
        return "";
    }
    
    public double getIntegral(){
        int nsamples = 400;
        double step = this.getRange().getLength()/nsamples;
        double integral = 0.0;
        for(int i = 0; i < nsamples; i++){
            double x = getRange().getMin() + i * step;
            integral += evaluate(x)*step;
        }
        return integral;
    }
    
    public Dimension1D getRange(){
        return this.functionRange;
    }
    
    public boolean isInRange(double x){
        return (x>=functionRange.getMin()&&x<=functionRange.getMax());
    }
    
    public double getMin(){
        return this.functionRange.getMin();
    }
    
    public double getMax(){
        return this.functionRange.getMax();
    }
    
    public void show(){
        System.out.println(userPars.toString());
    }
        
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("FUNCTION (%s) RANGE %9.4f %9.4f (exp:%s)\n", 
                getName(),getMin(),getMax(),getExpression()));
        str.append("-----\n");
        str.append(userPars.toString());
        return str.toString();
    }
    
    public void fit(DataSet ds, String options){
        DataFitter.fit(this, ds, options);
    }

    public void fit(DataSet ds){
        DataFitter.fit(this, ds, "N");
    }
    
    public double getChiSquare(){
        return this.funcChi2;
    }
    
    public int getNDF(){
        return this.funcNDF;
    }
    
    public void setChiSquare(double chi2){
        this.funcChi2 = chi2;
    }
    
    public void setNDF(int ndf){
        this.funcNDF = ndf;
    }
    

    
   /* public void setOptStat(int optStatString) {
		this.getAttributes().setOptStat(""+optStatString);
	}
    
	public void setOptStat(String optStatString) {
		this.getAttributes().setOptStat(optStatString);
	}
	public String getOptStat() {
		return this.getAttributes().getOptStat();
	}
	*/
    public void estimateParameters(){};
    public double[] getParameterEstimate(){return null;}

    @Override
    public void reset() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void save(String filename) {
        String extension = filename.substring(filename.lastIndexOf("."));
        filename = filename.substring(0, filename.lastIndexOf("."));

        try {
            FileWriter parsfile = new FileWriter(filename + "_pars" + extension);
            parsfile.write("#F1D: " + this.funcName + ", nsamples: " + getSize(0) +"\n");
            parsfile.write(this.toString());
            parsfile.close();

            FileWriter file = new FileWriter(filename + extension);
            file.write("#F1D: " + this.funcName + " nsamples: " + getSize(0) +"\n");
            file.write("#x,y\n");
            DataPoint point = new DataPoint();
            for(int i = 0; i < getSize(0); i++) {
                getPoint(point);
                file.write(String.format("%f,%f",
                        point.x, point.y));
                file.write('\n');
            }
            file.close();

        } catch (IOException e) {
        }
    }
    
    @Override
    public int getSize(int dimention) {
        return this.defaultDrawingPoints;
    }

    @Override
    public void getPoint(DataPoint point, int... coordinates) {
        int bin = coordinates[0];
        double length   = functionRange.getLength();
        double fraction = ((double) bin )/this.defaultDrawingPoints;
        point.x = functionRange.getMin() + fraction*length;
        point.y = evaluate(point.x);
        point.xerror = 0.0; point.yerror = 0.0;
    }

    @Override
    public void getRange(DataRange range) {
        DataPoint p = new DataPoint();
        int  npoints = getSize(0);
        getPoint(p,0);
        range.set(p.x, p.x, p.y, p.y);
        for(int i = 0; i < npoints; i++){
            getPoint(p,i);
            range.grow(p.x, p.y);
        }
    }
    
    public static double calcChiSquare(Func1D f, H1F h){
        double chi2 = 0;
        int bins = h.getXaxis().getNBins();
        for(int i = 0; i < bins; i++){
            double x = h.getXaxis().getBinCenter(i);
            double bc = h.getBinContent(i);
            double normalization = bc;
            if(x>f.getMin()&&x<f.getMax()&&bc>0.00000000000001){
              double fc = f.evaluate(x);
              chi2 += (fc-bc)*(fc-bc)/normalization;
            }
        }
        return chi2;
    }
    
    @Override
    public TDataAttributes attr() {
        return funcAttr;
    }
    
    @Override
    public List<String> getStats(String options) {
        List<String> stats = new ArrayList<>();
        String format = "%.3f";

        int np = this.getNPars();
        if(options.contains("S")==true){
            StringBuilder str = new StringBuilder();
            for(int i = 0; i < np; i++){
                if(i!=0) str.append(", ");
                str.append(String.format("%s:"+format, this.parameter(i).label(),
                        this.parameter(i).value()));
                //if(options.contains("F")==true)
                    str.append(String.format("#pm%.4f",this.parameter(i).error()));
            }
            stats.add(str.toString());
        } else {
            for(int i = 0; i < np; i++){
                
                String par = String.format("%s:"+format, this.parameter(i).label(),
                        this.parameter(i).value());                
                String parError = String.format("#pm%.4f",this.parameter(i).error());
                //if(options.contains("F")==true){
                    stats.add(par+parError);
                //} else {
                //    stats.add(par);
                //}
            }
        }
        return stats;
    }

}
