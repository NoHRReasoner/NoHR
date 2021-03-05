# NoHR Lightweight Benchmark

NoHR Lightweight Benchmark allows the realization of pinpoint benchmarks on specific functionality for the NoHR Reasoner.

The folder "workbench" contains scripts for automated benchmarking of the NoHR reasoner using the LUBM and LIPI datasets. It is used to evaluate and compare resoning using NoHR 3.0 (where all the facts are provided as owl assertions or rules) and NoHR 4.0 (that supports database integration, in order to provide facts in a more optimal way).


## Setting up the environment


The folder `data setup` contains some useful information regarding the domain setup. It explains how generated_data was created and explains how to use the data. In addition, the folder `data setup` contains the `odbc.ini` that was used for the mappings provided in the evaluation. Thus in order to run the given examples the odbc connections need to match the `odbc.ini`.

## Running a benchmark

If building with Maven, running the `benchmark.sh` from the `target` directory can be achieved using the sample command:


```
cd ~/NoHR/nohr-benchmark-lightweight/target

sh ../workbench/benchmark

```


*Note: in windows, classpath is separated with `;` instead of `:`.*

