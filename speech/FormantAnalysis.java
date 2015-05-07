package phonics;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.math3.analysis.function.Ceil;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class FormantAnalysis {
	/* variable */
	boolean shouldRepeat = false;
	int maxPeakindex;

	/* Parameters */
	final double f_low = 90, f_high = 3000; // Hz
	final int frameSize = 256;
	final int buttonEffectLastFrame = 25;
	final int lpcOrder = 10, downSampleNum = 4;
	// for smoothing the intensity spectrum with moving average filter of
	// period, results in peakNum peaks
	final int period = 3;
	final double valley_thres = 0.2;
	// hamming window thift (1/4 of frameSize)
	final int windowThift = 64;
	// Preemphasis for audio of 11025 Hz
	final double preemphasisFactor = 0.972;
	// removeNoise parameter
	final int removeNoiseStep = 2, medianFilterSize = 9; // size shoud be odd
	// number of bands of a hill
	final static int NumOfband = 7;
	// validate voice and noise intensity rate
	final double rateOfNtoV = 0.2;
	// wrong peak intensity value check
	final double rateOfWrongPeak = 0.05;

	public static void main(String[] args) {
		TestData testData = new TestData();
		for (int i = 0; i < testData.pathList.size(); i++) {
			String path = testData.pathList.get(i);
			System.out.println();
			String[] temp = path.split("\\/");
			System.out.println(temp[1]);
			String[] input = testData.unicodeList.get(i);
			FormantAnalysis.init(path, input, 1);
		}

	}

	public static AnalysisResult init(String path, String[] input, int gender) {
		FormantAnalysis test = new FormantAnalysis();

		CollinsDictionaryIPA2 dictionary = new CollinsDictionaryIPA2(NumOfband);
		int[] peaksIn = dictionary.getPeaksIn(input);
		List<String> phonemes = dictionary.getPhonemes();
		AnalysisResult result = new AnalysisResult(phonemes.size());
		List<List<double[]>> testFormants = test.getFormantFromAudio(path, peaksIn);

		if (test.shouldRepeat == true) {
			result.setRepeat(test.shouldRepeat);
			return result;
		} else {
			FormantsRange2 formantsRange = new FormantsRange2(gender);
			formantsRange.check(testFormants, phonemes, result);
			return result;
		}
	}

	private List<List<double[]>> getFormantFromAudio(String fileName, int[] peaksIn) {
		int peakNum = peaksIn[peaksIn.length - 1];
		List<List<double[]>> formantList = new ArrayList<List<double[]>>();

		// Import raw audio at sample rate of 11025 and size double
		double[] downSampledAudio;
		try {
			FileInputStream fis = new FileInputStream(fileName);
			FileChannel fileChannel = fis.getChannel();
			int numBytes = (int) fileChannel.size();
			int numDoubles = numBytes / 8;
			int sampleRate = 11025;
			MappedByteBuffer map = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0,
					fileChannel.size());
			map.order(ByteOrder.LITTLE_ENDIAN);
			DoubleBuffer doubleBuffer = map.asDoubleBuffer();
			downSampledAudio = new double[numDoubles];
			doubleBuffer.get(downSampledAudio);

			// /////////////////// INTENSITY OF AUDIO
			// ///////////////////////////////////////////////////////////////////

			List<Double> windowTotalPower = new ArrayList<Double>();

			// Hamming window
			HammingWindow hammingWindow = new HammingWindow(downSampledAudio);
			FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
			int FrameNum = hammingWindow.frames;
			for (int i = 0; i < FrameNum; i++) {
				double[] windowVal = hammingWindow.applyWindow(i);
				Complex[] fftvalues = fft.transform(windowVal, TransformType.FORWARD);
				double truncatedPower = removeHighFreqEnergy(fftvalues, f_high, frameSize,
						sampleRate);
				windowTotalPower.add(truncatedPower);
			}

			int microStart = removeMicrophoneStartEffect(windowTotalPower);
			windowTotalPower = windowTotalPower.subList(microStart, windowTotalPower.size());
			System.out.println("window size: " + windowTotalPower.size());
			// use floor method
			// smooth the intensity spectrum
			int[][] sloc = smoothTill(windowTotalPower, peakNum);
			for (int i = 0; i < sloc.length; i++) {
				System.out.println("sloc: " + sloc[i][0] + " " + sloc[i][1]);
			}
			if (shouldRepeat == true) {
				return null;
			} else {
				int[] position = { sloc[0][0], sloc[sloc.length - 1][1] };
				shouldRepeat = repeatCheckOnNoise(windowTotalPower, position);
				if (shouldRepeat == true) {
					return null;
				}
			}

			int[][] loc = twoVowelTogetherSituation(peaksIn, sloc, windowTotalPower);
			if (shouldRepeat == true) {
				return null;
			}

			// update loc if there is removeMicrophoneStartEffect
			updateLoc(loc, microStart);
			// ///////////////////////// LPC Analysis //
			Preemphasizer preemphasizer = new Preemphasizer(preemphasisFactor);
			// downSampledAudio has been preemphasized(changed here);
			preemphasizer.applyPreemphasis(downSampledAudio);
			hammingWindow = new HammingWindow(downSampledAudio);

			LPCAnalyzer lpcAnalyzer = new LPCAnalyzer(lpcOrder, sampleRate);
			List<double[]> allFormants = lpcAnalyzer.processAll(hammingWindow, loc);
			// printList(allFormants);

			int peaksInLength = peaksIn.length;
			for (int i = 0; i < peaksInLength; i++) {
				System.out.println(loc[i][0] + " " + loc[i][1]);
				formantList.add(new ArrayList<double[]>(allFormants.subList(loc[i][0],
						loc[i][1] + 1)));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return formantList;
	}

	private void updateLoc(int[][] loc, int start) {
		int locLength = loc.length;
		for (int i = 0; i < locLength; i++) {
			loc[i][0] = loc[i][0] + start;
			loc[i][1] = loc[i][1] + start;
		}

	}

	private int removeMicrophoneStartEffect(List<Double> windowTotalPower) {
		// return correct record start position
		// find the first valley
		double preFramePower;
		double currentFramePower;
		double postFramePower;
		for (int i = 1;; i++) {
			preFramePower = windowTotalPower.get(i - 1);
			currentFramePower = windowTotalPower.get(i);
			postFramePower = windowTotalPower.get(i + 1);
			if (currentFramePower < preFramePower && currentFramePower < postFramePower) {
				return i;
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private int[][] getHillsection(
	// return the start and end position of each hill
			List<SortedMap<Integer, Double>> peakValleyList, List<Double> power) {

		int intensityLength = power.size();
		SortedMap<Integer, Double> peaksMap = peakValleyList.get(0);
		SortedMap<Integer, Double> valleysMap = peakValleyList.get(1);
		//
		System.out.println(Arrays.toString(peaksMap.entrySet().toArray()));
		System.out.println(Arrays.toString(valleysMap.entrySet().toArray()));
		int preValleyIndex, postValleyIndex, peakIndex;
		double preValleyValue, postValleyValue, peakValue, threshold;

		int[][] result = new int[peaksMap.keySet().size()][2];

		// only one peak
		if (peaksMap.keySet().size() == 1) {
			peakIndex = peaksMap.firstKey();
			peakValue = peaksMap.get(peakIndex);
			threshold = peakValue * valley_thres;
			for (int i = 0; i < peakIndex; i++) {
				if (power.get(i) >= threshold) {
					result[0][0] = i;
					break;
				}
			}
			for (int i = peakIndex; i < intensityLength; i++) {
				result[0][1] = i;
				if (power.get(i) <= threshold) {
					break;
				}
			}
		}
		// more than one peak
		else {
			// index
			List<Integer> peaksList = new ArrayList<Integer>(peaksMap.keySet());
			List<Integer> valleysList = new ArrayList<Integer>(valleysMap.keySet());
			int peaksSize = peaksList.size();

			for (int i = 0; i < peaksSize; i++) {
				peakIndex = peaksList.get(i);
				peakValue = peaksMap.get(peakIndex);
				threshold = peakValue * valley_thres;
				preValleyIndex = 0;
				postValleyIndex = 0;

				// first hill
				if (i == 0) {
					for (int j = 0; j < peakIndex; j++) {
						if (power.get(j) >= threshold) {
							result[i][0] = j;
							break;
						}
					}
					// post valley
					for (Integer integer : valleysList) {
						if (integer > peakIndex) {
							postValleyIndex = integer;
							break;
						}
					}
					postValleyValue = valleysMap.get(postValleyIndex);
					if (postValleyValue >= threshold) {
						result[i][1] = postValleyIndex;
					} else {
						for (int j = postValleyIndex; j > peakIndex; j--) {
							if (power.get(j) > threshold) {
								result[i][1] = j;
								break;
							}
						}
					}
				}
				// last hill
				else if (i == peaksSize - 1) {
					// pre valley
					for (int k = valleysList.size() - 1; k >= 0; k--) {
						if (valleysList.get(k) < peakIndex) {
							preValleyIndex = valleysList.get(k);
							break;
						}
					}
					preValleyValue = valleysMap.get(preValleyIndex);
					if (preValleyValue >= threshold) {
						result[i][0] = preValleyIndex;
					} else {
						for (int j = preValleyIndex; j < peakIndex; j++) {
							if (power.get(j) > threshold) {
								result[i][0] = j;
								break;
							}
						}
					}
					for (int j = peakIndex; j < intensityLength; j++) {
						result[i][1] = j;
						if (power.get(j) <= threshold) {
							break;
						}
					}
				}
				// middle hill
				else {
					// pre valley
					for (int k = valleysList.size() - 1; k >= 0; k--) {
						if (valleysList.get(k) < peakIndex) {
							preValleyIndex = valleysList.get(k);
							break;
						}
					}
					preValleyValue = valleysMap.get(preValleyIndex);

					// post valley
					for (Integer integer : valleysList) {
						if (integer > peakIndex) {
							postValleyIndex = integer;
							break;
						}
					}
					postValleyValue = valleysMap.get(postValleyIndex);

					if (preValleyValue >= threshold) {
						result[i][0] = preValleyIndex;
					} else {
						for (int j = preValleyIndex; j < peakIndex; j++) {
							if (power.get(j) > threshold) {
								result[i][0] = j;
								break;
							}
						}
					}

					if (postValleyValue >= threshold) {
						result[i][1] = postValleyIndex;
					} else {
						for (int j = postValleyIndex; j > peakIndex; j--) {
							if (power.get(j) > threshold) {
								result[i][1] = j;
								break;
							}
						}
					}
				}
			}
		}
		return result;
	}

	private List<Double> movingAverage(List<Double> indata, int period) {
		// period must be odd number;
		int size = indata.size();
		List<Double> result = new ArrayList<Double>();
		int[] division = new int[size];
		Queue<Double> window = new LinkedList<Double>();
		double sum = 0;
		for (int i = 0; i < size; i++) {
			if (2 * i + 1 < period) {
				division[i] = 2 * i + 1;
			} else if (2 * (size - 1 - i) + 1 < period) {
				division[i] = 2 * (size - 1 - i) + 1;
			} else {
				division[i] = period;
			}
		}

		for (int i = 0; i < size; i++) {
			if (i <= period / 2) {
				if (i == 0) {
					sum += indata.get(i);
					window.add(indata.get(i));
					double temp = Math.floor(sum / division[i]);
					result.add(temp);
					continue;
				}
				sum += indata.get(2 * i) + indata.get(2 * i - 1);
				window.add(indata.get(2 * i - 1));
				window.add(indata.get(2 * i));
				double temp = Math.floor(sum / division[i]);
				result.add(temp);
			} else if ((size - 1 - i) < period / 2) {
				sum -= window.remove();
				sum -= window.remove();
				double temp = Math.floor(sum / division[i]);
				result.add(temp);
			} else {
				sum += indata.get(i + period / 2);
				window.add(indata.get(i + period / 2));
				sum -= window.remove();
				double temp = Math.floor(sum / division[i]);
				result.add(temp);
			}
		}
		return result;
	}

	private int[][] getBandth(int[][] hill, int[][] bandth) {
		// return the selected band start and end position
		int[][] result = new int[hill.length][2];
		for (int i = 0; i < hill.length; i++) {
			double width = (double) hill[i][1] - hill[i][0];
			result[i][1] = hill[i][0] + (int) Math.round(bandth[i][1] * width / NumOfband);
			result[i][0] = hill[i][0] + (int) Math.round((bandth[i][0] - 1) * width / NumOfband);

		}
		return result;
	}

	private double removeHighFreqEnergy(Complex[] fftPower, double highFreq, int frameSize,
			long sampleRate) {
		// remove frequency energy high than designated freq
		double remainEnergy = 0;
		Ceil ceil = new Ceil();
		int length = (int) ceil.value(highFreq * (double) frameSize / (double) (sampleRate));
		remainEnergy = sum(fftPower, length);
		return remainEnergy;
	}

	private double roundn(double val, double n) {
		double result;
		double base = 10;
		double level;
		if (n < 0) {
			level = Math.pow(base, -n);
			result = Math.round(val / level) * level;
		} else if (n > 0) {
			level = Math.pow(base, n);
			result = Math.round(val / level) * level;
		} else {
			result = Math.round(val);
		}
		return result;
	}

	private int[][] smoothTill(List<Double> windowTotalPower, int peakNum) {
		// return the peaks near location required by peakNum, if peakNum is
		// unreachable, return the reachable peak numbers.
		// peakNum usually should not be larger than 4
		// period should be 5 by default
		int currentPeakNum;
		List<Double> smoothedWindowTotalPower = new ArrayList<Double>();
		smoothedWindowTotalPower = movingAverage(windowTotalPower, period);
		// peaks map in list
		List<Map<Integer, Double>> peakVallyLists = PeakValleyDetector.peak_detection(
				smoothedWindowTotalPower, 0.0); // delta(smoothedWindowTotalPower)
		currentPeakNum = peakVallyLists.get(0).keySet().size();
		while (currentPeakNum > peakNum) {
			smoothedWindowTotalPower = movingAverage(smoothedWindowTotalPower, period);
			peakVallyLists = PeakValleyDetector.peak_detection(smoothedWindowTotalPower, 0.0);
			currentPeakNum = peakVallyLists.get(0).keySet().size();
		}

		if (currentPeakNum < peakNum) {
			shouldRepeat = true;
			System.err.println("can't find enough peaks!");
			return new int[1][2];
		}

		SortedMap<Integer, Double> peaksMap = new TreeMap<Integer, Double>(peakVallyLists.get(0));
		SortedMap<Integer, Double> valleysMap = new TreeMap<Integer, Double>(peakVallyLists.get(1));
		if (shouldRepeat == false) {
			shouldRepeat = repeatCheckOnWrongPeak(peaksMap.values());
		}

		if (shouldRepeat == true) {
			System.err.println("find wrong peak!");
			return new int[1][2];
		}

		// list 0 is peaksMap, list 1 is valleysMap
		List<SortedMap<Integer, Double>> listMaps = new ArrayList<SortedMap<Integer, Double>>();
		listMaps.add(peaksMap);
		listMaps.add(valleysMap);

		int[][] result = getHillsection(listMaps, smoothedWindowTotalPower);
		// special handle for only one peak situation
		if (peakNum == 1) {
			maxPeakindex = peaksMap.firstKey();
			if (CollinsDictionaryIPA2.categories.contains("v")) {
				int gap = (int) Math.round((result[0][1] - result[0][0] + 1) / 5.0);
				result[0][0] = maxPeakindex - gap + 1;
				result[0][1] = maxPeakindex + gap;
			} else if (CollinsDictionaryIPA2.categories.contains("d")) {
				int gap = (int) Math.round((result[0][1] - result[0][0] + 1) / 6.0);
				result[0][0] = maxPeakindex - gap * 2 + 1;
				result[0][1] = maxPeakindex + gap;
			}
		}

		return result;
	}

	private double sum(Complex[] inData, int length) {
		// add from 0 to length-1
		double sum = 0;
		for (int i = 0; i < length; i++) {
			sum += inData[i].abs();
		}
		return sum;
	}

	private boolean repeatCheckOnWrongPeak(Collection<Double> collection) {
		SortedSet<Double> sortedSet = new TreeSet<Double>(collection);
		double highestPeakVal = sortedSet.last();
		double lowestPeakVal = sortedSet.first();
		double rate = lowestPeakVal / highestPeakVal;
		if (rate > rateOfWrongPeak) {
			return false;
		} else {
			return true;
		}
	}

	private int[][] twoVowelTogetherSituation(int[] peaksIn, int[][] loc,
			List<Double> windowTotalPower) {
		int peaksInSize = peaksIn.length;
		int[][] resloc = new int[peaksInSize][2];

		resloc[0] = loc[0];
		int j = 1;
		for (int i = 1; i < peaksInSize; i++) {
			if (peaksIn[i] == peaksIn[i - 1]) {
				// only two Vowel Together
				// use the loc[j] powerSubList
				j--;
				int[][] temploc = smoothTill(windowTotalPower.subList(loc[j][0], loc[j][1] + 1), 2);
				resloc[i - 1][0] = loc[j][0] + temploc[0][0];
				resloc[i - 1][1] = loc[j][0] + temploc[0][1];
				resloc[i][0] = loc[j][0] + temploc[1][0];
				resloc[i][1] = loc[j][0] + temploc[1][1];
				j++;
			} else {
				resloc[i] = loc[j++];
			}
		}
		return resloc;
	}

	private boolean repeatCheckOnNoise(List<Double> windowTotalPower, int[] position) {
		if(position[0] < 0) return true;
		int size = windowTotalPower.size();
		double totalPower = 0;
		double voicePartPower = 0;
		double avg_voicePartPower = 0;
		double avg_unvoicePartPower = 0;
		double ratio = 0;

		for (int i = 0; i < size; i++) {
			totalPower += windowTotalPower.get(i);
		}

		List<Double> powerSubList = windowTotalPower.subList(position[0], position[1]);
		int subListSize = powerSubList.size();
		for (int i = 0; i < subListSize; i++) {
			voicePartPower += powerSubList.get(i);
		}

		avg_voicePartPower = voicePartPower / (position[1] - position[0]);
		avg_unvoicePartPower = (totalPower - voicePartPower) / (size - (position[1] - position[0]));
		ratio = avg_unvoicePartPower / avg_voicePartPower;
		if (ratio >= rateOfNtoV) {
			System.out.print("repeat check on noise return true");
			return true;
			
		} else {
			return false;
		}
	}

}
