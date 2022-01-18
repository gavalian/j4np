//************************************************** 
//*--#$ String command = System.getProperty("a");
//*--#$ System.out.println(command);
//**************************************************

TGCanvas c = new TGCanvas(1200,550);
c.view().divide(4,2);

H1F h1 = (H1F) DataSetSerializer.load("histograms.twig","/data/conventional/mxepipi_10");
H1F h2 = (H1F) DataSetSerializer.load("histograms.twig","/data/conventional/mxepipi_10_45");
H1F h3 = (H1F) DataSetSerializer.load("histograms.twig","/data/conventional/mxepipi_10_95");
H1F h4 = (H1F) DataSetSerializer.load("histograms.twig","/data/conventional/mxepipi_10_150");

H1F h12 = (H1F) DataSetSerializer.load("histograms.twig","/data/conventional/mxepipi_10");
H1F h22 = (H1F) DataSetSerializer.load("histograms.twig","/data/denoised/mxepipi_10_dn_45.dataset");
H1F h32 = (H1F) DataSetSerializer.load("histograms.twig","/data/denoised/mxepipi_10_dn_95.dataset");
H1F h42 = (H1F) DataSetSerializer.load("histograms.twig","/data/denoised/mxepipi_10_dn_150.dataset");

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
c.repaint();

c.view().export("figure_phys_conv.pdf","pdf");