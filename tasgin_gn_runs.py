import os
import subprocess
from os import listdir
from os.path import isfile, join
import psutil

p = psutil.Process(os.getpid())
p.nice(psutil.REALTIME_PRIORITY_CLASS)
# get all files in gn folder
data_path = "benchmark_gen/gml_files/benchmarks/gn_kout/"
jarpath = "impl_TasginGA/out/artifacts/impl_TasginGA_jar/impl_TasginGA.jar"
cmd = "java -jar "

files = [f for f in listdir(data_path) if f.endswith(".gml")]
print files
for f in files:
	print f
	subprocess.call(cmd + jarpath + " -G=D:/alien-pineapple/"+ data_path + f + " -P=impl_TasginGA/default.properties -R=10", shell=True)
	subprocess.call(cmd + jarpath + " -G=D:/alien-pineapple/"+ data_path + f + " -P=impl_TasginGA/large.properties -R=10", shell=True)
	subprocess.call(cmd + jarpath + " -G=D:/alien-pineapple/"+ data_path + f + " -P=impl_TasginGA/high_mut.properties -R=10", shell=True)
	subprocess.call(cmd + jarpath + " -G=D:/alien-pineapple/"+ data_path + f + " -P=impl_TasginGA/high_init.properties -R=10", shell=True)
	subprocess.call(cmd + jarpath + " -G=D:/alien-pineapple/"+ data_path + f + " -P=impl_TasginGA/high_elite.properties -R=10", shell=True)
