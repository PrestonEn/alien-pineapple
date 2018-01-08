import os
import subprocess
from os import listdir
from os.path import isfile, join

# get all files in gn folder
data_path = "benchmark_gen/gml_files/benchmarks/gn/"
jarpath = "impl_GANET/out/artifacts/gaNet_jar/gaNet.jar"
cmd = "java -jar "

files = [f for f in listdir(data_path) if f.endswith(".gml")]
print files
for f in files:
	print f
	subprocess.call(cmd + jarpath + " -G=D:/alien-pineapple/"+ data_path + f + " -P=impl_GANET/default.properties -R=10", shell=True)
