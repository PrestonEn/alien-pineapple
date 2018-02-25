import os
import subprocess
from os import listdir
import psutil

from os.path import isfile, join
p = psutil.Process(os.getpid())
p.nice(psutil.REALTIME_PRIORITY_CLASS)

# get all files in gn folder
data_path = "benchmark_gen/gml_files/real_networks/"
jarpath = "impl_GACD/out/artifacts/impl_GACD_jar/impl_GACD.jar"
cmd = "java -jar "

files = [f for f in listdir(data_path) if f.endswith(".gml")]
for f in files:
	subprocess.call(cmd + jarpath + " -G=D:/alien-pineapple/"+ data_path + f + " -P=impl_GACD/default.properties -R=10", shell=True)
	subprocess.call(cmd + jarpath + " -G=D:/alien-pineapple/"+ data_path + f + " -P=impl_GACD/large.properties -R=10", shell=True)
	subprocess.call(cmd + jarpath + " -G=D:/alien-pineapple/"+ data_path + f + " -P=impl_GACD/low_mut_high_cross.properties -R=10", shell=True)
