//************************************************** 
//*--#$ String command = System.getProperty("a");
//*--#$ System.out.println(command);
//**************************************************
import j4np.physics.VectorOperator.OperatorType;

PhysicsReaction react = new PhysicsReaction("11:211:-211:X+:X-:Xn",10.2);

react.addVector(react.getVector(), "-[11]-[211]-[-211]");
react.addEntry("mxepipi", 0, OperatorType.MASS);

HipoReader r = new HipoReader("infile.hipo");
react.setDataSource(r, "REC::Particle");
/**
 * the modifiers are used to adjust particle status flags.
 * This can be used to mask certain particles from different
 * regions of detector. In the current example only particles
 * with 2000>=status<3000 are set to status=1 so only particles
 * from forward detector will be considered. 
 */
react.addModifier(new EventModifier(){
    @Override
    public void modify(PhysicsEvent event) {
        int counter = event.count();
        for(int i = 0; i < counter ; i++){
            int status = event.status(i);
            if(Math.abs(status)>=2000&&Math.abs(status)<3000){
                event.status(i, 1);
            } else { event.status(i, -1);}
        }
    }
});

H1F h = new H1F("h",120,0.5,2.5);
h.attr().setLegend("H(e^-,e^-#pi^+#pi^-)X");
h.attr().setTitleX("Mx(e^-#pi^+#pi^-) [GeV]");

TGCanvas c = new TGCanvas();
c.draw(h);
c.view().initTimer(1000);
c.region().showLegend(0.05,0.98);

while(react.next()==true){
    //System.out.println("missing mass = " + react.getValue("mxepipi"));
    h.fill(react.getValue("mxepipi"));
}
//H1F h = react.geth("mxepipi", "", 120, 0.5, 2.6);
//TGCanvas c = new TGCanvas();
//c.view().region().draw(h);
//c.repaint();
