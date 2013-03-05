
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PopulationQuery {
    // next four constants are relevant to parsing
    public static final int TOKENS_PER_LINE  = 7;
    public static final int POPULATION_INDEX = 4; // zero-based indices
    public static final int LATITUDE_INDEX   = 5;
    public static final int LONGITUDE_INDEX  = 6;
    private static Implementation imp;

//    private static void query(Implementation imp, int west, int south, int east, int north) {
//        while (true) {
//            imp.query(west, south, east, north);
//        }
//    }
//    private static void preprocess(Implementation imp, int row, int columns) {
//        imp.preprocess();
//    }

    // parse the input file into a large array held in a CensusData object
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

    // argument 1: file name for input data: pass this to parse
    // argument 2: number of x-dimension buckets
    // argument 3: number of y-dimension buckets
    // argument 4: -v1, -v2, -v3, -v4, or -v5
    public static void main(String[] args) {
        String fileName = args[0];
        String xNum = args[1];
        String yNum = args[2];
        String version = args[3];

        CensusData data = parse(fileName);
        int x = Integer.parseInt(xNum);
        int y = Integer.parseInt(yNum);
        if(version.equals("-v1")) {
            imp = new SimpleAndSequential(x, y, data);
        } else if(version.equals("-v2")) {
        	imp = null;
        } else if(version.equals("-v3")) {
        	imp = null;
        } else if(version.equals("-v4")) {
        	imp = null;
        } else if(version.equals("-v5")) {
        	imp = null;
        } else {
        	imp = null;
        }
        imp.preprocess();

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

                Pair<Integer,Float> p = singleInteraction(west,south,east,north);
               	System.out.println("population of rectangle: "+p.getElementA());
               	System.out.println("percent of total population: "+p.getElementB());
            }
        }
    }
    public static Pair<Integer, Float> singleInteraction(int w, int s, int e, int n) {
        // TODO Auto-generated method stub
        int areaPop= imp.query(w, s, e, n);
        return new Pair<Integer, Float>(areaPop, (float) (100*areaPop/imp.getPop() ));
    }
    public static void preprocess(String filename, int columns, int rows, int versionNum) {
        CensusData data = parse(filename);
        if(versionNum == 1) {
            imp = new SimpleAndSequential(rows, columns, data);
            imp.preprocess();
        } else if (versionNum == 2) {
        	imp = null;
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
}
