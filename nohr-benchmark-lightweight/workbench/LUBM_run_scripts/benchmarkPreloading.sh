#!/bin/bash

currpath="$( cd "$(dirname "$0")" ; pwd -P )"
currpath=${currpath}/..

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_1/nohr4,${currpath}/inputs/LUBM EL" "${currpath}/outputs/LUBM/Loading" "maplubm1EL" 5 false HERMIT
echo " ----- MAPPING LUBM 1 EL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_5/nohr4,${currpath}/inputs/LUBM EL" "${currpath}/outputs/LUBM/Loading" "maplubm5EL" 5 false HERMIT
echo " ----- MAPPING LUBM 5 EL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_10/nohr4,${currpath}/inputs/LUBM EL" "${currpath}/outputs/LUBM/Loading" "maplubm10EL" 5 false HERMIT
echo " ----- MAPPING LUBM 10 EL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_15/nohr4,${currpath}/inputs/LUBM EL" "${currpath}/outputs/LUBM/Loading" "maplubm15EL" 5 false HERMIT
echo " ----- MAPPING LUBM 15 EL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_20/nohr4,${currpath}/inputs/LUBM EL" "${currpath}/outputs/LUBM/Loading" "maplubm20EL" 5 false HERMIT
echo " ----- MAPPING LUBM 20 EL DONE----- "

echo " ---------- MAPPING LUBM EL DONE ---------- "

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_1/nohr4,${currpath}/inputs/LUBM QL" "${currpath}/outputs/LUBM/Loading" "maplubm1QL" 5 false HERMIT
echo " ----- MAPPING LUBM 1 QL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_5/nohr4,${currpath}/inputs/LUBM QL" "${currpath}/outputs/LUBM/Loading" "maplubm5QL" 5 false HERMIT
echo " ----- MAPPING LUBM 5 QL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_10/nohr4,${currpath}/inputs/LUBM QL" "${currpath}/outputs/LUBM/Loading" "maplubm10QL" 5 false HERMIT
echo " ----- MAPPING LUBM 10 QL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_15/nohr4,${currpath}/inputs/LUBM QL" "${currpath}/outputs/LUBM/Loading" "maplubm15QL" 5 false HERMIT
echo " ----- MAPPING LUBM 15 QL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_20/nohr4,${currpath}/inputs/LUBM QL" "${currpath}/outputs/LUBM/Loading" "maplubm20QL" 5 false HERMIT
echo " ----- MAPPING LUBM 20 QL DONE----- "

echo " ---------- MAPPING LUBM QL DONE ---------- "

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_1/nohr4,${currpath}/inputs/LUBM FULL" "${currpath}/outputs/LUBM/Loading" "maplubm1FULL" 5 false HERMIT
echo " ----- MAPPING LUBM 1 FULL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_5/nohr4,${currpath}/inputs/LUBM FULL" "${currpath}/outputs/LUBM/Loading" "maplubm5FULL" 5 false HERMIT
echo " ----- MAPPING LUBM 5 FULL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_10/nohr4,${currpath}/inputs/LUBM FULL" "${currpath}/outputs/LUBM/Loading" "maplubm10FULL" 5 false HERMIT
echo " ----- MAPPING LUBM 10 FULL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_15/nohr4,${currpath}/inputs/LUBM FULL" "${currpath}/outputs/LUBM/Loading" "maplubm15FULL" 5 false HERMIT
echo " ----- MAPPING LUBM 15 FULL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_20/nohr4,${currpath}/inputs/LUBM FULL" "${currpath}/outputs/LUBM/Loading" "maplubm20FULL" 5 false HERMIT
echo " ----- MAPPING LUBM 20 FULL DONE----- "

echo " ---------- MAPPING LUBM FULL DONE ---------- "


