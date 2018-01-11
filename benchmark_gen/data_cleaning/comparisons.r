library(igraph)
library(RSQLite)
library(plyr)



connection <- dbConnect(SQLite(), "D:\\alien-pineapple\\ClusterResults.db")
results <- as.data.frame(dbGetQuery(connection, "SELECT * from ClusterResultsRedux where file='D:/alien-pineapple/benchmark_gen/gml_files/benchmarks/lfr2/n1000/lfr_1000_mu2_s_3.gml';"))
records <- results[1:10,]

doCompare <- function(row){
    clsts <- read.csv(paste(row$file,".coms", sep=""), sep="\t", header=F)    
    clsts <- as.integer(clsts$V2)
    res <- as.integer(unlist(strsplit(row$membership,",")))
    row$nmi <- compare(clsts, res, method = c("nmi"))
    row$rand <- compare(clsts, res, method = c("rand"))
    row$vi <- compare(clsts, res, method = c("vi"))
    return(row)
}


outs <- adply(results, 1, doCompare)

tasgin <- outs$nmi[1:5]
ganet <- outs$nmi[6:10]