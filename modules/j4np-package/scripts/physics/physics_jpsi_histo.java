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
//react.export("jpsi_ntuple.csv","mee>2.5");

H1F hmass = new H1F("hmass",50,2.5,3.3);
hmass.attr().setTitleX("M(e^-e^+) [GeV]");
hmass.attr().set("fc=3");

H1F hphi = new H1F("hphi",120,-6.28,6.28);
hphi.attr().setTitleX("#phi_e_--#phi_e_+ [rad]");
hphi.attr().set("fc=44,lc=4");

H1F helep = new H1F("helep",120,0.0,9.5);
helep.attr().setTitleX("P_e_- [GeV]");
helep.attr().set("fc=52,lc=2");

H1F hposp = new H1F("hposp",120,0.0,9.5);
hposp.attr().setTitleX("P_e_+ [GeV]");
hposp.attr().set("fc=52,lc=2");

H1F hgamma = new H1F("hgamma",120,-5,5);
hgamma.attr().setTitleX("E_#gamma [GeV^2]");
hgamma.attr().set("fc=49,lc=9");


TGCanvas c = new TGCanvas(900,800);
c.view().divide(2,2);
c.cd(0).draw(hmass).cd(1).draw(hphi);
c.cd(2).draw(hposp).cd(3).draw(hgamma);

c.view().initTimer(1000);
//c.region().showLegend(0.05,0.98);

while(react.next()==true){
    double mass = react.getValue("mee");
    if(mass>2.0){
	hmass.fill(react.getValue("mee"));
	hphi.fill(react.getValue("elef") - react.getValue("posf"));
	
	hgamma.fill(react.getValue("egamma"));
	hposp.fill(react.getValue("posp"));
	helep.fill(react.getValue("elep"));
    }
}


TDirectory dir = new TDirectory();
dir.add("/analysis/jpsi",hmass,hposp,helep,hphi,hgamma);
dir.write("datahistogram.twig");
//--------------------------------------------------
// To browser through saved histograms use command
// prompt> ../../bin/j4shell.sh
// jshell> TwigStudio.browser("datahistogram.twig");
//-------------------------------------------------

