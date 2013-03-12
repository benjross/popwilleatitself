/*
 * Jordan Hazari (Primary Author)
 * Ben Ross
 * 3/5/13
 * CSE 332 AB
 * Project 3 Part A
 */

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the different implementations of
 * processing queries to find the population of
 * specific areas in the US.
 */
public class TestImplementations {
	private static final int TIMEOUT = 2000; // 2000 ms = 2 s
	
	PopulationQueryVerison[] imps;
	PopulationQueryVerison[] oImps;
	
	PopulationQueryVerison imp1; PopulationQueryVerison imp1Other;
	PopulationQueryVerison imp2; PopulationQueryVerison imp2Other;
	PopulationQueryVerison imp3; PopulationQueryVerison imp3Other;
	PopulationQueryVerison imp4; PopulationQueryVerison imp4Other;
	PopulationQueryVerison imp5; PopulationQueryVerison imp5Other;
	PopulationQueryVerison impEmpty;
	PopulationQueryVerison badBounds;
	PopulationQueryVerison zeroPop;
	
	CensusData data;
	CensusData zero;
	CensusData empty;
	
	@Before
	public void setUp() throws Exception {
		data = PopulationQuery.parse("CenPop2010.txt");
		zero = PopulationQuery.parse("zeroPop.txt");
		empty = PopulationQuery.parse("empty.txt");
		
		imps = new PopulationQueryVerison[5];
		oImps = new PopulationQueryVerison[5];
		
		imp1 = new SimpleAndSequential(20,25,data); imp1.preprocess(); imps[0] = imp1;
		imp1Other = new SimpleAndSequential(9,14,data); imp1Other.preprocess(); oImps[0] = imp1Other;
		
		imp2 = new SimpleAndParallel(20,25,data); imp2.preprocess(); imps[1] = imp2;
		imp2Other = new SimpleAndParallel(9,14,data); imp2Other.preprocess(); oImps[1] = imp2Other;
		
		imp3 = new SmarterAndSequential(20,25,data); imp3.preprocess(); imps[2] = imp3;
		imp3Other = new SmarterAndSequential(9,14,data); imp3Other.preprocess(); oImps[2] = imp3Other;
		
		imp4 = new SmarterAndParallel(20,25,data); imp4.preprocess(); imps[3] = imp4;
		imp4Other = new SmarterAndParallel(9,14,data); imp4Other.preprocess(); oImps[3] = imp4Other;
		
		imp5 = new SmarterAndLockBased(20,25,data); imp5.preprocess(); imps[4] = imp5;
		imp5Other = new SmarterAndLockBased(9,14,data); imp5Other.preprocess(); oImps[4] = imp5Other;
		
		zeroPop = new SimpleAndSequential(20,25,zero); zeroPop.preprocess();
		impEmpty = new SimpleAndSequential(20,25,empty); impEmpty.preprocess();
	}
	
	@Test(timeout = TIMEOUT)
	public void testTotalPopulation() {
		for(int i = 0; i < imps.length; i++) {
			assertEquals(imps[i].getPop(),312471327);
		}
	}
	
	@Test(timeout = TIMEOUT)
	public void testHawaii() {
		for(int i = 0; i < imps.length; i++) {
			assertEquals(imps[i].query(1, 1, 5, 4), 1360301);
		}
	}
	
	@Test(timeout = TIMEOUT)
	public void testAlaska() {
		for(int i = 0; i < imps.length; i++) {
			assertEquals(imps[i].query(1, 12, 9, 25), 710231);
		}
	}
	
	@Test(timeout = TIMEOUT)
	public void testMainUS() {
		for(int i = 0; i < imps.length; i++) {
			assertEquals(imps[i].query(9, 1, 20, 13), 310400795);
		}
	}
	
	@Test(timeout = TIMEOUT)
	public void testWholeGrid() {
		for(int i = 0; i < imps.length; i++) {
			assertEquals(imps[i].query(1, 1, 20, 25), 312471327);
		}
	}
	
	@Test(timeout = TIMEOUT)
	public void testBottomFourRows() {
		for(int i = 0; i < imps.length; i++) {
			assertEquals(imps[i].query(1, 1, 20, 4), 36493611);
		}
	}
	
	@Test(timeout = TIMEOUT)
	public void testMiddleishThreeColumns() {
		for(int i = 0; i < imps.length; i++) {
			assertEquals(imps[i].query(9, 1, 11, 25), 52392739);
		}
	}
	
	@Test(timeout = TIMEOUT)
	public void testOther() {
		for(int i = 0; i < oImps.length; i++) {
			assertEquals(oImps[i].query(5, 5, 7, 5), 18820388);
			assertEquals(oImps[i].query(6, 3, 8, 4), 105349619);
		}
	}
	
	@Test(timeout = TIMEOUT)
	public void testZeroPop() {
		assertEquals(zeroPop.query(1, 1, 20, 25), 0);
	}
	
	@Test(timeout = TIMEOUT)
	public void emptyFilePopZero() {
		assertEquals(impEmpty.query(1, 1, 20, 25), 0);
	}
	
	@Test(expected = java.lang.IndexOutOfBoundsException.class)
	public void badBoundsThrows() {
		badBounds = new SimpleAndSequential(0,25,data);
	}
}
