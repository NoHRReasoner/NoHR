#!/bin/bash

cd pellet
bash test.sh
cd ..

cd lubm
bash generateData.sh
bash test.sh
cd ..
