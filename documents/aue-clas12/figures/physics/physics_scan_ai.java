//import org.jlab.groot.ui.*;
//import org.jlab.groot.data.*;
//import org.jlab.groot.math.*;
//import org.jlab.groot.fitter.*;
//import org.jlab.jnp.groot.graphics.*;
//import org.jlab.jnp.groot.settings.*;
import java.awt.Font;

int marker_size = 12;

GraphErrors convOut = new GraphErrors("conv",
                  new double[] {1.0,45.0,95.0,150.0},
                  new double[]{1.0,0.593,0.347,0.047},
                  new double[]{0.0009,0.0007,0.0004,0.0005}
                  );

GraphErrors  dnOut = new GraphErrors("dn",
                  new double[] {1.0,45.0,95.0,150.0},
                  new double[]{1.0,0.813,0.604,0.351},
                  new double[]{0.0009,0.0006,0.0004,0.0004}
                  );

GraphErrors  aiOut = new GraphErrors("ai",
                  new double[] {1.0,45.0,95.0,150.0},
                  new double[]{1.0,0.857,0.677,0.476},
                  new double[]{0.0009,0.0006,0.0004,0.0004}
                  );

TStyle.getInstance().getPalette().setColorScheme("gold10");


TGCanvas c = new TGCanvas("figure_2",600,540);
c.view().divide(1,1);

setStyle1x1(c,10,10);
convOut.attr().setTitleX("Beam Current (nA)");
convOut.attr().setTitleY("Tracking Efficiency");

convOut.attr().setMarkerColor(7);
convOut.attr().setLineColor(7);
convOut.attr().setLineStyle(1);
convOut.attr().setMarkerSize(marker_size);
convOut.attr().setMarkerStyle(3);

dnOut.attr().setMarkerColor(11);
dnOut.attr().setLineColor(11);
dnOut.attr().setMarkerSize(marker_size);

aiOut.attr().setMarkerColor(12);
aiOut.attr().setLineColor(12);
aiOut.attr().setMarkerStyle(4);
aiOut.attr().setMarkerSize(marker_size);

c.view().region().draw(convOut,"PL").draw(dnOut,"samePL").draw(aiOut,"samePL");

F1D line = new F1D("line","[a]",0.0,160);
line.setParameter(0,1.0);
line.attr().setLineColor(7);
line.attr().setLineStyle(0);

F1D convF1 = new F1D("conv","[a]+[b]*x",1.0,152);
convF1.setParameter(0,1.0);
convF1.setParameter(1,-0.005);
convF1.attr().setLineColor(7);
convF1.attr().setLineWidth(2);
convF1.attr().setLineStyle(0);

F1D dnF1 = new F1D("ai","[a]+[b]*x",1.0,152);
dnF1.setParameter(0,1.0);
dnF1.setParameter(1,-0.005);
dnF1.attr().setLineColor(11);
dnF1.attr().setLineWidth(2);
dnF1.attr().setLineStyle(0);

F1D aiF1 = new F1D("ai","[a]+[b]*x",1.0,152);
aiF1.setParameter(0,1.0);
aiF1.setParameter(1,-0.005);
aiF1.attr().setLineColor(12);
aiF1.attr().setLineWidth(2);
aiF1.attr().setLineStyle(1);

DataFitter.fit(convF1,convOut,"N");
DataFitter.fit(dnF1,dnOut,"N");
DataFitter.fit(aiF1,aiOut,"N");

//c.view().cd(0).region().draw(line,"same").draw(convF1,"samePL").draw(dnF1,"samePL").draw(aiF1,"same");

c.view().cd(0).region().getAxisFrame().getAxisX().setFixedLimits(0.0,160.0);
c.view().cd(0).region().getAxisFrame().getAxisY().setFixedLimits(0.0,1.11);

String text1 = String.format("f_c=%.2f%.6fx",convF1.getParameter(0),convF1.getParameter(1));
String text2 = String.format("f_d=%.2f%.6fx",dnF1.getParameter(0),dnF1.getParameter(1));
String text3 = String.format("f_a=%.2f%.6fx",aiF1.getParameter(0),aiF1.getParameter(1));

PaveText t1 = new PaveText(text1,0.05,0.2);
t1.setNDF(true);
t1.setTextColor(TStyle.getInstance().getPalette().getColor(7));
t1.setDrawBox(false);
t1.setFillBox(false);
t1.setFont(new Font("Avenir",Font.PLAIN,20));

PaveText t2 = new PaveText(text2,0.05,0.28);
t2.setNDF(true);
t2.setTextColor(TStyle.getInstance().getPalette().getColor(11));
t2.setDrawBox(false);
t2.setFillBox(false);
t2.setFont(new Font("Avenir",Font.PLAIN,20));

PaveText t3 = new PaveText(text3,0.05,0.36);
t3.setNDF(true);
t3.setTextColor(TStyle.getInstance().getPalette().getColor(1));
t3.setDrawBox(false);
t3.setFillBox(false);
t3.setFont(new Font("Avenir",Font.PLAIN,20));

PaveText ta = new PaveText("a)",0.92,1.0);
ta.setNDF(true);
ta.setDrawBox(false);
ta.setFillBox(false);
ta.setFont(new Font("Avenir",Font.PLAIN,20));

c.view().cd(0).region().draw(t1).draw(t2).draw(t3).draw(ta);

c.repaint();

c.view().export("figure_phys_scan_ai.pdf","pdf");
