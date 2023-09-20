
public class Generator {

    public static H1F generate(double mean, double sigma){
	F1D f = new F1D("f","[p0]+landau(x,[mean],[sigma])",0.,1.);
	H1F h = new H1F("h",48,0.0,1.0);
	f.setParameters(0.15,mean,sigma);
	RandomFunc rf = new RandomFunc(f);
	for(int it = 0; it < 25000; it++){
	    h.fill(rf.random());
	}
	h.unit();
	return h;
    }

    public static H1F generate2(double mean1, double sigma1, double mean2, double sigma2, double ratio){
        F1D f = new F1D("f","[p0]+landau(x,[mean],[sigma])+[amp]*landau(x,[mean2],[sigma2])",0.,1.);
        H1F h = new H1F("h",48,0.0,1.0);
        f.setParameters(0.15,mean1,sigma1,ratio,mean2,sigma2);
        RandomFunc rf = new RandomFunc(f);
        for(int it = 0; it < 25000; it++){
            h.fill(rf.random());
        }
        h.unit();
        return h;
    }
    
    public static void generate(int count){
	Random r = new Random();
	TextFileWriter tw = new TextFileWriter();
	tw.open("pulse_generated.csv");
	for(int i = 0; i < count; i++){
	    double mean = r.nextDouble()*0.7+0.1;
	    H1F h = Generator.generate(mean,0.02);
	    h.divide(1.005);
	    String data = DataArrayUtils.floatToString(h.getData(),",");
	    //System.out.println(data);
	    tw.writeString(data);
	}
	tw.close();
    }

    public static void generate2(int count){
        Random r = new Random();
        TextFileWriter tw = new TextFileWriter();
        tw.open("pulse_generated.csv");
        for(int i = 0; i < count; i++){
	    
            double mean1 = r.nextDouble()*0.30+0.10;
	    double mean2 = r.nextDouble()*0.30+0.60;
	    double ratio = r.nextDouble()*0.65+0.25;
	    
	    double which = r.nextDouble();
	    if(which>0.5){
		H1F h = Generator.generate2(mean1,0.015,mean2,0.015,ratio);
		h.divide(1.005);	    
		String data = DataArrayUtils.floatToString(h.getData(),",");
		tw.writeString(data);
	    } else {
		H1F h = Generator.generate2(mean2,0.015,mean1,0.015,ratio);
		h.divide(1.005);
		String data = DataArrayUtils.floatToString(h.getData(),",");
		tw.writeString(data);
	    }
        }
        tw.close();
    }


}
