
public class SimpleAndSequential  implements Implementation {
    private final int x;
    private final int y;
    private float gridX;
    private float gridY;
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


    @Override
    public int query(int west, int south, int east, int north) {
        CensusGroup group;
        int population = 0;
        double gLong, gLat;
        for (int i = 0; i < censusData.data_size; i++) {
            group = censusData.data[i];
            gLong = group.longitude;
            gLat = group.latitude;
            if (gLat >= (america.bottom + (south - 1) * gridY) &&
                    gLat <= (america.bottom + (north - 1) * gridY) &&
                    gLong <= (america.left + (east- 1) * gridX) &&
                    gLong >= (america.left + (west - 1) * gridX)
                    )
                population += group.population;
        }
        return population;
    }

    // maybe modify to use rectangle
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
        gridX = (rec.right - rec.left) / x;
        gridY = (rec.top - rec.bottom) / y;
        america = rec;
        totalPopulation = pop;
    }

    public boolean queryChecker(int west, int south, int east, int north) {
        return !(west < 1 || west > x ||
                south < 1 || south > y ||
                east < west || east > x ||
                north < south || north > y);
    }

    public SimpleAndSequential(int x, int y, CensusData data) {
        this.x = x;
        this.y = y;
        gridX = 0;
        gridY = 0;
        this.censusData = data;
        totalPopulation = 0;
    }

    @Override
    public int getPop() {
        return totalPopulation;
    }
}
