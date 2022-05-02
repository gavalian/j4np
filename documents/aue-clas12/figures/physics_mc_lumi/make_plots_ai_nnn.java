//---- script

TDirectory dir = new TDirectory();
dir.read("physics_lumi.twig");

double min = 0.85;
double max = 1.05;

H1F h00_0 = (H1F) dir.get("/results/bc000/h1000");
H1F h45_1 = (H1F) dir.get("/results/bc045/h1000");
H1F h45_2 = (H1F) dir.get("/results/bc045dn/h1000");
H1F h45_3 = (H1F) dir.get("/results/bc045dn/h2000");

H1F h95_1 = (H1F) dir.get("/results/bc095/h1000");
H1F h95_2 = (H1F) dir.get("/results/bc095dn/h1000");
H1F h95_3 = (H1F) dir.get("/results/bc095dn/h2000");

H1F h150_1 = (H1F) dir.get("/results/bc150/h1000");
H1F h150_2 = (H1F) dir.get("/results/bc150dn/h1000");
H1F h150_3 = (H1F) dir.get("/results/bc150dn/h2000");

GraphErrors gr0na = h00_0.getGraph();
gr0na.attr().setMarkerSize(8);
h00_0.attr().setMarkerSize(4);

h45_1.attr().setLineColor(4);
h45_1.attr().setFillColor(84);
h45_1.attr().setFillStyle(2);

h45_2.attr().setLineColor(2);
h95_1.attr().setLineColor(4);
h95_1.attr().setFillColor(84);
h95_1.attr().setFillStyle(2);
h95_2.attr().setLineColor(2);
//h95_2.attr().setFillColor(57);

h150_1.attr().setLineColor(4);
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

leg45.add(gr0na,"conventional 0 nA");
leg45.add(h45_1,"conventional 45 nA");
//leg45.add(h45_2,"de-noised conv. 45 nA");
leg45.add(h45_3,"de-noised ai 45 nA");

Legend leg95 = new Legend(0.5,0.96);

leg95.add(gr0na,"conventional 0 nA");
leg95.add(h95_1,"conventional 95 nA");
leg95.add(h95_3,"de-noised conv. 95 nA");

Legend leg150 = new Legend(0.5,0.96);

leg150.add(gr0na,"conventional 0 nA");
leg150.add(h150_1,"conventional 150 nA");
//leg150.add(h150_2,"de-noised conv. 150 nA");
leg150.add(h150_3,"de-noised ai 150 nA");

c.view().region(1).draw(h45_3).draw(h45_1,"same").draw(h00_0,"EPsame").joinX().wrapX();
c.view().region(1).draw(leg45);
c.view().region(1).addLabel(0.0,0.95,"a)").addLabel(0.0,0.8,"45 nA");

c.view().region(2).draw(h95_3).draw(h95_1,"same").draw(h00_0,"EPsame").joinX().wrapX();
c.view().region(2).draw(leg95);
c.view().region(2).addLabel(0.0,0.95,"b)").addLabel(0.0,0.8,"95 nA");

c.view().region(3).draw(h150_3).draw(h150_1,"same").draw(h00_0,"EPsame").wrapX();
c.view().region(3).draw(leg150);
c.view().region(3).addLabel(0.0,0.95,"c)").addLabel(0.0,0.8,"150 nA");

c.view().region(4).setBlank();
//c.view().addLabels(0.92,0.95);
//c.view().addLabels(0.0,0.95,new String[]{" ","45 nA", "95 nA","150 nA"});
c.repaint();

double[]  x = new double[]{45.0, 95.0, 150.0};
double[]  xc = new double[]{45.0, 95.0};
double[] yc = new double[2];
double[] yd = new double[3];
double[] ya = new double[3];

yc[0] = h45_1.area(min,max)/h00_0.area(min,max);
yc[1] = h95_1.area(min,max)/h00_0.area(min,max);
//yc[2] = h150_1.area(min,max)/h00_0.area(min,max);

yd[0] = h45_2.area(min,max)/h00_0.area(min,max);
yd[1] = h95_2.area(min,max)/h00_0.area(min,max);
yd[2] = h150_2.area(min,max)/h00_0.area(min,max);

ya[0] = h45_3.area(min,max)/h00_0.area(min,max);
ya[1] = h95_3.area(min,max)/h00_0.area(min,max);
ya[2] = h150_3.area(min,max)/h00_0.area(min,max);

GraphErrors grc = new GraphErrors("gr",xc,yc);
GraphErrors grd = new GraphErrors("gr",x,yd);
GraphErrors gra = new GraphErrors("gr",x,ya);

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

TGCanvas cr = new TGCanvas(400,500);
cr.view().divide(new double[][]{{0.05,0.91,0.13}});
grc.attr().setTitleX("Beam Current (nA)");
grc.attr().setTitleY("Reconstructed Fraction");
Line line = new Line(38.0,0.70,162,0.70);
Legend leg = new Legend(0.2,0.98);
leg.add(grc,"ratio - conv/0nA");
leg.add(grd,"ratio - denoised conv/0nA");
leg.add(gra,"ratio - denoised ai/0nA");

cr.region(0).setBlank();
cr.view().region(1).draw(grc,"PL").draw(grd,"PLsame").draw(gra,"PLsame").wrapX();
cr.region(1).getInsets().bottom(30);
cr.region(1).draw(line);
cr.region(2).setBlank();
//cr.view().addLabels(0.9,0.98,new String[]{"","a)"});

cr.view().cd(1).region().getAxisFrame().getAxisY().setFixedLimits(0.0,1.11);
cr.view().cd(1).region().getAxisFrame().getAxisX().setFixedLimits(38.0,162);
cr.view().region(1).draw(leg);
cr.view().cd(1).region().addLabel(0.9,0.98,"d)");
//c.view().cd(0).region().getAxisFrame().getAxisX().setFixedLimits(0.0,160.0);
//cr.view().region().axisLimitsY(0.0,1.2);
//cr.view().region().axisLimitsY(0.0,160);

cr.repaint();
c.view().export("plots_mxepipi_dn_ai_ns.pdf","pdf");
cr.view().export("graph_mxepipi_dn_ai_ns.pdf","pdf");
