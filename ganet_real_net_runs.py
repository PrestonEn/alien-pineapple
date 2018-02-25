import os
import subprocess
from os import listdir
from os.path import isfile, join
import psutil

p = psutil.Process(os.getpid())
p.nice(psutil.REALTIME_PRIORITY_CLASS)

# get all files in gn folder
data_path = "benchmark_gen/gml_files/real_networks/"
jarpath = "impl_GANET/out/artifacts/gaNet_jar/gaNet.jar"
cmd = "java -jar "

files = [f for f in listdir(data_path) if f.endswith(".gml")]
print files
for f in files:
	subprocess.call(cmd + jarpath + " -G=D:/alien-pineapple/"+ data_path + f + " -P=impl_GANET/default.properties -R=10", shell=True)
	subprocess.call(cmd + jarpath + " -G=D:/alien-pineapple/"+ data_path + f + " -P=impl_GANET/large.properties -R=10", shell=True)