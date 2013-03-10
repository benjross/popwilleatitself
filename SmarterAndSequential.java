
public class SmarterAndSequential extends PopulationQueryVerison {
	private int[][] grid;

	public SmarterAndSequential(int x, int y, CensusData data) {
		super(x, y, data);
		grid = new int[y][x];
	}

	@Override
	public int query(int west, int south, int east, int north) {
		int pop = 0;
		pop += grid[y - south][east - 1];
		pop -= (north == y ? 0 : grid[y - north][east - 1]);
		pop -= (west == 1 ? 0 : grid[south - 1][west - 2]);
		pop += (west == x ? 0 : grid[north - 1][west]);
		return pop;
	}

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
            row = grid.length - (int) ((group.latitude - xAxis) / gridSquareHeight);
            row = (row == y ?  y - 1: row); // edge case
            col = (int) ((group.longitude - yAxis) / gridSquareWidth);
            col = (col == x ? x - 1 : col); // edge case
            grid[row][col] += group.population;
            
        }
        
        for (int i = 1; i < grid.length; i++) {
        	grid[i][0] += grid [i-1][0];
        }
        
        for (int i = 1; i < grid[0].length; i++) {
        	grid[0][i] += grid [0][i-1];
        }
        
        for (int i = 1; i < grid.length; i++) {
        	for (int j = 1; j < grid[i].length; j++) {
        		grid[i][j] += (grid[i-1][j] + grid[i][j-1] - grid[i-1][j-1]);
        	}
        }
        
	}

}
