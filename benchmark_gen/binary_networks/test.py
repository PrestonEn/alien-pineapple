from igraph import Graph
g = Graph.Read_Edgelist("network.dat", directed = False)
g.simplify()
# # write to gml
g.write_gml("outnet.gml")
# move communities.dat to filename.coms
