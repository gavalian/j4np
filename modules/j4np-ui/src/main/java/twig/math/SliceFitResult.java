/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.math;

import twig.data.H1F;

/**
 *
 * @author gavalian
 */
public class SliceFitResult {
    
    private    String funcExpression = "";
    protected  F1D          function = null;
    protected  H1F         histogram = null;
    
    public SliceFitResult(){
        funcExpression = "[p0]+[p1]*x+[p2]*x*x+[amp]*gaus(x,[mean],[sigma])";
    }
    
    public SliceFitResult(String exp){
        funcExpression = exp;
    }
    
    public void fit(H1F h){
        histogram = h;
        function = new F1D("f",funcExpression,
                h.getAxis().min(),h.getAxis().max());
        function.setParameter(0, 1);
        function.setParameter(1, 1);
        function.setParameter(2, 0);
        function.setParameter(3,h.getMax());
        function.setParameter(4,h.getMean());
        function.setParameter(5,h.getRMS()*0.5);        
        function.fit(h);
    }
    
}
