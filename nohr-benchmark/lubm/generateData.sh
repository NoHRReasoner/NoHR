#!/bin/sh

source ../env

cd data
rm *.owl
${JAVA} -jar ./lubmGenerator.jar -univ 20 -seed 0 -onto ../univ-bench-ql.owl -prefix http://swat.cse.lehigh.edu/onto/univ-bench.owl
cd .. 
