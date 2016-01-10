Kernel Space:

o Kernel module
> + Description:
    1. The kernel module will monitor system calls to detect when a file is created, deleted, modified and will create update packets which will be forwarded to the server.
> + Tasks:
    1. Additional functionalities will be added as required.



User Space:

o Control applications
> + Description:
    1. The control application(s) will be C(Python) programs that will interact with the kernel module, server and will offer an interface to the user.
> + Tasks:
    1. Allow the user to add/remove files(folders) from the list of monitored files(folders).
    1. Allow the user to Create/add tags to files/folders
    1. Allow the user to control the kernel module
    1. Offer advanced search functionalities to the user
    1. Allow the user to control the server.

o Server
> + Description:
    1. The main logic of the application resides in the server. The server will process data and add/retrieve it to/from the database.
    1. Depending on time constraints additional modules will be added to it in order to act as an distributed server.
    1. Modules can be added to the server to allow parsing and indexing of different types of files.
> + Tasks:
    1. Use different parsing techniques to obtain relevant information from different types of files in order to generate tags. Example: parse  word documents, mp3 files, pictures (Reading exif information).
    1. Submit/request data from the database.
    1. Maintain a list of saved Virtual Folders ( cache ) optimize searches.

o Database
> + Description:
    1. Holds information about files: tags, indexed text and so on.
    1. Can be a distributed database.
