#!/bin/sh

cd pellet
sh run.sh
cd ..

cd lubm
sh generateData.sh
sh run.sh
cd ..
