

LuminosityClass lc = new LuminosityClass(new double[]{0.0,45.0,95.0,150.0});

GraphErrors gc = lc.makeGraphNorm(new double[]{168950,149172,119606,12023}, new double[]{218968,168777,113304,10349});
GraphErrors gd = lc.makeGraphNorm(new double[]{168950,159661,147170,131524}, new double[]{218968,190985,158105,122738});
GraphErrors ga = lc.makeGraphNorm(new double[]{168950,165809,158249,147587}, new double[]{218968,208376,185561,157137});

F1D fc = lc.getFit(gc,"N");
F1D fd = lc.getFit(gd,"N");


fc.attr().setLegend(String.format("func, b = %.6f",fc.getParameter(1)));
fd.attr().setLegend(String.format("func, b = %.6f",fd.getParameter(1)));

TGCanvas c = new TGCanvas(900,600);

gc.attr().setTitleX("beam current (nA)");
gc.attr().setTitleX("beam current (nA)");

c.view().divide(2,1);
c.view().region(0).draw(gc,"EP").draw(fc,"same");
c.view().region(1).draw(gd,"EP").draw(fd,"same");


c.repaint();


