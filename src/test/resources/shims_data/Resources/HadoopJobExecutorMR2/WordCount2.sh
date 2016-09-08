#!/bin/bash
INPUTFILE=/tmp/wordcount2-input.txt
OUTPUTDIR=/tmp/wordcount2-output/

# Write numbers 1 through 10, 10 times each to validate the WordCount application
for i in {1..10}; do for j in {1..10}; do echo $i; done; done > $INPUTFILE
hadoop fs -rm -r $INPUTFILE &> /dev/null; hadoop fs -put $INPUTFILE $INPUTFILE
hadoop fs -rm -r $OUTPUTDIR &> /dev/null; hadoop --config /etc/hadoop/conf jar /root/WordCount2.jar $INPUTFILE $OUTPUTDIR

hadoop fs -cat $OUTPUTDIR/part-r-00000