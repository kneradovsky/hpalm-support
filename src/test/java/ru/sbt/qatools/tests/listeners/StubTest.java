package ru.sbt.qatools.tests.listeners;

import static org.junit.Assert.*;

import org.junit.Assume;
import org.junit.Test;

public class StubTest {

	@Test
	public void testFail() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testPass() {
		assertTrue(true);
	}
	
	@Test
	public void testAssumptionFailed() {
		Assume.assumeTrue(false);
	}

}
