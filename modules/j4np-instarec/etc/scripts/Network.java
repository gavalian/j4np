import j4ml.data.*;
import j4ml.deepnetts.*;
import deepnetts.net.layers.activation.*;
import j4np.utils.*;
import j4np.utils.base.*;
import j4np.utils.io.*;

public final class Network {

    public static void fixer(String file, int run){
	DataList dlt = DataList.fromCSV(file, new int[]{0,1,2,3,4,5,6,7,8,9,10,11}, new int[]{0,1,2,3,4,5,6,7,8,9,10,11});
	Random r = new Random();	
	DataList data = new DataList();
	for(int i = 0; i < dlt.getList().size(); i++){
	    for(int k = 0; k < 6; k++){
		float[]  input = dlt.getList().get(i).featuresCopy();
		float[] output = dlt.getList().get(i).featuresCopy();
		input[k*2  ] = 0.0f; input[k*2+1] = 0.0f;
		data.add(new DataEntry(input,output));
	    }
	}
	data.shuffle();	
	DataList[] data2 = DataList.split(data,0.8,0.2);
	DeepNettsNetwork fixer = new DeepNettsNetwork();
	fixer.activation(ActivationType.TANH); // or ActivationType.TANH
	fixer.outputActivation(ActivationType.LINEAR);
	fixer.learningRate(0.01);
	fixer.init(new int[]{12,24,12,24,12});
	fixer.train(data2[0],128);
	
	fixer.evaluate(data2[1]);
	data2[1].export("evaluatedfixer12.csv");

	List<String> network = fixer.getNetworkStream();
	TextFileWriter.write("trackfixer12.network",network);
	ArchiveUtils.writeFile("clas12default.network",String.format("network/%d/default/trackfixer12.network",run), network);
    }

    
    public static void fixer6(String file, int run){

	DataList dlt = DataList.fromCSV(file, new int[]{0,1,2,3,4,5,6,7,8,9,10,11}, new int[]{0,1,2,3,4,5,6,7,8,9,10,11});
	Random r = new Random();	
	DataList data = new DataList();
	for(int i = 0; i < dlt.getList().size(); i++){
	    for(int k = 0; k < 6; k++){
		float[]  input = dlt.getList().get(i).featuresCopy();
		float[] output = dlt.getList().get(i).featuresCopy();
		
		float[]  input6 = new float[6];
		float[] output6 = new float[6];
		for(int j = 0; j < 6; j++) input6[j] = (float) (0.5*(input[j*2] + input[j*2+1]));
		for(int j = 0; j < 6; j++) output6[j] = (float) (0.5*(input[j*2] + input[j*2+1]));
		
		input6[k] = 0.0f; 			
		data.add(new DataEntry(input6,output6));
	    }
	}
	data.shuffle();	
	DataList[] data2 = DataList.split(data,0.8,0.2);
	DeepNettsNetwork fixer = new DeepNettsNetwork();
	fixer.activation(ActivationType.TANH); // or ActivationType.TANH
	fixer.outputActivation(ActivationType.LINEAR);
	fixer.learningRate(0.01);
	fixer.init(new int[]{6,12,6,12,6});
	fixer.train(data2[0],128);
	
	fixer.evaluate(data2[1]);
	data2[1].export("evaluatedfixer6.csv");
	
	List<String> network = fixer.getNetworkStream();
	TextFileWriter.write("trackfixer6.network",network);
	ArchiveUtils.writeFile("clas12default.network",String.format("network/%d/default/trackfixer6.network",run), network);
    }

