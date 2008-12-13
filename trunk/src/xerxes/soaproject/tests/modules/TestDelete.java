package xerxes.soaproject.tests.modules;

import xerxes.soaproject.modules.LuceneDeleteModule;

public class TestDelete {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LuceneDeleteModule ldm=new LuceneDeleteModule();
		ldm.deleteFile("/home/csrazvan/workspace/SOAProj/tests/test.mp3");
	}

}
