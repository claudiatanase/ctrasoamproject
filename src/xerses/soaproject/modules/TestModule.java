package xerses.soaproject.modules;

public class TestModule implements Modules {
	
	public boolean parseFile(String filePath) {
		System.out.println("Parsing " + filePath);
		return true;
	}

	public String getExtension() {
		// TODO Auto-generated method stub
		return null;
	}
}
