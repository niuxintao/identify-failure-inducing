package experimentData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.fc.tuple.Tuple;

public class SimulateBatchTestByFixK {
	// public static final int CHAIN = 0;
	public static final int AUGCHAIN = 0;
	// public static final int FEEDBACK = 2;
	public static final int AUGFEEDBACK = 1;
	public static final int FIC = 2;
	public static final int RI = 3;
	public static final int OFOT = 4;
	public static final int LG = 5;
	public static final int SP = 6;
	public static final int AIFL = 7;
	public static final int TRT = 8;
	public static final int AUGTRT = 9;

	public static final int NUM = 10;

	public SimulateBatchTestByFixK() {

	}

	public void testSingle(int caseLenth, int value, int degree) {
		DataBaseOfTestCase casedata = new DataBaseOfTestCase(caseLenth, value);
		ExperimentData experimentData = new ExperimentData(casedata);
		List<Tuple> bugs = experimentData.generateBugByDegree(degree);
		TestEveryAlogrithm ta = new TestEveryAlogrithm();

		// data record
		List<List<double[]>> data = new ArrayList<List<double[]>>();
		for (int i = 0; i < NUM; i++)
			data.add(new ArrayList<double[]>());

		// create bug paris
		List<List<Tuple>> buPairs = new ArrayList<List<Tuple>>();
		for (Tuple tuple : bugs) {
			List<Tuple> bgPair = new ArrayList<Tuple>();
			bgPair.add(tuple);
			buPairs.add(bgPair);
		}

		doProcess(experimentData, ta, data, buPairs, "single-"+caseLenth+"-"+degree);
	}

	public void testDouble(int caseLenth, int value, int degree) {
		DataBaseOfTestCase casedata = new DataBaseOfTestCase(caseLenth, value);
		ExperimentData experimentData = new ExperimentData(casedata);
		List<Tuple[]> bugs = experimentData.getTwoBugs(experimentData
				.generateBugByDegree(degree));

		TestEveryAlogrithm ta = new TestEveryAlogrithm();

		// data record
		List<List<double[]>> data = new ArrayList<List<double[]>>();
		for (int i = 0; i < NUM; i++)
			data.add(new ArrayList<double[]>());

		// create bug paris
		List<List<Tuple>> buPairs = new ArrayList<List<Tuple>>();
		for (Tuple[] tuple : bugs) {
			List<Tuple> bgPair = new ArrayList<Tuple>();
			bgPair.add(tuple[0]);
			bgPair.add(tuple[1]);
			buPairs.add(bgPair);
		}

		doProcess(experimentData, ta, data, buPairs, "pair-"+caseLenth+"-"+degree);
	}

	public void testImport(int caseLenth, int value, int degree) {
		DataBaseOfTestCase casedata = new DataBaseOfTestCase(caseLenth, value);
		ExperimentData experimentData = new ExperimentData(casedata);
		List<Tuple[]> bugs = experimentData
				.getOneBugWithOneNewBug(experimentData
						.generateBugByDegree(degree));

		TestEveryAlogrithm ta = new TestEveryAlogrithm();

		// data record
		List<List<double[]>> data = new ArrayList<List<double[]>>();
		for (int i = 0; i < NUM; i++)
			data.add(new ArrayList<double[]>());

		// create bug paris
		List<List<Tuple>> buPairs = new ArrayList<List<Tuple>>();
		for (Tuple[] tuple : bugs) {
			List<Tuple> bgPair = new ArrayList<Tuple>();
			bgPair.add(tuple[0]);
			bgPair.add(tuple[1]);
			buPairs.add(bgPair);
		}

		doProcess(experimentData, ta, data, buPairs, "import-"+caseLenth+"-"+degree);
	}

	private void doProcess(ExperimentData experimentData,
			TestEveryAlogrithm ta, List<List<double[]>> data,
			List<List<Tuple>> buPairs, String fileId) {
		// for each bug pairs, inject and test and record
		for (List<Tuple> bgPair : buPairs) {
			data.get(TRT).add(
					ta.expTRT(experimentData.getWrongCase(), bgPair,
							experimentData.getParam(),
							experimentData.getRightSuite()));
			data.get(AUGCHAIN).add(
					ta.expAugChain(experimentData.getWrongCase(), bgPair,
							experimentData.getParam(),
							experimentData.getRightSuite()));
			data.get(AUGTRT).add(
					ta.expAUGTRT(experimentData.getWrongCase(), bgPair,
							experimentData.getParam(),
							experimentData.getRightSuite()));
			data.get(AUGFEEDBACK).add(
					ta.expChainAugFeedBack(experimentData.getWrongCase(),
							bgPair, experimentData.getParam(),
							experimentData.getRightSuite()));
			data.get(FIC).add(
					ta.expFIC(experimentData.getWrongCase(), bgPair,
							experimentData.getParam()));
			data.get(RI).add(
					ta.expRI(experimentData.getWrongCase(), bgPair,
							experimentData.getParam()));
			data.get(OFOT).add(
					ta.expOFOT(experimentData.getWrongCase(), bgPair,
							experimentData.getParam()));
			data.get(LG).add(
					ta.expLocateGraph(experimentData.getWrongCase(), bgPair,
							experimentData.getParam(), experimentData
									.getRightSuite().getAt(0)));

			// sp set 4 degree
			data.get(SP).add(
					ta.expSpectrumBased(experimentData.getWrongCase(), bgPair,
							experimentData.getParam()));
			data.get(AIFL).add(
					ta.expIterAIFL(experimentData.getWrongCase(), bgPair,
							experimentData.getParam()));
		}

		// output result
		this.setOutPut(fileId + "-fixK-addtion.txt");
		for (List<double[]> da : data) {
			outputResult(da, 0);
		}
		this.setOutPut(fileId + "-fixK-recall.txt");
		for (List<double[]> da : data) {
			outputResult(da, 1);
		}
		this.setOutPut(fileId + "-fixK-precise.txt");
		for (List<double[]> da : data) {
			outputResult(da, 2);
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
		SimulateBatchTestByFixK fk = new SimulateBatchTestByFixK();
		fk.testSingle(8, 3, 2);
		fk.testSingle(8, 3, 3);
		fk.testSingle(8, 3, 4);

		fk.testDouble(8, 3, 2);
		fk.testImport(8, 3, 2);
	}
}
