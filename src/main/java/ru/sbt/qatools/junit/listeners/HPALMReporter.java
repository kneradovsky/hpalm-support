package ru.sbt.qatools.junit.listeners;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HPALMReporter  extends RunListener {
	Logger logger = LoggerFactory.getLogger(HPALMReporter.class);

	
	public HPALMReporter() {
		logger.debug("HPALM reporter constructed");
	} 
	
	@Override
	public void testAssumptionFailure(Failure failure) {
		logger.debug(ReflectionToStringBuilder.toString(failure));
		super.testAssumptionFailure(failure);
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		logger.debug(ReflectionToStringBuilder.toString(failure));
		super.testFailure(failure);
	}

	@Override
	public void testFinished(Description description) throws Exception {
		logger.debug(ReflectionToStringBuilder.toString(description));
		super.testFinished(description);
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		logger.debug(ReflectionToStringBuilder.toString(description));
		super.testIgnored(description);
	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		logger.debug("Test run finished:"+result.toString());
		logger.debug(ReflectionToStringBuilder.toString(result));
		super.testRunFinished(result);
	}

	@Override
	public void testRunStarted(Description description) throws Exception {
		logger.debug("Test run started:"+description.getClassName()+";"+description.getDisplayName()+";"+description.getMethodName());
		logger.debug(ReflectionToStringBuilder.toString(description));
		super.testRunStarted(description);
	}

	@Override
	public void testStarted(Description description) throws Exception {
		logger.debug(ReflectionToStringBuilder.toString(description));
		super.testStarted(description);
	}

}
