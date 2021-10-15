/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.math;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class PDF1D extends Func1D {
    
    List<F1D>  funcList = new ArrayList<>();
    
    private  static int[] lineColors = new int[]{4,2,7,6,11,10};
    private  static int[] lineStyles = new int[]{4,2,6,9,5,7};
    
    public PDF1D(String name, double min, double max){
        super(name,min,max);
    }
    
    public PDF1D(String name, double min, double max, String[] exp){
        super(name,min,max);
        this.add(exp);
    }
    
    public final void  add(String[] exp){
        List<F1D> list = new ArrayList<>();
        
        for(int i = 0; i < exp.length; i++){
            F1D f = new F1D("f"+i,exp[i],getMin(),getMax());
            f.show();
            list.add(f);
        }
        this.add(list);
    }
    
    public PDF1D add(F1D f){ funcList.add(f); return this;}
    
    public PDF1D add(List<F1D> funcs){
        this.userPars.clear();
        for(F1D f : funcs){
            int n = f.getNPars();
            for(int i = 0; i < n; i++){
                this.addParameter(
                        new UserParameter(f.parameter(i).name(),
                                f.parameter(i).value()
                        ));
            }
            funcList.add(f);
        } 
        
        this.attr().setLineColor(5);
        this.attr().setLineWidth(2);
        this.attr().setLineStyle(0);
        
        for(int i = 0; i < funcList.size(); i++){
            if(i<PDF1D.lineColors.length){
                funcList.get(i).attr().setLineWidth(2);
                funcList.get(i).attr().setLineColor(PDF1D.lineColors[i]);
                funcList.get(i).attr().setLineStyle(PDF1D.lineStyles[i]);
            }
        }
        return this;
    }
    
    public  F1D  getFunction(){

        StringBuilder str = new StringBuilder();
        for( F1D f : funcList){
            str.append("+").append(f.getExpression());
        }
        str.deleteCharAt(0);
        //System.out.println("exp : " + str.toString());
        F1D fp = new F1D("pdf",str.toString(),getMin(),getMax());
        fp.attr().setLineColor(this.attr().getLineColor());
        fp.attr().setLineWidth(this.attr().getLineWidth());
        fp.attr().setLineStyle(this.attr().getLineStyle());
        
        int npars = fp.getNPars();
        for(int i = 0; i < npars; i++){
            String name = fp.parameter(i).name();
            int   index = this.userPars.contains(name);
            if(index>=0){
                fp.parameter(i).setValue(userPars.parameters.get(index).value());
                fp.parameter(i).setError(userPars.parameters.get(index).error());
            }
        }
        
        return fp;
    }
    private void updateParameters(){
        int whichOne = 0;
        for(int i = 0; i < this.funcList.size(); i++){
            F1D temp = funcList.get(i);
            int nparams = temp.getNPars();
            for(int p = 0; p < nparams; p++){
                temp.parameter(p).setValue(parameter(whichOne).value());
                whichOne++;
            }
        }
    }
    
    @Override
    public double evaluate(double x){
        updateParameters();
        double value = 0.0;
        for(int i = 0; i < funcList.size();i++){            
            value+= funcList.get(i).evaluate(x);
        }
        return value;
    }
    
    public List<F1D>  list(){ return funcList;}
}
