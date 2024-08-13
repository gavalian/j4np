HipoReader r = new HipoReader("infile.h5");
HipoWriter w = HipoWriter.create("outfile.h5",r);
Bank[]     b = r.getBanks("RUN::config");
Event      e = new Event();

List<Integer> events = Arrays.asList(3730,3726,3715,3717,5007);
    
while(r.next(e)){
    e.read(b); int n = b[0].getInt("event",0);
    if(events.contains(n)) w.addEvent(e);
}
w.close();
