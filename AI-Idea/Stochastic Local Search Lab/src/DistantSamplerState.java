import java.util.Arrays;
import java.util.Random;

public class DistantSamplerState implements State{

	double[][] data;
	int numSamples;
	int dataSize;
	int nDim;
	
	int[] sampleIndices;
	int lastPosition;
	int lastSampleIndex;
	
	Random r = new Random();
	
	public DistantSamplerState(double[][] data, int numSamples) {
		this.data = data;
		this.numSamples = numSamples;
		this.dataSize = data.length;
		this.nDim = data[0].length;
		
		sampleIndices = new int[numSamples];
		for (int i = 0; i < numSamples; i++) {
			sampleIndices[i] = r.nextInt(dataSize);
			while (!checkDiffer(sampleIndices, i)) {
				sampleIndices[i] = r.nextInt(dataSize);
//				System.out.println("get here");
			}
		}
	}
	
	private static double invDistance(double[] vector1, double[] vector2) {
		double squaredSum = 0;
		for (int i = 0; i < vector1.length; i++) {
			squaredSum += (vector1[i] - vector2[i]) * (vector1[i] - vector2[i]);
		}
		return 1.0 / squaredSum;
	}
	
	private boolean checkDiffer (int[] vector, int id) {
		assert id < vector.length : "Wrong use of method";
		return checkDiffer(vector, id, vector[id]);
	}
	
	private boolean checkDiffer (int[] vector, int id, int number) {
		if (id == 0) {
			return true;
		}
		for (int i = id - 1; i >= 0; i--) {
			if (number == vector[i]) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void step() {
		lastPosition = r.nextInt(numSamples);
		lastSampleIndex = sampleIndices[lastPosition];
		int newSampleId = r.nextInt(dataSize);
		while(!checkDiffer(sampleIndices, numSamples, newSampleId)) {
			newSampleId = r.nextInt(dataSize);
		}
		sampleIndices[lastPosition] = newSampleId;
	}

	@Override
	public void undo() {
		sampleIndices[lastPosition] = lastSampleIndex;
	}

	@Override
	public double energy() {
		double sum = 0;
		for (int i = 0; i < numSamples; i++) {
			for (int j = i + 1; j < numSamples; j++) {
				sum += invDistance(data[sampleIndices[i]], data[sampleIndices[j]]);
			}
		}
		return sum;
	}
	
	public int[] getSampleIndices() {
		Arrays.sort(sampleIndices);
		return sampleIndices;
	}
	
	public String toString() {
		return Arrays.toString(getSampleIndices());
	}
	
	public DistantSamplerState clone() {
		try {
			DistantSamplerState copy = (DistantSamplerState) super.clone();
			copy.data = data.clone();
			copy.numSamples = numSamples;
			copy.nDim = nDim;
			copy.dataSize = dataSize;
			
			copy.sampleIndices = sampleIndices.clone();
			copy.lastPosition = lastPosition;
			copy.lastSampleIndex = lastSampleIndex;
			return copy;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
