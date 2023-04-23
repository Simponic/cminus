#!/bin/zsh

# usage: ./test.sh <test-number>

javac -d classes -cp classes -cp *.jar **/*.java && java -cp "classes:antlr-4.9.1-complete.jar" main.Main data/test$1.c

java -jar data/examples/Mars4_5.jar data/test$1.asm
