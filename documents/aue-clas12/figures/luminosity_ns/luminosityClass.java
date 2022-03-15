//-------------------------------------------------
// This class calculates luminosity for given 
// electron and hardon numbers as a function of
// beam current
import twig.math.*;

public class LuminosityClass {
    
    double[]  beam = null;
    double[]  elec = null;
    double[] hplus = null;
    double[] hminus = null;

    public LuminosityClass() {}
    public LuminosityClass(double[] b) { beam = b; }

    public GraphErrors makeGraph(double[] e, double[] hp){        
        StatNumber rn = new StatNumber();
        StatNumber rd = new StatNumber();
        
        GraphErrors gr = new GraphErrors();

        for(int i = 0; i < beam.length; i++){
            rd.set(e[i],Math.sqrt(e[i]));
            rn.set(hp[i],Math.sqrt(hp[i]));
            rn.divide(rd);
            gr.addPoint(beam[i],rn.number(),0.0,rn.error());
        }
        gr.attr().setMarkerSize(9);
        return gr;
    }

    public GraphErrors makeGraphNorm(double[] e, double[] hp){        
        GraphErrors gr = makeGraph(e,hp);
        GraphErrors grn = gr.divide(gr.getVectorY().getValue(0));
        grn.attr().setMarkerSize(9);
        return grn;
    }

    public F1D getFit(GraphErrors gr, String options){
        F1D f = new F1D("f","[a]+[b]*x",beam[0],beam[beam.length-1]);
        f.fit(gr,options);
        f.attr().setLineStyle(2);
        return f;
    }
 
}
