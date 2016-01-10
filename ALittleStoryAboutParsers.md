# Introduction #

A little story about parsers and libs


# Details #

The parsers for this project are being loaded dynamically from a specified folder.
When a class is loaded the server calls one of the classes methods to obtain a list of file extensions that the class can handle.

Once a new file is added in the parse queue ( TODO :) ) the server looks at the extension of the file and calls the appropiate parser.
If there is no parser for that file the text parser will be used.

All parsers have a basic functionality that is identical.
They will create the following tags for the file that is being parsed:
filename,last-modified,extension,path.
They will also set the tags that the user specifies for that particular file.


## MP3 Parser ##
The mp3 parser uses the [JAudioTagger library](https://jaudiotagger.dev.java.net/). It tests if the file has ID3V2 tags and preffers them to the ID3V1 but it can work with both.
The library can be also used to parse other types of audio files.

The parser will be used to index the following : artist,title,album,year,genre.

## PDF Parser ##

The pdf parser uses [PDFBox library](http://www.pdfbox.org/).
The parser will be used to index the following: author,subject,title,creator,content.

## OO parser ##
This one is just a little bit more complex so ...we'll be talking about it later.
The basic point is that it reads both the content and the metadata of the file.
## Text parser ##
This is pretty straightforward. It parses the content and that's all :D.