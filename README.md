LittleBasic
===========

A toy BASIC-like language implemented using [Antlr4](http://www.antlr.org/) and Java.

It's just an excuse to play with Antlr and language creation. It has most structured programming constructs 
while still being simple enough to understand for a newbie.

### Syntax

Check the grammar file `src/main/antlr4/basic/LittleBasic.g4` and the test files `src/test/resources/*.bas`

Example:
```PureBasic
REM Greatest common divisor
INPUT "A=" ain
INPUT "B=" bin
a = VAL(ain)
b = VAL(bin)

WHILE b > 0
    t = a MOD b
    a = b
    b = t
END

PRINT "GCD=" + a
```
Output:
```
A= 36
B= 27
GCD=9
```

### Building

It's built with Maven, so simply do this in the project directory:

```
mvn install
```
You'll find the jar in the `/target` directory.

### Running

In the console:

```
java -jar LittleBasic.jar /path/to/MyAwesomeProgram.bas
```


Enjoy!