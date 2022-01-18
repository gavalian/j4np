//************************************************** 
//*--#$ String command = System.getProperty("a");
//*--#$ System.out.println(command);
//**************************************************
import j4np.physics.VectorOperator.OperatorType;

PhysicsReaction react = new PhysicsReaction("11:211:-211",10.6);

react.addVector(react.getVector(), "-[11]-[211]-[-211]");
react.addEntry("mxepipi", 0, OperatorType.MASS);

HipoReader r = new HipoReader("/Users/gavalian/Work/dataspace/denoise/out.ev.10.150na.dn.rec.ai.filtered.hipo");
react.setDataSource(r, "RECAI::Particle");
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
H1F h = react.geth("mxepipi", "", 120, 0.6, 2.6);
h.setName("mxepipi_10_dn_ai_150");
System.out.printf("%9.2f : filename\n",h.area(0.8,1.05));
h.attr().setFillColor(44);
TGCanvas c = new TGCanvas();
c.view().region().draw(h);
c.repaint();
DataSetSerializer.export(h,"histograms.twig","/data/denoisedai");