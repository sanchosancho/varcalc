```shell
$ mvn install
$ java -jar ./target/varcalc-1.0-SNAPSHOT-jar-with-dependencies.jar -h
Usage: varcalc [-hV] [-f=<scriptPath>] [-n=<numThreads>] [-o=<outputPath>]
  -f, --file=<scriptPath>   Path to file with script. If not set, stdin will be
                              used to read script line by line.
  -h, --help                Show this help message and exit.
  -n, --num-threads=<numThreads>
                            Maximum number of threads to use for parallel
                              MapReduce computations. Value of 1 forces single
                              threaded computation.
  -o, --output=<outputPath> Path to write script output.
  -V, --version             Print version information and exit.
```