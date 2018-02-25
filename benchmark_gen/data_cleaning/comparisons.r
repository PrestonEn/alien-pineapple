library(igraph)
library(RSQLite)
library(plyr)



connection <- dbConnect(SQLite(), "D:\\alien-pineapple\\ClusterResults.db")
results <- as.data.frame(dbGetQuery(connection, "SELECT * from ClusterResults where file not like '%dol%' and file not like '%neur%';"))


doCompare <- function(row){
    clsts <- read.csv(paste(row$file,".coms", sep=""), sep="\t", header=F)    
    clsts <- as.integer(clsts$V2)
    res <- as.integer(unlist(strsplit(row$membership,",")))
    row$nmi <- compare(clsts, res, method = c("nmi"))
    row$rand <- compare(clsts, res, method = c("adjusted.rand"))
    row$vi <- compare(clsts, res, method = c("vi"))
    return(row)
}

outs <- adply(results, 1, doCompare)

