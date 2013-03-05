import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class SimpleAndParallel implements Implementation {
    private final int x;
    private final int y;
    private Rectangle america;
    private final CensusData censusData;
    int totalPopulation; //312471327

    /*
     * This version is the same as version 1 except both the initial
     * corner-finding and the traversal for each query should use the ForkJoin
     * Framework effectively. The work will remain O(n), but the span should
     * lower to O(log n). Finding the corners should require only one data
     * traversal, and each query should require only one additional data
     * traversal.
     */

    class Result {
        Rectangle rec;
        int population;
        Result(Rectangle rec, int pop) {
            this.rec = rec;
            population = pop;
        }
    }

    @SuppressWarnings("serial")
    class Preprocessor extends RecursiveTask<Result> {
        int hi, lo;
        Preprocessor(int lo, int hi) {
            this.lo  = lo;
            this.hi = hi;
        }
        @Override
        protected Result compute() {
            if(hi - lo <  100) {
                CensusGroup group = censusData.data[lo];
                int pop = group.population;
                Rectangle rec = new Rectangle(group.longitude, group.longitude,
                        group.latitude, group.latitude), temp;
                for (int i = lo + 1; i < hi; i++) {
                    group = censusData.data[i];
                    temp = new Rectangle(group.longitude, group.longitude,
                            group.latitude, group.latitude);
                    rec = rec.encompass(temp);
                    pop += group.population;
                }
                return new Result(rec, pop);
            } else {
                Preprocessor left = new Preprocessor(lo, (hi+lo)/2);
                Preprocessor right = new Preprocessor((hi+lo)/2, hi);

                left.fork(); // fork a thread and calls compute
                Result rightAns = right.compute();//call compute directly
                Result leftAns = left.join();
                return new Result(rightAns.rec.encompass(leftAns.rec),
                        rightAns.population + leftAns.population);
            }

        }
    }

    @SuppressWarnings("serial")
    class Query extends RecursiveTask<Integer> {
        int hi, lo, west, south, east, north;
        Query(int lo, int hi, int west, int south, int east, int north) {
            this.lo  = lo;
            this.hi = hi;
            this.west = west;
            this.south = south;
            this.east = east;
            this.north = north;
        }
        @Override
        protected Integer compute() {

            if(hi - lo <  100) {
                CensusGroup group;
                int population = 0;
                double groupLong, groupLat;
                float yAxis = america.left;
                float xAxis = america.bottom;
                float gridSquareWidth = (america.right - america.left) / x;
                float gridSquareHeight = (america.top - america.bottom) / y;
                double leftBound = (yAxis + (west - 1) * gridSquareWidth);
                double rightBound = (yAxis + (east) * gridSquareWidth);
                double topBound = (xAxis + (north) * gridSquareHeight);
                double bottomBound = (xAxis + (south - 1) * gridSquareHeight);
                for (int i = lo; i < hi; i++) {
                    group = censusData.data[i];
                    groupLong = group.longitude;
                    groupLat = group.latitude;
                    // Defaults to North and/or East in case of tie
                    if (groupLat >= bottomBound &&
                            groupLat <= topBound &&
                            groupLong <= rightBound &&
                            groupLong >= leftBound
                            )
                        population += group.population;
                }

                return population;
            } else {
                Query left = new Query(lo, (hi+lo)/2, west, south, east, north);
                Query right = new Query((hi+lo)/2, hi, west, south, east, north);

                left.fork(); // fork a thread and calls compute
                Integer rightAns = right.compute();//call compute directly
                Integer leftAns = left.join();
                return rightAns + leftAns;
            }

        }
    }

    public SimpleAndParallel(int x, int y, CensusData data) {
        this.x = x;
        this.y = y;
        this.censusData = data;
        totalPopulation = 0;
    }

    static final ForkJoinPool fjPool = new ForkJoinPool();

    @Override
    public int query(int west, int south, int east, int north) {
        return fjPool.invoke(new Query(0, censusData.data_size, west, south, east, north));
    }

    @Override
    public void preprocess() {
        Result res = fjPool.invoke(new Preprocessor(0, censusData.data_size));
        america = res.rec;
        totalPopulation = res.population;
    }

    @Override
    public int getPop() {
        return totalPopulation;
    }
}
