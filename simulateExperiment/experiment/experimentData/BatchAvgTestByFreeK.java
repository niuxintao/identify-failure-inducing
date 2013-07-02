package experimentData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.fc.tuple.Tuple;

public class BatchAvgTestByFreeK {
	public static final int CHAIN = 0;
	public static final int AUGCHAIN = 1;
	public static final int FEEDBACK = 2;
	public static final int AUGFEEDBACK = 3;
	public static final int FIC = 4;
	public static final int RI = 5;
	public static final int OFOT = 6;
	public static final int AIFL = 7;
	public static final int LG = 8;
	public static final int SP = 9;

	public static final int NUM = 10;

	public void batchSingle(int[] lengths, int value, int degree) {
		List<List<double[]>> data = new ArrayList<List<double[]>>();
		for (int i = 0; i < NUM; i++)
			data.add(new ArrayList<double[]>());
		for (Integer len : lengths) {
			List<double[]> da = this.testSingle(len, value, degree);
			for (int i = 0; i < NUM; i++) {
				data.get(i).add(da.get(i));
			}
		}

		outputResult("single", data);
	}

	public void batchDouble(int[] lengths, int value, int degree) {
		List<List<double[]>> data = new ArrayList<List<double[]>>();
		for (int i = 0; i < NUM; i++)
			data.add(new ArrayList<double[]>());
		for (Integer len : lengths) {
			List<double[]> da = this.testDouble(len, value, degree);
			for (int i = 0; i < NUM; i++) {
				data.get(i).add(da.get(i));
			}
		}

		outputResult("pair", data);
	}

	public void outputResult(String fileId, List<List<double[]>> data) {
		// output result
		this.setOutPut(fileId + "-freeK-addtion.txt");
		for (List<double[]> da : data) {
			outputResult(da, 0);
		}
		this.setOutPut(fileId + "freeK-recall.txt");
		for (List<double[]> da : data) {
			outputResult(da, 1);
		}
		this.setOutPut(fileId + "freeK-precise.txt");
		for (List<double[]> da : data) {
			outputResult(da, 2);
		}
	}

	public List<double[]> testSingle(int caseLenth, int value, int degree) {
		DataBaseOfTestCase casedata = new DataBaseOfTestCase(caseLenth, value);
		ExperimentData experimentData = new ExperimentData(casedata);
		List<Tuple> bugs = experimentData.generateBugByDegree(degree);
		TestEveryAlogrithm ta = new TestEveryAlogrithm();

		// data record
		List<double[]> data = new ArrayList<double[]>();
		for (int i = 0; i < NUM; i++)
			data.add(new double[3]);

		// create bug paris
		List<List<Tuple>> buPairs = new ArrayList<List<Tuple>>();
		for (Tuple tuple : bugs) {
			List<Tuple> bgPair = new ArrayList<Tuple>();
			bgPair.add(tuple);
			buPairs.add(bgPair);
		}

		doProcess(experimentData, ta, data, buPairs);
		return data;
	}

	public List<double[]> testDouble(int caseLenth, int value, int degree) {
		DataBaseOfTestCase casedata = new DataBaseOfTestCase(caseLenth, value);
		ExperimentData experimentData = new ExperimentData(casedata);
		List<Tuple[]> bugs = experimentData.getTwoBugs(experimentData
				.generateBugByDegree(degree));

		TestEveryAlogrithm ta = new TestEveryAlogrithm();

		// data record
		List<double[]> data = new ArrayList<double[]>();
		for (int i = 0; i < NUM; i++)
			data.add(new double[3]);

		// create bug paris
		List<List<Tuple>> buPairs = new ArrayList<List<Tuple>>();
		for (Tuple[] tuple : bugs) {
			List<Tuple> bgPair = new ArrayList<Tuple>();
			bgPair.add(tuple[0]);
			bgPair.add(tuple[1]);
			buPairs.add(bgPair);
		}

		doProcess(experimentData, ta, data, buPairs);
		return data;
	}

	private void doProcess(ExperimentData experimentData,
			TestEveryAlogrithm ta, List<double[]> data,
			List<List<Tuple>> buPairs) {
		// for each bug pairs, inject and test and record
		for (List<Tuple> bgPair : buPairs) {
			add(data.get(CHAIN), ta.expChain(experimentData.getWrongCase(),
					bgPair, experimentData.getParam(),
					experimentData.getRightSuite()));
			add(data.get(AUGCHAIN), ta.expAugChain(
					experimentData.getWrongCase(), bgPair,
					experimentData.getParam(), experimentData.getRightSuite()));
			add(data.get(FEEDBACK), ta.expChainFeedBack(
					experimentData.getWrongCase(), bgPair,
					experimentData.getParam(), experimentData.getRightSuite()));
			add(data.get(AUGFEEDBACK), ta.expChainAugFeedBack(
					experimentData.getWrongCase(), bgPair,
					experimentData.getParam(), experimentData.getRightSuite()));
			add(data.get(FIC), ta.expFIC(experimentData.getWrongCase(), bgPair,
					experimentData.getParam()));
			add(data.get(RI), ta.expRI(experimentData.getWrongCase(), bgPair,
					experimentData.getParam()));
			add(data.get(OFOT), ta.expOFOT(experimentData.getWrongCase(),
					bgPair, experimentData.getParam()));
			add(data.get(AIFL), ta.expIterAIFL(experimentData.getWrongCase(),
					bgPair, experimentData.getParam()));
			add(data.get(LG), ta.expLocateGraph(experimentData.getWrongCase(),
					bgPair, experimentData.getParam(), experimentData
							.getRightSuite().getAt(0)));
			add(data.get(SP), ta.expSpectrumBased(
					experimentData.getWrongCase(), bgPair,
					experimentData.getParam()));
		}

		for (double[] da : data) {
			getAvg(da, buPairs.size());
		}
	}

	public void outputResult(List<double[]> data, int id) {
		for (double[] da : data)
			System.out.print(da[id] + " ");
		System.out.println();
	}

	public void add(double[] a, double[] b) {
		a[0] += b[0];
		a[1] += b[1];
		a[2] += b[2];
	}

	public void getAvg(double[] a, int num) {
		a[0] /= num;
		a[1] /= num;
		a[2] /= num;
		System.out.println(a[0]);
		System.out.println(a[1]);
		System.out.println(a[2]);
	}

	public void setOutPut(String name) {
		File test = new File(name);
		try {
			PrintStream out = new PrintStream(new FileOutputStream(test));
			System.setOut(out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		BatchAvgTestByFreeK fk = new BatchAvgTestByFreeK();
		fk.batchSingle(new int[] { 8, 9, 10 }, 3, 2);
		fk.batchDouble(new int[] { 8, 9, 10 }, 3, 2);

	}
}
