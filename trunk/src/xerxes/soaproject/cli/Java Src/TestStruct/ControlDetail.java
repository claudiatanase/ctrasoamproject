public class ControlDetail
{
	public int ID;
	public String Name;
	public String IP;
	public int Port;	
	
	public ControlDetail(int nID, String szName, String szIP, int nPort)
	{
		this.ID = nID;
		this.Name = szName;
		this.IP = szIP;
		this.Port = nPort;
	}
}