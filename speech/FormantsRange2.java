package phonics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class FormantsRange2 {
	// parameter
	final int minDeviationSize = 3;
	final int maxDeviationSize = 6;
	final double F1F2Para = 0.5; // for selectCandiByDeviation
	final int deviationBandthNum = 10;
	// final int sweepMatchThredLength = 15;
	// variable
	int gender; // 1 for male, 2 for female, 3 for kid
	// Formants Ranges
	Map<String, Rectangle2D> rangeMap_male;
	Map<String, Rectangle2D> rangeMap_female;
	Map<String, Rectangle2D> rangeMap_kid = new HashMap<String, Rectangle2D>();

	public FormantsRange2(int gender) {
		this.gender = gender;
		if (gender == 1) {
			rangeMap_male = new HashMap<String, Rectangle2D>();
			rangeMap_male.put("I", new Rectangle2D(275, 1850, 225, 500));
			rangeMap_male.put("i:", new Rectangle2D(200, 2000, 200, 800));
			rangeMap_male.put("i", new Rectangle2D(200, 2000, 200, 800));
			rangeMap_male.put("e", new Rectangle2D(500, 1500, 250, 500));
			rangeMap_male.put("ae", new Rectangle2D(550, 1550, 300, 400));
			rangeMap_male.put("a:", new Rectangle2D(600, 950, 300, 600));
			rangeMap_male.put("o", new Rectangle2D(600, 950, 300, 600));
			rangeMap_male.put("o:", new Rectangle2D(450, 700, 300, 600));
			rangeMap_male.put("u", new Rectangle2D(400, 900, 250, 600));
			rangeMap_male.put("u:", new Rectangle2D(250, 1050, 100, 600));
			rangeMap_male.put("^", new Rectangle2D(550, 900, 250, 550));
			rangeMap_male.put("eh", new Rectangle2D(400, 1000, 300, 550));
			rangeMap_male.put("3r", new Rectangle2D(250, 1100, 250, 600));
			// dipthong range
			rangeMap_male.put("ei", new Rectangle2D(300, 1800, 300, 400));
			rangeMap_male.put("ai", new Rectangle2D(600, 950, 300, 600));
			rangeMap_male.put("au", new Rectangle2D(600, 950, 300, 600));
			rangeMap_male.put("oi", new Rectangle2D(450, 700, 300, 600));
			rangeMap_male.put("ou", new Rectangle2D(400, 800, 250, 600));
		} else if (gender == 2) {
			rangeMap_female.put("I", new Rectangle2D(500, 1800, 200, 400));
			rangeMap_female.put("i:", new Rectangle2D(200, 1950, 300, 400));
			rangeMap_female.put("i", new Rectangle2D(200, 1950, 300, 400));
			rangeMap_female.put("e", new Rectangle2D(500, 1750, 200, 400));
			rangeMap_female.put("ae", new Rectangle2D(650, 1700, 400, 600));
			rangeMap_female.put("a:", new Rectangle2D(650, 1050, 200, 400));
			rangeMap_female.put("o", new Rectangle2D(650, 1050, 200, 400));
			rangeMap_female.put("o:", new Rectangle2D(500, 1100, 200, 400));
			rangeMap_female.put("u", new Rectangle2D(350, 1050, 200, 400));
			rangeMap_female.put("u:", new Rectangle2D(300, 900, 200, 400));
			rangeMap_female.put("^", new Rectangle2D(600, 1250, 300, 400));
			rangeMap_female.put("eh", new Rectangle2D(300, 1200, 300, 400));
			rangeMap_female.put("3r", new Rectangle2D(150, 1000, 400, 800));
			// dipthong range
			rangeMap_female.put("ei", new Rectangle2D(450, 1550, 300, 400));
			rangeMap_female.put("ai", new Rectangle2D(650, 1050, 200, 400));
			rangeMap_female.put("au", new Rectangle2D(650, 1050, 200, 400));
			rangeMap_female.put("oi", new Rectangle2D(500, 1100, 200, 400));
			rangeMap_female.put("ou", new Rectangle2D(400, 1450, 200, 400));
		} else if (gender == 3) {
			rangeMap_kid = new HashMap<String, Rectangle2D>();
			rangeMap_kid.put("I", new Rectangle2D(350, 1750, 400, 600));
			rangeMap_kid.put("i:", new Rectangle2D(200, 2300, 500, 600));
			rangeMap_kid.put("i", new Rectangle2D(200, 2300, 500, 600));
			rangeMap_kid.put("e", new Rectangle2D(650, 1950, 400, 500));
			rangeMap_kid.put("ae", new Rectangle2D(800, 1650, 600, 600));
			rangeMap_kid.put("a:", new Rectangle2D(750, 1100, 400, 500));
			rangeMap_kid.put("o", new Rectangle2D(750, 1100, 400, 500));
			rangeMap_kid.put("o:", new Rectangle2D(600, 1100, 300, 400));
			rangeMap_kid.put("u", new Rectangle2D(300, 900, 300, 500));
			rangeMap_kid.put("u:", new Rectangle2D(250, 1050, 300, 500));
			rangeMap_kid.put("^", new Rectangle2D(600, 1650, 400, 400));
			rangeMap_kid.put("eh", new Rectangle2D(500, 1850, 300, 500));
			rangeMap_kid.put("3r", new Rectangle2D(150, 1000, 400, 800));
			// dipthong range
			rangeMap_kid.put("ei", new Rectangle2D(300, 2100, 400, 600));
			rangeMap_kid.put("ai", new Rectangle2D(750, 1100, 400, 500));
			rangeMap_kid.put("au", new Rectangle2D(750, 1100, 400, 500));
			rangeMap_kid.put("oi", new Rectangle2D(600, 1100, 300, 400));
			rangeMap_kid.put("ou", new Rectangle2D(450, 1100, 300, 400));
		} else {
			System.err.println("GENDER ERROR");
		}

	}

	// default rangeMap_male
	public void check(List<List<double[]>> formantsList, List<String> phonemes,
			AnalysisResult result) {
		int phonemeSize = phonemes.size();

		if (formantsList.size() != phonemeSize) {
			System.err.println("formants and phonemes size unmatched");
			result.setRepeat(true);
			return;
		}

		Map<String, Rectangle2D> checkRangeMap;
		if (gender == 1) {
			checkRangeMap = rangeMap_male;
		} else if (gender == 2) {
			checkRangeMap = rangeMap_female;
		} else {
			checkRangeMap = rangeMap_kid;
		}

		StringBuilder sb = new StringBuilder();
		for (int k = 0; k < phonemeSize; k++) {
			String phoneme = phonemes.get(k);
			if (checkRangeMap.containsKey(phoneme)) {
				Rectangle2D rangeRectangle = checkRangeMap.get(phoneme);
				List<double[]> formants = formantsList.get(k);

				sb.append(phoneme);
				sb.append(" ");
				int formantSize = formants.size();
				if (formantSize < minDeviationSize) {
					// too few formants in bandwidth
					result.setRepeat(true);
				} else {

					double[] candidateFormants = new double[2];
					boolean gassianCheckRes = gaussianCheck(formants, rangeRectangle, k,
							candidateFormants);
					System.out.println("candiate:" + candidateFormants[0] + " "
							+ candidateFormants[1]);
					result.degreeArray[k] = getDegree(candidateFormants, rangeRectangle,
							gassianCheckRes);
					result.instructionArray[k] = getInstructionCata(candidateFormants,
							rangeRectangle, gassianCheckRes);
				}

				System.out.println(sb.toString());
				sb.setLength(0);
			} else {
				System.err.println("no phoneme matched");
			}
		}
		return;
	}

	public boolean gaussianCheck(List<double[]> formants, Rectangle2D range, int k,
			double[] cadidateFormants) {
		// k refers to the k'th syllable
		int formantsLength = formants.size();
		if (formantsLength < 10) {
			return false;
		}
		// parameter
		int windowLength = (int) Math.ceil(((double) formantsLength / 7));
		windowLength = Math.max(windowLength, 5);
		int overlap = (int) Math.ceil(((double) windowLength / 4));
		double gaussian_F1_Range_std = range.width / 2;
		double gaussian_F2_Range_std = range.height / 2;
		// linear combination para of weight-h(f_ijk) and standard
		// deviation-stddev_ijk
		double alpha = -0.01;
		// linear combination para of F1 confidence and F2 confidence
		double F1_threshold = 0.05;
		double F2_threshold = 0.0;

		// variable
		boolean result; // true-pass; false-fail

		// generate F1 and F2
		double[] F1val = new double[formantsLength];
		double[] F2val = new double[formantsLength];
		for (int i = 0; i < formantsLength; i++) {
			F1val[i] = formants.get(i)[0];
			F2val[i] = formants.get(i)[1];
		}
		// gaussian distribution of the range
		double F1_range_mid = range.x + range.width / 2;
		double F2_range_mid = range.y + range.height / 2;
		Gaussian gaussian_F1_range = new Gaussian(1, F1_range_mid, gaussian_F1_Range_std);
		Gaussian gaussian_F2_range = new Gaussian(1, F2_range_mid, gaussian_F2_Range_std);

		// overlapped window
		int windowNum = (formantsLength - windowLength) / overlap + 1;
		// analysis value of F1 and F2
		double[] avg_F1_window = new double[windowNum];
		double[] std_F1_window = new double[windowNum];
		double[] weight_F1_window = new double[windowNum];
		double[] score_F1_window = new double[windowNum];

		double[] avg_F2_window = new double[windowNum];
		double[] std_F2_window = new double[windowNum];
		double[] weight_F2_window = new double[windowNum];
		double[] score_F2_window = new double[windowNum];

		Mean mean = new Mean();
		StandardDeviation std = new StandardDeviation();
		// temporary variable
		int start;
		int end;
		double[] F1_window;
		double[] F2_window;
		double F1_confidence = -100;
		double F2_confidence = -100;
		double combinedScore;
		double combinedConfidence = -100;
		int F1_loc = 0;
		int F2_loc = 0;
		for (int i = 0; i < windowNum; i++) {
			start = i * overlap;
			end = start + windowLength;
			F1_window = Arrays.copyOfRange(F1val, start, end);
			F2_window = Arrays.copyOfRange(F2val, start, end);
			avg_F1_window[i] = mean.evaluate(F1_window);
			avg_F2_window[i] = mean.evaluate(F2_window);
			mean.clear();
			std_F1_window[i] = std.evaluate(F1_window);
			std_F2_window[i] = std.evaluate(F2_window);
			std.clear();
			weight_F1_window[i] = gaussian_F1_range.value(avg_F1_window[i]);
			weight_F2_window[i] = gaussian_F2_range.value(avg_F2_window[i]);
			score_F1_window[i] = weight_F1_window[i] + alpha * std_F1_window[i];
			score_F2_window[i] = weight_F2_window[i] + alpha * std_F2_window[i];
			combinedScore = score_F1_window[i] + score_F2_window[i];

			if (combinedScore > combinedConfidence) {
				combinedConfidence = combinedScore;
				F1_loc = i;
				F2_loc = i;
				F1_confidence = score_F1_window[i];
				F2_confidence = score_F2_window[i];
			}
			// if (score_F2_window[i] > F2_confidence) {
			// F2_loc = i;
			// F2_confidence = score_F2_window[i];
			// }
		}
		cadidateFormants[0] = avg_F1_window[F1_loc];
		cadidateFormants[1] = avg_F2_window[F2_loc];

		if (F1_confidence >= F1_threshold && F2_confidence >= F2_threshold) {
			result = true;
			System.out.println(" gaussian PASS ");
		} else {
			result = false;
			System.out.println(" gaussian Not PASS ");
		}
		return result;
	}

	public int getInstructionCata(double[] formants, Rectangle2D rect, boolean gaussianCheck) {
		int instructionCata = 0;
		if (gaussianCheck == true) {
			return instructionCata;
		}

		double[] range = new double[4];
		// F1 lower bound
		range[0] = rect.x;
		// F1 higher bound
		range[1] = rect.x + rect.width;
		// F2 lower bound
		range[2] = rect.y;
		range[3] = rect.y + rect.height;

		// for F1 distance to range
		double dist_F1;
		if (formants[0] < range[0]) {
			dist_F1 = formants[0] - range[0]; // minus
		} else if (formants[0] > range[1]) {
			dist_F1 = formants[0] - range[1]; // positive
		} else {
			dist_F1 = 0; // in range
		}

		// for F2 distance to range
		double dist_F2;
		if (formants[1] < range[2]) {
			dist_F2 = formants[1] - range[2]; // minus
		} else if (formants[1] > range[3]) {
			dist_F2 = formants[1] - range[3]; // positive
		} else {
			dist_F2 = 0; // in range
		}

		// find the category
		if (dist_F1 == 0) {
			if (dist_F2 == 0) {
				// F1 F2 in range
				instructionCata = 1;
			} else if (dist_F2 > 0) {
				instructionCata = 2;
			} else if (dist_F2 < 0) {
				instructionCata = 3;
			}
		} else if (dist_F1 > 0) {
			if (dist_F2 == 0) {
				instructionCata = 4;
			} else if (dist_F2 > 0) {
				instructionCata = 5;
			} else if (dist_F2 < 0) {
				instructionCata = 6;
			}
		} else if (dist_F1 < 0) {
			if (dist_F2 == 0) {
				instructionCata = 7;
			} else if (dist_F2 > 0) {
				instructionCata = 8;
			} else if (dist_F2 < 0) {
				instructionCata = 9;
			}
		}
		return instructionCata;
	}

	public int getDegree(double[] formants, Rectangle2D rect, boolean gaussianCheck) {
		int degree = 5;
		if (gaussianCheck == true) {
			return degree;
		}
		// not pass return degree from 4
		Rectangle2D rectangle = new Rectangle2D(rect.x, rect.y, rect.width, rect.height);
		double widthStep = rectangle.width / ((degree - 1) * 2);
		double heightStep = rectangle.height / ((degree - 1) * 2);

		do {
			if (degree == 1) {
				break;
			}

			double rect_newX = rectangle.x - widthStep;
			double rect_newY = rectangle.y - heightStep;
			double rect_newWidth = rectangle.width + 2 * widthStep;
			double rect_newHeight = rectangle.height + 2 * heightStep;
			rectangle = new Rectangle2D(rect_newX, rect_newY, rect_newWidth, rect_newHeight);
			degree--;
		} while (!rectangle.contains(formants[0], formants[1]));

		return degree;
	}
}

