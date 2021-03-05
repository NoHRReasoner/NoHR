# NoHR Lightweight Benchmark

NoHR Lightweight Benchmark allows the realization of pinpoint benchmarks on specific functionality for the NoHR Reasoner.

The folder "workbench" contains scripts for automated benchmarking of the NoHR reasoner using the LUBM and LIPI datasets. It is used to evaluate and compare resoning using NoHR 3.0 (where all the facts are provided as owl assertions or rules) and NoHR 4.0 (that supports database integration, in order to provide facts in a more optimal way).

## Requirements

Requires a java environment (1.8^), XSB and Konclude. 

The environment variables `NOHR_XSB_DIRECTORY`, `NOHR_KONCLUDE_BINARY` and `ODBCINI` must be set and point to the XSB installation directory, to the Konclude binary and to the odbc.ini file respectively.

## Running a benchmark

To run a benchmark you must call `java` with the correct parameters. The overall syntax is:

```
java <benchmark-class> <input-directories> <output-directory> <label> <repetition> <force-dl> <reasoner> <?owl-schema-path>
```

The parameters are mandatory and can have the following values.

### `<benchmark-class>`

The `Benchmark` class that contains the logic for the benchmark. It can be one of the following values:

- `pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading` will run the benchmark for KB loading.
- `pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkQueries` will run the benchmark for iterative querying. 
- `pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkIndividualQueries` will run the benchmark for querying, while loading a new KB each time. 

### `<input-directories>`

The path to directories (separated by commas) containing all the files to be loaded into the benchmark in the following manner:

- `*.owl` files will be loaded as ontologies
- `*.nohr` files will be loaded as programs
- `*.map` files will be loaded as database mappings
- `*.q` files will contain queries for benchmarking with the format `<query-name> <query>`

### `<output-directory>`

The directory where all the output files will be generated.

### `<label>`

Label used to distinguish the benchmark when scripting the tests.

### `<repetition>`

The number of times to repeat the test in each iteration.

### `<force-dl>`

Indicates whether to force the use of the DL reasoner instead of the OWL 2 Profile specific reasoners/techniques. Can be set to `true` or `false`, 

### `<reasoner>`

Selects the DL reasoner to use in the benchmark. Can be set to `HERMIT` or `KONCLUDE`.

### `<owl-schema-path>` - optional

This is an optional parameter and is rarely used. It represent the path to the directory that contains the schemas of the owl files. It used when we need to load the owl schema files before we load the corresponding owl files that contain the facts.  

## Example 1

If building with Maven, running the benchmark from the `target` directory can be achieved using the sample command:

```
java -Xms4g -Xmx4g -cp "./lib/*:./nohr-benchmark-lightweight-4.0.0.jar" 
pt.unl.fct.di.novalincs.nohr.benchmark.lightweight.BenchmarkLoading  "~/inputs1,~/inputs2" "~/outputs" "testBenchmark" 10 false HERMIT
```

## Example 2

To run the prederined LIPID and LUBM banchmarking scripts from the`target` directory can be achieved using the sample command:

```
../workbench/LIPID_run_scripts/benchmarkPreloading.sh
```
NOTE: It is neccessary to run the script from a directory that contains the `nohr-benchmark-lightweight-4.0.0.jar`.

## Example 3

In order to run the 

*Note: in windows, classpath is separated with `;` instead of `:`.*

*Note: set java memory requirements to your needs.*
