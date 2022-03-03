
HipoTree t = HipoTree.withFile("halldfdc_022.h5","raw::data");
H1F     h1 = t.geth("charge","slot==3&&channel==11",120,3000,8000);

F1D func = new F1D("func","[a]+[b]*x+[c]*x*x+[d]*gaus(x,[e],[f])",3500,6600);
func.setParameters(new double[]{1.0,1.0,1.0,10000,6000,500});

func.setParLimits(3,0,40000);
func.setParLimits(4,5500,6500);
func.setParLimits(5,0.0,1000);
func.attr().setLineWidth(2);
DataFitter.fit(func,h1,"N");

PaveText    paveStats = new PaveText(func.getStats("M"),0.05,0.95, false,18);
paveStats.setNDF(true).setMultiLine(true);

TGCanvas c = new TGCanvas(600,550);
c.view().region(0).draw(h1).draw(func,"same").draw(paveStats);
c.repaint();
