EE422C Project 7 submission by
Varun Prabhu
vp6793
15465
Kelby Erickson
kde528
15495
Spring 2018

Our program takes a directory of text files and prints out a list indicating
how many n-word phrases any two files have in common. In order to use our
program, the user should give the path of the directory and the value n as
command line arguments. The user can include a third optional command line
argument indicating the exclusive minimum number of phrases that two files
should have in common in order for them to be displayed in the output list.
If this third argument is not given, all file pairs with at least one n-word
phrase in common will be displayed in the list.

Our program successfully detects most examples of word-for-word plagiarism
by indicating files with the highest number of n-word phrases in common.
Additionally, our program is time-efficient, producing meaningful output
for the large set of documents in less than 7 seconds. Our program has no
known bugs.