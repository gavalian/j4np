import j4ml.data.*;
import j4ml.deepnetts.*;
import j4ml.ejml.EJMLModel;
import j4ml.ejml.EJMLModel.ModelType;


DataList dlt = DataList.fromCSV("data_sample_tr.csv", new int[]{0,1,2,3,4,5}, new int[]{6,7});
DataList dle = DataList.fromCSV("data_sample_ev.csv", new int[]{0,1,2,3,4,5}, new int[]{6,7});
dlt.show();

DeepNettsClassifier classifier = new DeepNettsClassifier();
classifier.init(new int[]{6,12,12,6,2});
classifier.train(dlt,100);

classifier.save("classifier.network");

EJMLModel model = new EJMLModel("classifier.network",ModelType.SOFTMAX);

System.out.println("network structure: " + model.summary());
System.out.println("\n\nRunning Inference:\n------------");

float[] output = new float[2];

for(int i = 0; i < dle.getList().size(); i++){
    float[] input = dle.getList().get(i).floatFirst();
    float[] desired = dle.getList().get(i).floatSecond();
    model.getOutput(input,output);
    System.out.println(Arrays.toString(input)
		       + " => " + Arrays.toString(desired)
		       + " => " + Arrays.toString(output));
}
