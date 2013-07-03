package com.fc.TRT;

import com.fc.testObject.TestCase;
import com.fc.testObject.TestCaseImplement;
import com.fc.testObject.TestSuite;
import com.fc.testObject.TestSuiteImplement;
import com.fc.tuple.Tuple;

public class Testpath  {
	protected CaseRunner caseRunner;
	protected CorpTupleWithTestCase generate;
	protected TreeStruct tree;
	
	public void testWorkFlow() {
		//init();
		int[] wrong = new int[8];
		int[] pass = new int[8];
		int[] param = new int [8];
		for(int i = 0; i < 8 ; i++){
			wrong[i] = 1;
			pass[i] = 0;
			param[i] = 3;
		}

		TestCase rightCase = new TestCaseImplement();
		((TestCaseImplement) rightCase).setTestCase(pass);
		TestCase wrongCase = new TestCaseImplement();
		((TestCaseImplement) wrongCase).setTestCase(wrong);

		TestSuite rightSuite = new TestSuiteImplement();
		rightSuite.addTest(rightCase);

		Tuple bugModel = new Tuple(2, wrongCase);
		bugModel.set(0, 2);
		bugModel.set(1, 3);

		
		caseRunner = new CaseRunnerWithBugInject();
		((CaseRunnerWithBugInject)caseRunner).inject(bugModel);
		
		tree = new TreeStruct(wrongCase,rightSuite);
		tree.constructTree();
		tree.init();
		
		generate = new CorpTupleWithTestCase(wrongCase,param);
		
		Path path = new Path(tree,caseRunner,generate);
		path.process();
		System.out.println(tree.getBugModes());
		TestSuite extra = path.getExtraCases();
		System.out.println("all:"+extra.getTestCaseNum());
		for(int i = 0 ; i < extra.getTestCaseNum(); i++){
			System.out.println(extra.getAt(i).getStringOfTest());
		}
		
		/*List<Tuple> bugs = tree.getBugModes();
		for(Tuple bug : bugs){
			System.out.println(bug.toString());
		}*/
	}
	public static void main(String[] args){
		Testpath na = new Testpath();
		na.testWorkFlow();
	}
}
