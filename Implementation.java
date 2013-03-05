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
public interface Implementation {

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
    public int query(int west, int south, int east, int north);

    /**
     * Parses the data file, and counts the total population.
     * grid.
     * 
     */
    public void preprocess();

    /**
     * Returns the total population as an int.  Should only be called after
     * a preprocess() has been called, otherwise this method has undefined
     * behavior.
     * 
     * @requires requires call prior call to preprocess().
     * @return the total population.
     */
    public int getPop();
}
