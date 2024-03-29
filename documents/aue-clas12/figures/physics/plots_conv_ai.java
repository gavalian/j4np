//************************************************** 
//*--#$ String command = System.getProperty("a");
//*--#$ System.out.println(command);
//**************************************************

//TGCanvas c = new TGCanvas(1200,825);
//c.view().divide(4,2);

TGCanvas c = new TGCanvas(800,650);
c.view().divide(4,2);
c.view().divisionsX(5);
c.view().left(40).right(10).top(10).bottom(50);

H1F h1 = (H1F) DataSetSerializer.load("histograms.twig","/data/conventional/mxepipi_10");
H1F h2 = (H1F) DataSetSerializer.load("histograms.twig","/data/conventional/mxepipi_10_45");
H1F h3 = (H1F) DataSetSerializer.load("histograms.twig","/data/conventional/mxepipi_10_95");
H1F h4 = (H1F) DataSetSerializer.load("histograms.twig","/data/conventional/mxepipi_10_150");

H1F h12 = (H1F) DataSetSerializer.load("histograms.twig","/data/conventional/mxepipi_10");
H1F h22 = (H1F) DataSetSerializer.load("histograms.twig","/data/denoised/mxepipi_10_dn_45.dataset");
H1F h32 = (H1F) DataSetSerializer.load("histograms.twig","/data/denoised/mxepipi_10_dn_95.dataset");
H1F h42 = (H1F) DataSetSerializer.load("histograms.twig","/data/denoised/mxepipi_10_dn_150.dataset");

H1F h13 = (H1F) DataSetSerializer.load("histograms.twig","/data/conventional/mxepipi_10");
H1F h23 = (H1F) DataSetSerializer.load("histograms.twig","/data/denoisedai/mxepipi_10_dn_ai_45.dataset");
H1F h33 = (H1F) DataSetSerializer.load("histograms.twig","/data/denoisedai/mxepipi_10_dn_ai_95.dataset");
H1F h43 = (H1F) DataSetSerializer.load("histograms.twig","/data/denoisedai/mxepipi_10_dn_ai_150.dataset");

h1.attr().setTitleX("M(x)(e^- #pi^+ #pi^-) [GeV]");
h2.attr().setTitleX("M(x)(e^- #pi^+ #pi^-) [GeV]");
h3.attr().setTitleX("M(x)(e^- #pi^+ #pi^-) [GeV]");
h4.attr().setTitleX("M(x)(e^- #pi^+ #pi^-) [GeV]");

h12.attr().setTitleX("M(x)(e^- #pi^+ #pi^-) [GeV]");
h22.attr().setTitleX("M(x)(e^- #pi^+ #pi^-) [GeV]");
h32.attr().setTitleX("M(x)(e^- #pi^+ #pi^-) [GeV]");
h42.attr().setTitleX("M(x)(e^- #pi^+ #pi^-) [GeV]");

c.view().region(0).draw(h1);
c.view().region(0).getAxisFrame().getAxisY().setFixedLimits(0.,75);
c.view().region(1).draw(h2);
c.view().region(1).getAxisFrame().getAxisY().setFixedLimits(0.,75);
c.view().region(2).draw(h3);
c.view().region(2).getAxisFrame().getAxisY().setFixedLimits(0.,75);
c.view().region(3).draw(h4);
c.view().region(3).getAxisFrame().getAxisY().setFixedLimits(0.,75);

c.view().region(4).draw(h12);
c.view().region(4).getAxisFrame().getAxisY().setFixedLimits(0.,75);
c.view().region(5).draw(h22);
c.view().region(5).getAxisFrame().getAxisY().setFixedLimits(0.,75);
c.view().region(6).draw(h32);
c.view().region(6).getAxisFrame().getAxisY().setFixedLimits(0.,75);
c.view().region(7).draw(h42);
c.view().region(7).getAxisFrame().getAxisY().setFixedLimits(0.,75);

h13.attr().setFillColor(-1);
h23.attr().setFillColor(-1);
h33.attr().setFillColor(-1);
h43.attr().setFillColor(-1);

//h13.attr().setLineColor(1);
h23.attr().setLineColor(5);
h33.attr().setLineColor(5);
h43.attr().setLineColor(5);

c.view().region(4).draw(h13,"same");
//c.view().region(8).getAxisFrame().getAxisY().setFixedLimits(0.,75);
c.view().region(5).draw(h23,"same");
//c.view().region(9).getAxisFrame().getAxisY().setFixedLimits(0.,75);
c.view().region(6).draw(h33,"same");
//c.view().region(10).getAxisFrame().getAxisY().setFixedLimits(0.,75);
c.view().region(7).draw(h43,"same");
//c.view().region(11).getAxisFrame().getAxisY().setFixedLimits(0.,75);
c.repaint();

c.view().export("figure_phys_conv_ai.pdf","pdf");