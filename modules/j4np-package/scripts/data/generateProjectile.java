//***********************************************************************
//* generate points at given intervals for projectile motion
//***********************************************************************
public class Generator {
    static double[] xpoints = new double[]{0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9};
    static Random r = new Random();

    public Generator(){
	r.setSeed(123456L);
    }

    public static DataVector getVectorX(){
	DataVector vecX = new DataVector();
	 for(int xp = 0; xp < xpoints.length; xp++){
	     vecX.add(xpoints[xp]);
	 }
	 return vecX;
    }

    public static DataVector getVectorY(){    
	double g = 2.4;		
	double th = Math.toRadians(r.nextDouble()*45.0+25.0);
	double v0 = r.nextDouble()*1.0+1.5;
	double vx = Math.cos(th)*v0;
	double vy = Math.sin(th)*v0;	
	DataVector vecY = new DataVector();		
	for(int xp = 0; xp < xpoints.length; xp++){
	    double x = xpoints[xp];
	    double t = x/vx;
	    double y = vy*t - t*t*g/2.0;	    
	    vecY.add(y);
	    //System.out.printf(" %8.5f \n",Math.toDegrees(th));
	}
	return vecY;
    }

    public static List<DataVector> generate(){
	DataVector v1 = Generator.getVectorY();
	DataVector v2 = Generator.getVectorY();
	return Arrays.asList(v1,v2);
    }

    public static void plot(){

	List<DataVector> v = Generator.generate();
	TGCanvas c = new TGCanvas();
	for(int i = 0; i < v.size(); i++){
	    GraphErrors gr = new GraphErrors("gr",Generator.getVectorX(),v.get(i));
	    gr.attr().setLineColor(i+2);
	    c.draw(gr,"PL,same");
	}
    }
}

