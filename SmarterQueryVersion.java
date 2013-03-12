/*
 * Ben Ross (Primary Author)
 * Jordan Hazari
 * 3/5/13
 * CSE 332 AC
 * Daniel Jones
 * Project 3 part A
 */

/**
 * An implementation of a version of a population query. It extends
 * SimpleAndParallel.  Provides features for finding the information about
 * geographic populations.
 */
public class SmarterQueryVersion extends SimpleAndParallel {
	// An array of int arrays representing the grid of America
	protected int[][] grid;

    /**
     * Creates a SmarterQueryVersion
     * 
     * @param x The number of columns
     * @param y The number of rows
     * @param data The CensusData object to be queried
     */
	public SmarterQueryVersion(int x, int y, CensusData data) {
		super(x, y, data);
		grid = new int[x][y];
	}
	
	/** {@inheritDoc} */
	@Override
	public int query(int west, int south, int east, int north) {
		// 		north
		// west			east
		// 		south
		int pop = 0;
		pop += grid[east - 1][south - 1];
		pop -= (north == y ? 0 : grid[east- 1][north]); // top right
		pop -= (west == 1 ? 0 : grid[west- 1 - 1][south - 1]); // bottom left
		pop += (west == 1 || north == y ? 0 : grid[west - 1 - 1][north]); // top left
		return pop;
	}

	/** {@inheritDoc} */
	@Override
	public void preprocess() {
		super.preprocess();
	}
}
