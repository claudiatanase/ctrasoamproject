package xerxes.soaproject.modules;

public class TestModule2 implements Modules {

	
	public String getExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean parseFile(String pathName) {
		System.out.println("Parsing"+pathName);
		return true;
	}

}
