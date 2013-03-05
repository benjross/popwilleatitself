/*
 * Ben Ross (Primary Author)
 * Jordan Hazari
 * 3/5/13
 * CSE 332 AC
 * Daniel Jones
 * Project 3 part A
 */

/**
 * SimpleAndSequential implements the Implementation interface to provide
 * functionality for finding information about a population.  The constructor
 * takes in the number of rows, the number of columns, and the CensusData of
 * the population.
 * 
 * @author benross
 */
public class SimpleAndSequential implements Implementation {
    private final int x;
    private final int y;
    private Rectangle america;
    private final CensusData censusData;
    int totalPopulation;

    /**
     * Creates a SimpleAndSequential object to provide population query
     * functions.
     * 
     * @param x The number of columns
     * @param y The number of rows
     * @param data The CensusData object to be queried
     */
    public SimpleAndSequential(int x, int y, CensusData data) {
        this.x = x;
        this.y = y;
        this.censusData = data;
        totalPopulation = 0;
    }

    /** {@inheritDoc} */
    @Override
    public int query(int west, int south, int east, int north) {
        CensusGroup group;
        int population = 0;
        double groupLong, groupLat;
        float yAxis = america.left;
        float xAxis = america.bottom;
        float gridSquareWidth = (america.right - america.left) / x;
        float gridSquareHeight = (america.top - america.bottom) / y;
        double leftBound = (yAxis + (west - 1) * gridSquareWidth);
        double rightBound = (yAxis + (east) * gridSquareWidth);
        double topBound = (xAxis + (north) * gridSquareHeight);
        double bottomBound = (xAxis + (south - 1) * gridSquareHeight);
        for (int i = 0; i < censusData.data_size; i++) {
            group = censusData.data[i];
            groupLong = group.longitude;
            groupLat = group.latitude;
            // Defaults to North and/or East in case of tie
            if (groupLat >= bottomBound &&
                    groupLat <= topBound &&
                    groupLong <= rightBound &&
                    groupLong >= leftBound
                    )
                population += group.population;
        }

        return population;
    }

    /** {@inheritDoc} */
    @Override
    public void preprocess() {
        int pop = 0;
        CensusGroup group = censusData.data[0];

        Rectangle rec = new Rectangle(group.longitude, group.longitude,
                group.latitude, group.latitude), temp;
        pop += group.population;

        for (int i = 1; i < censusData.data_size; i++) {
            group = censusData.data[i];
            temp = new Rectangle(group.longitude, group.longitude,
                    group.latitude, group.latitude);
            rec = rec.encompass(temp);
            pop += group.population;
        }

        america = rec;
        totalPopulation = pop;
    }

    /** {@inheritDoc} */
    @Override
    public int getPop() {
        return totalPopulation;
    }
}
