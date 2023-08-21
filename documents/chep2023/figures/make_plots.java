
public class MakePlots {

	public static void plotRGA(){
		TDirectory dir = new TDirectory("rga_m2212.twig");
		H1F hc = (H1F) dir.get("/rga/conv");
		H1F hd = (H1F) dir.get("/rga/dnai");
		MakePlots.plot(hc,hd);
	}
	public static void plotRGB(){
		TDirectory dir = new TDirectory("rgb_m2212.twig");
		H1F hc = (H1F) dir.get("/rgb/35na/conv");
		H1F hd = (H1F) dir.get("/rgb/35na/dnai");
		MakePlots.plot(hc,hd);
	}

    public static void plot(H1F hc, H1F hd){
	//TDirectory dir = new TDirectory("rga_m2212.twig");
	//H1F hc = (H1F) dir.get("/rga/conv");
	//H1F hd = (H1F) dir.get("/rga/dnai");

	PDF1D pdf = new PDF1D("pdf",0.8,1.17);
	F1D    fs = new F1D("signal","[amp]*gaus(x,[mean],[sigma])",0.8,1.17);
	F1D    fb = new F1D("backgr","[p0]+[p1]*x+[p2]*x*x",0.8,1.17);
	pdf.add(Arrays.asList(fs,fb));
	pdf.setParameters(3000,0.938,0.02,1.,1.,1.);

	PDF1D pdfd = new PDF1D("pdf",0.8,1.17);
        F1D    fsd = new F1D("signal","[amp]*gaus(x,[mean],[sigma])",0.8,1.17);
        F1D    fbd = new F1D("backgr","[p0]+[p1]*x+[p2]*x*x",0.8,1.17);
        pdfd.add(Arrays.asList(fs,fb));
        pdfd.setParameters(3000,0.938,0.02,1.,1.,1.);

	pdfd.fit(hd);
	pdf.fit(hc);
	pdf.show();
	pdfd.show();

	double bw = hc.getAxisX().getBinWidth(0);
	double int_conv = pdf.getParameter(0)*pdf.getParameter(2)/bw;;
	double int_dnai = pdfd.getParameter(0)*pdfd.getParameter(2)/bw;;

	System.out.printf("N_c = %f, N_d = %f, ratio = %f\n",int_conv,int_dnai,int_dnai/int_conv);
	
	TGCanvas c = new TGCanvas(500,600);
	
	hc.attr().set("fc=-1,lc=4,mc=4,mt=3,ms=8");
	hd.attr().set("fc=-1,lc=6,mc=6,mt=2,ms=8");
	pdf.attr().set("lc=2,ls=3");
	pdfd.attr().set("lc=1,ls=5");
	pdf.attr().setLegend("Fit to Conventional");
	pdfd.attr().setLegend("Fit De-Noised/AI-assisted");
	PaveText pt = new PaveText(String.format("ratio=%.3f",int_dnai/int_conv),0.65,0.15);

	hc.attr().setLegend("Conventional Tracking");
	hd.attr().setLegend("De-Noised/AI-assisted tracking");
	hc.attr().setTitleX("Mx (e^-#pi^+#pi^-) [GeV]");
	c.draw(hc,"EP").draw(hd,"sameEP").draw(pdf,"same").draw(pdfd,"same");;
	c.draw(pt);
	c.region().showLegend(0.05,0.98);
    }
}
