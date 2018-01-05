from igraph import Graph
import os
import subprocess
# set working directory to output directory
# if its not there, make it

# input will look like
# file_out_path, base_name, number of instances, benchmark command
file = open("bench_settings.txt", "r")

for line in file:
	if not line.startswith('#'):
		params = line.split(',')
		if os.path.exists(params[0]):
			print params[0]
			print "yay"
		else:
			os.makedirs(params[0])

		for i in xrange(int(params[2])):
		    filename = params[0].strip() + params[1].strip() + "_" + str(i) + ".gml"
		    print filename
		    print "calling"
		    print params[3] + " trying to call"
		    subprocess.call(params[3], shell = True)
		    # g = Graph.Read_Edgelist("network.dat", directed = False)
		    # # # write to gml
		    # g.write_gml(filename)
		    # # move communities.dat to filename.coms
		    # os.rename("community.dat", filename + ".coms")