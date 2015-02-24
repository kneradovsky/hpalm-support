package ru.sbt.qatools.tests.listeners;

import static org.junit.Assert.*;

import org.junit.Assume;
import org.junit.Test;
import ru.sbt.qatools.junit.annotations.AlmTest;
import ru.sbt.qatools.junit.annotations.AlmTestStep;

@AlmTest(name="testName",id="1000")
public class StubTest {

	@Test
	@AlmTestStep("stepFail")
	public void testFail() {
		fail("Not yet implemented");
	}
	
	@Test
	@AlmTestStep("step1")
	public void testPass() {
		assertTrue(true);
	}
	
	@Test
	@AlmTestStep("step2")
	public void testAssumptionFailed() {
		Assume.assumeTrue(false);
	}

}
