package phonics;

public class Preemphasizer {

	private double preemphasisFactor;
	private double prior;

	public Preemphasizer(double preemphasisFactor) {
		this.preemphasisFactor = preemphasisFactor;
		prior = 0;
	}

	public void applyPreemphasis(double[] inData) {
		if (inData == null) {
			System.err.println("Preemphasiser input not exist");
			System.exit(-1);
		}
		// set the prior value for the next Audio
		double nextPrior = prior;
		int length = inData.length;
		if (length > 0) {
			nextPrior = inData[length - 1];
		}
		if (length > 1) {
			// do preemphasis
			double current;
			double previous = inData[0];
			inData[0] = previous - preemphasisFactor * prior;
			for (int i = 1; i < length; i++) {
				current = inData[i];
				inData[i] = current - preemphasisFactor * previous;
				previous = current;
			}
		}
		prior = nextPrior;
	}

}
