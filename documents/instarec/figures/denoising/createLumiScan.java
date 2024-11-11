double[] pos_conv = new double[]{0.7232, 0.6875, 0.6328, 0.5705};
double[] pos_aias = new double[]{0.7599, 0.7349, 0.7008, 0.6463};
double[] pos_dn_conv = new double[]{0.7319, 0.7081, 0.6749, 0.6300};
double[] pos_dn_aias = new double[]{0.7585, 0.7398, 0.7203, 0.6822};
double[] x_axis = new double[]{5,15,30,50};

int[] colors = new int[]{2,3,4,5};

GraphErrors g_pos_dn_aias = new GraphErrors("g_pos_dn_aias",x_axis,pos_dn_aias);

F1D[] func = new F1D[4];

for(int i = 0; i < 4; i++){
    func[i] = new F1D("f_"+i,"[a]+[b]*x",0,60);
    func[i].attr().setLineColor(colors[i]);
    func[i].attr().setLineWidth(2);
}


func[0].fit(g_pos_dn_aias);

double norm = func[0].getParameter(0);

GraphErrors[] gg = new GraphErrors[4];
GraphErrors[] gn = new GraphErrors[4];

gg[0] = new GraphErrors("g",x_axis,pos_conv);
gg[1] = new GraphErrors("g",x_axis,pos_aias);
gg[2] = new GraphErrors("g",x_axis,pos_dn_conv);
gg[3] = new GraphErrors("g",x_axis,pos_dn_aias);

String[] labels = new String[]{"conventional","ai-assised","denoised/conventional","denoised/ai-assisted"};
Legend leg = new Legend(0.5,0.98);

for(int i = 0; i < 4; i++){
    gn[i] = gg[i].divide(norm);
    gn[i].attr().setTitleX("Beam Current [nA]");
    gn[i].attr().setTitleY("Track Efficiency");
    gn[i].attr().setMarkerColor(colors[i]);
    gn[i].attr().setLineColor(colors[i]);
    gn[i].attr().setLineWidth(2);
    gn[i].attr().set("ms=12");
    gn[i].attr().setMarkerStyle(i+1);;
    func[i].fit(gn[i]);
    gn[i].attr().setLegend(String.format("%s (slope=%.5f)",labels[i],func[i].getParameter(1)));
    leg.add(gn[i]);//,String.format("%s (slope=%.5f)",labels[i],func[i].getParameter(1)));
}


System.out.println("normalization = " + norm);
TGCanvas c = new TGCanvas("luminosity_scan",600,600);
c.draw(gn[0],"same");
c.draw(gn[3],"same");
c.region().showLegend(0.05,0.3);
c.draw(func[0],"same");
c.draw(func[3],"same");
/*
for(int i = 0; i < 4; i++) c.draw(gn[i],"same");//.draw(func[i],"same");
c.region().showLegend(0.05,0.3);
for(int i = 0; i < 4; i++) c.draw(func[i],"same");
*/
c.region().axisLimitsY(0.65,1.05);
c.repaint();
//c.region().draw(leg);
//c.draw(g_pos_dn_aias).draw(func[0],"same");
