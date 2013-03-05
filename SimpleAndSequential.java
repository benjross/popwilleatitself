
public class SimpleAndSequential  implements Implementation {
    private final int x;
    private final int y;
    private Rectangle america;
    private final CensusData censusData;
    int totalPopulation;
    /*
     * Before processing any queries, process the data to find the four corners
     * of the U.S. rectangle using a sequential O(n) algorithm where n is the
     * number of census-block-groups.  Then for each query do another
     * sequential O(n) traversal to answer the query (determining for each
     * census-block-group whether or not it is in the query rectangle).
     * The simplest and most reusable approach for each census-block-group is
     * probably to first compute what grid position it is in and then see if
     * this grid position is in the query rectangle.
     */


    public SimpleAndSequential(int x, int y, CensusData data) {
        this.x = x;
        this.y = y;
        this.censusData = data;
        totalPopulation = 0;
    }

    @Override
    public int query(int west, int south, int east, int north) {
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
        for (int i = 0; i < censusData.data_size; i++) {
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
    }

    @Override
    public void preprocess() {
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
        totalPopulation = pop;
    }


    @Override
    public int getPop() {
        return totalPopulation;
    }

    public boolean queryChecker(int west, int south, int east, int north) {
        return !(west < 1 || west > x ||
                south < 1 || south > y ||
                east < west || east > x ||
                north < south || north > y);
    }

}
