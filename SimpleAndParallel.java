/*
 * Ben Ross (Primary Author)
 * Jordan Hazari
 * 3/12/13
 * CSE 332 AC
 * Daniel Jones
 * Project 3 part A
 */

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * SimpleAndParallel extends PopulationQueryVersion to provide
 * functionality for finding information about a population.  The constructor
 * takes in the number of rows, the number of columns, and the CensusData of
 * the population.
 * 
 * @author Ben Ross
 */
public class SimpleAndParallel extends PopulationQueryVerison {
    /**
     * Creates a SimpleAndParallel object to provide population query
     * functions.
     * 
     * @param x The number of columns
     * @param y The number of rows
     * @param data The CensusData object to be queried
     */
    public SimpleAndParallel(int x, int y, CensusData data) {
        super(x, y, data);
    }

    // An internal class for preprocessing
    class Result {
        Rectangle rec;
        int population;
        Result(Rectangle rec, int pop) {
            this.rec = rec;
            population = pop;
        }
    }

    // An internal class for preprocessing
    @SuppressWarnings("serial")
    class Preprocessor extends RecursiveTask<Result> {
        int hi, lo;

        // Look at data from lo (inlcusive) to hi (exclusive)
        Preprocessor(int lo, int hi) {
            this.lo  = lo;
            this.hi = hi;
        }

        /** {@inheritDoc} */
        @Override
        protected Result compute() {
            if(hi - lo <  cutoff) {
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

    // An internal class for querying
    @SuppressWarnings("serial")
    class Query extends RecursiveTask<Integer> {
        int hi, lo;
        double leftBound, rightBound, topBound, bottomBound;

        // Look at data from lo (inclusive) to hi (exclusive)
        // Query bounded by *Bound fields
        Query(int lo, int hi, double leftBound, double rightBound, double topBound, double bottomBound) {
            this.lo  = lo;
            this.hi = hi;
            this.leftBound = leftBound;
            this.rightBound = rightBound;
            this.topBound = topBound;
            this.bottomBound = bottomBound;
        }

        /** {@inheritDoc} */
        @Override
        protected Integer compute() {
            if(hi - lo <  cutoff) {
                CensusGroup group;
                int population = 0;
                double groupLong, groupLat;

                for (int i = lo; i < hi; i++) {
                    group = censusData.data[i];
                    groupLong = group.longitude;
                    groupLat = group.latitude;
                    // Defaults to North and/or East in case of tie
                    if (groupLat >= bottomBound &&
                            (groupLat < topBound ||
                                    (topBound - america.top) >= 0) &&
                                    (groupLong < rightBound ||
                                            (rightBound - america.right) >= 0) &&
                                            groupLong >= leftBound)
                        population += group.population;
                }

                return population;
            } else {
                Query left =
                        new Query(lo, (hi+lo)/2, leftBound, rightBound, topBound, bottomBound);
                Query right =
                        new Query((hi+lo)/2, hi, leftBound, rightBound, topBound, bottomBound);

                left.fork(); // fork a thread and calls compute
                Integer rightAns = right.compute(); // call compute directly
                Integer leftAns = left.join();
                return rightAns + leftAns;
            }

        }
    }

    // for parallel programming!
    static final ForkJoinPool fjPool = new ForkJoinPool();

    /** {@inheritDoc} */
    @Override
    public int query(int west, int south, int east, int north) {
        if (america == null)
            return 0;
        double leftBound = (yAxis + (west - 1) * gridSquareWidth);
        double rightBound = (yAxis + (east) * gridSquareWidth);
        double topBound = (xAxis + (north) * gridSquareHeight);
        double bottomBound = (xAxis + (south - 1) * gridSquareHeight);
        return fjPool.invoke(
                new Query(0, censusData.data_size, leftBound, rightBound, topBound, bottomBound));
    }

    /** {@inheritDoc} */
    @Override
    public void preprocess() {
        if (censusData.data_size == 0)
            return;

        Result res = fjPool.invoke(new Preprocessor(0, censusData.data_size));
        america = res.rec;

        yAxis = america.left;
        xAxis = america.bottom;
        gridSquareWidth = (america.right - america.left) / x;
        gridSquareHeight = (america.top - america.bottom) / y;

        totalPopulation = res.population;
    }
}
