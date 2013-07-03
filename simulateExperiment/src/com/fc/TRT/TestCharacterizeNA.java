package com.fc.TRT;

import java.util.List;


import com.fc.testObject.TestCase;
import com.fc.testObject.TestCaseImplement;
import com.fc.testObject.TestSuite;
import com.fc.testObject.TestSuiteImplement;
import com.fc.tuple.Tuple;

public class TestCharacterizeNA {
	protected CaseRunner caseRunner;
	protected CorpTupleWithTestCase generate;
	protected TreeStruct tree;
	Tuple bugModel;
	
	public void testWorkFlow(){
		init();
		PathNA workMachine = new PathNA(tree,caseRunner,generate);	
		workMachine.process();	
		
		List<Tuple> bugs = tree.getBugModes();
		for(Tuple bug : bugs){
			System.out.println(bug.toString());
			System.out.println(bug.equals(bugModel));
		}
		
		TestSuite extra = workMachine.getExtraCases();
		System.out.println("all:"+extra.getTestCaseNum());
		for(int i = 0 ; i < extra.getTestCaseNum(); i++){
			System.out.println(extra.getAt(i).getStringOfTest());
		}
		
	}
	
	public static void main(String[] args){
		TestCharacterizeNA na = new TestCharacterizeNA();
		na.testWorkFlow();
	}

	protected void init() {
		
	    int[] wrong = new int[] { 1, 1, 1, 1, 1, 1, 1, 1,1,1,1,1,1,1,1};
		
		int[] wrong2 = new int[] { 2, 2, 2, 2, 2, 2,2, 2,2,2,2,2,2,2,2};
		
		int[] pass = new int[] { 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0,0,0,0};
		TestCase rightCase = new TestCaseImplement();
		((TestCaseImplement) rightCase).setTestCase(pass);
		TestCase wrongCase = new TestCaseImplement();
		((TestCaseImplement) wrongCase).setTestCase(wrong);
		
		TestCase wrongCase2 = new TestCaseImplement();
		((TestCaseImplement) wrongCase2).setTestCase(wrong2);

		TestSuite rightSuite = new TestSuiteImplement();
		rightSuite.addTest(rightCase);
	

		int[] param = new int[] { 3, 3, 3, 3,3,3,3,3,3,3,3,3,3,3,3};
		
		
		TestSuite wrongSuite = new TestSuiteImplement();
		wrongSuite.addTest(wrongCase);
		
		
		bugModel = new Tuple(3, wrongCase);
		bugModel.set(0, 1);
		bugModel.set(1, 2);
		bugModel.set(2, 4);
		
		Tuple bugModel2 = new Tuple(3, wrongCase2);
		bugModel2.set(0, 12);
		bugModel2.set(1, 13);
		bugModel2.set(2, 14);
		

		caseRunner = new CaseRunnerWithBugInject();
		((CaseRunnerWithBugInject) caseRunner).inject(bugModel);
		((CaseRunnerWithBugInject) caseRunner).inject(bugModel2);
		
		
		tree = new TreeStruct(wrongCase,rightSuite);
		tree.constructTree();
		tree.init();
		
		generate = new CorpTupleWithTestCase(wrongCase,param);
	}
}
