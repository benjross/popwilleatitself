/*
 * Ben Ross (Primary Author)
 * Jordan Hazari
 * 3/5/13
 * CSE 332 AC
 * Daniel Jones
 * Project 3 part A
 */

/**
 * Interface for an implementation of a population query object.  Provides
 * features for finding the information about geographic populations.
 */
public abstract class PopulationQueryVerison {
    // The number of columns
    protected final int x;
    // The number of rows
    protected final int y;
    // A Rectangle representing the size of America
    protected Rectangle america;
    // The population data
    protected final CensusData censusData;
    // The total population of America
    protected int totalPopulation;

    /**
     * Creates a PopulationQueryVersion
     * 
     * @param x The number of columns
     * @param y The number of rows
     * @param data The CensusData object to be queried
     */
    public PopulationQueryVerison(int x, int y, CensusData data) {
        this.x = x;
        this.y = y;
        this.censusData = data;
        totalPopulation = 0;
        america = null;
    }

    /**
     * Processes a query, and returns the population of the group in the area
     * bounded by east, west, south, and north as an int.
     * 
     * @param west the west coordinate of the group.
     * @param south the south coordinate of the group.
     * @param east the east coordinate of the group.
     * @param north the north coordinate of the group.
     * @return the population of the group.
     */
    public abstract int query(int west, int south, int east, int north);

    /**
     * Parses the data file, and counts the total population.
     * grid.
     * 
     */
    public abstract void preprocess();

    /**
     * Returns the total population as an int.  Should only be called after
     * a preprocess() has been called, otherwise this method has undefined
     * behavior.
     * 
     * @requires requires prior call to preprocess().
     * @return the total population.
     */
    public int getPop() {
        return totalPopulation;
    }
}
