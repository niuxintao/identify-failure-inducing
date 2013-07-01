package experimentData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.fc.tuple.Tuple;

public class BatchTestByFixK {
	public BatchTestByFixK() {

	}

	public void testSingle(int caseLenth, int value, int degree) {
		DataBaseOfTestCase casedata = new DataBaseOfTestCase(caseLenth, value);
		ExperimentData experimentData = new ExperimentData(casedata);
		List<Tuple> bugs = experimentData.generateBugByDegree(degree);
		TestEveryAlogrithm ta = new TestEveryAlogrithm();
		List<double[]> chain = new ArrayList<double[]>();
		List<double[]> augchain = new ArrayList<double[]>();
		List<double[]> feedBack = new ArrayList<double[]>();
		List<double[]> augFeedBack = new ArrayList<double[]>();
		List<double[]> fic = new ArrayList<double[]>();
		List<double[]> ri = new ArrayList<double[]>();
		List<double[]> ofot = new ArrayList<double[]>();
		List<double[]> aifl = new ArrayList<double[]>();
		List<double[]> lg = new ArrayList<double[]>();
		List<double[]> sp = new ArrayList<double[]>();

		for (Tuple tuple : bugs) {
			List<Tuple> cn = new ArrayList<Tuple>();
			cn.add(tuple);
			chain.add(ta.expChain(experimentData.getWrongCase(), cn,
					experimentData.getParam(), experimentData.getRightSuite()));
			augchain.add(ta.expAugChain(experimentData.getWrongCase(), cn,
					experimentData.getParam(), experimentData.getRightSuite()));
			feedBack.add(ta.expChainFeedBack(experimentData.getWrongCase(), cn,
					experimentData.getParam(), experimentData.getRightSuite()));
			augFeedBack.add(ta.expChainFeedBack(experimentData.getWrongCase(),
					cn, experimentData.getParam(),
					experimentData.getRightSuite()));
			fic.add(ta.expFIC(experimentData.getWrongCase(), cn,
					experimentData.getParam()));
			ri.add(ta.expRI(experimentData.getWrongCase(), cn,
					experimentData.getParam()));
			ofot.add(ta.expOFOT(experimentData.getWrongCase(), cn,
					experimentData.getParam()));
			aifl.add(ta.expIterAIFL(experimentData.getWrongCase(), cn,
					experimentData.getParam()));
			lg.add(ta.expLocateGraph(experimentData.getWrongCase(), cn,
					experimentData.getParam(), experimentData.getRightSuite()
							.getAt(0)));
			sp.add(ta.expSpectrumBased(experimentData.getWrongCase(), cn,
					experimentData.getParam()));
		}
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

	public void testDouble(int caseLenth, int value, int degree) {
		DataBaseOfTestCase casedata = new DataBaseOfTestCase(caseLenth, value);
		ExperimentData experimentData = new ExperimentData(casedata);
		List<Tuple[]> bugs = experimentData.getTwoBugs(experimentData
				.generateBugByDegree(degree));
	//	TestEveryAlogrithm ta = new TestEveryAlogrithm();

		for (Tuple[] tuple : bugs) {
			List<Tuple> cn = new ArrayList<Tuple>();
			cn.add(tuple[0]);
			cn.add(tuple[1]);
			// double[] chain = ta.expChain(experimentData.getWrongCase(), cn,
			// experimentData.getParam(), experimentData.getRightSuite());
		}
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

}
