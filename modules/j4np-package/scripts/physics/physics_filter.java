HipoDataStream stream = new HipoDataStream("infile.hipo","outfile.hipo",64);
//H1F h = new H1F("h",120,0.0,10.0);
HipoDataWorker worker = new HipoDataWorker(){
    Schema[] schemas = null;
    @Override
    public boolean init(HipoReader src) {
	schemas = src.getSchemas("REC::Particle"); return true;
    }
	
    @Override
    public void execute(Event e) {
	Bank[] b = e.read(schemas);
	if(b[0].getRows()==0) { e.reset(); return;}
	if(b[0].getInt("pid",0)!=11) { e.reset(); return;}	
    }
};
stream.consumer(worker).threads(4);
stream.run();
stream.show();

