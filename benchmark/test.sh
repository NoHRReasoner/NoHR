#!/bin/sh

cd pellet
sh test.sh
cd ..

cd lubm
sh generateData.sh
sh test.sh
cd ..