/*
 * Ben Ross (Primary Author)
 * Jordan Hazari
 * 3/12/13
 * CSE 332 AC
 * Daniel Jones
 * Project 3 part A
 */

/**
 * SimpleAndSequential extends PopulationQueryVersion to provide
 * functionality for finding information about a population.  The constructor
 * takes in the number of rows, the number of columns, and the CensusData of
 * the population.
 * 
 * @author Ben Ross
 */
public class SimpleAndSequential extends PopulationQueryVerison {

    /**
     * Creates a SimpleAndSequential object to provide population query
     * functions.
     * 
     * @param x The number of columns
     * @param y The number of rows
     * @param data The CensusData object to be queried
     */
    public SimpleAndSequential(int x, int y, CensusData data) {
        super(x, y, data);
    }

    /** {@inheritDoc} */
    @Override
    public int query(int west, int south, int east, int north) {
        if (america == null)
            return 0;

        CensusGroup group;
        int population = 0;
        double groupLong, groupLat;

        // Grid bounds of query
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
                    (groupLat < topBound ||
                            (topBound - america.top) >= 0) &&
                            (groupLong < rightBound ||
                                    (rightBound - america.right) >= 0) &&
                                    groupLong >= leftBound)
                population += group.population;
        }

        return population;
    }

    /** {@inheritDoc} */
    @Override
    public void preprocess() {
        if (censusData.data_size == 0)
            return;

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

        yAxis = america.left;
        xAxis = america.bottom;
        gridSquareWidth = (america.right - america.left) / x;
        gridSquareHeight = (america.top - america.bottom) / y;

        totalPopulation = pop;
    }
}
