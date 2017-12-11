package com.brock.pe12nh.TasginGA;

import com.brock.pe12nh.AdjGraph.AdjGraph;
import com.sun.org.apache.xpath.internal.operations.Number;
import org.apache.commons.cli.*;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Main {

    /* TODO Individual cleanup method
     * TODO argparse
     * TODO sqlLite
     */
    private static Options options;
    public static Random randgen;
    public static int popSize;
    public static int generations;
    public static double mutRate;
    public static double initRate;
    public static double elitePortion;
    public static double cleanRate;
    public static double cleanThold;
    public static double cleanPortion;
    public static String batch;
    public static String gmlPath;
    public static String outName;
    public static long seed;

    public static void main(String args[]) throws IOException, ParseException,
                                                            ClassNotFoundException, SQLException {
        // Set up session variables and sql connection
        CommandLineParser parser = new DefaultParser();
        options = new Options();

        // create the Options
        Option generationsOpt = Option.builder("g").hasArg(true).longOpt("gens").required(false)
                .desc("generations to run").type(Number.class).build();

        Option popSizeOpt = Option.builder("p").hasArg().longOpt("popsize").required(false)
                .desc("population size").type(Integer.class).build();

        Option elitismPortionOpt = Option.builder("e").hasArg().longOpt("elite").required(false)
                .desc("portion to keep of best individuals").type(Double.class).build();

        Option mutateOpt = Option.builder("m").hasArg().longOpt("mut").required(false)
                .desc("mutation rate").type(Double.class).build();

        Option initBiasOpt = Option.builder("i").hasArg().longOpt("initbias").required(false)
                .desc("initialization bias").type(Double.class).build();

        Option graphPathOpt = Option.builder("G").hasArg().longOpt("graph").required(false)
                .desc("path to gml file").type(String.class).build();

        Option batchOpt = Option.builder("b").hasArg().longOpt("batch").required(false)
                .desc("experiment set").type(String.class).build();

        Option outnameOpt = Option.builder("o").hasArg().longOpt("outputName").required(false)
                .desc("name for output files").type(String.class).build();

        Option seedOpt = Option.builder("s").hasArg().longOpt("seed").required(false)
                .desc("seed").type(Long.class).build();

        Option cleanUpRateOpt = Option.builder("c").hasArg().longOpt("cleanRate").required(false)
                .desc("cleanup rate").type(Double.class).build();

        Option cleanUpTholdOpt = Option.builder("P").hasArg().longOpt("cleanThold").required(false)
                .desc("cleanup threshold").type(Double.class).build();

        Option cleanUpPortionOpt = Option.builder("t").hasArg().longOpt("cleanPort").required(false)
                .desc("cleanup portion").type(Double.class).build();


        options.addOption(generationsOpt);
        options.addOption(popSizeOpt);
        options.addOption(mutateOpt);
        options.addOption(elitismPortionOpt);
        options.addOption(initBiasOpt);
        options.addOption(graphPathOpt);
        options.addOption(batchOpt);
        options.addOption(outnameOpt);

        // parse out options
        CommandLine cmdLine = parser.parse(options, args);
        popSize = cmdLine.hasOption('p') ? Integer.parseInt(cmdLine.getOptionValue('p')) : 250;
        generations = cmdLine.hasOption('g') ? Integer.parseInt(cmdLine.getOptionValue('g')) : 30;
        mutRate = cmdLine.hasOption('m') ? Double.parseDouble(cmdLine.getOptionValue('m')) : 0.05;
        initRate = cmdLine.hasOption('i') ? Double.parseDouble(cmdLine.getOptionValue('i')) : 0.1;
        elitePortion = cmdLine.hasOption('e') ? Double.parseDouble(cmdLine.getOptionValue('e')) : 0.1;
        batch = cmdLine.hasOption('b') ? cmdLine.getOptionValue('b') : "tasgin_test";
        gmlPath = cmdLine.hasOption('G') ? cmdLine.getOptionValue('G') : "../gml_files/real_networks/karate.gml";
        outName = cmdLine.hasOption('o') ? cmdLine.getOptionValue('o') : "tasgin_testing_" + String.valueOf(seed);;
        seed = cmdLine.hasOption('s')?Long.parseLong(cmdLine.getOptionValue('s')):System.nanoTime();
        cleanRate = cmdLine.hasOption('c') ? Double.parseDouble(cmdLine.getOptionValue('c')) : 0.0;
        cleanPortion = cmdLine.hasOption('P') ? Double.parseDouble(cmdLine.getOptionValue('P')) : 0.1;
        cleanThold = cmdLine.hasOption('t') ? Double.parseDouble(cmdLine.getOptionValue('t')) : 0.8;


        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:../ClusterResults.db");

        randgen = new Random(seed);
        ArrayList<Double> bestScores = new ArrayList();
        ArrayList<Double> avgScores = new ArrayList();

        AdjGraph a = new AdjGraph(gmlPath);
        Population p = new Population(popSize, a, elitePortion, initRate);

        for (int i = 0; i < generations; i++) {
            if(i%10 == 0)
                System.out.println(p.getPopAvg()+ "\t" + p.getBestInd().score);
            p.updateGen(true);
            updateGenStats(bestScores, avgScores, p);
        }
        Individual ind = p.getBestInd();
        System.out.println(ind.getMembershipString());
        System.out.println(p.getBestInd().score);
        Main.writeRecord(conn, bestScores, avgScores, p.getBestInd());
        conn.close();
    }

    /**
     * update lists of generation average and best fitness
     * @param score
     * @param avg
     * @param p
     */
    public static void updateGenStats(ArrayList<Double> score, ArrayList<Double> avg, Population p){
        score.add(new Double(p.getBestInd().score));
        avg.add(new Double(p.getPopAvg()));
    }

    /**
     * lazy method to write lists to sqlite
     * @param list
     */
    public static String listStrRep(ArrayList<Double> list){
        String str = "";
        for(int i=0; i < list.size(); i++){
            str += list.get(i);
            if (i != list.size()-1)
                str += ",";
        }
        return str;
    }

    /**
     * dump results to sqlite
     * @param conn
     * @param best
     * @param avg
     * @param bestInd
     * @throws SQLException
     */
    public static void writeRecord(Connection conn, ArrayList<Double> best, ArrayList<Double> avg, Individual bestInd) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert into ClusterOutputs values(?,?,?,?,?,?,?,?,?,?)");
        stmt.setString(1, Main.batch);
        stmt.setString(2, "tasgin");
        stmt.setString(3, Long.toString(Main.seed));
        stmt.setString(4, "");
        stmt.setString(5, bestInd.getMembershipString());
        stmt.setString(6, Main.gmlPath);
        stmt.setString(7, Main.listStrRep(best));
        stmt.setString(8, Main.listStrRep(avg));
        stmt.setString(9, Double.toString(bestInd.score));
        stmt.setString(10, "");
        stmt.execute();
    }

}
