package phonics;


public class HammingWindow extends FormantAnalysis {
	int frames;
	private double[] x;
	private double[] s;

	// N The length of a frame
	// M The shift or interval between successive frames

	/**
	 * Class constructor for HammingWindow
	 * 
	 * @param M
	 *            The shift between successive frames
	 * @param N
	 *            The length of a frame
	 */
	public HammingWindow(double[] data) {
		s = data;
		frames = (int) Math.ceil((data.length - frameSize) / (double) windowThift) + 1;
		x = new double[frameSize];
	}

	// public List<double[]> filter() {
	// ArrayList<double[]> result = new ArrayList<double[]>();
	// for (int l = 0; l < frames; l++) {
	// initialize();
	// double[] temp = applyWindow(l);
	// int length = temp.length;
	// result.add(Arrays.copyOf(temp, length));
	// }
	// return result;
	// }

	private void initialize() {
		int length = x.length;
		for (int i = 0; i < length; i++) {
			x[i] = 0;
		}
	}

	/**
	 * Applies Hamming window to the frame numbered <code>L</code>
	 * 
	 * @param L
	 *            The frame number 0, 1, 2...
	 */
	public double[] applyWindow(int L) {
		int n;
		int sLength = s.length;
		initialize();
		for (int i = 0; i < frameSize; i++) {
			n = windowThift * L + i;
			if (n < sLength) {
				x[i] = s[n] * hammingWindow((double) i);
			} else {
				x[i] = 0 * hammingWindow((double) i);
			}

		}
		return x;
	}

	/**
	 * Performs calculations of the Hamming function for a given <code>n</code>
	 * 
	 * @param n
	 *            The value for which the Hamming function has to be calculated
	 * @return The value of the evaluated Hamming function for the given value
	 *         of n
	 */
	private double hammingWindow(double n) {
		return 0.54 - 0.46 * Math.cos(2 * Math.PI * n / (frameSize - 1));
	}

}