java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_1/nohr3,${currpath}/inputs/LUBM EL" "${currpath}/outputs/LUBM/Loading" "lubm1EL" 5 false HERMIT "${currpath}/inputs/LUBM EL/owl"
echo " ----- LUBM 1 EL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_5/nohr3,${currpath}/inputs/LUBM EL" "${currpath}/outputs/LUBM/Loading" "lubm5EL" 5 false HERMIT "${currpath}/inputs/LUBM EL/owl"
echo " ----- LUBM 5 EL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_10/nohr3,${currpath}/inputs/LUBM EL" "${currpath}/outputs/LUBM/Loading" "lubm10EL" 5 false HERMIT "${currpath}/inputs/LUBM EL/owl"
echo " ----- LUBM 10 EL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_15/nohr3,${currpath}/inputs/LUBM EL" "${currpath}/outputs/LUBM/Loading" "lubm15EL" 5 false HERMIT "${currpath}/inputs/LUBM EL/owl"
echo " ----- LUBM 15 EL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_20/nohr3,${currpath}/inputs/LUBM EL" "${currpath}/outputs/LUBM/Loading" "lubm20EL" 5 false HERMIT "${currpath}/inputs/LUBM EL/owl"
echo " ----- LUBM 20 EL DONE----- "

echo " ---------- LUBM EL DONE ---------- "

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_1/nohr3,${currpath}/inputs/LUBM QL" "${currpath}/outputs/LUBM/Loading" "lubm1QL" 5 false HERMIT "${currpath}/inputs/LUBM QL/owl"
echo " ----- LUBM 1 QL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_5/nohr3,${currpath}/inputs/LUBM QL" "${currpath}/outputs/LUBM/Loading" "lubm5QL" 5 false HERMIT "${currpath}/inputs/LUBM QL/owl"
echo " ----- LUBM 5 QL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_10/nohr3,${currpath}/inputs/LUBM QL" "${currpath}/outputs/LUBM/Loading" "lubm10QL" 5 false HERMIT "${currpath}/inputs/LUBM QL/owl"
echo " ----- LUBM 10 QL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_15/nohr3,${currpath}/inputs/LUBM QL" "${currpath}/outputs/LUBM/Loading" "lubm15QL" 5 false HERMIT "${currpath}/inputs/LUBM QL/owl"
echo " ----- LUBM 15 QL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_20/nohr3,${currpath}/inputs/LUBM QL" "${currpath}/outputs/LUBM/Loading" "lubm20QL" 5 false HERMIT "${currpath}/inputs/LUBM QL/owl"
echo " ----- LUBM 20 QL DONE----- "

echo " ---------- LUBM QL DONE ---------- "

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_1/nohr3,${currpath}/inputs/LUBM FULL" "${currpath}/outputs/LUBM/Loading" "lubm1FULL" 5 false HERMIT "${currpath}/inputs/LUBM FULL/owl"
echo " ----- LUBM 1 FULL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_5/nohr3,${currpath}/inputs/LUBM FULL" "${currpath}/outputs/LUBM/Loading" "lubm5FULL" 5 false HERMIT "${currpath}/inputs/LUBM FULL/owl"
echo " ----- LUBM 5 FULL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_10/nohr3,${currpath}/inputs/LUBM FULL" "${currpath}/outputs/LUBM/Loading" "lubm10FULL" 5 false HERMIT "${currpath}/inputs/LUBM FULL/owl"
echo " ----- LUBM 10 FULL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_15/nohr3,${currpath}/inputs/LUBM FULL" "${currpath}/outputs/LUBM/Loading" "lubm15FULL" 5 false HERMIT "${currpath}/inputs/LUBM FULL/owl"
echo " ----- LUBM 15 FULL DONE----- "
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "${currpath}/inputs/LUBM_20/nohr3,${currpath}/inputs/LUBM FULL" "${currpath}/outputs/LUBM/Loading" "lubm20FULL" 5 false HERMIT "${currpath}/inputs/LUBM FULL/owl"
echo " ----- LUBM 20 FULL DONE----- "

echo " ---------- LUBM FULL DONE ---------- "

echo " ---------- LOADING DONE ---------- "

