import java.awt.Font;


public class Results {

    public static int X_SIZE = 450;
    public static int Y_SIZE = 500;

    public static void writeBench(){
	// parquete write sepeed - 15.97 seconds 5.1GB file 24 columns
	// hipo write 5.83 seconds uncompressed 5.7 GB file 24 columns
	// hipo write 5.11 seconds compressed lz4 4.8 GB file 24 columns
	// root write 149.05 seconds compressed Lz4 4.4 GB
    }
    
    public static void readBench(){
	TStyle.getInstance().getPalette().setColorScheme("gold10");
	TStyle.getInstance().setDefaultPaveTextFont( new Font("Paletino",Font.PLAIN,14));
	TStyle.getInstance().setDefaultAxisLabelFont(new Font("Paletino",Font.PLAIN,18));
	TStyle.getInstance().setDefaultAxisTitleFont(new Font("Paletino",Font.PLAIN,20));
	
	BarChartBuilder b = new BarChartBuilder();
	// --- root with LZ4 12 - 14.35 sec , 8 - 10.10 sec, 4 - 5.37 sec
	// --- root with GZIP 12 24.00 sec, 8 - 16.77 sec, 4 - 8.93 sec
	DataGroup group =

	    b.addEntry("ROOT TTree, C++"  , 5.37, 10.10, 14.35)
	    .addEntry("HiPO Tuple, C++"   , 1.35, 2.61, 4.22)
	    .addEntry("HiPO Tuple, Java"  , 2.16, 3.78, 5.84)
	    .addEntry("Parquete, Python"  , 1.80, 3.56, 5.44)
	    
	    //b.addEntry("ROOT TTree read, C++",4.0650,5.2855,5.5584)
	    //               .addEntry("ROOT RNTuple read, C++",0,9.31,10.8)
	    //.addEntry("HIPO clas12 read, C++", 0,6.14,5.37)
	    //.addEntry("HIPO Lib read, C++", 2.5788,4.0313,3.7532)
	    //.addEntry("HIPO Lib read, Java",3.8582,6.4356,6.9700)
	    //.addEntry("ROOT RNTuple, C++", 3.2,1.2,2.45)
	    //.addEntry("HIPO Read, C++", 3.2,1.2,2.45)
	    //.addEntry("HIPO Read, Java", 3.7,6.2,1.6,2)
	    
	    .setTitleY("Time (sec)")
	    //.setColors(new int[]{52,72,29,49})
	    .setColors("#FE9500","#8FF402","#D8F97E","#78F9D5")
	    .setLabels(new String[]{"c=4","c=8","c=12"})
	    .build();
	
	System.out.println("size = " + group.getData().size());
	TGCanvas c = new TGCanvas(Results.X_SIZE, Results.Y_SIZE);
	//for(DataSet ds : group.getData()) c.draw(ds, "same");
	c.view().region().draw(group);//.showLegend(0.05, 0.95);
	c.view().region().showLegend(0.05, 0.95);
	c.view().export("bench_root_vs_hipo_read.pdf","pdf");
    }
    public static void readBenchFrame(){
		TStyle.getInstance().getPalette().setColorScheme("gold10");
		TStyle.getInstance().setDefaultPaveTextFont( new Font("Paletino",Font.PLAIN,14));
		TStyle.getInstance().setDefaultAxisLabelFont(new Font("Paletino",Font.PLAIN,18));
		TStyle.getInstance().setDefaultAxisTitleFont(new Font("Paletino",Font.PLAIN,20));
		
		BarChartBuilder b = new BarChartBuilder();
		// --- root with LZ4 12 - 14.35 sec , 8 - 10.10 sec, 4 - 5.37 sec
		// --- root with GZIP 12 24.00 sec, 8 - 16.77 sec, 4 - 8.93 sec
		DataGroup group =
	
			b.addEntry("Julia DataFrame"  , 0.48)			
			.addEntry("HiPO DataFrame, Java", 0.46)
			.addEntry("Parquete, Python"  , 1.80)
			
			//b.addEntry("ROOT TTree read, C++",4.0650,5.2855,5.5584)
			//               .addEntry("ROOT RNTuple read, C++",0,9.31,10.8)
			//.addEntry("HIPO clas12 read, C++", 0,6.14,5.37)
			//.addEntry("HIPO Lib read, C++", 2.5788,4.0313,3.7532)
			//.addEntry("HIPO Lib read, Java",3.8582,6.4356,6.9700)
			//.addEntry("ROOT RNTuple, C++", 3.2,1.2,2.45)
			//.addEntry("HIPO Read, C++", 3.2,1.2,2.45)
			//.addEntry("HIPO Read, Java", 3.7,6.2,1.6,2)
			
			.setTitleY("Time (sec)")
			//.setColors(new int[]{52,72,29,49})
			.setColors("#8FF402","#D8F97E","#78F9D5")
			.setLabels(new String[]{"c=4"})
			.build();
		
		System.out.println("size = " + group.getData().size());
		TGCanvas c = new TGCanvas(Results.X_SIZE, Results.Y_SIZE);
		//for(DataSet ds : group.getData()) c.draw(ds, "same");
		c.view().region().draw(group);//.showLegend(0.05, 0.95);
		c.view().region().showLegend(0.05, 0.95);
		c.view().export("data_frame_benchmark.pdf","pdf");
		}
		
    public static void main(String[] args){    
	//Results.draw7();
    }

}
