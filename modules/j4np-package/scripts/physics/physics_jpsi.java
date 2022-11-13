//************************************************** 
//*--#$ String command = System.getProperty("a");
//*--#$ System.out.println(command);
//**************************************************
import j4np.physics.VectorOperator.OperatorType;

PhysicsReaction react = new PhysicsReaction("11:-11:2212:Xn",10.6);
LorentzVector   vect = LorentzVector.withPxPyPzM(0.0,0.,0.0,0.938);

react.addVector("[11]+[-11]");
react.addVector("[11]");
react.addVector("[-11]");
react.addVector("[2212]");
react.addVector(vect,"-[11]-[-11]-[2212]");

react.addEntry("mee",  0, OperatorType.MASS);
react.addEntry("elep", 1, OperatorType.P);
react.addEntry("elef", 1, OperatorType.PHI);

react.addEntry("posp", 2, OperatorType.P);
react.addEntry("posf", 2, OperatorType.PHI);

react.addEntry("pp", 3, OperatorType.P);
react.addEntry("pf", 3, OperatorType.PHI);

react.addEntry("egamma", 4, OperatorType.MASS2);


HipoReader r = new HipoReader("infile.hipo");

react.setDataSource(r, "REC::Particle");
react.addModifier(PhysicsReaction.FORWARD_ONLY);

// creates an CSV file for further analysis
react.export("jpsi_ntuple.csv","mee>2.5");

/*
H1F h = new H1F("h",50,2.5,3.3);
h.attr().setLegend("H(e^-,e^-e^+p)X");
h.attr().setTitleX("M(e^-e^+) [GeV]");
h.attr().set("fc=3");
TGCanvas c = new TGCanvas();
c.draw(h).draw(h,"EPsame");
c.view().initTimer(1000);
c.region().showLegend(0.05,0.98);

while(react.next()==true){
    h.fill(react.getValue("mee"));
}
*/

