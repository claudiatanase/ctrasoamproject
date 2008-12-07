public class WorkOrder
{
	String		sumSerialId;	
	String		accessNumber;
	String		actionType;
	String		effectiveDate;
	String		fetchFlag;
	String		reason;
	String		accessSource;
	public WorkOrder(String szSumID,String szAccNum, String szActType, String szEffectDate, String fetchFlg, String szReason, String szAccSrc )
	{
		this.sumSerialId = szSumID;
		this.accessNumber = szAccNum;
		this.actionType = szActType;
		this.effectiveDate = szEffectDate;
		this.fetchFlag = fetchFlg;
		this.reason = szReason;
		this.accessSource = szAccSrc;			
	}	
};