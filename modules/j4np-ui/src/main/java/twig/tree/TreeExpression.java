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
import net.objecthunter.exp4j.function.Function;

/**
 *
 * @author gavalian
 */
public class TreeExpression {
    
    String  expName = "";
    String  treeExpression = "";
    List<String> expVariables = new ArrayList<String>();

    private static List<Function>  userDefined = new ArrayList<>();
    
    Expression expr = null;
    private boolean  isCutActive = true;
    
    Function funcVec3p = new Function("vec3p", 3) {
            @Override
            public double apply(double... args) {
                return Math.sqrt(args[0]*args[0] + 
                        args[1]*args[1] + args[2]*args[2]);
            }
    };
    
    Function funcVec3r = new Function("vec3r", 3) {
            @Override
            public double apply(double... args) {
                return Math.sqrt(args[0]*args[0] + 
                        args[1]*args[1]);
            }
    };
    
    Function funcVec3t = new Function("vec3t", 3) {
            @Override
            public double apply(double... args) {
                if(args[2]==0.0) return 0.0;
                double cth = args[2]/Math.sqrt(
                        args[0]*args[0]+args[1]*args[1]
                        + args[2]*args[2]
                );
                return Math.acos(cth);
            }
    };
    /*
    Function funcVec3p = new Function("vec3p", 3) {
            @Override
            public double apply(double... args) {
                if(args[2]==0.0) return 0.0;
                return Math.atan2(args[1],args[0]);
            }
    };*/
    
    Function funcVec3f = new Function("vec3f", 3) {
            @Override
            public double apply(double... args) {
                if(args[2]==0.0) return 0.0;
                return Math.atan2(args[1],args[0]);
            }
    };
    Function funcVec3pt = new Function("vec3pt", 3) {
            @Override
            public double apply(double... args) {
                if(args[2]==0.0) return 0.0;
                return Math.sqrt(args[0]*args[0] + args[1]*args[1]);
            }
    };
    
    Function funcVec3dist = new Function("vec3dist", 6) {
            @Override
            public double apply(double... args) {
                if(args[2]==0.0) return 0.0;
                return Math.sqrt( (args[0] - args[3])*(args[0] - args[3])
                        + (args[1] - args[4])*(args[1] - args[4]) 
                        + (args[2] - args[5])*(args[2] - args[5]));
            }
    };
    
    public TreeExpression(String name, String exp, List<String> branches){
        expName       = name;
        treeExpression = exp;
        expVariables.clear();
        for(String br : branches){
            expVariables.add(br);
        }
        init();
    }
    
    public TreeExpression( String exp, List<String> branches){
        expName       = "exp";
        treeExpression = exp;
        expVariables.clear();
        for(String br : branches){
            expVariables.add(br);
        }
        init();
    }
    
    public static void defineFunction(Function f){
        TreeExpression.userDefined.add(f);
    }
    
    final void init(){
        String[] variables = new String[expVariables.size()];
        for(int i=0; i < variables.length; i++) variables[i] = expVariables.get(i);        
        ExpressionBuilder builder = new ExpressionBuilder(treeExpression)
                .function(funcVec3f).function(funcVec3p).function(funcVec3t).function(funcVec3r);
        builder.functions(userDefined);
        builder.variables(variables);
        //System.out.println("expression = " + treeExpression);
        //System.out.println(" variables = " + Arrays.toString(variables));        
        expr = builder.build();
    }
    
    public double getValue(Tree tree){
        for(int i = 0; i < expVariables.size(); i++){
            expr.setVariable(expVariables.get(i), tree.getValue(expVariables.get(i)));
                    //tree.getBranch(expVariables.get(i)).getValue().doubleValue());
        }
        double result = expr.evaluate();
        return result;
    }
    
    public static boolean validateExpression(String expression,  List<String> branches){
    	if(expression.isEmpty()){
    		return false;
    	}
        String[] variables = new String[branches.size()];
        for(int i=0; i < branches.size(); i++) variables[i] = branches.get(i);
        ExpressionBuilder builder = new ExpressionBuilder(expression);
        builder.variables(variables);
        try {
            Expression expr = builder.build(); 
        } catch(Exception e){
            return false;
        }
        return true;
    }
}
