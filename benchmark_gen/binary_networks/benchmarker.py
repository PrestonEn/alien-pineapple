"""
	Simple script for builiding batches of test data from simple settings text files.
"""
from igraph import Graph
import os
import subprocess


file = open("bench_settings.txt", "r")
FNULL = open(os.devnull, 'w')

for line in file:
	print line
	if not line.startswith('#'):
		params = line.split(',')
		if not os.path.exists(params[0]):
			os.makedirs(params[0])
		print line
		for i in xrange(int(params[2])):
		    filename = params[0].strip() + params[1].strip() + "_" + str(i) + ".gml"
		    subprocess.call(params[3], shell = True)
		    g = Graph.Read_Edgelist("network.dat", directed = False)
		    g.simplify()
		    # # write to gml
		    g.write_gml(filename)
		    # move communities.dat to filename.coms
		    os.rename("community.dat", filename + ".coms")