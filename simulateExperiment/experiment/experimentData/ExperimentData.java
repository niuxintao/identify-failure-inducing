package experimentData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fc.caseRunner.CaseRunner;
import com.fc.caseRunner.CaseRunnerWithBugInject;
import com.fc.testObject.TestCase;
import com.fc.testObject.TestCaseImplement;
import com.fc.testObject.TestSuite;
import com.fc.tuple.CorpTupleWithTestCase;
import com.fc.tuple.Tuple;

public class ExperimentData {
	private CaseRunner caseRunner;
	private CorpTupleWithTestCase generate;
	private TestCase wrongCase;
	private TestSuite rightSuite;

	private List<Tuple> bugs2Mode;
	private List<Tuple> bugs3Mode;
	private List<Tuple> bugs4Mode;

	private int[] param;

	public ExperimentData(DataBaseOfTestCase data) {
		this.reset(data);
	}

	public void init() {

	}

	public Tuple getRoot(TestCase testCase) {
		Tuple tuple = new Tuple(0, testCase);
		return tuple.getReverseTuple();
	}

	public List<Tuple> generateBugByDegree(int degree) {
		return this.getRoot(this.wrongCase).getChildTuplesByDegree(degree);

	}

	public List<Tuple> generate2DegreeBug() {
		if (bugs2Mode == null)
			bugs2Mode = this.getRoot(this.wrongCase).getChildTuplesByDegree(2);
		return bugs2Mode;

	}

	public List<Tuple> generate3DegreeBug() {
		if (bugs3Mode == null)
			bugs3Mode = this.getRoot(this.wrongCase).getChildTuplesByDegree(3);
		return bugs3Mode;

	}

	public List<Tuple> generate4DegreeBug() {
		if (bugs4Mode == null)
			bugs4Mode = this.getRoot(this.wrongCase).getChildTuplesByDegree(4);
		return bugs4Mode;
	}

	public void reset(DataBaseOfTestCase data) {
		clear();
		this.wrongCase = data.getWrongCase();
		this.rightSuite = data.getRightSuite();
		caseRunner = new CaseRunnerWithBugInject();
		generate = new CorpTupleWithTestCase(data.getWrongCase(),
				data.getParam());

		param = data.getParam();
	}

	public TestCase getWrongCase() {
		return wrongCase;
	}

	public TestSuite getRightSuite() {
		return rightSuite;
	}

	public int[] getParam() {
		return param;
	}

	public void clear() {
		this.bugs2Mode = null;
		this.bugs3Mode = null;
		this.bugs4Mode = null;
	}

	public void inject(List<Tuple> bugsMode) {
		((CaseRunnerWithBugInject) caseRunner).setBugMode(bugsMode);
	}

	public CorpTupleWithTestCase getGenerate() {
		return generate;
	}

	public CaseRunner getCaseRunner() {
		return caseRunner;
	}

	public List<Tuple[]> getTwoBugs(List<Tuple> tuples) {
		List<Tuple[]> result = new ArrayList<Tuple[]>();
		for (Tuple tuple : tuples) {
			for (int index = tuples.indexOf(tuple) + 1; index < tuples.size(); index++) {
				Tuple[] combine = new Tuple[2];
				combine[0] = tuple;
				combine[1] = tuples.get(index);
				result.add(combine);
			}
		}
		return result;
	}

	public List<Tuple[]> getTwoOverlapBugs(List<Tuple> tuples) {
		List<Tuple[]> result = new ArrayList<Tuple[]>();
		for (Tuple tuple : tuples) {
			for (int index = tuples.indexOf(tuple) + 1; index < tuples.size(); index++) {
				if (this.isOverlap(tuple, tuples.get(index))) {
					Tuple[] combine = new Tuple[2];
					combine[0] = tuple;
					combine[1] = tuples.get(index);
					result.add(combine);
				}
			}
		}
		return result;
	}

	public List<Tuple[]> getTwoNonOverlapBugs(List<Tuple> tuples) {
		List<Tuple[]> result = new ArrayList<Tuple[]>();
		for (Tuple tuple : tuples) {
			for (int index = tuples.indexOf(tuple) + 1; index < tuples.size(); index++) {
				if (!this.isOverlap(tuple, tuples.get(index))) {
					Tuple[] combine = new Tuple[2];
					combine[0] = tuple;
					combine[1] = tuples.get(index);
					result.add(combine);
				}
			}
		}
		return result;
	}

	public List<Tuple[]> getOneBugWithOneNewBug(List<Tuple> tuples) {
		int[] wrong = new int[param.length];
		Random random = new Random();
		for (int i = 0; i < param.length; i++) {
			int a = random.nextInt(3);
			if (a == 0)
				wrong[i] = 2 + 1;
			else
				wrong[i] = 2;
		}
		TestCase anotherWrongCase = new TestCaseImplement();
		((TestCaseImplement) anotherWrongCase).setTestCase(wrong);
//		System.out.println("anotherWrongCase:"
//				+ anotherWrongCase.getStringOfTest());

		List<Tuple> importTuples = getRoot(anotherWrongCase)
				.getChildTuplesByDegree(2);

		List<Tuple[]> result = new ArrayList<Tuple[]>();
		for (Tuple tuple : tuples) {
			for (Tuple importTuple : importTuples) {
				Tuple[] combine = new Tuple[2];
				combine[0] = tuple;
				combine[1] = importTuple;
				result.add(combine);
			}
		}
		return result;
	}

	// two array compare( have sequence )
	public boolean isOverlap(Tuple a, Tuple b) {
		boolean result = false;
		int[] index_a = a.getParamIndex();
		int[] index_b = b.getParamIndex();
		int taga = 0, tagb = 0;
		boolean flag = true;
		while (taga < index_a.length && tagb < index_b.length) {
			if (index_a[taga] == index_b[tagb]) {
				result = true;
				break;
			}
			if (flag) {
				// count in index_a
				if (index_a[taga] < index_b[tagb]) {
					taga++;
					continue;
				} else {
					flag = false;
					tagb++;
					continue;
				}
			} else {
				// count in index_b
				if (index_b[tagb] < index_a[taga]) {
					tagb++;
					continue;
				} else {
					flag = true;
					taga++;
					continue;
				}
			}
		}
		return result;
	}

}