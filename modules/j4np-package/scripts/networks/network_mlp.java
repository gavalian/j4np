import j4ml.data.*;
import j4ml.deepnetts.*;

DataList dlt = DataList.fromCSV("data_sample_tr.csv", new int[]{0,1,2,3,4,5}, new int[]{6,7});
DataList dle = DataList.fromCSV("data_sample_ev.csv", new int[]{0,1,2,3,4,5}, new int[]{6,7});
dlt.show();

DeepNettsClassifier classifier = new DeepNettsClassifier();
classifier.init(new int[]{6,12,12,6,2});
classifier.train(dlt,100);

classifier.evaluate(dle);
dle.show();

dle.export("evaluated.csv");