// public boolean sweepMatch2(List<double[]> formants, Rectangle2D range) {
// int formantsLength = formants.size();
// // the max size for formants frames matching in the range is 4
// int bandWidth = (int) Math.ceil(((double) formants.size() / 7));
// int windowSize = Math.max(bandWidth, 4);
// int overlap = 1;
//
// // generate F1 and F2
// double[] F1val = new double[formantsLength];
// double[] F2val = new double[formantsLength];
// for (int i = 0; i < formantsLength; i++) {
// F1val[i] = formants.get(i)[0];
// F2val[i] = formants.get(i)[1];
// }
//
// int windowNum = (formantsLength - windowSize) / overlap + 1;
// int start;
// int end;
// double[] F1_window;
// double[] F2_window;
// double[] std_F1_window = new double[windowNum];
// double[] std_F2_window = new double[windowNum];
// StandardDeviation std = new StandardDeviation();
// for (int i = 0; i < windowNum; i++) {
// start = i * overlap;
// end = start + windowSize;
// F1_window = Arrays.copyOfRange(F1val, start, end);
// F2_window = Arrays.copyOfRange(F2val, start, end);
// for(int k = 0; k < windowSize; k++){
// if(!range.contains(F1_window[k], F2_window[k])){
// break;
// }
// }
// std_F1_window[i] = std.evaluate(F1_window);
// std_F1_window[i] = std.evaluate(F2_window);
// std.clear();
// }
//
// for (int i = 0; i < formants.size(); i++) {
// if (match >= windowSize) {
// return true;
// }
// double[] currFormant = formants.get(i);
// if (range.contains(currFormant[0], currFormant[1])) {
// match++;
// } else {
// match = 0;
// }
// }
// return false;
// }

