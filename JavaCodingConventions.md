# Introduction #

Code conventions are important to programmers for a number of reasons:

  * 80% of the lifetime cost of a piece of software goes to maintenance.
  * Hardly any software is maintained for its whole life by the original author.
  * Code conventions improve the readability of the software, allowing engineers to understand new code more quickly and thoroughly.
  * If you ship your source code as a product, you need to make sure it is as well packaged and clean as any other product you create.


# Details #

## 1. File organization ##

Each Java source file contains a single public class or interface. When private classes and interfaces are associated with a public class, you can put them in the same source file as the public class. The public class should be the first class or interface in the file.

Java source files have the following ordering:

  * Beginning comments (see "Beginning Comments" on page 4)
  * Package and Import statements
  * Class and interface declarations (see "Class and Interface Declarations" on page 4)

### 1.1 Beginning Comments ###

All source files should begin with a c-style comment that lists the class name, version information, date, and copyright notice:

```
    /*
     * Classname
     * 
     * Version information
     *
     * Date
     * 
     * Copyright notice
     */
```

### 1.2 Package and Import Statements ###

The first non-comment line of most Java source files is a package statement. After that, import statements can follow.

### 1.3 Class and Interface Declarations ###

The following table describes the parts of a class or interface declaration, in the order that they should appear.


|   | Part of Class/Interface Declaration | Notes |
|:--|:------------------------------------|:------|
| 1 | Class/interface documentation comment (/...**/)**|See "Documentation Comments" on page 9 for information on what should be in this comment. |
| 2 | class or interface statement        |       |
| 3 | Class/interface implementation comment (/**...**/), if necessary| This comment should contain any class-wide or interface-wide information that wasn't appropriate for the class/interface documentation comment.|
| 4 | Class (static) variables            | First the public class variables, then the protected, then package level (no access modifier), and then the private.|
| 5 | Instance variables                  | First public, then protected, then package level (no access modifier), and then private.|
| 6 | Constructors                        |       |
| 7 | Methods                             | These methods should be grouped by functionality rather than by scope or accessibility. For example, a private class method can be in between two public instance methods. The goal is to make reading and understanding the code easier.   |


## 2 Identation ##
our spaces should be used as the unit of indentation. The exact construction of the indentation (spaces vs. tabs) is unspecified. Tabs must be set exactly every 8 spaces (not 4).
### 2.1 Line Length ###

Avoid lines longer than 80 characters, since they're not handled well by many terminals and tools.

Note: Examples for use in documentation should have a shorter line length-generally no more than 70 characters.
### 2.2 Wrapping Lines ###

When an expression will not fit on a single line, break it according to these general principles:

  * Break after a comma.
  * Break before an operator.
  * Prefer higher-level breaks to lower-level breaks.
  * Align the new line with the beginning of the expression at the same level on the previous line.
  * If the above rules lead to confusing code or to code that's squished up against the right margin, just indent 8 spaces instead.

Here are some examples of breaking method calls:
```
    someMethod(longExpression1, longExpression2, longExpression3, 
            longExpression4, longExpression5);
     
    var = someMethod1(longExpression1,
                    someMethod2(longExpression2,
                            longExpression3)); 
```
Following are two examples of breaking an arithmetic expression. The first is preferred, since the break occurs outside the parenthesized expression, which is at a higher level.
```
    longName1 = longName2 * (longName3 + longName4 - longName5)
               + 4 * longname6; // PREFER

    longName1 = longName2 * (longName3 + longName4
                           - longName5) + 4 * longname6; // AVOID 
```
Following are two examples of indenting method declarations. The first is the conventional case. The second would shift the second and third lines to the far right if it used conventional indentation, so instead it indents only 8 spaces.
```
    //CONVENTIONAL INDENTATION
    someMethod(int anArg, Object anotherArg, String yetAnotherArg,
               Object andStillAnother) {
        ...
    }

    //INDENT 8 SPACES TO AVOID VERY DEEP INDENTS
    private static synchronized horkingLongMethodName(int anArg,
            Object anotherArg, String yetAnotherArg,
            Object andStillAnother) {
        ...
    }
```
Line wrapping for if statements should generally use the 8-space rule, since conventional (4 space) indentation makes seeing the body difficult. For example:
```
    //DON'T USE THIS INDENTATION
    if ((condition1 && condition2)
        || (condition3 && condition4)
        ||!(condition5 && condition6)) { //BAD WRAPS
        doSomethingAboutIt();            //MAKE THIS LINE EASY TO MISS
    } 

    //USE THIS INDENTATION INSTEAD
    if ((condition1 && condition2)
            || (condition3 && condition4)
            ||!(condition5 && condition6)) {
        doSomethingAboutIt();
    } 

    //OR USE THIS
    if ((condition1 && condition2) || (condition3 && condition4)
            ||!(condition5 && condition6)) {
        doSomethingAboutIt();
    } 
```
Here are three acceptable ways to format ternary expressions:
```
    alpha = (aLongBooleanExpression) ? beta : gamma;  

    alpha = (aLongBooleanExpression) ? beta
                                     : gamma;  

    alpha = (aLongBooleanExpression)
            ? beta 
            : gamma;  
```

