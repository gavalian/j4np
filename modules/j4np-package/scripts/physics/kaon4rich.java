//************************************************** 
//*--#$ String command = System.getProperty("a");
//*--#$ System.out.println(command);
//**************************************************
import j4np.physics.VectorOperator.OperatorType;

PhysicsReaction rek = new PhysicsReaction("11:321:2212:Xn:X-:X+",10.2);

//--------------------------------------------------------
// If looking for pion misudentified as kaon you can use
// [321,0,0.135] in the particle expression, which will
// take first kaon in the event (0 - is 0 skip) and
// assign pion mass (0.135) to the particle
//--------------------------------------------------------
rek.addVector(rek.getVector(), "-[11]-[321]-[2212]");
rek.addVector(rek.getVector(), "-[11]-[321]");
rek.addVector("[321]");

rek.addEntry("mm2"  , 0, OperatorType.MASS2);
rek.addEntry("mp"   , 0, OperatorType.P);
rek.addEntry("mmkp" , 1, OperatorType.MASS);
rek.addEntry("pkp"  , 2, OperatorType.P);
rek.addEntry("tkp"  , 2, OperatorType.THETA);
rek.addEntry("fkp"  , 2, OperatorType.PHI);

HipoReader r = new HipoReader("infile.h5");
rek.setDataSource(r, "REC::Particle");
rek.addModifier(PhysicsReaction.FORWARD_ONLY);

//-----------------------------------------------------------
// to load this into Jshell use
// >./bin/j4shell kaon4rich.java
// Then one could make plots with defined variables:
// jshell> rek.draw("mm2","mm2>-0.1&&mm2<0.4")
//
// To draw into a histogram with defined bins use:
// jshell> rek.draw("mmkp>>h1(120,-0.1,0.4)","mm2>0.0&&mm2<0.05");
//-----------------------------------------------------------
// 
