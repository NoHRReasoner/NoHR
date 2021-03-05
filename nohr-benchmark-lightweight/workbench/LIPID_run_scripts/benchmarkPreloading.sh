#!/bin/bash

#!/bin/bash

currpath="$( cd "$(dirname "$0")" ; pwd -P )"
currpath=${currpath}/..

java -Xms4g -Xmx15g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading "${currpath}/inputs/LIPID/LIPID Common,${currpath}/inputs/LIPID/LIPID 0,${currpath}/inputs/LIPID/LIPID 0/NoHR 4.0" "${currpath}/outputs/LIPID/Loading" "maplipid0" 5 false HERMIT ""
echo " ----- MAPPING LIPID 0 DONE----- "

java -Xms4g -Xmx15g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading "${currpath}/inputs/LIPID/LIPID Common,${currpath}/inputs/LIPID/LIPID 1,${currpath}/inputs/LIPID/LIPID 1/NoHR 4.0" "${currpath}/outputs/LIPID/Loading" "maplipid1" 5 false HERMIT ""
echo " ----- MAPPING LIPID 1 DONE----- "

java -Xms4g -Xmx15g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading "${currpath}/inputs/LIPID/LIPID Common,${currpath}/inputs/LIPID/LIPID 1,${currpath}/inputs/LIPID/LIPID 1/NoHR 4.0" "${currpath}/outputs/LIPID/Loading" "maplipid2" 5 false HERMIT ""
echo " ----- MAPPING LIPID 2 DONE----- "

java -Xms4g -Xmx15g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading "${currpath}/inputs/LIPID/LIPID 0/NoHR 3.0,${currpath}/inputs/LIPID/LIPID 0,${currpath}/inputs/LIPID/LIPID Common" "${currpath}/outputs/LIPID/Loading" "lipid0" 5 false HERMIT ""
echo " ----- LIPID 0 DONE----- "

java -Xms4g -Xmx15g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading "${currpath}/inputs/LIPID/LIPID 1/NoHR 3.0,${currpath}/inputs/LIPID/LIPID 1,${currpath}/inputs/LIPID/LIPID Common" "${currpath}/outputs/LIPID/Loading" "lipid1" 5 false HERMIT ""
echo " ----- LIPID 1 DONE----- "


java -Xms4g -Xmx15g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading "${currpath}/inputs/LIPID/LIPID 2/NoHR 3.0,${currpath}/inputs/LIPID/LIPID 2,${currpath}/inputs/LIPID/LIPID Common" "${currpath}/outputs/LIPID/Loading" "lipid2" 5 false HERMIT ""
echo " ----- LIPID 2 DONE----- "

java -Xms4g -Xmx15g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading "${currpath}/inputs/LIPID/LIPID 3/NoHR 3.0,${currpath}/inputs/LIPID/LIPID 3,${currpath}/inputs/LIPID/LIPID Common" "${currpath}/outputs/LIPID/Loading" "lipid3" 5 false HERMIT ""
echo " ----- LIPID 3 DONE----- "


java -Xms4g -Xmx15g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading "${currpath}/inputs/LIPID/LIPID 4/NoHR 3.0,${currpath}/inputs/LIPID/LIPID 4,${currpath}/inputs/LIPID/LIPID Common" "${currpath}/outputs/LIPID/Loading" "lipid4" 5 false HERMIT ""
echo " ----- LIPID 4 DONE----- "


java -Xms4g -Xmx15g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading "${currpath}/inputs/LIPID/LIPID 5/NoHR 3.0,${currpath}/inputs/LIPID/LIPID 5,${currpath}/inputs/LIPID/LIPID Common" "${currpath}/outputs/LIPID/Loading" "lipid5" 5 false HERMIT ""
echo " ----- LIPID 5 DONE----- "


java -Xms4g -Xmx15g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading "${currpath}/inputs/LIPID/LIPID 6/NoHR 3.0,${currpath}/inputs/LIPID/LIPID 6,${currpath}/inputs/LIPID/LIPID Common" "${currpath}/outputs/LIPID/Loading" "lipid6" 5 false HERMIT ""
echo " ----- LIPID 6 DONE----- "


java -Xms4g -Xmx15g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading "${currpath}/inputs/LIPID/LIPID 7/NoHR 3.0,${currpath}/inputs/LIPID/LIPID 7,${currpath}/inputs/LIPID/LIPID Common" "${currpath}/outputs/LIPID/Loading" "lipid7" 5 false HERMIT ""
echo " ----- LIPID 7 DONE----- "


java -Xms4g -Xmx15g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading "${currpath}/inputs/LIPID/LIPID 8/NoHR 3.0,${currpath}/inputs/LIPID/LIPID 8,${currpath}/inputs/LIPID/LIPID Common" "${currpath}/outputs/LIPID/Loading" "lipid8" 5 false HERMIT ""
echo " ----- LIPID 8 DONE----- "


java -Xms4g -Xmx15g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading "${currpath}/inputs/LIPID/LIPID 9/NoHR 3.0,${currpath}/inputs/LIPID/LIPID 9,${currpath}/inputs/LIPID/LIPID Common" "${currpath}/outputs/LIPID/Loading" "lipid9" 5 false HERMIT ""
echo " ----- LIPID 9 DONE----- "



