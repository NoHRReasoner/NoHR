param([string]$ontology=$env:NOHR_BENCHMARK_ONTOLOGY)

Set-Location ../data
Remove-Item *.owl

java -jar ../lib/lubmGenerator.jar -univ 20 -seed 0 -onto ../template/$ontology -prefix http://swat.cse.lehigh.edu/onto/univ-bench.owl

Set-Location ../scripts