/*
 * Ben Ross (Primary Author)
 * Jordan Hazari
 * 3/12/13
 * CSE 332 AC
 * Daniel Jones
 * Project 3 part B
 */

/**
 * SmarterAndSequential extends SmarterQueryVersion to provide
 * functionality for finding information about a population.  The constructor
 * takes in the number of rows, the number of columns, and the CensusData of
 * the population.
 * 
 * @author Ben Ross
 */
public class SmarterAndSequential extends SmarterQueryVersion {


    /**
     * Creates a SmarterAndSequential object to provide population query
     * functions.
     * 
     * @param x The number of columns
     * @param y The number of rows
     * @param data The CensusData object to be queried
     */
    public SmarterAndSequential(int x, int y, CensusData data) {
        super(x, y, data);
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
        int row, col;

        for (int i = 0; i < censusData.data_size; i++) {
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
            grid[row][col] += group.population;

        }

        // sum top edge (of graph)
        for (int i = 1; i < grid.length; i++) {
            grid[i][grid[0].length - 1] += grid [i - 1][grid[0].length - 1];
        }

        // sum left edge (of graph)
        for (int i = grid[0].length - 2; i >= 0; i--) {
            grid[0][i] += grid [0][i + 1];
        }

        //  second step of grid addition
        for (int j = grid[0].length - 1 - 1; j >= 0; j--) {
            for (int i = 1; i < grid.length; i++) {
                grid[i][j] += (grid[i-1][j] + grid[i][j+1] - grid[i-1][j+1]);
            }
        }

    }

}
