package brock.pe12nh;

/**
 * link node ids to partitions for ease of selection
 */
public class PartitionLookup {

    Partition[] lookup;

    public PartitionLookup(Population p){
        lookup = new Partition[Main.g.adjMat.length];
        for (Partition i:
             p.pop) {
            for (int node:
                 i.membership) {
            lookup[node] = i;
            }
        }
    }

    public void updatePartition(Partition p){
        for (int i:
             p.membership) {
            lookup[i] = p;
        }
    }

    public Partition getPartition(int nodeId){
        return lookup[nodeId];
    }
}
