

H1F h = TDataFactory.createH1F(2500,120,0.0,1.0,0.6,0.05);

F1D func = new F1D("func","[a]+[b]*x+[c]*x*x+[d]*gaus(x,[e],[f])",0.0,1.0);
func.setParameters(new double[]{1.0,1.0,1.0,150,0.6,0.02});
func.setParLimits(3,0,1500);
func.setParLimits(4,0.2,0.8);
func.setParLimits(5,0.0,0.05);
func.attr().setLineWidth(2);
func.attr().setLineStyle(2);
func.fit(h,"N");

PaveText    paveStats = new PaveText(func.getStats("M"),0.05,0.95, false,18);
paveStats.setNDF(true).setMultiLine(true);

TGCanvas c = new TGCanvas(600,550);
c.view().region(0).draw(h).draw(func,"same").draw(paveStats);
c.repaint();
