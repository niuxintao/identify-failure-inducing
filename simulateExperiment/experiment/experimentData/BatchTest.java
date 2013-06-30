package experimentData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class BatchTest {
	public BatchTest() {

	}

	public void test(int caseLenth, int value) {
		DataBaseOfTestCase casedata = new DataBaseOfTestCase(caseLenth, value);
		ExperimentData experimentData = new ExperimentData(casedata);
		
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
