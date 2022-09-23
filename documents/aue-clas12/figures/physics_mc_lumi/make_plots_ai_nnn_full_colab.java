//---- script

TDirectory dir = new TDirectory();
dir.read("physics_lumi.twig");

double min = 0.87;
double max = 1.0;


int mode = 1;

//H1F h00_0 = (H1F) dir.get("/results/bc000/h1000");

H1F h00_0 = (H1F) dir.get("/results/bc045/h1000");

H1F h45_1 = (H1F) dir.get("/results/bc045/h1000");
H1F h45_1a = (H1F) dir.get("/results/bc045/h2000");
H1F h45_2 = (H1F) dir.get("/results/bc045dn/h1000");
H1F h45_3 = (H1F) dir.get("/results/bc045dn/h2000");

H1F h95_1 = (H1F) dir.get("/results/bc095/h1000");
H1F h95_1a = (H1F) dir.get("/results/bc095/h2000");
H1F h95_2 = (H1F) dir.get("/results/bc095dn/h1000");
H1F h95_3 = (H1F) dir.get("/results/bc095dn/h2000");

H1F h150_1 = (H1F) dir.get("/results/bc150/h1000");
H1F h150_1a = (H1F) dir.get("/results/bc150/h2000");
H1F h150_2 = (H1F) dir.get("/results/bc150dn/h1000");
H1F h150_3 = (H1F) dir.get("/results/bc150dn/h2000");

GraphErrors gr0na = h00_0.getGraph();
gr0na.attr().setMarkerSize(8);
h00_0.attr().setMarkerSize(4);

h45_1.attr().setLineColor(4);
h45_1a.attr().setLineColor(3);
h45_1.attr().setFillColor(84);
h45_1.attr().setFillStyle(2);

h45_2.attr().setLineColor(2);

h95_1.attr().setLineColor(4);
h95_1a.attr().setLineColor(3);
h95_1.attr().setFillColor(84);
h95_1.attr().setFillStyle(2);
h95_2.attr().setLineColor(2);
//h95_2.attr().setFillColor(57);

h150_1.attr().setLineColor(4);
h150_1a.attr().setLineColor(3);
h150_1.attr().setFillColor(84);
h150_2.attr().setLineColor(2);
h150_1.attr().setFillStyle(2);
h45_3.attr().setLineColor(6);
h95_3.attr().setLineColor(6);
h150_3.attr().setLineColor(6);



h150_3.attr().setTitleX("Mx(e#pi^+#pi^-) [GeV]");
TGCanvas c = new TGCanvas(500,500);

c.view().divide(new double[][]{{0.05,0.28,0.28,0.28,0.13}});
c.view().region(0).setBlank();

Legend leg45 = new Legend(0.5,0.96);

leg45.add(gr0na,"conventional 45 nA");
leg45.add(h45_1,"conventional 45 nA");
//leg45.add(h45_2,"de-noised conv. 45 nA");
leg45.add(h45_3,"de-noised ai 45 nA");

Legend leg95 = new Legend(0.5,0.96);

leg95.add(gr0na,"conventional 45 nA");
leg95.add(h95_1,"conventional 95 nA");
leg95.add(h95_3,"de-noised conv. 95 nA");

Legend leg150 = new Legend(0.5,0.96);

leg150.add(gr0na,"conventional 45 nA");
leg150.add(h150_1,"conventional 150 nA");
//leg150.add(h150_2,"de-noised conv. 150 nA");
leg150.add(h150_3,"de-noised ai 150 nA");
if(mode==0){
  c.view().region(1).draw(h45_1).draw(h00_0,"EPsame").joinX().wrapX();
  c.view().region(1).draw(leg45);
    c.view().region(1).addLabel(0.0,0.95,"a)").addLabel(0.0,0.8,"45 nA");
    c.view().region(2).draw(h95_1).draw(h00_0,"EPsame").joinX().wrapX();
    c.view().region(2).draw(leg95);
    c.view().region(2).addLabel(0.0,0.95,"b)").addLabel(0.0,0.8,"95 nA");

    c.view().region(3).draw(h150_1).draw(h00_0,"EPsame").wrapX();
    c.view().region(3).draw(leg150);
    c.view().region(3).addLabel(0.0,0.95,"c)").addLabel(0.0,0.8,"150 nA");
}

if(mode==1){
    c.view().region(1).draw(h45_1a,"same").draw(h45_1,"same").draw(h00_0,"EPsame").joinX().wrapX();
    c.view().region(1).draw(leg45);
    c.view().region(1).addLabel(0.0,0.95,"a)").addLabel(0.0,0.8,"45 nA");
    c.view().region(2).draw(h95_1).draw(h95_1a,"same").draw(h00_0,"EPsame").joinX().wrapX();
    c.view().region(2).draw(leg95);
    c.view().region(2).addLabel(0.0,0.95,"b)").addLabel(0.0,0.8,"95 nA");

    c.view().region(3).draw(h150_1).draw(h150_1a,"same").draw(h00_0,"EPsame").wrapX();
    c.view().region(3).draw(leg150);
    c.view().region(3).addLabel(0.0,0.95,"c)").addLabel(0.0,0.8,"150 nA");
}

