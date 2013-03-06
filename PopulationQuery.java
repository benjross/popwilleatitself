/*
 * Jordan Hazari (Primary Author)
 * Ben Ross
 * 3/5/13
 * CSE 332 AB
 * Project 3 Part A
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

/**
 * This class can be used to parse a data file into usable
 * US census population data.  It can then process queries,
 * to provide information about the population in specific
 * areas of the US.
 */
public class PopulationQuery {
    // next four constants are relevant to parsing
    public static final int TOKENS_PER_LINE  = 7;
    public static final int POPULATION_INDEX = 4; // zero-based indices
    public static final int LATITUDE_INDEX   = 5;
    public static final int LONGITUDE_INDEX  = 6;
    private static PopulationQueryVerison imp;

    /**
     * parse the input file into a large array held 
     * in a CensusData object.
     * 
     * @param filename the file to be parsed.
     * @return a CensusData object holding the parsed data.
     */
    public static CensusData parse(String filename) {
        CensusData result = new CensusData();

        try {
            BufferedReader fileIn = new BufferedReader(new FileReader(filename));

            // Skip the first line of the file
            // After that each line has 7 comma-separated numbers (see constants above)
            // We want to skip the first 4, the 5th is the population (an int)
            // and the 6th and 7th are latitude and longitude (floats)
            // If the population is 0, then the line has latitude and longitude of +.,-.
            // which cannot be parsed as floats, so that's a special case
            //   (we could fix this, but noisy data is a fact of life, more fun
            //    to process the real data as provided by the government)

            String oneLine = fileIn.readLine(); // skip the first line

            // read each subsequent line and add relevant data to a big array
            while ((oneLine = fileIn.readLine()) != null) {
                String[] tokens = oneLine.split(",");
                if(tokens.length != TOKENS_PER_LINE)
                    throw new NumberFormatException();
                int population = Integer.parseInt(tokens[POPULATION_INDEX]);
                if(population != 0)
                    result.add(population,
                            Float.parseFloat(tokens[LATITUDE_INDEX]),
                            Float.parseFloat(tokens[LONGITUDE_INDEX]));
            }

            fileIn.close();
        } catch(IOException ioe) {
            System.err.println("Error opening/reading/writing input or output file.");
            System.exit(1);
        } catch(NumberFormatException nfe) {
            System.err.println(nfe.toString());
            System.err.println("Error in file format");
            System.exit(1);
        }
        return result;
    }

    /**
     * Asks the user for input, processes the query, and
     * outputs the group population and percentage of the
     * total population.
     * 
     * @param args[0] file name for input data: pass this to parse
     * @param args[1] number of x-dimension buckets
     * @param args[2] number of y-dimension buckets
     * @param args[3] -v1, -v2, -v3, -v4, or -v5
     */
    public static void main(String[] args) {
        String fileName = args[0];
        String xNum = args[1];
        String yNum = args[2];
        String version = args[3];

        int x = Integer.parseInt(xNum);
        int y = Integer.parseInt(yNum);
        int vNum;
        if(version.equals("-v1")) {
            vNum = 1;
        } else if(version.equals("-v2")) {
        	vNum = 2;
        } else if(version.equals("-v3")) {
        	vNum = 3;
        } else if(version.equals("-v4")) {
        	vNum = 4;
        } else {//(version.equals("-v5"))
        	vNum = 5;
        }
        preprocess(fileName,x,y,vNum);

        boolean fourArgs = true;
        while(fourArgs) {
            System.out.println("Please give west, south, east, north coordinates of your query rectangle:");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String userInput = null;

            try {
                userInput = br.readLine();
            } catch (IOException ioe) {
                System.out.println("IO error trying to read your name!");
                System.exit(1);
            }

            String[] coordinates = userInput.split(" ");
            if(coordinates.length != 4)
                fourArgs = false; //exit the loop
            else {
                int west = Integer.parseInt(coordinates[0]);
                int south = Integer.parseInt(coordinates[1]);
                int east = Integer.parseInt(coordinates[2]);
                int north = Integer.parseInt(coordinates[3]);
                
                if(queryChecker(west,south,east,north,x,y))
                	throw new IndexOutOfBoundsException("One or more group"
                			+" coordinate is outside the bounds of the grid");
                
                DecimalFormat df = new DecimalFormat("0.00");
                
                Pair<Integer,Float> p = singleInteraction(west,south,east,north);
               	System.out.println("population of rectangle: "+p.getElementA());
               	String percent = df.format(p.getElementB());
               	System.out.println("percent of total population: "+percent);
            }
        }
    }
    
    /**
     * Processes a query, and returns the population of the group and
     * the percent of the total population.
     * 
     * @param w the west coordinate of the group.
     * @param s the south coordinate of the group.
     * @param e the east coordinate of the group.
     * @param n the north coordinate of the group.
     * @return a Pair holding the group's population, and the
     * 		   percent of the total population.
     */
    public static Pair<Integer, Float> singleInteraction(int w, int s, int e, int n) {
    	float totPop = (float) imp.getPop();
        if(totPop == 0)
        	return new Pair<Integer,Float>(0,totPop);
        int areaPop= imp.query(w, s, e, n);
        return new Pair<Integer, Float>(areaPop, (100*(float)areaPop/totPop));
    }
    
    /**
     * Parses the data file, and sets up the initial population
     * grid.
     * 
     * @param filename the file to be parsed.
     * @param columns the number of columns in the grid.
     * @param rows the number of rows in the grid.
     * @param versionNum 1-5, chooses which implementation of the
     * 		  program to use.
     */
    public static void preprocess(String filename, int columns, int rows, int versionNum) {
        CensusData data = parse(filename);
        
        if (data == null)
            throw new NullPointerException("No population to process - check your file");
        if (columns < 1 || rows < 1)
            throw new IndexOutOfBoundsException("positive row/column numbers expected");
        
        if(versionNum == 1) {
            imp = new SimpleAndSequential(columns, rows, data);
            imp.preprocess();
        } else if (versionNum == 2) {
        	imp = new SimpleAndParallel(columns, rows, data);
        	imp.preprocess();
        } else if (versionNum == 3) {
        	imp = null;
        } else if (versionNum == 4) {
        	imp = null;
        } else if (versionNum == 5) {
        	imp = null;
        } else {
        	imp = null;
        }
    }
    
    /**
     * Decides whether or not a query is valid, by checking
     * to make sure all the desired group coordinates are inside
     * the bounds of the grid.
     * 
     * @param west the west coordinate of the group.
     * @param south the south coordinate of the group.
     * @param east the east coordinate of the group.
     * @param north the north coordinate of the group.
     * @param x the number of columns in the grid.
     * @param y the number of rows in the grid.
     * @return true if the query is valid, false otherwise.
     */
    private static boolean queryChecker(int west, int south, int east, int north, int x, int y) {
        return (west < 1 || west > x ||
                south < 1 || south > y ||
                east < west || east > x ||
                north < south || north > y);
    }
}
