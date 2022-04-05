

LuminosityClass lc = new LuminosityClass(new double[]{0.0,45.0,95.0,150.0});

GraphErrors gc = lc.makeGraphNorm(new double[]{168950,149172,119606,12023}, new double[]{69922,54859,37274,3252});
GraphErrors gd = lc.makeGraphNorm(new double[]{168950,159661,147170,131524}, new double[]{69922,61133,50928,39919});
GraphErrors gd = lc.makeGraphNorm(new double[]{168950,165809,158249,147587}, new double[]{69922,66603,59744,50713});

F1D fc = lc.getFit(grsp,"N");
F1D fd = lc.getFit(grsn,"N");


fc.attr().setLegend(String.format("func, b = %.6f",fsp.getParameter(1)));
fd.attr().setLegend(String.format("func, b = %.6f",fsn.getParameter(1)));

TGCanvas c = new TGCanvas(900,600);

gc.attr().setTitleX("beam current (nA)");
gc.attr().setTitleX("beam current (nA)");

c.view().divide(2,1);
c.view().region(0).draw(gc).draw(fc,"same");
c.view().region(1).draw(gd).draw(fd,"same");


c.repaint();


