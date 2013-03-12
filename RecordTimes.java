
public class RecordTimes {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CensusData data = PopulationQuery.parse("CenPop2010.txt");
		
		PopulationQueryVerison imp1;
		PopulationQueryVerison imp2;
		PopulationQueryVerison imp3;
		PopulationQueryVerison imp4;
		PopulationQueryVerison imp5;
		
		for(int i = 10; i < 200; i+=5) {
			imp4 = new SmarterAndParallel(i,i,data);
			imp5 = new SmarterAndLockBased(i,i,data);
			double time4 = 0.0;
			double time5 = 0.0;
			for(int j = 0; j < 100; j++) {
				double startTime = System.currentTimeMillis();
				imp4.preprocess();
				double endTime = System.currentTimeMillis();
				time4 += (endTime - startTime);
				
				startTime = System.currentTimeMillis();
				imp5.preprocess();
				endTime = System.currentTimeMillis();
				time5 += (endTime - startTime);
			}
			System.out.println(time4/100+"\t"+time5/100);
		}

	}

}