    public static final void classifier6(String file, int run){
	
	DataList buff = DataList.fromCSV(file, new int[]{0,1,2,3,4,5,6,7,8,9,10,11}, new int[]{13,14,15});
	DataList data = new DataList();
	Random      r = new Random();

	
	for(int i = 0; i < buff.getList().size(); i++){
	    
	    float[] first = buff.getList().get(i).features();
	    float[] means = new float[6];
	    for(int j = 0; j < 6; j++) means[j] = 0.5f*(first[2*j] + first[2*j+1]);
	    DataEntry  entrytrue = new DataEntry(means,buff.getList().get(i).labels());
	    DataEntry entryfalse = entrytrue.copy(new float[]{1.0f,0.0f,0.0f});
	    int howMany =r.nextInt(3)+1;
	    for(int h = 0; h < howMany; h++){
		int which = r.nextInt(6);
		entryfalse.swap(r,new int[]{which},4./112.0,24.0/112.0);
	    }
	    
	    if(entryfalse.checkRange()==true){
		data.getList().add(entrytrue);
		data.getList().add(entryfalse);
	    }
	}

	DataList[] xtrain = DataList.split(data,0.8,0.2);

	xtrain[0].shuffle();
	xtrain[0].shuffle();
	xtrain[0].shuffle();

	xtrain[0].show();
	
	DeepNettsNetwork classifier = new DeepNettsNetwork();
	classifier.activation(ActivationType.RELU); // or ActivationType.TANH
	classifier.outputActivation(ActivationType.SOFTMAX);
	classifier.learningRate(0.001);
	classifier.init(new int[]{6,12,6,3});
	classifier.train(xtrain[0],1024);
	
	classifier.evaluate(xtrain[1]);
        xtrain[1].export(String.format("evaluatedclassifier6.csv"));
	List<String> network = classifier.getNetworkStream();
        TextFileWriter.write("trackclassifier6.network",network);
        ArchiveUtils.writeFile("clas12default.network",String.format("network/%d/default/trackclassifier.network",run), network);

	ConfusionMatrix m = new ConfusionMatrix(3);
	m.apply(xtrain[1]);
	System.out.println("************ CONFUSSION MATRIX ");
	System.out.println(Arrays.toString(m.getConfusionMatrixFlat()));
	System.out.println("\n\n");
    }
    
    public static final void regression(String file, int run, int sector, int charge){	
	DataList buff = DataList.fromCSV(file, new int[]{0,1,2,3,4,5,6,7,8,9,10,11}, new int[]{12,13,14,15,16,17,18,19});
	DataList data = new DataList();
	
	for(int i = 0; i < buff.getList().size(); i++){	    
	    int sec = (int) buff.getList().get(i).labels()[0];
	    int neg = (int) buff.getList().get(i).labels()[2];
	    int pos = (int) buff.getList().get(i).labels()[3];
	    //System.out.println(" sector = " + sec + "  neg = " + neg);
	    if(charge<0&&neg==1&&sector==sec){
		float[] output = new float[3];
		for(int k = 0; k < 3; k++) output[k] = buff.getList().get(i).labels()[k+4];
		data.add(new DataEntry(buff.getList().get(i).features(), output));
	    }
	    if(charge>0&&pos==1&&sector==sec){
                float[] output = new float[3];
                for(int k = 0; k < 3; k++) output[k] = buff.getList().get(i).labels()[k+4];
                data.add(new DataEntry(buff.getList().get(i).features(), output));
            }
	}
	
	data.show();
	data.shuffle();
	DataList[]  dataset = DataList.split(data,0.8,0.2);
	
	DeepNettsNetwork reg = new DeepNettsNetwork();
	reg.activation(ActivationType.TANH); // or ActivationType.TANH
	reg.outputActivation(ActivationType.LINEAR);
	reg.learningRate(0.01);
	reg.init(new int[]{12,48,6,3});
	reg.train(dataset[0],1024);
	
	String prefix = (charge<0)?"n":"p";
	
	reg.evaluate(dataset[1]);
        dataset[1].export(String.format("evaluatedregression12_%d_%s.csv",sector,prefix));

        List<String> network = reg.getNetworkStream();
        TextFileWriter.write(String.format("trackregression12_%d_%s.network",sector,prefix),network);
	ArchiveUtils.writeFile("clas12default.network",String.format("network/%d/default/%d/%s/trackregression12.network",run,sector,prefix), network);	
    }

