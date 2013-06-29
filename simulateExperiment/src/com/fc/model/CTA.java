package com.fc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.fc.caseRunner.CaseRunner;
import com.fc.caseRunner.CaseRunnerWithBugInject;
import com.fc.testObject.TestCase;
import com.fc.testObject.TestCaseImplement;
import com.fc.testObject.TestSuite;
import com.fc.testObject.TestSuiteImplement;
import com.fc.tuple.Tuple;

public class CTA {
	public HashMap<TestCase, Integer> executed;

	public CTA() {
		executed = new HashMap<TestCase, Integer>();
	}

	public void process(int[] paramters, String[] classes, TestSuite suite,
			String[] state) throws Exception {
		FastVector attr = this.constructAttributes(paramters, classes);
		Instances data = this.constructData(suite, attr, state);
		System.out.println(data.numInstances());
		this.constructClassifier(data);
	}

	public FastVector constructAttributes(int[] paramters, String[] classes) {

		FastVector attributes = new FastVector(paramters.length + 1);

		for (int i = 0; i < paramters.length; i++) {
			FastVector labels = new FastVector(paramters[i]);
			for (int j = 0; j < paramters[i]; j++)
				labels.addElement("" + j);
			String c = "c_" + i;
			Attribute nominal = new Attribute(c, labels);
			attributes.addElement(nominal);
		}

		final FastVector classValues = new FastVector(classes.length);
		for (String str : classes)
			classValues.addElement(str);

		attributes.addElement(new Attribute("Class", classValues));

		return attributes;
		// Instances data = new Instances();
	}

	public Instances constructData(TestSuite suite, FastVector attributes,
			String[] state) {
		Instances data = new Instances("Data1", attributes,
				suite.getTestCaseNum());
		// set the index of the col of the class in the data
		data.setClassIndex(data.numAttributes() - 1);

		for (int i = 0; i < suite.getTestCaseNum(); i++) {
			TestCase testCase = suite.getAt(i);
			Instance ins = new Instance(testCase.getLength() + 1);
			for (int j = 0; j < testCase.getLength(); j++) {
				ins.setValue((Attribute) attributes.elementAt(j),
						testCase.getAt(j));
			}
			ins.setValue(
					(Attribute) attributes.elementAt(testCase.getLength()),
					state[i]);
			// testCase.testDescription() == TestCase.PASSED ? "pass": "fail");
			data.add(ins);
		}
		return data;
	}

	public void constructClassifier(Instances data) throws Exception {
		J48 classifier = new J48();
		String[] options = new String[3];
		options[0] = "-U";
		
		
		options[1] = "-M";
		options[2] = "1";
		classifier.setOptions(options);
		classifier.setConfidenceFactor((float) 0.25);
		classifier.buildClassifier(data);
		System.out.println(classifier.toString());
	}

	public void test() throws Exception {
		int[] parameters = new int[] { 3, 3, 3, 3, 3, 3, 3, 3 };
		int[] wrong = new int[] { 1, 1, 1, 1, 1, 1, 1, 1 };
		TestCase wrongCase = new TestCaseImplement();
		((TestCaseImplement) wrongCase).setTestCase(wrong);
		wrongCase.setTestState(TestCase.FAILED);

		Tuple bugModel1 = new Tuple(2, wrongCase);
		bugModel1.set(0, 4);
		bugModel1.set(1, 7);

		Tuple bugModel2 = new Tuple(1, wrongCase);
		bugModel2.set(0, 3);

		CaseRunner caseRunner = new CaseRunnerWithBugInject();
		((CaseRunnerWithBugInject) caseRunner).inject(bugModel1);
		// ((CaseRunnerWithBugInject) caseRunner).inject(bugModel2);

		TestSuite suite = new TestSuiteImplement();
		suite.addTest(wrongCase);

		this.process(suite, parameters, caseRunner);
	}

