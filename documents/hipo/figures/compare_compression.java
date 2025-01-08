HipoReader r = new HipoReader("infile.h5");
Bank[] b = r.getBanks("REC::Traj");
//Bank[] b = r.getBanks("REC::CaloExtras");

Event eb = new Event();
Event el = new Event();

HipoWriter wb = new HipoWriter();
HipoWriter wl = new HipoWriter();
wb.setCompressionType(0);
wl.setCompressionType(0);

wb.open("wb.h5"); wl.open("wl.h5");

while(r.nextEvent(b)){

    Leaf leaf = b[0].toLeaf(12,3);
    //leaf.show();
    //leaf.print();
    eb.reset(); eb.write(b[0]);
    el.reset(); el.write(leaf);
    wb.addEvent(eb); wl.addEvent(el);
}

wb.close(); wl.close();