    public static final void vertex(String file, int run, int sector, int charge){
	
	DataList buff = DataList.fromCSV(file, new int[]{0,1,2,3,4,5,6,7,8,9,10,11,16,17,18}, new int[]{12,13,14,15,16,17,18,19});
	DataList data = new DataList();
	
	for(int i = 0; i < buff.getList().size(); i++){	    
	    int sec = (int) buff.getList().get(i).labels()[0];
	    int neg = (int) buff.getList().get(i).labels()[2];
	    int pos = (int) buff.getList().get(i).labels()[3];
	    //System.out.println(" sector = " + sec + "  neg = " + neg);
	    if(charge<0&&neg==1&&sector==sec){
		float[] output = new float[1];
		output[0] = buff.getList().get(i).labels()[7];
		double z = output[0]*20-15;
		if(z>-6&&z<1){
		    double norm = (z+6)/7.0;
		    output[0] = (float) norm;
		    data.add(new DataEntry(buff.getList().get(i).features(), output));
		}
	    }
	}
	
	data.show();
	data.shuffle();
	DataList[]  dataset = DataList.split(data,0.8,0.2);
	
	DeepNettsNetwork reg = new DeepNettsNetwork();
	reg.activation(ActivationType.RELU); // or ActivationType.TANH
	reg.outputActivation(ActivationType.LINEAR);
	reg.learningRate(0.01);
	reg.init(new int[]{15,48,6,1});
	reg.train(dataset[0],1500);
	
	String prefix = (charge<0)?"n":"p";
	
	reg.evaluate(dataset[1]);
	dataset[1].export(String.format("evaluatedvertex12_%d_%s.csv",sector,prefix));
	
	List<String> network = reg.getNetworkStream();
	TextFileWriter.write("trackfixer6.network",network);
	ArchiveUtils.writeFile("clas12default.network",String.format("network/%d/default/%d/%s/trackvertex12.network",run,sector,prefix), network);	
    }
    
    public static void regression(String file, int run){
	Thread[] pooln = new Thread[12];
	int[] sectors = new int[]{1,2,3,4,5,6};
	
	pooln[ 0] = new Thread(){ public void run(){ Network.regression(file,run,1,-1);} };
	pooln[ 1] = new Thread(){ public void run(){ Network.regression(file,run,2,-1);} };
	pooln[ 2] = new Thread(){ public void run(){ Network.regression(file,run,3,-1);} };
	pooln[ 3] = new Thread(){ public void run(){ Network.regression(file,run,4,-1);} };
	pooln[ 4] = new Thread(){ public void run(){ Network.regression(file,run,5,-1);} };
	pooln[ 5] = new Thread(){ public void run(){ Network.regression(file,run,6,-1);} };
	
	pooln[ 6] = new Thread(){ public void run(){ Network.regression(file,run,1,1);} };
        pooln[ 7] = new Thread(){ public void run(){ Network.regression(file,run,2,1);} };
	pooln[ 8] = new Thread(){ public void run(){ Network.regression(file,run,3,1);} };
        pooln[ 9] = new Thread(){ public void run(){ Network.regression(file,run,4,1);} };
	pooln[10] = new Thread(){ public void run(){ Network.regression(file,run,5,1);} };
        pooln[11] = new Thread(){ public void run(){ Network.regression(file,run,6,1);} };
	//pooln[s].sector = s+1;
	
	for(int i = 0; i < 12; i++) pooln[i].start();
    }

    public static void vertex(String file, int run){
	
	Thread[] pooln = new Thread[12];
	int[]  sectors = new int[]{1,2,3,4,5,6};
	
	pooln[0] = new Thread(){ public void run(){ Network.vertex(file,run,1,-1);} };
	pooln[1] = new Thread(){ public void run(){ Network.vertex(file,run,2,-1);} };
	pooln[2] = new Thread(){ public void run(){ Network.vertex(file,run,3,-1);} };
	pooln[3] = new Thread(){ public void run(){ Network.vertex(file,run,4,-1);} };
	pooln[4] = new Thread(){ public void run(){ Network.vertex(file,run,5,-1);} };
	pooln[5] = new Thread(){ public void run(){ Network.vertex(file,run,6,-1);} };
	//pooln[s].sector = s+1;
	
		for(int i = 0; i < 6; i++) pooln[i].start();
	}
}
