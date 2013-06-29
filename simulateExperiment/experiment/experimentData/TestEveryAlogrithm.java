package experimentData;

import java.util.ArrayList;
import java.util.List;

import com.fc.caseRunner.CaseRunner;
import com.fc.caseRunner.CaseRunnerWithBugInject;
import com.fc.model.FIC;
import com.fc.model.IterAIFL;
import com.fc.model.OFOT;
import com.fc.model.RI;
import com.fc.testObject.TestCase;
import com.fc.testObject.TestCaseImplement;
import com.fc.testObject.TestSuite;
import com.fc.testObject.TestSuiteImplement;
import com.fc.tuple.CorpTupleWithTestCase;
import com.fc.tuple.Tuple;

import driver.ChainProcess;
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
	}

	public void expChainFeedBack(TestCase wrongCase, List<Tuple> bugs,
			int[] param, TestSuite suite) {
		System.out.println("FeedBack");
		CaseRunner caseRunner = getCaseRunner(bugs);
		FeedBackProcess fb = new FeedBackProcess(wrongCase, caseRunner, param,
				suite);
		fb.testWorkFlow();
	}

	public void expFIC(TestCase wrongCase, List<Tuple> bugs, int[] param) {
		System.out.println("FIC_BS");
		CaseRunner caseRunner = getCaseRunner(bugs);
		FIC fic = new FIC(wrongCase, param, caseRunner);
		fic.FicNOP();

		for (Tuple bug : fic.getBugs()) {
			System.out.println(bug.toString());
		}
		System.out.println("all:" + fic.getExtraCases().getTestCaseNum());
		for (int i = 0; i < fic.getExtraCases().getTestCaseNum(); i++) {
			System.out.print(fic.getExtraCases().getAt(i).getStringOfTest());
			System.out
					.println(fic.getExtraCases().getAt(i).testDescription() == TestCase.PASSED ? "pass"
							: "fail");
		}
	}

	public void expRI(TestCase wrongCase, List<Tuple> bugs, int[] param) {
		System.out.println("RI");
		CaseRunner caseRunner = getCaseRunner(bugs);

		CorpTupleWithTestCase generate = new CorpTupleWithTestCase(wrongCase,
				param);

		RI ri = new RI(generate, caseRunner);
		List<Tuple> tupleg = ri.process(wrongCase);
		for (Tuple tuple : tupleg)
			System.out.println(tuple.toString());
		System.out
				.println("all:" + ri.getAddtionalTestSuite().getTestCaseNum());
		for (int i = 0; i < ri.getAddtionalTestSuite().getTestCaseNum(); i++) {
			System.out.print(ri.getAddtionalTestSuite().getAt(i)
					.getStringOfTest());
			System.out.println(ri.getAddtionalTestSuite().getAt(i)
					.testDescription() == TestCase.PASSED ? "pass" : "fail");
		}
	}

	public void expOFOT(TestCase wrongCase, List<Tuple> bugs, int[] param) {
		System.out.println("OFOT");
		CaseRunner caseRunner = getCaseRunner(bugs);
		OFOT ofot = new OFOT();
		ofot.process(wrongCase, param, caseRunner);

		System.out.println("bugs:");
		for (Tuple tuple : ofot.getBugs()) {
			System.out.println(tuple.toString());
		}
		System.out.println("cases:" + ofot.getExecuted().size());

		for (TestCase cases : ofot.getExecuted().keySet()) {
			System.out.print(cases.getStringOfTest());
			System.out
					.println(cases.testDescription() == TestCase.PASSED ? "pass"
							: "fail");
		}

	}

	public void expIterAIFL(TestCase wrongCase, List<Tuple> bugs, int[] param) {
		System.out.println("IterAIFL");
		CorpTupleWithTestCase generate = new CorpTupleWithTestCase(wrongCase,
				param);
		CaseRunner caseRunner = getCaseRunner(bugs);
		IterAIFL ifl = new IterAIFL(generate, caseRunner);
		ifl.process(wrongCase);
		for (Tuple tuple : ifl.getBugs()) {
			System.out.println(tuple.toString());
		}
		System.out.println("cases:" + ifl.getSuite().getTestCaseNum());
		for (int i = 0; i < ifl.getSuite().getTestCaseNum(); i++) {
			System.out.print(ifl.getSuite().getAt(i).getStringOfTest());
			System.out
					.println(ifl.getSuite().getAt(i).testDescription() == TestCase.PASSED ? "pass"
							: "fail");
		}
	}

	private CaseRunner getCaseRunner(List<Tuple> bugs) {
		CaseRunner caseRunner = new CaseRunnerWithBugInject();
		for (Tuple bug : bugs)
			((CaseRunnerWithBugInject) caseRunner).inject(bug);
		return caseRunner;
	}

	public void test() {
		int[] wrong = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1 };
		int[] pass = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0 };
		TestCase rightCase = new TestCaseImplement();
		((TestCaseImplement) rightCase).setTestCase(pass);
		TestCase wrongCase = new TestCaseImplement();
		((TestCaseImplement) wrongCase).setTestCase(wrong);

		TestSuite rightSuite = new TestSuiteImplement();
		rightSuite.addTest(rightCase);

		int[] param = new int[] { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
				3, 3, 3, 3, 3, 3, 3, 3 };

		Tuple bugModel = new Tuple(1, wrongCase);
		bugModel.set(0, 2);

		Tuple bugModel2 = new Tuple(1, wrongCase);
		bugModel2.set(0, 4);

		List<Tuple> bugs = new ArrayList<Tuple>();
		bugs.add(bugModel);
		// bugs.add(bugModel2);

		this.expChain(wrongCase, bugs, param, rightSuite);
		this.expChainFeedBack(wrongCase, bugs, param, rightSuite);
		this.expFIC(wrongCase, bugs, param);
		this.expRI(wrongCase, bugs, param);
		this.expOFOT(wrongCase, bugs, param);
		this.expIterAIFL(wrongCase, bugs, param);
	}

	public static void main(String[] args) {
		TestEveryAlogrithm ta = new TestEveryAlogrithm();
		ta.test();
	}

}
