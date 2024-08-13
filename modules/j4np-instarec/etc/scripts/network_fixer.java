import j4ml.data.*;
import j4ml.deepnetts.*;
import deepnetts.net.layers.activation.*;

DataList dlt = DataList.fromCSV("output.csv", new int[]{0,1,2,3,4,5,6,7,8,9,10,11}, new int[]{0,1,2,3,4,5,6,7,8,9,10,11});
//dlt.show();
Random r = new Random();

DataList data = new DataList();

for(int i = 0; i < dlt.getList().size(); i++){
    //int layer = r.nextInt(6);
    for(int k = 0; k < 6; k++){
	float[]  input = dlt.getList().get(i).featuresCopy();
	float[] output = dlt.getList().get(i).featuresCopy();
	input[k*2  ] = 0.0f;
	input[k*2+1] = 0.0f;
	//for(int j = 0; j < input.length; j++) {input[j] = (float) (input[j]/112.0); output[j] = (float) (output[j]/112.);}
	//System.out.println(" L : " + layer);
	data.add(new DataEntry(input,output));
    }
}

//data.show();
data.shuffle();


DataList[] data2 = DataList.split(data,0.8,0.2);

data2[0].shuffle();
data2[0].shuffle();
data2[0].show();

DeepNettsNetwork fixer = new DeepNettsNetwork();
fixer.activation(ActivationType.RELU); // or ActivationType.TANH
fixer.outputActivation(ActivationType.LINEAR);
//fixer.learningRate(0.01);
fixer.learningRate(0.01);
fixer.init(new int[]{12,24,12,6,12,24,12});
fixer.train(data2[0],1024);

fixer.evaluate(data2[1]);
data2[1].export("evaluated.csv");

List<String> network = fixer.getNetworkStream();
TextFileWriter.write("trackfixer12.network",network);

//DeepNettsClassifier classifier = new DeepNettsClassifier();
//classifier.init(new int[]{6,12,12,6,2});
//classifier.train(dlt,100);

//classifier.evaluate(dle);
//dle.show();
//dle.export("evaluated.csv");
