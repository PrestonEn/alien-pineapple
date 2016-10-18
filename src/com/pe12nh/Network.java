package com.pe12nh;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Credit to adam balint for implementing all the parsing logic for gml files
 */
public class Network {
    private int nodeCount;
    private int edgeCount;
    private boolean[][] adjMat;
    File f;
    private HashMap<Integer, List<Integer>> adjList;

    private HashMap<Integer, String> nodeLabels;



    public Network(String path){
        adjList = new HashMap();
        nodeLabels = new HashMap();
        this.f = new File(path);
        generateNetworkInfo();
    }

    public void generateNetworkInfo(){
        Scanner in = null;

        try {
            in = new Scanner(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            System.err.println("Error opening file");
            e.printStackTrace();
        }

        while (in.hasNext()){
            String line = in.nextLine().trim();
            String[] parts = line.split(" ");
            if (parts.length == 1 && parts[0].equalsIgnoreCase("node")){
                processNode(in);
            }else if(parts.length == 1 && parts[0].equalsIgnoreCase("edge")){

                if (adjMat == null) {
                    adjMat = new boolean[nodeCount][nodeCount];
                }

                processEdge(in);
            }
        }
        System.err.println("Number of nodes: " + nodeCount);
        System.err.println("Number of edges: " + edgeCount);
    }

    // Iterates through input file and adds node to the adjacency matrix
    // also encodes the label into a hashmap
    private void processNode(Scanner in){
        nodeCount++;
        String[] tmp = in.nextLine().trim().split(" ");
        String id = "";
        String lab = "";
        while(!tmp[0].equals("]")){
            if (tmp[0].equals("id")){
                id = tmp[1];
            }else if (tmp[0].equals("label")){
                lab =  tmp[1].replaceAll("\"", "");
            }
            tmp = in.nextLine().trim().split(" ");
        }
        nodeLabels.put(Integer.parseInt(id), lab);
        System.out.printf("added (%s, %s) to the hashmap\n", id, lab);


    }

    // Iterates through the file and adds the edge to the adjacency matrix
    private void processEdge(Scanner in){
        edgeCount++;
        int source = -1;
        int dest = -1;
        String[] tmp = in.nextLine().trim().split(" ");
        while(!tmp[0].equals("]")){
            if (tmp[0].equals("source")){
                source = Integer.parseInt(tmp[1]);
            }else if (tmp[0].equals("target")){
                dest =  Integer.parseInt(tmp[1]);;
            }
            tmp = in.nextLine().trim().split(" ");
        }
        updateAdjMat(source,dest);
        updateAdjList(source,dest);

    }
    /**
     *
     * @param f
     * @param t
     */
    private void updateAdjList(int f, int t){
        if(!adjList.containsKey(f)){
            List<Integer> nbrs = new ArrayList<>(1);
            adjList.put(f, nbrs);
            adjList.get(f).add(t);
        }else{
            adjList.get(f).add(t);
        }

        if(!adjList.containsKey(t)){
            List<Integer> nbrs = new ArrayList<>(1);
            adjList.put(t, nbrs);
            adjList.get(t).add(f);
        }else{
            adjList.get(t).add(f);
        }
    }


    /**
     *
     * @param f
     * @param t
     */
    private void updateAdjMat(int f, int t){

        adjMat[f][t] = true;
        adjMat[t][f] = true;
    }

    /**
     *
     */
    public void printAdjList(){
        for (Integer name: adjList.keySet()){
            String key =name.toString();
            String value = adjList.get(name).toString();
            System.out.println(key + " " + value);
        }

    }

}


