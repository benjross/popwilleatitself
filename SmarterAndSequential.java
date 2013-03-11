
public class SmarterAndSequential extends PopulationQueryVerison {
	private int[][] grid;

	public SmarterAndSequential(int x, int y, CensusData data) {
		super(x, y, data);
		grid = new int[x][y];
	}

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
            col = (col == y ?  y - 1: col); // edge case
            row = (int) ((group.longitude - yAxis) / gridSquareWidth);
            row = (row == x ? x - 1 : row); // edge case
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
        
        
        for (int j = grid[0].length - 1 - 1; j >= 0; j--) {
        	for (int i = 1; i < grid.length; i++) {
        		grid[i][j] += (grid[i-1][j] + grid[i][j+1] - grid[i-1][j+1]);
        	}
        }
        
	}

}
