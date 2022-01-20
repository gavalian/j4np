import java.awt.Font;
import j4np.utils.io.TextFileReader;


TextFileReader r = new TextFileReader();
r.open("h100.txt");
List<String> lines = r.readLines(100);

H1F h = new H1F("h",100,0.,3.0);

for(int i = 0; i < lines.size();i++){
    String[] tokens = lines.get(i).split("\\s+");
    h.setBinContent(i,Double.parseDouble(tokens[1]));
    h.setBinError(i,Double.parseDouble(tokens[2]));
}

TextFileReader r2 = new TextFileReader();
r2.open("h101.txt");
List<String> lines2 = r2.readLines(100);

H1F h2 = new H1F("h",100,0.,3.0);

for(int i = 0; i < lines2.size();i++){
    String[] tokens = lines2.get(i).split("\\s+");
    h2.setBinContent(i,Double.parseDouble(tokens[1]));
    h2.setBinError(i,Double.parseDouble(tokens[2]));
}

TextFileReader r3 = new TextFileReader();
r3.open("h102.txt");
List<String> lines3 = r3.readLines(100);

H1F h3 = new H1F("h",100,0.,3.0);

for(int i = 0; i < lines3.size();i++){
    String[] tokens = lines3.get(i).split("\\s+");
    h3.setBinContent(i,Double.parseDouble(tokens[1]));
    h3.setBinError(i,Double.parseDouble(tokens[2]));
}

h.attr().setTitleX("M(x) (e^-#pi^+#pi^-) [GeV]");
h.attr().setLineColor(2);
h2.attr().setLineColor(5);
TGCanvas c = new TGCanvas();

c.view().region().draw(h).draw(h2,"same").draw(h3,"same");
c.repaint();
c.view().export("figure_denoise_expdata.pdf","pdf");

