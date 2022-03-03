import twig.tree.TreeExpression;
import net.objecthunter.exp4j.function.Function;
import j4np.physics.*;

Function funcPhys = new Function("mxe",6){
    @Override
    public double apply(double... p){
	LorentzVector v = LorentzVector.withPxPyPzM(0.0,0.0,6.6,0.0005);
	v.add(0.0,0.0,0.0,0.938)
	    .sub(p[0],p[1],p[2],0.0005)
	    .sub(p[3],p[4],p[5],0.13957);	
	return v.mass();
    }
}

TreeExpression.defineFunction(funcPhys);
