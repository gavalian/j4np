import j4ml.data.*;
import j4ml.deepnetts.*;
import deepnetts.net.layers.activation.*;

DataList dlt = DataList.fromCSV("data_sample_tr.csv", new int[]{0,1,2,3,4,5}, new int[]{6,7});
DataList dle = DataList.fromCSV("data_sample_ev.csv", new int[]{0,1,2,3,4,5}, new int[]{6,7});
dlt.show();

DeepNettsNetwork regression = new DeepNettsNetwork();
regression.activation(ActivationType.RELU); // or ActivationType.TANH
regression.outputActivation(ActivationType.LINEAR);
regression.init(new int[]{6,12,12,6,2});
regression.train(dlt,100);

regression.evaluate(dle);
dle.show();

dle.export("evaluated.csv");
