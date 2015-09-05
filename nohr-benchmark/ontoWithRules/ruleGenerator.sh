#!/bin/sh

#for more options see the help running:
#sh ../.runclass benchmark.data.ProgramGenerator --help

sh ../.runclass "benchmark.data.ProgramGenerator" "--ontology ./lipid.fs.owl --output lipid/ --rules 100 --facts 1000 --facts-step 1000 --rules-step 0 --constants 1000 --vars-ratio 1 --vars-rep-ratio 0"
