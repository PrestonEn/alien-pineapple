package com.brock.pe12nh.TasginGA;

import com.brock.pe12nh.AdjGraph.AdjGraph;
import com.sun.org.apache.xpath.internal.operations.Number;
import org.apache.commons.cli.*;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Properties;

public class Main {

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
    public static String gmlPath;
    public static String propertiesPath;
    public static long seed;
    public static int reps = 1;

    public static void main(String args[]) throws IOException, ParseException,
            ClassNotFoundException, SQLException {
        // Set up session variables and sql connection
        CommandLineParser parser = new DefaultParser();
        options = new Options();
        Option graphPathOpt = Option.builder("G").hasArg().longOpt("graph").required(false)
                .desc("path to gml file").type(String.class).build();

        Option propFileOpt = Option.builder("P").hasArg().longOpt("prop").required(false)
                .desc("path properties file").type(String.class).build();

        Option repOpt = Option.builder("R").hasArg().longOpt("rep").required(false)
                .desc("Number of experiments").type(Integer.class).build();

        options.addOption(graphPathOpt);
        options.addOption(propFileOpt);
        options.addOption(repOpt);

        seed = System.nanoTime();
        // parse out options
        CommandLine cmdLine = parser.parse(options, args);
        if (cmdLine.hasOption('G')) {
            gmlPath = cmdLine.getOptionValue('G');
        } else {
            System.out.println("you must provide a graph file");
            return;
        }


        if (cmdLine.hasOption('P')) {
            propertiesPath = cmdLine.getOptionValue('P');
            Main.readProperties();
        } else {
            System.out.println("you must provide a properties file");
            return;
        }

        if (cmdLine.hasOption('R')) {
            reps = Integer.parseInt(cmdLine.getOptionValue('R'));
            Main.readProperties();
        } else {
            System.out.println("you must provide a properties file");
            return;
        }

        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:D://alien-pineapple/ClusterResults.db");
        int r = 0;
        while (reps > r) {
            randgen = new Random(seed);
            ArrayList<Double> bestScores = new ArrayList();
            ArrayList<Double> avgScores = new ArrayList();

            System.out.println("reading");
            AdjGraph a = new AdjGraph(gmlPath);
            System.out.println("initializaing");
            Population p = new Population(popSize, a, elitePortion, initRate);

            for (int i = 0; i < generations; i++) {
                if (i % 5 == 0)
                    System.out.println("generation" + i + " best: " + p.getBestInd().score + "\taverage: " + p.getPopAvg());
                p.updateGen(true);
                updateGenStats(bestScores, avgScores, p);
            }
            Main.writeRecord(conn, bestScores, avgScores, p.getBestInd());
            seed = System.nanoTime(); // if we are doing multiple runs, should reset the seed
            r++;
        }

        conn.close();
    }

    /**
     * update lists of generation average and best fitness
     *
     * @param score
     * @param avg
     * @param p
     */
    public static void updateGenStats(ArrayList<Double> score, ArrayList<Double> avg, Population p) {
        score.add(new Double(p.getBestInd().score));
        avg.add(new Double(p.getPopAvg()));
    }

    /**
     * lazy method to write lists to sqlite
     *
     * @param list
     */
    public static String listStrRep(ArrayList<Double> list) {
        String str = "";
        for (int i = 0; i < list.size(); i++) {
            str += list.get(i);
            if (i != list.size() - 1)
                str += '\n';
        }
        return str;
    }

    /**
     * dump results to sqlite
     *
     * @param conn
     * @param best
     * @param avg
     * @param bestInd
     * @throws SQLException
     */
    public static void writeRecord(Connection conn, ArrayList<Double> best, ArrayList<Double> avg, Individual bestInd) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert into ClusterResults values(?,?,?,?,?,?,?,?,?,?,?)");
        stmt.setString(1, "tasgin");
        stmt.setString(2, Long.toString(Main.seed));
        stmt.setString(3, Main.gmlPath);
        stmt.setString(4, Main.listStrRep(avg));
        stmt.setString(5, Main.listStrRep(best));
        stmt.setString(6, Double.toString(bestInd.score));
        stmt.setString(7, Main.propertiesPath);
        stmt.setString(8, bestInd.getMembershipString());
        stmt.setDouble(9, 0);
        stmt.setDouble(10, 0);
        stmt.setDouble(11, 0);
        stmt.execute();
    }

    static void readProperties() {
        Properties p = new Properties();
        try {
            p.load(new FileReader(propertiesPath));
            if (p.containsKey("popSize")) {
                Main.popSize = Integer.parseInt(p.getProperty("popSize"));
            }

            if (p.containsKey("generations")) {
                Main.generations = Integer.parseInt(p.getProperty("generations"));
            }

            if (p.containsKey("mutRate")) {
                Main.mutRate = Double.parseDouble(p.getProperty("mutRate"));
            }

            if (p.containsKey("initRate")) {
                Main.initRate = Double.parseDouble(p.getProperty("initRate"));
            }

            if (p.containsKey("elitePortion")) {
                Main.elitePortion = Double.parseDouble(p.getProperty("elitePortion"));
            }
            if (p.containsKey("cleanRate")) {
                Main.cleanRate = Double.parseDouble(p.getProperty("cleanRate"));
            }
            if (p.containsKey("cleanThold")) {
                Main.cleanThold = Double.parseDouble(p.getProperty("cleanThold"));
            }
            if (p.containsKey("cleanPortion")) {
                Main.cleanPortion = Double.parseDouble(p.getProperty("cleanPortion"));
            }

            if (p.containsKey("seed")) {
                Main.seed = Long.parseLong(p.getProperty("seed"));
            }

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

}
