#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <sys/socket.h>
#include <linux/netlink.h>
//#include <jni.h>

#define MAX_PAYLOAD 1024  
#define STRING_LENGTH 1024

//structures needed to send/receive data - 
//for now they are global, I will put them in the right place later
struct sockaddr_nl src_addr, dest_addr;
struct nlmsghdr *nlh = NULL;
struct iovec iov;
int sock_fd;
struct sockaddr_nl nladdr;
struct msghdr msg;

int cmd;
char buf[STRING_LENGTH];
char tag[STRING_LENGTH];
char path[STRING_LENGTH];

//JNIEnv *env;
//JavaVM * jvm;

struct ControlDetail2
{
	int ID;
	char path[STRING_LENGTH];
	char tag[STRING_LENGTH];
	int cmd;
};

/*
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
}*/

/*
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
*/


int send_to_server(int cmd, char * path, char * tag) {



	return 0;
}


int main(void) {

	//env = create_vm(&jvm);
	//if (env == NULL)
	//	return 1;

	sock_fd = socket(PF_NETLINK, SOCK_RAW,NETLINK_UNUSED);

	memset(&src_addr, 0, sizeof(src_addr));
	src_addr.nl_family = AF_NETLINK;
	src_addr.nl_pid = getpid();  /* self pid */
	src_addr.nl_groups = 0;  /* not in mcast groups */

	bind(sock_fd, (struct sockaddr*)&src_addr, sizeof(src_addr));

	memset(&dest_addr, 0, sizeof(dest_addr));
	dest_addr.nl_family = AF_NETLINK;
	dest_addr.nl_pid = 0;   // For Linux Kernel /
	dest_addr.nl_groups = 0; // unicast 

	nlh=(struct nlmsghdr *)malloc(NLMSG_SPACE(MAX_PAYLOAD));

	// Fill the netlink message header 
	nlh->nlmsg_len = NLMSG_SPACE(MAX_PAYLOAD);
	nlh->nlmsg_pid = getpid();  // self pid 
	nlh->nlmsg_flags = 0;

	// Fill in the netlink message payload
	strcpy(NLMSG_DATA(nlh), "Hello you! This is Userspace App");

	iov.iov_base = (void *)nlh;
	iov.iov_len = nlh->nlmsg_len;
	msg.msg_name = (void *)&dest_addr;
	msg.msg_namelen = sizeof(dest_addr);
	msg.msg_iov = &iov;
	msg.msg_iovlen = 1;

	sendmsg(sock_fd, &msg, 0);

	memset(nlh, 0, NLMSG_SPACE(MAX_PAYLOAD));
	recvmsg(sock_fd, &msg, 0);
	printf(" Received message payload: %s\n", NLMSG_DATA(nlh));
	
	// Read message from kernel 
	while (1) {
		char str[1024];
		char * pch;
		char path[1024], syscall[10];

		bzero(str, STRING_LENGTH);
		bzero(path, STRING_LENGTH);
		bzero(syscall, STRING_LENGTH);

		memset(nlh, 0, NLMSG_SPACE(MAX_PAYLOAD));
		recvmsg(sock_fd, &msg, 0);
		printf(" Received message payload: %s\n", NLMSG_DATA(nlh));

		sprintf(str, "%s", NLMSG_DATA(nlh));

		pch = strtok (str,",");
		if (pch != NULL) {
			sprintf(syscall, "%s", pch);
			pch = strtok (NULL, ",");
			if (pch != NULL)
				sprintf(path, "%s", pch);
		}

		printf(" syscall=%s, path=%s\n", syscall, path);		

		send_to_server(cmd, path, syscall);

		//break command
		//if (cmd == QUIT) 
		//break;
	}
	
	// Close Netlink Socket 
	close(sock_fd);
	
	//Release resources.
	//int n = jvm->DestroyJavaVM();

	return 0;
}
