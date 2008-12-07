public class HelloWorld 
{
      public HelloWorld(){
	System.out.println("This is the constructor speaking");
	}
	public static void main(String args[]) 
      {
         System.out.println("Hello World!");
		
         System.out.println("This is the main function in HelloWorld class");
		HelloWorld hw=new HelloWorld();
      }
      public static void TestCall(String szArg)
      {
      	System.out.println(szArg);      
      }
      public static int DisplayStruct(ControlDetail ctrlDetail)
      {
      	System.out.println("Structure is:\n-------------------------");
      	System.out.println("Name:" + ctrlDetail.Name);
      	System.out.println("IP:" + ctrlDetail.IP);
      	System.out.println("Port" + ctrlDetail.Port);
      	return 1;     	
      }
	public static int DisplayStruct2(ControlDetail2 ctrlDetail)
      {
      	System.out.println("Structure is:\n-------------------------");
      	System.out.println("Path: " + ctrlDetail.path);
      	System.out.println("Tag: " + ctrlDetail.tag);
      	System.out.println("Cmd: " + ctrlDetail.cmd);
      	return 1;     	
      }

      public static void DisplayStructArray(WorkOrder ArrWO[])
      {
      	System.out.println("WorkOrders are Given hereunder:\n----------------------------");
      	for(int i = 0; i< ArrWO.length;i++)
      	{
      		System.out.println("<---Work Order Number:" + String.valueOf(i+1) + "<---");
      		System.out.println("Sum_Serial_ID: " + ArrWO[i].sumSerialId);
      		System.out.println("Access_Number: " + ArrWO[i].accessNumber);
      		System.out.println("Action_Type: " + ArrWO[i].actionType);
      		System.out.println("Effective_Date: " + ArrWO[i].effectiveDate);
      		System.out.println("Fetch_Flag: " + ArrWO[i].fetchFlag);
      		System.out.println("Reason: " + ArrWO[i].reason);
      		System.out.println("Access_Source: " + ArrWO[i].accessSource);
      	}
      	
      }
      public static Object ReturnObjFunc()
      {
      	System.out.println("Going to return an object from java");
      	ReturnData RetData = new ReturnData(1,"Successfull function call");
      	return RetData;
      }
}