// //default rangeMap_male
// public void check(List<List<double[]>> formantsList, List<String> phonemes,
// AnalysisResult result) {
// int phonemeSize = phonemes.size();
//
// if (formantsList.size() != phonemeSize) {
// System.err.println("formants and phonemes size unmatched");
// result.setRepeat(true);
// return;
// }
//
// Map<String, Rectangle2D> checkRangeMap;
// if (gender == 1) {
// checkRangeMap = rangeMap_male;
// } else if (gender == 2) {
// checkRangeMap = rangeMap_female;
// } else {
// checkRangeMap = rangeMap_kid;
// }
//
// StringBuilder sb = new StringBuilder();
// for (int k = 0; k < phonemeSize; k++) {
// String phoneme = phonemes.get(k);
// if (checkRangeMap.containsKey(phoneme)) {
// Rectangle2D rangeRectangle = checkRangeMap.get(phoneme);
// List<double[]> formants = formantsList.get(k);
//
// sb.append(phoneme);
// sb.append(" ");
// int formantSize = formants.size();
// if (formantSize < minDeviationSize) {
// // too few formants in bandwidth
// result.setRepeat(true);
// } else {
//
// if (sweepMatch(formants, rangeRectangle)) {
// sb.append(" SWEEP PASS ");
// result.degreeArray[k] = 5;
// result.instructionArray[k] = 1;
// sb.append(" instruct " + result.instructionArray[k]);
// } else {
// sb.append(" SWEEP NOT PASS ");
// double[] candidateFormants = searchByRLOWESS(formants);
// result.degreeArray[k] = getDegree(candidateFormants, rangeRectangle);
// sb.append(result.degreeArray[k]);
// result.instructionArray[k] = getInstructionCata(candidateFormants,
// rangeRectangle);
// sb.append(" instruct " + result.instructionArray[k]);
// }
// }
//
// System.out.println(sb.toString());
// sb.setLength(0);
// } else {
// System.err.println("no phoneme matched");
// }
// }
// return;
// }

