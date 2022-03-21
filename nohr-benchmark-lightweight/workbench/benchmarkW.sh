#!/bin/bash

echo " ----- LIPID BENCHMARK----- "

echo " ----- LIPID PRELOADING BENCHMARK----- "
sh ../workbench/LIPID_run_scripts/benchmarkPreloadingW.sh

echo " ----- LIPID QUERYING BENCHMARK----- "
sh ../workbench/LIPID_run_scripts/benchmarkQueriesW.sh

echo " ----- LIPID BENCHMARK DONE----- "


#echo " ----- LUBM BENCHMARK----- "

#echo " ----- LUBM PRELOADING BENCHMARK----- "
#sh ./LUBM_run_scripts/benchmarkPreloading.sh

#echo " ----- LUBM QUERYING BENCHMARK----- "
#sh ./LUBM_run_scripts/benchmarkQueries.sh

#echo " ----- LUBM NO MAPPING QUERYING BENCHMARK----- "
#sh ./LUBM_run_scripts/benchmarkIndividualQueries.sh

#echo " ----- LUBM BENCHMARK DONE----- "