if(mode==2){
    c.view().region(1).draw(h45_3).draw(h45_1a,"same").draw(h45_1,"same").draw(h00_0,"EPsame").joinX().wrapX();
    c.view().region(1).draw(leg45);
    c.view().region(1).addLabel(0.0,0.95,"a)").addLabel(0.0,0.8,"45 nA");

    c.view().region(2).draw(h95_3).draw(h95_1a,"same").draw(h95_1,"same").draw(h00_0,"EPsame").joinX().wrapX();
    c.view().region(2).draw(leg95);
    c.view().region(2).addLabel(0.0,0.95,"b)").addLabel(0.0,0.8,"95 nA");

    c.view().region(3).draw(h150_3).draw(h150_1a,"same").draw(h150_1,"same").draw(h00_0,"EPsame").wrapX();
    c.view().region(3).draw(leg150);
    c.view().region(3).addLabel(0.0,0.95,"c)").addLabel(0.0,0.8,"150 nA");
}

c.view().region(4).setBlank();
//c.view().addLabels(0.92,0.95);
//c.view().addLabels(0.0,0.95,new String[]{" ","45 nA", "95 nA","150 nA"});
c.repaint();

double[]  x = new double[]{45.0, 95.0, 150.0};
double[] yc = new double[3];
double[] yd = new double[3];
double[] ya = new double[3];
double[] yf = new double[3];

yc[0] = h45_1.area(min,max)/h00_0.area(min,max);
yc[1] = h95_1.area(min,max)/h00_0.area(min,max);
yc[2] = h150_1.area(min,max)/h00_0.area(min,max);

yd[0] = h45_2.area(min,max)/h00_0.area(min,max);
yd[1] = h95_2.area(min,max)/h00_0.area(min,max);
yd[2] = h150_2.area(min,max)/h00_0.area(min,max);

ya[0] = h45_3.area(min,max)/h00_0.area(min,max);
ya[1] = h95_3.area(min,max)/h00_0.area(min,max);
ya[2] = h150_3.area(min,max)/h00_0.area(min,max);

yf[0] = h45_1a.area(min,max)/h00_0.area(min,max);
yf[1] = h95_1a.area(min,max)/h00_0.area(min,max);
yf[2] = h150_1a.area(min,max)/h00_0.area(min,max);

GraphErrors grc = new GraphErrors("gr",x,yc);
GraphErrors grd = new GraphErrors("gr",x,yd);
GraphErrors gra = new GraphErrors("gr",x,ya);
GraphErrors grf = new GraphErrors("gr",x,yf);

grc.attr().setMarkerSize(12);
grc.attr().setMarkerStyle(2);
grc.attr().setMarkerColor(4);
grc.attr().setLineColor(4);
grc.attr().setLineStyle(0);
grc.attr().setLineWidth(2);

grd.attr().setMarkerSize(18);
grd.attr().setMarkerStyle(3);
grd.attr().setMarkerColor(2);
grd.attr().setLineColor(2);
grd.attr().setLineStyle(0);
grd.attr().setLineWidth(2);

gra.attr().setMarkerSize(18);
gra.attr().setMarkerStyle(5);
gra.attr().setMarkerColor(6);
gra.attr().setLineColor(6);
gra.attr().setLineStyle(0);
gra.attr().setLineWidth(2);


grf.attr().setMarkerSize(18);
grf.attr().setMarkerStyle(5);
grf.attr().setMarkerColor(3);
grf.attr().setLineColor(3);
grf.attr().setLineStyle(0);
grf.attr().setLineWidth(2);

TGCanvas cr = new TGCanvas(400,500);
cr.view().divide(new double[][]{{0.05,0.91,0.13}});
grc.attr().setTitleX("Beam Current (nA)");
grc.attr().setTitleY("Reconstructed Fraction");
Line line = new Line(38.0,1.0,162,1.0);
Legend leg = new Legend(0.3,0.98);
leg.add(grc,"ratio - conv/45nA");
leg.add(grd,"ratio - denoised conv/45nA");
leg.add(gra,"ratio - denoised ai/45nA");
leg.add(grf,"ratio - ai/45nA");

cr.region(0).setBlank();
if(mode==0){
 cr.view().region(1).draw(grc,"PL").wrapX();//.draw(grf,"PLsame").draw(grd,"PLsame").draw(gra,"PLsame").wrapX();
}

if(mode==1){
    cr.view().region(1).draw(grc,"PL").draw(grf,"PLsame").wrapX();//.draw(grf,"PLsame").draw(grd,"PLsame").draw(gra,"PLsame").wrapX();
}

if(mode==2){
 cr.view().region(1).draw(grc,"PL").draw(grf,"PLsame").draw(grd,"PLsame").draw(gra,"PLsame").wrapX();
}
cr.region(1).getInsets().bottom(30);
cr.region(1).draw(line);
cr.region(2).setBlank();
//cr.view().addLabels(0.9,0.98,new String[]{"","a)"});

cr.view().cd(1).region().getAxisFrame().getAxisY().setFixedLimits(0.0,1.65);
cr.view().cd(1).region().getAxisFrame().getAxisX().setFixedLimits(38.0,162);
cr.view().region(1).draw(leg);
cr.view().cd(1).region().addLabel(0.9,0.98,"d)");
//c.view().cd(0).region().getAxisFrame().getAxisX().setFixedLimits(0.0,160.0);
//cr.view().region().axisLimitsY(0.0,1.2);
//cr.view().region().axisLimitsY(0.0,160);

cr.repaint();
c.view().export("plots_mxepipi_dn_ai_full_ns.pdf","pdf");
cr.view().export("graph_mxepipi_dn_ai_full_ns.pdf","pdf");
