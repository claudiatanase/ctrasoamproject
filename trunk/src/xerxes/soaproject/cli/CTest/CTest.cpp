#include "StdAfx.h"
#include <stdio.h>
#include <jni.h>
#include <string.h>

#define PATH_SEPARATOR ';' /* define it to be ':' on Solaris */
#define USER_CLASSPATH "." /* where Prog.class is */

#define QUIT	0
#define ADD		1
#define REM		2
#define TAG		3
#define TADD	4
#define SEARCH  5
#define SAVE	6
#define UNK		7

int cmd = UNK;
char buf[1024];
char tag[1024];
char path[1024];

struct ControlDetail
{
	int ID;
	char Name[100];
	char IP[100];
	int Port;
};

struct ControlDetail2
{
	int ID;
	char path[100];
	char tag[100];
	int cmd;
};

struct WorkOrder
{
	char		sumSerialId[20];	
	char		accessNumber[18];
	char		actionType[4];
	char		effectiveDate[24];
	char		fetchFlag[2];
	char		reason[456];
	char		accessSource[100];	
};

	JNIEnv *env;
	JavaVM * jvm;


JNIEnv* create_vm(JavaVM ** jvm) {
	
    JNIEnv *env;
    JavaVMInitArgs vm_args;
    JavaVMOption options;
    //options.optionString = "-Djava.class.path=E:\\java\\Java Src\\TestStruct"; //Path to the java source code
	options.optionString = "-Djava.class.path=E:\\MyFolder\\AC\\Anul 6 MasterSPBA_1\\SOA(Sisteme de Operare Avansate)\\Proiect\\surse\\ctrasoamproject\\src\\xerxes\\soaproject\\cli\\Java Src\\TestStruct"; //Path to the java source code
    vm_args.version = JNI_VERSION_1_6; //JDK version. This indicates version 1.6
    vm_args.nOptions = 1;
    vm_args.options = &options;
    vm_args.ignoreUnrecognized = 0;
    
    int ret = JNI_CreateJavaVM(jvm, (void**)&env, &vm_args);
    if(ret < 0)
    	printf("\nUnable to Launch JVM\n");   	
	return env;
}


int send_to_server(int cmd, char * path, char * tag) {

	struct ControlDetail2 ctrlDetail;	
	ctrlDetail.ID = 1;
	strcpy(ctrlDetail.tag, tag);
	strcpy(ctrlDetail.path, path);
	ctrlDetail.cmd = cmd;
	
	printf("Struct Created in C has values:\nID:%d\nTag:%s\n path:%s\ncmd:%d\n",ctrlDetail.ID,ctrlDetail.tag,ctrlDetail.path,ctrlDetail.cmd);
	
    jclass clsH=NULL;
    jclass clsC = NULL;   
	
    jmethodID midMain = NULL;
    jmethodID midDispStruct = NULL;
	jmethodID midCtrlDetConst = NULL;
    
    jobject jobjDet = NULL;
    
    //Obtaining Classes
    clsH = env->FindClass("HelloWorld");
    clsC = env->FindClass("ControlDetail2");

	
	//Obtaining Method IDs
    if (clsH != NULL)
    {
		midMain       = env->GetStaticMethodID(clsH, "main", "([Ljava/lang/String;)V");
		midDispStruct = env->GetStaticMethodID(clsH,"DisplayStruct2","(LControlDetail2;)I");
		
	}
	else
    {
    	printf("\nUnable to find the requested class\n");    	
    }
	if(clsC != NULL)
	{
		//Get constructor ID for ControlDetail
		midCtrlDetConst = env->GetMethodID(clsC, "<init>", "(ILjava/lang/String;Ljava/lang/String;I)V");		
	}
	else
    {
    	printf("\nUnable to find the requested class\n");    	
    }	

	
	// Now we will call the functions using the their method IDs		
	
	if(midMain != NULL)
		env->CallStaticVoidMethod(clsH, midMain, NULL); //Calling the main method.
	
	
	printf("\nGoing to Call DisplayStruct\n");
	if (midDispStruct!=NULL)
	{
		if(clsC != NULL && midCtrlDetConst != NULL)
		{
			jstring StringArgTag = env->NewStringUTF(ctrlDetail.tag);
			jstring StringArgPath = env->NewStringUTF(ctrlDetail.path);
			
			//Creating the Object of ControlDetail.
			jobjDet = env->NewObject(clsC, midCtrlDetConst, (jint)ctrlDetail.ID, StringArgTag, StringArgPath, (jint)ctrlDetail.cmd);
		}
		
		if(jobjDet != NULL && midDispStruct != NULL)
			env->CallStaticIntMethod(clsH,midDispStruct,jobjDet); //Calling the method and passing ControlDetail Object as parameter
	}


	return 0;
}


