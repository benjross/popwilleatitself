
public class SmarterQueryVersion extends SimpleAndParallel {
	protected int[][] grid;

	public SmarterQueryVersion(int x, int y, CensusData data) {
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
		super.preprocess();
	}
}
