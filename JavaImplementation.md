# Introduction #

This page will offer information about the Java side of the project.


# Details #

## Development language ##

Java is a programming language developed by Sun in 1995. It shares a lot of its syntax with C and C++ but offers a simpler object model and fewer low-level facilites.
The unique thing about Java at the time when it first appeared is that it was developed in a manner that allows a Java application to be developed on one platform and run on multiple ones. In order to perform this Java source code is compiled into bytecode which can be run ( interpreted ) by different JVM( Java Virtual Machines) operating on different opperating systems.
Another interesting thing about Java is the Garbage Collector an automatic mechanism of memory management in the object lifecycle.The programmer determines when objects are created, and the Java runtime is responsible for recovering the memory once objects are no longer in use. Once no references to an object remain, the unreachable object becomes eligible to be freed automatically by the garbage collector. Something similar to a memory leak may still occur if a programmer's code holds a reference to an object that is no longer needed, typically when objects that are no longer needed are stored in containers that are still in use.

The language was extended from its creation and now it is used on various platforms ( even mobile ones) and for different purposes ( web development, desktop apps, etc).

### Primary goals ###
There were five primary goals in the creation of the Java language:
**It should be "simple, object oriented, and familiar".** It should be "robust and secure".
**It should be "architecture neutral and portable".** It should execute with "high performance".
**It should be "interpreted, threaded, and dynamic".**

## Apis and frameworks ##

### Apache Lucene ###

[Apache Lucene](http://lucene.apache.org/java/docs/index.html) is a free/open source information retrieval library. It is supported by the [Apache Software Foundation](http://en.wikipedia.org/wiki/Apache_Software_Foundation) and is released under the [Apache Software License](http://en.wikipedia.org/wiki/Apache_Software_License).
Although originally created for Java it was ported to programming languages including Delphi, Perl, C#, C++, Python, Ruby and PHP.

#### Features and common use ####

While suitable for any application which requires full text indexing and searching capability, Lucene has been widely recognized for its utility in the implementation of Internet search engines and local, single-site searching.
At the core of Lucene's logical architecture is the idea of a document containing fields of text. This flexibility allows Lucene's API to be independent of file format. Text from PDFs, HTML, Microsoft Word documents, as well as many others can all be indexed so long as their textual information can be extracted.

#### Lucene-based projects ####

Lucene itself is just an indexing and search library and does not contain crawling and HTML parsing functionality. The Apache project Nutch is based on Lucene and provides this functionality; the Apache project Solr is a fully-featured search server based on Lucene; Compass is a Java Search Engine Framework built on top of Lucene.

### JUnit ###

[JUnit](http://www.junit.org/) is a unit testing framework for the Java programming language. Created by Kent Beck and Erich Gamma, JUnit is one of the xUnit family of frameworks that originated with Kent Beck's SUnit. JUnit has spawned its own ecosystem of JUnit extensions.
Experience gained with JUnit has been important in the development of test-driven development, and as a result, some knowledge of JUnit is often presumed in discussions of test-driven development.
JUnit has been ported to other languages, including PHP (PHPUnit), C# (NUnit), Python (PyUnit), Fortran (fUnit), Perl (Test::Class and Test::Unit), C++ (CPPUnit) and JavaScript (JSUnit) . This family of unit testing frameworks is referred to collectively as xUnit. TestNG has many of the same goals as JUnit.

#### Examples ####

```
  public class HelloWorld
  {
    @Test public void testMultiplication()
    {
      // Testing if 3*2=6:
      Assert.assertEquals ("Multiplication", 6, 3*2);
    }
  }
```

The method testMultiplication will be discovered automatically by its Test Annotation (a feature of Java 5). It offers a fundamental test using only the framework and the core of the Java virtual machine and language specifications.
But there are several issues to consider here: JUnit is not a programming language, so this trivial example does not demonstrate the power of JUnit. It is conventional to see test case class names end with "Test", as well as the prefix name specified as the name of the class being tested. Also, something more meaningful is usually printed in the assertion message. The unit test subclasses the base class TestCase as in the following example:

```
public class MultiplierTest extends TestCase
  {
    public void testMultiplication()
    {
      // Testing if the Multiplier class agrees that, 2*2=4:
      assertEquals ("Multiplication basic test case 2*2", 4, Multiplier.multiply (2, 2) );
    }
  
```
### JDBC library ###

Java Database Connectivity (JDBC) is an API for the Java programming language that defines how a client may access a database. It provides methods for querying and updating data in a database. JDBC is oriented towards relational databases.

#### Example ####
```
PreparedStatement ps = conn.prepareStatement( "SELECT i.*, j.* FROM Omega i, Zappa j WHERE i.name = ? AND j.num = ?" );
try {
    // In the SQL statement being prepared, each question mark is a placeholder
    // that must be replaced with a value you provide through a "set" method invocation.
    // The following two method calls replace the two placeholders; the first is
    // replaced by a string value, and the second by an integer value.
    ps.setString(1, "Poor Yorick");
    ps.setInt(2, 8008);
 
    // The ResultSet, rs, conveys the result of executing the SQL statement.
    // Each time you call rs.next(), an internal row pointer, or cursor,
    // is advanced to the next row of the result.  The cursor initially is
    // positioned before the first row.
    ResultSet rs = ps.executeQuery();
    try {
        while ( rs.next() ) {
            int numColumns = rs.getMetaData().getColumnCount();
            for ( int i = 1 ; i <= numColumns ; i++ ) {
                // Column numbers start at 1.
                // Also there are many methods on the result set to return
                // the column as a particular type. Refer to the Sun documentation
                // for the list of valid conversions.
                System.out.println( "COLUMN " + i + " = " + rs.getObject(i) );
            } // for
        } // while
    } finally {
        rs.close();
    }
} finally {
   ps.close();
} // try
```

## Project organization ##

The project is organized accordingly with the Java coding conventions. Each module of the project resides in its own package. All packages have a common root ( in this case **soa.project.xerxes**). All packages reside in the **src** directory and when compiled the .class files will be found in the **bin**
From here we have:
**soa.project.xerxes.modules - the source code for modules that parse files** soa.project.xerxes.server - the source code for the java server
**soa.project.xerxes.interceptor.kernel - the kernel module ( developed in C ) that monitors file activity.** soa.project.xerxes.interceptor.user - the user space application that is used to send and recieve data from the kernel module.

_Any other files that will be added later will have to conform to the already in place coding conventions._

## Testing process ##

At the end of each development phase the code will be tested. The bulk of the tests will be [unit tests](http://en.wikipedia.org/wiki/Unit_test) and for this JUnit will be used.
Several examples of tests are :
**testing that the Java code that writes/reads data from the DB performs correctly.** testing that the server receives and responds correctly to queries from the user space application.

After each phase(if possible) integration tests shall be ran to test if the different modules of the application perform correctly when working together.
