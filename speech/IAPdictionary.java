package phonics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IAPdictionary {

	private StringBuilder sb;
	private static String[] catagArray;
	private List<String> phonemes;
	private int numOfBand;

	private HashMap<String, String> vow_chart;
	private HashMap<String, String> diphthong_chart;
	private HashMap<String, String> semi_vow_chart;
	private HashMap<String, String> voiceless_const_chart;
	private HashMap<String, String> voiced_const_chart;

	public IAPdictionary(int numOfBand) {
		this.numOfBand = numOfBand;

		voiced_const_chart = new HashMap<String, String>();
		voiced_const_chart.put("0064", "d");
		voiced_const_chart.put("02A4", "dg");
		voiced_const_chart.put("0292", "rg");
		voiced_const_chart.put("00F0", "tH");
		voiced_const_chart.put("0076", "v");
		voiced_const_chart.put("0261", "g");
		voiced_const_chart.put("007A", "z");
		voiced_const_chart.put("0062", "b");

		voiceless_const_chart = new HashMap<String, String>();
		voiceless_const_chart.put("0074", "t");
		voiceless_const_chart.put("0073", "s");
		voiceless_const_chart.put("006B", "k");
		voiceless_const_chart.put("0283", "sh");
		voiceless_const_chart.put("02A7", "ch");
		voiceless_const_chart.put("03B8", "th");
		voiceless_const_chart.put("0066", "f");
		voiceless_const_chart.put("0068", "h");
		voiceless_const_chart.put("0070", "p");

		semi_vow_chart = new HashMap<String, String>();
		semi_vow_chart.put("006A", "j");
		semi_vow_chart.put("0077", "w");
		semi_vow_chart.put("0072", "r");
		semi_vow_chart.put("006D", "m");
		semi_vow_chart.put("006E", "n");
		semi_vow_chart.put("006C", "l");
		semi_vow_chart.put("014B", "ng");

		vow_chart = new HashMap<String, String>();
		vow_chart.put("026A", "I");
		vow_chart.put("006902D0", "i:");
		vow_chart.put("0069", "i");
		vow_chart.put("0065", "e");
		vow_chart.put("00E6", "ae");
		// symbol "a:"
		vow_chart.put("025102D0", "a:");
		vow_chart.put("0252", "o");
		vow_chart.put("025402D0", "o:");
		vow_chart.put("028A", "u");
		vow_chart.put("007502D0", "u:");
		// symbol "<"
		vow_chart.put("028C", "a");
		vow_chart.put("025C", "eh");
		vow_chart.put("025C02D0", "eh");
		vow_chart.put("0259", "eh");

		diphthong_chart = new HashMap<String, String>();
		diphthong_chart.put("0065026A", "ei");
		diphthong_chart.put("0061026A", "ai");
		diphthong_chart.put("0061028A", "au");
		diphthong_chart.put("0254026A", "oi");
		diphthong_chart.put("006F028A", "ou");
		diphthong_chart.put("006A007502D0", "ju:");
	}

	public int[][] process(String[] input) {
		int numOfVowel = parse(input);
		return selectBand2(numOfVowel);
	}

	private int parse(String[] input) {
		int numOfVowel = 0;
		catagArray = new String[input.length];
		phonemes = new ArrayList<String>();
		sb = new StringBuilder();

		for (int i = 0; i < input.length; i++) {
			String s = input[i];
			if (voiceless_const_chart.containsKey(s)) {
				sb.append(voiceless_const_chart.get(s));
				catagArray[i] = "-c";
				sb.append(" ");
			} else if (voiced_const_chart.containsKey(s)) {
				sb.append(voiced_const_chart.get(s));
				catagArray[i] = "+c";
				sb.append(" ");
			} else if (semi_vow_chart.containsKey(s)) {
				sb.append(semi_vow_chart.get(s));
				catagArray[i] = "s";
				// need to add numOfVowel ???
				sb.append(" ");
			} else if (diphthong_chart.containsKey(s)) {
				sb.append(diphthong_chart.get(s));
				phonemes.add(diphthong_chart.get(s));
				catagArray[i] = "d";
				sb.append(" ");
				numOfVowel++;
			} else if (vow_chart.containsKey(s)) {
				sb.append(vow_chart.get(s));
				phonemes.add(vow_chart.get(s));
				catagArray[i] = "v";
				sb.append(" ");
				numOfVowel++;
			} else {
				sb.append("?");
				sb.append(" ");
			}
		}
		String parsedString = sb.toString();
		System.out.println(parsedString);

		return numOfVowel;
	}

	private int[] selectBand(int num) {
		// bandth start from 1 to 7
		int[] bandth = new int[num];

		int j = 0;
		for (int i = 0; i < catagArray.length; i++) {
			if (i > 0) {
				if (catagArray[i].equals("v")) {
					// "+c" "v"
					if (catagArray[i - 1].equals("+c")) {
						bandth[j++] = (int) Math.round(numOfBand * (4.0 / 7.0));
					}
					// "-c" "v"
					else if (catagArray[i - 1].equals("-c")) {
						bandth[j++] = (int) Math.round(numOfBand * (3.0 / 7.0));
					}
					// "s" "v"
					else if (catagArray[i - 1].equals("s")) {
						bandth[j++] = (int) Math.round(numOfBand * (5.0 / 7.0));
					}
				} else if (catagArray[i].equals("d")) {
					// "c" "d"
					if (catagArray[i - 1].equals("-c")
							|| catagArray[i - 1].equals("+c")) {
						bandth[j++] = (int) Math.round(numOfBand * (5.0 / 7.0));
						;
					}
					// "s" "d"
					else if (catagArray[i - 1].equals("s")) {
						bandth[j++] = (int) Math.round(numOfBand * (4.0 / 7.0));
					}
					// "d" "c" ‘ı√¥–¥, œ¬√Ê
					else {
						bandth[j++] = (int) Math.round(numOfBand * (3.0 / 7.0));
					}
				}
			}
			// i = 0
			else {
				// "v" "c"
				if (catagArray[i].equals("v")) {
					bandth[j++] = (int) Math.round(numOfBand * (3.0 / 7.0));
				}
				// "d" "c"
				else if (catagArray[i].equals("d")) {
					bandth[j++] = (int) Math.round(numOfBand * (3.0 / 7.0));
				}
			}
		}
		return bandth;
	}

	private int[][] selectBand2(int num) {
		// bandth start from 1 to 7
		int[][] bandth = new int[num][2];
		int j = 0;
		for (int i = 0; i < catagArray.length; i++) {
			if (catagArray[i].equals("v")) {
				if (i > 0) {
					// "+c" "v" ...
					if (catagArray[i - 1].equals("+c")) {
						if (i < catagArray.length - 1) {
							// "+c" "v" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (3.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (5.0 / 7.0));

							}
							// "+c" "v" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (7.0 / 7.0));
							}
							// "+c" "v" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (5.0 / 7.0));
							}
							// "+c" "v" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (1.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
							// "+c" "v" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (1.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "+c" "v" "o"
							bandth[j++][0] = (int) Math.round(numOfBand
									* (4.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (7.0 / 7.0));
						}
					}
					// "-c" "v" ...
					else if (catagArray[i - 1].equals("-c")) {
						if (i < catagArray.length - 1) {
							// "-c" "v" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (5.0 / 7.0));
							}
							// "-c" "v" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (3.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (5.0 / 7.0));
							}
							// "-c" "v" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (1.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
							// "-c" "v" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (1.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (3.0 / 7.0));
							}
							// "-c" "v" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "-c" "v" "o"
							bandth[j++][0] = (int) Math.round(numOfBand
									* (3.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (5.0 / 7.0));
						}
					}
					// "s" "v" ...
					else if (catagArray[i - 1].equals("s")) {
						if (i < catagArray.length - 1) {
							// "s" "v" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (3.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (6.0 / 7.0));
							}
							// "s" "v" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (7.0 / 7.0));
							}
							// "s" "v" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (3.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (5.0 / 7.0));
							}
							// "s" "v" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (3.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (5.0 / 7.0));
							}
							// "s" "v" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (3.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (5.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "s" "v" "o"
							bandth[j++][0] = (int) Math.round(numOfBand
									* (4.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (7.0 / 7.0));
						}
					}
					// "v" "v" ...
					else if (catagArray[i - 1].equals("v")) {
						if (i < catagArray.length - 1) {
							// "v" "v" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (7.0 / 7.0));
							}
							// "v" "v" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (7.0 / 7.0));
							}
							// "v" "v" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (6.0 / 7.0));
							}
							// "v" "v" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
							// "v" "v" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "v" "v" "o"
							bandth[j++][0] = (int) Math.round(numOfBand
									* (4.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (7.0 / 7.0));
						}
					}
					// "d" "v" ...
					else if (catagArray[i - 1].equals("d")) {
						if (i < catagArray.length - 1) {
							// "d" "v" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (5.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (7.0 / 7.0));
							}
							// "d" "v" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (5.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (7.0 / 7.0));
							}
							// "d" "v" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (6.0 / 7.0));
							}
							// "d" "v" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
							// "d" "v" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "d" "v" "o"
							bandth[j++][0] = (int) Math.round(numOfBand
									* (5.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (7.0 / 7.0));
						}
					}
				} else {
					// i = 0, pre is o
					// "o" "v" ...
					if (i < catagArray.length - 1) {
						// "o" "v" "+c"
						if (catagArray[i + 1].equals("+c")) {
							bandth[j++][0] = (int) Math.round(numOfBand
									* (2.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (5.0 / 7.0));
						}
						// "o" "v" "-c"
						else if (catagArray[i + 1].equals("-c")) {
							bandth[j++][0] = (int) Math.round(numOfBand
									* (3.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (5.0 / 7.0));
						}
						// "o" "v" "s"
						else if (catagArray[i + 1].equals("s")) {
							bandth[j++][0] = (int) Math.round(numOfBand
									* (1.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (4.0 / 7.0));
						}
						// "o" "v" "v"
						else if (catagArray[i + 1].equals("v")) {
							bandth[j++][0] = (int) Math.round(numOfBand
									* (1.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (3.0 / 7.0));
						}
						// "o" "v" "d"
						else if (catagArray[i + 1].equals("v")) {
							bandth[j++][0] = (int) Math.round(numOfBand
									* (2.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (4.0 / 7.0));
						}
					} else {
						// i = categories.length-1, post is o
						// "o" "v" "o"
						bandth[j++][0] = (int) Math.round(numOfBand
								* (2.0 / 7.0));
						bandth[j][1] = (int) Math
								.round(numOfBand * (6.0 / 7.0));
					}
				}
			} else if (catagArray[i].equals("d")) {
				if (i > 0) {
					// "+c" "d" ...
					if (catagArray[i - 1].equals("+c")) {
						if (i < catagArray.length - 1) {
							// "+c" "d" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
							// "+c" "d" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (5.0 / 7.0));
							}
							// "+c" "d" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
							// "+c" "d" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (3.0 / 7.0));
							}
							// "+c" "d" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (3.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "+c" "d" "o"
							bandth[j++][0] = (int) Math.round(numOfBand
									* (2.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (5.0 / 7.0));
						}
					}
					// "-c" "d" ...
					else if (catagArray[i - 1].equals("-c")) {
						if (i < catagArray.length - 1) {
							// "-c" "d" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
							// "-c" "d" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (5.0 / 7.0));
							}
							// "-c" "d" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
							// "-c" "d" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (3.0 / 7.0));
							}
							// "-c" "d" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (3.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "-c" "d" "o"
							bandth[j++][0] = (int) Math.round(numOfBand
									* (2.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (5.0 / 7.0));
						}
					}
					// "s" "d" ...
					else if (catagArray[i - 1].equals("s")) {
						if (i < catagArray.length - 1) {
							// "s" "d" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
							// "s" "d" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (5.0 / 7.0));
							}
							// "s" "d" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
							// "s" "d" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
							// "s" "d" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (2.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "s" "d" "o"
							bandth[j++][0] = (int) Math.round(numOfBand
									* (2.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (5.0 / 7.0));
						}
					}
					// "v" "d" ...
					else if (catagArray[i - 1].equals("v")) {
						if (i < catagArray.length - 1) {
							// "v" "d" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (3.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (5.0 / 7.0));
							}
							// "v" "d" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (6.0 / 7.0));
							}
							// "v" "d" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (3.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (5.0 / 7.0));
							}
							// "v" "d" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
							// "v" "d" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "v" "d" "o"
							bandth[j++][0] = (int) Math.round(numOfBand
									* (4.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (6.0 / 7.0));
						}
					}
					// "d" "d" ...
					else if (catagArray[i - 1].equals("d")) {
						if (i < catagArray.length - 1) {
							// "d" "d" "+c"
							if (catagArray[i + 1].equals("+c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (6.0 / 7.0));
							}
							// "d" "d" "-c"
							else if (catagArray[i + 1].equals("-c")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (6.0 / 7.0));
							}
							// "d" "d" "s"
							else if (catagArray[i + 1].equals("s")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (3.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (5.0 / 7.0));
							}
							// "d" "d" "v"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
							// "d" "d" "d"
							else if (catagArray[i + 1].equals("v")) {
								bandth[j++][0] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
								bandth[j][1] = (int) Math.round(numOfBand
										* (4.0 / 7.0));
							}
						} else {
							// i = categories.length-1, post is o
							// "d" "d" "o"
							bandth[j++][0] = (int) Math.round(numOfBand
									* (4.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (6.0 / 7.0));
						}
					}
				} else {
					// i = 0, pre is o
					// "o" "v" ...
					if (i < catagArray.length - 1) {
						// "o" "d" "+c"
						if (catagArray[i + 1].equals("+c")) {
							bandth[j++][0] = (int) Math.round(numOfBand
									* (2.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (4.0 / 7.0));
						}
						// "o" "d" "-c"
						else if (catagArray[i + 1].equals("-c")) {
							bandth[j++][0] = (int) Math.round(numOfBand
									* (2.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (5.0 / 7.0));
						}
						// "o" "d" "s"
						else if (catagArray[i + 1].equals("s")) {
							bandth[j++][0] = (int) Math.round(numOfBand
									* (2.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (4.0 / 7.0));
						}
						// "o" "d" "v"
						else if (catagArray[i + 1].equals("v")) {
							bandth[j++][0] = (int) Math.round(numOfBand
									* (2.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (3.0 / 7.0));
						}
						// "o" "d" "d"
						else if (catagArray[i + 1].equals("v")) {
							bandth[j++][0] = (int) Math.round(numOfBand
									* (2.0 / 7.0));
							bandth[j][1] = (int) Math.round(numOfBand
									* (3.0 / 7.0));
						}
					} else {
						// i = categories.length-1, post is o
						// "o" "d" "o"
						bandth[j++][0] = (int) Math.round(numOfBand
								* (2.0 / 7.0));
						bandth[j][1] = (int) Math
								.round(numOfBand * (5.0 / 7.0));
					}
				}
			}
		}
		return bandth;
	}

	public List<String> getPhonemes() {
		return phonemes;
	}

	// for test
	// public static void main(String[] args) {
	// // TODO Auto-generated method stub
	// String[] test = { "0259","0064","0076","0190", "006E","0074", "00650049",
	// "02A4", "0069", "0073"};
	// IAPdictionary d = new IAPdictionary(7);
	// int[] res = d.process(test);
	// System.out.println(Arrays.asList(res));
	// }

}
