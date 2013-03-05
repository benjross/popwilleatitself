/**
 * An interface for an implementation of completing
 * population queries.  Each implementation must be able
 * to process the data and set up the initial grid, find
 * the population of a specific area in the US, and find
 * the total population of the US.
 */
public interface Implementation {
	
	/**
	 * Processes a query, and returns the population of the group and
     * the percent of the total population.
	 * 
	 * @param west the west coordinate of the group.
	 * @param south the south coordinate of the group.
	 * @param east the east coordinate of the group.
	 * @param north the north coordinate of the group.
	 * @return the population of the group.
	 */
    public int query(int west, int south, int east, int north);
    
    /**
     * Parses the data file, and sets up the initial population
     * grid.
     */
    public void preprocess();
    
    /**
     * Gets the total population.
     * 
     * @return the total population.
     */
    public int getPop();
}
