//************************************************** 
//*--#$ String command = System.getProperty("a");
//*--#$ System.out.println(command);
//**************************************************
import j4np.physics.VectorOperator.OperatorType;

PhysicsReaction react = new PhysicsReaction("11:-11:2212:Xn",10.6);
LorentzVector   vect = LorentzVector.withPxPyPzM(0.0,0.,0.0,0.938);
LorentzVector    vcm = LorentzVector.withPxPyPzM(0.,0.,10.6,0.0005).add(0.0,0.,0.0,0.938);
vcm.invert();

react.addVector("[11]+[-11]");
react.addVector("[11]");
react.addVector("[-11]");
react.addVector("[2212]");
react.addVector(vect,"-[11]-[-11]-[2212]");
react.addVector(vcm,"[11]+[-11]+[2212]");

react.addEntry("mee",  0, OperatorType.MASS);
react.addEntry("elep", 1, OperatorType.P);
react.addEntry("elef", 1, OperatorType.PHI);

react.addEntry("posp", 2, OperatorType.P);
react.addEntry("posf", 2, OperatorType.PHI);

react.addEntry("pp", 3, OperatorType.P);
react.addEntry("pf", 3, OperatorType.PHI);

react.addEntry("egamma", 4, OperatorType.MASS2);
react.addEntry("w", 5, OperatorType.MASS);


HipoReader r = new HipoReader("infile.hipo");

react.setDataSource(r, "REC::Particle");
react.addModifier(PhysicsReaction.FORWARD_ONLY);

LorentzVector vep = new LorentzVector();
LorentzVector vem = new LorentzVector();
LorentzVector vp = new LorentzVector();

while(react.next()==true){
    //h.fill(react.getValue("mee"));

    LorentzVector cm  = LorentzVector.withPxPyPzM(0.,0.,10.6,0.0005).add(0.,0.,0.,0.938);
    LorentzVector cmi = LorentzVector.withPxPyPzM(0.,0.,10.6,0.0005).add(0.,0.,0.,0.938);
    cmi.invert();
    
    react.event().vector(vep,0.0005,  -11, 0);
    react.event().vector(vem,0.0005,   11, 0);
    react.event().vector(vp , 0.938272088, 2212, 0);

    LorentzVector w = LorentzVector.from(vp);

    w.add(vep).add(vem).sub(cm);
    cmi.add(vp).add(vep).add(vem);
    
    System.out.println("-----------");
    System.out.println("electron  = " + vem);
    System.out.println("positron  = " + vep);
    System.out.println("proton    = " + vp);
    System.out.println("vector w  = " + w);
    System.out.println("vector wi = " + cmi);
    System.out.println("operator  = " + react.getValue("w"));
}

