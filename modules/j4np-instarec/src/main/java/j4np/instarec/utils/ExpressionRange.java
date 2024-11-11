/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.utils;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 *
 * @author gavalian
 */
public class ExpressionRange {
    Expression expression = null;
    public ExpressionRange(){
        
    }
    
    public void init(String exp){
        ExpressionBuilder builder = new ExpressionBuilder(exp);
        builder.variables(new String[]{"x"});
        expression = builder.build();
        expression.setVariable("x", 45);
    }
    
    public double evaluate(double x){
        expression.setVariable("x", x);
        return expression.evaluate();
    }
    
    public static void main(String[] args){
        ExpressionRange range = new ExpressionRange();
        range.init("(x+5)/120.0");
        int counter = 0;
        long then = System.currentTimeMillis();
        double summ = 0.0;
        for(int j = 0; j < 1200000; j++){
            for(int i = 0; i < 112; i++){
                //System.out.printf(" %f -> %f\n",(double) i, range.evaluate((double)i));
                double value = (double) i;
                summ += (value+5)/120.0; //range.evaluate((double)i);
                //summ += range.evaluate((double)i);
                counter++;
            }
        }
        long now = System.currentTimeMillis();
        System.out.printf(" expression rate = %f Hz, summ = %f time = %d\n" , ((double) counter)/(now-then), summ, now-then);
    }
}
