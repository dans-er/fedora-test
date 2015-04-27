#!/bin/bash --


mkdir ../test-files2

# create 1000 files of 1Mb
no_of_files=1000;
counter=1; 
while [[ $counter -le $no_of_files ]]; 
 do echo Creating file no $counter;
  dd if=/dev/urandom bs=1024 count=1024 of=../test-files2/1Mb.$counter;
  let "counter += 1";
 done