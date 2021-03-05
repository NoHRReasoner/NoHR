#!/bin/bash

echo " ----- LIPID BENCHMARK----- "

echo " ----- LIPID PRELOADING BENCHMARK----- "
sh ./LIPID_run_scripts/benchmarkPreloading

echo " ----- LIPID QUERYING BENCHMARK----- "
sh ./LIPID_run_scripts/benchmarkQueries

echo " ----- LIPID BENCHMARK DONE----- "


echo " ----- LUBM BENCHMARK----- "

echo " ----- LUBM PRELOADING BENCHMARK----- "
sh ./LUBM_run_scripts/benchmarkPreloading

echo " ----- LUBM QUERYING BENCHMARK----- "
sh ./LUBM_run_scripts/benchmarkQueries

echo " ----- LUBM NO MAPPING QUERYING BENCHMARK----- "
sh ./LUBM_run_scripts/benchmarkIndividualQueries

echo " ----- LUBM BENCHMARK DONE----- "

