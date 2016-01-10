# Introduction #
Status report for week 6.


# Details #

Razvan Alecsandrescu:

## Tasks programmed until 11.13.2008: ##

> - Database

> - Server

> - Testing

> - Documentation

## Tasks completed (11.11.2008) ##

> - Documentation

> - Database

> - Server (80%)

> - Modules (10%)


I'm on schedule. Actually a little ahead of schedule but I haven't started running into the really difficult problems of this project. And I also rule so that's a plus.

## Encountered problems and changes performed ##

No significant problems encountered. Spent some time searching for the best method to load classes dynamically ( parsing modules can and will be added to the project later and this shouldn't mean that the project must be rebuilt).

## Self-assessment ##
As always yours truly shows outstanding coding skills, motivation and may I say signs of genius. Humility is another one of my qualities.

## Assessment of technologies used ##
They work. The right tools for the right job.

## ? ##
**Best caching solution for searches and answers.** Best entry point for C code.


## Claudia Tanase ##

## Tasks programmed until 13.11.2008: ##

> - Kernel module which intercepts system calls


> - Testing the kernel module


> - Documentation


## Tasks completed (11.11.2008) ##

> - Kernel module(80%)

> - Testing the interception of the system calls interesting to us

> - Userspace module to communicate with the kernel module and server (10%)

> - Documentation

## Encountered problems and changes performed ##

Added a userspace module to ease communication between my kernel module(interceptor)(C) and the server in userspace(Java). One problem encountered was how to ensure the asynchronous flow of information between these modules.

## Self-assessment ##

I am on schedule, taking into account that there were some design changes.

## Assessment of technologies used ##

The solution to the asynchronous communication was the use of netlink sockets. They seem to do their job.