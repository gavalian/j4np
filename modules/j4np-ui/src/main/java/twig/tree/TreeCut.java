/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package twig.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;

/**
 *
 * @author gavalian
 */
public class TreeCut {
    
    String  cutName = "";
    String  cutExpression = "";
    List<String> cutVariables = new ArrayList<String>();
    Expression expr = null;
    private boolean  isCutActive = true;
    
    public ArrayList<String> getBranches(){
        return (ArrayList<String>) cutVariables;
    }
    
    static Operator operatorGT = new Operator(">", 2, true, Operator.PRECEDENCE_MULTIPLICATION) {
        @Override
        public double apply(final double... args) {
            if(args[0]>args[1]) return 1.0;
            return 0.0;
        }
    };
    
    static Operator operatorLT = new Operator("<", 2, true, Operator.PRECEDENCE_MULTIPLICATION) {
        @Override
        public double apply(final double... args) {
            if(args[0]<args[1]) return 1.0;
            return 0.0;
        }
    };
    
    static Operator operatorEQ = new Operator("==", 2, true, Operator.PRECEDENCE_MULTIPLICATION) {
        @Override
        public double apply(final double... args) {
            if(args[0]==args[1]) return 1.0;
            return 0.0;
        }
    };
    
    static Operator operatorNOTEQ = new Operator("!=", 2, true, Operator.PRECEDENCE_MULTIPLICATION) {
        @Override
        public double apply(final double... args) {
            if(args[0] == (int) args[1]) return 0.0;
            return 1.0;
        }
    };
    static Operator operatorAND = new Operator("&&", 2, true, Operator.PRECEDENCE_ADDITION) {
        @Override
        public double apply(final double... args) {
            if(args[0]>0.0&&args[1]>0.0) return 1.0;
            return 0.0;
        }
    };
    
    static Operator operatorOR = new Operator("||", 2, true, Operator.PRECEDENCE_ADDITION) {
        @Override
        public double apply(final double... args) {
            if(args[0]>0.0||args[1]>0.0) return 1.0;
            return 0.0;
        }
    };
    
    public static boolean validateExpression(String expression,  List<String> branches){
        if(expression.isEmpty()){
            return false;
        }
        String[] variables = new String[branches.size()];
        for(int i=0; i < branches.size(); i++) variables[i] = branches.get(i);
        ExpressionBuilder builder = new ExpressionBuilder(expression)
                .operator(operatorAND)
                .operator(operatorOR)
                .operator(operatorGT)
                .operator(operatorLT)
                .operator(operatorEQ)
                .operator(operatorNOTEQ);
        builder.variables(variables);
        try {
            Expression expr = builder.build();
        } catch(Exception e){
            return false;
        }
        return true;
    }
    
    
    public static String combine(List<TreeCut> cuts){
        if(cuts.size()>0){
            StringBuilder str = new StringBuilder();
            str.append(cuts.get(0).getExpression());
            for (int i = 1; i < cuts.size(); i++) {
                str.append("&&").append(cuts.get(i).getExpression());
            }
            return str.toString();
        }
        return "";
    }
    /*
    public DataCut(String name, String exp){
    cutName = name;
    cutExpression = exp;
    }*/
    
    public TreeCut(String name, String exp, List<String> branches){
        cutName       = name;
        cutExpression = exp;
        cutVariables.clear();
        for(String br : branches){
            cutVariables.add(br);
        }
        init();
    }
    
    public boolean isActive(){
        return this.isCutActive;
    }
    
    public void setActive(boolean activeFlag){
        this.isCutActive = activeFlag;
    }
    
    public String getName(){
        return cutName;
    }
    
    public String getExpression(){
        return cutExpression;
    }
    
    public void setExpression(String expression){
        this.cutExpression = expression;
        init();
    }
    
    public void setName(String name){
        this.cutName = name;
    }
    
    final void init(){
        String[] variables = new String[cutVariables.size()];
        for(int i=0; i < variables.length; i++) variables[i] = cutVariables.get(i);
        if(cutExpression.length()>0){
            ExpressionBuilder builder = new ExpressionBuilder(cutExpression)
                    .operator(operatorAND)
                    .operator(operatorOR)
                    .operator(operatorGT)
                    .operator(operatorLT)
                    .operator(operatorEQ)
                    .operator(operatorNOTEQ);
            builder.variables(variables);
            expr = builder.build();
        }
    }
    
    public double isValid(Tree tree){
        //System.out.println("------ '" + cutExpression + "' size = " + cutExpression.length());
        if(cutExpression.length()==0) return 1.0;
        if(expr!=null){
            for(int i = 0; i < cutVariables.size(); i++){
                expr.setVariable(cutVariables.get(i),tree.getValue(cutVariables.get(i)));
                        //tree.getBranch(cutVariables.get(i)).getValue().doubleValue());
            }
            double result = expr.evaluate();
            //if(result>0.0) return true;
            return result;
            /*}else{
            return true;
            }*/
        }
        if(this.getExpression()==""){
            return 1.0;
        }else{
            return 0.0;
        }
    }
}
