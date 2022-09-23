import j4ml.data.*;
import j4ml.deepnetts.*;
import j4ml.extratrees.networks.*;

DataList dlt = DataList.fromCSV("data_sample_tr.csv", new int[]{0,1,2,3,4,5}, new int[]{7});
DataList dle = DataList.fromCSV("data_sample_ev.csv", new int[]{0,1,2,3,4,5}, new int[]{7});
dlt.show();

ClassifierExtraTrees classifier = new ClassifierExtraTrees(6,1);

//------ these are default values, can be commented
classifier.setK(5); // set number of features in bootstrapping
classifier.setNMin(15); // minimumu number of rows in the sample
classifier.setNumTrees(50); // set number of trees

classifier.train(dlt);

classifier.evaluate(dle);
dle.show();
dle.export("evaluated_ert.csv");
