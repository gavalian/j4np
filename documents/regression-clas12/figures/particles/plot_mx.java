TDirectory dir = new TDirectory("inference.twig");

TGCanvas c = new TGCanvas(1200,450);
c.view().divide(2,1);

H1F hd = (H1F) dir.get("/results2c/hdata_1n1p");
H1F hi = (H1F) dir.get("/results2c/hreg_1n1p");

hd.attr().setLegend("clas12 reconstruction (HB)");
hi.attr().setLegend("neural network reconstruction");
//hd.attr().setFillColor(54);
hd.attr().set("fc=54,lc=4");
hd.attr().setFillStyle(2);

c.view().region(0).draw(hd).showLegend(0.05,0.98);
c.view().region(0).draw(hi,"same").showLegend(0.05,0.98);
c.view().region(1).draw(hi,"same").showLegend(0.05,0.98);
c.repaint();
c.view().export("compare_mx_1n1p.pdf","pdf");