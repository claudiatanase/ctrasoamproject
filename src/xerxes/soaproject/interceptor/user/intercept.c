#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <sys/socket.h>
#include <linux/netlink.h>

#define MAX_PAYLOAD 1024  /* maximum payload size*/

//structures needed to send/receive data - 
//for now they are global, I will put them in the right place later
struct sockaddr_nl src_addr, dest_addr;
struct nlmsghdr *nlh = NULL;
struct iovec iov;
int sock_fd;
struct sockaddr_nl nladdr;
struct msghdr msg;

char path[1024], tag[1024];
int cmd;

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


int main(void) {

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

	
	// Read message from kernel 
	while (1) {
		memset(nlh, 0, NLMSG_SPACE(MAX_PAYLOAD));
		recvmsg(sock_fd, &msg, 0);
		printf(" Received message payload: %s\n", NLMSG_DATA(nlh));

		send_to_server(cmd, path, tag);

		//break command
		//if (cmd == QUIT) 
		//break;
	}
	
	// Close Netlink Socket 
	close(sock_fd);

	return 0;
}
