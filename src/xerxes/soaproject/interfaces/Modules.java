package xerxes.soaproject.interfaces;

public interface Modules {
	public String getExtension();
	public boolean parseFile(String pathName);
	public boolean parseFile(String pathName,String[] args);
}
