
import com.brock.pe12nh.AdjGraph.AdjGraph;
import com.sun.org.apache.xpath.internal.operations.Number;
import org.apache.commons.cli.*;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
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
    public static String gmlPath;
    public static long seed;
    public static String propertiesPath;
    public static int reps;

    public static void main(String args[]) throws IOException, InterruptedException, ParseException, SQLException, ClassNotFoundException {
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

        readProperties();

        randgen = new Random(seed);
        adjG = new AdjGraph(gmlPath);

        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:D://alien-pineapple/ClusterResults.db");

        int r = 0;
        while (reps > r) {
            r++;
            System.out.println(gmlPath + " attempt " + r);
            ArrayList<Double> bestScores = new ArrayList();
            ArrayList<Double> avgScores = new ArrayList();


            randgen = new Random();
            Population p = new Population(popSize, adjG);
            for (int i = 0; i < generations; i++) {
                p.updatePop();
              //  System.out.println(p.getMax().score);
                bestScores.add(p.getMax().score);
                avgScores.add(p.getPopAvg());
            }

            writeRecord(conn, bestScores, avgScores, p.getMax());
            seed = System.nanoTime();
        }
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
        PreparedStatement stmt = conn.prepareStatement("insert into ClusterResults values(?,?,?,?,?,?,?,?)");
        stmt.setString(1, "ganet");
        stmt.setString(2, Long.toString(Main.seed));
        stmt.setString(3, Main.gmlPath);
        stmt.setString(4, Main.listStrRep(avg));
        stmt.setString(5, Main.listStrRep(best));
        stmt.setString(6, Double.toString(bestInd.score));
        stmt.setString(7, Main.propertiesPath);
        stmt.setString(8, bestInd.getMembershipString());
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

            if (p.containsKey("crossoverRate")) {
                Main.crossoverRate = Double.parseDouble(p.getProperty("crossoverRate"));
            }


            if (p.containsKey("mutRate")) {
                Main.mutRate = Double.parseDouble(p.getProperty("mutRate"));
            }


            if (p.containsKey("elitePortion")) {
                Main.elitePortion = Double.parseDouble(p.getProperty("elitePortion"));
            }

            if (p.containsKey("powerVal")) {
                Main.powerVal = Double.parseDouble(p.getProperty("powerVal"));
            }

            if (p.containsKey("seed")) {
                Main.seed = Long.parseLong(p.getProperty("seed"));
            }

        } catch (IOException e) {

            e.printStackTrace();
        }

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
                str += "\n";
        }
        return str;
    }

}