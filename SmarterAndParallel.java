import java.util.concurrent.RecursiveAction;


public class SmarterAndParallel extends SmarterQueryVersion {

	public SmarterAndParallel(int x, int y, CensusData data) {
		super(x, y, data);
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("serial")
	class SmarterPreprocessor extends RecursiveAction{
	       int hi, lo;

	        // Look at data from lo (inlcusive) to hi (exclusive)
	        SmarterPreprocessor(int lo, int hi) {
	            this.lo  = lo;
	            this.hi = hi;
	        }

		@Override
        protected void compute() {
            if(hi - lo <  cutoff) {
                CensusGroup group;
                int row, col;
                for (int i = lo; i < hi; i++) {
                    group = censusData.data[i];
                    col = (int) ((group.latitude - xAxis) / gridSquareHeight);
                    col = (col == y ?  y - 1: col); // edge case
                    row = (int) ((group.longitude - yAxis) / gridSquareWidth);
                    row = (row == x ? x - 1 : row); // edge case
                    grid[row][col] += group.population;
                    
                }

            } else {
                SmarterPreprocessor left = new SmarterPreprocessor(lo, (hi+lo)/2);
                SmarterPreprocessor right = new SmarterPreprocessor((hi+lo)/2, hi);

                left.fork(); // fork a thread and calls compute
                right.compute();//call compute directly
                left.join();
            }

        }
		// make this parallel

	}
	
	@Override
	public void preprocess() {
		super.preprocess();
		
		fjPool.invoke(new SmarterPreprocessor(0, censusData.data_size));
        
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
