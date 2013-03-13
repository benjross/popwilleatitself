/*
 * Ben Ross (Primary Author)
 * Jordan Hazari
 * 3/12/13
 * CSE 332 AC
 * Daniel Jones
 * Project 3 part B
 */

import java.util.concurrent.locks.ReentrantLock;


/**
 * SmarterAndLockBased extends SmarterQueryVersion to provide
 * functionality for finding information about a population.  The constructor
 * takes in the number of rows, the number of columns, and the CensusData of
 * the population.
 * 
 * @author Ben Ross
 */
public class SmarterAndLockBased extends SmarterQueryVersion {
	//private static final int NUM_THREADS = 4;
	private ReentrantLock[][] locks;



    /**
     * Creates a SmarterAndLockBased object to provide population query
     * functions.
     * 
     * @param x The number of columns
     * @param y The number of rows
     * @param data The CensusData object to be queried
     */
	public SmarterAndLockBased(int x, int y, CensusData data) {
		super(x, y, data);
		locks = new ReentrantLock[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				locks[i][j] = new ReentrantLock();
			}
		}
	}

    // An internal class for preprocessing
	class SmarterPreprocessor extends java.lang.Thread {
	       int hi, lo;
	        // Look at data from lo (inlcusive) to hi (exclusive)
	        SmarterPreprocessor(int lo, int hi) {
	            this.lo  = lo;
	            this.hi = hi;
	        }

		@Override
		public void run() {
            if(hi - lo <  1000) {
                CensusGroup group;
                int row, col, pop;
                for (int i = lo; i < hi; i++) {
                    group = censusData.data[i];
                    col = (int) ((group.latitude - xAxis) / gridSquareHeight);
                    col = (col == y ?  y - 1: col); // edge case
                    row = (int) ((group.longitude - yAxis) / gridSquareWidth);
                    row = (row == x ? x - 1 : row); // edge case
                    pop = group.population;
                    locks[row][col].lock();
                    try {
                    	grid[row][col] += pop;
                    } finally {
                    	locks[row][col].unlock();
                    }    
                }

            } else {
                SmarterPreprocessor left = new SmarterPreprocessor(lo, (hi+lo)/2);
                SmarterPreprocessor right = new SmarterPreprocessor((hi+lo)/2, hi);

                left.start(); // fork a thread and calls compute
                right.run();//call compute directly
                try {
					left.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }

        }

	}


	/** {@inheritDoc} */
	@Override
	public void preprocess() {
		super.preprocess();
		
		SmarterPreprocessor sp = new SmarterPreprocessor(0, censusData.data_size);
		sp.run();
        
        // sum top edge (of graph)
        for (int i = 1; i < grid.length; i++) {
        	grid[i][grid[0].length - 1] += grid [i - 1][grid[0].length - 1];
        }
        
        // sum left edge (of graph)
        for (int i = grid[0].length - 2; i >= 0; i--) {
        	grid[0][i] += grid [0][i + 1];
        }
        
        
        for (int j = grid[0].length - 1 - 1; j >= 0; j--) {
        	for (int i = 1; i < grid.length; i++) {
        		grid[i][j] += (grid[i-1][j] + grid[i][j+1] - grid[i-1][j+1]);
        	}
        }
	}

}
