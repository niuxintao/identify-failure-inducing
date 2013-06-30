package experimentData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.fc.tuple.Tuple;

public class BatchTest {
	public BatchTest() {

	}

	public void testSingle(int caseLenth, int value, int degree) {
		DataBaseOfTestCase casedata = new DataBaseOfTestCase(caseLenth, value);
		ExperimentData experimentData = new ExperimentData(casedata);
		List<Tuple> bugs = experimentData.generateBugByDegree(degree);
		TestEveryAlogrithm ta = new TestEveryAlogrithm();
		double[] chain = new double[3];
		double[] augchain = new double[3];
		double[] feedBack = new double[3];
		double[] augFeedBack = new double[3];
		double[] fic = new double[3];
		double[] ri = new double[3];
		double[] ofot = new double[3];
		double[] aifl = new double[3];
		double[] lg = new double[3];
		double[] sp = new double[3];

		for (Tuple tuple : bugs) {
			List<Tuple> cn = new ArrayList<Tuple>();
			cn.add(tuple);
			add(chain, ta.expChain(experimentData.getWrongCase(), cn,
					experimentData.getParam(), experimentData.getRightSuite()));
			add(augchain, ta.expAugChain(experimentData.getWrongCase(), cn,
					experimentData.getParam(), experimentData.getRightSuite()));
			add(feedBack, ta.expChainFeedBack(experimentData.getWrongCase(),
					cn, experimentData.getParam(),
					experimentData.getRightSuite()));
			add(augFeedBack, ta.expChainFeedBack(experimentData.getWrongCase(),
					cn, experimentData.getParam(),
					experimentData.getRightSuite()));
			add(fic,
					ta.expFIC(experimentData.getWrongCase(), cn,
							experimentData.getParam()));
			add(ri,
					ta.expRI(experimentData.getWrongCase(), cn,
							experimentData.getParam()));
			add(ofot, ta.expOFOT(experimentData.getWrongCase(), cn,
					experimentData.getParam()));
			add(aifl, ta.expIterAIFL(experimentData.getWrongCase(), cn,
					experimentData.getParam()));
			add(lg, ta.expLocateGraph(experimentData.getWrongCase(), cn,
					experimentData.getParam(), experimentData.getRightSuite()
							.getAt(0)));
			add(sp, ta.expSpectrumBased(experimentData.getWrongCase(), cn,
					experimentData.getParam()));
		}
	}

	public void add(double[] a, double[] b) {
		a[0] += b[0];
		a[1] += b[1];
		a[2] += b[2];
	}
	
	public void getAvg(double[] a, int num){
		a[0] /= num;
		a[1] /= num;
		a[2] /= num;
		System.out.println(a[0]);
		System.out.println(a[0]);
		System.out.println(a[0]);
	}

	public void testDouble(int caseLenth, int value, int degree) {
		DataBaseOfTestCase casedata = new DataBaseOfTestCase(caseLenth, value);
		ExperimentData experimentData = new ExperimentData(casedata);
		List<Tuple[]> bugs = experimentData.getTwoBugs(experimentData
				.generateBugByDegree(degree));
		TestEveryAlogrithm ta = new TestEveryAlogrithm();

		for (Tuple[] tuple : bugs) {
			List<Tuple> cn = new ArrayList<Tuple>();
			cn.add(tuple[0]);
			cn.add(tuple[1]);
			double[] chain = ta.expChain(experimentData.getWrongCase(), cn,
					experimentData.getParam(), experimentData.getRightSuite());
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
