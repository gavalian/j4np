/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.data;
import java.util.ArrayList;
import java.util.List;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.operator.Operator;
/**
 *
 * @author gavalian
 */
public class Evaluator {
        private int             counter = 0;
        private Expression         expr = null;
        private List<String>    varList = new ArrayList<String>();
        
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
        
        /*
        static Function bitOperation = new Function("bit", 2) {
        @Override
        public double apply(double... args) {
        //DataByteUtils.readLong(counter, counter, counter)
        if(args[2]==0.0) return 0.0;
        return Math.sqrt(args[0]*args[0] + args[1]*args[1] + args[2]*args[2])/args[2];
        }
        };*/
        
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
        
        public Evaluator( Schema schema,String expression){
            initExpression(expression,schema);
        }
        
        public Evaluator(Bank b,String expression){
            initExpression(expression,b.getSchema());
        }
        
        private void initExpression(String expression, Schema schema){
            String[] array = schema.getEntryArray();
            String[] arrayWithI = new String[array.length+1];
            for(int i = 0; i < array.length; i++){
                arrayWithI[i] = array[i];
            }
            arrayWithI[array.length] = "i";
            initExpression(expression,arrayWithI);
        }
        
        private void initExpression(String expression, String[] variables){
        ExpressionBuilder builder = new ExpressionBuilder(expression)
                .operator(operatorAND)
                .operator(operatorOR)
                .operator(operatorGT)
                .operator(operatorLT)
                .operator(operatorEQ)
                .operator(operatorNOTEQ);
        builder.variables(variables);
        expr = builder.build();
        varList.clear();
        for(int i = 0; i < variables.length; i++){
            varList.add(variables[i]);
        }
    }
        
        public double evaluate(Bank bank,int row){
            int nrows = varList.size();
            for(int i = 0; i < nrows-1; i++){
                String  name = varList.get(i);
                double value = bank.getValue(i, row);
                expr.setVariable(name, value);
            }
            expr.setVariable("i", row);
            return expr.evaluate();
        }
        
        public List<Integer> getIterator(Bank bank){
            List<Integer> iter = new ArrayList<>();
            int rows = bank.getRows();
            for(int row = 0; row < rows; row++){
                double value = this.evaluate(bank, row);
                if(value>0.5) iter.add(row);
            }
            return iter;
        }
}
