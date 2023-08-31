//---- script
import java.awt.Font;

public class MakePlots {


    public void draw(){
	TStyle.getInstance().getAxisAttrX().setAxisLabelFont(new Font("Palatino",Font.PLAIN,18));
	TStyle.getInstance().getAxisAttrY().setAxisLabelFont(new Font("Palatino",Font.PLAIN,18));
	TStyle.getInstance().getAxisAttrX().setAxisTitleFont(new Font("Palatino",Font.PLAIN,20));
	TStyle.getInstance().getAxisAttrY().setAxisTitleFont(new Font("Palatino",Font.PLAIN,20));
	TStyle.getInstance().getAxisAttrY().setAxisGridLineColor(31);
	
        TDirectory dir = new TDirectory();
        dir.read("physics_lumi.twig");

        double min = 0.875;
        double max = 1.025;

        H1F h00_0 = (H1F) dir.get("/results/bc000/h1000");

        H1F h45_1 = (H1F) dir.get("/results/bc045/h1000");
        H1F h45_2 = (H1F) dir.get("/results/bc045/h2000");
        H1F h45_3 = (H1F) dir.get("/results/bc045dn/h1000");
        H1F h45_4 = (H1F) dir.get("/results/bc045dn/h2000");

        H1F h95_1 = (H1F) dir.get("/results/bc095/h1000");
        H1F h95_2 = (H1F) dir.get("/results/bc095/h2000");
        H1F h95_3 = (H1F) dir.get("/results/bc095dn/h1000");
        H1F h95_4 = (H1F) dir.get("/results/bc095dn/h2000");

        H1F h150_1 = (H1F) dir.get("/results/bc150/h1000");
        H1F h150_2 = (H1F) dir.get("/results/bc150/h2000");
        H1F h150_3 = (H1F) dir.get("/results/bc150dn/h1000");
        H1F h150_4 = (H1F) dir.get("/results/bc150dn/h2000");

        h00_0.attr().setLegend("simulated distribution");
        h45_1.attr().setLegend("conventional tracking 45 nA");
        h45_2.attr().setLegend("ai-assisted tracking 45 nA");
        h45_3.attr().setLegend("denoised conv. tracking 45 nA");
        h45_4.attr().setLegend("denoised ai-assisted trkg 45 nA");
        
        h95_1.attr().setLegend("conventional tracking 95 nA");
        h95_2.attr().setLegend("ai-assisted tracking 95 nA");
        h95_3.attr().setLegend("denoised conv. tracking 95 nA");
        h95_4.attr().setLegend("denoised ai-assisted trkg 95 nA");

        h150_1.attr().setLegend("conventional tring 150 nA");
        h150_2.attr().setLegend("ai-assisted tracking 150 nA");
        h150_3.attr().setLegend("denoised conv. tracking 150 nA");
        h150_4.attr().setLegend("denoised ai-assisted trkg 150 nA");

        h00_0.attr().set("lw=1,lc=1");

        setAttr("lc=4",h45_1,h95_1,h150_1);
        setAttr("lc=2",h45_2,h95_2,h150_2);
        setAttr("lc=3",h45_3,h95_3,h150_3);
        setAttr("lc=6",h45_4,h95_4,h150_4);
        setTitle("Mx (e^-#pi^+#pi^-) [GeV]",h00_0,h45_1,h95_1,h150_1);
        TGCanvas c = new TGCanvas(700,750);
        
        c.view().divide(1,3);
        c.view().top(5).bottom(45);
        c.cd(0).draw(h00_0).draw(h45_1,"same").draw(h45_2,"same").draw(h45_3,"same").draw(h45_4,"same").region().showLegend(0.40,0.98);
        c.cd(1).draw(h00_0).draw(h95_1,"same").draw(h95_2,"same").draw(h95_3,"same").draw(h95_4,"same").region().showLegend(0.40,0.98);
        c.cd(2).draw(h00_0).draw(h150_1,"same").draw(h150_2,"same").draw(h150_3,"same").draw(h150_4,"same").region().showLegend(0.40,0.98);
	String[] labels = new String[]{"a)","b)","c)"};
	for(int i = 0; i < 3; i++){
	    PaveText t = new PaveText(labels[i],0.94,0.97);
	    t.setDrawBox(false);
	    t.setFillBox(false);
	    c.cd(i).draw(t);
	}
        TGCanvas c2 = new TGCanvas(700,750);
        c2.view().top(5).bottom(45);
        double norm = h45_1.area(min,max);

        GraphErrors g_conv = getGraph("lc=4,lw=2,mc=4,ms=18,mt=1", norm, h45_1,h95_1,h150_1 );
        GraphErrors g_aias = getGraph("lc=2,lw=2,mc=2,ms=18,mt=2", norm, h45_2,h95_2,h150_2 );
        GraphErrors g_dncv = getGraph("lc=3,lw=2,mc=3,ms=18,mt=3", norm, h45_3,h95_3,h150_3 );
        GraphErrors g_dnai = getGraph("lc=6,lw=2,mc=6,ms=18,mt=4", norm, h45_4,h95_4,h150_4 );

        g_conv.attr().setTitleX("Beam Current (nA)");
        g_conv.attr().setTitleY("Reconstruction Efficiency");
        g_conv.attr().setLegend("conventional tracking");
        g_aias.attr().setLegend("ai-assisted tracking");
        g_dncv.attr().setLegend("denoised conv. tracking");
        g_dnai.attr().setLegend("denoised ai-assisted tracking");

	
        c2.draw(g_conv,"PL").draw(g_aias,"samePL").draw(g_dncv,"samePL").draw(g_dnai,"samePL");
        c2.region().showLegend(0.05,0.25);

	PaveText t = new PaveText("d)",0.94,0.99);
	t.setDrawBox(false);
	t.setFillBox(false);
	c2.draw(t);
        c.view().export("missing_mass.pdf","pdf");
        c2.view().export("luminosity_scan.pdf","pdf");
	
	
    }

    public void setAttr(String attr, H1F... hL){
        for(H1F h : hL) h.attr().set(attr);
    }

    public void setTitle(String attr, H1F... hL){
        for(H1F h : hL) h.attr().setTitleX(attr);
    }
public GraphErrors getGraph(String attr, double norm, H1F... hL){
    double min = 0.875;
    double max = 1.025;
    double[] beam = new double[]{45,95,150.0};
    GraphErrors g = new GraphErrors();
    for(int i = 0; i < hL.length; i++){
        g.addPoint(beam[i],hL[i].area(min,max)/norm);
    }
    g.attr().set(attr);
    return g;
}

}