int main(int argc, char* argv[])
{

	env = create_vm(&jvm);
	if (env == NULL)
		return 1;
		
	struct ControlDetail ctrlDetail;	
	ctrlDetail.ID = 11;
	strcpy(ctrlDetail.Name,"HR-HW");
	strcpy(ctrlDetail.IP,"10.32.164.133");
	ctrlDetail.Port = 9099;
	
	printf("Struct Created in C has values:\nID:%d\nName:%s\n IP:%s\nPort:%d\n",ctrlDetail.ID,ctrlDetail.Name,ctrlDetail.IP,ctrlDetail.Port);

	/********************************************************/
	struct WorkOrder WO[2];
	strcpy(WO[0].sumSerialId,"2000");
	strcpy(WO[0].accessNumber,"2878430");
	strcpy(WO[0].actionType,"04");
	strcpy(WO[0].effectiveDate,"25-12-2007 12:20:30 PM");
	strcpy(WO[0].fetchFlag, "0");
	strcpy(WO[0].reason,"Executed Successfully");
	strcpy(WO[0].accessSource,"PMS");
	strcpy(WO[1].sumSerialId,"1000");
	strcpy(WO[1].accessNumber,"2878000");
	strcpy(WO[1].actionType,"T4");
	strcpy(WO[1].effectiveDate,"25-12-2007 11:20:30 PM");
	strcpy(WO[1].fetchFlag,"0");
	strcpy(WO[1].reason,"");
	strcpy(WO[1].accessSource,"RMS");

	
    jclass clsH=NULL;
    jclass clsC = NULL;
    jclass clsW = NULL;
	jclass clsR = NULL;
    jmethodID midMain = NULL;
    jmethodID midCalling = NULL;
    jmethodID midDispStruct = NULL;
    jmethodID midDispStructArr = NULL;
	jmethodID midRetObjFunc = NULL;
    jmethodID midCtrlDetConst = NULL;
    jmethodID midWoConst = NULL;
    
    jobject jobjDet = NULL;
	jobject jobjRetData = NULL;
    jobjectArray jobjWOArr = NULL;
    
    //Obtaining Classes
    clsH = env->FindClass("HelloWorld");
    clsC = env->FindClass("ControlDetail");
    clsW = env->FindClass("WorkOrder");
	
	//Obtaining Method IDs
    if (clsH != NULL)
    {
		midMain       = env->GetStaticMethodID(clsH, "main", "([Ljava/lang/String;)V");
		midCalling    = env->GetStaticMethodID(clsH,"TestCall","(Ljava/lang/String;)V");
		midDispStruct = env->GetStaticMethodID(clsH,"DisplayStruct","(LControlDetail;)I");
		midDispStructArr = env->GetStaticMethodID(clsH,"DisplayStructArray","([LWorkOrder;)V");
		midRetObjFunc = env->GetStaticMethodID(clsH,"ReturnObjFunc","()Ljava/lang/Object;");
	}
	else
    {
    	printf("\nUnable to find the requested class\n");    	
    }
	if(clsC != NULL)
	{
		//Get constructor ID for ControlDetail
		midCtrlDetConst = env->GetMethodID(clsC, "<init>", "(ILjava/lang/String;Ljava/lang/String;I)V");		
	}
	else
    {
    	printf("\nUnable to find the requested class\n");    	
    }	

	if(clsW != NULL)
	{
		//Get Constructor ID for WorkOrder
		midWoConst = env->GetMethodID(clsW, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");		
	}
	else
    {
    	printf("\nUnable to find the requested class\n");    	
    }

	/************************************************************************/
	/* Now we will call the functions using the their method IDs			*/
	/************************************************************************/
	if(midMain != NULL)
		env->CallStaticVoidMethod(clsH, midMain, NULL); //Calling the main method.
	
	if (midCalling!=NULL)
	{
		jstring StringArg = env->NewStringUTF("\nTestCall:Called from the C Program\n");
		//Calling another static method and passing string type parameter
		env->CallStaticVoidMethod(clsH,midCalling,StringArg);
	}
	
	printf("\nGoing to Call DisplayStruct\n");
	if (midDispStruct!=NULL)
	{
		if(clsC != NULL && midCtrlDetConst != NULL)
		{
			jstring StringArgName = env->NewStringUTF(ctrlDetail.Name);
			jstring StringArgIP = env->NewStringUTF(ctrlDetail.IP);
			
			//Creating the Object of ControlDetail.
			jobjDet = env->NewObject(clsC, midCtrlDetConst, (jint)ctrlDetail.ID, StringArgName, StringArgIP, (jint)ctrlDetail.Port);
		}
		
		if(jobjDet != NULL && midDispStruct != NULL)
			env->CallStaticIntMethod(clsH,midDispStruct,jobjDet); //Calling the method and passing ControlDetail Object as parameter
	}
	//Calling a function from java and passing Structure array to it.
	printf("\n\nGoing to call DisplayStructArray From C\n\n");
	if (midDispStructArr!=NULL)
	{
		if(clsW != NULL && midWoConst != NULL)
		{
			//Creating the Object Array that will contain 2 structures.
			jobjWOArr = (jobjectArray)env->NewObjectArray(2,clsW,env->NewObject(clsW, midWoConst,env->NewStringUTF(""),env->NewStringUTF(""),env->NewStringUTF(""),
														  env->NewStringUTF(""),env->NewStringUTF(""),env->NewStringUTF(""),env->NewStringUTF("")));         	
			//Initializing the Array
			for(int i=0;i<2;i++)
			{
				env->SetObjectArrayElement(jobjWOArr,i,env->NewObject(clsW, midWoConst,env->NewStringUTF(WO[i].sumSerialId),
														env->NewStringUTF(WO[i].accessNumber),
														env->NewStringUTF(WO[i].actionType),
														env->NewStringUTF(WO[i].effectiveDate),
														env->NewStringUTF(WO[i].fetchFlag),
														env->NewStringUTF(WO[i].reason),
														env->NewStringUTF(WO[i].accessSource)));	
			}
		}
		//Calling the Static method and passing the Structure array to it.
		if(jobjWOArr != NULL && midDispStructArr != NULL)
			env->CallStaticVoidMethod(clsW,midDispStructArr,jobjWOArr);
	}
	//Calling a Static function that return an Object
	if (midRetObjFunc != NULL)
	{
		//Calling the function and storing the return object into jobject type variable
		//Returned object is basically a structure having two fields (string and integer)
		jobjRetData = (jobject)env->CallStaticObjectMethod(clsH,midRetObjFunc,NULL);
		//Get the class of object
		clsR = env->GetObjectClass(jobjRetData);
		//Obtaining the Fields data from the returned object
		jint nRet = env->GetIntField(jobjRetData,env->GetFieldID(clsR,"returnValue","I"));
		jstring jstrLog = (jstring)env->GetObjectField(jobjRetData,env->GetFieldID(clsR,"Log","Ljava/lang/String;"));
		const char *pLog = env->GetStringUTFChars(jstrLog,0);
		
		printf("\n\nValues Returned from Object are:\nreturnValue=%d\nLog=%s",nRet,pLog);
		//After using the String type data release it.
		env->ReleaseStringUTFChars(jstrLog,pLog);
	}


	while(cmd != QUIT) {
		printf("\n\n============================================\n");
		printf("Acesta este programul de control al serverului:\n");
		printf("Comenzi posibile:\n");
		printf("Adauga un fisier pentru monitorizare - ADD path_to_file\n");
		printf("Sterge un fisier de la monitorizare - REM path_to_file\n");
		printf("Creaza un tag - TAG nume_tag\n");
		printf("Adauga un tag unui fisier - TADD tag path_to_file\n");
		printf("Cauta un fisier - SEARCH args\n");
		printf("Salveaza ultima cautare - SAVE\n");
		printf("Terminare program - QUIT\n");
		printf("\nVa rugam in troduceti comanda:\n");

		scanf("%s", buf);

		printf("Ati ales %s\n", buf);

		cmd = QUIT;

		if (strcmp(buf,"ADD") == 0) {
			printf("Introduceti calea catre fisier:\n");
			scanf("%s", path);
			printf("Ati ales %s\n", path);
			cmd = ADD;
			//send cmd to server
			send_to_server(ADD, path, tag);
		}
		if (strcmp(buf,"REM") == 0) {
			printf("Introduceti calea catre fisier:\n");
			scanf("%s", path);
			printf("Ati ales %s\n", path);
			cmd = REM;
			//send cmd to server
			send_to_server(REM, path, NULL);
		}
		if (strcmp(buf,"TAG") == 0) {
			printf("Introduceti numele tagului:\n");
			scanf("%s", tag);
			printf("Ati ales %s\n", tag);
			cmd = TAG;
			//send cmd to server
			send_to_server(TAG, NULL, tag);
		}
		if (strcmp(buf,"TADD") == 0) {
			printf("Introduceti numele tagului:\n");
			scanf("%s", tag);
			printf("Introduceti calea catre fisier:\n");
			scanf("%s", path);
			printf("Ati ales %s %s\n", tag, path);
			cmd = TADD;
			//send cmd to server
			send_to_server(TADD, path, tag);
		}
		if (strcmp(buf,"SEARCH") == 0) {
			cmd = SEARCH;
			//send cmd to server
			send_to_server(ADD, path, tag);
		}
		if (strcmp(buf,"SAVE") == 0) {
			cmd = SAVE;
			//send cmd to server
			send_to_server(ADD, NULL, NULL);
		}
		if (strcmp(buf,"QUIT") == 0) {
			cmd = QUIT;
		}

		
	}	


	//Release resources.
	int n = jvm->DestroyJavaVM();
    return 0;
}
