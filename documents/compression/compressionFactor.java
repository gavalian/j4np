double[] xa = new double[]{2.0, 4.0,6.0,8.0};
double[] dd = new double[]{1.0, 1.4,2.3,4.5};
double[] ds = new double[]{0.4, 0.7,1.4,1.8};
double[] dr = new double[]{1.2, 1.6,3.4,5.6};


GraphErrors gdd = new GraphErrors("gdouble",xa,dd);
GraphErrors gds = new GraphErrors("gsingle",xa,ds);
GraphErrors gdr = new GraphErrors("graw"   ,xa,dr);


gdd.attr().set("ms=12,mc=2,mt=2,lc=2,lw=2");
gds.attr().set("ms=12,mc=3,mt=3,lc=3,lw=2");
gdr.attr().set("ms=12,mc=4,mt=4,lc=4,lw=2");

gdd.attr().setTitleX("Compression Factor");
gdd.attr().setTitleY("Pulse Integral Resolution (%)");

gdd.attr().setLegend("double peaks (simulated)");
gds.attr().setLegend("single peaks (simulated)");
gdr.attr().setLegend("raw data pulses");

TGCanvas c = new TGCanvas(900,500);
c.draw(gdd,"PL").draw(gds,"PLsame").draw(gdr,"PLsame");
