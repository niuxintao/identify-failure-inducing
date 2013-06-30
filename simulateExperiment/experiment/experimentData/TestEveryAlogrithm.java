package experimentData;

import java.util.ArrayList;
import java.util.List;

import com.fc.caseRunner.CaseRunner;
import com.fc.caseRunner.CaseRunnerWithBugInject;
import com.fc.model.CTA;
import com.fc.model.FIC;
import com.fc.model.IterAIFL;
import com.fc.model.LocateGraph;
import com.fc.model.OFOT;
import com.fc.model.RI;
import com.fc.model.SpectrumBased;
import com.fc.testObject.TestCase;
import com.fc.testObject.TestCaseImplement;
import com.fc.testObject.TestSuite;
import com.fc.testObject.TestSuiteImplement;
import com.fc.tuple.CorpTupleWithTestCase;
import com.fc.tuple.Tuple;

import driver.ChainAugProcess;
import driver.ChainProcess;
import driver.FeedBackAugProcess;
import driver.FeedBackProcess;

public class TestEveryAlogrithm {
	// testCase bug Model then run

	public void expChain(TestCase wrongCase, List<Tuple> bugs, int[] param,
			TestSuite suite) {
		System.out.println("Chain");
		CaseRunner caseRunner = getCaseRunner(bugs);

		ChainProcess test = new ChainProcess(wrongCase, caseRunner, param,
				suite);
		test.testWorkFlow();
		this.outputResult(
				test.getWorkMachine().getPool().getExistedBugTuples(), test
						.getWorkMachine().getExtraCases());
	}

	public void expChainAugFeedBack(TestCase wrongCase, List<Tuple> bugs,
			int[] param, TestSuite suite) {
		System.out.println("FeedBackAug");
		CaseRunner caseRunner = getCaseRunner(bugs);
		FeedBackAugProcess fb = new FeedBackAugProcess(wrongCase, caseRunner,
				param, suite);
		fb.testWorkFlow();
		TestSuite addsuite = new TestSuiteImplement();
		for (TestCase testCase : fb.getFb().getTestCases())
			addsuite.addTest(testCase);

		this.outputResult(fb.getFb().getBugs(), addsuite);
	}

	public void expAugChain(TestCase wrongCase, List<Tuple> bugs, int[] param,
			TestSuite suite) {
		System.out.println("ChainAug");
		CaseRunner caseRunner = getCaseRunner(bugs);

		ChainAugProcess test = new ChainAugProcess(wrongCase, caseRunner,
				param, suite);
		test.testWorkFlow();
		this.outputResult(
				test.getWorkMachine().getPool().getExistedBugTuples(), test
						.getWorkMachine().getExtraCases());
	}

	public void expChainFeedBack(TestCase wrongCase, List<Tuple> bugs,
			int[] param, TestSuite suite) {
		System.out.println("FeedBack");
		CaseRunner caseRunner = getCaseRunner(bugs);
		FeedBackProcess fb = new FeedBackProcess(wrongCase, caseRunner, param,
				suite);
		fb.testWorkFlow();
		TestSuite addsuite = new TestSuiteImplement();
		for (TestCase testCase : fb.getFb().getTestCases())
			addsuite.addTest(testCase);
		this.outputResult(fb.getFb().getBugs(), addsuite);
	}

	public void expFIC(TestCase wrongCase, List<Tuple> bugs, int[] param) {
		System.out.println("FIC_BS");
		CaseRunner caseRunner = getCaseRunner(bugs);
		FIC fic = new FIC(wrongCase, param, caseRunner);
		fic.FicNOP();

		outputResult(fic.getBugs(), fic.getExtraCases());
	}

	public void expRI(TestCase wrongCase, List<Tuple> bugs, int[] param) {
		System.out.println("RI");
		CaseRunner caseRunner = getCaseRunner(bugs);

		CorpTupleWithTestCase generate = new CorpTupleWithTestCase(wrongCase,
				param);

		RI ri = new RI(generate, caseRunner);
		List<Tuple> tupleg = ri.process(wrongCase);
		outputResult(tupleg, ri.getAddtionalTestSuite());
	}

	public void expOFOT(TestCase wrongCase, List<Tuple> bugs, int[] param) {
		System.out.println("OFOT");
		CaseRunner caseRunner = getCaseRunner(bugs);
		OFOT ofot = new OFOT();
		ofot.process(wrongCase, param, caseRunner);

		TestSuite suite = new TestSuiteImplement();
		for (TestCase testCase : ofot.getExecuted().keySet())
			suite.addTest(testCase);
		outputResult(ofot.getBugs(), suite);

	}

	public void expIterAIFL(TestCase wrongCase, List<Tuple> bugs, int[] param) {
		System.out.println("IterAIFL");
		CorpTupleWithTestCase generate = new CorpTupleWithTestCase(wrongCase,
				param);
		CaseRunner caseRunner = getCaseRunner(bugs);
		IterAIFL ifl = new IterAIFL(generate, caseRunner);
		ifl.process(wrongCase);
		outputResult(ifl.getBugs(), ifl.getSuite());
	}
	
