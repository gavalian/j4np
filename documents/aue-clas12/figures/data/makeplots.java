import java.awt.Font;
import j4np.utils.io.TextFileReader;


TextFileReader r = new TextFileReader();
r.open("h102.txt");
List<String> lines = r.readLines(100);

H1F h = new H1F("h",100,0.,3.0);

for(int i = 0; i < lines.size();i++){
    String[] tokens = lines.get(i).split("\\s+");
    h.setBinContent(i,Double.parseDouble(tokens[1]));
    h.setBinError(i,Double.parseDouble(tokens[2]));
}


F1D f = new F1D("f","[p0]+[p1]*x+[p2]*x*x+[p3]*gaus(x,[mean],[sigma])",0.8,1.6);
f.setParameter(3,20);
f.setParameter(4,0.938);
f.setParameter(5,0.04);

DataFitter.fit(f,h,"N");

TGCanvas c = new TGCanvas();

c.view().region().draw(h).draw(f,"same");;
c.repaint();

System.out.printf("area = %f\n",f.getParameter(3)*f.getParameter(5));