## 3 Comments ##

Java programs can have two kinds of comments: implementation comments and documentation comments. Implementation comments are those found in C++, which are delimited by /**...**/, and //. Documentation comments (known as "doc comments") are Java-only, and are delimited by /...**/. Doc comments can be extracted to HTML files using the javadoc tool.**

Implementation comments are meant for commenting out code or for comments about the particular implementation. Doc comments are meant to describe the specification of the code, from an implementation-free perspective. to be read by developers who might not necessarily have the source code at hand.

Comments should be used to give overviews of code and provide additional information that is not readily available in the code itself. Comments should contain only information that is relevant to reading and understanding the program. For example, information about how the corresponding package is built or in what directory it resides should not be included as a comment.

Discussion of nontrivial or nonobvious design decisions is appropriate, but avoid duplicating information that is present in (and clear from) the code. It is too easy for redundant comments to get out of date. In general, avoid any comments that are likely to get out of date as the code evolves.

Note:The frequency of comments sometimes reflects poor quality of code. When you feel compelled to add a comment, consider rewriting the code to make it clearer.

Comments should not be enclosed in large boxes drawn with asterisks or other characters.
Comments should never include special characters such as form-feed and backspace.

### 3.1 Implementation Comment Formats ###

Programs can have four styles of implementation comments: block, single-line, trailing, and end-of-line.
#### 3.1.1 Block Comments ####

Block comments are used to provide descriptions of files, methods, data structures and algorithms. Block comments may be used at the beginning of each file and before each method. They can also be used in other places, such as within methods. Block comments inside a function or method should be indented to the same level as the code they describe.

A block comment should be preceded by a blank line to set it apart from the rest of the code.
```
    /*
     * Here is a block comment.
     */
```
#### 3.1.2 Single-Line Comments ####

Short comments can appear on a single line indented to the level of the code that follows. If a comment can't be written in a single line, it should follow the block comment format (see section 5.1.1). A single-line comment should be preceded by a blank line. Here's an example of a single-line comment in Java code:
```
    if (condition) {

        /* Handle the condition. */
        ...
    }
```
#### 3.1.3 Trailing Comments ####

Very short comments can appear on the same line as the code they describe, but should be shifted far enough to separate them from the statements. If more than one short comment appears in a chunk of code, they should all be indented to the same tab setting.

Here's an example of a trailing comment in Java code:
```
    if (a == 2) {
        return TRUE;            /* special case */
    } else {
        return isPrime(a);      /* works only for odd a */
    }
```
#### 3.1.4 End-Of-Line Comments ####

The // comment delimiter can comment out a complete line or only a partial line. It shouldn't be used on consecutive multiple lines for text comments; however, it can be used in consecutive multiple lines for commenting out sections of code. Examples of all three styles follow:
```
    if (foo > 1) {

        // Do a double-flip.
        ...
    }
    else {
        return false;          // Explain why here.
    }
    //if (bar > 1) {
    //
    //    // Do a triple-flip.
    //    ...
    //}
    //else {
    //    return false;
    //}
```

## 4 - Declarations ##
### 4.1 Number Per Line ###

