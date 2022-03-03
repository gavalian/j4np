import twig.tree.TreeExpression;
import net.objecthunter.exp4j.function.Function;

Function func = new Function("myf",3){
    @Override
    public double apply(double... par){
	return Math.sqrt(par[0]*par[0]+par[1]*par[1])/Math.abs(par[2]);
    }
}

TreeExpression.defineFunction(func);
