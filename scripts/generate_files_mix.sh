#!/bin/bash --

# from http://rajaseelan.com/2009/07/29/generate-files-with-random-content-and-size-in-bash/ with thanks
# + https://support.asperasoft.com/entries/20150617-How-to-generate-test-files

mkdir ../test-files

# create 10 files of 1Mb
no_of_files=10;
counter=1; 
while [[ $counter -le $no_of_files ]]; 
 do echo Creating file no $counter;
  dd if=/dev/urandom bs=1024 count=1024 of=../test-files/1Mb.$counter;
  let "counter += 1";
 done
 
 # create 9 files of 10Mb
no_of_files=9;
counter=1; 
while [[ $counter -le $no_of_files ]]; 
 do echo Creating file no $counter;
  dd if=/dev/urandom bs=1024 count=10240 of=../test-files/10Mb.$counter;
  let "counter += 1";
 done
 
  # create 9 files of 100Mb
no_of_files=9;
counter=1; 
while [[ $counter -le $no_of_files ]]; 
 do echo Creating file no $counter;
  dd if=/dev/urandom bs=1024 count=102400 of=../test-files/100Mb.$counter;
  let "counter += 1";
 done
 