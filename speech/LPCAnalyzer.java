package phonics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ejml.data.Complex64F;

public class LPCAnalyzer extends FormantAnalysis {
	// bandwith threshold
	final double bw_thres = 400.0;

	private int p;
	private int sampleRate;
	private double alpha;
	private double[] autocor; // Auto-correlation values
	private double[] reflectionCoeffs; // PARCOR coefficients
	private double[] lpc;
	private double[] x;

	/**
	 * Class constructor for LPCAnalyzer
	 * 
	 * @param p
	 *            The order in LPC
	 * @param M
	 *            The shift between successive frames
	 * @param N
	 *            The length of a frame
	 */
	public LPCAnalyzer(int lpOrder, int sampleRate) {
		p = lpOrder;
		this.sampleRate = sampleRate;
	}

	// public static void main(String[] args) {
	// double[] data = { 6, 5, 6, 9, 1, 8, 2, 2, 4, 3 };
	// LPCAnalyzer lpc = new LPCAnalyzer(3, 3, 6, data);
	// System.out.println(lpc.frames);
	// lpc.hammingWindow();
	// lpc.process();
	// }

	/**
	 * @param data
	 *            all hamming window values
	 * @param list
	 *            The formants of each frame of the whole audio
	 */
	public List<double[]> processAll(HammingWindow window, int[][] loc) {
		List<double[]> res = new ArrayList<double[]>();
		int frames = window.frames;
		int locStart = loc[0][0];
		int locEnd = loc[loc.length - 1][1];
		for (int i = 0; i < frames; i++) {
			if (i >= locStart && i <= locEnd) {
				double[] windowVal = window.applyWindow(i);
				double[] temp = process(windowVal);
				// F1 and F2
				for (int k = 0; k < temp.length; k++) {
					temp[k] = (double) Math.round(temp[k]);
				}
				res.add(Arrays.copyOf(temp, temp.length));
			} else {
				// set formants before locStart and after locEnd to be 0
				double[] temp = { 0.0, 0.0 };
				res.add(temp);
			}

		}
		return res;
	}

	/*
	 * @param data one hamming window values
	 */
	public double[] process(double[] data) {
		initialize(data);
		return getFormants(p);
	}

	private double[] getFormants(int lpOrder) {
		double[] formants = new double[2];
		autoCorrelate(lpOrder);
		if (autocor[0] == 0) {
			System.err.println("A unique lpc solution does not exist");
			System.exit(-1);
		}
		linearPrediction(lpOrder);
		reverse(lpc);
		// LaguerreSolver ploynomialSover = new LaguerreSolver();
		// Complex[] roots = ploynomialSover.solveAllComplex(lpCoeffs,
		// -1);
		Complex64F[] roots = PolynomialRootFinder.findRoots(lpc);
		Complex64F[] positiveRoots = positiveSign(roots);
		double[] angz = angles(positiveRoots);
		double[][] freq_Bwt = sortAngles(angz, positiveRoots);
		double[][] freq_Bw = findFrqsWithinRange(freq_Bwt, f_low, f_high);
		int freq_Bw_Length = freq_Bw.length;

		if (freq_Bw_Length < 2) {
			formants = getFormants(lpOrder + 1);
		} else {
			int n = 0;
			for (int k = 0; k < freq_Bw_Length; k++) {
				if (n > 1) {
					break;
				}
				if (freq_Bw[k][0] > f_low && freq_Bw[k][1] < bw_thres) {
					formants[n++] = freq_Bw[k][0];
				}
			}
			if (n == 1) {
				formants = getFormants(lpOrder + 1);
			}
		}
		return formants;
	}

	/**
	 * LPC Analysis using Durbin's algorithm
	 */
	private void linearPrediction(int lpOrder) {
		lpc = new double[lpOrder + 1];
		reflectionCoeffs = new double[lpOrder + 1];
		double[] backwardPredictor = new double[lpOrder + 1];

		alpha = autocor[0];
		reflectionCoeffs[1] = -autocor[1] / autocor[0];
		lpc[0] = 1.0;
		lpc[1] = reflectionCoeffs[1];
		alpha *= (1 - reflectionCoeffs[1] * reflectionCoeffs[1]);

		for (int i = 2; i <= lpOrder; i++) {
			for (int j = 1; j < i; j++) {
				backwardPredictor[j] = lpc[i - j];
			}
			reflectionCoeffs[i] = 0;
			for (int j = 0; j < i; j++) {
				reflectionCoeffs[i] -= lpc[j] * autocor[i - j];
			}
			reflectionCoeffs[i] /= alpha;

			for (int j = 1; j < i; j++) {
				lpc[j] += reflectionCoeffs[i] * backwardPredictor[j];
			}
			lpc[i] = reflectionCoeffs[i];
			alpha *= (1 - reflectionCoeffs[i] * reflectionCoeffs[i]);

		}
		// System.out.println(Arrays.toString(lpc));
		return;
	}

	/**
	 * Initializing the arrays x, reflectionCoeffs, alpha, lpc
	 * 
	 * @param data
	 */
	private void initialize(double[] data) {
		x = data;
		alpha = 0;
	}

	/**
	 * Auto-correlation method
	 * 
	 * after a hamming window frame
	 */
	private double[] autoCorrelate(int lpOrder) {
		autocor = new double[lpOrder + 1];
		int i, j;
		for (i = 0; i < lpOrder + 1; i++) {
			autocor[i] = 0;
			for (j = 0; j < frameSize - i; j++) {
				autocor[i] += (x[j] * x[j + i]);
			}
		}
		return autocor;
	}

	private void reverse(double[] data) {
		int length = data.length;
		for (int left = 0, right = length - 1; left < right; left++, right--) {
			// swap the values at the left and right indices
			double temp = data[left];
			data[left] = data[right];
			data[right] = temp;
		}
	}

	private Complex64F[] positiveSign(Complex64F[] inData) {
		ArrayList<Complex64F> temp = new ArrayList<Complex64F>();
		for (Complex64F data : inData) {
			if (data.getImaginary() >= 0) {
				temp.add(data);
			}
		}
		return temp.toArray(new Complex64F[temp.size()]);
	}

	private double[] angles(Complex64F[] inData) {
		int length = inData.length;
		double[] temp = new double[length];
		for (int i = 0; i < length; i++) {
			temp[i] = Math.atan2(inData[i].getImaginary(), inData[i].getReal());
		}
		return temp;
	}

	public double[][] sortAngles(double[] angz, Complex64F[] roots) {
		// return the frqs and correspond bandwidth
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		int length = angz.length;
		for (int i = 0; i < length; i++) {
			// frqs
			map.put(i, angz[i] * sampleRate / (2 * Math.PI));
		}
		List<Map.Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
				return (o1.getValue().compareTo(o2.getValue()));
			}
		});
		int listLength = list.size();
		double[][] result = new double[listLength][2];
		for (int i = 0; i < listLength; i++) {
			Map.Entry<Integer, Double> entry = list.get(i);
			result[i][0] = entry.getValue();
			// bandwith
			int indice = entry.getKey();
			result[i][1] = (-1.0 / 2.0) * (sampleRate / (2 * Math.PI))
					* Math.log(roots[indice].getMagnitude());
		}
		return result;
	}

	public double[][] findFrqsWithinRange(double[][] inData, double f_low, double f_high) {
		ArrayList<double[]> list = new ArrayList<double[]>();
		for (double[] it : inData) {
			if (it[0] > f_low && it[0] < f_high) {
				list.add(it);
			}
		}
		return list.toArray(new double[list.size()][]);
	}

}
