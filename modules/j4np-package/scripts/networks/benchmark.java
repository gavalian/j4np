import j4ml.data.*;
import j4ml.deepnetts.*;
import j4ml.ejml.EJMLModel;
import j4ml.ejml.EJMLModel.ModelType;


DeepNettsClassifier classifier = new DeepNettsClassifier();
classifier.init(new int[]{112*6,112,112/2});

classifier.save("benchmark.network");

EJMLModel model = new EJMLModel("benchmark.network",ModelType.SOFTMAX);

model.benchmark(150000*36);
