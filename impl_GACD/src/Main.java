import com.brock.pe12nh.AdjGraph.AdjGraph;
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

/**
 * Created by speng on 8/14/2017.
 */
public class Main {

    public static Random randgen;
    public static int popSize;
    public static AdjGraph adjG;
    public static Options options;
    public static double crossoverRate;
    public static double mutatePortion;
    public static int generations;
    public static long seed;
    public static String gmlPath;
    public static String propertiesPath;
    public static int reps;
    public static void main(String args[])throws IOException, InterruptedException, ParseException, SQLException, ClassNotFoundException {
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
            System.out.println("you must provide a runs count");
            return;
        }

        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:D://alien-pineapple/ClusterResults.db");
        readProperties();

        randgen = new Random(seed);
        adjG = new AdjGraph(gmlPath);
        for(int r=0; r <  reps; r++) {
            ArrayList<Double> bestScores = new ArrayList();
            ArrayList<Double> avgScores = new ArrayList();
            randgen = new Random();
            Population p = new Population(popSize, adjG);
            bestScores.add(p.getMax().score);
            avgScores.add(p.getPopAvg());
            for (int j = 0; j < generations; j++) {
                p.updatePop();
                bestScores.add(p.getMax().score);
                avgScores.add(p.getPopAvg());
            }
            writeRecord(conn, bestScores, avgScores, p.getMax());
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
        PreparedStatement stmt = conn.prepareStatement("insert into ClusterResults values(?,?,?,?,?,?,?,?,?,?,?)");
        stmt.setString(1, "gacd");
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

            if (p.containsKey("crossoverRate")) {
                Main.crossoverRate = Double.parseDouble(p.getProperty("crossoverRate"));
            }

            if (p.containsKey("mutatePortion")) {
                Main.mutatePortion = Double.parseDouble(p.getProperty("mutatePortion"));
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

