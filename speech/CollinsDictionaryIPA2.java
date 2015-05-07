package phonics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CollinsDictionaryIPA2 {

	private StringBuilder sb;
	public static List<String> categories;
	private List<String> phonemes;
	private int numOfBand;

	private final HashSet<String> vow_chart;
	private final HashSet<String> diphthong_chart;
	private final HashSet<String> semi_vow_chart;
	private final HashSet<String> voiceless_const_chart;
	private final HashSet<String> voiced_const_chart;
	private final HashSet<String> symbol_chart;

	public CollinsDictionaryIPA2(int numOfBand) {
		this.numOfBand = numOfBand;
		categories = new ArrayList<String>();
		phonemes = new ArrayList<String>();

		voiced_const_chart = new HashSet<String>();
		voiced_const_chart.add("d");
		voiced_const_chart.add("dg");
		voiced_const_chart.add("rg");
		voiced_const_chart.add("tH");
		voiced_const_chart.add("v");
		voiced_const_chart.add("g");
		voiced_const_chart.add("z");
		voiced_const_chart.add("b");

		voiceless_const_chart = new HashSet<String>();
		voiceless_const_chart.add("t");
		voiceless_const_chart.add("s");
		voiceless_const_chart.add("k");
		voiceless_const_chart.add("sh");
		voiceless_const_chart.add("ch");
		voiceless_const_chart.add("th");
		voiceless_const_chart.add("f");
		voiceless_const_chart.add("h");
		voiceless_const_chart.add("p");

		semi_vow_chart = new HashSet<String>();
		semi_vow_chart.add("j");
		semi_vow_chart.add("w");
		semi_vow_chart.add("r");
		semi_vow_chart.add("m");
		semi_vow_chart.add("n");
		semi_vow_chart.add("l");
		semi_vow_chart.add("ng");

		vow_chart = new HashSet<String>();
		vow_chart.add("I");
		vow_chart.add("i");
		vow_chart.add("e");
		vow_chart.add("ae");
		// symbol "a:"
		vow_chart.add("a:");

		vow_chart.add("o");
		vow_chart.add("u");
		vow_chart.add("u");
		// symbol "<"
		vow_chart.add("^");

		vow_chart.add("eh");
		vow_chart.add("eh");

		diphthong_chart = new HashSet<String>();
		diphthong_chart.add("ei");
		diphthong_chart.add("ai");
		diphthong_chart.add("au");
		diphthong_chart.add("oi");
		diphthong_chart.add("ou");
		diphthong_chart.add("ju");
		diphthong_chart.add("3r");

		symbol_chart = new HashSet<String>();
		symbol_chart.add("'");
	}

	public int[] getPeaksIn(String[] input) {
		int numOfVowel = parse(input);
		return peaksIn(numOfVowel);
		// return selectBand(numOfVowel);
	}

	private int parse(String[] input) {
		String[] handledInput = input;
		int length = handledInput.length;
		int numOfVowel = 0;
		sb = new StringBuilder();
		String s;

		for (int i = 0; i < length; i++) {
			s = handledInput[i];
			if (voiceless_const_chart.contains(s)) {
				sb.append(s);
				categories.add("-c");
				sb.append(" ");
			} else if (voiced_const_chart.contains(s)) {
				sb.append(s);
				categories.add("+c");
				sb.append(" ");
			} else if (semi_vow_chart.contains(s)) {
				sb.append(s);
				categories.add("s");
				sb.append(" ");
			} else if (diphthong_chart.contains(s)) {
				phonemes.add(s);
				sb.append(s);
				categories.add("d");
				sb.append(" ");
				numOfVowel++;
			} else if (vow_chart.contains(s)) {
				phonemes.add(s);
				sb.append(s);
				categories.add("v");
				sb.append(" ");
				numOfVowel++;
			} else if (symbol_chart.contains(s)) {
				sb.append(s);
				sb.append(" ");
				continue;
			} else {
				sb.append("?");
				sb.append(" ");
			}
		}
		String parsedString = sb.toString();
		System.out.println(parsedString);
		return numOfVowel;
	}

	private int[] peaksIn(int numOfVowel) {
		// bandth start from 1 to 7
		String[] catagArray = new String[categories.size()];
		catagArray = categories.toArray(catagArray);
		int[] result = new int[numOfVowel];

		int index = 0;
		int peakIn = 0;
		int cataLength = catagArray.length;
		for (int i = 0; i < cataLength; i++) {
			if (catagArray[i].equals("v")) {
				if (i > 0) {
					if (catagArray[i - 1].equals("v")) {
						// "v" "v"
						result[index++] = peakIn;
					} else if (catagArray[i - 1].equals("d")) {
						// "d" "v"
						result[index++] = peakIn;
					} else {
						// "?" "v"
						result[index++] = ++peakIn;
					}
				} else {
					// i == 0
					result[index++] = ++peakIn;
				}
			} else if (catagArray[i].equals("d")) {
				if (i > 0) {
					if (catagArray[i - 1].equals("v")) {
						// "v" "d"
						result[index++] = peakIn;
					} else if (catagArray[i - 1].equals("d")) {
						// "d" "d"
						result[index++] = peakIn;
					} else {
						// "?" "d"
						result[index++] = ++peakIn;
					}
				} else {
					// i == 0
					result[index++] = ++peakIn;
				}
			}
		}
		return result;
	}

	public List<String> getPhonemes() {
		return phonemes;
	}

	private int[][] selectBand(int num) {
		// bandth start from 1 to 7
		int[][] bandth = new int[num][2];
		String[] catagArray = new String[categories.size()];
		catagArray = categories.toArray(catagArray);

		int j = 0;
		int cataLength = catagArray.length;
		for (int i = 0; i < cataLength; i++) {
			if (catagArray[i].equals("v")) {
				if (i > 0) {
					// "+c" "v" ...
					if (catagArray[i - 1].equals("+c")) {
						if (i < catagArray.length - 1) {
							// "+c" "v" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (3.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));

							}
							// "+c" "v" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (7.0 / 7.0));
							}
							// "+c" "v" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
							}
							// "+c" "v" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (1.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
							// "+c" "v" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (1.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "+c" "v" "o"
							bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (7.0 / 7.0));
						}
					}
					// "-c" "v" ...
					else if (catagArray[i - 1].equals("-c")) {
						if (i < catagArray.length - 1) {
							// "-c" "v" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
							}
							// "-c" "v" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (3.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
							}
							// "-c" "v" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j][0] = (int) Math.round(numOfBand * (1.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
							// "-c" "v" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (1.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (3.0 / 7.0));
							}
							// "-c" "v" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "-c" "v" "o"
							bandth[j][0] = (int) Math.round(numOfBand * (3.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
						}
					}
					// "s" "v" ...
					else if (catagArray[i - 1].equals("s")) {
						if (i < catagArray.length - 1) {
							// "s" "v" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (3.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (6.0 / 7.0));
							}
							// "s" "v" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (7.0 / 7.0));
							}
							// "s" "v" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j][0] = (int) Math.round(numOfBand * (3.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
							}
							// "s" "v" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (3.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
							}
							// "s" "v" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (3.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "s" "v" "o"
							bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (7.0 / 7.0));
						}
					}
					// "v" "v" ...
					else if (catagArray[i - 1].equals("v")) {
						if (i < catagArray.length - 1) {
							// "v" "v" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (7.0 / 7.0));
							}
							// "v" "v" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (7.0 / 7.0));
							}
							// "v" "v" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (6.0 / 7.0));
							}
							// "v" "v" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
							// "v" "v" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "v" "v" "o"
							bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (7.0 / 7.0));
						}
					}
					// "d" "v" ...
					else if (catagArray[i - 1].equals("d")) {
						if (i < catagArray.length - 1) {
							// "d" "v" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (5.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (7.0 / 7.0));
							}
							// "d" "v" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (5.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (7.0 / 7.0));
							}
							// "d" "v" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (6.0 / 7.0));
							}
							// "d" "v" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
							// "d" "v" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "d" "v" "o"
							bandth[j][0] = (int) Math.round(numOfBand * (5.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (7.0 / 7.0));
						}
					}
				} else {
					// i = 0, pre is o
					// "o" "v" ...
					if (i < catagArray.length - 1) {
						// "o" "v" "+c"
						if (catagArray[i + 1].equals("+c")) {
							bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
						}
						// "o" "v" "-c"
						else if (catagArray[i + 1].equals("-c")) {
							bandth[j][0] = (int) Math.round(numOfBand * (3.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
						}
						// "o" "v" "s"
						else if (catagArray[i + 1].equals("s")) {
							bandth[j][0] = (int) Math.round(numOfBand * (1.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
						}
						// "o" "v" "v"
						else if (catagArray[i + 1].equals("v")) {
							bandth[j][0] = (int) Math.round(numOfBand * (1.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (3.0 / 7.0));
						}
						// "o" "v" "d"
						else if (catagArray[i + 1].equals("v")) {
							bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
						}
					} else {
						// i = categories.length-1, post is o
						// "o" "v" "o"
						bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
						bandth[j++][1] = (int) Math.round(numOfBand * (6.0 / 7.0));
					}
				}
			} else if (catagArray[i].equals("d")) {
				if (i > 0) {
					// "+c" "d" ...
					if (catagArray[i - 1].equals("+c")) {
						if (i < catagArray.length - 1) {
							// "+c" "d" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
							// "+c" "d" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
							}
							// "+c" "d" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
							// "+c" "d" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (3.0 / 7.0));
							}
							// "+c" "d" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (3.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "+c" "d" "o"
							bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
						}
					}
					// "-c" "d" ...
					else if (catagArray[i - 1].equals("-c")) {
						if (i < catagArray.length - 1) {
							// "-c" "d" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
							// "-c" "d" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
							}
							// "-c" "d" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
							// "-c" "d" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (3.0 / 7.0));
							}
							// "-c" "d" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (3.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "-c" "d" "o"
							bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
						}
					}
					// "s" "d" ...
					else if (catagArray[i - 1].equals("s")) {
						if (i < catagArray.length - 1) {
							// "s" "d" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
							// "s" "d" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
							}
							// "s" "d" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
							// "s" "d" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
							// "s" "d" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "s" "d" "o"
							bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
						}
					}
					// "v" "d" ...
					else if (catagArray[i - 1].equals("v")) {
						if (i < catagArray.length - 1) {
							// "v" "d" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (3.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
							}
							// "v" "d" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (6.0 / 7.0));
							}
							// "v" "d" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j][0] = (int) Math.round(numOfBand * (3.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
							}
							// "v" "d" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
							// "v" "d" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "v" "d" "o"
							bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (6.0 / 7.0));
						}
					}
					// "d" "d" ...
					else if (catagArray[i - 1].equals("d")) {
						if (i < catagArray.length - 1) {
							// "d" "d" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (6.0 / 7.0));
							}
							// "d" "d" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (6.0 / 7.0));
							}
							// "d" "d" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j][0] = (int) Math.round(numOfBand * (3.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
							}
							// "d" "d" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
							// "d" "d" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
								bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "d" "d" "o"
							bandth[j][0] = (int) Math.round(numOfBand * (4.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (6.0 / 7.0));
						}
					}
				} else {
					// i = 0, pre is o
					// "o" "v" ...
					if (i < catagArray.length - 1) {
						// "o" "d" "+c"
						if (catagArray[i + 1].equals("+c")) {
							bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
						}
						// "o" "d" "-c"
						else if (catagArray[i + 1].equals("-c")) {
							bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
						}
						// "o" "d" "s"
						else if (catagArray[i + 1].equals("s")) {
							bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (4.0 / 7.0));
						}
						// "o" "d" "v"
						else if (catagArray[i + 1].equals("v")) {
							bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (3.0 / 7.0));
						}
						// "o" "d" "d"
						else if (catagArray[i + 1].equals("v")) {
							bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
							bandth[j++][1] = (int) Math.round(numOfBand * (3.0 / 7.0));
						}
					} else {
						// i = categories.length-1, post is o
						// "o" "d" "o"
						bandth[j][0] = (int) Math.round(numOfBand * (2.0 / 7.0));
						bandth[j++][1] = (int) Math.round(numOfBand * (5.0 / 7.0));
					}
				}
			}
		}
		return bandth;
	}
}
