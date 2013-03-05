import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class SimpleAndParallel implements Implementation {
    private final int x;
    private final int y;
    private Rectangle america;
    private final CensusData censusData;
    int totalPopulation;

    /*
     * This version is the same as version 1 except both the initial
     * corner-finding and the traversal for each query should use the ForkJoin
     * Framework effectively. The work will remain O(n), but the span should
     * lower to O(log n). Finding the corners should require only one data
     * traversal, and each query should require only one additional data
     * traversal.
     */

    class Parallel extends RecursiveTask<Rectangle> { // extends RecursiveTask<Integer> {
        int hi, lo;
        Parallel(int lo, int hi) {
            this.lo  = lo;
            this.hi = hi;
        }
        @Override
        protected Rectangle compute() {
            if(hi - lo <  100) {
                CensusGroup group = censusData.data[lo];
                totalPopulation += group.population;
                Rectangle rec = new Rectangle(group.longitude, group.longitude,
                        group.latitude, group.latitude), temp;
                for (int i = lo + 1; i < hi; i++) {
                    group = censusData.data[i];
                    temp = new Rectangle(group.longitude, group.longitude,
                            group.latitude, group.latitude);
                    rec = rec.encompass(temp);
                    totalPopulation += group.population;
                }
                return rec;
            } else {
                Parallel left = new Parallel(lo, (hi+lo)/2);
                Parallel right = new Parallel((hi+lo)/2, hi);

                left.fork(); // fork a thread and calls compute
                Rectangle rightAns = right.compute();//call compute directly
                Rectangle leftAns = left.join();
                return rightAns.encompass(leftAns);
            }

        }
    }


    //static final ForkJoinPool fjPool = new ForkJoinPool();

    public SimpleAndParallel(int x, int y, CensusData data) {
        this.x = x;
        this.y = y;
        this.censusData = data;
        totalPopulation = 0;
    }

    @Override
    public int query(int west, int south, int east, int north) {
        // TODO Auto-generated method stub
        return 100;
    }

    static final ForkJoinPool fjPool = new ForkJoinPool();
    @Override
    public void preprocess() {
        america = fjPool.invoke(new Parallel(0, censusData.data_size));
    }

    @Override
    public int getPop() {
        System.out.println(totalPopulation);
        return totalPopulation;
    }
}
