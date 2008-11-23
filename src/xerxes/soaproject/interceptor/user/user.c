#include <stdio.h>
//#include <jni.h>
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


/*
struct ControlDetail
{
	int ID;
	char path[100];
	char tag[100];
	int cmd;
};


JNIEnv* create_vm(JavaVM ** jvm) {
	
    JNIEnv *env;
    JavaVMInitArgs vm_args;
    JavaVMOption options;
    options.optionString = "-Djava.class.path=/soaproject"; //Path to the java source code
    vm_args.version = JNI_VERSION_1_6; //JDK version. This indicates version 1.6
    vm_args.nOptions = 1;
    vm_args.options = &options;
    vm_args.ignoreUnrecognized = 0;
    
    int ret = JNI_CreateJavaVM(jvm, (void**)&env, &vm_args);
    if(ret < 0)
    	printf("\nUnable to Launch JVM\n");   	
	return env;
}

int init_java()
{
	JNIEnv *env;
	JavaVM * jvm;
	env = create_vm(&jvm);
	if (env == NULL)
		return 1;
		
	struct ControlDetail ctrlDetail;	
	ctrlDetail.ID = 1;
	strcpy(ctrlDetail.tag,"BARCELONA");
	strcpy(ctrlDetail.path,"/soaproject");
	ctrlDetail.cmd = UNK;
	
	printf("Struct Created in C has values:\nID:%d\nTag:%s\n path:%s\ncmd:%d\n",ctrlDetail.ID,ctrlDetail.tag,ctrlDetail.path,ctrlDetail.cmd);
	
    jclass clsH=NULL;
    jclass clsC = NULL;   
	jclass clsR = NULL;
    jmethodID midMain = NULL;
    jmethodID midCalling = NULL;
    jmethodID midDispStruct = NULL;
    
    jobject jobjDet = NULL;
    
    //Obtaining Classes
    clsH = env->FindClass("HelloWorld");
    clsC = env->FindClass("ControlDetail");

	
	//Obtaining Method IDs
    if (clsH != NULL)
    {
		midMain       = env->GetStaticMethodID(clsH, "main", "([Ljava/lang/String;)V");
		midCalling    = env->GetStaticMethodID(clsH,"TestCall","(Ljava/lang/String;)V");
		midDispStruct = env->GetStaticMethodID(clsH,"DisplayStruct","(LControlDetail;)I");
		
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
			jstring StringArgTag = env->NewStringUTF(ctrlDetail.Tag);
			jstring StringArgPath = env->NewStringUTF(ctrlDetail.Path);
			
			//Creating the Object of ControlDetail.
			jobjDet = env->NewObject(clsC, midCtrlDetConst, (jint)ctrlDetail.ID, StringArgTag, StringArgPath, (jint)ctrlDetail.Cmd);
		}
		
		if(jobjDet != NULL && midDispStruct != NULL)
			env->CallStaticIntMethod(clsH,midDispStruct,jobjDet); //Calling the method and passing ControlDetail Object as parameter
	}
	
	//Release resources.
	int n = jvm->DestroyJavaVM();
    return 0;
}
*/  


int send_to_server(int cmd, char * path, char * tag) {



	return 0;
}

int main(int argc, char * argv) {

	printf("\n\nHELLOOOOOOO!!!!\n");

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
			send_to_server(ADD, path, NULL);
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
	return 0;
}

