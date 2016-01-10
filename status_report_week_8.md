# Introduction #

Status report for week 8


# Details #

Razvan Alecsandrescu:

## Tasks programmed until 26.11.2008 ##
Server completed
Database completed
Modules completed
Testing

## Tasks completed 26.11.2008 ##
Server 90% ( minor tweaks must be made to allow for automatic parsing of files)
Modules - 100% ( we have created modules that will parse text,OO files, Pdf-s, Mp3s [other audio formats can be adder easily](.md)).
> - depending on time constraints we plan to add more types of files
Database 100% - Because of the changes introduced by us relying heavily on Lucene to do the indexing and searching the MySQL db has only a few tasks left.


## Problems and changes ##

There were a couple of issues with parsing the different kinds of files but by using already existing libraries the task has been made a little more easy.
One thing that can be improved is the searching process. At this moment the user must be aware of several types of default tags and include them in his search in order to be able to make more complex queries.
This could be improved with some work by using the Lucene Query Parser.

The next thing on our agenda is to do an integration of the C/Java components and do some test runs.
Afterwards we can optimise the project and add other features.

## Claudia Tanase : Tasks programmed until 2.12.2008 ##
User-space Application

Kernel-module - User-space application integration

## Tasks completed 26.11.2008 ##
User-space Application  -> 80 % complete

Kernel-module - User-space application integration   -> 70% complete

Server - User-space application integration   -> 70 % complete


## Problems and changes ##
The two main problems now are:

- meeting with Razvan to figure out all the details regarding how to put everything together

- debugging netlink socket code used to communicate asynchronously between kernel module and user-space module