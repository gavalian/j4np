TDirectory dir = new TDirectory("inference.twig");

List<String[]> names = new ArrayList<>();

names.add(new String[]{"hdata_emom","hreg_emom"});
names.add(new String[]{"hdata_eth","hreg_eth"});
names.add(new String[]{"hdata_ephi","hreg_ephi"});
names.add(new String[]{"hdata_pmom","hreg_pmom"});
names.add(new String[]{"hdata_pth","hreg_pth"});
names.add(new String[]{"hdata_pphi","hreg_pphi"});

String parent = "/results2c";

TGCanvas c = new TGCanvas(1200,700);
c.view().divide(3,2);

for(int i = 0; i < names.size();i++){
   String[] n = names.get(i);
   H1F hd = (H1F) dir.get(parent+"/"+n[0]);
   H1F hi = (H1F) dir.get(parent+"/"+n[1]);
   hd.attr().setFillColor(4);
   if(i>2) hd.attr().setFillColor(2);
   hd.attr().setFillStyle(2);
   hi.attr().setLineWidth(2);
   hd.attr().setLegend("simulated data");
   hi.attr().setLegend("ai inferred data");
   c.view().region(i).draw(hd).draw(hi,"same");
   c.view().region(i).showLegend(0.05,0.98);
}
c.repaint();
c.view().export("particle_params.pdf","pdf");
