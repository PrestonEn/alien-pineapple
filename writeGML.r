library("igraph")

args <- commandArgs(TRUE)
infile <- args[1]
outfile <- args[2]

edgelist <- as.matrix(read.csv(infile, sep = '\t'))

g <- graph_from_edgelist(edgelist, directed = F)
g <- simplify(g)
plot.igraph(g)

write_graph(g, outfile, format = "gml")