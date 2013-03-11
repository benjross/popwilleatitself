
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;


public class SmarterAndParallel extends SmarterQueryVersion {

	public SmarterAndParallel(int x, int y, CensusData data) {
		super(x, y, data);
	}
	
	@SuppressWarnings("serial")
	class SmarterPreprocessor extends RecursiveTask<int[][]>{
	       int hi, lo;

	        // Look at data from lo (inlcusive) to hi (exclusive)
	        SmarterPreprocessor(int lo, int hi) {
	            this.lo  = lo;
	            this.hi = hi;
	        }

		@Override
        protected int[][] compute() {
            if(hi - lo <  cutoff) {
                CensusGroup group;
                int row, col;
                int[][] g = new int[x][y];
                
                for (int i = lo; i < hi; i++) {
                    group = censusData.data[i];
                    col = (int) ((group.latitude - xAxis) / gridSquareHeight);
                    col = (col == y ?  y - 1: col); // edge case
                    row = (int) ((group.longitude - yAxis) / gridSquareWidth);
                    row = (row == x ? x - 1 : row); // edge case
                    g[row][col] += group.population;
                }
                return g;

            } else {
                SmarterPreprocessor left = new SmarterPreprocessor(lo, (hi+lo)/2);
                SmarterPreprocessor right = new SmarterPreprocessor((hi+lo)/2, hi);

                left.fork(); // fork a thread and calls compute
                int[][] gRight = right.compute();//call compute directly
                int[][] gLeft = left.join();
                //int[][] g = new int[x][y];
//                for(int i = 0; i < g.length; i++) {
//                	for(int j = 0; j < (g[i].length); j++) {
//                		g[i][j] = gLeft[i][j]+gRight[i][j];
//                	}
//                }
                fjPool.invoke(new AddGrids(0, x, 0, y, gLeft, gRight));
                return gRight;
            }

        }
	}
	
	@SuppressWarnings("serial")
	class AddGrids extends RecursiveAction{
	       int xhi, xlo, yhi, ylo;
	       int[][] l, r;

	        // Look at data from lo (inlcusive) to hi (exclusive)
	        AddGrids(int xlo, int xhi, int ylo, int yhi, int[][] l, int[][] r) {
	            this.xlo  = xlo;
	            this.xhi = xhi;
	            this.ylo = ylo;
	            this.yhi = yhi;
	            this.l = l;
	            this.r = r;
	        }

		@Override
        protected void compute() {
			int cutoff = (int) Math.pow(10, -1 + Math.floor(Math.log((xhi-xlo)*(yhi-ylo)))) + 10;
            if((xhi-xlo)*(yhi-ylo) <  cutoff) {
                for(int i = xlo; i < xhi; i++) {
                	for(int j = ylo; j < yhi; j++) {
                		r[i][j] += l[i][j];
                	}
                }
            } else {
                AddGrids left = new AddGrids(xlo, (xhi+xlo)/2, ylo, yhi, l, r);
                AddGrids right = new AddGrids((xhi+xlo)/2, xhi, ylo, yhi, l, r);

                left.fork(); // fork a thread and calls compute
                right.compute();//call compute directly
                left.join();
            }

        }
	}
	
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
