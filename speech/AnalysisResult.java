package phonics;

public class AnalysisResult {
	private boolean shouldRepeat;
	int[] instructionArray;
	int[] degreeArray;

	public AnalysisResult(int size) {
		shouldRepeat = false;
		instructionArray = new int[size];
		degreeArray = new int[size];
	}

	public int[] getDegreeArray() {
		return degreeArray;
	}

	public int[] getInstructionArray() {
		return instructionArray;
	}

	public void setRepeat(boolean bool) {
		shouldRepeat = bool;
	}

	public boolean getRepeat() {
		return shouldRepeat;
	}

}
