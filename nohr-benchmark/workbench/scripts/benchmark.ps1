param([int]$runs=5, [string]$queryFile="queries.txt", [string]$profile="QL", [bool]$forceDL=0, [string]$xmx="2g", [string]$xsb=$env:XSB_BIN_DIRECTORY,[int[]]$univs=@(1,5,10,15,20))

mkdir -Force ../output/report/queries/
mkdir -Force ../output/report/loading/
mkdir -Force ../output/result/$profile

$env:KONCLUDE_BIN="N/A"
$env:NOHR_RULES="../template/univ-benchQL.nohr"
$univlist = ""

foreach($i in $univs) {
    $univlist = $univlist + " " + $i
}

For ($i=0; $i -lt $runs; $i++) {
    Write-Host "Run" ($i + 1)

    if ($forceDL) {
        $command = "java -Xmx$xmx '-Djava.library.path=$xsb' -DentityExpansionLimit=100000000 -cp ../lib/* benchmark.LubmTest --force-dl --profile $profile --data-dir ../data --queries-file ../template/$queryFile --univs-list $univlist --output-dir ../output/result/$profile"        
    } else {
        $command = "java -Xmx$xmx '-Djava.library.path=$xsb' -DentityExpansionLimit=100000000 -cp ../lib/* benchmark.LubmTest --profile $profile --data-dir ../data --queries-file ../template/$queryFile --univs-list $univlist --output-dir ../output/result/$profile"
    }

    Invoke-Expression $command

    if ($forceDL) {
        Move-Item -Force queries.csv ../output/report/queries/$profile${i}-DL.csv
        Move-Item -Force loading.csv ../output/report/loading/$profile${i}-DL.csv
    } else { 
        Move-Item -Force queries.csv ../output/report/queries/$profile${i}.csv
        Move-Item -Force loading.csv ../output/report/loading/$profile${i}.csv
    }
}