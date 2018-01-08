import os
import subprocess
from os import listdir
from os.path import isfile, join

# get all files in gn folder
data_path = "benchmark_gen/gml_files/benchmarks/lfr/n1000/"
jarpath = "impl_TasginGA/out/artifacts/impl_TasginGA_jar/impl_TasginGA.jar"
cmd = "java -jar "

files = [f for f in listdir(data_path) if f.endswith(".gml")]
print files
for f in files:
	subprocess.call(cmd + jarpath + " -G=D:/alien-pineapple/"+ data_path + f + " -P=impl_TasginGA/defaultProperties.prop -R=10")
