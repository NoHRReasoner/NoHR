#!/bin/bash

RUNS=$1
QUERYFILE=$2
PROFILE=$3
UNIVS="${@:4:$#}" 

echo $PROFILE

for ((i=1; i<=$RUNS; i++)); do
	echo "RUN ${i}"

	java -Xmx${XMX} -Djava.library.path=$XSB_BIN_DIRECTORY -DentityExpansionLimit=100000000 -jar ../bin/lubmTest.jar --profile $PROFILE --data-dir ../data --queries-file ${QUERYFILE}  --univs-list ${UNIVS} 2>&1 | tee logs/$PROFILE${i}.txt

	mv queries.csv ./report/queries/$PROFILE${i}.csv
	mv loading.csv ./report/loading/$PROFILE${i}.csv
done