	public void expLocateGraph(TestCase wrongCase, List<Tuple> bugs,
			int[] param, TestCase rightCase) {
		System.out.println("LocateGraph");

		CaseRunner caseRunner = getCaseRunner(bugs);
		LocateGraph lg = new LocateGraph(caseRunner);
		Tuple tuple = new Tuple(0, wrongCase);
		tuple = tuple.getReverseTuple();

		List<Tuple> faidu = lg.locateErrorsInTest(rightCase, wrongCase, tuple);

		this.outputResult(faidu, lg.getAddtionalTestSuite());
	}
	

	// a covering array may make it better
	public void expSpectrumBased(TestCase wrongCase, List<Tuple> bugs,
			int[] param) {
		System.out.println("SpectrumBased");

		CaseRunner caseRunner = getCaseRunner(bugs);

		SpectrumBased sp = new SpectrumBased(caseRunner);
		TestSuite suite = new TestSuiteImplement();
		suite.addTest(wrongCase);

		sp.process(suite, param, 2);

		this.outputResult(sp.getFailreIndcuing(), sp.getAddtionalSuite());
	}

	// hasn't think
	public void expLocateGraphBinary() {

	}

	// need a covering array
	public void expCTA(TestSuite suite, List<Tuple> bugs, int[] param)
			throws Exception {
		System.out.println("Classified tree analysis");
		CaseRunner caseRunner = getCaseRunner(bugs);
		for (int i = 0; i < suite.getTestCaseNum(); i++)
			suite.getAt(i).setTestState(caseRunner.runTestCase(suite.getAt(i)));

		CTA cta = new CTA();
		String[] classes = { "pass", "fail" };
		String[] state = new String[suite.getTestCaseNum()];
		for (int i = 0; i < suite.getTestCaseNum(); i++)
			state[i] = (suite.getAt(i).testDescription() == TestCase.PASSED ? "pass"
					: "fail");
		cta.process(param, classes, suite, state);
	}

	private CaseRunner getCaseRunner(List<Tuple> bugs) {
		CaseRunner caseRunner = new CaseRunnerWithBugInject();
		for (Tuple bug : bugs)
			((CaseRunnerWithBugInject) caseRunner).inject(bug);
		return caseRunner;
	}

	public void outputResult(List<Tuple> bugs, TestSuite suite) {
		for (Tuple tuple : bugs) {
			System.out.println(tuple.toString());
		}
		System.out.println("cases:" + suite.getTestCaseNum());
		 for (int i = 0; i < suite.getTestCaseNum(); i++) {
		 System.out.print(suite.getAt(i).getStringOfTest());
		 System.out
		 .println(suite.getAt(i).testDescription() == TestCase.PASSED ? "pass"
		 : "fail");
		 }
	}

	public double[] getRecallAndPrecise(List<Tuple> identified,
			List<Tuple> realBugs) {
		double recall = 0;
		double precise = 0;
		for (Tuple iden : identified) {
			if (realBugs.contains(iden)) {
				recall++;
				precise++;
			}
		}
		recall = recall / (double) realBugs.size();
		precise = precise / (double) identified.size();
		
		double[] result = new double[2];
		result[0] = recall;
		result[1] = precise;
		return result;
	}
	
	public double[] getResult(List<Tuple> bugs, TestSuite suite,List<Tuple> realBugs){
		double[] result = new double[3];
		result[0] = suite.getTestCaseNum();
		double[] info = this.getRecallAndPrecise(bugs, realBugs);
 		result[1] = info[0];
		result[2] = info[1];
		
		return result;
	}

	public void test() {
		int[] wrong = new int[] { 1, 1, 1, 1, 1, 1, 1, 1 };
		int[] pass = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		TestCase rightCase = new TestCaseImplement();
		((TestCaseImplement) rightCase).setTestCase(pass);
		TestCase wrongCase = new TestCaseImplement();
		((TestCaseImplement) wrongCase).setTestCase(wrong);
		wrongCase.setTestState(TestCase.FAILED);
		rightCase.setTestState(TestCase.PASSED);

		TestSuite rightSuite = new TestSuiteImplement();
		rightSuite.addTest(rightCase);

		int[] param = new int[] { 3, 3, 3, 3, 3, 3, 3, 3 };

		Tuple bugModel = new Tuple(1, wrongCase);
		bugModel.set(0, 2);

		Tuple bugModel2 = new Tuple(1, wrongCase);
		bugModel2.set(0, 4);

		List<Tuple> bugs = new ArrayList<Tuple>();
		bugs.add(bugModel);
		// bugs.add(bugModel2);

		this.expChain(wrongCase, bugs, param, rightSuite);
		this.expChainFeedBack(wrongCase, bugs, param, rightSuite);
		this.expAugChain(wrongCase, bugs, param, rightSuite);
		this.expChainAugFeedBack(wrongCase, bugs, param, rightSuite);
		this.expFIC(wrongCase, bugs, param);
		this.expRI(wrongCase, bugs, param);
		this.expOFOT(wrongCase, bugs, param);
		this.expIterAIFL(wrongCase, bugs, param);
		this.expSpectrumBased(wrongCase, bugs, param);
		this.expLocateGraph(wrongCase, bugs, param, rightCase);
	}

	public static void main(String[] args) {
		TestEveryAlogrithm ta = new TestEveryAlogrithm();
		ta.test();
	}

}