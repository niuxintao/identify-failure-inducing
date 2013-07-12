package experimentData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.fc.tuple.Tuple;

public class SimulateBatchTestByFreeK {
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
	public static final int CTA = 10;

	public static final int NUM = 11;

	public SimulateBatchTestByFreeK() {

	}

	public void batchTest(int[] lengths, int value, int degree) {
		List<List<double[]>> data = new ArrayList<List<double[]>>();
		for (int i = 0; i < NUM; i++)
			data.add(new ArrayList<double[]>());
		for (Integer len : lengths) {
			List<double[]> da = this.testByDegree(len, value, degree);
			for (int i = 0; i < NUM; i++) {
				data.get(i).add(da.get(i));
			}
		}

		outputResult("single-8-freeK", data);

	}

	public void outputResult(String fileId, List<List<double[]>> data) {
		// output result
		this.setOutPut(fileId + "-freeK-addtion.txt");
		for (List<double[]> da : data) {
			outputResult(da, 0);
		}
		this.setOutPut(fileId + "-freeK-recall.txt");
		for (List<double[]> da : data) {
			outputResult(da, 1);
		}
		this.setOutPut(fileId + "-freeK-precise.txt");
		for (List<double[]> da : data) {
			outputResult(da, 2);
		}
	}

	public List<double[]> testByDegree(int caseLenth, int value, int degree) {
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

	private void doProcess(ExperimentData experimentData,
			TestEveryAlogrithm ta, List<double[]> data,
			List<List<Tuple>> buPairs) {
		// for each bug pairs, inject and test and record
		int[] param = new int[experimentData.getParam().length];
		int k = param.length;
		for (int i = 0; i < param.length; i++)
			param[i] = 3;

		for (List<Tuple> bgPair : buPairs) {
			if (k < 15)
				add(data.get(TRT), ta.expTRT(experimentData.getWrongCase(),
						bgPair, experimentData.getParam(),
						experimentData.getRightSuite()));
			if (k < 140)
			add(data.get(AUGCHAIN), ta.expAugChain(
					experimentData.getWrongCase(), bgPair,
					experimentData.getParam(), experimentData.getRightSuite()));
			if (k < 15)
				add(data.get(AUGTRT), ta.expAUGTRT(
						experimentData.getWrongCase(), bgPair,
						experimentData.getParam(),
						experimentData.getRightSuite()));
			if (k < 140)
			add(data.get(AUGFEEDBACK), ta.expChainAugFeedBack(
					experimentData.getWrongCase(), bgPair,
					experimentData.getParam(), experimentData.getRightSuite()));
			if (k < 140)
			add(data.get(FIC), ta.expFIC(experimentData.getWrongCase(), bgPair,
					experimentData.getParam()));
			if (k < 140)
			add(data.get(RI), ta.expRI(experimentData.getWrongCase(), bgPair,
					experimentData.getParam()));
			if (k < 140)
			add(data.get(OFOT), ta.expOFOT(experimentData.getWrongCase(),
					bgPair, experimentData.getParam()));
			if (k < 140)
			add(data.get(LG), ta.expLocateGraph(experimentData.getWrongCase(),
					bgPair, experimentData.getParam(), experimentData
							.getRightSuite().getAt(0)));

			// sp set 4 degree
			if (k < 30)
				add(data.get(SP), ta.expSpectrumBased(experimentData
						.getWrongCase(), bgPair, param, bgPair.get(0)
						.getDegree()));
			if (k < 15)
				add(data.get(AIFL), ta.expIterAIFL(
						experimentData.getWrongCase(), bgPair,
						experimentData.getParam()));
			if (k < 140)
				try {

					add(data.get(CTA), ta.expCTA(experimentData.getWrongCase(),
							bgPair, param));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		for (double[] da : data) {
			getAvg(da, buPairs.size());
		}
	}

	public List<double[]> exec(final ExperimentData experimentData,
			final TestEveryAlogrithm ta, final List<double[]> data,
			final List<Tuple> bgPair, final int[] param, final int Algorithm) {
		final ExecutorService exec = Executors.newFixedThreadPool(1);

		Callable<List<double[]>> call = new Callable<List<double[]>>() {
			public List<double[]> call() throws Exception {
				List<double[]> result = new ArrayList<double[]>();
				for (double[] r : data)
					result.add(r);

				switch (Algorithm) {
				case TRT:
					add(result.get(TRT), ta.expTRT(
							experimentData.getWrongCase(), bgPair,
							experimentData.getParam(),
							experimentData.getRightSuite()));
					break;
				case AUGCHAIN:
					add(result.get(AUGCHAIN), ta.expAugChain(
							experimentData.getWrongCase(), bgPair,
							experimentData.getParam(),
							experimentData.getRightSuite()));
					break;
				case AUGTRT:
					add(result.get(AUGTRT), ta.expAUGTRT(
							experimentData.getWrongCase(), bgPair,
							experimentData.getParam(),
							experimentData.getRightSuite()));
					break;
				case AUGFEEDBACK:
					add(result.get(AUGFEEDBACK), ta.expChainAugFeedBack(
							experimentData.getWrongCase(), bgPair,
							experimentData.getParam(),
							experimentData.getRightSuite()));
					break;
				case FIC:
					add(result.get(FIC), ta.expFIC(
							experimentData.getWrongCase(), bgPair,
							experimentData.getParam()));
					break;
				case RI:
					add(result.get(RI), ta.expRI(experimentData.getWrongCase(),
							bgPair, experimentData.getParam()));
					break;
				case OFOT:
					add(result.get(OFOT), ta.expOFOT(
							experimentData.getWrongCase(), bgPair,
							experimentData.getParam()));
					break;
				case LG:
					add(result.get(LG), ta.expLocateGraph(experimentData
							.getWrongCase(), bgPair, experimentData.getParam(),
							experimentData.getRightSuite().getAt(0)));
					break;
				case SP:
					add(result.get(SP), ta.expSpectrumBased(
							experimentData.getWrongCase(), bgPair, param,
							bgPair.get(0).getDegree()));
					break;
				case AIFL:
					add(result.get(AIFL), ta.expIterAIFL(
							experimentData.getWrongCase(), bgPair,
							experimentData.getParam()));
					break;
				case CTA:
					add(result.get(CTA), ta.expCTA(
							experimentData.getWrongCase(), bgPair, param));
					break;
				}
				return result;
			}
		};
		List<double[]> result = data;
		try {
			Future<List<double[]>> future = exec.submit(call);
			result = future.get(1000 * 1, TimeUnit.MILLISECONDS);
		} catch (TimeoutException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		exec.shutdown();
		return result;
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
		SimulateBatchTestByFreeK fk = new SimulateBatchTestByFreeK();
		fk.batchTest(new int[] {120 }, 10, 2);
		// fk.batchTest(new int[] { 8 , 9 , 10 , 12 , 15 , 20 , 30 , 40 , 60,
		// 100 }, 10,2 );
	}
}
