//---- script

TDirectory dir = new TDirectory();
dir.read("mc_lumiscan.twig");

double min = 0.85;
double max = 1.05;

H1F h00_0 = (H1F) dir.get("/mxepipi/0000/h1000");
H1F h45_1 = (H1F) dir.get("/mxepipi/0045/h1000");
H1F h45_1A = (H1F) dir.get("/mxepipi/0045/h2000");
H1F h45_2 = (H1F) dir.get("/mxepipi/0045/h3000");
H1F h45_3 = (H1F) dir.get("/mxepipi/0045/h4000");

H1F h95_1 = (H1F) dir.get("/mxepipi/0095/h1000");
H1F h95_1A = (H1F) dir.get("/mxepipi/0095/h2000");
H1F h95_2 = (H1F) dir.get("/mxepipi/0095/h3000");
H1F h95_3 = (H1F) dir.get("/mxepipi/0095/h4000");

H1F h150_1 = (H1F) dir.get("/mxepipi/0150/h1000");
H1F h150_1A = (H1F) dir.get("/mxepipi/0150/h2000");
H1F h150_2 = (H1F) dir.get("/mxepipi/0150/h3000");
H1F h150_3 = (H1F) dir.get("/mxepipi/0150/h4000");

h45_1.attr().setLineColor(4);
h45_1.attr().setFillColor(-1);
h45_1A.attr().setLineColor(3);
h45_1A.attr().setFillColor(-1);
h45_1.attr().setLineWidth(2);
h45_1A.attr().setLineWidth(2);

h95_1.attr().setLineColor(4);
h95_1.attr().setFillColor(-1);
h95_1A.attr().setLineColor(3);
h95_1A.attr().setFillColor(-1);
h95_1.attr().setLineWidth(2);
h95_1A.attr().setLineWidth(2);

h150_1.attr().setLineColor(4);
h150_1.attr().setFillColor(-1);
h150_1A.attr().setLineColor(3);
h150_1A.attr().setFillColor(-1);
h150_1.attr().setLineWidth(2);
h150_1A.attr().setLineWidth(2);

h150_3.attr().setTitleX("Mx(e#pi^+#pi^-) [GeV]");
TGCanvas c = new TGCanvas(500,500);

c.view().divide(new double[][]{{0.05,0.28,0.28,0.28,0.13}});
c.view().region(0).setBlank();

c.view().region(1).draw(h45_1A).draw(h45_1,"same").draw(h00_0,"same").joinX().wrapX();
c.view().region(2).draw(h95_1A).draw(h95_1,"same").draw(h00_0,"same").joinX().wrapX();
c.view().region(3).draw(h150_1A).draw(h150_1,"same").draw(h00_0,"same").wrapX();
c.view().region(4).setBlank();
//c.view().addLabels(0.92,0.95);
c.view().addLabels(0.0,0.95,new String[]{" ","45 nA", "95 nA","150 nA"});
c.repaint();


double[]  x = new double[]{45.0, 95.0, 150.0};
double[] yc = new double[3];
double[] yd = new double[3];
double[] ya = new double[3];

double[] yca = new double[3];

yc[0] = h45_1.area(min,max)/h00_0.area(min,max);
yc[1] = h95_1.area(min,max)/h00_0.area(min,max);
yc[2] = h150_1.area(min,max)/h00_0.area(min,max);

yd[0] = h45_2.area(min,max)/h00_0.area(min,max);
yd[1] = h95_2.area(min,max)/h00_0.area(min,max);
yd[2] = h150_2.area(min,max)/h00_0.area(min,max);

ya[0] = h45_3.area(min,max)/h00_0.area(min,max);
ya[1] = h95_3.area(min,max)/h00_0.area(min,max);
ya[2] = h150_3.area(min,max)/h00_0.area(min,max);

yca[0] = h45_1A.area(min,max)/h00_0.area(min,max);
yca[1] = h95_1A.area(min,max)/h00_0.area(min,max);
yca[2] = h150_1A.area(min,max)/h00_0.area(min,max);

GraphErrors grc = new GraphErrors("gr",x,yc);
GraphErrors grd = new GraphErrors("gr",x,yd);
GraphErrors gra = new GraphErrors("gr",x,ya);
GraphErrors grca = new GraphErrors("gr",x,yca);

grc.attr().setMarkerSize(12);
grc.attr().setMarkerStyle(2);
grc.attr().setMarkerColor(4);
grc.attr().setLineColor(4);
grc.attr().setLineStyle(0);
grc.attr().setLineWidth(2);
grc.attr().setLegend("Conventional");

grd.attr().setMarkerSize(18);
grd.attr().setMarkerStyle(3);
grd.attr().setMarkerColor(4);
grd.attr().setLineColor(4);
grd.attr().setLineStyle(0);
grd.attr().setLineWidth(2);


grca.attr().setMarkerSize(18);
grca.attr().setMarkerStyle(3);
grca.attr().setMarkerColor(3);
grca.attr().setLineColor(3);
grca.attr().setLineStyle(0);
grca.attr().setLineWidth(2);
grca.attr().setLegend("AI-assisted");

TGCanvas cr = new TGCanvas(400,500);
cr.view().divide(new double[][]{{0.05,0.91,0.13}});
grc.attr().setTitleX("Beam Current (nA)");
grc.attr().setTitleY("Reconstructed Fraction");
Line line = new Line(38.0,0.73,162,0.73);

cr.region(0).setBlank();
cr.view().region(1).draw(grc,"PL").draw(grca,"PLsame").wrapX();

//.draw(gra,"PLsame").draw(grca,"PLsame").wrapX();

cr.region(1).getInsets().bottom(30);
cr.region(1).draw(line);
cr.region(2).setBlank();
//cr.view().addLabels(0.9,0.98,new String[]{"","a)"});
cr.view().cd(1).region().getAxisFrame().getAxisY().setFixedLimits(0.0,1.01);
cr.view().cd(1).region().getAxisFrame().getAxisX().setFixedLimits(38.0,162);
//c.view().cd(0).region().getAxisFrame().getAxisX().setFixedLimits(0.0,160.0);
//cr.view().region().axisLimitsY(0.0,1.2);
//cr.view().region().axisLimitsY(0.0,160);

cr.repaint();
c.view().export("plots_mxepipi_dn_ai.pdf","pdf");
cr.view().export("graph_mxepipi_dn_ai.pdf","pdf");