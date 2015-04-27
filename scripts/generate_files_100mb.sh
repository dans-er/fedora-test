#!/bin/bash --


mkdir ../test-files4

  # create 10 files of 100Mb
no_of_files=10;
counter=1; 
while [[ $counter -le $no_of_files ]]; 
 do echo Creating file no $counter;
  dd if=/dev/urandom bs=1024 count=102400 of=../test-files4/100Mb.$counter;
  let "counter += 1";
 done