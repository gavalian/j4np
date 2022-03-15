

LuminosityClass lc = new LuminosityClass(new double[]{0.0,45.0,95.0});

GraphErrors grsp = lc.makeGraphNorm(new double[]{10164,8869,7048}, new double[]{9809,7364,4874});
GraphErrors grsn = lc.makeGraphNorm(new double[]{10164,8869,7048}, new double[]{1937,1441,991});

F1D fsp = lc.getFit(grsp,"N");
F1D fsn = lc.getFit(grsn,"N");

GraphErrors grep = lc.makeGraphNorm(new double[]{1905,1648,1296}, new double[]{2481,1889,1217});
GraphErrors gren = lc.makeGraphNorm(new double[]{1905,1648,1296}, new double[]{793,591,394});


grep.attr().setMarkerColor(4);
gren.attr().setMarkerColor(4);

F1D fep = lc.getFit(grep,"N");
F1D fen = lc.getFit(gren,"N");

fen.attr().setLineColor(4);
fep.attr().setLineColor(4);

gren.attr().setLegend("H(e,e^-#pi^+)X negative");
grep.attr().setLegend("H(e,e^-#pi^+)X positive");

grsn.attr().setLegend("SIDIS negative");
grsp.attr().setLegend("SIDIS positive");

fsp.attr().setLegend(String.format("func, b = %.6f",fsp.getParameter(1)));
fsn.attr().setLegend(String.format("func, b = %.6f",fsn.getParameter(1)));

fep.attr().setLegend(String.format("func, b = %.6f",fep.getParameter(1)));
fen.attr().setLegend(String.format("func, b = %.6f",fen.getParameter(1)));

GraphErrors grop = lc.makeGraphNorm(new double[]{168950,149172,119606}, new double[]{218968,168777,113304});
GraphErrors gron = lc.makeGraphNorm(new double[]{168950,149172,119606}, new double[]{69922,54859,37274});

F1D fop = lc.getFit(grop,"N");
F1D fon = lc.getFit(gron,"N");

gron.attr().setLegend("H(e,e^-#pi^+)X negative FULL");
grop.attr().setLegend("H(e,e^-#pi^+)X positive FULL");

grop.attr().setMarkerColor(5);
gron.attr().setMarkerColor(5);
fon.attr().setLineColor(5);
fop.attr().setLineColor(5);
fop.attr().setLegend(String.format("func, b = %.6f",fop.getParameter(1)));
fon.attr().setLegend(String.format("func, b = %.6f",fon.getParameter(1)));

TGCanvas c = new TGCanvas(900,600);

grsp.attr().setTitleX("beam current (nA)");
grsn.attr().setTitleX("beam current (nA)");

c.view().divide(2,1);
c.view().region(0).draw(grsp).draw(fsp,"same");
c.view().region(1).draw(grsn,"same").draw(fsn,"same");

c.view().region(0).draw(grep,"same").draw(fep,"same");
c.view().region(1).draw(gren,"same").draw(fen,"same");

c.view().region(0).draw(grop,"same").draw(fop,"same");
c.view().region(1).draw(gron,"same").draw(fon,"same");

c.repaint();

fep.show();
fsp.show();
