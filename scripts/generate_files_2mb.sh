#!/bin/bash --


mkdir ../test-files5

# create 500 files of 2Mb
no_of_files=500;
counter=1; 
while [[ $counter -le $no_of_files ]]; 
 do echo Creating file no $counter;
  dd if=/dev/urandom bs=1024 count=2048 of=../test-files5/2Mb.$counter;
  let "counter += 1";
 done