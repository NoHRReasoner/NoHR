#!/bin/bash

#!/bin/bash

currpath="$( cd "$(dirname "$0")" ; pwd -P )"
currpath=${currpath}/..

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkQueries  "${currpath}/inputs/LUBM_1/nohr4,${currpath}/inputs/LUBM EL" "${currpath}/outputs/LUBM/Querying" "maplubm1EL" 5 false HERMIT
echo " ----- MAPPING LUBM 1 EL DONE----- "

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkQueries  "${currpath}/inputs/LUBM_1/nohr4,${currpath}/inputs/LUBM QL" "${currpath}/outputs/LUBM/Querying" "maplubm1QL" 5 false HERMIT
echo " ----- MAPPING LUBM 1 QL DONE----- "

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkQueries  "${currpath}/inputs/LUBM_1/nohr4,${currpath}/inputs/LUBM FULL" "${currpath}/outputs/LUBM/Querying" "maplubm1FULL" 5 false HERMIT
echo " ----- MAPPING LUBM 1 FULL DONE----- "

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkQueries  "${currpath}/inputs/LUBM_1/nohr3,${currpath}/inputs/LUBM EL" "${currpath}/outputs/LUBM/Querying" "lubm1EL" 5 false HERMIT "${currpath}/inputs/LUBM EL/owl"
echo " ----- LUBM 1 EL DONE----- "

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkQueries  "${currpath}/inputs/LUBM_1/nohr3,${currpath}/inputs/LUBM QL" "${currpath}/outputs/LUBM/Querying" "lubm1QL" 5 false HERMIT "${currpath}/inputs/LUBM QL/owl"
echo " ----- LUBM 1 QL DONE----- "

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkQueries  "${currpath}/inputs/LUBM_1/nohr3,${currpath}/inputs/LUBM FULL" "${currpath}/outputs/LUBM/Querying" "lubm1FULL" 5 false HERMIT "${currpath}/inputs/LUBM FULL/owl"
echo " ----- LUBM 1 FULL DONE----- "

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkQueries  "${currpath}/inputs/LUBM_5/nohr4,${currpath}/inputs/LUBM EL" "${currpath}/outputs/LUBM/Querying" "maplubm5EL" 5 false HERMIT
echo " ----- MAPPING LUBM 5 EL DONE----- "

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkQueries  "${currpath}/inputs/LUBM_5/nohr4,${currpath}/inputs/LUBM QL" "${currpath}/outputs/LUBM/Querying" "maplubm5QL" 5 false HERMIT
echo " ----- MAPPING LUBM 5 QL DONE----- "

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkQueries  "${currpath}/inputs/LUBM_5/nohr4,${currpath}/inputs/LUBM FULL" "${currpath}/outputs/LUBM/Querying" "maplubm5FULL" 5 false HERMIT
echo " ----- MAPPING LUBM 5 FULL DONE----- "

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkQueries  "${currpath}/inputs/LUBM_5/nohr3,${currpath}/inputs/LUBM EL" "${currpath}/outputs/LUBM/Querying" "lubm5EL" 5 false HERMIT "${currpath}/inputs/LUBM EL/owl"
echo " ----- LUBM 5 EL DONE----- "

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkQueries  "${currpath}/inputs/LUBM_5/nohr3,${currpath}/inputs/LUBM QL" "${currpath}/outputs/LUBM/Querying" "lubm5QL" 5 false HERMIT "${currpath}/inputs/LUBM QL/owl"
echo " ----- LUBM 5 QL DONE----- "

java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkQueries  "${currpath}/inputs/LUBM_5/nohr3,${currpath}/inputs/LUBM FULL" "${currpath}/outputs/LUBM/Querying" "lubm5FULL" 5 false HERMIT "${currpath}/inputs/LUBM FULL/owl"
echo " ----- LUBM 5 FULL DONE----- "
