package xerxes.soaproject.modules;

public class TestModule2 implements Modules {

	@Override
	public String getExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean parseFile(String pathName) {
		System.out.println("Parsing"+pathName);
		return true;
	}

}
