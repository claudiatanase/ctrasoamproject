#include <stdio.h>
#include <jni.h>
#include <string.h>

#define PATH_SEPARATOR ';' /* define it to be ':' on Solaris */
#define USER_CLASSPATH "." /* where Prog.class is */

#define STRING_LENGTH 1024

#define QUIT		0
#define ADD		1
#define REM		2
#define TAG		3
#define TADD		4
#define SEARCH  	5
#define SAVE		6
#define UNK		7

int cmd = UNK;
char buf[STRING_LENGTH];
char tag[STRING_LENGTH];
char path[STRING_LENGTH];


struct ControlDetail2
{
	int ID;
	char path[STRING_LENGTH];
	char tag[STRING_LENGTH];
	int cmd;
};

JNIEnv *env;
JavaVM * jvm;


JNIEnv* create_vm(JavaVM ** jvm) {
	
    JNIEnv *env;
    JavaVMInitArgs vm_args;
    JavaVMOption options;
    
    options.optionString = "-Djava.class.path=./xerxes.jar"; //Path to the java source code
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
	clsH = env->FindClass("xerxes/soaproject/bridge/HelloWorld");
	clsC = env->FindClass("xerxes/soaproject/bridge/ControlDetail2");


	//Obtaining Method IDs
	if (clsH != NULL)
	{
		midMain       = env->GetStaticMethodID(clsH, "main", "([Ljava/lang/String;)V");
		midDispStruct = env->GetStaticMethodID(clsH,"DisplayStruct2","(Lxerxes/soaproject/bridge/ControlDetail2;)I");
	
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


int test_file_path(char * path) {

	FILE * f;

	f = fopen(path, "r");

	if (f == NULL) {
		return 0;
	} else {	
		fclose(f);
		return 1;
	}
}

int main(int argc, char* argv[])
{

	env = create_vm(&jvm);
	if (env == NULL)
		return 1;
	
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
		printf("\nVa rugam introduceti comanda:\n");

		scanf("%s", buf);

		printf("Ati ales %s\n", buf);

		cmd = QUIT;

		if ((strcmp(buf,"ADD") == 0) || strcmp(buf,"add") == 0) {
			printf("Introduceti calea catre fisier:\n");
			scanf("%s", path);
			printf("Ati ales %s\n", path);
			cmd = ADD;
			if (test_file_path(path)) {
				//send cmd to server
				send_to_server(ADD, path, tag);
			} else {
				printf("Fisier inexistent!!!\n");
			}
			bzero(path, STRING_LENGTH);
		}
		if ((strcmp(buf,"REM") == 0) || strcmp(buf,"rem") == 0) {
			printf("Introduceti calea catre fisier:\n");
			scanf("%s", path);
			printf("Ati ales %s\n", path);
			cmd = REM;
			if (test_file_path(path)) {
				//send cmd to server
				send_to_server(REM, path, tag);
			} else {
				printf("Fisier inexistent!!!\n");
			}
			bzero(path, STRING_LENGTH);
		}
		if ((strcmp(buf,"TAG") == 0) || strcmp(buf,"tag") == 0) {
			printf("Introduceti numele tagului:\n");
			scanf("%s", tag);
			printf("Ati ales %s\n", tag);
			cmd = TAG;
			//send cmd to server
			send_to_server(TAG, path, tag);
			bzero(tag, STRING_LENGTH);
		}
		if ((strcmp(buf,"TADD") == 0) || strcmp(buf,"tadd") == 0) {
			printf("Introduceti numele tagului:\n");
			scanf("%s", tag);
			printf("Introduceti calea catre fisier:\n");
			scanf("%s", path);
			printf("Ati ales %s %s\n", tag, path);
			cmd = TADD;
			if (test_file_path(path)) {
				//send cmd to server
				send_to_server(TADD, path, tag);
			} else {
				printf("Fisier inexistent!!!\n");
			}
			bzero(path, STRING_LENGTH);
			bzero(tag, STRING_LENGTH);
		}
		if ((strcmp(buf,"SEARCH") == 0) || strcmp(buf,"search") == 0) {
			cmd = SEARCH;
			printf("Introduceti parametri pentru search:\n");
			scanf("%s", path);
			//send cmd to server
			send_to_server(SEARCH, path, tag);
			bzero(path, STRING_LENGTH);
		}
		if ((strcmp(buf,"SAVE") == 0) || strcmp(buf,"save") == 0) {
			cmd = SAVE;
			//send cmd to server
			send_to_server(SAVE, path, tag);
			bzero(path, STRING_LENGTH);
		}
		if ((strcmp(buf,"QUIT") == 0) || strcmp(buf,"quit") == 0) {
			cmd = QUIT;
		}
		
	}	


	//Release resources.
	int n = jvm->DestroyJavaVM();
    	return 0;
}
