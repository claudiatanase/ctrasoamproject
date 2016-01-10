# Introduction #

Example on how to call Java code from C.


# Details #


Suppose CTest.cpp file contains the code that calls different functions from Java classes. Java Classes that are used are given here under:

HelloWorld.java

ControlDetail.java

WorkOrder.java

ReturnData.java


HelloWorld.java contains the functions that will be called from the CTest.cpp. Other three Java classes are simply used in place of structures in Java. As there is no structure concept in Java so we can use classes for that purposes. This is what other three .java file contains.


HelloWorld.java contain following functions that will be called from C/C++ code.



public static void main(String args[.md](.md))
{
}

public static void TestCall(String szArg)
{
}

public static int DisplayStruct(ControlDetail ctrlDetail)
{
}

public static void DisplayStructArray(WorkOrder ArrWO[.md](.md))
{
}

public static Object ReturnObjFunc()
{
}



To call these functions from C/C++ first you need to load the JVM using the following function


JNIEnv**create\_vm(JavaVM****jvm) {**

> JNIEnv **env;**

> JavaVMInitArgs vm\_args;

> JavaVMOption options;

> //Path to the java source code

> options.optionString = "-Djava.class.path=D:\\Java Src\\TestStruct";

> vm\_args.version = JNI\_VERSION\_1\_6; //JDK version. This indicates version 1.6

> vm\_args.nOptions = 1;

> vm\_args.options = &options;

> vm\_args.ignoreUnrecognized = 0;

> int ret = JNI\_CreateJavaVM(jvm, (void)&env, &vm\_args);

> if(ret < 0)

> printf("\nUnable to Launch JVM\n");

> return env;
}


Kindly note that to use this code you will have to modify the options.optionString variable. You will have to set the path of the Java code. I mean where the Java classes are placed. Currently it being set to D:\Java Src\TestStruct. You can modify accordingly. You will also need to modify the JDK version information in the above code as it is mentioned below:


> vm\_args.version = JNI\_VERSION\_1\_6; //JDK version. This indicates version 1.6


Modify it if you have another JDK version installed.

To call a specific Java function from C you need to do the following


1. Obtain the Class reference using the FindClass(,,) method.

2. Obtain the Method IDs of the functions of the class that you want to call using
GetStaticMethodID and GetMethodID function calls.

3. Call the functions using CallStaticVoidMethod, CallStaticIntMethod and CallStaticObjectMethod.




One important thing to be noted here is specifying the function signatures while obtaining the method IDs.