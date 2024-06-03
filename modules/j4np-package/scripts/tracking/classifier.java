import j4np.neural.classifier.NeuralClassifierModel;

NeuralClassifierModel classifier = new NeuralClassifierModel();
classifier.loadFromFile("../../etc/networks/clas12rgd.network",12);
Random r = new Random();

float[] segments = new float[6];
float[]   labels = new float[3];

int iter = 20;

for(int i = 0; i < iter; i++){
    for(int s = 0; s < segments.length; s++) segments[s] = r.nextFloat();
    classifier.getModel().getOutput(segments,labels);
    System.out.println(Arrays.toString(segments) + "  ==> " + Arrays.toString(labels));
}
