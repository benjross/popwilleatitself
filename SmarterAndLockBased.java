
public class SmarterAndLockBased extends SmarterQueryVersion {
	private static final int NUM_THREADS = 4;


	public SmarterAndLockBased(int x, int y, CensusData data) {
		super(x, y, data);
	}

	class SumThread extends java.lang.Thread
	{
		int lo; int hi;
		int[] arr;
		// fields to know what to do
		int ans = 0;
		// result
		SumThread(int[] a, int l, int h) { arr = a; lo = l;  hi = h;}
		public void run() {
			// override
			if ((hi - lo) < cutoff)
				for (int i = lo; i < hi; i++)
					ans += arr[i];
			else
			{
				SumThread left = new SumThread(arr, lo, (hi+lo)/2);
				SumThread right = new SumThread(arr, (hi+lo)/2, hi);
				left.start();
				right.start();
				// change this to run() to save threads
				left.run();
				// don’t move this up a line – why?
				right.join();
				// not needed if you used right.run
				ans = left.ans + right.ans;
			}
		}
	}
	
	int sum(int[] arr) {
		// just make one thread!
		SumThread t = new SumThread(arr,0,arr.length);
		t.run();
		return t.ans;
	}


	@Override
	public void preprocess() {
		super.preprocess();
		

	}

}
