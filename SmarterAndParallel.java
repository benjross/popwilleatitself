/*
 * Jordan Hazari (Primary Author)
 * Ben Ross
 * 3/12/13
 * CSE 332 AB
 * Project 3 Part B
 */

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 * SmarterAndParallel extends SmarterQueryVersion to provide
 * functionality for finding information about a population.  The constructor
 * takes in the number of rows, the number of columns, and the CensusData of
 * the population.
 * 
 * @author Jordan Hazari
 */
public class SmarterAndParallel extends SmarterQueryVersion {

    /**
     * Creates a SmarterAndParallel object to provide population query
     * functions.
     * 
     * @param x The number of columns
     * @param y The number of rows
     * @param data The CensusData object to be queried
     */
    public SmarterAndParallel(int x, int y, CensusData data) {
        super(x, y, data);
    }

    // An internal class for preprocessing
    @SuppressWarnings("serial")
    class SmarterPreprocessor extends RecursiveTask<int[][]>{
        int hi, lo;

        // Look at data from lo (inlcusive) to hi (exclusive)
        SmarterPreprocessor(int lo, int hi) {
            this.lo  = lo;
            this.hi = hi;
        }

        /** {@inheritDoc} */
        @Override
        protected int[][] compute() {
            if(hi - lo <  cutoff) {
                CensusGroup group;
                int row, col;
                int[][] g = new int[x][y];

                for (int i = lo; i < hi; i++) {
                    group = censusData.data[i];
                    col = (int) ((group.latitude - xAxis) / gridSquareHeight);
                    // Default to North
                    if (group.latitude >= (col + 1) * gridSquareHeight + xAxis)
                        col++;
                    col = (col == y ?  y - 1: col); // edge case due to rounding
                    row = (int) ((group.longitude - yAxis) / gridSquareWidth);
                    // Default to East
                    if (group.longitude >= (row + 1) * gridSquareWidth + yAxis)
                        col++;
                    row = (row == x ? x - 1 : row); // edge case due to rounding
                    g[row][col] += group.population;

                }
                return g;

            } else {
                SmarterPreprocessor left = new SmarterPreprocessor(lo, (hi+lo)/2);
                SmarterPreprocessor right = new SmarterPreprocessor((hi+lo)/2, hi);

                left.fork(); // fork a thread and calls compute
                int[][] gRight = right.compute();//call compute directly
                int[][] gLeft = left.join();

                // add the two grids together in parallel
                fjPool.invoke(new AddGrids(0, x, gLeft, gRight));
                return gRight;
            }

        }
    }

    @SuppressWarnings("serial")
    class AddGrids extends RecursiveAction{
        int xhi, xlo;
        int[][] l, r;

        /*
         * examines data from two grids in the range:
         * 
         * (xlo,yhi)    ...    (xhi,yhi)
         *     .                   .
         *     .                   .
         *     .                   .
         * (xlo,ylo)    ...    (xhi,ylo)
         */
        AddGrids(int xlo, int xhi, int[][] l, int[][] r) {
            this.xlo  = xlo;
            this.xhi = xhi;
            this.l = l;
            this.r = r;
        }

        /** {@inheritDoc} */
        @Override
        protected void compute() {
            // find the optimal cutoff, based on grid size
            //			int cutoff = (int) Math.pow(10, -1 + Math.floor(Math.log(x*y))) + 10;

            if((xhi-xlo) <  cutoff) {
                for(int i = xlo; i < xhi; i++) {
                    for(int j = 0; j < y; j++)
                        r[i][j] += l[i][j];
                }
            } else {
                AddGrids left = new AddGrids(xlo, (xhi+xlo)/2, l, r);
                AddGrids right = new AddGrids((xhi+xlo)/2, xhi, l, r);

                left.fork(); // fork a thread and calls compute
                right.compute();//call compute directly
                left.join();
            }

        }
    }

    /** {@inheritDoc} */
    @Override
    public void preprocess() {
        super.preprocess();
        grid = fjPool.invoke(new SmarterPreprocessor(0, censusData.data_size));
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
