import os
import subprocess
from os import listdir
import psutil

from os.path import isfile, join
p = psutil.Process(os.getpid())
p.nice(psutil.REALTIME_PRIORITY_CLASS)
# get all files in gn folder
data_path = "benchmark_gen/gml_files/benchmarks/gn/"
jarpath = "impl_GALS/out/artifacts/impl_GALS_jar/impl_GALS.jar"
cmd = "java -jar "

files = [f for f in listdir(data_path) if f.endswith(".gml")]
print files
for f in files:
	subprocess.call(cmd + jarpath + " -G=D:/alien-pineapple/"+ data_path + f + " -P=impl_GALS/default.properties -R=10", shell=True)
	subprocess.call(cmd + jarpath + " -G=D:/alien-pineapple/"+ data_path + f + " -P=impl_GALS/large.properties -R=10", shell=True)
# get all files in gn folder
data_path = "benchmark_gen/gml_files/real_networks/"
jarpath = "impl_GALS/out/artifacts/impl_GALS_jar/impl_GALS.jar"
cmd = "java -jar "

files = [f for f in listdir(data_path) if f.endswith(".gml")]
print files
for f in files:
	subprocess.call(cmd + jarpath + " -G=D:/alien-pineapple/"+ data_path + f + " -P=impl_GALS/small.properties -R=10", shell=True)
