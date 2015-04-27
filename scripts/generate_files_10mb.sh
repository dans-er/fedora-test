#!/bin/bash --


mkdir ../test-files3

# create 100 files of 10Mb
no_of_files=1000;
counter=1; 
while [[ $counter -le $no_of_files ]]; 
 do echo Creating file no $counter;
  dd if=/dev/urandom bs=1024 count=10240 of=../test-files3/10Mb.$counter;
  let "counter += 1";
 done