One declaration per line is recommended since it encourages commenting. In other words,
```
    int level; // indentation level
    int size;  // size of table
```
is preferred over
```
    int level, size;
```
Do not put different types on the same line. Example:
```
     
    		      int foo,  fooarray[]; //WRONG!
```
Note: The examples above use one space between the type and the identifier. Another acceptable alternative is to use tabs, e.g.:
```
    int	level;	        // indentation level
    int	size;	         // size of table
    Object	currentEntry;	 // currently selected table entry
```
### 4.2 Initialization ###

Try to initialize local variables where they're declared. The only reason not to initialize a variable where it's declared is if the initial value depends on some computation occurring first.
### 4.3 Placement ###

Put declarations only at the beginning of blocks. (A block is any code surrounded by curly braces "{" and "}".) Don't wait to declare variables until their first use; it can confuse the unwary programmer and hamper code portability within the scope.
```
    void myMethod() {
        int int1 = 0;         // beginning of method block

        if (condition) {
            int int2 = 0;     // beginning of "if" block
            ...
        }
    }
```
The one exception to the rule is indexes of for loops, which in Java can be declared in the for statement:
```
    for (int i = 0; i < maxLoops; i++) { ... }
```
Avoid local declarations that hide declarations at higher levels. For example, do not declare the same variable name in an inner block:
```
    int count;
    ...
    myMethod() {
        if (condition) {
            int count = 0;     // AVOID!
            ...
        }
        ...
    }
```
### 4.4 Class and Interface Declarations ###

When coding Java classes and interfaces, the following formatting rules should be followed:

  * No space between a method name and the parenthesis "(" starting its parameter list
  * Open brace "{" appears at the end of the same line as the declaration statement
  * Closing brace "}" starts a line by itself indented to match its corresponding opening statement, except when it is a null statement the "}" should appear immediately after the "{"
```
    class Sample extends Object {
        int ivar1;
        int ivar2;

        Sample(int i, int j) {
            ivar1 = i;
            ivar2 = j;
        }

        int emptyMethod() {}

        ...
    }
```
  * Methods are separated by a blank line
## 5 Statements ##

Usual stuff goes here.

## 6 Naming Conventions ##

Naming conventions make programs more understandable by making them easier to read. They can also give information about the function of the identifier-for example, whether it's a constant, package, or class-which can be helpful in understanding the code.



### 6.1 Packages ###


The prefix of a unique package name is always written in all-lowercase ASCII letters and should be one of the top-level domain names, currently com, edu, gov, mil, net, org, or one of the English two-letter codes identifying countries as specified in ISO Standard 3166, 1981.

Subsequent components of the package name vary according to an organization's own internal naming conventions. Such conventions might specify that certain directory name components be division, department, project, machine, or login names.

Example:
```
com.sun.eng

com.apple.quicktime.v2

edu.cmu.cs.bovik.cheese
```

### 6.2 Classes ###


Class names should be nouns, in mixed case with the first letter of each internal word capitalized. Try to keep your class names simple and descriptive. Use whole words-avoid acronyms and abbreviations (unless the abbreviation is much more widely used than the long form, such as URL or HTML).

Examples:
```
class Raster;
class ImageSprite;
```

### 6.3 Interfaces ###


Interface names should be capitalized like class names.

Example:
```
interface RasterDelegate;
interface Storing;
```

### 6.4 Methods ###

Methods should be verbs, in mixed case with the first letter lowercase, with the first letter of each internal word capitalized.

Example:
```
run();
runFast();
getBackground();
```

### 6.5 Variables ###


Except for variables, all instance, class, and class constants are in mixed case with a lowercase first letter. Internal words start with capital letters. Variable names should not start with underscore _or dollar sign $ characters, even though both are allowed._

Variable names should be short yet meaningful. The choice of a variable name should be mnemonic- that is, designed to indicate to the casual observer the intent of its use. One-character variable names should be avoided except for temporary "throwaway" variables. Common names for temporary variables are i, j, k, m, and n for integers; c, d, and e for characters.

Example:
```
int             i;
char            c;
float           myWidth;
```

### 6.5 Constants ###


The names of variables declared class constants and of ANSI constants should be all uppercase with words separated by underscores ("_"). (ANSI constants should be avoided, for ease of debugging.)_

Example:
```
static final int MIN_WIDTH = 4;

static final int MAX_WIDTH = 999;

static final int GET_THE_CPU = 1; 
```