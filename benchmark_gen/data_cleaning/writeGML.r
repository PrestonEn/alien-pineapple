library("igraph")

args <- commandArgs(TRUE)
infile <- args[1]
outfile <- args[2]

edgelist <- read.csv(infile, sep = '\t')
g <- graph_from_data_frame(edgelist, directed = F)
g <- simplify(g)
write_graph(g, outfile, format = "gml")