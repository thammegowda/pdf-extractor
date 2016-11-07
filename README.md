PDF Extract
============

This project contains a tool for extracting text from PDFs.


To Build this:

    mvn clean package

To Use this:

    java -jar target/*.jar filelist

 The input file should contain list of inputs and outputs in the following format:

    path/to/file1.pdf,path/to/file1.txt
    path/to/file2.pdf,path/to/file2.txt
    path/to/file3.pdf,path/to/file3.txt

