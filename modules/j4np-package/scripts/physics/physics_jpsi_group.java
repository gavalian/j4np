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
DataGroup grp = new DataGroup();

grp.add(new H1F("hmass",50,2.5,3.3));
grp.add(new H1F("hphi",120,-6.28,6.28));
grp.add(new H1F("helep",120,0.0,9.5));
grp.add(new H1F("hposp",120,0.0,9.5));
grp.add(new H1F("hgamma",120,-5,5));

DataGroup[] grp2 = grp.duplicate("cut1","cut2");

grp.fillColors(42,42,42,42,42);
grp2[0].fillColors(43,43,43,43,43);
grp2[1].fillColors(44,44,44,44,44);

TGCanvas c = new TGCanvas(900,800);
c.view().divide(2,3);

grp.draw(c);
grp2[0].draw(c);
grp2[1].draw(c);

c.view().initTimer(1000);
//c.region().showLegend(0.05,0.98);

while(react.next()==true){
    double egamma = react.getValue("egamma");
    double   mass = react.getValue("mee");
    
    if(mass>2.0){
	grp.fill(react.getValue("mee"),
		 react.getValue("elef") - react.getValue("posf"),
		 react.getValue("posp"),
		 react.getValue("elep"),
		 react.getValue("egamma")
		 );
	if(egamma<0){
	    grp2[0].fill(react.getValue("mee"),
                 react.getValue("elef") - react.getValue("posf"),
                 react.getValue("posp"),
                 react.getValue("elep"),
                 react.getValue("egamma"));
	} else {
	     grp2[1].fill(react.getValue("mee"),
                 react.getValue("elef") - react.getValue("posf"),
                 react.getValue("posp"),
                 react.getValue("elep"),
                 react.getValue("egamma"));
	}
    }
}


//TDirectory dir = new TDirectory();
//dir.add("/analysis/jpsi",hmass,hposp,helep,hphi,hgamma);
//dir.write("datahistogram.twig");
//--------------------------------------------------
// To browser through saved histograms use command
// prompt> ../../bin/j4shell.sh
// jshell> TwigStudio.browser("datahistogram.twig");
//-------------------------------------------------