	public void process(TestSuite suite, int[] parameters, CaseRunner runner)
			throws Exception {
		for (int i = 0; i < suite.getTestCaseNum(); i++) {
			this.executed.put(suite.getAt(i), suite.getAt(i).testDescription());
		}
		for (int i = 0; i < suite.getTestCaseNum(); i++) {
			if (suite.getAt(i).testDescription() == TestCase.FAILED)
				this.processOneTestCase(suite.getAt(i), parameters, runner);
		}
		TestSuite su = new TestSuiteImplement();
		String[] state = new String[this.executed.size()];
		int cur = 0;
		for (TestCase testCase : this.executed.keySet()) {
			su.addTest(testCase);
			state[cur] = testCase.testDescription() == TestCase.PASSED ? "pass"
					: "fail";
//			System.out.print(testCase.getStringOfTest());
//			System.out.println(" "+state[cur]);
			cur++;
		}
		String[] classes = { "pass", "fail" };
		this.process(parameters, classes, su, state);
	}

	public void processOneTestCase(TestCase wrongCase, int[] parameters,
			CaseRunner runner) {
		List<List<TestCase>> array = this.generateSuiteArray(wrongCase,
				parameters);
		for (List<TestCase> list : array) {
			for (TestCase testCase : list) {
				if (executed.containsKey(testCase))
					testCase.setTestState(executed.get(testCase));
				else {
					testCase.setTestState(runner.runTestCase(testCase));
					executed.put(testCase, testCase.testDescription());
				}
			}
		}
	}

	public List<List<TestCase>> generateSuiteArray(TestCase wrongCase,
			int[] parameters) {
		List<List<TestCase>> suite = new ArrayList<List<TestCase>>();
		// TestSuite suite = new TestSuiteImplement();
		for (int i = 0; i < wrongCase.getLength(); i++) {
			List<TestCase> temp = new ArrayList<TestCase>();
			TestCase lastCase = wrongCase;
			for (int k = 0; k < (parameters[i] - 1); k++) {
				TestCase casetemple = new TestCaseImplement(
						wrongCase.getLength());
				for (int j = 0; j < lastCase.getLength(); j++)
					casetemple.set(j, lastCase.getAt(j));
				casetemple.set(i, (casetemple.getAt(i) + 1) % parameters[i]);
				temp.add(casetemple);
				lastCase = casetemple;
			}
			suite.add(temp);
		}
		return suite;
	}

	public static void main(String[] args) throws Exception {
		CTA cta = new CTA();
		int[] param = { 3, 3, 3 };
		String[] classes = { "pass", "err1", "err2", "err3" };

		int[][] suites = { { 0, 0, 0 }, { 0, 0, 1 }, { 0, 0, 2 }, { 0, 1, 0 },
				{ 0, 1, 1 }, { 0, 1, 2 }, { 0, 2, 0 }, { 0, 2, 1 },
				{ 0, 2, 2 }, { 1, 0, 0 }, { 1, 0, 1 }, { 1, 0, 2 },
				{ 1, 1, 0 }, { 1, 1, 1 }, { 1, 1, 2 }, { 1, 2, 0 },
				{ 1, 2, 1 }, { 1, 2, 2 }, { 2, 0, 0 }, { 2, 0, 1 },
				{ 2, 0, 2 }, { 2, 1, 0 }, { 2, 1, 1 }, { 2, 1, 2 },
				{ 2, 2, 0 }, { 2, 2, 1 }, { 2, 2, 2 } };
		TestSuite suite = new TestSuiteImplement();

		for (int[] test : suites) {
			TestCaseImplement testCase = new TestCaseImplement(test.length);
			testCase.setTestCase(test);
			suite.addTest(testCase);
		}

		String[] state = { "pass", "pass", "err3", "pass", "pass", "pass",
				"pass", "pass", "pass", "err1", "err1", "err1", "err3", "err1",
				"err1", "err1", "err1", "err1", "err2", "err2", "err2", "err2",
				"err2", "err2", "err3", "err2", "err2" };

		cta.process(param, classes, suite, state);
		cta.test();
	}
}
