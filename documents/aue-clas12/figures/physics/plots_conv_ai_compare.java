//************************************************** 
//*--#$ String command = System.getProperty("a");
//*--#$ System.out.println(command);
//**************************************************

TGCanvas c = new TGCanvas(500,500);
c.view().divide(1,3);

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

h23.attr().setFillColor(-1);
h33.attr().setFillColor(-1);
h43.attr().setFillColor(-1);

h1.attr().setFillColor(-1);
h1.attr().setLineColor(3);


c.view().region(0).draw(h2).draw(h23,"same").draw(h1,"same");
c.view().region(0).getAxisFrame().getAxisY().setFixedLimits(0.,75);
c.view().region(1).draw(h3).draw(h33,"same").draw(h1,"same");
c.view().region(1).getAxisFrame().getAxisY().setFixedLimits(0.,75);
c.view().region(2).draw(h4).draw(h43,"same").draw(h1,"same");
c.view().region(2).getAxisFrame().getAxisY().setFixedLimits(0.,75);

c.repaint();

c.view().export("figure_phys_conv_ai_compare.pdf","pdf");