#!/bin/bash --


mkdir ../test-files6

# create 2000 files of 500Kb
no_of_files=2000;
counter=1; 
while [[ $counter -le $no_of_files ]]; 
 do echo Creating file no $counter;
  dd if=/dev/urandom bs=1024 count=512 of=../test-files6/500Kb.$counter;
  let "counter += 1";
 done