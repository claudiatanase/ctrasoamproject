# Introduction #

Task Plan

# Details #


# 1. Objective: #
The purpose of our project is to develop an advanced system used for file organization and localization that would integrate seamless with an existing OS. To accomplish that we will be using a combination of normal search, semantic labeling and Virtual Folders. Depending on time constraints the project can be further improved with a distributed component that would allow the same functionality over large networks of computers.

### 2. Technologies and specifications: ###
**C for the following components:**

 Kernel Space module

 User Space control application

**Java for the following components:**

 Server

 Server modules (that will parse different types of files)

**Apache Lucene for text search engine library.**

**Python for any scripts required**

**MySQL for the database**

**JDBC will be used to acces the DB from the JAVA server.**

**Hadoop for the distributed part of the project.**


# Specifications: #
> The kernel module intercepts system calls and signals this to the server. The server receives these updates from kernel module and interacts with the user through our control application . The main function of the server is to index and retrieve files. It atomatically extracts basic file information, such as the filename, path, size, modified date, time of creation, etc. Files are passed to appropriate modules for further metadata extraction and indexing. We will also provide full-text indexing on text files by using a search engine library called Lucene. The server also manages Virtual Folders and user-defined labels. Users interact with the server through our user-space control application to save queries as Virtual Folders, and to define their own labels and use them as organizational semantic tags.


# 3. Task description and schedule: #

**Razvan**

 Database

**Create the database which holds information about files: tags, indexed text and so on.**

 Server+modules

**Implement the server which will process data and add/retrieve data to/from the database. Modules will be added to the server to allow parsing and indexing of different types of files.**

 Server control

**Interface with the application (Java)**

**Interface with the module (Java)**

 Linux Integration

**Integrate our new file system with Linux**

 Distributed

Optional: daemon+cache


**Claudia**

 Kernel Module

**Monitor system calls to detect when a file is created, deleted, modified and create update packets which will be forwarded to the server**

 User Space control Application

**Implement the control application which will interact with the kernel module, with the server and will offer an interface to the user.**

**Sub-tasks:**

 Allow the user to add/remove files(folders) from the list of monitored
files(folders).

 Allow the user to Create/add tags to files/folders

 Allow the user to control the kernel module

 Offer advanced search functionalities to the user

 Allow the user to control the server

 Server Module Interface ( C )

**Implement an interface between the module and the server**

 Server App Interface ( C )

**Implement an interface between the control application and the server**

 Linux Integration
