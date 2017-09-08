/**
 * Created by speng on 7/31/2017.
 */
import com.brock.pe12nh.AdjGraph.AdjGraph;
import com.sun.org.apache.xpath.internal.operations.Number;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    /* TODO Individual cleanup method
     * TODO argparse
     * TODO sqlLite
     */

    public static Random randgen;
    public static double powerVal;
    public static int generations;
    public static int popSize;
    public static double elitePortion;
    public static double crossoverRate;
    public static double mutRate;
    private static Options options;
    public static AdjGraph adjG;
    public static String batch;
    public static String gmlPath;
    public static String outName;
    public static long seed;


    public static void main(String args[]) throws IOException, InterruptedException, ParseException, SQLException, ClassNotFoundException {
        seed = System.nanoTime();
        powerVal = 0.9;
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

        Option crossOpt = Option.builder("c").hasArg().longOpt("crossRate").required(false)
                .desc("crossover rate").type(Double.class).build();

        Option graphPathOpt = Option.builder("G").hasArg().longOpt("graph").required(false)
                .desc("path to gml file").type(String.class).build();

        Option batchOpt = Option.builder("b").hasArg().longOpt("batch").required(false)
                .desc("experiment set").type(String.class).build();

        Option outnameOpt = Option.builder("o").hasArg().longOpt("outputName").required(false)
                .desc("name for output files").type(String.class).build();

        Option powerValOpt = Option.builder("p").hasArg().longOpt("powerVal").required(false)
                .desc("power exponent").type(Double.class).build();

        Option seedOpt = Option.builder("s").hasArg().longOpt("seed").required(false)
                .desc("seed").type(Long.class).build();

        // parse out options
        CommandLine cmdLine = parser.parse(options, args);

        options.addOption(popSizeOpt);
        options.addOption(mutateOpt);
        options.addOption(elitismPortionOpt);
        options.addOption(generationsOpt);
        options.addOption(graphPathOpt);
        options.addOption(batchOpt);
        options.addOption(outnameOpt);
        options.addOption(seedOpt);
        options.addOption(crossOpt);
        options.addOption(powerValOpt);

        popSize = cmdLine.hasOption('p') ? Integer.parseInt(cmdLine.getOptionValue('p')) : 300;
        generations = cmdLine.hasOption('g') ? Integer.parseInt(cmdLine.getOptionValue('g')) : 30;
        mutRate = cmdLine.hasOption('m') ? Double.parseDouble(cmdLine.getOptionValue('m')) : 0.03;
        elitePortion = cmdLine.hasOption('e') ? Double.parseDouble(cmdLine.getOptionValue('e')) : 0.1;
        crossoverRate = cmdLine.hasOption('p') ? Double.parseDouble(cmdLine.getOptionValue('p')) : 0.9;
        batch = cmdLine.hasOption('b') ? cmdLine.getOptionValue('b') : "ganet_testing";
        gmlPath = cmdLine.hasOption('G') ? cmdLine.getOptionValue('G') : "/Users/preston/Downloads/4f90/gml_files/karate.gml";
        outName = cmdLine.hasOption('o') ? cmdLine.getOptionValue('o') : "ganet_testing_" + String.valueOf(seed);
        seed = cmdLine.hasOption('s')?Long.parseLong(cmdLine.getOptionValue('s')):System.nanoTime();
        adjG = new AdjGraph(gmlPath);


        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:/Users/preston/Downloads/4f90/ClusterResults.db");

        randgen = new Random(seed);
        ArrayList<Double> bestScores = new ArrayList();
        ArrayList<Double> avgScores = new ArrayList();


        randgen = new Random();
        Population p = new Population(popSize, adjG);
        long st = System.nanoTime();
        for (int i = 0; i < generations; i++) {
            p.updatePop();
        }
        p.sortPop();
        Individual i = p.pop.get(0);
        Solution s = Individual.decode(i);
        System.out.println(i.getMembershipString());
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

}