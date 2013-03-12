
public class RecordTimes {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//q7();
		q8();
	}
	
	public static void q7() {
		CensusData data = PopulationQuery.parse("CenPop.txt");
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
				imp4.query(1, 1, i, i);
				double endTime = System.currentTimeMillis();
				time4 += (endTime - startTime);
				
				startTime = System.currentTimeMillis();
				imp5.preprocess();
				imp5.query(1, 1, i, i);
				endTime = System.currentTimeMillis();
				time5 += (endTime - startTime);
			}
			System.out.println(time4/100+"\t"+time5/100);
		}
	}
	
	public static void q8() {
		CensusData data = PopulationQuery.parse("CenPop.txt");
		PopulationQueryVerison imp1 = new SimpleAndSequential(100,500,data);
		PopulationQueryVerison imp2 = new SimpleAndParallel(100,500,data);
		PopulationQueryVerison imp3 = new SmarterAndSequential(100,500,data);
		PopulationQueryVerison imp4 = new SmarterAndParallel(100,500,data);
		
		for(int i = 1; i < 100; i+=5) {
			double time1 = 0.0;
			double time2 = 0.0;
			double time3 = 0.0;
			double time4 = 0.0;
			for(int j = 0; j < 10; j++) {
				double startTime = System.currentTimeMillis();
				imp1.preprocess();
				for (int k = 1; k < i; k++)
					imp1.query(1, 1, 100, 500);
				double endTime = System.currentTimeMillis();
				time1 += (endTime - startTime);
				
				startTime = System.currentTimeMillis();
				imp2.preprocess();
				for (int k = 1; k < i; k++)
					imp2.query(1, 1, 100, 500);
				endTime = System.currentTimeMillis();
				time2 += (endTime - startTime);
				
				startTime = System.currentTimeMillis();
				imp3.preprocess();
				for (int k = 1; k < i; k++)
					imp3.query(1, 1, 100, 500);
				endTime = System.currentTimeMillis();
				time3 += (endTime - startTime);
				
				startTime = System.currentTimeMillis();
				imp4.preprocess();
				for (int k = 1; k < i; k++)
					imp4.query(1, 1, 100, 500);
				endTime = System.currentTimeMillis();
				time4 += (endTime - startTime);
			}
			System.out.println(time1/10+"\t"+time3/10+"\t"+time2/10+"\t"+time4/10);
		}
	}

}
