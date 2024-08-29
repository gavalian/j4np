import j4ml.data.*;
import j4ml.deepnetts.*;
import deepnetts.net.layers.activation.*;
import deepnetts.net.loss.LossType;

DataList data = DataList.fromCSV("data2.csv", new int[]{0,1,2,3,4,5,6,7,8,9,10,11}, new int[]{12,13,14});
//dlt.show();
//data.show();
data.shuffle();

DataList[] data2 = DataList.split(data,0.8,0.2);

data2[0].shuffle();
data2[0].shuffle();
data2[0].show();

DeepNettsNetwork fixer = new DeepNettsNetwork();
fixer.activation(ActivationType.RELU); // or ActivationType.TANH
fixer.outputActivation(ActivationType.SOFTMAX);
//fixer.lossType(LossType.CROSS_ENTROPY);
//fixer.learningRate(0.01);
fixer.learningRate(0.001);
fixer.init(new int[]{12,28,24,6,3});
fixer.train(data2[0],2048);

fixer.evaluate(data2[1]);
data2[1].export("evaluated.csv");

List<String> network = fixer.getNetworkStream();
TextFileWriter.write("classifier12.network",network);

//DeepNettsClassifier classifier = new DeepNettsClassifier();
//classifier.init(new int[]{6,12,12,6,2});
//classifier.train(dlt,100);

//classifier.evaluate(dle);
//dle.show();
//dle.export("evaluated.csv");