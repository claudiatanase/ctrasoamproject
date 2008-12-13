package xerxes.soaproject.bridge;

public class ControlDetail2
{
	public int ID;
	public String path;
	public String tag;
	public int cmd;	
	
	public ControlDetail2(int nID, String szTag, String szPath, int nCmd)
	{
		this.ID = nID;
		this.path = szPath;
		this.tag = szTag;
		this.cmd = nCmd;
	}
}