// /////////////////////////////////////////////////////////////////////////////////////////////////////////

/*
 * public double[] searchByRLOWESS(List<double[]> formantsList) { int size =
 * formantsList.size(); // generate xval from 1 : size double[] xval = new
 * double[size]; for (int i = 0; i < size; i++) { xval[i] = i + 1; } // generate
 * F1val F2val double[] F1val = new double[size]; double[] F2val = new
 * double[size]; for (int i = 0; i < size; i++) { F1val[i] =
 * formantsList.get(i)[0]; F2val[i] = formantsList.get(i)[1]; } double
 * bandwidth; if (size >= 7) { bandwidth = 0.3; } else { bandwidth = 2.1 / size;
 * }
 * 
 * LoessInterpolator loessInterpolator = new LoessInterpolator(bandwidth, 4);
 * double[] smF1val = loessInterpolator.smooth(xval, F1val); double[] smF2val =
 * loessInterpolator.smooth(xval, F2val);
 * 
 * // find peak and valley of smF1val List<Double> smF1List =
 * toDoubleList(smF1val); List<Map<Integer, Double>> peakVallyLists =
 * PeakValleyDetector .peak_detection(smF1List, 0.0); SortedMap<Integer, Double>
 * peaksMap = new TreeMap<Integer, Double>(peakVallyLists.get(0));
 * SortedMap<Integer, Double> valleysMap = new TreeMap<Integer,
 * Double>(peakVallyLists.get(1)); if (peaksMap.size() == 0 && valleysMap.size()
 * == 0) { return lowestDeviation(F1val, F2val); } else { List<Integer>
 * candidateIndexes = new ArrayList<Integer>();
 * candidateIndexes.addAll(peaksMap.keySet());
 * candidateIndexes.addAll(valleysMap.keySet()); return
 * selectCandiByDeviation(F1val, F2val, candidateIndexes); }
 * 
 * }
 * 
 * public double[] lowestDeviation(double[] F1, double[] F2) { double[] result;
 * int size = F1.length; // select window size int tempSize = size /
 * deviationBandthNum; int windowSize; if (tempSize < minDeviationSize) {
 * windowSize = minDeviationSize; } else if (tempSize > maxDeviationSize) {
 * windowSize = maxDeviationSize; } else { windowSize = tempSize; }
 * 
 * // middle index for std SortedMap<Double, Integer> sortedMap = new
 * TreeMap<Double, Integer>(); StandardDeviation standardDeviation = new
 * StandardDeviation(); for (int i = 0; i < size - windowSize + 1; i++) { double
 * F1std = standardDeviation.evaluate(F1, i, windowSize); // windowSize /2 = 2
 * ,start from 2 to size - 3 sortedMap.put(F1std, i + windowSize / 2); } // get
 * the lowest Deviation index int index = sortedMap.get(sortedMap.firstKey());
 * result = new double[] { F1[index], F2[index] };
 * System.out.println("candidate: " + Arrays.toString(result)); return result; }
 * 
 * public double[] selectCandiByDeviation(double[] F1, double[] F2,
 * List<Integer> candidateIndexes) { double[] result; int size = F1.length; //
 * select window size int tempSize = size / deviationBandthNum; int windowSize;
 * if (tempSize < minDeviationSize) { windowSize = minDeviationSize; } else if
 * (tempSize > maxDeviationSize) { windowSize = maxDeviationSize; } else {
 * windowSize = tempSize; }
 * 
 * SortedMap<Double, Integer> sortedMap = new TreeMap<Double, Integer>();
 * StandardDeviation standardDeviation = new StandardDeviation(); for (Integer
 * it : candidateIndexes) { if (it >= windowSize / 2 && it < size - windowSize /
 * 2) { double F1std = standardDeviation.evaluate(F1, it - windowSize / 2,
 * windowSize); double F2std = standardDeviation.evaluate(F2, it - windowSize /
 * 2, windowSize); double measure = F1std + F1F2Para * F2std;
 * sortedMap.put(measure, it); } } int index =
 * sortedMap.get(sortedMap.firstKey()); result = new double[] { F1[index],
 * F2[index] }; System.out.println("candidate: " + Arrays.toString(result));
 * return result; }
 * 
 * public List<Double> toDoubleList(double[] array) { ArrayList<Double> result =
 * new ArrayList<Double>(); for (double it : array) { result.add(it); } return
 * result; }
 */

/*
 * public boolean sweepMatch(List<double[]> formants, Rectangle2D range) { //
 * the max size for formants frames matching in the range is 4 int bandWidth =
 * (int) Math.ceil(((double) formants.size() / 7)); int windowSize =
 * Math.max(bandWidth, 4); // int windowSize = bandWidth;
 * 
 * int match = 0; for (int i = 0; i < formants.size(); i++) { if (match >=
 * windowSize) { return true; } double[] currFormant = formants.get(i); if
 * (range.contains(currFormant[0], currFormant[1])) { match++; } else { match =
 * 0; } } return false; }
 */
